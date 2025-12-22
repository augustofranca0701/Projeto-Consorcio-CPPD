package com.consorcio.api.dto.PaymentDTO;

import java.time.OffsetDateTime;
import java.util.UUID;

public class PaymentCreateRequestDTO {

    private UUID userUuid;
    private Integer parcelaNumero;
    private Long valor;
    private OffsetDateTime dataVencimento;

    public UUID getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(UUID userUuid) {
        this.userUuid = userUuid;
    }

    public Integer getParcelaNumero() {
        return parcelaNumero;
    }

    public void setParcelaNumero(Integer parcelaNumero) {
        this.parcelaNumero = parcelaNumero;
    }

    public Long getValor() {
        return valor;
    }

    public void setValor(Long valor) {
        this.valor = valor;
    }

    public OffsetDateTime getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(OffsetDateTime dataVencimento) {
        this.dataVencimento = dataVencimento;
    }
}
