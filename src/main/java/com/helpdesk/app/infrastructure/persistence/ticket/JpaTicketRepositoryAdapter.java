package com.helpdesk.app.infrastructure.persistence.ticket;

import com.helpdesk.app.application.port.out.TicketRepository;
import com.helpdesk.app.application.port.out.TicketSearchCriteria;
import com.helpdesk.app.domain.exception.TicketNotFoundException;
import com.helpdesk.app.domain.model.ticket.Ticket;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class JpaTicketRepositoryAdapter implements TicketRepository {

    private final SpringDataTicketRepository repository;

    public JpaTicketRepositoryAdapter(SpringDataTicketRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Ticket save(Ticket ticket) {
        TicketJpaEntity entity = TicketPersistenceMapper.toEntity(ticket);

        TicketJpaEntity savedEntity = repository.save(entity);

        return TicketPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Ticket findById(Long id) {
        TicketJpaEntity entity = repository.findById(id).orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + id));

        return TicketPersistenceMapper.toDomain(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> findAll(TicketSearchCriteria criteria) {


        Specification<TicketJpaEntity> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.userId() != null) {
                predicates.add(builder.equal(root.get("userId"), criteria.userId()));
            }

            if (criteria.status() != null) {
                predicates.add(builder.equal(root.get("status"), criteria.status()));
            }

            if (criteria.priority() != null) {
                predicates.add(builder.equal(root.get("priority"), criteria.priority()));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };

        return repository.findAll(specification, Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(TicketPersistenceMapper::toDomain)
                .toList();
    }
}
