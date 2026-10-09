package com.helpdesk.app.infrastructure.persistence.ticket;

import com.helpdesk.app.domain.model.ticket.Ticket;
import com.helpdesk.app.domain.model.ticket.TicketStatusTransition;

import java.util.ArrayList;
import java.util.List;

public class TicketPersistenceMapper {

    public static TicketJpaEntity toEntity(Ticket ticket) {

        return new TicketJpaEntity(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getUserId(),
                ticket.getCreatedAt(),
                toEmbeddables(ticket.getStatusHistory())
        );
    }

    public static Ticket toDomain(TicketJpaEntity entity) {

        return new Ticket(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getPriority(),
                entity.getUserId(),
                entity.getCreatedAt(),
                toDomain(entity.getStatusHistory())
        );
    }

    private static List<TicketStatusTransitionEmbeddable> toEmbeddables(List<TicketStatusTransition> history) {

        List<TicketStatusTransitionEmbeddable> embeddables = new ArrayList<>(history.size());

        for (int position = 0; position < history.size(); position++) {
            TicketStatusTransition transition = history.get(position);

            embeddables.add(new TicketStatusTransitionEmbeddable(
                    position,
                    transition.fromStatus(),
                    transition.toStatus(),
                    transition.occurredAt()
            ));
        }

        return embeddables;
    }

    private static List<TicketStatusTransition> toDomain(List<TicketStatusTransitionEmbeddable> embeddables) {

        return embeddables.stream()
                .map(embeddable -> new TicketStatusTransition(
                        embeddable.getFromStatus(),
                        embeddable.getToStatus(),
                        embeddable.getOccurredAt()
                ))
                .toList();
    }
}
