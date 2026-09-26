package com.helpdesk.app.domain.model.ticket;

import com.helpdesk.app.domain.exception.InvalidTicketStateException;


public class Ticket {

    private Long id;
    private String title;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private Long userId;


    public Ticket(String title, String description, TicketPriority priority, Long userId) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.userId = userId;
        this.status = TicketStatus.OPEN;

    }


    public Ticket(Long id, String title, String description, TicketStatus status, TicketPriority priority, Long userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.userId = userId;
    }

    public Ticket(String noPuedoIniciarSesión, String elSistemaRechazaMisCredenciales, TicketPriority ticketPriority) {
    }

    public void changePriority(TicketPriority priority) {
        this.priority = priority;
    }

    public void startProgress() {
        if (status != TicketStatus.OPEN) {
            throw new InvalidTicketStateException("Only open tickets can start progress");
        }

        this.status = TicketStatus.IN_PROGRESS;
    }

    public void resolve() {
        if (status != TicketStatus.IN_PROGRESS) {
            throw new InvalidTicketStateException("Only in progress tickets can be resolved.");
        }

        this.status = TicketStatus.RESOLVED;

    }

    public void close() {
        if (status != TicketStatus.RESOLVED) {
            throw new InvalidTicketStateException("Only resolved tickets can be closed.");
        }

        this.status = TicketStatus.CLOSED;
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
}
