package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DbFilmStorageImpl implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public List<Film> findAll() {
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

        String sqlQueryGetDirectors = "select FILM_ID as filmId, " +
                "       D.DIRECTOR_ID as directorId, " +
                "       DIRECTOR_NAME as directorName " +
                "from DIRECTORS_FILMS " +
                "    inner join DIRECTORS D on D.DIRECTOR_ID = DIRECTORS_FILMS.DIRECTOR_ID";

        List<Map<String, Object>> directorsFilms = jdbcTemplate.queryForList(sqlQueryGetDirectors);

        directorsFilms.forEach(t -> mapFilms.get(Long.parseLong(t.get("filmId").toString())).getDirectors().add(
                new Director.Builder()
                        .id(Long.parseLong(t.get("directorId").toString()))
                        .name(t.get("directorName").toString())
                        .build()
        ));

        return films;
    }

    @Override
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

        log.info("Попытка записать жанры и директоров");

        assert film != null;

        setGenresToOneFilm(film, jdbcTemplate);
        setDirectorsToOneFilm(film, jdbcTemplate);

        return Optional.of(film);
    }

    @Override
    @Transactional
    public Optional<Film> saveOne(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");

        long filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
        film.setId(filmId);

        saveGenresFromOneFilm(film, jdbcTemplate);
        saveDirectorsFromOneFilm(film, jdbcTemplate);

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

        if (jdbcTemplate.update(sqlQueryUpdateFilms,
                film.getName(),
                film.getDescription(),
                film.getMpa().getId(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId()) == 0) {
            return Optional.empty();
        }

        String sqlQueryDeleteGenres = "delete from GENRES_FILMS " +
                "where FILM_ID = ?";

        jdbcTemplate.update(sqlQueryDeleteGenres, film.getId());
        saveGenresFromOneFilm(film, jdbcTemplate);

        String sqlQueryDeleteDirectors = "delete from DIRECTORS_FILMS " +
                "where FILM_ID = ?";
        jdbcTemplate.update(sqlQueryDeleteDirectors, film.getId());
        saveDirectorsFromOneFilm(film, jdbcTemplate);

        return getById(film.getId());
    }

    @Override
    @Transactional
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
            setGenresToMapFilms(mapFilms, jdbcTemplate);
            setDirectorsToMapFilms(mapFilms, jdbcTemplate);
        }

        return films;
    }

    @Override
    @Transactional
    public List<Film> getPopularFilms(Long count, Integer genreId) {
        String sqlQueryGetPopularFilms = "with filtered_CTE as (" +
                "select DISTINCT F.FILM_ID, " +
                "       FILM_DESCRIPTION, " +
                "       FILM_NAME, " +
                "       RELEASE_DATE, " +
                "       DURATION, " +
                "       RATING_ID " +
                "from FILMS F " +
                "       left join GENRES_FILMS GF on F.FILM_ID = GF.FILM_ID " +
                "where GENRE_ID = ? " +
                ")" +
                "select FCTE.FILM_ID as id, " +
                "       FILM_DESCRIPTION as description, " +
                "       FILM_NAME as name, " +
                "       RELEASE_DATE as releaseDate, " +
                "       DURATION as duration, " +
                "       RATING_NAME as mpa " +
                "from filtered_CTE FCTE " +
                "       left join LIKES_FILMS FL on FL.FILM_ID = FCTE.FILM_ID " +
                "       left join RATINGS R on R.RATING_ID = FCTE.RATING_ID " +
                "group by id, " +
                "       description, " +
                "       name, " +
                "       releaseDate, " +
                "       duration, " +
                "       mpa " +
                "order by count(FL.USER_ID) desc " +
                "limit ?";

        List<Film> films = jdbcTemplate.query(sqlQueryGetPopularFilms, Mapper::mapRowToFilm, genreId, count);

        if (!films.isEmpty()) {
            Map<Long, Film> mapFilms = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
            setGenresToMapFilms(mapFilms, jdbcTemplate);
            setDirectorsToMapFilms(mapFilms, jdbcTemplate);
        }

        return films;
    }

    @Override
    @Transactional
    public List<Film> getPopularFilms(Long count, String year) {
        String sqlQueryGetPopularFilms = "with filtered_CTE as (" +
                "select DISTINCT F.FILM_ID, " +
                "       FILM_DESCRIPTION, " +
                "       FILM_NAME, " +
                "       RELEASE_DATE, " +
                "       DURATION, " +
                "       RATING_ID " +
                "from FILMS F " +
                "where EXTRACT(YEAR FROM f.RELEASE_DATE) = ? " +
                ")" +
                "select FCTE.FILM_ID as id, " +
                "       FILM_DESCRIPTION as description, " +
                "       FILM_NAME as name, " +
                "       RELEASE_DATE as releaseDate, " +
                "       DURATION as duration, " +
                "       RATING_NAME as mpa " +
                "from filtered_CTE FCTE " +
                "       left join LIKES_FILMS FL on FL.FILM_ID = FCTE.FILM_ID " +
                "       left join RATINGS R on R.RATING_ID = FCTE.RATING_ID " +
                "group by id, " +
                "       description, " +
                "       name, " +
                "       releaseDate, " +
                "       duration, " +
                "       mpa " +
                "order by count(FL.USER_ID) desc " +
                "limit ?";

        List<Film> films = jdbcTemplate.query(sqlQueryGetPopularFilms, Mapper::mapRowToFilm, year, count);

        if (!films.isEmpty()) {
            Map<Long, Film> mapFilms = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
            setGenresToMapFilms(mapFilms, jdbcTemplate);
            setDirectorsToMapFilms(mapFilms, jdbcTemplate);
        }

        return films;
    }

    @Override
    @Transactional
    public List<Film> getPopularFilms(Long count, Integer genreId, String year) {
        String sqlQueryGetPopularFilms = "with filtered_CTE as (" +
                "select DISTINCT F.FILM_ID, " +
                "       FILM_DESCRIPTION, " +
                "       FILM_NAME, " +
                "       RELEASE_DATE, " +
                "       DURATION, " +
                "       RATING_ID " +
                "from FILMS F " +
                "       left join GENRES_FILMS GF on F.FILM_ID = GF.FILM_ID " +
                "where GENRE_ID = ? " +
                "       and EXTRACT(YEAR FROM f.RELEASE_DATE) = ? " +
                ")" +
                "select FCTE.FILM_ID as id, " +
                "       FILM_DESCRIPTION as description, " +
                "       FILM_NAME as name, " +
                "       RELEASE_DATE as releaseDate, " +
                "       DURATION as duration, " +
                "       RATING_NAME as mpa " +
                "from filtered_CTE FCTE " +
                "       left join LIKES_FILMS FL on FL.FILM_ID = FCTE.FILM_ID " +
                "       left join RATINGS R on R.RATING_ID = FCTE.RATING_ID " +
                "group by id, " +
                "       description, " +
                "       name, " +
                "       releaseDate, " +
                "       duration, " +
                "       mpa " +
                "order by count(FL.USER_ID) desc " +
                "limit ?";

        List<Film> films = jdbcTemplate.query(sqlQueryGetPopularFilms, Mapper::mapRowToFilm, genreId, year, count);

        if (!films.isEmpty()) {
            Map<Long, Film> mapFilms = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
            setGenresToMapFilms(mapFilms, jdbcTemplate);
            setDirectorsToMapFilms(mapFilms, jdbcTemplate);
        }

        return films;
    }

    @Override
    @Transactional
    public void creatLike(Long idFilm, Long idUser) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("LIKES_FILMS");

        Map<String, Object> like = new HashMap<>();
        like.put("FILM_ID", idFilm);
        like.put("USER_ID", idUser);

        simpleJdbcInsert.execute(like);
    }

    @Override
    @Transactional
    public void removeLike(Long idFilm, Long idUser) {
        String sqlQueryDeleteLikes = "delete from LIKES_FILMS " +
                "where FILM_ID = ? AND USER_ID = ?";

        jdbcTemplate.update(sqlQueryDeleteLikes, idFilm, idUser);
    }

    @Override
    @Transactional
    public Optional<Boolean> deleteFilmById(long filmId) {
        String sqlQueryDeleteFilm = "delete from FILMS where FILM_ID = ?;";
        if (jdbcTemplate.update(sqlQueryDeleteFilm, filmId) == 0) {
            return Optional.empty();
        }

        return Optional.of(true);
    }

    @Override
    @Transactional
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        String sql = "WITH common_films AS ( " +
                "    SELECT lf.FILM_ID " +
                "    FROM LIKES_FILMS lf " +
                "        INNER JOIN ( " +
                "            SELECT lf2.FILM_ID  " +
                "            FROM LIKES_FILMS lf2  " +
                "            WHERE lf2.USER_ID = ? " +
                "        ) AS flf ON lf.FILM_ID = flf.FILM_ID  " +
                "    WHERE lf.USER_ID = ? " +
                ") " +
                " " +
                "SELECT f.FILM_ID AS id, " +
                "    f.FILM_NAME AS name, " +
                "    f.FILM_DESCRIPTION AS description, " +
                "    r.RATING_NAME AS mpa, " +
                "    f.RELEASE_DATE AS releaseDate, " +
                "    f.DURATION AS duration " +
                "FROM FILMS f  " +
                "    INNER JOIN common_films cf ON f.FILM_ID = cf.FILM_ID " +
                "    INNER JOIN RATINGS r ON f.RATING_ID = r.RATING_ID " +
                "    LEFT JOIN LIKES_FILMS lf ON f.FILM_ID = lf.FILM_ID  " +
                "GROUP BY id, name, description, mpa, releaseDate, duration " +
                "ORDER BY count(lf.USER_ID) DESC ";
        return jdbcTemplate.query(sql, Mapper::mapRowToFilm, userId, friendId);
    }

    @Override
    @Transactional
    public List<Film> getFilmsByDirectorSortedByYear(Long directorId, String sort) {
        String sqlQueryGetDirectorFilmsSortedByLike = "SELECT f.FILM_ID as id, f.FILM_NAME as name, f.FILM_DESCRIPTION as description, " +
                "R.RATING_NAME as mpa, F.RELEASE_DATE as releaseDate, F.DURATION as duration " +
                "FROM PUBLIC.FILMS f " +
                "JOIN PUBLIC.DIRECTORS_FILMS df ON f.FILM_ID = df.FILM_ID " +
                "LEFT JOIN PUBLIC.RATINGS R ON R.RATING_ID = f.RATING_ID " +
                "WHERE df.DIRECTOR_ID = ? " +
                "ORDER BY EXTRACT(YEAR FROM f.RELEASE_DATE)";

        List<Film> films = jdbcTemplate.query(sqlQueryGetDirectorFilmsSortedByLike, Mapper::mapRowToFilm, directorId);

        if (!films.isEmpty()) {
            Map<Long, Film> mapFilms = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));

            setGenresToMapFilms(mapFilms, jdbcTemplate);
            setDirectorsToMapFilms(mapFilms, jdbcTemplate);
        }

        return films;
    }

    @Override
    @Transactional
    public List<Film> getFilmsByDirectorSortedByLikes(Long directorId, String sort) {
        String sqlQueryGetDirectorFilmsSortedByLike = "SELECT f.FILM_ID as id, f.FILM_NAME as name, f.FILM_DESCRIPTION as description, " +
                "R.RATING_NAME as mpa, F.RELEASE_DATE as releaseDate, F.DURATION as duration " +
                "FROM PUBLIC.FILMS f " +
                "JOIN PUBLIC.DIRECTORS_FILMS df ON f.FILM_ID = df.FILM_ID " +
                "LEFT JOIN PUBLIC.RATINGS R ON R.RATING_ID = f.RATING_ID " +
                "LEFT JOIN LIKES_FILMS LF on f.FILM_ID = LF.FILM_ID " +
                "WHERE df.DIRECTOR_ID = ? " +
                "GROUP BY id, name, description, mpa, releaseDate, duration " +
                "ORDER BY COUNT(LF.USER_ID) DESC";

        List<Film> films = jdbcTemplate.query(sqlQueryGetDirectorFilmsSortedByLike, Mapper::mapRowToFilm, directorId);

        if (!films.isEmpty()) {
            Map<Long, Film> mapFilms = films.stream()
                    .collect(Collectors.toMap(Film::getId, Function.identity()));

            setGenresToMapFilms(mapFilms, jdbcTemplate);
            setDirectorsToMapFilms(mapFilms, jdbcTemplate);
        }

        return films;
    }

    @Override
    @Transactional
    public List<Film> findFilmsByNameAndDirector(String query, List<String> by) {
        String sqlQuery;
        String sqlQueryFindFilmsByName = "select FF.FILM_ID             as id, " +
                "             FILM_NAME              as name, " +
                "             FILM_DESCRIPTION       as description, " +
                "             R.RATING_NAME          as mpa, " +
                "             RELEASE_DATE           as releaseDate, " +
                "             DURATION               as duration, " +
                "             COALESCE(LF.USER_ID, 0) as likes " +
                "      from FILMS FF " +
                "               left join RATINGS R on FF.RATING_ID = R.RATING_ID " +
                "               left join LIKES_FILMS LF on FF.FILM_ID = LF.FILM_ID " +
                "      where FILM_NAME ILIKE CONCAT('%', ?, '%') ";
        String sqlQueryFindFilmsByDirector = "select F.FILM_ID              as id, " +
                "             FILM_NAME              as name, " +
                "             FILM_DESCRIPTION       as description, " +
                "             R.RATING_NAME          as mpa, " +
                "             RELEASE_DATE           as releaseDate, " +
                "             DURATION               as duration, " +
                "             COALESCE(L.USER_ID, 0) as likes " +
                "      from FILMS F " +
                "               left join RATINGS R on F.RATING_ID = R.RATING_ID " +
                "               inner join DIRECTORS_FILMS DF on F.FILM_ID = DF.FILM_ID " +
                "               inner join DIRECTORS D on DF.DIRECTOR_ID = D.DIRECTOR_ID " +
                "               left join LIKES_FILMS L on F.FILM_ID = L.FILM_ID " +
                "      where DIRECTOR_NAME ILIKE CONCAT('%', ?, '%')";

        String sqlQueryTop = "select id, name, description, mpa, releaseDate, duration " +
                "from (";
        String addQueryBottom = ") as t " +
                "group by id, " +
                "         name, " +
                "         description, " +
                "         mpa, " +
                "         releaseDate, " +
                "         duration " +
                "order by sum(likes) desc, id";

        List<Film> films;
        if (by.size() == 2) {
            sqlQuery = sqlQueryTop +
                    sqlQueryFindFilmsByName +
                    "union all " +
                    sqlQueryFindFilmsByDirector +
                    addQueryBottom;
            films = jdbcTemplate.query(sqlQuery, Mapper::mapRowToFilm, query, query);
        } else if (by.contains("title")) {
            sqlQuery = sqlQueryTop + sqlQueryFindFilmsByName + addQueryBottom;
            films = jdbcTemplate.query(sqlQuery, Mapper::mapRowToFilm, query);
        } else if (by.contains("director")) {
            sqlQuery = sqlQueryTop + sqlQueryFindFilmsByDirector + addQueryBottom;
            films = jdbcTemplate.query(sqlQuery, Mapper::mapRowToFilm, query);
        } else {
            return new ArrayList<>();
        }


        if (!films.isEmpty()) {
            Map<Long, Film> mapFilms = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));

            setGenresToMapFilms(mapFilms, jdbcTemplate);
            setDirectorsToMapFilms(mapFilms, jdbcTemplate);
        }

        return films;
    }

    private static void setGenresToOneFilm(Film film, JdbcTemplate jdbcTemplate) {
        String sqlQueryGetGenres = "select GENRE_NAME " +
                "from GENRES_FILMS " +
                "         left join GENRES G on G.GENRE_ID = GENRES_FILMS.GENRE_ID " +
                "where FILM_ID = ? " +
                "order by G.GENRE_ID";

        List<Genre> genresFilms = jdbcTemplate.query(sqlQueryGetGenres, Mapper::mapRowToGenre, film.getId());

        film.setGenres(new LinkedHashSet<>(genresFilms));
    }

    private static void setDirectorsToOneFilm(Film film, JdbcTemplate jdbcTemplate) {
        String sqlQueryGetDirectors = "select D.DIRECTOR_ID as id, " +
                "       DIRECTOR_NAME as name " +
                "from DIRECTORS_FILMS " +
                "    inner join DIRECTORS D on D.DIRECTOR_ID = DIRECTORS_FILMS.DIRECTOR_ID " +
                "where FILM_ID = ?";

        List<Director> directors = jdbcTemplate.query(sqlQueryGetDirectors, Mapper::mapToRowDirector, film.getId());

        film.setDirectors(new LinkedHashSet<>(directors));
    }

    private static void saveGenresFromOneFilm(Film film, JdbcTemplate jdbcTemplate) {
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
    }

    private static void saveDirectorsFromOneFilm(Film film, JdbcTemplate jdbcTemplate) {
        List<Director> directors = new ArrayList<>(film.getDirectors());

        String sqlQueryAddDirectors = "insert into DIRECTORS_FILMS (DIRECTOR_ID, FILM_ID) " +
                "values (?, ?)";

        jdbcTemplate.batchUpdate(sqlQueryAddDirectors, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, directors.get(i).getId());
                ps.setLong(2, film.getId());
            }

            @Override
            public int getBatchSize() {
                return directors.size();
            }
        });
    }

    private static void setGenresToMapFilms(Map<Long, Film> films, JdbcTemplate jdbcTemplate) {
        String sqlQueryGetAllGenres = "select FG.FILM_ID as filmId, " +
                "       G2.GENRE_NAME as genreName, " +
                "       G2.GENRE_ID as genreId " +
                "from GENRES_FILMS FG " +
                "    left join GENRES G2 on FG.GENRE_ID = G2.GENRE_ID " +
                "where FG.FILM_ID IN ( " +
                films.keySet()
                        .stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(",")) +
                " ) " +
                "order by genreId ";
        List<Map<String, Object>> genres = jdbcTemplate.queryForList(sqlQueryGetAllGenres);
        genres.forEach(t -> films.get(Long.parseLong(t.get("filmId").toString()))
                .getGenres()
                .add(Genre.valueOf(t.get("genreName").toString())
                ));
    }

    private static void setDirectorsToMapFilms(Map<Long, Film> films, JdbcTemplate jdbcTemplate) {
        String sqlQueryGetDirectors = "select FILM_ID as filmId, " +
                "       D.DIRECTOR_ID as directorId, " +
                "       DIRECTOR_NAME as directorName " +
                "from DIRECTORS_FILMS " +
                "    inner join DIRECTORS D on D.DIRECTOR_ID = DIRECTORS_FILMS.DIRECTOR_ID " +
                "where FILM_ID IN (" +
                films.keySet()
                        .stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(",")) +
                " )" +
                "order by directorId";

        List<Map<String, Object>> directorsFilms = jdbcTemplate.queryForList(sqlQueryGetDirectors);

        directorsFilms.forEach(t -> films.get(Long.parseLong(t.get("filmId").toString())).getDirectors().add(
                new Director.Builder()
                        .id(Long.parseLong(t.get("directorId").toString()))
                        .name(t.get("directorName").toString())
                        .build()
        ));
    }
}
