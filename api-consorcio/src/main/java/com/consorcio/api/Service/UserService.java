package com.consorcio.api.service;

import com.consorcio.api.domain.exception.ForbiddenDomainException;
import com.consorcio.api.domain.exception.NotFoundDomainException;
import com.consorcio.api.model.UserModel;
import com.consorcio.api.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================================
    // CREATE
    // =========================================================
    public UserModel create(UserModel user) {

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        return userRepository.save(user);
    }

    // =========================================================
    // AUTHENTICATE (LOGIN)
    // =========================================================
    public UserModel authenticate(String email, String rawPassword) {

        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundDomainException("user_not_found"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new ForbiddenDomainException("invalid_credentials");
        }

        return user;
    }

    // =========================================================
    // USERS (SYSTEM ADMIN)
    // =========================================================
    public List<UserModel> findAll() {
        return userRepository.findAll();
    }

    public UserModel findByUuid(String uuid) {
        return userRepository.findByUuid(UUID.fromString(uuid))
                .orElseThrow(() -> new NotFoundDomainException("user_not_found"));
    }
}
