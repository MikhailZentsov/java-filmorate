package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.mapper.ReviewMapper;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class DbReviewStorageImpl implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public List<Review> findAll(int count) {
        List<Review> reviews = jdbcTemplate.query("select R.REVIEW_ID, " +
                "R.FILM_ID,  " +
                "R.USER_ID,  " +
                "R.CONTENT,  " +
                "R.IS_POSITIVE,  " +
                "SUM(COALESCE(RR.REACTION, 0)) as USEFUL " +
                "from REVIEWS as R  " +
                "left outer join REVIEW_REACTION as RR on R.REVIEW_ID = RR.REVIEW_ID  " +
                "GROUP BY R.REVIEW_ID, R.FILM_ID, R.USER_ID, R.CONTENT, R.IS_POSITIVE  " +
                "ORDER BY USEFUL DESC " +
                "LIMIT ? ", ReviewMapper::mapRowToReview, count);

        log.info("Получен список всех отзывов с ограничием по количеству равным {} и сортировкой полезности.",
                count);

        return reviews;
    }

    @Override
    @Transactional
    public List<Review> findAllByFilmId(Long filmId, int count) {
        List<Review> reviews = jdbcTemplate.query("select R.REVIEW_ID, "
                + "R.FILM_ID, "
                + "R.USER_ID, "
                + "R.CONTENT, "
                + "R.IS_POSITIVE, "
                + "COALESCE(SUM(RR.REACTION), 0) as USEFUL "
                + "from REVIEWS as R "
                + "left outer join REVIEW_REACTION as RR on R.REVIEW_ID = RR.REVIEW_ID "
                + "where FILM_ID = ? "
                + "group by R.REVIEW_ID, R.FILM_ID, R.USER_ID, R.CONTENT, R.IS_POSITIVE "
                + "order by USEFUL desc "
                + "limit ? ", ReviewMapper::mapRowToReview, filmId, count);

        log.info("Получен список всех отзывов к фильму ID = {} " +
                        "с ограничием по количеству равным {} и сортировкой полезности.",
                filmId,
                count);

        return reviews;
    }

    @Override
    @Transactional
    public Optional<Review> getById(Long reviewId) {
        try {
            Optional<Review> review = Optional.ofNullable(jdbcTemplate.queryForObject("select R.REVIEW_ID, "
                    + "R.FILM_ID, "
                    + "R.USER_ID, "
                    + "R.CONTENT, "
                    + "R.IS_POSITIVE, "
                    + "SUM(RR.REACTION) as USEFUL "
                    + "from REVIEWS as R "
                    + "left outer join REVIEW_REACTION as RR on R.REVIEW_ID = RR.REVIEW_ID "
                    + "where R.REVIEW_ID = ? "
                    + "group by R.REVIEW_ID, "
                    + "         FILM_ID,"
                    + "         R.USER_ID,"
                    + "         CONTENT,"
                    + "         IS_POSITIVE "
                    + "order by USEFUL desc ", ReviewMapper::mapRowToReview, reviewId));
            log.info("Отзыв с ID = {} получен.", reviewId);

            return review;
        } catch (DataAccessException e) {
            log.info("Отзыва с ID = {} не существует.", reviewId);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Optional<Review> saveReview(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("REVIEWS")
                .usingGeneratedKeyColumns("REVIEW_ID");

        Long reviewId = simpleJdbcInsert.executeAndReturnKey(review.toMap()).longValue();
        review.setReviewId(reviewId);

        log.info("Отзыв с ID = {} сохранен.", reviewId);

        return Optional.of(review);
    }

    @Override
    @Transactional
    public Optional<Review> updateReview(Review review) {
        if (jdbcTemplate.update("update REVIEWS "
                        + "set    CONTENT = ?, "
                        + "IS_POSITIVE = ? "
                        + "where REVIEW_ID = ?",
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()) == 0) {

            log.info("Отзыва с ID = {} не существует.", review.getReviewId());

            return Optional.empty();
        }

        log.info("Отзыв с ID = {} обновлен.", review.getReviewId());

        return getById(review.getReviewId());
    }

    @Override
    @Transactional
    public Optional<Long> removeReview(Long reviewId) {
        Long userId = jdbcTemplate.queryForObject("select user_id from reviews where review_id = ?",
                Long.class,
                reviewId);

        if (jdbcTemplate.update("delete "
                + "from REVIEWS "
                + "where REVIEW_ID = ?", reviewId) == 0) {
            log.info("Отзыва с ID = {} не существует.", reviewId);

            return Optional.empty();
        } else {
            log.info("Отзыв с ID = {} удален.", reviewId);

            return Optional.of(userId);
        }
    }

    @Override
    @Transactional
    public void addReviewReaction(Long reviewId, Long userId, int reaction) {
        removeReviewReaction(reviewId, userId);
        jdbcTemplate.update("insert "
                + "into REVIEW_REACTION (REVIEW_ID, USER_ID, REACTION) "
                + "values (?, ?, ?)", reviewId, userId, reaction);

        log.info("Отзыву с ID = {} добавлена реакция от пользователя с ID = {}.",
                reviewId,
                userId);
    }

    @Override
    @Transactional
    public void removeReviewReaction(Long reviewId, Long userId) {
        jdbcTemplate.update("delete "
                + "from REVIEW_REACTION "
                + "where REVIEW_ID = ? and USER_ID = ?", reviewId, userId);
        log.info("Отзыву с ID = {} удалена реакция от пользователя с ID = {}.",
                reviewId,
                userId);
    }
}
