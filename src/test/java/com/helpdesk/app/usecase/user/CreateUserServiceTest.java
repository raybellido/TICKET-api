package com.helpdesk.app.usecase.user;

import com.helpdesk.app.application.usecase.user.CreateUserCommand;
import com.helpdesk.app.application.usecase.user.CreateUserService;
import com.helpdesk.app.domain.model.user.Email;
import com.helpdesk.app.domain.model.user.User;
import com.helpdesk.app.domain.model.user.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateUserServiceTest {

    @Test
    void shouldCreateUser() {

        FakeUserRepository repository = new FakeUserRepository();

        CreateUserService service = new CreateUserService(repository);

        CreateUserCommand command =
                new CreateUserCommand("Juan", new Email("juan@gmail.com"), "123456");

        User user = service.execute(command);

        assertEquals("Juan", user.getName());
        assertEquals(UserRole.CUSTOMER, user.getRole());
        assertEquals(user, repository.getSavedUser());
    }
}
