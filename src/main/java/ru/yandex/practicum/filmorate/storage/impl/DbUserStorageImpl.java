package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.mapper.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class DbUserStorageImpl implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public DbUserStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public Optional<List<User>> findAll() {
        String sqlQueryGetUsers = "select USER_ID as id, " +
                "       USER_NAME as name, " +
                "       EMAIL as email, " +
                "       LOGIN as login, " +
                "       BIRTHDAY as birthday " +
                "from USERS " +
                "order by USER_ID";

        List<User> users = jdbcTemplate.query(sqlQueryGetUsers, Mapper::mapRowToUser);

        return Optional.of(users);
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
            user = jdbcTemplate.queryForObject(sqlQueryGetUser, Mapper::mapRowToUser, id);
        } catch (DataAccessException e) {
            return Optional.empty();
        }

        assert user != null;

        return Optional.of(user);
    }

    @Override
    @Transactional
    public Optional<User> saveOne(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");

        Long userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();

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
            return Optional.empty();
        }

        return getById(user.getId());
    }

    @Override
    @Transactional
    public Optional<List<User>> findAllFriendsById(Long id) {
        String sqlQueryGetFriends = "select USERS.user_id as id, " +
                "       email as email, " +
                "       login as login, " +
                "       user_name as name, " +
                "       birthday as birthday " +
                "from USERS " +
                "    inner join RELATIONSHIP_USERS RU on USERS.USER_ID = RU.FRIEND_ID " +
                "where RU.USER_ID = ?" +
                "order by id";

        return Optional.of(jdbcTemplate.query(sqlQueryGetFriends, Mapper::mapRowToUser, id));
    }

    @Override
    @Transactional
    public Optional<List<User>> saveOneFriend(Long idUser, Long idFriend) {
        String sqlQueryAddFriend = "insert into RELATIONSHIP_USERS (user_id, friend_id)" +
                "values (?, ?)";
        jdbcTemplate.update(sqlQueryAddFriend, idUser, idFriend);
        return findAllFriendsById(idUser);
    }

    @Override
    @Transactional
    public Optional<List<User>> deleteOneFriend(Long idUser, Long idFriend) {
        String sqlQueryRemoveFriend = "delete from RELATIONSHIP_USERS " +
                "where USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sqlQueryRemoveFriend, idUser, idFriend);
        return findAllFriendsById(idUser);
    }

    @Override
    @Transactional
    public List<Film> findRecommendationsFilms(Long userId) {
        String sqlQueryGetRecommendationsFilms = "with user_likes_CTE as (select FILM_ID,\n" +
                "                               USER_ID\n" +
                "                        from LIKES_FILMS\n" +
                "                        where USER_ID = " + userId + "),\n" +
                "     most_intersection_user_CTE as (select AL.USER_ID,\n" +
                "                                           count(*) as total\n" +
                "                                    from LIKES_FILMS AL\n" +
                "                                             inner join user_likes_CTE UL on UL.FILM_ID = AL.FILM_ID\n" +
                "                                                    and NOT AL.USER_ID = UL.USER_ID\n" +
                "                                    group by AL.USER_ID\n" +
                "                                    order by total desc\n" +
                "                                    limit 1),\n" +
                "     another_user_films_CTE as (select FILM_ID,\n" +
                "                                       FL.USER_ID\n" +
                "                                from LIKES_FILMS FL\n" +
                "                                         inner join most_intersection_user_CTE MIU on FL.USER_ID = MIU.USER_ID),\n" +
                "     recommended_films_CTE as (select AUF.FILM_ID\n" +
                "                               from another_user_films_CTE AUF\n" +
                "                                        left join user_likes_CTE UL on UL.FILM_ID = AUF.FILM_ID\n" +
                "                               where UL.USER_ID IS NULL)\n" +
                "select F.FILM_ID        as id,\n" +
                "       FILM_NAME        as name,\n" +
                "       FILM_DESCRIPTION as description,\n" +
                "       RATING_NAME      as mpa,\n" +
                "       RELEASE_DATE     as releaseDate,\n" +
                "       DURATION         as duration\n" +
                "from FILMS F\n" +
                "         left join RATINGS R on R.RATING_ID = F.RATING_ID\n" +
                "         inner join recommended_films_CTE RFC on F.FILM_ID = RFC.FILM_ID\n" +
                "order by id";

        List<Film> films = jdbcTemplate.query(sqlQueryGetRecommendationsFilms, Mapper::mapRowToFilm);

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
}
