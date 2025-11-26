package com.studybuddy.collaboration_service.groups.entities;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class GroupMember {
    private UUID groupId;
    private UUID userId;
    private Instant joinedAt;
}

