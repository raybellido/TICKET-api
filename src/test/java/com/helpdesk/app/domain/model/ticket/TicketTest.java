package com.helpdesk.app.domain.model.ticket;

import com.helpdesk.app.domain.exception.InvalidTicketStateException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class TicketTest {

    @Test
    void shouldCreateTicketWithOpenStatus() {
        // Given
        Ticket ticket = new Ticket(
                "No puedo iniciar sesión",
                "El sistema rechaza mis credenciales",
                TicketPriority.HIGH,
                1l
        );

        assertEquals(TicketStatus.OPEN, ticket.getStatus());
    }

    @Test
    void shouldStartProgressWhenTicketIsOpen() {
        // Given
        Ticket ticket = new Ticket(
                "No puedo iniciar sesión",
                "El sistema rechaza mis credenciales",
                TicketPriority.HIGH,
        1l
        );
          ticket.startProgress();

        assertEquals(TicketStatus.IN_PROGRESS, ticket.getStatus(), "El ticket debería haber cambiado su estado a IN_PROGRESS");
    }


    @Test
    void shouldResolveTicketWhenItIsInProgress() {

        Ticket ticket = new Ticket(
                "No puedo iniciar sesión",
                "El sistema rechaza mis credenciales",
                TicketPriority.HIGH,1l
        );
        ticket.startProgress();
        ticket.resolve();


        assertEquals(TicketStatus.RESOLVED, ticket.getStatus(), "El ticket debería haber cambiado su estado a Resolved");
    }


    @Test
    void shouldCloseTicketWhenItIsResolved() {

        Ticket ticket = new Ticket(
                "No puedo iniciar sesión",
                "El sistema rechaza mis credenciales",
                TicketPriority.HIGH,1l
        );
        ticket.startProgress();
        ticket.resolve();
        ticket.close();

        assertEquals(TicketStatus.CLOSED, ticket.getStatus(), "El ticket debería haber cambiado su estado a Closed");
    }


    @Test
    void shouldNotResolveOpenTicket() {
        Ticket ticket = new Ticket(
                "No puedo iniciar sesión",
                "El sistema rechaza mis credenciales",
                TicketPriority.HIGH,
        1l
        );

        assertThrows(
                InvalidTicketStateException.class,
                ticket::resolve
        );
    }

    @Test
    void shouldNotCloseOpenTicket() {
        Ticket ticket = new Ticket(
                "No puedo iniciar sesión",
                "El sistema rechaza mis credenciales",
                TicketPriority.HIGH,1l
        );

        assertThrows(
                InvalidTicketStateException.class,
                ticket::close
        );
    }





}
