package com.studybuddy.collaboration_service.groups.exception;

import java.util.UUID;

public class AlreadyMemberException extends RuntimeException {
    public AlreadyMemberException(UUID groupId, UUID userId) {
        super("User " + userId + " is already a member of group " + groupId);
    }
}