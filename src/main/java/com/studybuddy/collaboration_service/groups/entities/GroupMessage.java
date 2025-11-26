package com.studybuddy.collaboration_service.groups.entities;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class GroupMessage {
    private UUID id;
    private UUID groupId;
    private UUID senderId;
    private String content;
    private Instant sentAt;
}

