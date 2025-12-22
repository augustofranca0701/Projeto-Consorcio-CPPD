package com.consorcio.api.dto.PaymentDTO;

import java.time.OffsetDateTime;
import java.util.UUID;

public class PaymentHistoryResponseDTO {

    private UUID paymentUuid;
    private String action;
    private Boolean oldValue;
    private Boolean newValue;
    private UUID performedBy;
    private OffsetDateTime performedAt;

    public PaymentHistoryResponseDTO(
            UUID paymentUuid,
            String action,
            Boolean oldValue,
            Boolean newValue,
            UUID performedBy,
            OffsetDateTime performedAt
    ) {
        this.paymentUuid = paymentUuid;
        this.action = action;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.performedBy = performedBy;
        this.performedAt = performedAt;
    }

    public UUID getPaymentUuid() {
        return paymentUuid;
    }

    public String getAction() {
        return action;
    }

    public Boolean getOldValue() {
        return oldValue;
    }

    public Boolean getNewValue() {
        return newValue;
    }

    public UUID getPerformedBy() {
        return performedBy;
    }

    public OffsetDateTime getPerformedAt() {
        return performedAt;
    }
}
