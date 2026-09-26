package com.helpdesk.app.application.usecase.user;

import com.helpdesk.app.domain.model.user.User;

public interface CreateUserUseCase {

    User execute(CreateUserCommand command);

}
