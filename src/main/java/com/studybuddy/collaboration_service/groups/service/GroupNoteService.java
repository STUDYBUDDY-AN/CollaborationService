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
        enrichPreviewMetadata(note);

        noteRepo.save(note);

        return noteId;
    }

    private void enrichPreviewMetadata(GroupNote note) {
        if (note.getFileType() == null) {
            note.setPreviewAvailable(false);
            note.setPreviewType("NONE");
            return;
        }

        if (note.getFileType().startsWith("image/")) {
            note.setPreviewAvailable(true);
            note.setPreviewType("IMAGE");
        }
        else if (note.getFileType().equals("application/pdf")) {
            note.setPreviewAvailable(true);
            note.setPreviewType("PDF");
        }
        else if (note.getFileType().startsWith("text/")) {
            note.setPreviewAvailable(true);
            note.setPreviewType("TEXT");
        }
        else {
            note.setPreviewAvailable(false);
            note.setPreviewType("NONE");
        }
    }


    public List<GroupNote> getNotes(UUID groupId) {
        return noteRepo.findByGroup(groupId);
    }

    public List<GroupNote> searchNotes(
            UUID groupId,
            String query,
            String fileType,
            UUID uploadedBy,
            int limit,
            int offset
    ) {
        return noteRepo.search(groupId, query, fileType, uploadedBy, limit, offset);
    }

}
