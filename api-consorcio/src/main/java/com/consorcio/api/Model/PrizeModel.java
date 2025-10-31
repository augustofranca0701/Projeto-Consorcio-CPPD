package com.consorcio.api.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "prizes")
public class PrizeModel
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Date cannot be null.")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate datePrize;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "group_id") // foreign key in the table
    private GroupModel grupo;

    private Long user_id;
}
