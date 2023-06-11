package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest()
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmorateApplicationTests {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final ReviewStorage reviewStorage;

    private static User userOne;
    private static User userTwo;
    private static User userThree;
    private static Film filmOne;
    private static Film filmTwo;
    private static Film filmThree;
    private static Review reviewOne;
    private static Review reviewTwo;

    @BeforeEach
    void setUp() {
        userOne = new User(0,
                "loginOne",
                "nameOne",
                "email@email.ru",
                LocalDate.of(1990, 12, 12));
        userTwo = new User(0,
                "loginTwo",
                "nameTwo",
                "yandex@yandex.ru",
                LocalDate.of(1995, 5, 5));
        userThree = new User(0,
                "loginThree",
                "nameThree",
                "gmail@gmail.com",
                LocalDate.of(1985, 4, 2));
        filmOne = new Film(0,
                "filmOne",
                "descriptionOne",
                LocalDate.of(1949, 1, 1),
                100,
                Mpa.G);
        filmTwo = new Film(0,
                "filmTwo",
                "descriptionTwo",
                LocalDate.of(1977, 7, 7),
                200,
                Mpa.NC17);
        filmThree = new Film(0,
                "filmThree",
                "descriptionThree",
                LocalDate.of(2001, 4, 14),
                140,
                Mpa.PG);
        reviewOne = new Review("Review_One_Content",
                true,
                1L,
                1L,
                0L,
                0L);
        reviewTwo = new Review("Review_Two_Content",
                true,
                1L,
                1L,
                0L,
                0L);
    }

    @Test
    public void testGetReviews() {
        userStorage.saveOne(userOne);
        filmStorage.saveOne(filmOne);

        reviewStorage.saveReview(reviewOne);
        reviewStorage.saveReview(reviewTwo);

        assertEquals(reviewStorage.findAll(10).size(), 2);
    }

    @Test
    public void testGetReviewsByFilmId() {
        userStorage.saveOne(userOne);
        filmStorage.saveOne(filmOne);
        filmStorage.saveOne(filmTwo);

        reviewStorage.saveReview(reviewOne);
        reviewStorage.saveReview(reviewTwo);

        assertEquals(reviewStorage.findAllByFilmId(1L, 1).size(), 1);
    }

    @Test
    public void testGetReviewById() {
        userStorage.saveOne(userOne);
        filmStorage.saveOne(filmOne);
        reviewStorage.saveReview(reviewOne);

        Optional<Review> reviewOptional = reviewStorage.getById(1L);
        assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(review -> {
                    assertThat(review).hasFieldOrPropertyWithValue("reviewId", 1L);
                    assertThat(review).hasFieldOrPropertyWithValue("content", "Review_One_Content");
                    assertThat(review).hasFieldOrPropertyWithValue("isPositive", true);
                    assertThat(review).hasFieldOrPropertyWithValue("userId", 1L);
                    assertThat(review).hasFieldOrPropertyWithValue("filmId", 1L);
                });
    }

    @Test
    public void testUpdateReview() {
        filmStorage.saveOne(filmTwo);
        filmStorage.saveOne(filmTwo);
        userStorage.saveOne(userOne);
        reviewStorage.saveReview(reviewOne);

        Review reviewOneUpdate = new Review("Review_One_Content_Update",
                false,
                0L,
                0L,
                0L,
                1L);

        Optional<Review> updatedReview = reviewStorage.updateReview(reviewOneUpdate);
        assertThat(updatedReview)
                .isPresent()
                .hasValueSatisfying(review -> {
                    assertThat(review).hasFieldOrPropertyWithValue("reviewId", 1L);
                    assertThat(review).hasFieldOrPropertyWithValue("content", "Review_One_Content_Update");
                    assertThat(review).hasFieldOrPropertyWithValue("isPositive", false);
                    assertThat(review).hasFieldOrPropertyWithValue("userId", 1L);
                    assertThat(review).hasFieldOrPropertyWithValue("filmId", 1L);
                });
    }

    @Test
    public void testGetGenres() {
        assertEquals(genreStorage.findAll().orElse(new ArrayList<>()).size(), 6);
    }

    @Test
    public void testGetGenreById() {
        assertEquals(genreStorage.getById(1).orElse(null), Genre.COMEDY);
        assertEquals(genreStorage.getById(2).orElse(null), Genre.DRAMA);
        assertEquals(genreStorage.getById(3).orElse(null), Genre.CARTOON);
        assertEquals(genreStorage.getById(4).orElse(null), Genre.THRILLER);
        assertEquals(genreStorage.getById(5).orElse(null), Genre.DOCUMENTARY);
        assertEquals(genreStorage.getById(6).orElse(null), Genre.ACTION);
    }

    @Test
    void testGetMpas() {
        assertEquals(mpaStorage.findAll().orElse(new ArrayList<>()).size(), 5);
    }

    @Test
    void testGetMpaById() {
        assertEquals(mpaStorage.getById(1).orElse(null), Mpa.G);
        assertEquals(mpaStorage.getById(2).orElse(null), Mpa.PG);
        assertEquals(mpaStorage.getById(3).orElse(null), Mpa.PG13);
        assertEquals(mpaStorage.getById(4).orElse(null), Mpa.R);
        assertEquals(mpaStorage.getById(5).orElse(null), Mpa.NC17);
    }

    @Test
    void testEmptyGetFilms() {
        Optional<List<Film>> films = filmStorage.findAll();

        assertTrue(films.isPresent());
        assertTrue(films.get().isEmpty());
    }

    @Test
    void testAddFilm() {
        Optional<Film> filmOptional = filmStorage.saveOne(filmOne);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(film).hasFieldOrPropertyWithValue("name", "filmOne");
                    assertThat(film).hasFieldOrPropertyWithValue("description", "descriptionOne");
                    assertThat(film).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1949, 1, 1));
                    assertThat(film).hasFieldOrPropertyWithValue("duration", 100);
                    assertThat(film).hasFieldOrPropertyWithValue("mpa", Mpa.G);
                });
    }

    @Test
    void testGetFilms() {
        filmStorage.saveOne(filmOne);
        Optional<List<Film>> filmsOptional = filmStorage.findAll();

        assertTrue(filmsOptional.isPresent());
        assertEquals(filmsOptional.get().size(), 1);
        assertThat(filmsOptional)
                .isPresent()
                .hasValueSatisfying(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "filmOne");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("description", "descriptionOne");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1949, 1, 1));
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("duration", 100);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("mpa", Mpa.G);
                });

        filmStorage.saveOne(filmTwo);
        filmsOptional = filmStorage.findAll();

        assertTrue(filmsOptional.isPresent());
        assertEquals(filmsOptional.get().size(), 2);
        assertThat(filmsOptional)
                .isPresent()
                .hasValueSatisfying(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "filmOne");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("description", "descriptionOne");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1949, 1, 1));
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("duration", 100);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("mpa", Mpa.G);

                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("name", "filmTwo");
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("description", "descriptionTwo");
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1977, 7, 7));
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("duration", 200);
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("mpa", Mpa.NC17);
                });
    }

    @Test
    void testUpdateFilm() {
        filmStorage.saveOne(filmOne);
        filmTwo.setId(1);
        filmStorage.updateOne(filmTwo);
        Optional<List<Film>> filmsOptional = filmStorage.findAll();

        assertTrue(filmsOptional.isPresent());
        assertEquals(filmsOptional.get().size(), 1);
        assertThat(filmsOptional)
                .isPresent()
                .hasValueSatisfying(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "filmTwo");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("description", "descriptionTwo");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1977, 7, 7));
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("duration", 200);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("mpa", Mpa.NC17);
                });
    }

    @Test
    void testEmptyGetUsers() {
        Optional<List<User>> usersOptional = userStorage.findAll();

        assertTrue(usersOptional.isPresent());
        assertTrue(usersOptional.get().isEmpty());
    }

    @Test
    void testAddUser() {
        Optional<User> userOptional = userStorage.saveOne(userOne);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(user).hasFieldOrPropertyWithValue("name", "nameOne");
                    assertThat(user).hasFieldOrPropertyWithValue("email", "email@email.ru");
                    assertThat(user).hasFieldOrPropertyWithValue("login", "loginOne");
                    assertThat(user).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1990, 12, 12));
                });
    }

    @Test
    void testGetUsers() {
        userStorage.saveOne(userOne);
        Optional<List<User>> usersOptional = userStorage.findAll();

        assertTrue(usersOptional.isPresent());
        assertEquals(usersOptional.get().size(), 1);
        assertThat(usersOptional)
                .isPresent()
                .hasValueSatisfying(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "nameOne");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("email", "email@email.ru");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("login", "loginOne");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1990, 12, 12));
                });

        userStorage.saveOne(userTwo);
        usersOptional = userStorage.findAll();

        assertTrue(usersOptional.isPresent());
        assertEquals(usersOptional.get().size(), 2);
        assertThat(usersOptional)
                .isPresent()
                .hasValueSatisfying(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "nameOne");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("email", "email@email.ru");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("login", "loginOne");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1990, 12, 12));

                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("name", "nameTwo");
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("email", "yandex@yandex.ru");
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("login", "loginTwo");
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1995, 5, 5));
                });
    }

    @Test
    void testUpdateUser() {
        userStorage.saveOne(userOne);
        userTwo.setId(1);
        userStorage.updateOne(userTwo);
        Optional<List<User>> usersOptional = userStorage.findAll();

        assertTrue(usersOptional.isPresent());
        assertEquals(usersOptional.get().size(), 1);
        assertThat(usersOptional)
                .isPresent()
                .hasValueSatisfying(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "nameTwo");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("email", "yandex@yandex.ru");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("login", "loginTwo");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1995, 5, 5));
                });
    }

    @Test
    void testSaveOneFriend() {
        userStorage.saveOne(userOne);
        userStorage.saveOne(userTwo);

        Optional<List<User>> friendsUserOne = userStorage.saveOneFriend(1L, 2L);

        assertTrue(friendsUserOne.isPresent());
        assertEquals(friendsUserOne.get().size(), 1);
        assertThat(friendsUserOne)
                .isPresent()
                .hasValueSatisfying(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "nameTwo");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("email", "yandex@yandex.ru");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("login", "loginTwo");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1995, 5, 5));
                });

        Optional<List<User>> friendsUserTwo = userStorage.findAllFriendsById(2L);

        assertTrue(friendsUserTwo.isPresent());
        assertTrue(friendsUserTwo.get().isEmpty());
    }

    @Test
    void testDeleteOneFriend() {
        userStorage.saveOne(userOne);
        userStorage.saveOne(userTwo);
        userStorage.saveOneFriend(1L, 2L);

        Optional<List<User>> friendsUserOne = userStorage.deleteOneFriend(1L, 2L);

        assertTrue(friendsUserOne.isPresent());
        assertTrue(friendsUserOne.get().isEmpty());
    }

    @Test
    void testGetCommonFilms() {
        userStorage.saveOne(userOne);
        userStorage.saveOne(userTwo);
        userStorage.saveOne(userThree);
        userStorage.saveOneFriend(1L, 2L);
        filmStorage.saveOne(filmOne);
        filmStorage.saveOne(filmTwo);
        filmStorage.saveOne(filmThree);
        filmStorage.creatLike(1L, 2L);

        List<Film> emptyCommonFilms = filmStorage.getCommonFilms(1L, 2L);

        assertTrue(emptyCommonFilms.isEmpty());

        filmStorage.creatLike(1L, 1L);
        List<Film> commonFilm = filmStorage.getCommonFilms(1L, 2L);

        assertEquals(filmOne, commonFilm.get(0));

        filmStorage.creatLike(2L, 3L);
        filmStorage.creatLike(2L, 1L);
        filmStorage.creatLike(2L, 2L);
        filmStorage.creatLike(3L, 1L);

        List<Film> commonFilms = filmStorage.getCommonFilms(1L, 2L);

        assertEquals(filmTwo, commonFilms.get(0));
        assertEquals(filmOne, commonFilms.get(1));
        assertEquals(2, commonFilms.size());
    }

    @Test
    void testFindRecommendationsFilms() {
        userStorage.saveOne(userOne);
        userStorage.saveOne(userTwo);
        filmStorage.saveOne(filmOne);
        filmStorage.saveOne(filmTwo);
        filmOne.setName("Some new film");
        filmStorage.saveOne(filmOne);

        List<Film> emptyListFilms = userStorage.findRecommendationsFilms(1L);

        assertTrue(emptyListFilms.isEmpty());

        filmStorage.creatLike(1L, 1L);
        filmStorage.creatLike(2L, 1L);
        filmStorage.creatLike(1L, 2L);
        filmStorage.creatLike(3L, 2L);

        List<Film> oneFilmRecommended = userStorage.findRecommendationsFilms(1L);

        assertThat(oneFilmRecommended)
                .hasSize(1);
        assertThat(oneFilmRecommended.get(0))
                .hasFieldOrPropertyWithValue("id", 3L)
                .hasFieldOrPropertyWithValue("name", "Some new film")
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1949, 1, 1))
                .hasFieldOrPropertyWithValue("duration", 100)
                .hasFieldOrPropertyWithValue("mpa", Mpa.G);

        filmStorage.creatLike(3L, 1L);

        List<Film> emptyListFilmsAfterLike = userStorage.findRecommendationsFilms(1L);

        assertTrue(emptyListFilmsAfterLike.isEmpty());
    }

    @Test
    void deleteFilmById() {
        filmStorage.saveOne(filmOne);
        Optional<List<Film>> filmsOptional = filmStorage.findAll();

        assertTrue(filmsOptional.isPresent());
        assertEquals(filmsOptional.get().size(), 1);

        filmStorage.deleteFilmById(1);

        filmsOptional = filmStorage.findAll();

        assertTrue(filmsOptional.isPresent());
        assertEquals(filmsOptional.get().size(), 0);
    }

    @Test
    void deleteUserById() {
        userStorage.saveOne(userOne);
        Optional<List<User>> usersOptional = userStorage.findAll();

        assertTrue(usersOptional.isPresent());
        assertEquals(usersOptional.get().size(), 1);

        userStorage.deleteUserById(1);

        usersOptional = userStorage.findAll();

        assertTrue(usersOptional.isPresent());
        assertEquals(usersOptional.get().size(), 0);
    }
}
