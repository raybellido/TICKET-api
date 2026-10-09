package com.helpdesk.app.usecase.ticket;

import com.helpdesk.app.application.usecase.ticket.CreateTicketCommand;
import com.helpdesk.app.application.usecase.ticket.CreateTicketUseCase;
import com.helpdesk.app.domain.model.ticket.Ticket;
import com.helpdesk.app.domain.model.ticket.TicketPriority;
import com.helpdesk.app.domain.model.ticket.TicketStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class CreateTicketIntegrationTest {


    @Autowired
    private CreateTicketUseCase createTicketUseCase;

    @Test
    void shouldCreateTicket() {

        CreateTicketCommand command = new CreateTicketCommand("No puedo iniciar sesión", "El sistema rechaza mis credenciales", TicketPriority.HIGH, 1L);

        Ticket ticket = createTicketUseCase.execute(command);

        assertNotNull(ticket);
        assertNotNull(ticket.getId());

        assertEquals("No puedo iniciar sesión", ticket.getTitle());

        assertEquals("El sistema rechaza mis credenciales", ticket.getDescription());

        assertEquals(TicketPriority.HIGH, ticket.getPriority());

        assertEquals(TicketStatus.OPEN, ticket.getStatus());

        assertEquals(1L, ticket.getUserId());
    }
}
