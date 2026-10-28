package com.jsp.resolve_hub.exception;

/** Thrown when a user id/email does not exist. Mapped to 404 USER_NOT_FOUND. */
public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message) {
        super(message);
    }
}