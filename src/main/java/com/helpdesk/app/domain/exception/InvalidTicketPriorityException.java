package com.helpdesk.app.domain.exception;

public class InvalidTicketPriorityException extends RuntimeException {

    public InvalidTicketPriorityException(String message) {
        super(message);
    }
}
