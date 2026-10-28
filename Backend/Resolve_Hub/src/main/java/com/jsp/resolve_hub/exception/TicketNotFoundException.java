package com.jsp.resolve_hub.exception;

/** Thrown when a ticket id does not exist. Mapped to 404 TICKET_NOT_FOUND. */
public class TicketNotFoundException extends RuntimeException{
    public TicketNotFoundException(String message){
        super(message);
    }
}