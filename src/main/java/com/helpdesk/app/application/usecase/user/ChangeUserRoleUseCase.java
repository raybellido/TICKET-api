package com.helpdesk.app.application.usecase.user;

import com.helpdesk.app.domain.model.user.User;

public interface ChangeUserRoleUseCase {

    User execute(ChangeUserRoleCommand command);
}
