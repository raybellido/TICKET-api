package com.helpdesk.app.application.security;

import com.helpdesk.app.domain.model.user.UserRole;

public record AuthenticatedActor(Long userId, UserRole role) {

    public boolean isCustomer() {
        return role == UserRole.CUSTOMER;
    }

    public boolean isStaff() {
        return role == UserRole.AGENT || role == UserRole.ADMIN;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }
}
