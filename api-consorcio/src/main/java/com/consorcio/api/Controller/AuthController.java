package com.consorcio.api.controller;

import com.consorcio.api.dto.UserDTO.RegisterDTO;
import com.consorcio.api.dto.UserDTO.UserLoginDTO;
import com.consorcio.api.dto.UserDTO.UserResponseDTO;
import com.consorcio.api.model.UserModel;
import com.consorcio.api.security.AppUserPrincipal;
import com.consorcio.api.security.JwtUtil;
import com.consorcio.api.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(
            UserService userService,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil
    ) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    // ================= REGISTER =================
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody RegisterDTO dto) {

        if (dto == null || dto.getEmail() == null || dto.getPassword() == null) {
            return ResponseEntity.badRequest().build();
        }

        UserModel user = new UserModel();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setCpf(dto.getCpf());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setComplement(dto.getComplement());
        user.setCity(dto.getCity());
        user.setState(dto.getState());

        UserModel created = userService.create(user);

        UserResponseDTO response = new UserResponseDTO();
        response.setId(created.getId());
        response.setUuid(created.getUuid());
        response.setName(created.getName());
        response.setEmail(created.getEmail());
        response.setCreatedAt(created.getCreatedAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO dto) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getEmail(),
                            dto.getPassword()
                    )
            );

            String token = jwtUtil.generateToken(dto.getEmail());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "type", "Bearer"
            ));

        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "invalid_credentials"));
        }
    }

    // ================= ME =================
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponseDTO> me(Authentication authentication) {

        AppUserPrincipal principal = (AppUserPrincipal) authentication.getPrincipal();
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
