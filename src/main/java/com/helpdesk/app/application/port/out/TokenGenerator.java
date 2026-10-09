package com.helpdesk.app.application.port.out;

import com.helpdesk.app.domain.model.user.UserRole;

public interface TokenGenerator {

    String generate(Long userId, String email, UserRole role);
}
