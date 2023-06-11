package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DbReviewServiceImpl implements ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Override
    public Review getReview(Long reviewId) {
        return reviewStorage.getById(reviewId).orElseThrow(() -> new ReviewNotFoundException(String.format(
                "Отзыв с ID %s не найден", reviewId)));
    }

    @Override
    public List<Review> getReviews(Long filmId, int count) {
        if (filmId == null) {
            return reviewStorage.findAll(count);
        } else {
            filmStorage.getById(filmId).orElseThrow(() -> new FilmNotFoundException(String.format(
                    "Фильм с ID %s не найден", filmId)));
            return reviewStorage.findAllByFilmId(filmId, count);
        }
    }

    @Override
    public Review addReview(Review review) {
        userStorage.getById(review.getUserId()).orElseThrow(() -> new UserNotFoundException(String.format(
                "Пользователь с ID %s не найден", review.getUserId())));
        filmStorage.getById(review.getFilmId()).orElseThrow(() -> new FilmNotFoundException(String.format(
                "Фильм с ID %s не найден", review.getFilmId())));

        return reviewStorage.saveReview(review).orElseThrow(() -> new ReviewAlreadyExistsException(String.format(
                "Отзыв с ID %s уже существует", review.getReviewId())));
    }

    @Override
    public Review updateReview(Review review) {
        reviewStorage.getById(review.getReviewId()).orElseThrow(() -> new ReviewNotFoundException(String.format(
                "Отзыв с ID %s не найден", review.getReviewId())));
        return reviewStorage.updateReview(review).get();
    }

    @Override
    public void removeReview(Long reviewId) {
        reviewStorage.removeReview(reviewId).orElseThrow(() -> new ReviewNotFoundException(String.format(
                "Отзыв с ID %s не найден", reviewId)));
    }

    @Override
    public void addReviewReaction(Long reviewId, Long userId, int reaction) {
        userStorage.getById(userId).orElseThrow(() -> new UserNotFoundException(String.format(
                "Пользователь с ID %s не найден", userId)));

        reviewStorage.addReviewReaction(reviewId, userId, reaction);
    }

    @Override
    public void removeReviewReaction(Long reviewId, Long userId) {
        userStorage.getById(userId).orElseThrow(() -> new UserNotFoundException(String.format(
                "Пользователь с ID %s не найден", userId)));
        reviewStorage.getById(reviewId).orElseThrow(() -> new ReviewNotFoundException(String.format(
                "Отзыв с ID %s не найден", reviewId)));

        reviewStorage.removeReviewReaction(reviewId, userId);
    }
}
