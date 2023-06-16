package ru.yandex.practicum.filmorate.storage.trigger;

import lombok.extern.slf4j.Slf4j;
import org.h2.api.Trigger;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Slf4j
@Transactional
public class CalcRateTrigger implements Trigger {
    @Override
    public void init(Connection connection, String s, String s1, String s2, boolean b, int i) throws SQLException {
        Trigger.super.init(connection, s, s1, s2, b, i);
    }

    @Override
    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "merge into FILMS_RATE as target " +
                        "    using (select FILM_ID, sum(LIKE_RATE) / count(USER_ID) as RATE " +
                        "           from LIKES_FILMS " +
                        "           where FILM_ID = ? " +
                        "           group by FILM_ID) as source " +
                        "on (source.FILM_ID = target.FILM_ID) " +
                        "when matched then " +
                        "    update " +
                        "    set target.RATE = source.RATE " +
                        "when not matched then " +
                        "    insert " +
                        "    values (source.FILM_ID, source.RATE)")
        ) {
            stmt.setObject(1, newRow[0]);

            stmt.executeUpdate();
        }
    }

    @Override
    public void close() throws SQLException {
        Trigger.super.close();
    }

    @Override
    public void remove() throws SQLException {
        Trigger.super.remove();
    }
}
