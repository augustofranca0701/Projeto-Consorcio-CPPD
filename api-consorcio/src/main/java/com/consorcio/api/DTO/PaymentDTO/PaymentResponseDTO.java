package com.consorcio.api.dto.PaymentDTO;

import com.consorcio.api.model.PaymentModel;

import java.time.OffsetDateTime;
import java.util.UUID;

public class PaymentResponseDTO {

    private UUID uuid;
    private UUID userUuid;
    private Integer parcelaNumero;
    private Long valor;
    private Boolean isPaid;
    private OffsetDateTime paidAt;

    public static PaymentResponseDTO from(PaymentModel payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.uuid = payment.getUuid();
        dto.userUuid = payment.getUser().getUuid();
        dto.parcelaNumero = payment.getParcelaNumero();
        dto.valor = payment.getValor();
        dto.isPaid = payment.getIsPaid();
        dto.paidAt = payment.getPaidAt();
        return dto;
    }

    public UUID getUuid() {
        return uuid;
    }

    public UUID getUserUuid() {
        return userUuid;
    }

    public Integer getParcelaNumero() {
        return parcelaNumero;
    }

    public Long getValor() {
        return valor;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public OffsetDateTime getPaidAt() {
        return paidAt;
    }
}
