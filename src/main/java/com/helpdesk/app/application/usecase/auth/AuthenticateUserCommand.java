package com.helpdesk.app.application.usecase.auth;

public class AuthenticateUserCommand {

    private final String email;
    private final String password;

    public AuthenticateUserCommand(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
