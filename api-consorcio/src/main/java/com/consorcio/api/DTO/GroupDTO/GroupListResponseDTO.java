package com.consorcio.api.dto.GroupDTO;

import com.consorcio.api.domain.enums.GroupStatus;

import java.util.UUID;

public class GroupListResponseDTO {

    private UUID uuid;
    private String name;
    private GroupStatus status;

    public GroupListResponseDTO(UUID uuid, String name, GroupStatus status) {
        this.uuid = uuid;
        this.name = name;
        this.status = status;
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public GroupStatus getStatus() { return status; }
}
