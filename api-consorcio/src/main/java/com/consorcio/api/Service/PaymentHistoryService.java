package com.consorcio.api.service;

import com.consorcio.api.domain.enums.GroupRole;
import com.consorcio.api.domain.exception.ForbiddenDomainException;
import com.consorcio.api.domain.exception.NotFoundDomainException;
import com.consorcio.api.dto.PaymentDTO.PaymentHistoryResponseDTO;
import com.consorcio.api.model.GroupModel;
import com.consorcio.api.model.UserModel;
import com.consorcio.api.repository.GroupRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentHistoryService {

    private final JdbcTemplate jdbcTemplate;
    private final GroupRepository groupRepository;

    public PaymentHistoryService(JdbcTemplate jdbcTemplate, GroupRepository groupRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.groupRepository = groupRepository;
    }

    public List<PaymentHistoryResponseDTO> listHistory(
            UUID groupUuid,
            UserModel user,
            int page,
            int limit
    ) {

        GroupModel group = groupRepository.findByUuid(groupUuid)
                .orElseThrow(() -> new NotFoundDomainException("group_not_found"));

        Boolean isParticipant = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) > 0 FROM user_group
            WHERE group_id = ? AND user_id = ?
        """, Boolean.class, group.getId(), user.getId());

        if (!Boolean.TRUE.equals(isParticipant)) {
            throw new ForbiddenDomainException("forbidden");
        }

        String role = jdbcTemplate.queryForObject("""
            SELECT role FROM user_group
            WHERE group_id = ? AND user_id = ?
        """, String.class, group.getId(), user.getId());

        int offset = (page - 1) * limit;

        String baseQuery = """
            SELECT p.uuid AS payment_uuid,
                   ph.action,
                   ph.old_value,
                   ph.new_value,
                   u.uuid AS performed_by,
                   ph.performed_at
            FROM payment_history ph
            JOIN payments p ON p.id = ph.payment_id
            JOIN users u ON u.id = ph.performed_by
            WHERE p.group_id = ?
        """;

        if (!GroupRole.ADMIN.name().equals(role)) {
            baseQuery += " AND p.user_id = " + user.getId();
        }

        baseQuery += """
            ORDER BY ph.performed_at DESC
            LIMIT ? OFFSET ?
        """;

        return jdbcTemplate.query(
                baseQuery,
                (rs, i) -> new PaymentHistoryResponseDTO(
                        UUID.fromString(rs.getString("payment_uuid")),
                        rs.getString("action"),
                        rs.getBoolean("old_value"),
                        rs.getBoolean("new_value"),
                        UUID.fromString(rs.getString("performed_by")),
                        rs.getTimestamp("performed_at").toInstant()
                                .atOffset(java.time.ZoneOffset.UTC)
                ),
                group.getId(),
                limit,
                offset
        );
    }
}
