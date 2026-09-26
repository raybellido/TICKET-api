package com.helpdesk.app.domain.model.user;

import com.helpdesk.app.domain.exception.InvalidUserException;
import com.helpdesk.app.domain.model.ticket.TicketStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {

    @Test
    void shouldCreateUserAsCustomer(){
        User user = new User(
                "juan",
                new Email("juan@gmail.com"),
                "5743"
        );

        assertEquals(UserRole.CUSTOMER,user.getRole(),"should be customer");

    }

    @Test
    void shouldChangeUserName(){
        User user = new User(
                "juan",
                new Email("juan@gmail.com"),
                "5743"
        );

        user.changeName("pablo");

        assertEquals("pablo",user.getName(),"The name should be updated to pablo");

    }

    @Test
    void shouldChangeUserRole() {
        User user = new User(
                "juan",
                new Email("juan@gmail.com"),
                "5743"
        );

        user.changeRole(UserRole.ADMIN);

        assertEquals(UserRole.ADMIN,user.getRole(),"The role should be updated to Admin");
    }

    @Test
    void shouldNotAllowEmptyName() {
        User user = new User(
                "juan",
                new Email("juan@gmail.com"),
                "5743"
        );

        assertThrows(
                InvalidUserException.class,
                () -> user.changeName("")
        );
    }
}
