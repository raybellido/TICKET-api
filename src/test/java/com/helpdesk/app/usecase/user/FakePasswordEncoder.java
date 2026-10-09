package com.helpdesk.app.usecase.user;

import com.helpdesk.app.application.port.out.PasswordEncoder;

public class FakePasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(String rawPassword) {
        return "hashed:" + rawPassword;
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}
