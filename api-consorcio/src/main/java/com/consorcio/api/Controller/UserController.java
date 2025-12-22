package com.consorcio.api.controller;

import com.consorcio.api.dto.UserDTO.UserResponseDTO;
import com.consorcio.api.dto.UserDTO.UserUpdateDTO;
import com.consorcio.api.model.UserModel;
import com.consorcio.api.service.UserService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // =========================================================
    // GET /api/users (SYSTEM ADMIN)
    // =========================================================
    @GetMapping
    public List<UserResponseDTO> listUsers(
            @AuthenticationPrincipal UserModel loggedUser
    ) {
        if (!loggedUser.isSystemAdmin()) {
            throw new com.consorcio.api.domain.exception.ForbiddenDomainException("forbidden");
        }

        return userService.findAll()
                .stream()
                .map(UserResponseDTO::new)
                .toList();
    }

    // =========================================================
    // GET /api/users/{uuid} (SYSTEM ADMIN)
    // =========================================================
    @GetMapping("/{uuid}")
    public UserResponseDTO getUser(
            @PathVariable String uuid,
            @AuthenticationPrincipal UserModel loggedUser
    ) {
        if (!loggedUser.isSystemAdmin()) {
            throw new com.consorcio.api.domain.exception.ForbiddenDomainException("forbidden");
        }

        return new UserResponseDTO(
                userService.findByUuid(uuid)
        );
    }

    // =========================================================
    // PATCH /api/users/me
    // =========================================================
    @PatchMapping("/me")
    public UserResponseDTO updateMe(
            @RequestBody UserUpdateDTO dto,
            @AuthenticationPrincipal UserModel loggedUser
    ) {
        return new UserResponseDTO(
                userService.updateMe(loggedUser, dto)
        );
    }

    // =========================================================
    // DELETE /api/users/me (SOFT DELETE)
    // =========================================================
    @DeleteMapping("/me")
    public Map<String, String> deleteMe(
            @AuthenticationPrincipal UserModel loggedUser
    ) {
        userService.deleteMe(loggedUser);
        return Map.of("mensagem", "Usuário removido com sucesso");
    }
}
