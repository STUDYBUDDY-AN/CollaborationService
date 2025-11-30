package com.studybuddy.collaboration_service.groups.controller;

import com.studybuddy.collaboration_service.groups.dto.Request.SendMessageRequest;
import com.studybuddy.collaboration_service.groups.dto.Response.MessageResponse;
import com.studybuddy.collaboration_service.groups.entities.GroupMessage;
import com.studybuddy.collaboration_service.groups.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/groups/{groupId}/messages")
@AllArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(
            @PathVariable UUID groupId,
            @RequestBody SendMessageRequest request,
            Authentication authentication
    ) {
        UUID senderId = UUID.fromString(authentication.getName());
        UUID messageId = messageService.sendMessage(groupId, senderId, request.content());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("messageId", messageId));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<MessageResponse>> getRecentMessages(
            @PathVariable UUID groupId,
            @RequestParam(defaultValue = "50") int limit
    ) {
        List<MessageResponse> response = messageService
                .getRecentMessages(groupId, limit)
                .stream()
                .map(message -> new MessageResponse(
                        message.getId(),
                        message.getSenderId(),
                        message.getContent(),
                        message.getSentAt()
                )).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/after")
    public ResponseEntity<List<MessageResponse>> getMessagesAfter(
            @PathVariable UUID groupId,
            @RequestParam Instant after,
            @RequestParam(defaultValue = "50") int limit
    ) {
        List<MessageResponse> response = messageService
                .getMessagesAfter(groupId, after, limit)
                .stream()
                .map(message -> new MessageResponse(
                        message.getId(),
                        message.getSenderId(),
                        message.getContent(),
                        message.getSentAt()
                )).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/fetch")
    public ResponseEntity<List<MessageResponse>> fetchMessages(
            @PathVariable UUID groupId,
            @RequestParam Instant since
    ) {
        List<MessageResponse> response = messageService
                .fetchMessages(groupId, since)
                .stream()
                .map(message -> new MessageResponse(
                        message.getId(),
                        message.getSenderId(),
                        message.getContent(),
                        message.getSentAt()
                )).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/before")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @PathVariable UUID groupId,
            @RequestParam String before,
            @RequestParam(defaultValue = "50") int limit
    ) {
        List<GroupMessage> messages;

        if (before != null) {
            Instant beforeInstant = Instant.parse(before);
            messages = messageService.getMessagesBefore(groupId, beforeInstant, limit);
        } else {
            messages = messageService.getRecentMessages(groupId, limit);
        }

        List<MessageResponse> response = messages.stream()
                .map(message -> new MessageResponse(
                        message.getId(),
                        message.getSenderId(),
                        message.getContent(),
                        message.getSentAt()
                )).toList();

        return ResponseEntity.ok(response);
    }

}

