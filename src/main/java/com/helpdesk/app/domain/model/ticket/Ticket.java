package com.helpdesk.app.domain.model.ticket;

import com.helpdesk.app.domain.exception.InvalidTicketPriorityException;
import com.helpdesk.app.domain.exception.InvalidTicketStateException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Ticket {

    private Long id;
    private String title;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private Long userId;
    private Instant createdAt;

    private final List<TicketStatusTransition> statusHistory = new ArrayList<>();

    public Ticket(String title, String description, TicketPriority priority, Long userId, Instant createdAt) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.userId = userId;
        this.createdAt = createdAt;
        this.status = TicketStatus.OPEN;
    }

    public Ticket(Long id, String title, String description, TicketStatus status, TicketPriority priority, Long userId, Instant createdAt, List<TicketStatusTransition> statusHistory) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.userId = userId;
        this.createdAt = createdAt;
        this.statusHistory.addAll(statusHistory);
    }

    public boolean isOwnedBy(Long userId) {
        return this.userId != null && this.userId.equals(userId);
    }

    public void changePriority(TicketPriority priority) {

        if (priority == null) {
            throw new InvalidTicketPriorityException("la prioridad del ticket no puede ser nula");
        }

        if (status == TicketStatus.CLOSED) {
            throw new InvalidTicketStateException("No se puede cambiar la prioridad de un ticket cerrado");
        }

        this.priority = priority;
    }

    public void startProgress(Instant now) {
        if (status != TicketStatus.OPEN) {
            throw new InvalidTicketStateException("Only open tickets can start progress");
        }

        transitionTo(TicketStatus.IN_PROGRESS, now);
    }

    public void resolve(Instant now) {
        if (status != TicketStatus.IN_PROGRESS) {
            throw new InvalidTicketStateException("Only in progress tickets can be resolved.");
        }

        transitionTo(TicketStatus.RESOLVED, now);
    }

    public void close(Instant now) {
        if (status != TicketStatus.RESOLVED) {
            throw new InvalidTicketStateException("Only resolved tickets can be closed.");
        }

        transitionTo(TicketStatus.CLOSED, now);
    }

    private void transitionTo(TicketStatus nextStatus, Instant now) {
        this.statusHistory.add(new TicketStatusTransition(this.status, nextStatus, now));
        this.status = nextStatus;
    }

    public TicketStatus getStatus() {
        return status;
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

    public TicketPriority getPriority() {
        return priority;
    }

    public Long getUserId() {
        return userId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<TicketStatusTransition> getStatusHistory() {
        return List.copyOf(statusHistory);
    }
}
