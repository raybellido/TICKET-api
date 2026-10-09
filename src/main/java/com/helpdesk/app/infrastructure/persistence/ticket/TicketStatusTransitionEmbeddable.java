package com.helpdesk.app.infrastructure.persistence.ticket;

import com.helpdesk.app.domain.model.ticket.TicketStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

import java.time.Instant;

@Getter
@Embeddable
public class TicketStatusTransitionEmbeddable {

    @Column(name = "position", nullable = false)
    private int position;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", nullable = false)
    private TicketStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false)
    private TicketStatus toStatus;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    protected TicketStatusTransitionEmbeddable() {
    }

    public TicketStatusTransitionEmbeddable(int position, TicketStatus fromStatus, TicketStatus toStatus, Instant occurredAt) {
        this.position = position;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.occurredAt = occurredAt;
    }
}
