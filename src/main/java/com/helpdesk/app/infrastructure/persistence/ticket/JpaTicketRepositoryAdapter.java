package com.helpdesk.app.infrastructure.persistence.ticket;

import com.helpdesk.app.application.port.out.TicketRepository;
import com.helpdesk.app.domain.exception.TicketNotFoundException;
import com.helpdesk.app.domain.model.ticket.Ticket;
import org.springframework.stereotype.Component;

@Component
public class JpaTicketRepositoryAdapter implements TicketRepository {

    private final SpringDataTicketRepository repository;

    public JpaTicketRepositoryAdapter(SpringDataTicketRepository repository) {
        this.repository = repository;
    }

    @Override
    public Ticket save(Ticket ticket) {
        TicketJpaEntity entity = TicketPersistenceMapper.toEntity(ticket);

        TicketJpaEntity savedEntity = repository.save(entity);

        return TicketPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Ticket findById(Long id) {
        TicketJpaEntity entity = repository.findById(id).orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + id));

        return TicketPersistenceMapper.toDomain(entity);
    }
}

