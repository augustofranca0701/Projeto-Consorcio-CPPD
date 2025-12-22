package com.consorcio.api.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class PaymentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private UserModel user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "group_id")
    private GroupModel group;

    @Column(name = "parcela_numero", nullable = false)
    private Integer parcelaNumero;

    @Column(nullable = false)
    private Long valor;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "paid_at")
    private OffsetDateTime paidAt;

    @Column(name = "is_paid", nullable = false)
    private Boolean isPaid = false;

    /* =========================
       JPA CALLBACK
    ========================= */
    @PrePersist
    public void prePersist() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
    }

    /* =========================
       GETTERS
    ========================= */

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public UserModel getUser() {
        return user;
    }

    public GroupModel getGroup() {
        return group;
    }

    public Integer getParcelaNumero() {
        return parcelaNumero;
    }

    public Long getValor() {
        return valor;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public OffsetDateTime getPaidAt() {
        return paidAt;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    /* =========================
       SETTERS
    ========================= */

    public void setUser(UserModel user) {
        this.user = user;
    }

    public void setGroup(GroupModel group) {
        this.group = group;
    }

    public void setParcelaNumero(Integer parcelaNumero) {
        this.parcelaNumero = parcelaNumero;
    }

    public void setValor(Long valor) {
        this.valor = valor;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public void setPaidAt(OffsetDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public void setIsPaid(Boolean paid) {
        isPaid = paid;
    }
}
