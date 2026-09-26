package com.helpdesk.app.usecase.user;

import com.helpdesk.app.application.usecase.user.CreateUserCommand;
import com.helpdesk.app.application.usecase.user.CreateUserUseCase;
import com.helpdesk.app.domain.model.user.Email;
import com.helpdesk.app.domain.model.user.User;
import com.helpdesk.app.domain.model.user.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CreateUserIntegrationTest {
    @Autowired
    private CreateUserUseCase createUserUseCase;

    @Test
    void shouldCreateUser() {

        CreateUserCommand command =
                new CreateUserCommand(
                        "Juan",
                        new Email("juan@gmail.com"),
                        "123456"
                );

        User user = createUserUseCase.execute(command);

        assertNotNull (user);
        assertEquals("Juan", user.getName());
        assertEquals(UserRole.CUSTOMER, user.getRole());
        assertNotNull(user.getId());
    }
}
