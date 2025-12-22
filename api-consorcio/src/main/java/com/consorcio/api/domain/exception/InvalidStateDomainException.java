package com.consorcio.api.domain.exception;

/**
 * Usada quando uma operação é tentada
 * em um estado inválido do domínio.
 *
 * Ex: criar pagamento em grupo CRIADO,
 * sair de grupo ATIVO, finalizar grupo CRIADO.
 */
public class InvalidStateDomainException extends DomainException {

    public InvalidStateDomainException(String message) {
        super(message);
    }
}
