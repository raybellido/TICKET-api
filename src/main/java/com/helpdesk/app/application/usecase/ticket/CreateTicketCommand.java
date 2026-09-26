package com.helpdesk.app.application.usecase.ticket;

import com.helpdesk.app.domain.model.ticket.TicketPriority;

public class CreateTicketCommand {


    private final String title;
    private final String description;
    private final TicketPriority priority;
    private final Long userId;

    public CreateTicketCommand(String title, String description, TicketPriority priority, Long userId) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public Long getUserId() {
        return userId;
    }
}
