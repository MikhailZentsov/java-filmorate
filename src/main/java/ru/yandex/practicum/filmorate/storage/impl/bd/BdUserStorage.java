package ru.yandex.practicum.filmorate.storage.impl.bd;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.impl.bd.mapper.Mapper;

import java.util.List;
import java.util.Optional;

@Repository("BdUserStorage")
public class BdUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public BdUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public Optional<List<User>> getUsers() {
        String sqlQueryGetUsers = "select USER_ID as id,\n" +
                "       USER_NAME as name,\n" +
                "       EMAIL as email,\n" +
                "       LOGIN as login,\n" +
                "       BIRTHDAY as birthday\n" +
                "from USERS\n" +
                "order by USER_ID";

        List<User> users = jdbcTemplate.query(sqlQueryGetUsers, Mapper::mapRowToUser);

        return Optional.of(users);
    }

    @Override
    @Transactional
    public Optional<User> getUser(Long id) {
        String sqlQueryGetUser = "select USER_ID as id,\n" +
                "       USER_NAME as name,\n" +
                "       EMAIL as email,\n" +
                "       LOGIN as login,\n" +
                "       BIRTHDAY as birthday\n" +
                "from USERS\n" +
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
    public Optional<User> addUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");

        Long userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();

        return getUser(userId);
    }

    @Override
    @Transactional
    public Optional<User> updateUser(User user) {
        String sqlQueryUpdateUser = "update USERS\n" +
                "set EMAIL = ?,\n" +
                "    LOGIN = ?,\n" +
                "    USER_NAME = ?,\n" +
                "    BIRTHDAY = ?\n" +
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

        return getUser(user.getId());
    }

    @Override
    public Optional<List<User>> getFriends(Long id) {
        String sqlQueryGetFriends = "select USERS.user_id as id,\n" +
                "       email as email,\n" +
                "       login as login,\n" +
                "       user_name as name,\n" +
                "       birthday as birthday\n" +
                "from USERS\n" +
                "    inner join RELATIONSHIP_USERS RU on USERS.USER_ID = RU.FRIEND_ID\n" +
                "where RU.USER_ID = ?" +
                "order by id";

        return Optional.of(jdbcTemplate.query(sqlQueryGetFriends, Mapper::mapRowToUser, id));
    }

    @Override
    public Optional<List<User>> addFriend(Long idUser, Long idFriend) {
        String sqlQueryAddFriend = "insert into RELATIONSHIP_USERS (user_id, friend_id)" +
                "values (?, ?)";
        jdbcTemplate.update(sqlQueryAddFriend, idUser, idFriend);
        return getFriends(idUser);
    }

    @Override
    public Optional<List<User>> removeFriend(Long idUser, Long idFriend) {
        String sqlQueryRemoveFriend = "delete from RELATIONSHIP_USERS " +
                "where USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sqlQueryRemoveFriend, idUser, idFriend);
        return getFriends(idUser);
    }
}
