package com.helpdesk.app.usecase.ticket;

import java.time.Instant;
import com.helpdesk.app.application.security.AuthenticatedActor;
import com.helpdesk.app.application.usecase.ticket.ListTicketsCommand;
import com.helpdesk.app.application.usecase.ticket.ListTicketsService;
import com.helpdesk.app.domain.model.ticket.Ticket;
import com.helpdesk.app.domain.model.ticket.TicketPriority;
import com.helpdesk.app.domain.model.ticket.TicketStatus;
import com.helpdesk.app.domain.model.user.UserRole;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ListTicketsServiceTest {

    private static final Long CUSTOMER_A = 1L;
    private static final Long CUSTOMER_B = 2L;

    private final FakeTicketRepository repository = new FakeTicketRepository();
    private final ListTicketsService service = new ListTicketsService(repository);

    @Test
    void shouldReturnEveryTicketToAgent() {

        repository.given(ticket(10L, CUSTOMER_A, TicketStatus.OPEN, TicketPriority.LOW));
        repository.given(ticket(11L, CUSTOMER_B, TicketStatus.OPEN, TicketPriority.HIGH));

        List<Ticket> result = service.execute(noFilters(),
                new AuthenticatedActor(99L, UserRole.AGENT));

        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnOnlyOwnTicketsToCustomer() {

        repository.given(ticket(10L, CUSTOMER_A, TicketStatus.OPEN, TicketPriority.LOW));
        repository.given(ticket(11L, CUSTOMER_B, TicketStatus.OPEN, TicketPriority.HIGH));

        List<Ticket> result = service.execute(noFilters(),
                new AuthenticatedActor(CUSTOMER_A, UserRole.CUSTOMER));

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
    }

    @Test
    void shouldIgnoreUserIdRequestedByCustomer() {

        repository.given(ticket(10L, CUSTOMER_A, TicketStatus.OPEN, TicketPriority.LOW));
        repository.given(ticket(11L, CUSTOMER_B, TicketStatus.OPEN, TicketPriority.HIGH));

        List<Ticket> result = service.execute(new ListTicketsCommand(CUSTOMER_B, null, null),
                new AuthenticatedActor(CUSTOMER_A, UserRole.CUSTOMER));

        assertEquals(CUSTOMER_A, repository.getLastCriteria().userId(),
                "El servicio debe forzar el userId del actor");
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
    }

    @Test
    void shouldKeepStatusAndPriorityFiltersForCustomer() {

        repository.given(ticket(10L, CUSTOMER_A, TicketStatus.OPEN, TicketPriority.LOW));
        repository.given(ticket(11L, CUSTOMER_A, TicketStatus.CLOSED, TicketPriority.CRITICAL));

        List<Ticket> result = service.execute(
                new ListTicketsCommand(null, TicketStatus.CLOSED, TicketPriority.CRITICAL),
                new AuthenticatedActor(CUSTOMER_A, UserRole.CUSTOMER));

        assertEquals(CUSTOMER_A, repository.getLastCriteria().userId());
        assertEquals(TicketStatus.CLOSED, repository.getLastCriteria().status());
        assertEquals(TicketPriority.CRITICAL, repository.getLastCriteria().priority());
        assertEquals(1, result.size());
        assertEquals(11L, result.get(0).getId());
    }

    @Test
    void shouldHonourUserIdFilterForAgent() {

        repository.given(ticket(10L, CUSTOMER_A, TicketStatus.OPEN, TicketPriority.LOW));
        repository.given(ticket(11L, CUSTOMER_B, TicketStatus.OPEN, TicketPriority.HIGH));

        List<Ticket> result = service.execute(new ListTicketsCommand(CUSTOMER_B, null, null),
                new AuthenticatedActor(99L, UserRole.AGENT));

        assertEquals(CUSTOMER_B, repository.getLastCriteria().userId(),
                "Un AGENT si puede filtrar por cualquier userId");
        assertEquals(1, result.size());
        assertEquals(11L, result.get(0).getId());
    }

    @Test
    void shouldReturnEmptyListWhenCustomerHasNoTickets() {

        repository.given(ticket(10L, CUSTOMER_B, TicketStatus.OPEN, TicketPriority.LOW));

        List<Ticket> result = service.execute(noFilters(),
                new AuthenticatedActor(CUSTOMER_A, UserRole.CUSTOMER));

        assertTrue(result.isEmpty());
    }

    private ListTicketsCommand noFilters() {
        return new ListTicketsCommand(null, null, null);
    }

    private Ticket ticket(Long id, Long userId, TicketStatus status, TicketPriority priority) {
        return new Ticket(id, "No puedo iniciar sesion", "El sistema rechaza mis credenciales",
                status, priority, userId, Instant.parse("2026-01-15T10:00:00Z"), List.of());
    }
}
