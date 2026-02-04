package com.studybuddy.collaboration_service.groups.repository;

import com.studybuddy.collaboration_service.groups.entities.PomodoroSession;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public class PomodoroSessionRepository {

    private final JdbcTemplate jdbc;

    public PomodoroSessionRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<PomodoroSession> mapper = (rs, rowNum) -> {
        PomodoroSession s = new PomodoroSession();
        s.setId(UUID.fromString(rs.getString("id")));
        s.setUserId(UUID.fromString(rs.getString("user_id")));
        s.setGroupId(rs.getString("group_id") != null ? UUID.fromString(rs.getString("group_id")) : null);
        s.setStartedAt(rs.getTimestamp("started_at").toInstant());
        s.setEndedAt(rs.getTimestamp("ended_at").toInstant());
        s.setMinutes(rs.getInt("minutes"));
        return s;
    };

    public void save(PomodoroSession session) {
        String sql = """
            INSERT INTO pomodoro_sessions (id, user_id, group_id, started_at, ended_at, minutes)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        jdbc.update(sql,
                session.getId().toString(),
                session.getUserId().toString(),
                session.getGroupId() != null ? session.getGroupId().toString() : null,
                Timestamp.from(session.getStartedAt()),
                Timestamp.from(session.getEndedAt()),
                session.getMinutes()
        );
    }

    public List<PomodoroSession> findRecentByUser(UUID userId) {
        String sql = """
            SELECT * FROM pomodoro_sessions
            WHERE user_id = ?
            ORDER BY started_at DESC
            LIMIT 50
        """;

        return jdbc.query(sql, mapper, userId.toString());
    }
}
