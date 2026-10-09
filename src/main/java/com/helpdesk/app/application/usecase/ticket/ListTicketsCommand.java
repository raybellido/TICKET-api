package com.helpdesk.app.application.usecase.ticket;

import com.helpdesk.app.domain.model.ticket.TicketPriority;
import com.helpdesk.app.domain.model.ticket.TicketStatus;

public record ListTicketsCommand(
        Long userId,
        TicketStatus status,
        TicketPriority priority
) {
}
