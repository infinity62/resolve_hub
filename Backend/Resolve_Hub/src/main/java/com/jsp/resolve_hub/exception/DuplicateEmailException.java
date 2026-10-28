package com.jsp.resolve_hub.exception;

/** Thrown when registering an email that already exists. Mapped to 409 DUPLICATE_EMAIL. */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}