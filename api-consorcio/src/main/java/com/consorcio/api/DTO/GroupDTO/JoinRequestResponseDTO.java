package com.consorcio.api.dto.GroupDTO;

import java.time.OffsetDateTime;
import java.util.UUID;

public class JoinRequestResponseDTO {

    private UUID uuid;
    private UUID userUuid;
    private String userName;
    private OffsetDateTime requestedAt;

    public JoinRequestResponseDTO(
            UUID uuid,
            UUID userUuid,
            String userName,
            OffsetDateTime requestedAt
    ) {
        this.uuid = uuid;
        this.userUuid = userUuid;
        this.userName = userName;
        this.requestedAt = requestedAt;
    }

    public UUID getUuid() { return uuid; }
    public UUID getUserUuid() { return userUuid; }
    public String getUserName() { return userName; }
    public OffsetDateTime getRequestedAt() { return requestedAt; }
}
