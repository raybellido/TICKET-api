package com.helpdesk.app.domain.model.ticket;

import java.time.Instant;

public record TicketStatusTransition(
        TicketStatus fromStatus,
        TicketStatus toStatus,
        Instant occurredAt
) {
}
