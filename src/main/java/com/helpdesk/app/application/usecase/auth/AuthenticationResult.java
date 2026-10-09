package com.helpdesk.app.application.usecase.auth;

import com.helpdesk.app.domain.model.user.UserRole;

public class AuthenticationResult {

    private final String token;
    private final Long userId;
    private final String email;
    private final UserRole role;

    public AuthenticationResult(String token, Long userId, String email, UserRole role) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }
}
