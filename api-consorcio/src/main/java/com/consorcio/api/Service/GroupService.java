package com.consorcio.api.service;

import com.consorcio.api.domain.enums.GroupRole;
import com.consorcio.api.domain.enums.GroupStatus;
import com.consorcio.api.domain.exception.*;
import com.consorcio.api.dto.GroupDTO.*;
import com.consorcio.api.model.GroupModel;
import com.consorcio.api.model.UserModel;
import com.consorcio.api.repository.GroupRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final JdbcTemplate jdbcTemplate;

    public GroupService(GroupRepository groupRepository, JdbcTemplate jdbcTemplate) {
        this.groupRepository = groupRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /* ======================================================
       CREATE GROUP
    ====================================================== */
    @Transactional
    public GroupModel create(CreateGroupDTO dto, UserModel creator) {

        GroupModel group = new GroupModel();
        group.setName(dto.getNome());
        group.setValorTotal(dto.getValorTotal());
        group.setValorParcelas(dto.getValorParcelas());
        group.setMeses(dto.getMeses());
        group.setQuantidadePessoas(dto.getQuantidadePessoas());
        group.setDataCriacao(dto.getDataCriacao());
        group.setDataFinal(dto.getDataFinal());
        group.setPrivado(dto.getPrivado());
        group.setCreatedBy(creator.getId());

        GroupModel saved = groupRepository.save(group);

        jdbcTemplate.update("""
            INSERT INTO user_group (user_id, group_id, role)
            VALUES (?, ?, 'ADMIN')
        """, creator.getId(), saved.getId());

        audit(saved.getId(), "GROUP_CREATED", creator.getId());
        return saved;
    }

    /* ======================================================
       LIST PUBLIC GROUPS
    ====================================================== */
    public List<GroupPublicResponseDTO> listPublicGroups() {
        return jdbcTemplate.query("""
            SELECT uuid, name, status
            FROM groups
            WHERE privado = false
              AND status IN ('CRIADO','ATIVO')
        """, (rs, i) ->
            new GroupPublicResponseDTO(
                UUID.fromString(rs.getString("uuid")),
                rs.getString("name"),
                GroupStatus.valueOf(rs.getString("status"))
            )
        );
    }

    /* ======================================================
       LIST MY GROUPS
    ====================================================== */
    public List<MyGroupResponseDTO> listMyGroups(UserModel user) {
        return jdbcTemplate.query("""
            SELECT g.uuid, g.name, g.status, g.privado, ug.role
            FROM user_group ug
            JOIN groups g ON g.id = ug.group_id
            WHERE ug.user_id = ?
              AND g.status IN ('CRIADO','ATIVO')
        """, (rs, i) ->
            new MyGroupResponseDTO(
                UUID.fromString(rs.getString("uuid")),
                rs.getString("name"),
                GroupStatus.valueOf(rs.getString("status")),
                rs.getBoolean("privado"),
                GroupRole.valueOf(rs.getString("role"))
            ),
            user.getId()
        );
    }

    /* ======================================================
       DETAIL
    ====================================================== */
    public GroupDetailResponseDTO getDetail(UUID uuid, UserModel user) {

        GroupModel group = getGroup(uuid);

        if (group.getPrivado()) {
            assertParticipant(group.getId(), user.getId());
        }

        return new GroupDetailResponseDTO(
            group.getUuid(),
            group.getName(),
            group.getValorTotal(),
            group.getValorParcelas(),
            group.getMeses(),
            group.getQuantidadePessoas(),
            group.getStatus(),
            group.getPrivado()
        );
    }

    /* ======================================================
       UPDATE (ACTIVATE / PRIVATE)
    ====================================================== */
    @Transactional
    public GroupModel update(UUID uuid, UpdateGroupDTO dto, UserModel user) {

        GroupModel group = getGroup(uuid);
        assertAdmin(group.getId(), user.getId());

        if (group.getStatus() != GroupStatus.CRIADO) {
            throw new InvalidStateDomainException("group_not_editable");
        }

        if (dto.getPrivado() != null) {
            group.setPrivado(dto.getPrivado());
        }

        if (dto.getStatus() == GroupStatus.ATIVO) {
            assertGroupFull(group.getId(), group.getQuantidadePessoas());
            group.setStatus(GroupStatus.ATIVO);
            audit(group.getId(), "GROUP_ACTIVATED", user.getId());
        }

        return groupRepository.save(group);
    }

    /* ======================================================
       CANCEL
    ====================================================== */
    @Transactional
    public GroupModel cancel(UUID uuid, UserModel user) {

        GroupModel group = getGroup(uuid);
        assertAdmin(group.getId(), user.getId());

        if (group.getStatus() != GroupStatus.CRIADO) {
            throw new ConflictDomainException("group_cannot_be_deleted");
        }

        Integer members = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) FROM user_group WHERE group_id = ?
        """, Integer.class, group.getId());

        if (members != null && members > 1) {
            throw new ConflictDomainException("group_cannot_be_deleted");
        }

        group.setStatus(GroupStatus.CANCELADO);
        audit(group.getId(), "GROUP_CANCELLED", user.getId());

        return groupRepository.save(group);
    }

    /* ======================================================
       FINALIZE
    ====================================================== */
    @Transactional
    public GroupModel finalizeGroup(UUID uuid, UserModel user) {

        GroupModel group = getGroup(uuid);
        assertAdmin(group.getId(), user.getId());

        if (group.getStatus() != GroupStatus.ATIVO) {
            throw new ConflictDomainException("group_cannot_be_finalized");
        }

        group.setStatus(GroupStatus.FINALIZADO);
        audit(group.getId(), "GROUP_FINALIZED", user.getId());

        return groupRepository.save(group);
    }

    /* ======================================================
       TRANSFER ADMIN
    ====================================================== */
    @Transactional
    public void transferAdmin(UUID groupUuid, UUID newAdminUuid, UserModel currentAdmin) {

        GroupModel group = getGroup(groupUuid);

        if (group.getStatus() != GroupStatus.CRIADO &&
            group.getStatus() != GroupStatus.ATIVO) {
            throw new InvalidStateDomainException("cannot_transfer_admin");
        }

        assertAdmin(group.getId(), currentAdmin.getId());

        Long newAdminId = jdbcTemplate.queryForObject("""
            SELECT id FROM users WHERE uuid = ?
        """, Long.class, newAdminUuid);

        if (newAdminId == null) {
            throw new NotFoundDomainException("user_not_found");
        }

        Boolean isParticipant = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) > 0 FROM user_group
            WHERE group_id = ? AND user_id = ?
        """, Boolean.class, group.getId(), newAdminId);

        if (!Boolean.TRUE.equals(isParticipant)) {
            throw new ConflictDomainException("user_not_in_group");
        }

        jdbcTemplate.update("""
            UPDATE user_group
            SET role = 'MEMBER'
            WHERE group_id = ? AND user_id = ?
        """, group.getId(), currentAdmin.getId());

        jdbcTemplate.update("""
            UPDATE user_group
            SET role = 'ADMIN'
            WHERE group_id = ? AND user_id = ?
        """, group.getId(), newAdminId);

        audit(group.getId(), "ADMIN_TRANSFERRED", currentAdmin.getId());
    }

    /* ======================================================
       HELPERS
    ====================================================== */
    private GroupModel getGroup(UUID uuid) {
        return groupRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundDomainException("group_not_found"));
    }

    private void assertAdmin(Long groupId, Long userId) {
        Boolean ok = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) > 0 FROM user_group
            WHERE group_id = ? AND user_id = ? AND role = 'ADMIN'
        """, Boolean.class, groupId, userId);

        if (!Boolean.TRUE.equals(ok)) {
            throw new ForbiddenDomainException("forbidden");
        }
    }

    private void assertParticipant(Long groupId, Long userId) {
        Boolean ok = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) > 0 FROM user_group
            WHERE group_id = ? AND user_id = ?
        """, Boolean.class, groupId, userId);

        if (!Boolean.TRUE.equals(ok)) {
            throw new ForbiddenDomainException("forbidden");
        }
    }

    private void assertGroupFull(Long groupId, Integer limit) {
        Integer count = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) FROM user_group WHERE group_id = ?
        """, Integer.class, groupId);

        if (count == null || count < limit) {
            throw new ConflictDomainException("group_not_full");
        }
    }

    private void audit(Long groupId, String action, Long userId) {
        jdbcTemplate.update("""
            INSERT INTO audit_logs (group_id, action, performed_by)
            VALUES (?, ?, ?)
        """, groupId, action, userId);
    }
}
