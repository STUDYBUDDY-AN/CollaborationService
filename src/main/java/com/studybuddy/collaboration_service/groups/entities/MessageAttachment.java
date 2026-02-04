package com.studybuddy.collaboration_service.groups.entities;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class MessageAttachment {
    private UUID id;
    private UUID messageId;
    private String fileUrl;
    private String fileType;
    private long fileSizeBytes;
    private Instant createdAt;
}
