package com.helpdesk.app.presentation.controller.ticket;

import com.helpdesk.app.application.usecase.ticket.*;
import com.helpdesk.app.domain.model.ticket.Ticket;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final CreateTicketUseCase createTicketUseCase;
    private final StartTicketProgressUseCase startTicketProgressUseCase;
    private final ResolveTicketUseCase resolveTicketUseCase;
    private final CloseTicketUseCase closeTicketUseCase;

    public TicketController(CreateTicketUseCase createTicketUseCase, StartTicketProgressUseCase startTicketProgressUseCase, ResolveTicketUseCase resolveTicketUseCase, CloseTicketUseCase closeTicketUseCase) {
        this.createTicketUseCase = createTicketUseCase;
        this.startTicketProgressUseCase = startTicketProgressUseCase;
        this.resolveTicketUseCase = resolveTicketUseCase;
        this.closeTicketUseCase = closeTicketUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse create(@Valid @RequestBody CreateTicketRequest request) {

        CreateTicketCommand command = new CreateTicketCommand(request.getTitle(), request.getDescription(), request.getPriority(), request.getUserId());

        Ticket ticket = createTicketUseCase.execute(command);

        return new TicketResponse(ticket.getId(), ticket.getTitle(), ticket.getDescription(), ticket.getStatus(), ticket.getPriority(), ticket.getUserId());
    }

    @PostMapping("/{id}/start")
    public TicketResponse start(@PathVariable Long id) {

        Ticket ticket = startTicketProgressUseCase.execute(id);

        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getUserId()
        );
    }

    @PostMapping("/{id}/resolve")
    public TicketResponse resolve(@PathVariable Long id) {

        Ticket ticket = resolveTicketUseCase.execute(id);

        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getUserId()
        );
    }

    @PostMapping("/{id}/close")
    public TicketResponse close(@PathVariable Long id) {

        Ticket ticket = closeTicketUseCase.execute(id);

        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getUserId()
        );
    }
}
