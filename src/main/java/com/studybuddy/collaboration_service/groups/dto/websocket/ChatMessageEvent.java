package com.studybuddy.collaboration_service.groups.dto.websocket;

import java.time.Instant;
import java.util.UUID;

public class ChatMessageEvent{
    private UUID id;
    private UUID groupId;
    private UUID senderId;
    private String content;
    private Instant sentAt;
    private String attachmentUrl;
    private String attachmentType;
    private Long attachmentSize;

    public ChatMessageEvent() {
    }

    public ChatMessageEvent(UUID id, UUID groupId, UUID senderId, String content, Instant sentAt){
        this.id = id;
        this.groupId = groupId;
        this.senderId = senderId;
        this.content = content;
        this.sentAt = sentAt;
    }

    public ChatMessageEvent(UUID id, UUID groupId, UUID senderId, String content, Instant sentAt, String attachmentUrl, String attachmentType, Long attachmentSize) {
        this.id = id;
        this.groupId = groupId;
        this.senderId = senderId;
        this.content = content;
        this.sentAt = sentAt;
        this.attachmentUrl = attachmentUrl;
        this.attachmentType = attachmentType;
        this.attachmentSize = attachmentSize;
    }
}

