package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.mapper.Mapper;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DbReviewStorageImpl implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public List<Review> findAll(int count) {
        return jdbcTemplate.query("select R.REVIEW_ID, " +
                "R.FILM_ID,  " +
                "R.USER_ID,  " +
                "R.CONTENT,  " +
                "R.IS_POSITIVE,  " +
                "SUM(COALESCE(RR.REACTION, 0)) as USEFUL " +
                "from REVIEWS as R  " +
                "left outer join REVIEW_REACTION as RR on R.REVIEW_ID = RR.REVIEW_ID  " +
                "GROUP BY R.REVIEW_ID, R.FILM_ID, R.USER_ID, R.CONTENT, R.IS_POSITIVE  " +
                "ORDER BY USEFUL DESC " +
                "LIMIT ? ", Mapper::mapRowToReview, count);
    }

    @Override
    @Transactional
    public List<Review> findAllByFilmId(Long filmId, int count) {
        return jdbcTemplate.query("select R.REVIEW_ID, "
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
                + "limit ? ", Mapper::mapRowToReview, filmId, count);
    }

    @Override
    @Transactional
    public Optional<Review> getById(Long reviewId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select R.REVIEW_ID, "
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
                    + "order by USEFUL desc ", Mapper::mapRowToReview, reviewId));
        } catch (DataAccessException e) {
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
            return Optional.empty();
        }

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
            return Optional.empty();
        } else {
            assert userId != null;
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
    }

    @Override
    @Transactional
    public void removeReviewReaction(Long reviewId, Long userId) {
        jdbcTemplate.update("delete "
                + "from REVIEW_REACTION "
                + "where REVIEW_ID = ? and USER_ID = ?", reviewId, userId);
    }
}
