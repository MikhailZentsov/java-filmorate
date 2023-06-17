package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.util.List;
import java.util.Optional;

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
}
