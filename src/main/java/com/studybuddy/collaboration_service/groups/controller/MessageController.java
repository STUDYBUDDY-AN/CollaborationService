package com.studybuddy.collaboration_service.groups.controller;

import com.studybuddy.collaboration_service.groups.dto.Request.SendMessageRequest;
import com.studybuddy.collaboration_service.groups.dto.Response.MessageResponse;
import com.studybuddy.collaboration_service.groups.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/groups/{groupId}/messages")
@AllArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<List<MessageResponse>> getMessages(
            @PathVariable UUID groupId,
            @RequestParam(required = false) String before,
            @RequestParam(required = false) String after,
            @RequestParam(defaultValue = "50") int limit
    ) {
        if (before != null) {
            return ResponseEntity.ok(
                    messageService.getMessagesBeforeWithAttachments(groupId, Instant.parse(before), limit)
            );
        }
        else if (after != null) {
            return ResponseEntity.ok(
                    messageService.getMessagesAfterWithAttachments(groupId, Instant.parse(after), limit)
            );
        }
        else {
            return ResponseEntity.ok(
                    messageService.getRecentMessagesWithAttachments(groupId, limit)
            );
        }
    }

    @PostMapping
    public ResponseEntity<?> sendMessage(
            @PathVariable UUID groupId,
            @RequestBody SendMessageRequest request,
            @RequestHeader("X-User-Id") UUID senderId
    ) {
        UUID messageId = messageService.sendMessageWithAttachment(
                groupId,
                senderId,
                request.content(),
                request.attachmentUrl(),
                request.attachmentType(),
                request.attachmentSize()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("messageId", messageId));
    }

    @GetMapping("/since")
    public ResponseEntity<List<MessageResponse>> fetchMessages(
            @PathVariable UUID groupId,
            @RequestParam Instant since
    ) {
        return ResponseEntity.ok(
                messageService.fetchMessagesWithAttachments(groupId, since)
        );
    }
}