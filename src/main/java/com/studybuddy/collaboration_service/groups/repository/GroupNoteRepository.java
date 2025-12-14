package com.studybuddy.collaboration_service.groups.repository;

import com.studybuddy.collaboration_service.groups.entities.GroupNote;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
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

        Timestamp created = rs.getTimestamp("created_at");
        note.setCreatedAt(created != null ? created.toInstant() : null);

        note.setPreviewAvailable(rs.getBoolean("preview_available"));
        note.setPreviewType(rs.getString("preview_type"));

        return note;
    };


    public void save(GroupNote note) {
        String sql = """
        INSERT INTO notes (
            id,
            group_id,
            uploaded_by,
            title,
            file_url,
            file_type,
            file_size_bytes,
            preview_available,
            preview_type,
            created_at
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        jdbc.update(sql,
                note.getId().toString(),
                note.getGroupId().toString(),
                note.getUploadedBy().toString(),
                note.getTitle(),
                note.getFileUrl(),
                note.getFileType(),
                note.getFileSizeBytes(),
                note.isPreviewAvailable(),
                note.getPreviewType(),
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

    public List<GroupNote> searchByTitle(UUID groupId, String query) {
        String sql = """
        SELECT *
        FROM notes
        WHERE group_id = ?
          AND LOWER(title) LIKE ?
        ORDER BY created_at DESC
    """;

        return jdbc.query(
                sql,
                mapper,
                groupId.toString(),
                "%" + query.toLowerCase() + "%"
        );
    }

    public List<GroupNote> filterByFileType(UUID groupId, String fileType) {
        String sql = """
        SELECT *
        FROM notes
        WHERE group_id = ?
          AND file_type = ?
        ORDER BY created_at DESC
    """;

        return jdbc.query(sql, mapper, groupId.toString(), fileType);
    }

    public List<GroupNote> filterByUploader(UUID groupId, UUID uploaderId) {
        String sql = """
        SELECT *
        FROM notes
        WHERE group_id = ?
          AND uploaded_by = ?
        ORDER BY created_at DESC
    """;

        return jdbc.query(sql, mapper,
                groupId.toString(),
                uploaderId.toString()
        );
    }

    public List<GroupNote> search(
            UUID groupId,
            String query,
            String fileType,
            UUID uploadedBy,
            int limit,
            int offset
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT *
        FROM notes
        WHERE group_id = ?
    """);

        List<Object> params = new ArrayList<>();
        params.add(groupId.toString());

        if (query != null && !query.isBlank()) {
            sql.append(" AND LOWER(title) LIKE ?");
            params.add("%" + query.toLowerCase() + "%");
        }

        if (fileType != null) {
            sql.append(" AND file_type = ?");
            params.add(fileType);
        }

        if (uploadedBy != null) {
            sql.append(" AND uploaded_by = ?");
            params.add(uploadedBy.toString());
        }

        sql.append(" ORDER BY created_at DESC");
        sql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        return jdbc.query(sql.toString(), mapper, params.toArray());
    }


}
