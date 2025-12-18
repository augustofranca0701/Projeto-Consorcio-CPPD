package com.consorcio.api.controller;

import com.consorcio.api.dto.UserDTO.UserResponseDTO;
import com.consorcio.api.model.UserModel;
import com.consorcio.api.security.AppUserPrincipal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> me(Authentication authentication) {

        if (authentication == null ||
            !(authentication.getPrincipal() instanceof AppUserPrincipal principal)) {

            return ResponseEntity
                    .status(401)
                    .body(Map.of("error", "nao_autenticado"));
        }

        UserModel user = principal.getUser();

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUuid(user.getUuid());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());

        return ResponseEntity.ok(dto);
    }
}
