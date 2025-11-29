package com.studybuddy.collaboration_service.groups.repository;

import com.studybuddy.collaboration_service.groups.entities.GroupNote;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public class GroupNoteRepository {

    private final JdbcTemplate jdbc;

    public GroupNoteRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<GroupNote> mapper = (rs, rowNum) -> {
        GroupNote note = new GroupNote();
        note.setId(UUID.fromString(rs.getString("id")));
        note.setGroupId(UUID.fromString(rs.getString("group_id")));
        note.setUploadedBy(UUID.fromString(rs.getString("uploaded_by")));
        note.setTitle(rs.getString("title"));
        note.setFileUrl(rs.getString("file_url"));
        note.setFileType(rs.getString("file_type"));
        note.setFileSizeBytes(rs.getLong("file_size_bytes"));
        note.setCreatedAt(rs.getTimestamp("created_at").toInstant());
        return note;
    };

    public void save(GroupNote note) {
        String sql = """
                    INSERT INTO notes (id, group_id, uploaded_by, title, file_url, file_type, file_size_bytes, created_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbc.update(sql,
                note.getId().toString(),
                note.getGroupId().toString(),
                note.getUploadedBy().toString(),
                note.getTitle(),
                note.getFileUrl(),
                note.getFileType(),
                note.getFileSizeBytes(),
                Timestamp.from(note.getCreatedAt())
        );
    }

    public List<GroupNote> findByGroup(UUID groupId) {
        String sql = """
                    SELECT * FROM notes
                    WHERE group_id = ?
                    ORDER BY created_at DESC
                """;

        return jdbc.query(sql, mapper, groupId.toString());
    }
}
