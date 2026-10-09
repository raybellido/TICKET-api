package com.helpdesk.app.presentation.controller.ticket;

import com.helpdesk.app.domain.model.ticket.Ticket;
import com.helpdesk.app.domain.model.ticket.TicketPriority;
import com.helpdesk.app.domain.model.ticket.TicketStatus;

import java.time.Instant;
import java.util.List;

public class TicketResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final TicketStatus status;
    private final TicketPriority priority;
    private final Long userId;
    private final Instant createdAt;
    private final List<TicketStatusTransitionResponse> statusHistory;

    public TicketResponse(
            Long id,
            String title,
            String description,
            TicketStatus status,
            TicketPriority priority,
            Long userId,
            Instant createdAt,
            List<TicketStatusTransitionResponse> statusHistory
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.userId = userId;
        this.createdAt = createdAt;
        this.statusHistory = statusHistory;
    }

    public static TicketResponse from(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getUserId(),
                ticket.getCreatedAt(),
                ticket.getStatusHistory().stream()
                        .map(TicketStatusTransitionResponse::from)
                        .toList()
        );
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<TicketStatusTransitionResponse> getStatusHistory() {
        return statusHistory;
    }
}
