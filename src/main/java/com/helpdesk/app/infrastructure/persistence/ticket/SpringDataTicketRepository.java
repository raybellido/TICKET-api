package com.helpdesk.app.infrastructure.persistence.ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SpringDataTicketRepository
        extends JpaRepository<TicketJpaEntity, Long>, JpaSpecificationExecutor<TicketJpaEntity> {
}
