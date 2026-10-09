package com.helpdesk.app.application.usecase.ticket;

import com.helpdesk.app.application.port.out.TicketRepository;
import com.helpdesk.app.application.security.AuthenticatedActor;
import com.helpdesk.app.domain.exception.ForbiddenAccessException;
import com.helpdesk.app.domain.model.ticket.Ticket;
import org.springframework.stereotype.Service;

@Service
public class GetTicketByIdService implements GetTicketByIdUseCase {

    private final TicketRepository ticketRepository;

    public GetTicketByIdService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Ticket execute(Long ticketId, AuthenticatedActor actor) {

        Ticket ticket = ticketRepository.findById(ticketId);

        if (!actor.isStaff() && !ticket.isOwnedBy(actor.userId())) {
            throw new ForbiddenAccessException("Solo puedes consultar tus propios tickets");
        }

        return ticket;
    }
}
