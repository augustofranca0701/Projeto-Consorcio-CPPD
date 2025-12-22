package com.consorcio.api.service;

import com.consorcio.api.domain.enums.GroupRole;
import com.consorcio.api.domain.enums.GroupStatus;
import com.consorcio.api.domain.exception.ConflictDomainException;
import com.consorcio.api.domain.exception.ForbiddenDomainException;
import com.consorcio.api.domain.exception.NotFoundDomainException;
import com.consorcio.api.dto.UserDTO.UserUpdateDTO;
import com.consorcio.api.model.GroupModel;
import com.consorcio.api.model.UserGroupModel;
import com.consorcio.api.model.UserModel;
import com.consorcio.api.repository.UserGroupRepository;
import com.consorcio.api.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            UserGroupRepository userGroupRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userGroupRepository = userGroupRepository;
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

    // =========================================================
    // PATCH /api/users/me (PARCIAL)
    // =========================================================
    public UserModel updateMe(UserModel user, UserUpdateDTO dto) {

        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getAddress() != null) {
            user.setAddress(dto.getAddress());
        }
        if (dto.getComplement() != null) {
            user.setComplement(dto.getComplement());
        }
        if (dto.getCity() != null) {
            user.setCity(dto.getCity());
        }
        if (dto.getState() != null) {
            user.setState(dto.getState());
        }

        user.setUpdatedAt(OffsetDateTime.now());
        return userRepository.save(user);
    }

    // =========================================================
    // DELETE /api/users/me (SOFT DELETE)
    // =========================================================
    public void deleteMe(UserModel user) {

        List<UserGroupModel> participations =
                userGroupRepository.findAllByUser(user);

        for (UserGroupModel ug : participations) {

            GroupModel group = ug.getGroup();

            boolean isAdmin = ug.getRole() == GroupRole.ADMIN;
            long totalParticipants =
                    userGroupRepository.countByGroup(group);

            if (isAdmin) {

                if (group.getStatus() == GroupStatus.ATIVO) {
                    throw new ConflictDomainException("user_is_admin_of_active_group");
                }

                if (group.getStatus() == GroupStatus.CRIADO && totalParticipants > 1) {
                    throw new ConflictDomainException("user_is_admin_of_group_with_members");
                }
            }
        }

        user.setDeletedAt(OffsetDateTime.now());
        userRepository.save(user);
    }
}
