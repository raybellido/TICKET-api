package com.helpdesk.app.infrastructure.persistence.ticket;

import com.helpdesk.app.domain.model.ticket.TicketPriority;
import com.helpdesk.app.domain.model.ticket.TicketStatus;
import com.helpdesk.app.domain.model.ticket.TicketStatusTransition;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @ElementCollection
    @CollectionTable(name = "ticket_status_history", joinColumns = @JoinColumn(name = "ticket_id"))
    @OrderBy("position ASC")
    private List<TicketStatusTransitionEmbeddable> statusHistory = new ArrayList<>();

    protected TicketJpaEntity() {
    }

    public TicketJpaEntity(
            Long id,
            String title,
            String description,
            TicketStatus status,
            TicketPriority priority,
            Long userId,
            Instant createdAt,
            List<TicketStatusTransitionEmbeddable> statusHistory
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.userId = userId;
        this.createdAt = createdAt;
        this.statusHistory.addAll(statusHistory);
    }

    public void replaceStatusHistory(List<TicketStatusTransitionEmbeddable> statusHistory) {
        this.statusHistory.clear();
        this.statusHistory.addAll(statusHistory);
    }
}
