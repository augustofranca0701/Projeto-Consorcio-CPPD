package com.consorcio.api.service;

import com.consorcio.api.domain.enums.GroupStatus;
import com.consorcio.api.domain.exception.*;
import com.consorcio.api.dto.GroupDTO.InviteResponseDTO;
import com.consorcio.api.model.GroupModel;
import com.consorcio.api.model.UserModel;
import com.consorcio.api.repository.GroupRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class GroupInviteService {

    private final JdbcTemplate jdbcTemplate;
    private final GroupRepository groupRepository;

    public GroupInviteService(JdbcTemplate jdbcTemplate, GroupRepository groupRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.groupRepository = groupRepository;
    }

    /* ======================================================
       CREATE INVITE
    ====================================================== */
    @Transactional
    public InviteResponseDTO createInvite(UUID groupUuid, UserModel admin) {

        GroupModel group = getGroup(groupUuid);
        assertAdmin(group.getId(), admin.getId());

        if (!group.getPrivado()) {
            throw new ConflictDomainException("group_is_public");
        }

        if (group.getStatus() != GroupStatus.CRIADO) {
            throw new InvalidStateDomainException("group_not_created");
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        OffsetDateTime expiresAt = OffsetDateTime.now().plusDays(7);

        jdbcTemplate.update("""
            INSERT INTO group_invites (group_id, token, expires_at, created_by)
            VALUES (?, ?, ?, ?)
        """, group.getId(), token, expiresAt, admin.getId());

        return new InviteResponseDTO(token, expiresAt);
    }

    /* ======================================================
       JOIN BY INVITE
    ====================================================== */
    @Transactional
    public void joinByInvite(String token, UserModel user) {

        var invite = jdbcTemplate.queryForMap("""
            SELECT gi.*, g.status, g.quantidade_pessoas
            FROM group_invites gi
            JOIN groups g ON g.id = gi.group_id
            WHERE gi.token = ?
              AND gi.used = false
              AND gi.expires_at > now()
        """, token);

        Long groupId = ((Number) invite.get("group_id")).longValue();
        GroupStatus status = GroupStatus.valueOf((String) invite.get("status"));

        if (status != GroupStatus.CRIADO) {
            throw new InvalidStateDomainException("group_not_created");
        }

        Integer count = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) FROM user_group WHERE group_id = ?
        """, Integer.class, groupId);

        Integer limit = ((Number) invite.get("quantidade_pessoas")).intValue();

        if (count != null && count >= limit) {
            throw new ConflictDomainException("group_is_full");
        }

        Integer exists = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) FROM join_requests
            WHERE user_id = ? AND group_id = ?
        """, Integer.class, user.getId(), groupId);

        if (exists != null && exists > 0) {
            throw new ConflictDomainException("join_request_already_exists");
        }

        jdbcTemplate.update("""
            INSERT INTO join_requests (user_id, group_id)
            VALUES (?, ?)
        """, user.getId(), groupId);

        jdbcTemplate.update("""
            UPDATE group_invites SET used = true WHERE token = ?
        """, token);
    }

    /* ======================================================
       HELPERS
    ====================================================== */
    private GroupModel getGroup(UUID uuid) {
        return groupRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundDomainException("group_not_found"));
    }

    private void assertAdmin(Long groupId, Long userId) {
        Boolean isAdmin = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) > 0 FROM user_group
            WHERE group_id = ? AND user_id = ? AND role = 'ADMIN'
        """, Boolean.class, groupId, userId);

        if (!Boolean.TRUE.equals(isAdmin)) {
            throw new ForbiddenDomainException("forbidden");
        }
    }
}
