package com.consorcio.api.dto.UserDTO;

import com.consorcio.api.model.UserModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String uuid;
    private String name;
    private String email;
    private OffsetDateTime createdAt;

    public UserResponseDTO(UserModel user) {
        this.id = user.getId();
        this.uuid = user.getUuid().toString(); // ✅ AQUI ESTÁ A CORREÇÃO
        this.name = user.getName();
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt();
    }
}
