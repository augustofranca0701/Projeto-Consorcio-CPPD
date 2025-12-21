package com.consorcio.api.controller.exception;

import com.consorcio.api.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Traduz exceções de domínio para respostas HTTP.
 *
 * Controllers não devem tratar regras de negócio.
 * Services não devem conhecer HTTP.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ForbiddenDomainException.class)
    public ResponseEntity<?> handleForbidden(ForbiddenDomainException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(NotFoundDomainException.class)
    public ResponseEntity<?> handleNotFound(NotFoundDomainException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ConflictDomainException.class)
    public ResponseEntity<?> handleConflict(ConflictDomainException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidStateDomainException.class)
    public ResponseEntity<?> handleInvalidState(InvalidStateDomainException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }

    /**
     * Falha inesperada — nunca exponha stacktrace.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "internal_server_error"));
    }
}
