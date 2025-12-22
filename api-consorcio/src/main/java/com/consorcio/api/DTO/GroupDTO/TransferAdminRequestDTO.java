package com.consorcio.api.dto.GroupDTO;

import java.util.UUID;

public class TransferAdminRequestDTO {

    private UUID newAdminUuid;

    public UUID getNewAdminUuid() {
        return newAdminUuid;
    }

    public void setNewAdminUuid(UUID newAdminUuid) {
        this.newAdminUuid = newAdminUuid;
    }
}
