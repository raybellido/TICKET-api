package com.helpdesk.app.application.usecase.ticket;

import com.helpdesk.app.application.port.out.TicketRepository;
import com.helpdesk.app.domain.model.ticket.Ticket;
import org.springframework.stereotype.Service;

@Service
public class CloseTicketService implements CloseTicketUseCase {

    private final TicketRepository ticketRepository;


    public CloseTicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Ticket execute(Long ticketId) {

        Ticket ticket = ticketRepository.findById(ticketId);

        ticket.close();

        return ticketRepository.save(ticket);
    }
}
