package com.studybuddy.collaboration_service.groups.dto.Response;

public record PresignUploadResponse(
        String uploadUrl,
        String finalUrl,
        long expiresInSeconds
) {}
