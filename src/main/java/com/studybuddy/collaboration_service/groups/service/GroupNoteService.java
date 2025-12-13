package com.studybuddy.collaboration_service.groups.service;

import com.studybuddy.collaboration_service.groups.entities.GroupNote;
import com.studybuddy.collaboration_service.groups.exception.GroupNotFoundException;
import com.studybuddy.collaboration_service.groups.exception.NotGroupMemberException;
import com.studybuddy.collaboration_service.groups.repository.GroupMemberRepository;
import com.studybuddy.collaboration_service.groups.repository.GroupNoteRepository;
import com.studybuddy.collaboration_service.groups.repository.StudyGroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GroupNoteService {

    private final GroupNoteRepository noteRepo;
    private final GroupMemberRepository members;
    private final StudyGroupRepository groups;

    @Transactional
    public UUID saveNote(UUID groupId, UUID uploadedBy, String title, String url, String type, long size) {

        if(groups.findById(groupId).isEmpty())
            throw new GroupNotFoundException(groupId);

        if(!members.exists(groupId, uploadedBy))
            throw new NotGroupMemberException(groupId, uploadedBy);

        UUID noteId = UUID.randomUUID();

        GroupNote note = new GroupNote();
        note.setId(noteId);
        note.setGroupId(groupId);
        note.setUploadedBy(uploadedBy);
        note.setTitle(title);
        note.setFileUrl(url);
        note.setFileType(type);
        note.setFileSizeBytes(size);
        note.setCreatedAt(Instant.now());

        noteRepo.save(note);

        return noteId;
    }

    public List<GroupNote> getNotes(UUID groupId) {
        return noteRepo.findByGroup(groupId);
    }
}
