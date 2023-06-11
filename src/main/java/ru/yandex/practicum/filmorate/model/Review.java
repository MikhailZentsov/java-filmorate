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

    public Review() {

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
}
