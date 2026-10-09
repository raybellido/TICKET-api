package com.helpdesk.app.presentation.controller.ticket;

import com.helpdesk.app.domain.model.ticket.TicketPriority;
import com.helpdesk.app.domain.model.ticket.TicketStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListTicketsRequest {

    private Long userId;

    private TicketStatus status;

    private TicketPriority priority;

    public ListTicketsRequest() {
    }

    public ListTicketsRequest(Long userId, TicketStatus status, TicketPriority priority) {
        this.userId = userId;
        this.status = status;
        this.priority = priority;
    }
}
