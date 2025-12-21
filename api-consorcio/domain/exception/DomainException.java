package com.consorcio.api.domain.exception;

/**
 * Exceção base do domínio.
 *
 * Nunca deve carregar conceitos HTTP.
 * Nunca deve ser capturada dentro do domínio.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
}
