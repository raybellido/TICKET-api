package com.helpdesk.app.application.usecase.ticket;

import com.helpdesk.app.application.port.out.TicketRepository;
import com.helpdesk.app.application.port.out.TicketSearchCriteria;
import com.helpdesk.app.application.security.AuthenticatedActor;
import com.helpdesk.app.domain.model.ticket.Ticket;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListTicketsService implements ListTicketsUseCase {

    private final TicketRepository ticketRepository;

    public ListTicketsService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public List<Ticket> execute(ListTicketsCommand command, AuthenticatedActor actor) {

        Long effectiveUserId = actor.isStaff() ? command.userId() : actor.userId();

        TicketSearchCriteria effectiveCriteria = new TicketSearchCriteria(
                effectiveUserId,
                command.status(),
                command.priority()
        );

        return ticketRepository.findAll(effectiveCriteria);
    }
}
