package com.consorcio.api.dto.GroupDTO;

import java.time.OffsetDateTime;

public class InviteResponseDTO {

    private String inviteToken;
    private OffsetDateTime expiresAt;

    public InviteResponseDTO(String inviteToken, OffsetDateTime expiresAt) {
        this.inviteToken = inviteToken;
        this.expiresAt = expiresAt;
    }

    public String getInviteToken() {
        return inviteToken;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }
}
