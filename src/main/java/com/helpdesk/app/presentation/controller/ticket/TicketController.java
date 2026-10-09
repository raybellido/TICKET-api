package com.helpdesk.app.presentation.controller.ticket;

import com.helpdesk.app.application.security.AuthenticatedActor;
import com.helpdesk.app.application.usecase.ticket.CloseTicketUseCase;
import com.helpdesk.app.application.usecase.ticket.CreateTicketCommand;
import com.helpdesk.app.application.usecase.ticket.CreateTicketUseCase;
import com.helpdesk.app.application.usecase.ticket.GetTicketByIdUseCase;
import com.helpdesk.app.application.usecase.ticket.ListTicketsCommand;
import com.helpdesk.app.application.usecase.ticket.ListTicketsUseCase;
import com.helpdesk.app.application.usecase.ticket.ResolveTicketUseCase;
import com.helpdesk.app.application.usecase.ticket.StartTicketProgressUseCase;
import com.helpdesk.app.application.usecase.ticket.UpdateTicketPriorityCommand;
import com.helpdesk.app.application.usecase.ticket.UpdateTicketPriorityUseCase;
import com.helpdesk.app.domain.model.ticket.Ticket;
import com.helpdesk.app.domain.model.ticket.TicketPriority;
import com.helpdesk.app.domain.model.ticket.TicketStatus;
import com.helpdesk.app.infrastructure.security.AuthenticatedActorPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final CreateTicketUseCase createTicketUseCase;
    private final GetTicketByIdUseCase getTicketByIdUseCase;
    private final ListTicketsUseCase listTicketsUseCase;
    private final UpdateTicketPriorityUseCase updateTicketPriorityUseCase;
    private final StartTicketProgressUseCase startTicketProgressUseCase;
    private final ResolveTicketUseCase resolveTicketUseCase;
    private final CloseTicketUseCase closeTicketUseCase;

    public TicketController(CreateTicketUseCase createTicketUseCase, GetTicketByIdUseCase getTicketByIdUseCase, ListTicketsUseCase listTicketsUseCase, UpdateTicketPriorityUseCase updateTicketPriorityUseCase, StartTicketProgressUseCase startTicketProgressUseCase, ResolveTicketUseCase resolveTicketUseCase, CloseTicketUseCase closeTicketUseCase) {
        this.createTicketUseCase = createTicketUseCase;
        this.getTicketByIdUseCase = getTicketByIdUseCase;
        this.listTicketsUseCase = listTicketsUseCase;
        this.updateTicketPriorityUseCase = updateTicketPriorityUseCase;
        this.startTicketProgressUseCase = startTicketProgressUseCase;
        this.resolveTicketUseCase = resolveTicketUseCase;
        this.closeTicketUseCase = closeTicketUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse create(@Valid @RequestBody CreateTicketRequest request, @AuthenticationPrincipal AuthenticatedActorPrincipal principal) {

        CreateTicketCommand command = new CreateTicketCommand(request.getTitle(), request.getDescription(), request.getPriority(), resolveOwnerId(request.getUserId(), principal.toActor()));

        return TicketResponse.from(createTicketUseCase.execute(command));
    }

    @GetMapping("/{id}")
    public TicketResponse getById(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedActorPrincipal principal) {
        return TicketResponse.from(getTicketByIdUseCase.execute(id, principal.toActor()));
    }
    
    @GetMapping
    public List<TicketSummaryResponse> list(@Valid ListTicketsRequest request, @AuthenticationPrincipal AuthenticatedActorPrincipal principal) {

        List<Ticket> tickets = listTicketsUseCase.execute(new ListTicketsCommand(request.getUserId(), request.getStatus(), request.getPriority()), principal.toActor());

        return tickets.stream().map(TicketSummaryResponse::from).toList();
    }

    @PatchMapping("/{id}/priority")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public TicketResponse updatePriority(@PathVariable Long id, @Valid @RequestBody UpdateTicketPriorityRequest request, @AuthenticationPrincipal AuthenticatedActorPrincipal principal) {

        UpdateTicketPriorityCommand command = new UpdateTicketPriorityCommand(id, request.getPriority(), principal.toActor());

        return TicketResponse.from(updateTicketPriorityUseCase.execute(command));
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public TicketResponse start(@PathVariable Long id) {
        return TicketResponse.from(startTicketProgressUseCase.execute(id));
    }

    @PostMapping("/{id}/resolve")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public TicketResponse resolve(@PathVariable Long id) {
        return TicketResponse.from(resolveTicketUseCase.execute(id));
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public TicketResponse close(@PathVariable Long id) {
        return TicketResponse.from(closeTicketUseCase.execute(id));
    }

    private Long resolveOwnerId(Long requestedUserId, AuthenticatedActor actor) {
        if (actor.isStaff() && requestedUserId != null) {
            return requestedUserId;
        }
        return actor.userId();
    }
}
