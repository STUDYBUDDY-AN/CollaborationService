package com.studybuddy.collaboration_service.groups.dto.Request;

public record GroupNoteRequest(
        String title,
        String fileUrl,
        String fileType,
        long fileSizeBytes) { }


