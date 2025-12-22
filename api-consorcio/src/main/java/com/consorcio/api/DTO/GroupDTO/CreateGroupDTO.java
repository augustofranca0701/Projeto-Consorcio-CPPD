package com.consorcio.api.dto.GroupDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class CreateGroupDTO {

    @NotBlank
    private String nome;

    @NotNull
    private Long valorTotal;

    @NotNull
    private Long valorParcelas;

    @NotNull
    private Integer meses;

    @NotNull
    private Integer quantidadePessoas;

    @NotNull
    private LocalDate dataCriacao;

    @NotNull
    private LocalDate dataFinal;

    @NotNull
    private Boolean privado;

    // getters e setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Long getValorTotal() { return valorTotal; }
    public void setValorTotal(Long valorTotal) { this.valorTotal = valorTotal; }

    public Long getValorParcelas() { return valorParcelas; }
    public void setValorParcelas(Long valorParcelas) { this.valorParcelas = valorParcelas; }

    public Integer getMeses() { return meses; }
    public void setMeses(Integer meses) { this.meses = meses; }

    public Integer getQuantidadePessoas() { return quantidadePessoas; }
    public void setQuantidadePessoas(Integer quantidadePessoas) { this.quantidadePessoas = quantidadePessoas; }

    public LocalDate getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDate dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDate getDataFinal() { return dataFinal; }
    public void setDataFinal(LocalDate dataFinal) { this.dataFinal = dataFinal; }

    public Boolean getPrivado() { return privado; }
    public void setPrivado(Boolean privado) { this.privado = privado; }
}
