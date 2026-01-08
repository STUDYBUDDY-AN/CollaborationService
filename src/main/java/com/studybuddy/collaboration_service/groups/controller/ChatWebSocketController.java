package com.studybuddy.collaboration_service.groups.controller;

import com.studybuddy.collaboration_service.groups.dto.websocket.*;
import com.studybuddy.collaboration_service.groups.dto.websocket.request.DeleteMessageWsRequest;
import com.studybuddy.collaboration_service.groups.dto.websocket.request.EditMessageWsRequest;
import com.studybuddy.collaboration_service.groups.dto.websocket.request.SendMessageWsRequest;
import com.studybuddy.collaboration_service.groups.entities.GroupMessage;
import com.studybuddy.collaboration_service.groups.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class ChatWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    // 1) SEND MESSAGE
    @MessageMapping("/chat.send.{groupId}")
    public void sendMessage(
            @DestinationVariable UUID groupId,
            SendMessageWsRequest payload,
            @Header("X-User-Id") String userIdString
    ) {
        UUID senderId = UUID.fromString(userIdString);

        // Broadcast to all subscribers of this group
        ChatMessageEvent event;
        if (payload.attachmentUrl() != null) {
            GroupMessage saved = messageService.sendAndReturn(groupId, senderId, payload.content(), payload.attachmentUrl(), payload.attachmentType(), payload.attachmentSize());
            event = new ChatMessageEvent(
                    saved.getId(),
                    saved.getGroupId(),
                    saved.getSenderId(),
                    saved.getContent(),
                    saved.getSentAt(),
                    payload.attachmentUrl(),
                    payload.attachmentType(),
                    payload.attachmentSize()
            );
        }
        else {
            GroupMessage saved = messageService.sendAndReturn(groupId, senderId, payload.content());
            event = new ChatMessageEvent(
                    saved.getId(),
                    saved.getGroupId(),
                    saved.getSenderId(),
                    saved.getContent(),
                    saved.getSentAt()
            );
        }

        ChatEvent wrapper = new ChatEvent(
                "MESSAGE_CREATED",
                groupId,
                event
        );

        messagingTemplate.convertAndSend(
                "/topic/groups/" + groupId + "/events",
                wrapper
        );

    }

    // 2) TYPING INDICATOR
    @MessageMapping("/chat.typing.{groupId}")
    public void typing(
            @DestinationVariable UUID groupId,
            TypingEvent payload,
            @Header("X-User-Id") String userIdString
    ) {
        UUID userId = UUID.fromString(userIdString);

        TypingEvent event = new TypingEvent(
                groupId,
                userId,
                payload.typing()
        );

        ChatEvent wrapper = new ChatEvent(
                "TYPING",
                groupId,
                event
        );

        messagingTemplate.convertAndSend(
                "/topic/groups/" + groupId + "/events",
                wrapper
        );
    }

    // 3) EDIT MESSAGE
    @MessageMapping("/chat.edit.{groupId}")
    public void editMessage(
            @DestinationVariable UUID groupId,
            EditMessageWsRequest payload,
            @Header("X-User-Id") String userIdString
    ) {
        UUID userId = UUID.fromString(userIdString);

        GroupMessage message = messageService.editMessage(groupId, payload.messageId(), userId, payload.content());

        MessageUpdatedEvent event = new MessageUpdatedEvent(
                message.getId(),
                message.getContent(),
                message.getEditedAt()
        );

        ChatEvent wrapper = new ChatEvent(
                "MESSAGE_UPDATED",
                groupId,
                event
        );

        messagingTemplate.convertAndSend(
                "/topic/groups/" + groupId + "/events",
                wrapper
        );
    }

    // 4) DELETE MESSAGE
    @MessageMapping("/chat.delete.{groupId}")
    public void deleteMessage(
            @DestinationVariable UUID groupId,
            DeleteMessageWsRequest payload,
            @Header("X-User-Id") String userIdString
    ) {
        UUID userId = UUID.fromString(userIdString);

        messageService.softDelete(groupId, payload.messageId(), userId);

        MessageDeletedEvent event = new MessageDeletedEvent(
                payload.messageId(),
                Instant.now(),
                userId
        );

        ChatEvent wrapper = new ChatEvent(
                "MESSAGE_DELETED",
                groupId,
                event
        );

        messagingTemplate.convertAndSend(
                "/topic/groups/" + groupId + "/events",
                wrapper
        );
    }
}