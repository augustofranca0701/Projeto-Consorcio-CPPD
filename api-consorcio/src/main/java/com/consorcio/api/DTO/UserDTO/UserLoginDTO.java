package com.consorcio.api.DTO.UserDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO
{
    @NotEmpty
    @NotBlank
    @Email
    @Column(unique = true)
    String email;

    @NotEmpty
    @NotBlank
    String password;
}
