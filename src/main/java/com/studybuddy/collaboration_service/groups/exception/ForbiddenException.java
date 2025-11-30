package com.studybuddy.collaboration_service.groups.exception;

public class ForbiddenException extends RuntimeException{
    public ForbiddenException() {
        super("Forbidden");
    }
    public ForbiddenException(String message) {
        super(message);
    }
}
