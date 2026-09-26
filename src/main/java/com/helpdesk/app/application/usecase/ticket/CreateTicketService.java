package com.helpdesk.app.application.usecase.ticket;

import com.helpdesk.app.application.port.out.TicketRepository;
import com.helpdesk.app.domain.model.ticket.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateTicketService implements CreateTicketUseCase {

    private final TicketRepository ticketRepository;

    public CreateTicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }


    @Override
    public Ticket execute(CreateTicketCommand command) {

       Ticket ticket = new Ticket(
                command.getTitle(),
                command.getDescription(),
                command.getPriority(),
                command.getUserId()
        );
        return ticketRepository.save(ticket);
    }

}
