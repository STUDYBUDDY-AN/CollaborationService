package com.studybuddy.collaboration_service.groups.controller;

import com.studybuddy.collaboration_service.groups.dto.Request.PresignUploadRequest;
import com.studybuddy.collaboration_service.groups.dto.Response.PresignUploadResponse;
import com.studybuddy.collaboration_service.groups.service.FileStorageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files")
@AllArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file
    ) throws Exception {

        String url = fileStorageService.upload(file);

        return ResponseEntity.ok(Map.of(
                "url", url,
                "type", file.getContentType(),
                "size", file.getSize()
        ));
    }

    @PostMapping("/presign/file")
    public ResponseEntity<PresignUploadResponse> presignUploadForUserFile(
            @RequestBody PresignUploadRequest request,
            @RequestHeader("X-User-Id") UUID userId
    ) throws Exception {

        PresignUploadResponse response =
                fileStorageService.createPresignedForUserFile(
                        userId,
                        request.fileName(),
                        request.fileType()
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/presign/attachment")
    public ResponseEntity<PresignUploadResponse> presignUploadForAttachment(
            @RequestBody PresignUploadRequest request,
            @RequestParam("group_id") UUID groupId,
            @RequestParam("message_id") UUID messageId
    ) throws Exception {

        PresignUploadResponse response =
                fileStorageService.createPresignedForAttachment(
                        groupId,
                        messageId,
                        request.fileName(),
                        request.fileType()
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/presign/note")
    public ResponseEntity<PresignUploadResponse> presignUploadForNote(
            @RequestBody PresignUploadRequest request,
            @RequestParam("group_id") UUID groupId,
            @RequestParam("message_id") UUID messageId
    ) throws Exception {

        PresignUploadResponse response =
                fileStorageService.createPresignedForAttachment(
                        groupId,
                        messageId,
                        request.fileName(),
                        request.fileType()
                );

        return ResponseEntity.ok(response);
    }
}
