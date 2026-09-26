package com.helpdesk.app.application.usecase.ticket;

import com.helpdesk.app.domain.model.ticket.Ticket;

public interface CreateTicketUseCase {

    Ticket execute(CreateTicketCommand command);
}
