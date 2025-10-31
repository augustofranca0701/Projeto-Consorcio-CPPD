package com.consorcio.api.Model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import jakarta.persistence.Entity;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "groups")
public class GroupModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @NotBlank
    String name;

    @NotNull
    Long valorTotal;

    @NotNull
    Long valorParcelas;

    @NotNull(message = "Date cannot be null")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    LocalDate dataCriacao;

    @NotNull
    int meses;

    @NotNull(message = "Date cannot be null")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    LocalDate dataFinal;

    @NotNull
    int quantidadePessoas;

    @NotNull
    boolean privado;

    Long createdBy;

    @ManyToMany(mappedBy = "groups")
    private List<UserModel> users = new ArrayList<>();

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL)
    private List<PrizeModel> prizes = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<PaymentModel> payments;
}
