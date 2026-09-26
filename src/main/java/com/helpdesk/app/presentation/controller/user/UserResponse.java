package com.helpdesk.app.presentation.controller.user;

import com.helpdesk.app.domain.model.user.UserRole;

public class UserResponse {
    private final Long id;
    private final String name;
    private final String email;
    private final UserRole role;

    public UserResponse(Long id, String name, String email, UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }
}
