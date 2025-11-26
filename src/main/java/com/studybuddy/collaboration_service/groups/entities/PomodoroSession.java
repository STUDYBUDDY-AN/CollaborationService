package com.studybuddy.collaboration_service.groups.entities;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class PomodoroSession {
    private UUID id;
    private UUID userId;
    private UUID groupId;
    private Instant startedAt;
    private Instant endedAt;
    private int minutes;
}

