package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Review {
    @NotNull
    private String content;
    @NotNull
    private Boolean isPositive;
    @NotNull
    private Long userId;
    @NotNull
    private Long filmId;
    private Long useful;
    private Long reviewId;

    private Review() {

    }

    public Review(String content, Boolean isPositive, Long userId, Long filmId, Long useful, Long reviewId) {
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
        this.useful = useful;
        this.reviewId = reviewId;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("REVIEW_ID", reviewId);
        values.put("CONTENT", content);
        values.put("IS_POSITIVE", isPositive);
        values.put("FILM_ID", filmId);
        values.put("USER_ID", userId);

        return values;
    }

    public static class Builder {
        private final Review newReview;

        public Builder() {
            newReview = new Review();
        }

        public Builder reviewId(Long reviewId) {
            newReview.setReviewId(reviewId);
            return this;
        }

        public Builder userId(Long userId) {
            newReview.setUserId(userId);
            return this;
        }

        public Builder filmId(Long filmId) {
            newReview.setFilmId(filmId);
            return this;
        }

        public Builder useful(Long useful) {
            newReview.setUseful(useful);
            return this;
        }

        public Builder isPositive(Boolean isPositive) {
            newReview.setIsPositive(isPositive);
            return this;
        }

        public Builder content(String content) {
            newReview.setContent(content);
            return this;
        }

        public Review build() {
            return newReview;
        }
    }
}
