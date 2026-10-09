package com.helpdesk.app.application.usecase.user;

import com.helpdesk.app.domain.model.user.UserRole;

public class ChangeUserRoleCommand {

    private final Long userId;
    private final UserRole role;

    public ChangeUserRoleCommand(Long userId, UserRole role) {
        this.userId = userId;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public UserRole getRole() {
        return role;
    }
}
