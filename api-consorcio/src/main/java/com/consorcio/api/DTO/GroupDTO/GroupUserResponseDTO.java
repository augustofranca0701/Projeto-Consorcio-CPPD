package com.consorcio.api.dto.GroupDTO;

import java.time.OffsetDateTime;
import java.util.UUID;

public class GroupUserResponseDTO {

    private UUID uuid;
    private String name;
    private OffsetDateTime joinedAt;

    public GroupUserResponseDTO(UUID uuid, String name, OffsetDateTime joinedAt) {
        this.uuid = uuid;
        this.name = name;
        this.joinedAt = joinedAt;
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public OffsetDateTime getJoinedAt() { return joinedAt; }
}
