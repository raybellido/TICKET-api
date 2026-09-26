package com.helpdesk.app.domain.exception;

public class InvalidTicketStateException extends RuntimeException{

    public InvalidTicketStateException(String message) {
        super(message);
    }
}
