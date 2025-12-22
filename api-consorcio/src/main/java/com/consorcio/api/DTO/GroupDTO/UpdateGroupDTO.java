package com.consorcio.api.dto.GroupDTO;

import com.consorcio.api.domain.enums.GroupStatus;

public class UpdateGroupDTO {

    private GroupStatus status;
    private Boolean privado;

    public GroupStatus getStatus() {
        return status;
    }

    public void setStatus(GroupStatus status) {
        this.status = status;
    }

    public Boolean getPrivado() {
        return privado;
    }

    public void setPrivado(Boolean privado) {
        this.privado = privado;
    }
}
