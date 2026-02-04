package com.studybuddy.collaboration_service.groups.repository;

import com.studybuddy.collaboration_service.groups.entities.MessageAttachment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public class MessageAttachmentRepository {

    private final JdbcTemplate jdbc;

    public MessageAttachmentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<MessageAttachment> mapper = (rs, rowNum) -> {
        MessageAttachment att = new MessageAttachment();
        att.setId(UUID.fromString(rs.getString("id")));
        att.setMessageId(UUID.fromString(rs.getString("message_id")));
        att.setFileUrl(rs.getString("file_url"));
        att.setFileType(rs.getString("file_type"));
        att.setFileSizeBytes(rs.getLong("file_size_bytes"));
        Timestamp created = rs.getTimestamp("created_at");
        att.setCreatedAt(created != null ? created.toInstant() : null);
        return att;
    };

    public void save(MessageAttachment att) {
        String sql = """
            INSERT INTO message_attachments (id, message_id, file_url, file_type, file_size_bytes, created_at)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        jdbc.update(sql,
                att.getId().toString(),
                att.getMessageId().toString(),
                att.getFileUrl(),
                att.getFileType(),
                att.getFileSizeBytes(),
                att.getCreatedAt() != null ? Timestamp.from(att.getCreatedAt()) : null
        );
    }

    public List<MessageAttachment> findByMessageId(UUID messageId) {
        String sql = """
            SELECT * FROM message_attachments
            WHERE message_id = ?
        """;
        return jdbc.query(sql, mapper, messageId.toString());
    }
}
