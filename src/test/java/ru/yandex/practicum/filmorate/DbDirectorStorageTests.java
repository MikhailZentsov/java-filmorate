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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest()
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DbDirectorStorageTests {

    private final DirectorStorage directorStorage;

    private static Director directorOne;

    private static Director directorTwo;

    @BeforeEach
    void setUp() {
        directorOne = new Director(
                0,
                "directorOne"
        );

        directorTwo = new Director(
                0,
                "directorTwo"
        );
    }

    @Test
    void createDirectorTest() {
        Optional<Director> directorOptional = directorStorage.createDirector(directorOne);

        assertThat(directorOptional)
                .isPresent()
                .hasValueSatisfying(director -> {
                            assertThat(director).hasFieldOrPropertyWithValue("id", 1L);
                            assertThat(director).hasFieldOrPropertyWithValue("name", "directorOne");
                        });
    }

    @Test
    void getEmptyDirectorsTest() {
        Optional<List<Director>> directors = directorStorage.getDirectors();
        assertTrue(directors.isPresent());
        assertTrue(directors.get().isEmpty());
    }

    @Test
    void updateDirectorTest() {
        directorStorage.createDirector(directorOne);
        directorTwo.setId(1L);
        directorStorage.updateDirector(directorTwo);
        Optional<List<Director>> directorsOptional = directorStorage.getDirectors();

        assertTrue(directorsOptional.isPresent());
        assertEquals(1, directorsOptional.get().size());
        assertThat(directorsOptional)
                .isPresent()
                .hasValueSatisfying(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "directorTwo");
                });
    }

    @Test
    void getDirectorsTest() {
        directorStorage.createDirector(directorOne);
        assertTrue(directorStorage.getDirectors().isPresent());
        assertEquals(1, directorStorage.getDirectors().get().size());
        directorStorage.createDirector(directorTwo);
        assertEquals(2, directorStorage.getDirectors().get().size());
    }

    @Test
    void getDirectorTest() {
        directorStorage.createDirector(directorOne);
        directorOne.setId(1);
        assertTrue(directorStorage.getDirector(1).isPresent());
        assertEquals(directorStorage.getDirector(1).get(), directorOne);
    }

    @Test
    void removeDirectorTest() {
        Optional<Director> director = directorStorage.createDirector(directorOne);
        assertTrue(director.isPresent());
        directorStorage.removeDirector(director.get().getId());
        assertTrue(directorStorage.getDirectors().isPresent());
        assertEquals(0, directorStorage.getDirectors().get().size());
    }
}
