package com.helpdesk.app.presentation.controller.ticket;

import com.helpdesk.app.domain.model.ticket.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateTicketRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private TicketPriority priority;

    private Long userId;

    public CreateTicketRequest() {
    }

}
