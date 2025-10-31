package com.consorcio.api.DTO.UserDTO;

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
public class UserUpdateDTO
{
    @NotEmpty
    @NotBlank
    private String name;

    @NotEmpty
    @NotBlank
    private String phone;

    @NotEmpty
    @NotBlank
    private String address;

    @NotEmpty
    @NotBlank
    private String complement;

    @NotEmpty
    @NotBlank
    private String state;

    @NotEmpty
    @NotBlank
    private String city;
}
