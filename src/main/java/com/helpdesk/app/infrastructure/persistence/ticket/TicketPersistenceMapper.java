package com.helpdesk.app.infrastructure.persistence.ticket;

import com.helpdesk.app.domain.model.ticket.Ticket;

public class TicketPersistenceMapper {

    public static TicketJpaEntity toEntity(Ticket ticket) {

        return new TicketJpaEntity(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getUserId()
        );
    }

    public static Ticket toDomain(TicketJpaEntity entity) {

        return new Ticket(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getPriority(),
                entity.getUserId()
        );
    }
}
