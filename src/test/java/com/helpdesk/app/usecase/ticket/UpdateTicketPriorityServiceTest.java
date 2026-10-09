package com.helpdesk.app.usecase.ticket;

import java.util.List;
import java.time.Instant;
import com.helpdesk.app.application.security.AuthenticatedActor;
import com.helpdesk.app.application.usecase.ticket.UpdateTicketPriorityCommand;
import com.helpdesk.app.application.usecase.ticket.UpdateTicketPriorityService;
import com.helpdesk.app.domain.exception.ForbiddenAccessException;
import com.helpdesk.app.domain.exception.TicketNotFoundException;
import com.helpdesk.app.domain.model.ticket.Ticket;
import com.helpdesk.app.domain.model.ticket.TicketPriority;
import com.helpdesk.app.domain.model.ticket.TicketStatus;
import com.helpdesk.app.domain.model.user.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UpdateTicketPriorityServiceTest {

    private final FakeTicketRepository repository = new FakeTicketRepository();
    private final UpdateTicketPriorityService service = new UpdateTicketPriorityService(repository);

    @Test
    void shouldUpdatePriorityWhenActorIsAgent() {

        repository.given(ticket(10L));

        Ticket result = service.execute(
                new UpdateTicketPriorityCommand(10L, TicketPriority.CRITICAL, new AuthenticatedActor(99L, UserRole.AGENT))
        );

        assertEquals(TicketPriority.CRITICAL, result.getPriority());
    }

    @Test
    void shouldUpdatePriorityWhenActorIsAdmin() {

        repository.given(ticket(10L));

        Ticket result = service.execute(
                new UpdateTicketPriorityCommand(10L, TicketPriority.LOW, new AuthenticatedActor(99L, UserRole.ADMIN))
        );

        assertEquals(TicketPriority.LOW, result.getPriority());
    }

    @Test
    void shouldRejectCustomerEvenWhenTicketIsOwn() {

        repository.given(ticket(10L));

        assertThrows(
                ForbiddenAccessException.class,
                () -> service.execute(
                        new UpdateTicketPriorityCommand(10L, TicketPriority.CRITICAL, new AuthenticatedActor(1L, UserRole.CUSTOMER))
                )
        );

        assertEquals(TicketPriority.HIGH, repository.findById(10L).getPriority(),
                "La prioridad no debe cambiar cuando la operacion es rechazada");
    }

    @Test
    void shouldFailWhenTicketDoesNotExist() {

        assertThrows(
                TicketNotFoundException.class,
                () -> service.execute(
                        new UpdateTicketPriorityCommand(404L, TicketPriority.HIGH, new AuthenticatedActor(99L, UserRole.AGENT))
                )
        );
    }

    private Ticket ticket(Long id) {
        return new Ticket(id, "No puedo iniciar sesion", "El sistema rechaza mis credenciales",
                TicketStatus.OPEN, TicketPriority.HIGH, 1L, Instant.parse("2026-01-15T10:00:00Z"), List.of());
    }
}
