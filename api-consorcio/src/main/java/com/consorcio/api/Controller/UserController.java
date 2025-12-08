package com.consorcio.api.controller;

import com.consorcio.api.dto.UserDTO.UserLoginDTO;
import com.consorcio.api.repository.UserRepository;
import com.consorcio.api.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UserController(UserRepository userRepo,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtUtil.generateToken(dto.getEmail());

            return ResponseEntity.ok(Map.of("token", token));

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "credenciais_invalidas"));
        }
    }
}
