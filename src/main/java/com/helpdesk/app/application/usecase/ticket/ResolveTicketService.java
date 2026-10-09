package com.helpdesk.app.application.usecase.ticket;

import com.helpdesk.app.application.port.out.TicketRepository;
import com.helpdesk.app.domain.model.ticket.Ticket;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class ResolveTicketService implements ResolveTicketUseCase {

    private final TicketRepository ticketRepository;
    private final Clock clock;

    public ResolveTicketService(TicketRepository ticketRepository, Clock clock) {
        this.ticketRepository = ticketRepository;
        this.clock = clock;
    }

    @Override
    public Ticket execute(Long ticketId) {

        Ticket ticket = ticketRepository.findById(ticketId);

        ticket.resolve(clock.instant());

        return ticketRepository.save(ticket);
    }
}
