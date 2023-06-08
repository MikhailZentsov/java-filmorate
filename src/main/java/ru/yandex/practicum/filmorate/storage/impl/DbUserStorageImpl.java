package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.mapper.Mapper;

import java.util.List;
import java.util.Optional;

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
    public Optional<List<User>> saveOneFriend(Long idUser, Long idFriend) {
        String sqlQueryAddFriend = "insert into RELATIONSHIP_USERS (user_id, friend_id)" +
                "values (?, ?)";
        jdbcTemplate.update(sqlQueryAddFriend, idUser, idFriend);
        return findAllFriendsById(idUser);
    }

    @Override
    public Optional<List<User>> deleteOneFriend(Long idUser, Long idFriend) {
        String sqlQueryRemoveFriend = "delete from RELATIONSHIP_USERS " +
                "where USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sqlQueryRemoveFriend, idUser, idFriend);
        return findAllFriendsById(idUser);
    }
    @Override
    public Optional<User> deleteUserById(long userId) {
        Optional<User> user = getById(userId);
        String sqlQueryDeleteUser = "delete from USERS where USER_ID = ?;";
        jdbcTemplate.update(sqlQueryDeleteUser, userId);

        return user;
    }
}
