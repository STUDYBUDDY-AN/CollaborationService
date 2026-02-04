package com.studybuddy.collaboration_service.groups.dto.Response;

import java.time.Instant;
import java.util.UUID;

public record AttachmentResponse(
        UUID id,
        String fileUrl,
        String fileType,
        long fileSizeBytes,
        Instant createdAt
) {}
