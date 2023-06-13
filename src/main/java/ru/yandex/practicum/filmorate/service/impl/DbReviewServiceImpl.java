package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.EventService;
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
    private final EventService eventService;

    @Override
    public Review getReview(Long reviewId) {
        return reviewStorage.getById(reviewId).orElseThrow(() ->
                new NotFoundException(String.format(
                        "Отзыв с ID %s не найден", reviewId)));
    }

    @Override
    public List<Review> getReviews(Long filmId, int count) {
        if (filmId == null) {
            return reviewStorage.findAll(count);
        } else {
            filmStorage.getById(filmId).orElseThrow(() ->
                    new NotFoundException(String.format(
                            "Фильм с ID %s не найден", filmId)));
            return reviewStorage.findAllByFilmId(filmId, count);
        }
    }

    @Override
    public Review addReview(Review review) {
        userStorage.getById(review.getUserId()).orElseThrow(() ->
                new NotFoundException(String.format(
                        "Пользователь с ID %s не найден", review.getUserId())));
        filmStorage.getById(review.getFilmId()).orElseThrow(() ->
                new NotFoundException(String.format(
                        "Фильм с ID %s не найден", review.getFilmId())));
        Review result = reviewStorage.saveReview(review).orElseThrow(() ->
                new AlreadyExistsException(String.format(
                        "Отзыв с ID %s уже существует", review.getReviewId())));
        eventService.createAddReviewEvent(result.getUserId(), result.getReviewId());

        return result;
    }

    @Override
    public Review updateReview(Review review) {
        Review result = reviewStorage.updateReview(review).orElseThrow(() ->
                new NotFoundException(String.format(
                        "Отзыв с ID %s не найден", review.getReviewId())));
        eventService.createUpdateReviewEvent(result.getUserId(), result.getReviewId());

        return result;
    }

    @Override
    public void removeReview(Long reviewId) {
        Long userId = reviewStorage.removeReview(reviewId).orElseThrow(() ->
                new NotFoundException(String.format(
                        "Отзыв с ID %s не найден", reviewId)));
        eventService.createRemoveReviewEvent(userId, reviewId);
    }

    @Override
    public void addReviewReaction(Long reviewId, Long userId, int reaction) {
        userStorage.getById(userId).orElseThrow(() ->
                new NotFoundException(String.format(
                        "Пользователь с ID %s не найден", userId)));
        reviewStorage.addReviewReaction(reviewId, userId, reaction);
    }

    @Override
    public void removeReviewReaction(Long reviewId, Long userId) {
        userStorage.getById(userId).orElseThrow(() ->
                new NotFoundException(String.format(
                        "Пользователь с ID %s не найден", userId)));
        reviewStorage.getById(reviewId).orElseThrow(() ->
                new NotFoundException(String.format(
                        "Отзыв с ID %s не найден", reviewId)));
        reviewStorage.removeReviewReaction(reviewId, userId);
    }
}
