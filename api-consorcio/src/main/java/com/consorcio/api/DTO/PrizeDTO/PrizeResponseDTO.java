package com.consorcio.api.dto.PrizeDTO;

import com.consorcio.api.model.PrizeModel;

import java.time.LocalDate;
import java.util.UUID;

public class PrizeResponseDTO {

    private UUID uuid;
    private UUID userUuid;
    private UUID groupUuid;
    private LocalDate datePrize;

    public static PrizeResponseDTO from(PrizeModel prize) {
        PrizeResponseDTO dto = new PrizeResponseDTO();
        dto.uuid = prize.getUuid();
        dto.userUuid = prize.getUser().getUuid();
        dto.groupUuid = prize.getGroup().getUuid();
        dto.datePrize = prize.getDatePrize();
        return dto;
    }

    public UUID getUuid() {
        return uuid;
    }

    public UUID getUserUuid() {
        return userUuid;
    }

    public UUID getGroupUuid() {
        return groupUuid;
    }

    public LocalDate getDatePrize() {
        return datePrize;
    }
}
