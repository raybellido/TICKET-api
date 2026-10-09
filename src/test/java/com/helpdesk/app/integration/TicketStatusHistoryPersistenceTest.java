package com.helpdesk.app.integration;

import com.helpdesk.app.application.port.out.TicketRepository;
import com.helpdesk.app.application.port.out.TicketSearchCriteria;
import com.helpdesk.app.domain.exception.InvalidTicketStateException;
import com.helpdesk.app.domain.model.ticket.Ticket;
import com.helpdesk.app.domain.model.ticket.TicketPriority;
import com.helpdesk.app.domain.model.ticket.TicketStatus;
import com.helpdesk.app.domain.model.ticket.TicketStatusTransition;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TicketStatusHistoryPersistenceTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldPersistCreatedAtAndStartWithAnEmptyHistory() {
        Ticket ticket = ticketRepository.save(newTicket());

        reload();

        Ticket found = ticketRepository.findById(ticket.getId());

        assertTrue(found.getStatusHistory().isEmpty());
        assertEquals(TicketStatus.OPEN, found.getStatus());
        assertTrue(found.getCreatedAt() != null, "createdAt debe persistirse");
    }

    @Test
    void shouldPersistASingleTransition() {
        Instant now = Instant.parse("2026-02-01T10:00:00Z");

        Ticket ticket = ticketRepository.save(newTicket());
        ticket.startProgress(now);
        ticketRepository.save(ticket);

        reload();

        assertEquals(
                List.of(new TicketStatusTransition(TicketStatus.OPEN, TicketStatus.IN_PROGRESS, now)),
                ticketRepository.findById(ticket.getId()).getStatusHistory()
        );
    }

    @Test
    void shouldPersistTheWholeLifecycleInOrderAcrossSeparateSaves() {
        Instant startedAt = Instant.parse("2026-02-01T10:00:00Z");
        Instant resolvedAt = Instant.parse("2026-02-01T14:00:00Z");
        Instant closedAt = Instant.parse("2026-02-02T09:00:00Z");

        Ticket ticket = ticketRepository.save(newTicket());

        ticket.startProgress(startedAt);
        ticketRepository.save(ticket);

        ticket.resolve(resolvedAt);
        ticketRepository.save(ticket);

        ticket.close(closedAt);
        ticketRepository.save(ticket);

        reload();

        assertEquals(
                List.of(
                        new TicketStatusTransition(TicketStatus.OPEN, TicketStatus.IN_PROGRESS, startedAt),
                        new TicketStatusTransition(TicketStatus.IN_PROGRESS, TicketStatus.RESOLVED, resolvedAt),
                        new TicketStatusTransition(TicketStatus.RESOLVED, TicketStatus.CLOSED, closedAt)
                ),
                ticketRepository.findById(ticket.getId()).getStatusHistory()
        );
    }

    @Test
    void shouldKeepTheOrderWhenEveryTransitionSharesTheSameInstant() {
        Instant same = Instant.parse("2026-02-01T10:00:00Z");

        Ticket ticket = ticketRepository.save(newTicket());
        ticket.startProgress(same);
        ticket.resolve(same);
        ticket.close(same);
        ticketRepository.save(ticket);

        reload();

        assertEquals(
                List.of(
                        new TicketStatusTransition(TicketStatus.OPEN, TicketStatus.IN_PROGRESS, same),
                        new TicketStatusTransition(TicketStatus.IN_PROGRESS, TicketStatus.RESOLVED, same),
                        new TicketStatusTransition(TicketStatus.RESOLVED, TicketStatus.CLOSED, same)
                ),
                ticketRepository.findById(ticket.getId()).getStatusHistory(),
                "El orden no puede depender de la precision del instante"
        );
    }

    @Test
    void shouldNotDuplicateTransitionsWhenTheSameStateIsSavedTwice() {
        Instant now = Instant.parse("2026-02-01T10:00:00Z");

        Ticket ticket = ticketRepository.save(newTicket());
        ticket.startProgress(now);
        ticketRepository.save(ticket);
        ticketRepository.save(ticket);

        reload();

        assertEquals(1, ticketRepository.findById(ticket.getId()).getStatusHistory().size());
    }

    @Test
    void shouldNotPersistATransitionThatWasRejected() {
        Ticket ticket = ticketRepository.save(newTicket());

        assertThrows(InvalidTicketStateException.class, () -> ticket.resolve(Instant.parse("2026-02-01T10:00:00Z")));
        ticketRepository.save(ticket);

        reload();

        assertTrue(ticketRepository.findById(ticket.getId()).getStatusHistory().isEmpty());
    }

    @Test
    void shouldNotLoseTheHistoryOfATicketReturnedByTheSearch() {
        Ticket ticket = ticketRepository.save(newTicket());
        ticket.startProgress(Instant.parse("2026-02-01T10:00:00Z"));
        ticketRepository.save(ticket);

        reload();

        List<Ticket> found = ticketRepository.findAll(
                new TicketSearchCriteria(ticket.getUserId(), null, null)
        );

        Ticket mine = found.stream()
                .filter(candidate -> candidate.getId().equals(ticket.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("El ticket guardado deberia aparecer en el listado"));

        assertEquals(1, mine.getStatusHistory().size());
        assertEquals(TicketStatus.IN_PROGRESS, mine.getStatus());
    }

    private void reload() {
        entityManager.flush();
        entityManager.clear();
    }

    private Ticket newTicket() {
        return new Ticket(
                "No puedo iniciar sesion",
                "El sistema rechaza mis credenciales",
                TicketPriority.HIGH,
                1L,
                Instant.parse("2026-02-01T09:00:00Z")
        );
    }
}
