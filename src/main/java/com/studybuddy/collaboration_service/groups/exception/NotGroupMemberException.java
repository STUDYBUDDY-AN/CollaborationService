package com.studybuddy.collaboration_service.groups.exception;

import java.util.UUID;

public class NotGroupMemberException extends RuntimeException{
    public NotGroupMemberException(UUID groupId, UUID userId) {
        super("User " + userId + " is not a member of group " + groupId);
    }
}
