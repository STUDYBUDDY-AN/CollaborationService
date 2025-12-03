package com.studybuddy.collaboration_service.groups.dto.websocket;

import java.util.UUID;

public record ChatEvent(
        String type,   // e.g. "MESSAGE_CREATED", "MESSAGE_UPDATED", "MESSAGE_DELETED", "TYPING"
        UUID groupId,
        Object payload // will be another DTO like ChatMessageEvent, MessageUpdatedEvent, etc.
) {}