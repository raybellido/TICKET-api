package com.helpdesk.app.infrastructure.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableConfigurationProperties(CorsProperties.class)
public class CorsConfig {

    /**
     * Se admiten todos los verbos HTTP habituales para que anadir un endpoint nuevo
     * (por ejemplo un DELETE) no provoque un preflight fallido sin avisar.
     */
    private static final List<String> ALLOWED_METHODS = List.of(
            HttpMethod.GET.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.PATCH.name(),
            HttpMethod.DELETE.name(),
            HttpMethod.HEAD.name(),
            HttpMethod.OPTIONS.name()
    );

    private static final List<String> ALLOWED_HEADERS = List.of(
            HttpHeaders.AUTHORIZATION,
            HttpHeaders.CONTENT_TYPE,
            HttpHeaders.ACCEPT
    );

    /**
     * Cabeceras de la respuesta que el navegador deja leer al frontend. Sin esto el
     * fetch no puede ver la cabecera Location de un 201 con redirect.
     */
    private static final List<String> EXPOSED_HEADERS = List.of(
            HttpHeaders.AUTHORIZATION,
            HttpHeaders.LOCATION
    );

    /**
     * Se cubre tambien la documentacion para que el frontend pueda leer el OpenAPI
     * JSON desde el navegador durante el desarrollo.
     */
    private static final String[] CORS_PATHS = {
            "/api/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**"
    };

    @Bean
    CorsConfigurationSource corsConfigurationSource(CorsProperties properties) {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(properties.allowedOrigins());
        configuration.setAllowedOriginPatterns(properties.allowedOriginPatterns());
        configuration.setAllowedMethods(ALLOWED_METHODS);
        configuration.setAllowedHeaders(ALLOWED_HEADERS);
        configuration.setExposedHeaders(EXPOSED_HEADERS);
        configuration.setAllowCredentials(properties.allowCredentials());
        configuration.setMaxAge(Duration.ofHours(1));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        for (String path : CORS_PATHS) {
            source.registerCorsConfiguration(path, configuration);
        }
        return source;
    }
}
