package com.studybuddy.collaboration_service.groups.repository;
import com.studybuddy.collaboration_service.groups.entities.GroupMessage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class GroupMessageRepository {

    private final JdbcTemplate jdbc;

    public GroupMessageRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<GroupMessage> mapper = (rs, rowNum) -> {
        GroupMessage message = new GroupMessage();
            message.setId(UUID.fromString(rs.getString("id")));
            message.setGroupId(UUID.fromString(rs.getString("group_id")));
            message.setSenderId(UUID.fromString(rs.getString("sender_id")));
            message.setContent(rs.getString("content"));
            message.setSentAt(rs.getTimestamp("sent_at").toInstant());
        return message;
    };

    public void save(GroupMessage msg) {
        String sql = """
            INSERT INTO group_messages (id, group_id, sender_id, content, sent_at)
            VALUES (?, ?, ?, ?, ?)
        """;

        jdbc.update(sql,
                msg.getId().toString(),
                msg.getGroupId().toString(),
                msg.getSenderId().toString(),
                msg.getContent(),
                Timestamp.from(msg.getSentAt())
        );
    }

    public List<GroupMessage> getRecentMessages(UUID groupId, int limit) {
        String sql = """
            SELECT *
            FROM group_messages
            WHERE group_id = ?
            ORDER BY sent_at DESC
            LIMIT ?
        """;
        return jdbc.query(sql, mapper, groupId.toString(), limit);
    }

    public List<GroupMessage> getMessagesAfter(UUID groupId, Instant after, int limit) {
        String sql = """
            SELECT *
            FROM group_messages
            WHERE group_id = ? AND sent_at > ?
            ORDER BY sent_at ASC
            LIMIT ?
        """;
        return jdbc.query(sql, mapper,
                groupId.toString(),
                Timestamp.from(after),
                limit
        );
    }

    public List<GroupMessage> fetchMessages(UUID groupId, Instant since) {
        String sql = """
            SELECT * FROM group_messages
            WHERE group_id = ? AND sent_at > ?
            ORDER BY sent_at ASC
            LIMIT 200
        """;

        return jdbc.query(sql, mapper, groupId.toString(), Timestamp.from(since));
    }

    public List<GroupMessage> getMessagesBefore(UUID groupId, Instant before, int limit) {
        String sql = """
            SELECT *
            FROM group_messages
            WHERE group_id = ? AND sent_at < ?
            ORDER BY sent_at DESC
            LIMIT ?
        """;

        return jdbc.query(
                sql,
                mapper,
                groupId.toString(),
                Timestamp.from(before),
                limit
        );
    }

    public Optional<GroupMessage> findById(UUID messageId){
        String sql = """
                SELECT * FROM group_messages
                WHERE id = ?
            """;

        return jdbc.query(sql, mapper, messageId.toString()).stream().findFirst();
    }
}