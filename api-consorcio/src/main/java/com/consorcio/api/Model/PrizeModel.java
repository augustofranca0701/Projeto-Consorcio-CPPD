package com.consorcio.api.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "prizes")
public class PrizeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid = UUID.randomUUID();

    @ManyToOne(optional = false)
    @JoinColumn(name = "group_id")
    private GroupModel group;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private UserModel user;

    @Column(name = "date_prize", nullable = false)
    private LocalDate datePrize;

    /* =======================
       GETTERS
    ======================= */

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public GroupModel getGroup() {
        return group;
    }

    public UserModel getUser() {
        return user;
    }

    public LocalDate getDatePrize() {
        return datePrize;
    }

    /* =======================
       SETTERS
    ======================= */

    public void setGroup(GroupModel group) {
        this.group = group;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public void setDatePrize(LocalDate datePrize) {
        this.datePrize = datePrize;
    }
}
