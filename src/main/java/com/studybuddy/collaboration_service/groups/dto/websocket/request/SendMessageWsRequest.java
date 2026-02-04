package com.studybuddy.collaboration_service.groups.dto.websocket.request;

public record SendMessageWsRequest(
        String content,
        String attachmentUrl,
        String attachmentType,
        Long attachmentSize
) {}
