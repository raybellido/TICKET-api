package com.helpdesk.app.presentation.controller.ticket;

import com.helpdesk.app.domain.model.ticket.TicketPriority;
import com.helpdesk.app.domain.model.ticket.TicketStatus;

public class TicketResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final TicketStatus status;
    private final TicketPriority priority;
    private final Long userId;

    public TicketResponse(Long id, String title, String description, TicketStatus status, TicketPriority priority, Long userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public Long getUserId() {
        return userId;
    }
}
