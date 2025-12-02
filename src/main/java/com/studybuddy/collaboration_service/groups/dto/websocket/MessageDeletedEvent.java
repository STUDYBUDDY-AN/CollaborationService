package com.studybuddy.collaboration_service.groups.dto.websocket;

import java.time.Instant;
import java.util.UUID;

public record MessageDeletedEvent(
        UUID id,
        Instant deletedAt,
        UUID deletedBy
) {}