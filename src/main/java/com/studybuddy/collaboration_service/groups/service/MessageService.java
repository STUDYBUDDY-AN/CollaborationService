package com.studybuddy.collaboration_service.groups.service;

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

    //----------------------------------------------
    // Get Messages from date to a certain limit
    //----------------------------------------------
    public List<GroupMessage> getMessagesAfter(UUID groupId, Instant after, int limit) {
        return groupMessageRepository.getMessagesAfter(groupId, after, limit);
    }

    //----------------------------------------------
    // Get Recent Messages
    //----------------------------------------------
    public List<GroupMessage> getRecentMessages(UUID groupId, int limit) {
        return groupMessageRepository.getRecentMessages(groupId, limit);
    }

    //----------------------------------------------
    // Fetch Messages from a certain date
    //----------------------------------------------
    public List<GroupMessage> fetchMessages(UUID groupId, Instant since) {
        return groupMessageRepository.fetchMessages(groupId, since);
    }

    //----------------------------------------------
    // Get Messages before a certain date
    //----------------------------------------------
    public List<GroupMessage> getMessagesBefore(UUID groupId, Instant before, int limit) {
        return groupMessageRepository.getMessagesBefore(groupId, before, limit);
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
