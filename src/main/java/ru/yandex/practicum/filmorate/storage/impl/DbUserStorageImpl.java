package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DbUserStorageImpl implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public List<User> findAll() {
        String sqlQueryGetUsers = "select USER_ID as id, " +
                "       USER_NAME as name, " +
                "       EMAIL as email, " +
                "       LOGIN as login, " +
                "       BIRTHDAY as birthday " +
                "from USERS " +
                "order by USER_ID";

        List<User> users = jdbcTemplate.query(sqlQueryGetUsers, UserMapper::mapRowToUser);

        log.info("Получены все пользователи.");

        return users;
    }

    @Override
    @Transactional
    public Optional<User> getById(Long id) {
        String sqlQueryGetUser = "select USER_ID as id, " +
                "       USER_NAME as name, " +
                "       EMAIL as email, " +
                "       LOGIN as login, " +
                "       BIRTHDAY as birthday " +
                "from USERS " +
                "where USER_ID = ?" +
                "order by USER_ID";

        User user;

        try {
            user = jdbcTemplate.queryForObject(sqlQueryGetUser, UserMapper::mapRowToUser, id);
            log.info("Пользователь с ID = {} получен.", id);
        } catch (DataAccessException e) {
            log.info("Пользователь с ID = {} не существует.", id);

            return Optional.empty();
        }

        return Optional.of(user);
    }

    @Override
    @Transactional
    public Optional<User> saveOne(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");

        Long userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();

        log.info("Пользователь с ID = {} записан.", userId);

        return getById(userId);
    }

    @Override
    @Transactional
    public Optional<User> updateOne(User user) {
        String sqlQueryUpdateUser = "update USERS " +
                "set EMAIL = ?, " +
                "    LOGIN = ?, " +
                "    USER_NAME = ?, " +
                "    BIRTHDAY = ? " +
                "where USER_ID = ?";

        try {
            jdbcTemplate.update(sqlQueryUpdateUser,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId()
            );
        } catch (DataAccessException e) {
            log.info("Пользователь с ID = {} не существует.", user.getId());

            return Optional.empty();
        }

        log.info("Пользователь с ID = {} обновлен.", user.getId());

        return getById(user.getId());
    }

    @Override
    @Transactional
    public List<User> findAllFriendsById(Long id) {
        String sqlQueryGetFriends = "select USERS.user_id as id, " +
                "       email as email, " +
                "       login as login, " +
                "       user_name as name, " +
                "       birthday as birthday " +
                "from USERS " +
                "    inner join RELATIONSHIP_USERS RU on USERS.USER_ID = RU.FRIEND_ID " +
                "where RU.USER_ID = ?" +
                "order by id";

        List<User> users = jdbcTemplate.query(sqlQueryGetFriends, UserMapper::mapRowToUser, id);

        log.info("Список друзей пользователя с ID = {} получен.", id);

        return users;
    }

    @Override
    @Transactional
    public List<User> saveOneFriend(Long idUser, Long idFriend) {
        String sqlQueryAddFriend = "insert into RELATIONSHIP_USERS (user_id, friend_id)" +
                "values (?, ?)";

        jdbcTemplate.update(sqlQueryAddFriend, idUser, idFriend);

        log.info("Пользователю с ID = {} добавлен друг с ID = {}.", idUser, idFriend);

        return findAllFriendsById(idUser);
    }

    @Override
    @Transactional
    public List<User> deleteOneFriend(Long idUser, Long idFriend) {
        String sqlQueryRemoveFriend = "delete from RELATIONSHIP_USERS " +
                "where USER_ID = ? and FRIEND_ID = ?";

        jdbcTemplate.update(sqlQueryRemoveFriend, idUser, idFriend);

        log.info("Пользователю с ID = {} удален друг с ID = {}.", idUser, idFriend);

        return findAllFriendsById(idUser);
    }

    @Override
    @Transactional
    public Optional<Boolean> deleteUserById(long userId) {
        String sqlQueryDeleteUser = "delete from USERS where USER_ID = ?;";
        if (jdbcTemplate.update(sqlQueryDeleteUser, userId) == 0) {
            log.info("Пользователь с ID = {} не существует.", userId);

            return Optional.empty();
        }

        log.info("Пользователь с ID = {} удален.", userId);

        return Optional.of(true);
    }

    @Override
    @Transactional
    public List<Film> findRecommendationsFilms(Long userId) {
        String sqlQueryGetRecommendationsFilms = "with user_likes_CTE as (select FILM_ID, " +
                "                               USER_ID, " +
                "                               LIKE_RATE " +
                "                        from LIKES_FILMS " +
                "                        where USER_ID = " + userId + "), " +
                "     most_intersection_user_CTE as (select AL.USER_ID, " +
                "                                           count(*) as total " +
                "                                    from LIKES_FILMS AL " +
                "                                        inner join user_likes_CTE UL on UL.FILM_ID = AL.FILM_ID " +
                "                                            and NOT AL.USER_ID = UL.USER_ID " +
                "                                            and AL.LIKE_RATE = UL.LIKE_RATE " +
                "                                    group by AL.USER_ID " +
                "                                    order by total desc " +
                "                                    limit 1), " +
                "     another_user_films_CTE as (select FILM_ID, " +
                "                                    FL.USER_ID " +
                "                                from LIKES_FILMS FL " +
                "                                    inner join most_intersection_user_CTE MIU on FL.USER_ID = MIU.USER_ID), " +
                "     recommended_films_CTE as (select AUF.FILM_ID " +
                "                               from another_user_films_CTE AUF " +
                "                                   left join user_likes_CTE UL on UL.FILM_ID = AUF.FILM_ID " +
                "                               where UL.USER_ID IS NULL) " +
                "select F.FILM_ID        as id, " +
                "       FILM_NAME        as name, " +
                "       FILM_DESCRIPTION as description, " +
                "       RATING_NAME      as mpa, " +
                "       RELEASE_DATE     as releaseDate, " +
                "       DURATION         as duration, " +
                "       RATE             as rate " +
                "from FILMS F " +
                "         left join RATINGS R on R.RATING_ID = F.RATING_ID " +
                "         inner join recommended_films_CTE RFC on F.FILM_ID = RFC.FILM_ID " +
                "         left join FILMS_RATE FR on F.FILM_ID = FR.FILM_ID " +
                "where RATE > 5 " +
                "order by id";

        List<Film> films = jdbcTemplate.query(sqlQueryGetRecommendationsFilms, FilmMapper::mapRowToFilm);

        log.info("Пользователю с ID = {} получен список рекомендованных фильмов.", userId);

        if (!films.isEmpty()) {
            Map<Long, Film> mapFilms = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
            String sqlQueryGetAllGenres = "select FG.FILM_ID as filmId, " +
                    "       G2.GENRE_NAME as genreName, " +
                    "       G2.GENRE_ID as genreId " +
                    "from GENRES_FILMS FG " +
                    "    left join GENRES G2 on FG.GENRE_ID = G2.GENRE_ID " +
                    "where FG.FILM_ID IN ( " +
                    mapFilms.keySet()
                            .stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(",")) +
                    " ) " +
                    "order by genreId ";
            List<Map<String, Object>> genres = jdbcTemplate.queryForList(sqlQueryGetAllGenres);
            genres.forEach(t -> mapFilms.get(Long.parseLong(t.get("filmId").toString()))
                    .getGenres()
                    .add(Genre.valueOf(t.get("genreName").toString())
                    ));

            log.info("У списка фильмов записаны жанры.");

            String sqlQueryGetDirectors = "select FILM_ID as filmId, " +
                    "       D.DIRECTOR_ID as directorId, " +
                    "       DIRECTOR_NAME as directorName " +
                    "from DIRECTORS_FILMS " +
                    "    inner join DIRECTORS D on D.DIRECTOR_ID = DIRECTORS_FILMS.DIRECTOR_ID " +
                    "where FILM_ID IN (" +
                    mapFilms.keySet()
                            .stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(",")) +
                    " )" +
                    "order by directorId";

            List<Map<String, Object>> directorsFilms = jdbcTemplate.queryForList(sqlQueryGetDirectors);

            directorsFilms.forEach(t -> mapFilms.get(Long.parseLong(t.get("filmId").toString())).getDirectors().add(
                    new Director.Builder()
                            .id(Long.parseLong(t.get("directorId").toString()))
                            .name(t.get("directorName").toString())
                            .build()
            ));

            log.info("У списка фильмов записаны режиссеры.");
        }

        return films;
    }
}
