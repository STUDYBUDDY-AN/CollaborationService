package com.studybuddy.collaboration_service.groups.service;

import com.studybuddy.collaboration_service.groups.dto.Response.PresignUploadResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@AllArgsConstructor
public class FileStorageService {

    private final MinioClient minioClient;
    private final String bucket = "studybuddy-attachments";

    public String upload(MultipartFile file) throws Exception {

        String objectName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        return "http://localhost:9000/" + bucket + "/" + objectName;
    }

    public PresignUploadResponse createPresigned(String fileName, String fileType) throws Exception {

        String objectName = UUID.randomUUID() + "-" + fileName;

        String presignedUrl = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.PUT)
                        .bucket(bucket)
                        .object(objectName)
                        .expiry(60 * 60) // 1 hour
                        .build()
        );

        String finalUrl = "http://localhost:9000/" + bucket + "/" + objectName;

        return new PresignUploadResponse(
                presignedUrl,
                finalUrl,
                3600
        );
    }

    public PresignUploadResponse createPresignedForAttachment(UUID groupId, UUID messageId, String fileName, String fileType) throws Exception {
        String objectName = "attachments/" + groupId + "/" + messageId + "/" + UUID.randomUUID() + "-" + fileName;
        return createPresigned(objectName, fileType);
    }

    public PresignUploadResponse createPresignedForUserFile(UUID userId, String fileName, String fileType) throws Exception {
        String objectName = "files/" + userId + "/" + UUID.randomUUID() + "-" + fileName;
        return createPresigned(objectName, fileType);
    }

    public PresignUploadResponse createPresignedForUserNote(UUID groupId, String fileName, String fileType) throws Exception {
        String objectName = "notes/" + groupId + "/" + UUID.randomUUID() + "-" + fileName;
        return createPresigned(objectName, fileType);
    }

}

