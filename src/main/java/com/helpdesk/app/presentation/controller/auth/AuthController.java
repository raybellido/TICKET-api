package com.helpdesk.app.presentation.controller.auth;

import com.helpdesk.app.application.usecase.auth.AuthenticateUserCommand;
import com.helpdesk.app.application.usecase.auth.AuthenticateUserUseCase;
import com.helpdesk.app.application.usecase.auth.AuthenticationResult;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;

    public AuthController(AuthenticateUserUseCase authenticateUserUseCase) {
        this.authenticateUserUseCase = authenticateUserUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        AuthenticationResult result = authenticateUserUseCase.execute(
                new AuthenticateUserCommand(request.getEmail(), request.getPassword())
        );

        return ResponseEntity.ok(
                new LoginResponse(result.getToken(), result.getUserId(), result.getEmail(), result.getRole())
        );
    }
}
