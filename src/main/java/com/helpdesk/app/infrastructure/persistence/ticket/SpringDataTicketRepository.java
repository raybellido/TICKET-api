package com.helpdesk.app.infrastructure.persistence.ticket;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTicketRepository extends JpaRepository<TicketJpaEntity, Long> {
}
