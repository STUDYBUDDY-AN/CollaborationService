package com.studybuddy.collaboration_service.groups.dto.Request;

public record PresignUploadRequest(
        String fileName,
        String fileType
) {}

