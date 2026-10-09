package com.helpdesk.app.application.usecase.user;

import com.helpdesk.app.application.port.out.UserRepository;
import com.helpdesk.app.domain.exception.UserNotFoundException;
import com.helpdesk.app.domain.model.user.User;
import org.springframework.stereotype.Service;

@Service
public class ChangeUserRoleService implements ChangeUserRoleUseCase {

    private final UserRepository userRepository;

    public ChangeUserRoleService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User execute(ChangeUserRoleCommand command) {

        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id: " + command.getUserId()));

        user.changeRole(command.getRole());

        return userRepository.save(user);
    }
}
