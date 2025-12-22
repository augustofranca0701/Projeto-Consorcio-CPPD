package com.consorcio.api.domain.exception;

/**
 * Usada quando uma entidade do domínio
 * não é encontrada ou não é visível
 * para o usuário no contexto atual.
 *
 * Ex: grupo inexistente ou privado
 * sem participação.
 */
public class NotFoundDomainException extends DomainException {

    public NotFoundDomainException(String message) {
        super(message);
    }
}
