package com.studybuddy.collaboration_service.groups.service;

import com.studybuddy.collaboration_service.groups.entities.GroupMessage;
import com.studybuddy.collaboration_service.groups.exception.GroupNotFoundException;
import com.studybuddy.collaboration_service.groups.exception.MessageNotFoundException;
import com.studybuddy.collaboration_service.groups.exception.NotGroupMemberException;
import com.studybuddy.collaboration_service.groups.repository.GroupMemberRepository;
import com.studybuddy.collaboration_service.groups.repository.GroupMessageRepository;
import com.studybuddy.collaboration_service.groups.repository.StudyGroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class MessageService {

    private final GroupMessageRepository groupMessageRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final GroupMemberRepository groupMemberRepository;

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
}
