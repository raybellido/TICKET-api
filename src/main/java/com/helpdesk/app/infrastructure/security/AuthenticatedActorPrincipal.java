package com.helpdesk.app.infrastructure.security;

import com.helpdesk.app.application.security.AuthenticatedActor;
import com.helpdesk.app.domain.model.user.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AuthenticatedActorPrincipal implements UserDetails {

    private final Long userId;
    private final String email;
    private final UserRole role;
    private final List<GrantedAuthority> authorities;

    public AuthenticatedActorPrincipal(Long userId, String email, UserRole role) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public AuthenticatedActor toActor() {
        return new AuthenticatedActor(userId, role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return String.valueOf(userId);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
