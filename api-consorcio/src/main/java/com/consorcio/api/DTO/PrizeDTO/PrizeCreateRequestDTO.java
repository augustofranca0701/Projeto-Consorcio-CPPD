package com.consorcio.api.dto.PrizeDTO;

import java.time.LocalDate;
import java.util.UUID;

public class PrizeCreateRequestDTO {

    private UUID userUuid;
    private LocalDate datePrize;

    public UUID getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(UUID userUuid) {
        this.userUuid = userUuid;
    }

    public LocalDate getDatePrize() {
        return datePrize;
    }

    public void setDatePrize(LocalDate datePrize) {
        this.datePrize = datePrize;
    }
}
