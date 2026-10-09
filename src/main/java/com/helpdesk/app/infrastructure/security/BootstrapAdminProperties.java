package com.helpdesk.app.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.bootstrap-admin")
public record BootstrapAdminProperties(
        boolean enabled,
        String name,
        String email,
        String password
) {
}
