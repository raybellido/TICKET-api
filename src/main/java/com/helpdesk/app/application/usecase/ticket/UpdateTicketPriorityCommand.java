package com.helpdesk.app.application.usecase.ticket;

import com.helpdesk.app.application.security.AuthenticatedActor;
import com.helpdesk.app.domain.model.ticket.TicketPriority;

public class UpdateTicketPriorityCommand {

    private final Long ticketId;
    private final TicketPriority priority;
    private final AuthenticatedActor actor;

    public UpdateTicketPriorityCommand(Long ticketId, TicketPriority priority, AuthenticatedActor actor) {
        this.ticketId = ticketId;
        this.priority = priority;
        this.actor = actor;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public AuthenticatedActor getActor() {
        return actor;
    }
}
