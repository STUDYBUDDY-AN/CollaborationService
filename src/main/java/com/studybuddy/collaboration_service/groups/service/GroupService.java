package com.studybuddy.collaboration_service.groups.service;

import com.studybuddy.collaboration_service.groups.entities.GroupMember;
import com.studybuddy.collaboration_service.groups.entities.StudyGroup;
import com.studybuddy.collaboration_service.groups.exception.AlreadyMemberException;
import com.studybuddy.collaboration_service.groups.exception.GroupNotFoundException;
import com.studybuddy.collaboration_service.groups.exception.InvalidGroupNameException;
import com.studybuddy.collaboration_service.groups.repository.GroupMemberRepository;
import com.studybuddy.collaboration_service.groups.repository.StudyGroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final GroupMemberRepository groupMemberRepository;

    //----------------------------------------------
    // Create Group
    //----------------------------------------------
    @Transactional
    public UUID createGroup(String name, String description, UUID ownerId){

        if (name == null || name.trim().isEmpty()) {
            throw new InvalidGroupNameException("Group name must not be empty");
        }

        UUID groupId = UUID.randomUUID();

        StudyGroup group = new StudyGroup();
            group.setId(groupId);
            group.setName(name);
            group.setDescription(description);
            group.setOwnerId(ownerId);

        studyGroupRepository.save(group);

        GroupMember owner = new GroupMember();
            owner.setGroupId(groupId);
            owner.setUserId(ownerId);

        groupMemberRepository.addMember(owner);

        return groupId;
    }

    //----------------------------------------------
    // Add Member To Group
    //----------------------------------------------
    public void addMemberToGroup(UUID groupId, UUID userId) {

        if (studyGroupRepository.findById(groupId).isEmpty()) {
            throw new GroupNotFoundException(groupId);
        }

        if (groupMemberRepository.exists(groupId, userId)) {
            throw new AlreadyMemberException(groupId, userId);
        }

        GroupMember member = new GroupMember();
            member.setGroupId(groupId);
            member.setUserId(userId);

        groupMemberRepository.addMember(member);
    }

    //----------------------------------------------
    // Get Group
    //----------------------------------------------
    public StudyGroup getGroup(UUID groupId) {
        return studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));
    }

    //----------------------------------------------
    // Get All Groups Of User
    //----------------------------------------------
    public List<StudyGroup> getAllGroupsOfUser(UUID userId){
        return studyGroupRepository.findByUserId(userId);
    }

    //----------------------------------------------
    // Get Group Members
    //----------------------------------------------
    public List<UUID> getMembers(UUID groupId) {
        return groupMemberRepository.findMembers(groupId)
                .stream()
                .map(GroupMember::getUserId)
                .toList();
    }

    //----------------------------------------------
    // Get All Groups
    //----------------------------------------------
    public List<StudyGroup> getAllGroups(){
        return studyGroupRepository.findAll();
    }

}
