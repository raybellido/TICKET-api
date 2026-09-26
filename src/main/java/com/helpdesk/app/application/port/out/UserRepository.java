package com.helpdesk.app.application.port.out;

import com.helpdesk.app.domain.model.user.User;

public interface UserRepository {

    User save(User user);
}
