package com.helpdesk.app.application.usecase.ticket;

import com.helpdesk.app.domain.model.ticket.Ticket;

public interface UpdateTicketPriorityUseCase {

    Ticket execute(UpdateTicketPriorityCommand command);
}
