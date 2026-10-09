package com.helpdesk.app.application.port.out;

import com.helpdesk.app.domain.model.ticket.TicketPriority;
import com.helpdesk.app.domain.model.ticket.TicketStatus;

public record TicketSearchCriteria(
        Long userId,
        TicketStatus status,
        TicketPriority priority
) {
}
