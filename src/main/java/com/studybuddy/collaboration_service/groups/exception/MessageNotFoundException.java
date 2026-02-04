package com.studybuddy.collaboration_service.groups.exception;

import java.util.UUID;

public class MessageNotFoundException extends RuntimeException{
    public MessageNotFoundException(UUID messageId) {
        super("Message not found with ID: " + messageId);
    }
}
