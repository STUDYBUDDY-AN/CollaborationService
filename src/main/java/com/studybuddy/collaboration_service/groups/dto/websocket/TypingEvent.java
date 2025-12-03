package com.studybuddy.collaboration_service.groups.dto.websocket;

import java.util.UUID;

public record TypingEvent(
        UUID groupId,
        UUID userId,
        boolean typing
) {}
