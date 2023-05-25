package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mapper.Mapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class DbFilmStorageImpl implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public DbFilmStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public Optional<List<Film>> getFilms() {
        String sqlQueryGetFilms = "select F.FILM_ID        as id,\n" +
                "       FILM_NAME        as name,\n" +
                "       FILM_DESCRIPTION as description,\n" +
                "       R.RATING_NAME    as mpa,\n" +
                "       RELEASE_DATE     as releaseDate,\n" +
                "       DURATION         as duration\n" +
                "from FILMS F\n" +
                "         left join RATINGS R on F.RATING_ID = R.RATING_ID\n" +
                "order by id";

        List<Film> films = jdbcTemplate.query(sqlQueryGetFilms, Mapper::mapRowToFilm);

        Map<Long, Film> mapFilms = films.stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));

        String sqlQueryGetGenres = "select FILM_ID as id,\n" +
                "       GENRE_NAME as genreName\n" +
                "from GENRES_FILMS GF\n" +
                "    left join GENRES G on GF.GENRE_ID = G.GENRE_ID\n" +
                "order by id";

        List<Map<String, Object>> genresFilms = jdbcTemplate.queryForList(sqlQueryGetGenres);

        genresFilms.forEach(
                t -> mapFilms.get(Long.parseLong(t.get("id").toString())).getGenres().add(
                        Genre.valueOf(t.get("genreName").toString())
                ));

        String sqlQueryGetLikes = "select USER_ID as userId,\n" +
                "       FILM_ID as filmId\n" +
                "from LIKES_FILMS\n" +
                "order by filmId, userId";

        List<Map<String, Object>> likesFilms = jdbcTemplate.queryForList(sqlQueryGetLikes);

        likesFilms.forEach(
                t -> mapFilms.get(Long.parseLong(t.get("filmId").toString())).getLikes().add(
                        Long.parseLong(t.get("userId").toString())
                ));

        return Optional.of(films);
    }

    @Override
    @Transactional
    public Optional<Film> getFilm(Long id) {
        String sqlQueryGetFilm = "select F.FILM_ID          as id,\n" +
                "       F.FILM_NAME        as name,\n" +
                "       F.FILM_DESCRIPTION as description,\n" +
                "       R.RATING_NAME      as mpa,\n" +
                "       F.RELEASE_DATE     as releaseDate,\n" +
                "       F.DURATION         as duration\n" +
                "from FILMS F\n" +
                "         left join RATINGS R on R.RATING_ID = F.RATING_ID\n" +
                "where FILM_ID = ?\n";

        Film film;

        try {
            film = jdbcTemplate.queryForObject(sqlQueryGetFilm, Mapper::mapRowToFilm, id);
        } catch (DataAccessException e) {
            return Optional.empty();
        }

        assert film != null;

        String sqlQueryGetGenres = "select GENRE_NAME as genre\n" +
                "from GENRES_FILMS\n" +
                "         left join GENRES G on G.GENRE_ID = GENRES_FILMS.GENRE_ID\n" +
                "where FILM_ID = ?\n" +
                "order by G.GENRE_ID";

        List<Genre> genresFilms = jdbcTemplate.query(sqlQueryGetGenres, Mapper::mapRowToGenre, id);

        film.setGenres(new LinkedHashSet<>(genresFilms));

        String sqlQueryGetLikes = "select USER_ID as userId\n" +
                "from LIKES_FILMS\n" +
                "where FILM_ID = ?\n" +
                "order by userId";

        List<Long> likesFilm = jdbcTemplate.query(sqlQueryGetLikes,
                (rs, rowNum) -> rs.getLong(1),
                id
        );

        film.setLikes(new LinkedHashSet<>(likesFilm));

        return Optional.of(film);
    }

    @Override
    @Transactional
    public Optional<Film> addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");

        Long filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();

        String sqlQueryAddGenres = "insert into GENRES_FILMS (FILM_ID, GENRE_ID)\n" +
                "values (?, ?)";

        List<Genre> genres = new ArrayList<>(film.getGenres());
        jdbcTemplate.batchUpdate(sqlQueryAddGenres, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setInt(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });

        String sqlQueryAddLikes = "insert into LIKES_FILMS (FILM_ID, USER_ID)\n" +
                "values (?, ?)";

        List<Long> likes = new ArrayList<>(film.getLikes());

        jdbcTemplate.batchUpdate(sqlQueryAddLikes, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setLong(2, likes.get(i));
            }

            @Override
            public int getBatchSize() {
                return likes.size();
            }
        });

        return getFilm(filmId);
    }

    @Override
    @Transactional
    public Optional<Film> updateFilm(Film film) {
        String sqlQueryUpdateFilms = "update FILMS\n" +
                "set FILM_NAME = ?,\n" +
                "    FILM_DESCRIPTION = ?,\n" +
                "    RATING_ID = ?,\n" +
                "    RELEASE_DATE = ?,\n" +
                "    DURATION = ?\n" +
                "where FILM_ID = ?";

        try {
            jdbcTemplate.update(sqlQueryUpdateFilms,
                    film.getName(),
                    film.getDescription(),
                    film.getMpa().getId(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getId()
            );
        } catch (DataAccessException e) {
            return Optional.empty();
        }

        String sqlQueryDeleteGenres = "delete from GENRES_FILMS\n" +
                "where FILM_ID = ?";

        jdbcTemplate.update(sqlQueryDeleteGenres, film.getId());

        String sqlQueryAddGenres = "insert into GENRES_FILMS (FILM_ID, GENRE_ID)\n" +
                "values (?, ?)";

        List<Genre> genres = new ArrayList<>(film.getGenres());
        jdbcTemplate.batchUpdate(sqlQueryAddGenres, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, film.getId());
                ps.setInt(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });

        String sqlQueryDeleteLikes = "delete from LIKES_FILMS\n" +
                "where FILM_ID = ?";

        jdbcTemplate.update(sqlQueryDeleteLikes, film.getId());

        String sqlQueryAddLikes = "insert into LIKES_FILMS (FILM_ID, USER_ID)\n" +
                "values (?, ?)";

        List<Long> likes = new ArrayList<>(film.getLikes());

        jdbcTemplate.batchUpdate(sqlQueryAddLikes, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, film.getId());
                ps.setLong(2, likes.get(i));
            }

            @Override
            public int getBatchSize() {
                return likes.size();
            }
        });

        return getFilm(film.getId());
    }
}
