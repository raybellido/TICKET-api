package com.helpdesk.app.usecase.user;

import com.helpdesk.app.application.port.out.UserRepository;
import com.helpdesk.app.domain.model.user.User;

import java.util.Optional;

public class FakeUserRepository implements UserRepository {

    private User savedUser;

    @Override
    public User save(User user) {
        this.savedUser = user;
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        if (savedUser == null || !savedUser.getId().equals(id)) {
            return Optional.empty();
        }
        return Optional.of(savedUser);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (savedUser == null || !savedUser.getEmail().getValue().equals(email)) {
            return Optional.empty();
        }
        return Optional.of(savedUser);
    }

    public User getSavedUser() {
        return savedUser;
    }

}
