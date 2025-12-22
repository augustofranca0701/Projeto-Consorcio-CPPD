package com.consorcio.api.controller;

import com.consorcio.api.dto.UserDTO.UserResponseDTO;
import com.consorcio.api.domain.exception.ForbiddenDomainException;
import com.consorcio.api.model.UserModel;
import com.consorcio.api.service.UserService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            throw new ForbiddenDomainException("forbidden");
        }

        return userService.findAll()
                .stream()
                .map(UserResponseDTO::new)
                .toList();
    }

    // =========================================================
    // GET /api/users/{uuid}
    // =========================================================
    @GetMapping("/{uuid}")
    public UserResponseDTO getUser(
            @PathVariable String uuid,
            @AuthenticationPrincipal UserModel loggedUser
    ) {
        if (!loggedUser.isSystemAdmin()) {
            throw new ForbiddenDomainException("forbidden");
        }

        return new UserResponseDTO(
                userService.findByUuid(uuid)
        );
    }
}
