package com.helpdesk.app.usecase.user;

import com.helpdesk.app.application.port.out.UserRepository;
import com.helpdesk.app.domain.model.user.User;

public class FakeUserRepository implements UserRepository {

    private User savedUser;

    @Override
    public User save(User user) {
        this.savedUser = user;
        return user;
    }

    public User getSavedUser() {
        return savedUser;
    }

}
