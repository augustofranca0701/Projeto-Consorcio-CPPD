package com.consorcio.api.service;

import com.consorcio.api.domain.enums.GroupRole;
import com.consorcio.api.domain.enums.GroupStatus;
import com.consorcio.api.domain.enums.JoinRequestStatus;
import com.consorcio.api.domain.exception.*;
import com.consorcio.api.dto.GroupDTO.GroupUserResponseDTO;
import com.consorcio.api.dto.GroupDTO.JoinRequestResponseDTO;
import com.consorcio.api.model.GroupModel;
import com.consorcio.api.model.UserModel;
import com.consorcio.api.repository.GroupRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GroupParticipationService {

    private final JdbcTemplate jdbcTemplate;
    private final GroupRepository groupRepository;

    public GroupParticipationService(JdbcTemplate jdbcTemplate, GroupRepository groupRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.groupRepository = groupRepository;
    }

    /* =========================
       JOIN PUBLIC GROUP
    ========================= */
    @Transactional
    public void joinPublic(UUID groupUuid, UserModel user) {

        GroupModel group = getGroup(groupUuid);

        if (group.getPrivado()) {
            throw new ForbiddenDomainException("group_is_private");
        }

        if (group.getStatus() != GroupStatus.CRIADO) {
            throw new InvalidStateDomainException("group_not_joinable");
        }

        assertNotParticipant(group.getId(), user.getId());
        assertGroupNotFull(group.getId(), group.getQuantidadePessoas());

        insertParticipant(group.getId(), user.getId(), GroupRole.MEMBER);
    }

    /* =========================
       JOIN REQUEST (PRIVATE)
    ========================= */
    @Transactional
    public void requestJoin(UUID groupUuid, UserModel user) {

        GroupModel group = getGroup(groupUuid);

        if (!group.getPrivado()) {
            throw new InvalidStateDomainException("group_is_public");
        }

        if (group.getStatus() != GroupStatus.CRIADO) {
            throw new InvalidStateDomainException("group_not_joinable");
        }

        assertNotParticipant(group.getId(), user.getId());

        Integer exists = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) FROM join_requests
            WHERE user_id = ? AND group_id = ?
        """, Integer.class, user.getId(), group.getId());

        if (exists != null && exists > 0) {
            throw new ConflictDomainException("join_request_already_exists");
        }

        jdbcTemplate.update("""
            INSERT INTO join_requests (user_id, group_id, status)
            VALUES (?, ?, ?)
        """, user.getId(), group.getId(), JoinRequestStatus.PENDING.name());
    }

    /* =========================
       LIST JOIN REQUESTS
    ========================= */
    public List<JoinRequestResponseDTO> listJoinRequests(UUID groupUuid, UserModel admin) {

        GroupModel group = getGroup(groupUuid);
        assertAdmin(group.getId(), admin.getId());

        return jdbcTemplate.query("""
            SELECT jr.uuid, u.uuid AS user_uuid, u.name, jr.requested_at
            FROM join_requests jr
            JOIN users u ON u.id = jr.user_id
            WHERE jr.group_id = ?
              AND jr.status = 'PENDING'
        """, (rs, i) ->
            new JoinRequestResponseDTO(
                UUID.fromString(rs.getString("uuid")),
                UUID.fromString(rs.getString("user_uuid")),
                rs.getString("name"),
                rs.getObject("requested_at", OffsetDateTime.class)
            ),
            group.getId()
        );
    }

    /* =========================
       APPROVE REQUEST
    ========================= */
    @Transactional
    public void approve(UUID groupUuid, UUID requestUuid, UserModel admin) {

        GroupModel group = getGroup(groupUuid);
        assertAdmin(group.getId(), admin.getId());
        assertGroupNotFull(group.getId(), group.getQuantidadePessoas());

        Long userId = jdbcTemplate.queryForObject("""
            SELECT user_id FROM join_requests
            WHERE uuid = ? AND group_id = ? AND status = 'PENDING'
        """, Long.class, requestUuid, group.getId());

        if (userId == null) {
            throw new NotFoundDomainException("join_request_not_found");
        }

        insertParticipant(group.getId(), userId, GroupRole.MEMBER);

        jdbcTemplate.update("""
            UPDATE join_requests
            SET status = ?, decided_at = now()
            WHERE uuid = ?
        """, JoinRequestStatus.APPROVED.name(), requestUuid);
    }

    /* =========================
       REJECT REQUEST
    ========================= */
    @Transactional
    public void reject(UUID groupUuid, UUID requestUuid, UserModel admin) {

        GroupModel group = getGroup(groupUuid);
        assertAdmin(group.getId(), admin.getId());

        int updated = jdbcTemplate.update("""
            UPDATE join_requests
            SET status = ?, decided_at = now()
            WHERE uuid = ? AND group_id = ? AND status = 'PENDING'
        """, JoinRequestStatus.REJECTED.name(), requestUuid, group.getId());

        if (updated == 0) {
            throw new NotFoundDomainException("join_request_not_found");
        }
    }

    /* =========================
       LEAVE GROUP
    ========================= */
    @Transactional
    public void leave(UUID groupUuid, UserModel user) {

        GroupModel group = getGroup(groupUuid);

        if (group.getStatus() != GroupStatus.CRIADO) {
            throw new InvalidStateDomainException("cannot_leave_group");
        }

        GroupRole role = jdbcTemplate.queryForObject("""
            SELECT role FROM user_group
            WHERE user_id = ? AND group_id = ?
        """, GroupRole.class, user.getId(), group.getId());

        if (role == null) {
            throw new NotFoundDomainException("user_not_in_group");
        }

        if (role == GroupRole.ADMIN) {

            Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*) FROM user_group WHERE group_id = ?
            """, Integer.class, group.getId());

            if (count != null && count > 1) {
                throw new ConflictDomainException("admin_cannot_leave");
            }

            group.setStatus(GroupStatus.CANCELADO);
            groupRepository.save(group);
        }

        jdbcTemplate.update("""
            DELETE FROM user_group
            WHERE user_id = ? AND group_id = ?
        """, user.getId(), group.getId());
    }

    /* =========================
       LIST USERS
    ========================= */
    public List<GroupUserResponseDTO> listUsers(UUID groupUuid, UserModel user) {

        GroupModel group = getGroup(groupUuid);
        assertParticipant(group.getId(), user.getId());

        return jdbcTemplate.query("""
            SELECT u.uuid, u.name, ug.joined_at
            FROM user_group ug
            JOIN users u ON u.id = ug.user_id
            WHERE ug.group_id = ?
        """, (rs, i) ->
            new GroupUserResponseDTO(
                UUID.fromString(rs.getString("uuid")),
                rs.getString("name"),
                rs.getObject("joined_at", OffsetDateTime.class)
            ),
            group.getId()
        );
    }

    /* =========================
       HELPERS
    ========================= */

    private GroupModel getGroup(UUID uuid) {
        return groupRepository.findByUuid(uuid)
            .orElseThrow(() -> new NotFoundDomainException("group_not_found"));
    }

    private void assertAdmin(Long groupId, Long userId) {
        Boolean ok = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) > 0 FROM user_group
            WHERE user_id = ? AND group_id = ? AND role = 'ADMIN'
        """, Boolean.class, userId, groupId);

        if (!Boolean.TRUE.equals(ok)) {
            throw new ForbiddenDomainException("forbidden");
        }
    }

    private void assertParticipant(Long groupId, Long userId) {
        Boolean ok = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) > 0 FROM user_group
            WHERE user_id = ? AND group_id = ?
        """, Boolean.class, userId, groupId);

        if (!Boolean.TRUE.equals(ok)) {
            throw new ForbiddenDomainException("forbidden");
        }
    }

    private void assertNotParticipant(Long groupId, Long userId) {
        Boolean exists = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) > 0 FROM user_group
            WHERE user_id = ? AND group_id = ?
        """, Boolean.class, userId, groupId);

        if (Boolean.TRUE.equals(exists)) {
            throw new ConflictDomainException("user_already_in_group");
        }
    }

    private void assertGroupNotFull(Long groupId, Integer limit) {
        Integer count = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) FROM user_group WHERE group_id = ?
        """, Integer.class, groupId);

        if (count != null && count >= limit) {
            throw new ConflictDomainException("group_is_full");
        }
    }

    private void insertParticipant(Long groupId, Long userId, GroupRole role) {
        jdbcTemplate.update("""
            INSERT INTO user_group (user_id, group_id, role)
            VALUES (?, ?, ?)
        """, userId, groupId, role.name());
    }
}
