package com.helpdesk.app.presentation.controller.user;

import com.helpdesk.app.application.usecase.user.ChangeUserRoleCommand;
import com.helpdesk.app.application.usecase.user.ChangeUserRoleUseCase;
import com.helpdesk.app.application.usecase.user.CreateUserCommand;
import com.helpdesk.app.application.usecase.user.CreateUserUseCase;
import com.helpdesk.app.domain.model.user.Email;
import com.helpdesk.app.domain.model.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final ChangeUserRoleUseCase changeUserRoleUseCase;

    public UserController(CreateUserUseCase createUserUseCase, ChangeUserRoleUseCase changeUserRoleUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.changeUserRoleUseCase = changeUserRoleUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {

        CreateUserCommand command = new CreateUserCommand(
                request.getName(),
                new Email(request.getEmail()),
                request.getPassword()
        );

        return UserResponse.from(createUserUseCase.execute(command));
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse changeRole(
            @PathVariable Long id,
            @Valid @RequestBody ChangeUserRoleRequest request
    ) {

        User user = changeUserRoleUseCase.execute(
                new ChangeUserRoleCommand(id, request.getRole())
        );

        return UserResponse.from(user);
    }
}
