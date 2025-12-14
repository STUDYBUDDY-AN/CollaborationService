package com.studybuddy.collaboration_service.groups.controller;

import com.studybuddy.collaboration_service.groups.dto.Request.GroupNoteRequest;
import com.studybuddy.collaboration_service.groups.entities.GroupNote;
import com.studybuddy.collaboration_service.groups.service.GroupNoteService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/groups/{groupId}/notes")
@AllArgsConstructor
public class GroupNoteController {

    private final GroupNoteService groupNoteService;

    @PostMapping
    public ResponseEntity<?> uploadNote(
            @PathVariable UUID groupId,
            @RequestBody GroupNoteRequest request,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());

        UUID id = groupNoteService.saveNote(
                groupId,
                userId,
                request.title(),
                request.fileUrl(),
                request.fileType(),
                request.fileSizeBytes()
        );

        return ResponseEntity.status(201).body(Map.of("noteId", id));
    }

    @GetMapping
    public List<GroupNote> getNotes(@PathVariable UUID groupId) {
        return groupNoteService.getNotes(groupId);
    }

    @GetMapping
    public ResponseEntity<List<GroupNote>> getNotes(
            @PathVariable UUID groupId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String fileType,
            @RequestParam(required = false) UUID uploadedBy
    ) {
        return ResponseEntity.ok(
                groupNoteService.searchNotes(groupId, q, fileType, uploadedBy)
        );
    }

}
