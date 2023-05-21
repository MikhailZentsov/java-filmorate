package ru.yandex.practicum.filmorate.storage.impl.bd;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.impl.bd.mapper.Mapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        Map<Long, User> mapUsers = users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        String sqlQueryGetFriends = "select RU1.USER_ID   as userId,\n" +
                "       RU1.FRIEND_ID as friendId\n" +
                "from RELATIONSHIP_USERS RU1\n" +
                "order by userId, friendId";

        List<Map<String, Object>> friends = jdbcTemplate.queryForList(sqlQueryGetFriends);

        friends.forEach(
                t -> mapUsers.get(Long.parseLong(t.get("userId").toString())).getFriends().add(
                        Long.parseLong(t.get("friendId").toString())
                ));

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

        String sqlQueryGetFriends = "select RU1.FRIEND_ID as friendId\n" +
                "from RELATIONSHIP_USERS RU1\n" +
                "where RU1.USER_ID = ?\n" +
                "order by friendId";

        List<Long> friendsUser = jdbcTemplate.query(sqlQueryGetFriends,
                (rs, rowNum) -> rs.getLong(1),
                id
        );

        user.setFriends(new LinkedHashSet<>(friendsUser));

        return Optional.of(user);
    }

    @Override
    @Transactional
    public Optional<User> addUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");

        Long userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();

        String sqlQueryAddFriends = "insert into RELATIONSHIP_USERS (USER_ID, FRIEND_ID)\n" +
                "values (?, ?)";

        List<Long> friends = new ArrayList<>(user.getFriends());

        jdbcTemplate.batchUpdate(sqlQueryAddFriends, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, userId);
                ps.setLong(2, friends.get(i));
            }

            @Override
            public int getBatchSize() {
                return friends.size();
            }
        });

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

        String sqlQueryDeleteFriends = "delete\n" +
                "from RELATIONSHIP_USERS\n" +
                "where USER_ID = ?";

        jdbcTemplate.update(sqlQueryDeleteFriends, user.getId());

        String sqlQueryAddFriends = "insert into RELATIONSHIP_USERS (USER_ID, FRIEND_ID)\n" +
                "values (?, ?)";

        List<Long> friends = new ArrayList<>(user.getFriends());

        jdbcTemplate.batchUpdate(sqlQueryAddFriends, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, user.getId());
                ps.setLong(2, friends.get(i));
            }

            @Override
            public int getBatchSize() {
                return friends.size();
            }
        });

        return getUser(user.getId());
    }
}
