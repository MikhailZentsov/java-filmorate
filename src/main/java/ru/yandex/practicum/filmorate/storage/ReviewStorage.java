package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    List<Review> findAll(int count);

    List<Review> findAllByFilmId(Long filmId, int count);

    Optional<Review> getById(Long reviewId);

    Optional<Review> saveReview(Review review);

    Optional<Review> updateReview(Review review);

    Optional<Long> removeReview(Long reviewId);

    void addReviewReaction(Long reviewId, Long userId, int reaction);

    void removeReviewReaction(Long reviewId, Long userId);

}
