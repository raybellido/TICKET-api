package com.helpdesk.app.presentation.controller.ticket;

import com.helpdesk.app.domain.model.ticket.TicketStatus;
import com.helpdesk.app.domain.model.ticket.TicketStatusTransition;

import java.time.Instant;

public class TicketStatusTransitionResponse {

    private final TicketStatus fromStatus;
    private final TicketStatus toStatus;
    private final Instant occurredAt;

    public TicketStatusTransitionResponse(TicketStatus fromStatus, TicketStatus toStatus, Instant occurredAt) {
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.occurredAt = occurredAt;
    }

    public static TicketStatusTransitionResponse from(TicketStatusTransition transition) {
        return new TicketStatusTransitionResponse(
                transition.fromStatus(),
                transition.toStatus(),
                transition.occurredAt()
        );
    }

    public TicketStatus getFromStatus() {
        return fromStatus;
    }

    public TicketStatus getToStatus() {
        return toStatus;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
