package com.helpdesk.app.infrastructure.persistence.ticket;

import com.helpdesk.app.domain.model.ticket.TicketPriority;
import com.helpdesk.app.domain.model.ticket.TicketStatus;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "tickets")
public class TicketJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Enumerated(EnumType.STRING)
    private TicketPriority priority;

    private Long userId;

    protected TicketJpaEntity() {
    }

    public TicketJpaEntity(Long id, String title, String description, TicketStatus status, TicketPriority priority, Long userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.userId = userId;
    }


}
