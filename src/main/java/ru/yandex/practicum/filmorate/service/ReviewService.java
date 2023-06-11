package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {

    Review getReview(Long reviewId);

    List<Review> getReviews(Long filmId, int count);

    Review addReview(Review review);

    Review updateReview(Review review);

    void removeReview(Long reviewId);

    void addReviewReaction(Long reviewId, Long userId, int reaction);

    void removeReviewReaction(Long reviewId, Long userId);
}
