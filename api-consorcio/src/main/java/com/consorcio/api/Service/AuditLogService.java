package com.consorcio.api.service;

import com.consorcio.api.domain.exception.ForbiddenDomainException;
import com.consorcio.api.domain.exception.NotFoundDomainException;
import com.consorcio.api.dto.GroupDTO.AuditLogResponseDTO;
import com.consorcio.api.model.GroupModel;
import com.consorcio.api.model.UserModel;
import com.consorcio.api.repository.GroupRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AuditLogService {

    private final JdbcTemplate jdbcTemplate;
    private final GroupRepository groupRepository;
    private final ObjectMapper objectMapper;

    public AuditLogService(
            JdbcTemplate jdbcTemplate,
            GroupRepository groupRepository,
            ObjectMapper objectMapper
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.groupRepository = groupRepository;
        this.objectMapper = objectMapper;
    }

    /* ======================================================
       LIST AUDIT LOGS
    ====================================================== */
    public List<AuditLogResponseDTO> listLogs(UUID groupUuid, UserModel user) {

        GroupModel group = groupRepository.findByUuid(groupUuid)
                .orElseThrow(() -> new NotFoundDomainException("group_not_found"));

        assertAdmin(group.getId(), user.getId());

        return jdbcTemplate.query("""
            SELECT 
                al.action,
                u.uuid AS user_uuid,
                al.created_at,
                al.metadata
            FROM audit_logs al
            JOIN users u ON u.id = al.performed_by
            WHERE al.group_id = ?
            ORDER BY al.created_at DESC
        """,
        (rs, i) -> new AuditLogResponseDTO(
                rs.getString("action"),
                UUID.fromString(rs.getString("user_uuid")),
                rs.getObject("created_at", OffsetDateTime.class),
                parseMetadata(rs.getString("metadata"))
        ),
        group.getId());
    }

    /* ======================================================
       HELPERS
    ====================================================== */

    private void assertAdmin(Long groupId, Long userId) {
        Boolean isAdmin = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) > 0 FROM user_group
            WHERE group_id = ? AND user_id = ? AND role = 'ADMIN'
        """, Boolean.class, groupId, userId);

        if (!Boolean.TRUE.equals(isAdmin)) {
            throw new ForbiddenDomainException("forbidden");
        }
    }

    private Map<String, Object> parseMetadata(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }

        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new IllegalStateException("invalid_audit_metadata", e);
        }
    }
}
