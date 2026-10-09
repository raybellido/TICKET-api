package com.helpdesk.app.integration;

import com.helpdesk.app.application.port.out.PasswordEncoder;
import com.helpdesk.app.application.port.out.TokenGenerator;
import com.helpdesk.app.application.port.out.UserRepository;
import com.helpdesk.app.domain.model.user.Email;
import com.helpdesk.app.domain.model.user.User;
import com.helpdesk.app.domain.model.user.UserRole;
import com.helpdesk.app.infrastructure.security.CorsProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CorsIntegrationTest {

    private static final String ALLOWED_ORIGIN = "http://localhost:5173";
    private static final String UNKNOWN_ORIGIN = "http://sitio-malicioso.example";
    private static final String PASSWORD = "secreto123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Autowired
    private CorsProperties properties;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Test
    void shouldAnswerPreflightBeforeAuthentication() throws Exception {

        mockMvc.perform(options("/api/tickets")
                        .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, containsString("PATCH")));
    }

    @Test
    void shouldAnswerPreflightOnThePublicLoginEndpoint() throws Exception {

        mockMvc.perform(options("/api/auth/login")
                        .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN));
    }

    @Test
    void shouldRejectPreflightFromAnOriginThatIsNotConfigured() throws Exception {

        mockMvc.perform(options("/api/tickets")
                        .header(HttpHeaders.ORIGIN, UNKNOWN_ORIGIN)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    void shouldAddAllowOriginHeaderToRealAuthenticatedRequests() throws Exception {

        String token = tokenFor(createUser("cors-ok", UserRole.CUSTOMER));

        mockMvc.perform(get("/api/tickets")
                        .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN));
    }

    @Test
    void shouldBlockRealRequestsComingFromAnUnknownOrigin() throws Exception {

        String token = tokenFor(createUser("cors-ko", UserRole.CUSTOMER));

        mockMvc.perform(get("/api/tickets")
                        .header(HttpHeaders.ORIGIN, UNKNOWN_ORIGIN)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    void shouldNotAllowWildcardOrigin() {
        assertThat(properties.allowedOrigins()).doesNotContain("*");
    }

    @Test
    void shouldNotAllowCredentialsBecauseAuthenticationIsABearerToken() {

        CorsConfiguration configuration = corsConfigurationSource
                .getCorsConfiguration(new MockHttpServletRequest("OPTIONS", "/api/tickets"));

        assertThat(configuration).isNotNull();
        assertThat(configuration.getAllowCredentials()).isFalse();
    }

    @Test
    void shouldApplyCorsOnlyToTheApiPaths() {

        CorsConfiguration configuration = corsConfigurationSource
                .getCorsConfiguration(new MockHttpServletRequest("OPTIONS", "/otro-camino"));

        assertThat(configuration).isNull();
    }

    private String createUser(String name, UserRole role) {
        String email = name + "@test.local";

        User user = new User(name, new Email(email), passwordEncoder.encode(PASSWORD));
        user.changeRole(role);

        return userRepository.save(user).getEmail().getValue();
    }

    private String tokenFor(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return tokenGenerator.generate(user.getId(), user.getEmail().getValue(), user.getRole());
    }
}
