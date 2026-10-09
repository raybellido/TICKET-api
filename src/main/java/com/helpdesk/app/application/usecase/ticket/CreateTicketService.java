package com.helpdesk.app.application.usecase.ticket;

import com.helpdesk.app.application.port.out.TicketRepository;
import com.helpdesk.app.domain.model.ticket.Ticket;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class CreateTicketService implements CreateTicketUseCase {

    private final TicketRepository ticketRepository;
    private final Clock clock;

    public CreateTicketService(TicketRepository ticketRepository, Clock clock) {
        this.ticketRepository = ticketRepository;
        this.clock = clock;
    }

    @Override
    public Ticket execute(CreateTicketCommand command) {

        Ticket ticket = new Ticket(
                command.getTitle(),
                command.getDescription(),
                command.getPriority(),
                command.getUserId(),
                clock.instant()
        );

        return ticketRepository.save(ticket);
    }
}
