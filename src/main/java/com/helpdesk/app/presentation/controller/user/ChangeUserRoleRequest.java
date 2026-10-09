package com.helpdesk.app.presentation.controller.user;

import com.helpdesk.app.domain.model.user.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ChangeUserRoleRequest {

    @NotNull
    private UserRole role;

    public ChangeUserRoleRequest() {
    }

    public ChangeUserRoleRequest(UserRole role) {
        this.role = role;
    }
}
