package com.helpdesk.app.application.usecase.ticket;

import com.helpdesk.app.application.port.out.TicketRepository;
import com.helpdesk.app.domain.exception.ForbiddenAccessException;
import com.helpdesk.app.domain.model.ticket.Ticket;
import org.springframework.stereotype.Service;

@Service
public class UpdateTicketPriorityService implements UpdateTicketPriorityUseCase {

    private final TicketRepository ticketRepository;

    public UpdateTicketPriorityService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Ticket execute(UpdateTicketPriorityCommand command) {

        if (!command.getActor().isStaff()) {
            throw new ForbiddenAccessException(
                    "Solo el personal del helpdesk puede cambiar la prioridad de un ticket");
        }

        Ticket ticket = ticketRepository.findById(command.getTicketId());

        ticket.changePriority(command.getPriority());

        return ticketRepository.save(ticket);
    }
}
