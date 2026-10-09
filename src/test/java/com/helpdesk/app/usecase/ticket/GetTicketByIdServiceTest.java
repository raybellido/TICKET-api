package com.helpdesk.app.usecase.ticket;

import java.util.List;
import java.time.Instant;
import com.helpdesk.app.application.security.AuthenticatedActor;
import com.helpdesk.app.application.usecase.ticket.GetTicketByIdService;
import com.helpdesk.app.domain.exception.ForbiddenAccessException;
import com.helpdesk.app.domain.model.ticket.Ticket;
import com.helpdesk.app.domain.model.ticket.TicketPriority;
import com.helpdesk.app.domain.model.ticket.TicketStatus;
import com.helpdesk.app.domain.model.user.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GetTicketByIdServiceTest {

    private static final Long OWNER_ID = 1L;
    private static final Long OTHER_CUSTOMER_ID = 2L;

    private final FakeTicketRepository repository = new FakeTicketRepository();
    private final GetTicketByIdService service = new GetTicketByIdService(repository);

    @Test
    void shouldReturnTicketOwnedByCustomer() {

        repository.given(ticket(10L, OWNER_ID));

        Ticket result = service.execute(10L, customer(OWNER_ID));

        assertEquals(10L, result.getId());
    }

    @Test
    void shouldRejectCustomerReadingAnotherCustomersTicket() {

        repository.given(ticket(10L, OWNER_ID));

        assertThrows(
                ForbiddenAccessException.class,
                () -> service.execute(10L, customer(OTHER_CUSTOMER_ID))
        );
    }

    @Test
    void shouldReturnTicketToStaff() {

        repository.given(ticket(10L, OWNER_ID));

        Ticket result = service.execute(10L, new AuthenticatedActor(99L, UserRole.AGENT));

        assertEquals(10L, result.getId());
    }

    @Test
    void shouldReturnTicketToAdmin() {

        repository.given(ticket(10L, OWNER_ID));

        Ticket result = service.execute(10L, new AuthenticatedActor(99L, UserRole.ADMIN));

        assertEquals(10L, result.getId());
    }

    private AuthenticatedActor customer(Long userId) {
        return new AuthenticatedActor(userId, UserRole.CUSTOMER);
    }

    private Ticket ticket(Long id, Long userId) {
        return new Ticket(id, "No puedo iniciar sesion", "El sistema rechaza mis credenciales",
                TicketStatus.OPEN, TicketPriority.HIGH, userId, Instant.parse("2026-01-15T10:00:00Z"), List.of());
    }
}
