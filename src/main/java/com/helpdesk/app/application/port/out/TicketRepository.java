package com.helpdesk.app.application.port.out;

import com.helpdesk.app.domain.model.ticket.Ticket;

public interface TicketRepository {
    Ticket save(Ticket ticket);

    Ticket findById(Long id);
}
