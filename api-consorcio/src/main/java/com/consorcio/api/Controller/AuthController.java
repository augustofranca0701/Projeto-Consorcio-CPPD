package com.consorcio.api.controller;

import com.consorcio.api.dto.UserDTO.RegisterDTO;
import com.consorcio.api.dto.UserDTO.UserLoginDTO;
import com.consorcio.api.dto.UserDTO.UserResponseDTO;
import com.consorcio.api.model.UserModel;
import com.consorcio.api.security.JwtUtil;
import com.consorcio.api.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(
        AuthenticationManager authenticationManager,
        UserService userService,
        JwtUtil jwtUtil
    ) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /* =======================
       REGISTER
    ======================== */

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(
        @Valid @RequestBody RegisterDTO dto
    ) {
        UserModel user = new UserModel();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setCpf(dto.getCpf());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setCity(dto.getCity());
        user.setState(dto.getState());
        user.setComplement(dto.getComplement());

        UserModel created = userService.create(user);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new UserResponseDTO(created));
    }

    /* =======================
       LOGIN
    ======================== */

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
        @Valid @RequestBody UserLoginDTO dto
    ) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                dto.getEmail(),
                dto.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserModel user = (UserModel) authentication.getPrincipal();

        String token = jwtUtil.generateToken(user.getEmail());

        return ResponseEntity.ok(
            Map.of(
                "token", token,
                "user", new UserResponseDTO(user)
            )
        );
    }

    /* =======================
       ME
    ======================== */

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(Authentication authentication) {
        UserModel user = (UserModel) authentication.getPrincipal();
        return ResponseEntity.ok(new UserResponseDTO(user));
    }
}
