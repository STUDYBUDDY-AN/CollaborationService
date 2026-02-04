package com.studybuddy.collaboration_service.groups.entities;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class GroupNote {
    private UUID id;
    private UUID groupId;
    private UUID uploadedBy;
    private String title;
    private String fileUrl;
    private String fileType;
    private long fileSizeBytes;
    private Instant createdAt;
    private boolean previewAvailable;
    private String previewType;
}

