package com.helpdesk.app.usecase.auth;

import com.helpdesk.app.application.usecase.auth.AuthenticateUserCommand;
import com.helpdesk.app.application.usecase.auth.AuthenticateUserService;
import com.helpdesk.app.application.usecase.auth.AuthenticationResult;
import com.helpdesk.app.domain.exception.InvalidCredentialsException;
import com.helpdesk.app.domain.model.user.Email;
import com.helpdesk.app.domain.model.user.User;
import com.helpdesk.app.domain.model.user.UserRole;
import com.helpdesk.app.usecase.user.FakePasswordEncoder;
import com.helpdesk.app.usecase.user.FakeUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AuthenticateUserServiceTest {

    private static final String EMAIL = "juan@gmail.com";
    private static final String PASSWORD = "123456";

    private final FakeUserRepository userRepository = new FakeUserRepository();
    private final FakePasswordEncoder passwordEncoder = new FakePasswordEncoder();
    private final FakeTokenGenerator tokenGenerator = new FakeTokenGenerator();

    private final AuthenticateUserService service =
            new AuthenticateUserService(userRepository, passwordEncoder, tokenGenerator);

    @BeforeEach
    void setUp() {
        userRepository.save(new User(1L, "Juan", new Email(EMAIL), passwordEncoder.encode(PASSWORD), UserRole.AGENT));
    }

    @Test
    void shouldAuthenticateWithValidCredentials() {

        AuthenticationResult result = service.execute(new AuthenticateUserCommand(EMAIL, PASSWORD));

        assertEquals("fake-token-1-AGENT", result.getToken());
        assertEquals(1L, result.getUserId());
        assertEquals(EMAIL, result.getEmail());
        assertEquals(UserRole.AGENT, result.getRole());
        assertEquals(1L, tokenGenerator.getLastUserId());
    }

    @Test
    void shouldRejectWrongPassword() {

        assertThrows(
                InvalidCredentialsException.class,
                () -> service.execute(new AuthenticateUserCommand(EMAIL, "otra-clave"))
        );
    }

    @Test
    void shouldRejectUnknownEmail() {

        assertThrows(
                InvalidCredentialsException.class,
                () -> service.execute(new AuthenticateUserCommand("nadie@gmail.com", PASSWORD))
        );
    }

    @Test
    void shouldNotRevealWhetherEmailExists() {

        String unknownEmailMessage = assertThrows(
                InvalidCredentialsException.class,
                () -> service.execute(new AuthenticateUserCommand("nadie@gmail.com", PASSWORD))
        ).getMessage();

        String wrongPasswordMessage = assertThrows(
                InvalidCredentialsException.class,
                () -> service.execute(new AuthenticateUserCommand(EMAIL, "otra-clave"))
        ).getMessage();

        assertEquals(unknownEmailMessage, wrongPasswordMessage,
                "El mensaje no debe distinguir entre email inexistente y password incorrecto");
    }
}
