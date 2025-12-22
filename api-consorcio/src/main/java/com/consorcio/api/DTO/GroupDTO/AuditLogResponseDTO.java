package com.consorcio.api.dto.GroupDTO;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public class AuditLogResponseDTO {

    private String action;
    private UUID performedBy;
    private OffsetDateTime performedAt;
    private Map<String, Object> metadata;

    public AuditLogResponseDTO(
            String action,
            UUID performedBy,
            OffsetDateTime performedAt,
            Map<String, Object> metadata
    ) {
        this.action = action;
        this.performedBy = performedBy;
        this.performedAt = performedAt;
        this.metadata = metadata;
    }

    public String getAction() {
        return action;
    }

    public UUID getPerformedBy() {
        return performedBy;
    }

    public OffsetDateTime getPerformedAt() {
        return performedAt;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
}
