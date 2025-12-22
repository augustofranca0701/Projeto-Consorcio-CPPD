package com.consorcio.api.dto.UserDTO;

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
    private String name;
    private String phone;
    private String address;
    private String complement;
    private String state;
    private String city;
}
