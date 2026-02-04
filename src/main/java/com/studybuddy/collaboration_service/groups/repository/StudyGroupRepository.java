package com.studybuddy.collaboration_service.groups.repository;

import com.studybuddy.collaboration_service.groups.entities.StudyGroup;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class StudyGroupRepository {

    private final JdbcTemplate jdbc;

    public StudyGroupRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<StudyGroup> mapper = (rs, rowNum) -> {
        StudyGroup studyGroup = new StudyGroup();
            Timestamp created = rs.getTimestamp("created_at");
            Timestamp updated = rs.getTimestamp("updated_at");

            studyGroup.setId(UUID.fromString(rs.getString("id")));
            studyGroup.setName(rs.getString("name"));
            studyGroup.setDescription(rs.getString("description"));
            studyGroup.setOwnerId(UUID.fromString(rs.getString("owner_id")));
            studyGroup.setCreatedAt(created != null ? created.toInstant() : null);
            studyGroup.setUpdatedAt(updated != null ? updated.toInstant() : null);
        return studyGroup;
    };

    public void save(StudyGroup group) {
        String sql = """
            INSERT INTO study_groups (id, name, description, owner_id)
            VALUES (?, ?, ?, ?)
        """;

        jdbc.update(sql,
                group.getId().toString(),
                group.getName(),
                group.getDescription(),
                group.getOwnerId().toString()
        );
    }

    public Optional<StudyGroup> findById(UUID id) {
        String sql = "SELECT * FROM study_groups WHERE id = ?";
        return jdbc.query(sql, mapper, id.toString()).stream().findFirst();
    }

    public List<StudyGroup> findByOwner(UUID ownerId) {
        String sql = "SELECT * FROM study_groups WHERE owner_id = ?";
        return jdbc.query(sql, mapper, ownerId.toString());
    }

    public List<StudyGroup> findByUserId(UUID userId) {
        String sql = """
            SELECT g.*
            FROM study_groups g
            JOIN group_members m ON g.id = m.group_id
            WHERE m.user_id = ?
        """;

        return jdbc.query(sql, mapper, userId.toString());
    }


    public List<StudyGroup> findAll(){
        String sql = "SELECT * FROM study_groups";
        return jdbc.query(sql, mapper);
    }

}
