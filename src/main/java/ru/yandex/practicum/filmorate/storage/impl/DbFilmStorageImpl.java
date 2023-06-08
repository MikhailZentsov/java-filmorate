package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mapper.Mapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
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
    public Optional<List<Film>> findAll() {
        String sqlQueryGetFilms = "select F.FILM_ID        as id, " +
                "       FILM_NAME        as name, " +
                "       FILM_DESCRIPTION as description, " +
                "       R.RATING_NAME    as mpa, " +
                "       RELEASE_DATE     as releaseDate, " +
                "       DURATION         as duration " +
                "from FILMS F " +
                "         left join RATINGS R on F.RATING_ID = R.RATING_ID " +
                "order by id";

        List<Film> films = jdbcTemplate.query(sqlQueryGetFilms, Mapper::mapRowToFilm);

        Map<Long, Film> mapFilms = films.stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));

        String sqlQueryGetGenres = "select FILM_ID as id, " +
                "       GENRE_NAME as genreName " +
                "from GENRES_FILMS GF " +
                "    left join GENRES G on GF.GENRE_ID = G.GENRE_ID " +
                "order by id";

        List<Map<String, Object>> genresFilms = jdbcTemplate.queryForList(sqlQueryGetGenres);

        genresFilms.forEach(
                t -> mapFilms.get(Long.parseLong(t.get("id").toString())).getGenres().add(
                        Genre.valueOf(t.get("genreName").toString())
                ));

        String sqlQueryGetDirectors = "select DIRECTOR_ID as id, DIRECTOR_NAME as name " +
                "from DIRECTORS ";

        List<Map<String, Object>> directorsFilms = jdbcTemplate.queryForList(sqlQueryGetDirectors);

        directorsFilms.forEach(t -> {
            long directorId = Long.parseLong(t.get("id").toString());
            String directorName = t.get("name").toString();
            Director director = new Director(directorId, directorName); // Замените на свой класс режиссера

            // Найти фильм по идентификатору режиссера и добавить режиссера к нему
            mapFilms.values().forEach(film -> {
                if (film.getDirectors().contains(directorId)) {
                    film.getDirectors().add(director);
                }
            });
        });

        return Optional.of(films);
    }

    @Override
    @Transactional
    public Optional<Film> getById(Long id) {
        String sqlQueryGetFilm = "select F.FILM_ID          as id, " +
                "       F.FILM_NAME        as name, " +
                "       F.FILM_DESCRIPTION as description, " +
                "       R.RATING_NAME      as mpa, " +
                "       F.RELEASE_DATE     as releaseDate, " +
                "       F.DURATION         as duration " +
                "from FILMS F " +
                "         left join RATINGS R on R.RATING_ID = F.RATING_ID " +
                "where FILM_ID = ? ";

        Film film;

        try {
            film = jdbcTemplate.queryForObject(sqlQueryGetFilm, Mapper::mapRowToFilm, id);
        } catch (DataAccessException e) {
            return Optional.empty();
        }

        assert film != null;

        String sqlQueryGetGenres = "select GENRE_NAME as genre " +
                "from GENRES_FILMS " +
                "         left join GENRES G on G.GENRE_ID = GENRES_FILMS.GENRE_ID " +
                "where FILM_ID = ? " +
                "order by G.GENRE_ID";

        List<Genre> genresFilms = jdbcTemplate.query(sqlQueryGetGenres, Mapper::mapRowToGenre, id);
        film.setGenres(new LinkedHashSet<>(genresFilms));

        String sqlQueryGetDirectors = "select DIRECTOR_ID as id, DIRECTOR_NAME as name " +
                "from DIRECTORS " +
                "where DIRECTOR_ID = ?";

        List<Director> directorsFilms = jdbcTemplate.query(sqlQueryGetDirectors, Mapper::mapToRowDirector, id);
        film.setDirectors(new HashSet<>(directorsFilms));

        return Optional.of(film);
    }

    @Override
    @Transactional
    public Optional<Film> saveOne(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");

        Long filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();

        String sqlQueryAddGenres = "insert into GENRES_FILMS (FILM_ID, GENRE_ID) " +
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

        List<Director> directors = new ArrayList<>(film.getDirectors());

        String sqlQueryAddDirectors = "insert into DIRECTORS_FILMS (DIRECTOR_ID, FILM_ID) " +
                "values (?, ?)";

        jdbcTemplate.batchUpdate(sqlQueryAddDirectors, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setLong(2, directors.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return directors.size();
            }
        });

        return getById(filmId);
    }

    @Override
    @Transactional
    public Optional<Film> updateOne(Film film) {
        String sqlQueryUpdateFilms = "update FILMS " +
                "set FILM_NAME = ?, " +
                "    FILM_DESCRIPTION = ?, " +
                "    RATING_ID = ?, " +
                "    RELEASE_DATE = ?, " +
                "    DURATION = ? " +
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

        String sqlQueryDeleteGenres = "delete from GENRES_FILMS " +
                "where FILM_ID = ?";

        jdbcTemplate.update(sqlQueryDeleteGenres, film.getId());

        String sqlQueryAddGenres = "insert into GENRES_FILMS (FILM_ID, GENRE_ID) " +
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

        return getById(film.getId());
    }

    @Override
    public List<Film> getPopularFilms(Long count) {
        String sqlQueryGetPopularFilms = "select F.FILM_ID as id, " +
                "       FILM_DESCRIPTION as description, " +
                "       FILM_NAME as name, " +
                "       RELEASE_DATE as releaseDate, " +
                "       DURATION as duration, " +
                "       R.RATING_NAME as mpa " +
                "from FILMS F " +
                "       left join LIKES_FILMS FL on FL.FILM_ID = F.FILM_ID " +
                "       left join RATINGS R on R.RATING_ID = F.RATING_ID " +
                "group by F.FILM_ID, " +
                "       FILM_DESCRIPTION, " +
                "       FILM_NAME, " +
                "       RELEASE_DATE, " +
                "       DURATION, " +
                "       R.RATING_NAME " +
                "order by count(FL.USER_ID) desc " +
                "limit ?";
        List<Film> films = jdbcTemplate.query(sqlQueryGetPopularFilms, Mapper::mapRowToFilm, count);
        if (!films.isEmpty()) {
            Map<Long, Film> mapFilms = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
            String sqlQueryGetAllGenres = "select FG.FILM_ID as filmId, " +
                    "       G2.GENRE_NAME as genreName, " +
                    "       G2.GENRE_ID as genreId " +
                    "from GENRES_FILMS FG " +
                    "    left join GENRES G2 on FG.GENRE_ID = G2.GENRE_ID " +
                    "where FG.FILM_ID IN ( " + mapFilms.keySet()
                                                .stream()
                                                .map(String::valueOf)
                                                .collect(Collectors.joining(",")) + " ) " +
                    "order by genreId ";
            List<Map<String, Object>> genres = jdbcTemplate.queryForList(sqlQueryGetAllGenres);
            genres.forEach(t -> mapFilms.get(Long.parseLong(t.get("filmId").toString()))
                    .getGenres()
                    .add(Genre.valueOf(t.get("genreName").toString())
                    ));
        }

        return films;
    }

    @Override
    public void creatLike(Long idFilm, Long idUser) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("LIKES_FILMS");

        Map<String, Object> like = new HashMap<>();
        like.put("FILM_ID", idFilm);
        like.put("USER_ID", idUser);

        simpleJdbcInsert.execute(like);
    }

    @Override
    public void removeLike(Long idFilm, Long idUser) {
        String sqlQueryDeleteLikes = "delete from LIKES_FILMS " +
                "where FILM_ID = ? AND USER_ID = ?";

        jdbcTemplate.update(sqlQueryDeleteLikes, idFilm, idUser);
    }
}
