package com.helpdesk.app.presentation.controller.user;

import com.helpdesk.app.application.usecase.user.CreateUserCommand;
import com.helpdesk.app.application.usecase.user.CreateUserUseCase;
import com.helpdesk.app.domain.model.user.Email;
import com.helpdesk.app.domain.model.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;

    public UserController(CreateUserUseCase createUserUseCase) {
        this.createUserUseCase = createUserUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {

        CreateUserCommand command = new CreateUserCommand(request.getName(), new Email(request.getEmail()), request.getPassword());

        User user = createUserUseCase.execute(command);

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail().getValue(),
                user.getRole()
        );

    }
}
