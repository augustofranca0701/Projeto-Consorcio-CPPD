package com.consorcio.api.domain.exception;

/**
 * Usada quando o usuário autenticado
 * não possui permissão para executar
 * uma ação de domínio.
 */
public class ForbiddenDomainException extends DomainException {

    public ForbiddenDomainException(String message) {
        super(message);
    }
}
