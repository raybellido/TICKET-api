package com.helpdesk.app.application.usecase.user;

import com.helpdesk.app.application.port.out.PasswordEncoder;
import com.helpdesk.app.application.port.out.UserRepository;
import com.helpdesk.app.domain.model.user.User;
import org.springframework.stereotype.Service;


@Service
public class CreateUserService implements CreateUserUseCase{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User execute(CreateUserCommand command) {

        User user = new User(
                command.getName(),
                command.getEmail(),
                passwordEncoder.encode(command.getPassword())
        );

        return userRepository.save(user);
    }

}
