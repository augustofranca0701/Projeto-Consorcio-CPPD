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
        if (dto == null || dto.getEmail() == null || dto.getPassword() == null) {
            return ResponseEntity.badRequest().body("email/password required");
        }

        try {
            UserModel newUser = new UserModel();

            // Campos básicos
            newUser.setEmail(dto.getEmail());
            newUser.setName(dto.getName());

            // Campos adicionais (verifique se UserModel tem esses setters)
            newUser.setCpf(dto.getCpf());
            newUser.setPhone(dto.getPhone());
            newUser.setAddress(dto.getAddress());
            newUser.setComplement(dto.getComplement());
            newUser.setCity(dto.getCity());
            newUser.setState(dto.getState());

            // Senha criptografada
            newUser.setPassword(passwordEncoder.encode(dto.getPassword()));

            var created = userService.create(newUser);

            // Mantive a forma de retorno que você já usava.
            return ResponseEntity.status(HttpStatus.CREATED).body(created.getBody());
        } catch (Exception e) {
            // Logue o erro no seu logger real (aqui uso print para exemplo)
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar usuário: " + e.getMessage());
        }
    }
}
