package com.helpdesk.app.domain.model.ticket;

import com.helpdesk.app.domain.exception.InvalidTicketPriorityException;
import com.helpdesk.app.domain.exception.InvalidTicketStateException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TicketTest {

    private static final Instant CREATED_AT = Instant.parse("2026-01-15T10:00:00Z");
    private static final Instant STARTED_AT = Instant.parse("2026-01-15T11:30:00Z");
    private static final Instant RESOLVED_AT = Instant.parse("2026-01-15T14:45:00Z");
    private static final Instant CLOSED_AT = Instant.parse("2026-01-16T09:00:00Z");

    @Test
    void shouldCreateTicketWithOpenStatus() {
        Ticket ticket = newTicket(TicketPriority.HIGH, 1L, CREATED_AT);

        assertEquals(TicketStatus.OPEN, ticket.getStatus());
    }

    @Test
    void shouldCreateTicketWithoutAnyTransitionRecorded() {
        Ticket ticket = newTicket(TicketPriority.HIGH, 1L, CREATED_AT);

        assertTrue(ticket.getStatusHistory().isEmpty(), "La creacion no es una transicion de estado");
        assertEquals(CREATED_AT, ticket.getCreatedAt());
    }

    @Test
    void shouldStartProgressWhenTicketIsOpen() {
        Ticket ticket = newTicket(TicketPriority.HIGH, 1L, CREATED_AT);

        ticket.startProgress(STARTED_AT);

        assertEquals(TicketStatus.IN_PROGRESS, ticket.getStatus());
    }

    @Test
    void shouldResolveTicketWhenItIsInProgress() {
        Ticket ticket = newTicket(TicketPriority.HIGH, 1L, CREATED_AT);

        ticket.startProgress(STARTED_AT);
        ticket.resolve(RESOLVED_AT);

        assertEquals(TicketStatus.RESOLVED, ticket.getStatus());
    }

    @Test
    void shouldCloseTicketWhenItIsResolved() {
        Ticket ticket = newTicket(TicketPriority.HIGH, 1L, CREATED_AT);

        ticket.startProgress(STARTED_AT);
        ticket.resolve(RESOLVED_AT);
        ticket.close(CLOSED_AT);

        assertEquals(TicketStatus.CLOSED, ticket.getStatus());
    }

    @Test
    void shouldNotResolveOpenTicket() {
        Ticket ticket = newTicket(TicketPriority.HIGH, 1L, CREATED_AT);

        assertThrows(InvalidTicketStateException.class, () -> ticket.resolve(RESOLVED_AT));
    }

    @Test
    void shouldNotCloseOpenTicket() {
        Ticket ticket = newTicket(TicketPriority.HIGH, 1L, CREATED_AT);

        assertThrows(InvalidTicketStateException.class, () -> ticket.close(CLOSED_AT));
    }

    @Test
    void shouldRecordEveryTransitionWithItsOwnInstant() {
        Ticket ticket = newTicket(TicketPriority.HIGH, 1L, CREATED_AT);

        ticket.startProgress(STARTED_AT);
        ticket.resolve(RESOLVED_AT);
        ticket.close(CLOSED_AT);

        assertEquals(
                List.of(
                        new TicketStatusTransition(TicketStatus.OPEN, TicketStatus.IN_PROGRESS, STARTED_AT),
                        new TicketStatusTransition(TicketStatus.IN_PROGRESS, TicketStatus.RESOLVED, RESOLVED_AT),
                        new TicketStatusTransition(TicketStatus.RESOLVED, TicketStatus.CLOSED, CLOSED_AT)
                ),
                ticket.getStatusHistory()
        );
    }

    @Test
    void shouldNotRecordATransitionWhenTheStateChangeIsRejected() {
        Ticket ticket = newTicket(TicketPriority.HIGH, 1L, CREATED_AT);

        assertThrows(InvalidTicketStateException.class, () -> ticket.resolve(RESOLVED_AT));

        assertTrue(ticket.getStatusHistory().isEmpty(), "Una transicion rechazada no debe dejar rastro");
        assertEquals(TicketStatus.OPEN, ticket.getStatus());
    }

    @Test
    void shouldExposeAnUnmodifiableStatusHistory() {
        Ticket ticket = newTicket(TicketPriority.HIGH, 1L, CREATED_AT);

        ticket.startProgress(STARTED_AT);

        List<TicketStatusTransition> history = ticket.getStatusHistory();

        assertThrows(
                UnsupportedOperationException.class,
                () -> history.add(new TicketStatusTransition(TicketStatus.RESOLVED, TicketStatus.CLOSED, CLOSED_AT))
        );
    }

    @Test
    void shouldRestoreHistoryWhenRebuiltFromPersistence() {
        List<TicketStatusTransition> history = List.of(
                new TicketStatusTransition(TicketStatus.OPEN, TicketStatus.IN_PROGRESS, STARTED_AT)
        );

        Ticket ticket = new Ticket(
                9L, "No puedo iniciar sesion", "Rechaza mis credenciales",
                TicketStatus.IN_PROGRESS, TicketPriority.HIGH, 1L, CREATED_AT, history
        );

        assertEquals(history, ticket.getStatusHistory());
        assertEquals(CREATED_AT, ticket.getCreatedAt());
    }

    @Test
    void shouldNotShareTheHistoryListOfTheSourceWhenRebuiltFromPersistence() {
        List<TicketStatusTransition> history = new ArrayList<>();
        history.add(new TicketStatusTransition(TicketStatus.OPEN, TicketStatus.IN_PROGRESS, STARTED_AT));

        Ticket ticket = new Ticket(
                9L, "No puedo iniciar sesion", "Rechaza mis credenciales",
                TicketStatus.IN_PROGRESS, TicketPriority.HIGH, 1L, CREATED_AT, history
        );

        history.add(new TicketStatusTransition(TicketStatus.IN_PROGRESS, TicketStatus.RESOLVED, RESOLVED_AT));

        assertEquals(1, ticket.getStatusHistory().size(), "El agregado no debe compartir la lista que le pasan");
    }

    @Test
    void shouldChangePriority() {
        Ticket ticket = newTicket(TicketPriority.LOW, 1L, CREATED_AT);

        ticket.changePriority(TicketPriority.CRITICAL);

        assertEquals(TicketPriority.CRITICAL, ticket.getPriority());
    }

    @Test
    void shouldChangePriorityOfAResolvedTicketBecauseItIsNotClosedYet() {
        Ticket ticket = newTicket(TicketPriority.LOW, 1L, CREATED_AT);

        ticket.startProgress(STARTED_AT);
        ticket.resolve(RESOLVED_AT);

        ticket.changePriority(TicketPriority.CRITICAL);

        assertEquals(TicketPriority.CRITICAL, ticket.getPriority());
    }

    @Test
    void shouldNotChangePriorityToNull() {
        Ticket ticket = newTicket(TicketPriority.LOW, 1L, CREATED_AT);

        assertThrows(InvalidTicketPriorityException.class, () -> ticket.changePriority(null));

        assertEquals(TicketPriority.LOW, ticket.getPriority(), "La prioridad no debe cambiar si la operacion falla");
    }

    @Test
    void shouldNotChangePriorityOfAClosedTicket() {
        Ticket ticket = newTicket(TicketPriority.LOW, 1L, CREATED_AT);

        ticket.startProgress(STARTED_AT);
        ticket.resolve(RESOLVED_AT);
        ticket.close(CLOSED_AT);

        assertThrows(InvalidTicketStateException.class, () -> ticket.changePriority(TicketPriority.CRITICAL));

        assertEquals(TicketPriority.LOW, ticket.getPriority(), "La prioridad no debe cambiar si la operacion falla");
    }

    @Test
    void shouldRecognizeOwner() {
        Ticket ticket = newTicket(TicketPriority.HIGH, 7L, CREATED_AT);

        assertTrue(ticket.isOwnedBy(7L));
        assertFalse(ticket.isOwnedBy(8L));
    }

    @Test
    void shouldNotBeOwnedWhenUserIdIsNull() {
        Ticket ticket = newTicket(TicketPriority.HIGH, null, CREATED_AT);

        assertFalse(ticket.isOwnedBy(null), "Un ticket sin propietario no es de nadie, ni siquiera del actor sin id");
    }

    private Ticket newTicket(TicketPriority priority, Long userId, Instant createdAt) {
        return new Ticket("No puedo iniciar sesion", "El sistema rechaza mis credenciales", priority, userId, createdAt);
    }
}
