package com.helpdesk.app.application.usecase.user;

import com.helpdesk.app.application.port.out.UserRepository;
import com.helpdesk.app.domain.model.user.User;
import org.springframework.stereotype.Service;


@Service
public class CreateUserService implements CreateUserUseCase{

    private final UserRepository userRepository;

    public CreateUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User execute(CreateUserCommand command) {
        User user = new User(
                command.getName(),
                command.getEmail(),
                command.getPassword()
        );

        return userRepository.save(user);
    }

}
