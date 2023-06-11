package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> addReview(@Valid @RequestBody Review review) {
        return new ResponseEntity<Review>(reviewService.addReview(review), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Review> updateReview(@Valid @RequestBody Review review) {
        return new ResponseEntity<Review>(reviewService.updateReview(review), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteReview(@PathVariable Long id) {
        reviewService.removeReview(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReview(@PathVariable Long id) {
        return new ResponseEntity<Review>(reviewService.getReview(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Review>> getReviewsByFilmId(@RequestParam(required = false) Long filmId, @RequestParam(defaultValue = "10") Integer count) {
        return new ResponseEntity<List<Review>>(reviewService.getReviews(filmId, count), HttpStatus.OK);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        int reaction = 1;
        reviewService.addReviewReaction(id, userId, reaction);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Long id, @PathVariable Long userId) {
        int reaction = -1;
        reviewService.addReviewReaction(id, userId, reaction);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeReviewReaction(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Long id, Long userId) {
        reviewService.removeReviewReaction(id, userId);
    }
}
