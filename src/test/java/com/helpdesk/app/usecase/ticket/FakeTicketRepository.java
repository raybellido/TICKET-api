package com.helpdesk.app.usecase.ticket;

import com.helpdesk.app.application.port.out.TicketRepository;
import com.helpdesk.app.application.port.out.TicketSearchCriteria;
import com.helpdesk.app.domain.exception.TicketNotFoundException;
import com.helpdesk.app.domain.model.ticket.Ticket;

import java.util.ArrayList;
import java.util.List;

public class FakeTicketRepository implements TicketRepository {

    private final List<Ticket> stored = new ArrayList<>();
    private TicketSearchCriteria lastCriteria;

    @Override
    public Ticket save(Ticket ticket) {
        stored.removeIf(existing -> existing.getId().equals(ticket.getId()));
        stored.add(ticket);
        return ticket;
    }

    @Override
    public Ticket findById(Long id) {
        return stored.stream()
                .filter(ticket -> ticket.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + id));
    }

    @Override
    public List<Ticket> findAll(TicketSearchCriteria criteria) {
        this.lastCriteria = criteria;

        return stored.stream()
                .filter(ticket -> criteria.userId() == null || ticket.isOwnedBy(criteria.userId()))
                .filter(ticket -> criteria.status() == null || ticket.getStatus() == criteria.status())
                .filter(ticket -> criteria.priority() == null || ticket.getPriority() == criteria.priority())
                .toList();
    }

    public void given(Ticket ticket) {
        stored.add(ticket);
    }

    public TicketSearchCriteria getLastCriteria() {
        return lastCriteria;
    }
}
