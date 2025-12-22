package com.consorcio.api.dto.GroupDTO;

import com.consorcio.api.domain.enums.GroupRole;
import com.consorcio.api.domain.enums.GroupStatus;

import java.util.UUID;

public class MyGroupResponseDTO {

    private UUID uuid;
    private String name;
    private GroupStatus status;
    private Boolean privado;
    private GroupRole role;

    public MyGroupResponseDTO(
            UUID uuid,
            String name,
            GroupStatus status,
            Boolean privado,
            GroupRole role
    ) {
        this.uuid = uuid;
        this.name = name;
        this.status = status;
        this.privado = privado;
        this.role = role;
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public GroupStatus getStatus() { return status; }
    public Boolean getPrivado() { return privado; }
    public GroupRole getRole() { return role; }
}
