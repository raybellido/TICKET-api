package com.helpdesk.app.application.usecase.ticket;

import com.helpdesk.app.application.security.AuthenticatedActor;
import com.helpdesk.app.domain.model.ticket.Ticket;

public interface GetTicketByIdUseCase {

    Ticket execute(Long ticketId, AuthenticatedActor actor);
}
