package com.consorcio.api.domain.exception;

/**
 * Usada quando uma regra de negócio
 * impede a operação devido a conflito
 * de estado ou duplicidade.
 *
 * Ex: usuário já está no grupo,
 * pagamento já existe, grupo cheio.
 */
public class ConflictDomainException extends DomainException {

    public ConflictDomainException(String message) {
        super(message);
    }
}
