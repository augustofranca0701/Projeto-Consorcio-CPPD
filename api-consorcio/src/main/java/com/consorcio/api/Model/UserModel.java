package com.consorcio.api.Model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "users")
public class UserModel
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @NotBlank
    String name;

    @NotEmpty
    @NotBlank
    @Email
    @Column(unique = true)
    String email;

    @NotEmpty
    @NotBlank
    String password;

    @NotEmpty
    @NotBlank
    @Column(unique = true)
    String cpf;

    @NotEmpty
    @NotBlank
    @Column(unique = true)
    String phone;

    @NotEmpty
    @NotBlank
    String address;

    @NotEmpty
    @NotBlank
    String complement;

    @NotEmpty
    @NotBlank
    String state;

    @NotEmpty
    @NotBlank
    String city;

    @JsonIgnore
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "user_group",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id")
    )
    private List<GroupModel> groups = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<PaymentModel> payments;

}
