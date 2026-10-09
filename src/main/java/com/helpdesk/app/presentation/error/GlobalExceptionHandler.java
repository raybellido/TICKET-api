package com.helpdesk.app.presentation.error;

import com.helpdesk.app.domain.exception.ForbiddenAccessException;
import com.helpdesk.app.domain.exception.InvalidCredentialsException;
import com.helpdesk.app.domain.exception.InvalidEmailException;
import com.helpdesk.app.domain.exception.InvalidTicketPriorityException;
import com.helpdesk.app.domain.exception.InvalidTicketStateException;
import com.helpdesk.app.domain.exception.InvalidUserException;
import com.helpdesk.app.domain.exception.TicketNotFoundException;
import com.helpdesk.app.domain.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TicketNotFoundException.class)
    ResponseEntity<ApiError> handleTicketNotFound(TicketNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Ticket no encontrado", ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Usuario no encontrado", ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    ResponseEntity<ApiError> handleInvalidCredentials(InvalidCredentialsException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Credenciales invalidas", ex.getMessage());
    }

    @ExceptionHandler(ForbiddenAccessException.class)
    ResponseEntity<ApiError> handleForbidden(ForbiddenAccessException ex) {
        return build(HttpStatus.FORBIDDEN, "Acceso denegado", ex.getMessage());
    }

    @ExceptionHandler(InvalidTicketStateException.class)
    ResponseEntity<ApiError> handleInvalidTicketState(InvalidTicketStateException ex) {
        return build(HttpStatus.CONFLICT, "Estado de ticket invalido", ex.getMessage());
    }

    @ExceptionHandler({
            InvalidEmailException.class,
            InvalidUserException.class,
            InvalidTicketPriorityException.class
    })
    ResponseEntity<ApiError> handleDomainRuleViolation(RuntimeException ex) {
        return build(HttpStatus.BAD_REQUEST, "Peticion invalida", ex.getMessage());
    }

    @ExceptionHandler(BindException.class)
    ResponseEntity<ApiError> handleValidation(BindException ex) {

        Map<String, String> fieldErrors = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage()));

        ApiError body = new ApiError(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Peticion invalida",
                "La peticion contiene campos invalidos",
                fieldErrors
        );

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    ResponseEntity<ApiError> handleUnreadable(Exception ex) {
        return build(HttpStatus.BAD_REQUEST, "Peticion invalida", "El cuerpo o los parametros de la peticion no son validos");
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String error, String message) {
        return ResponseEntity.status(status)
                .body(new ApiError(Instant.now(), status.value(), error, message, null));
    }
}
