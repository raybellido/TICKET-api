package com.helpdesk.app.domain.model.user;

import com.helpdesk.app.domain.exception.InvalidEmailException;

public class Email {

    private final String value;


    public String getValue() {
        return value;
    }

    public Email(String value) {

        if (value == null || !value.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new InvalidEmailException("Invalid email");
        }

        this.value = value;
    }
}
