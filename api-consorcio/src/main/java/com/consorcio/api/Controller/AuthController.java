package com.consorcio.api.controller;

import com.consorcio.api.dto.UserDTO.RegisterDTO;
import com.consorcio.api.model.UserModel;
import com.consorcio.api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO dto) {

        if (dto.getEmail() == null || dto.getPassword() == null) {
            return ResponseEntity.badRequest().body("email/password required");
        }

        UserModel newUser = new UserModel();
        newUser.setEmail(dto.getEmail());
        newUser.setName(dto.getName());

        // Senha criptografada
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));

        var created = userService.create(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(created.getBody());
    }
}
