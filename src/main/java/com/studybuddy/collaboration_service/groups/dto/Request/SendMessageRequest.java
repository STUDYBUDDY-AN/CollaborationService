package com.studybuddy.collaboration_service.groups.dto.Request;

public record SendMessageRequest(
        String content,
        String attachmentUrl,
        String attachmentType,
        Long attachmentSize
) {}


