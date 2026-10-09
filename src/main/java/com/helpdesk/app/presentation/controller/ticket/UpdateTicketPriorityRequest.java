package com.helpdesk.app.presentation.controller.ticket;

import com.helpdesk.app.domain.model.ticket.TicketPriority;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateTicketPriorityRequest {

    @NotNull
    private TicketPriority priority;

    public UpdateTicketPriorityRequest() {
    }

    public UpdateTicketPriorityRequest(TicketPriority priority) {
        this.priority = priority;
    }
}
