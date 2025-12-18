package com.consorcio.api.controller;

import com.consorcio.api.model.UserModel;
import com.consorcio.api.repository.UserRepository;
import com.consorcio.api.security.AppUserPrincipal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepo;

    public UserController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // ==========================
    // GET USER LOGADO (/me)
    // ==========================
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> me(Authentication authentication) {

        if (authentication == null || !(authentication.getPrincipal() instanceof AppUserPrincipal principal)) {
            return ResponseEntity
                    .status(401)
                    .body(Map.of("error", "nao_autenticado"));
        }

        UserModel user = principal.getUser();
        user.setPassword(null);

        return ResponseEntity.ok(user);
    }
}
