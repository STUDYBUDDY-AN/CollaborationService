package com.studybuddy.collaboration_service.groups.repository;

import com.studybuddy.collaboration_service.groups.entities.GroupMember;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public class GroupMemberRepository {

    private final JdbcTemplate jdbc;

    public GroupMemberRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void addMember(GroupMember member) {
        String sql = """
            INSERT IGNORE INTO group_members (group_id, user_id)
            VALUES (?, ?)
        """;

        jdbc.update(sql,
                member.getGroupId().toString(),
                member.getUserId().toString()
        );
    }

    public boolean exists(UUID groupId, UUID userId) {
        String sql = """
            SELECT COUNT(*) FROM group_members
            WHERE group_id = ? AND user_id = ?
        """;

        Integer count = jdbc.queryForObject(sql, Integer.class, groupId.toString(), userId.toString());
        return count != null && count > 0;
    }

    public List<GroupMember> findMembers(UUID groupId) {
        String sql = "SELECT * FROM group_members WHERE group_id = ?";

        return jdbc.query(sql, (rs, rowNum) -> {
            GroupMember member = new GroupMember();
            member.setGroupId(UUID.fromString(rs.getString("group_id")));
            member.setUserId(UUID.fromString(rs.getString("user_id")));
            member.setJoinedAt(rs.getTimestamp("joined_at").toInstant());
            return member;
        }, groupId.toString());
    }

    public List<GroupMember> findAllGroupsByUserId(UUID userId) {
        String sql = "SELECT * FROM group_members WHERE user_id = ?";
        return jdbc.query(sql, (rs, rowNum) -> {
            GroupMember member = new GroupMember();
            member.setGroupId(UUID.fromString(rs.getString("group_id")));
            member.setUserId(UUID.fromString(rs.getString("user_id")));
            member.setJoinedAt(rs.getTimestamp("joined_at").toInstant());
            return member;
        }, userId.toString());
    }
}
