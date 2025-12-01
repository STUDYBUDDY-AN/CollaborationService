package com.studybuddy.collaboration_service.groups.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GroupNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleGroupNotFound(GroupNotFoundException ex) {
        return Map.of(
                "error", "GROUP_NOT_FOUND",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(InvalidGroupNameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleInvalidName(InvalidGroupNameException ex) {
        return Map.of(
                "error", "INVALID_GROUP_NAME",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(AlreadyMemberException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleAlreadyMember(AlreadyMemberException ex) {
        return Map.of(
                "error", "ALREADY_MEMBER",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(NotGroupMemberException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Object> handleNotGroupMember(NotGroupMemberException ex) {
        return Map.of(
                "error", "NOT_GROUP_MEMBER",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(MessageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleMessageNotFound(MessageNotFoundException ex) {
        return Map.of(
                "error", "MESSAGE_NOT_FOUND",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleAny(Exception ex) {
        return Map.of(
                "error", "INTERNAL_SERVER_ERROR",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Object> handleForbidden(ForbiddenException ex) {
        return Map.of(
                "error", "FORBIDDEN",
                "message", ex.getMessage()
        );
    }
}

