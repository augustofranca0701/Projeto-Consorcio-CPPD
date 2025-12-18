package com.consorcio.api.service;

import com.consorcio.api.model.UserModel;
import com.consorcio.api.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserModel create(UserModel user) {

        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new IllegalArgumentException("email_already_exists");
        }

        if (user.getCpf() != null && userRepository.existsByCpf(user.getCpf())) {
            throw new IllegalArgumentException("cpf_already_exists");
        }

        if (user.getPhone() != null && userRepository.existsByPhone(user.getPhone())) {
            throw new IllegalArgumentException("phone_already_exists");
        }

        // responsabilidade única do service
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    /* ======================================================
       Métodos antigos (login, update, delete etc.)
       NÃO foram removidos aqui para não quebrar o sistema.
       Eles podem (e devem) ser refatorados depois, rota por rota.
       ====================================================== */
}
