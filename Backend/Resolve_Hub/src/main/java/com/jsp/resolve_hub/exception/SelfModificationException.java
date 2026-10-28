package com.jsp.resolve_hub.exception;

/**
 * Thrown when an admin tries to change their OWN status or role, which could
 * lock the only admin out. Mapped to 409 SELF_MODIFICATION_NOT_ALLOWED.
 */
public class SelfModificationException extends RuntimeException {
    public SelfModificationException(String message) {
        super(message);
    }
}