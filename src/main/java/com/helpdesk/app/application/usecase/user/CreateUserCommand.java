package com.helpdesk.app.application.usecase.user;

import com.helpdesk.app.domain.model.user.Email;

public class CreateUserCommand {


    private final Email email;
    private final String password;
    private final String name;


    public CreateUserCommand(
            String name,
            Email email,
            String password
    ) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public Email getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

}
