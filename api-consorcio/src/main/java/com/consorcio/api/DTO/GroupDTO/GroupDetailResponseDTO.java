package com.consorcio.api.dto.GroupDTO;

import com.consorcio.api.domain.enums.GroupStatus;

import java.util.UUID;

public class GroupDetailResponseDTO {

    private UUID uuid;
    private String name;
    private Long valorTotal;
    private Long valorParcelas;
    private Integer meses;
    private Integer quantidadePessoas;
    private GroupStatus status;
    private Boolean privado;

    public GroupDetailResponseDTO(
            UUID uuid,
            String name,
            Long valorTotal,
            Long valorParcelas,
            Integer meses,
            Integer quantidadePessoas,
            GroupStatus status,
            Boolean privado
    ) {
        this.uuid = uuid;
        this.name = name;
        this.valorTotal = valorTotal;
        this.valorParcelas = valorParcelas;
        this.meses = meses;
        this.quantidadePessoas = quantidadePessoas;
        this.status = status;
        this.privado = privado;
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public Long getValorTotal() { return valorTotal; }
    public Long getValorParcelas() { return valorParcelas; }
    public Integer getMeses() { return meses; }
    public Integer getQuantidadePessoas() { return quantidadePessoas; }
    public GroupStatus getStatus() { return status; }
    public Boolean getPrivado() { return privado; }
}
