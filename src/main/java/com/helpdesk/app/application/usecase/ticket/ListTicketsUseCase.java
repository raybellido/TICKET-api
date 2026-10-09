package com.helpdesk.app.application.usecase.ticket;

import com.helpdesk.app.application.security.AuthenticatedActor;
import com.helpdesk.app.domain.model.ticket.Ticket;

import java.util.List;

public interface ListTicketsUseCase {

    List<Ticket> execute(ListTicketsCommand command, AuthenticatedActor actor);
}
