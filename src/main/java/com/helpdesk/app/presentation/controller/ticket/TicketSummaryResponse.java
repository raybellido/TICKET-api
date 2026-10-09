package com.helpdesk.app.presentation.controller.ticket;

import com.helpdesk.app.domain.model.ticket.Ticket;
import com.helpdesk.app.domain.model.ticket.TicketPriority;
import com.helpdesk.app.domain.model.ticket.TicketStatus;

import java.time.Instant;

public class TicketSummaryResponse {

    private final Long id;
    private final String title;
    private final TicketStatus status;
    private final TicketPriority priority;
    private final Long userId;
    private final Instant createdAt;

    public TicketSummaryResponse(Long id, String title, TicketStatus status, TicketPriority priority, Long userId, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.priority = priority;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public static TicketSummaryResponse from(Ticket ticket) {
        return new TicketSummaryResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getUserId(),
                ticket.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
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

    public Instant getCreatedAt() {
        return createdAt;
    }
}
