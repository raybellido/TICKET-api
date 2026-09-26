package com.helpdesk.app.domain.model.user;

import com.helpdesk.app.domain.exception.InvalidUserException;

public class User {

    private Long id;
    private String name;
    private Email email;
    private String password;
    private UserRole role;

    public User(Long id, String name, Email email, String password, UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(String name, Email email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = UserRole.CUSTOMER;
    }

    public void changeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidUserException("el usuario no puede tener nombre vacio");
        }

        this.name = name;
    }

    public void changeEmail(Email email) {
        this.email = email;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeRole(UserRole role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public UserRole getRole() {
        return role;
    }

    public Long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public Email getEmail() {
        return email;
    }
}
