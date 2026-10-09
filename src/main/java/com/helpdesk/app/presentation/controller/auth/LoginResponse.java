package com.helpdesk.app.presentation.controller.auth;

import com.helpdesk.app.domain.model.user.UserRole;

public class LoginResponse {

    private final String accessToken;
    private final String tokenType;
    private final Long userId;
    private final String email;
    private final UserRole role;

    public LoginResponse(String accessToken, Long userId, String email, UserRole role) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
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
