package com.helpdesk.app.application.usecase.auth;

public interface AuthenticateUserUseCase {

    AuthenticationResult execute(AuthenticateUserCommand command);
}
