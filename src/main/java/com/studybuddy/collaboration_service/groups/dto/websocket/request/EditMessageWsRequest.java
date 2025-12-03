package com.studybuddy.collaboration_service.groups.dto.websocket.request;

import java.util.UUID;

public record EditMessageWsRequest(
        UUID messageId,
        String content
) {
}
