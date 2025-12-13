package com.studybuddy.collaboration_service.groups.service;

import com.studybuddy.collaboration_service.groups.dto.Response.AttachmentResponse;
import com.studybuddy.collaboration_service.groups.dto.Response.MessageResponse;
import com.studybuddy.collaboration_service.groups.entities.GroupMessage;
import com.studybuddy.collaboration_service.groups.entities.MessageAttachment;
import com.studybuddy.collaboration_service.groups.exception.ForbiddenException;
import com.studybuddy.collaboration_service.groups.exception.GroupNotFoundException;
import com.studybuddy.collaboration_service.groups.exception.MessageNotFoundException;
import com.studybuddy.collaboration_service.groups.exception.NotGroupMemberException;
import com.studybuddy.collaboration_service.groups.repository.GroupMemberRepository;
import com.studybuddy.collaboration_service.groups.repository.GroupMessageRepository;
import com.studybuddy.collaboration_service.groups.repository.MessageAttachmentRepository;
import com.studybuddy.collaboration_service.groups.repository.StudyGroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class MessageService {

    private final GroupMessageRepository groupMessageRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MessageAttachmentRepository attachmentRepository;

    //----------------------------------------------
    // Send Message
    //----------------------------------------------
    @Transactional
    public UUID sendMessage(UUID groupId,  UUID senderId, String content) {

        studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        if(!groupMemberRepository.exists(groupId, senderId)){
            throw new NotGroupMemberException(groupId, senderId);
        }

        UUID messageId = UUID.randomUUID();

        GroupMessage message = new GroupMessage();
        message.setId(messageId);
        message.setGroupId(groupId);
        message.setSenderId(senderId);
        message.setContent(content);

        groupMessageRepository.save(message);

        return messageId;
    }

    @Transactional
    public UUID sendMessageWithAttachment(
            UUID groupId,
            UUID senderId,
            String content,
            String attachmentUrl,
            String attachmentType,
            Long attachmentSize
    ) {
        if ((content == null || content.isBlank()) &&
                (attachmentUrl == null || attachmentUrl.isBlank())) {
            throw new IllegalArgumentException("Message must contain text or attachment");
        }

        GroupMessage saved = sendAndReturn(
                groupId,
                senderId,
                content,
                attachmentUrl,
                attachmentType,
                attachmentSize
        );

        return saved.getId();
    }


    //----------------------------------------------
    // Get Messages from date to a certain limit
    //----------------------------------------------
    public List<MessageResponse> getMessagesAfterWithAttachments(UUID groupId, Instant after, int limit) {
        var messages = groupMessageRepository.getMessagesAfter(groupId, after, limit);
        return mapToResponse(messages);
    }

    //----------------------------------------------
    // Get Recent Messages
    //----------------------------------------------
    public List<MessageResponse> getRecentMessagesWithAttachments(UUID groupId, int limit) {
        var messages = groupMessageRepository.getRecentMessages(groupId, limit);
        return mapToResponse(messages);
    }

    //----------------------------------------------
    // Fetch Messages from a certain date
    //----------------------------------------------
    public List<MessageResponse> fetchMessagesWithAttachments(UUID groupId, Instant since) {
        var messages = groupMessageRepository.fetchMessages(groupId, since);
        return mapToResponse(messages);
    }

    //----------------------------------------------
    // Get Messages before a certain date
    //----------------------------------------------
    public List<MessageResponse> getMessagesBeforeWithAttachments(UUID groupId, Instant before, int limit) {
        var messages = groupMessageRepository.getMessagesBefore(groupId, before, limit);
        return mapToResponse(messages);
    }

    private List<MessageResponse> mapToResponse(List<GroupMessage> messages) {
        return messages.stream()
                .map(message -> {
                    var attachments = attachmentRepository.findByMessageId(message.getId())
                            .stream()
                            .map(att -> new AttachmentResponse(
                                    att.getId(),
                                    att.getFileUrl(),
                                    att.getFileType(),
                                    att.getFileSizeBytes(),
                                    att.getCreatedAt()
                            )).toList();

                    return new MessageResponse(
                            message.getId(),
                            message.getSenderId(),
                            message.getContent(),
                            message.getSentAt(),
                            attachments
                    );
                })
                .toList();
    }

    //----------------------------------------------
    // Send Message and Return
    //----------------------------------------------
    public GroupMessage sendAndReturn(UUID groupId, UUID senderId, String content) {
        studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        if (!groupMemberRepository.exists(groupId, senderId)) {
            throw new NotGroupMemberException(groupId, senderId);
        }

        UUID messageId = UUID.randomUUID();

        GroupMessage message = new GroupMessage();
        message.setId(messageId);
        message.setGroupId(groupId);
        message.setSenderId(senderId);
        message.setContent(content);
        message.setSentAt(Instant.now());

        groupMessageRepository.save(message);

        return message;
    }

    public GroupMessage sendAndReturn(
            UUID groupId,
            UUID senderId,
            String content,
            String attachmentUrl,
            String attachmentType,
            Long attachmentSize
    ) {
        // 1. validate group & membership
        studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        if (!groupMemberRepository.exists(groupId, senderId)) {
            throw new NotGroupMemberException(groupId, senderId);
        }

        UUID messageId = UUID.randomUUID();

        GroupMessage message = new GroupMessage();
        message.setId(messageId);
        message.setGroupId(groupId);
        message.setSenderId(senderId);
        message.setContent(content);
        message.setSentAt(Instant.now());

        groupMessageRepository.save(message);

        // 2. persist attachment if present
        if (attachmentUrl != null && !attachmentUrl.isBlank()) {
            MessageAttachment att = new MessageAttachment();
            att.setId(UUID.randomUUID());
            att.setMessageId(messageId);
            att.setFileUrl(attachmentUrl);
            att.setFileType(attachmentType);
            att.setFileSizeBytes(attachmentSize != null ? attachmentSize : 0L);
            att.setCreatedAt(Instant.now());

            attachmentRepository.save(att);
            // optional: attach to message in-memory if you add a List<MessageAttachment> field later
        }

        return message;
    }


    //----------------------------------------------
    // Edit Message
    //----------------------------------------------
    public GroupMessage editMessage(UUID groupId, UUID messageId, UUID userId, String newContent) {
        GroupMessage message = groupMessageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));

        if (!message.getGroupId().equals(groupId)) throw new IllegalArgumentException("Message not in group");
        if (!message.getSenderId().equals(userId)) throw new ForbiddenException("Editor not authorized");

        message.setContent(newContent);
        message.setEditedAt(Instant.now());
        groupMessageRepository.updateContent(message);
        return message;
    }

    //----------------------------------------------
    // Soft Delete Message
    //----------------------------------------------
    public void softDelete(UUID groupId, UUID messageId, UUID userId) {
        GroupMessage message = groupMessageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));

        if (!message.getGroupId().equals(groupId)) throw new IllegalArgumentException("Message not in group");
        if (!message.getSenderId().equals(userId) && !isAdmin(userId)) throw new ForbiddenException("Editor not authorized");

        message.setDeleted(true);
        message.setDeletedAt(Instant.now());
        message.setDeletedBy(userId);
        groupMessageRepository.softDelete(message);
    }

    private boolean isAdmin(UUID userId) {
        return false;
    }


}
