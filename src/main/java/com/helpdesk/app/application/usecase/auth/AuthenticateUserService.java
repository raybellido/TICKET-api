package com.helpdesk.app.application.usecase.auth;

import com.helpdesk.app.application.port.out.PasswordEncoder;
import com.helpdesk.app.application.port.out.TokenGenerator;
import com.helpdesk.app.application.port.out.UserRepository;
import com.helpdesk.app.domain.exception.InvalidCredentialsException;
import com.helpdesk.app.domain.model.user.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticateUserService implements AuthenticateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;

    public AuthenticateUserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            TokenGenerator tokenGenerator
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public AuthenticationResult execute(AuthenticateUserCommand command) {

        Optional<User> maybeUser = userRepository.findByEmail(command.getEmail());

        if (maybeUser.isEmpty()) {
            throw new InvalidCredentialsException("Credenciales invalidas");
        }

        User user = maybeUser.get();

        if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Credenciales invalidas");
        }

        String token = tokenGenerator.generate(user.getId(), user.getEmail().getValue(), user.getRole());

        return new AuthenticationResult(token, user.getId(), user.getEmail().getValue(), user.getRole());
    }
}
