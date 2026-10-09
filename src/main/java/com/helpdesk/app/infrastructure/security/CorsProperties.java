package com.helpdesk.app.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.cors")
public record CorsProperties(
        List<String> allowedOrigins,
        List<String> allowedOriginPatterns,
        boolean allowCredentials
) {

    private static final String ALL = "*";

    public CorsProperties {
        allowedOrigins = List.copyOf(allowedOrigins == null ? List.of() : allowedOrigins);
        allowedOriginPatterns = List.copyOf(
                allowedOriginPatterns == null ? List.of() : allowedOriginPatterns);

        if (allowedOrigins.isEmpty() && allowedOriginPatterns.isEmpty()) {
            throw new IllegalStateException(
                    "app.cors debe declarar allowed-origins o allowed-origin-patterns, si no el "
                            + "navegador bloquea todas las llamadas del frontend sin avisar");
        }

        if (allowCredentials && allowedOrigins.contains(ALL)) {
            throw new IllegalStateException(
                    "app.cors.allowed-origins no puede ser \"*\" mientras allow-credentials=true, "
                            + "usa allowed-origin-patterns para permitir cualquier puerto de localhost");
        }
    }
}
