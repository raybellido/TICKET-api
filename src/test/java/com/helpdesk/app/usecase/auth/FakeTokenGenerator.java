package com.helpdesk.app.usecase.auth;

import com.helpdesk.app.application.port.out.TokenGenerator;
import com.helpdesk.app.domain.model.user.UserRole;

public class FakeTokenGenerator implements TokenGenerator {

    private Long lastUserId;
    private UserRole lastRole;

    @Override
    public String generate(Long userId, String email, UserRole role) {
        this.lastUserId = userId;
        this.lastRole = role;
        return "fake-token-" + userId + "-" + role.name();
    }

    public Long getLastUserId() {
        return lastUserId;
    }

    public UserRole getLastRole() {
        return lastRole;
    }
}
