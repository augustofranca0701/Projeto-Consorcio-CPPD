package com.consorcio.api.domain.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Handler global de exceções.
 *
 * IMPORTANTE:
 * - Em DEV: NUNCA mascarar exceção
 * - Em PROD: pode trocar mensagens genéricas
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================
    // ERROS DE DOMÍNIO
    // =========================

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
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    // =========================
    // ERROS DE BANCO / JPA
    // =========================

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrity(DataIntegrityViolationException ex) {
        ex.printStackTrace(); // 🔥 ESSENCIAL EM DEV

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "data_integrity_violation",
                        "message", ex.getMostSpecificCause().getMessage()
                ));
    }

    // =========================
    // FALLBACK (DEV)
    // =========================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        ex.printStackTrace(); // 🔥 SEM ISSO VOCÊ ESTÁ VOANDO CEGO

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "internal_server_error",
                        "exception", ex.getClass().getSimpleName(),
                        "message", ex.getMessage()
                ));
    }
}
