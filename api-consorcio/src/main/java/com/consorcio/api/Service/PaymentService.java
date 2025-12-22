package com.consorcio.api.service;

import com.consorcio.api.domain.enums.GroupStatus;
import com.consorcio.api.domain.exception.ConflictDomainException;
import com.consorcio.api.domain.exception.ForbiddenDomainException;
import com.consorcio.api.domain.exception.NotFoundDomainException;
import com.consorcio.api.dto.PaymentDTO.PaymentHistoryResponseDTO;
import com.consorcio.api.model.GroupModel;
import com.consorcio.api.model.PaymentModel;
import com.consorcio.api.model.UserModel;
import com.consorcio.api.repository.GroupRepository;
import com.consorcio.api.repository.PaymentRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final GroupRepository groupRepository;
    private final JdbcTemplate jdbcTemplate;

    public PaymentService(
            PaymentRepository paymentRepository,
            GroupRepository groupRepository,
            JdbcTemplate jdbcTemplate
    ) {
        this.paymentRepository = paymentRepository;
        this.groupRepository = groupRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /* ======================================================
       CREATE PAYMENT
    ====================================================== */
    @Transactional
    public PaymentModel createPayment(
            UUID groupUuid,
            UUID userUuid,
            Integer parcelaNumero,
            Long valor,
            OffsetDateTime dataVencimento,
            UserModel admin
    ) {

        GroupModel group = getGroup(groupUuid);
        assertGroupActive(group);
        assertAdmin(group.getId(), admin.getId());

        UserModel user = getUserByUuid(userUuid);

        if (paymentRepository.existsByGroupAndUserAndParcelaNumero(
                group, user, parcelaNumero
        )) {
            throw new ConflictDomainException("parcela_already_exists");
        }

        PaymentModel payment = new PaymentModel();
        payment.setGroup(group);
        payment.setUser(user);
        payment.setParcelaNumero(parcelaNumero);
        payment.setValor(valor);
        payment.setDataVencimento(dataVencimento.toLocalDate());
        payment.setIsPaid(false);

        PaymentModel saved = paymentRepository.save(payment);

        audit(group.getId(), "PAYMENT_CREATED", admin.getId());

        return saved;
    }

    /* ======================================================
       LIST PAYMENTS
    ====================================================== */
    @Transactional(readOnly = true)
    public List<PaymentModel> listPayments(UUID groupUuid, UserModel user) {

        GroupModel group = getGroup(groupUuid);

        if (isAdmin(group.getId(), user.getId())) {
            return paymentRepository.findByGroup(group);
        }

        return paymentRepository.findByGroupAndUser(group, user);
    }

    /* ======================================================
       MARK AS PAID
    ====================================================== */
    @Transactional
    public void markAsPaid(UUID paymentUuid, UserModel admin) {

        PaymentModel payment = paymentRepository.findByUuid(paymentUuid)
                .orElseThrow(() -> new NotFoundDomainException("payment_not_found"));

        GroupModel group = payment.getGroup();
        assertGroupActive(group);
        assertAdmin(group.getId(), admin.getId());

        if (Boolean.TRUE.equals(payment.getIsPaid())) {
            throw new ConflictDomainException("payment_already_paid");
        }

        payment.setIsPaid(true);
        payment.setPaidAt(OffsetDateTime.now());
        paymentRepository.save(payment);

        jdbcTemplate.update("""
            INSERT INTO payment_history
            (payment_id, action, old_value, new_value, performed_by)
            VALUES (?, 'MARKED_AS_PAID', false, true, ?)
        """, payment.getId(), admin.getId());

        audit(group.getId(), "PAYMENT_MARKED_AS_PAID", admin.getId());
    }

    /* ======================================================
       PAYMENT HISTORY
    ====================================================== */
    @Transactional(readOnly = true)
    public List<PaymentHistoryResponseDTO> listPaymentHistory(
            UUID groupUuid,
            UserModel user
    ) {

        GroupModel group = getGroup(groupUuid);
        boolean admin = isAdmin(group.getId(), user.getId());

        if (admin) {
            return jdbcTemplate.query("""
                SELECT p.uuid AS payment_uuid, ph.action, ph.old_value, ph.new_value,
                       u.uuid AS performed_by, ph.performed_at
                FROM payment_history ph
                JOIN payments p ON p.id = ph.payment_id
                JOIN users u ON u.id = ph.performed_by
                WHERE p.group_id = ?
                ORDER BY ph.performed_at DESC
            """, mapper(), group.getId());
        }

        return jdbcTemplate.query("""
            SELECT p.uuid AS payment_uuid, ph.action, ph.old_value, ph.new_value,
                   u.uuid AS performed_by, ph.performed_at
            FROM payment_history ph
            JOIN payments p ON p.id = ph.payment_id
            JOIN users u ON u.id = ph.performed_by
            WHERE p.group_id = ? AND p.user_id = ?
            ORDER BY ph.performed_at DESC
        """, mapper(), group.getId(), user.getId());
    }

    /* ======================================================
       HELPERS
    ====================================================== */

    private GroupModel getGroup(UUID uuid) {
        return groupRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundDomainException("group_not_found"));
    }

    private void assertGroupActive(GroupModel group) {
        if (group.getStatus() != GroupStatus.ATIVO) {
            throw new ConflictDomainException("group_not_active");
        }
    }

    private void assertAdmin(Long groupId, Long userId) {
        if (!isAdmin(groupId, userId)) {
            throw new ForbiddenDomainException("forbidden");
        }
    }

    private boolean isAdmin(Long groupId, Long userId) {
        Boolean admin = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) > 0
            FROM user_group
            WHERE group_id = ? AND user_id = ? AND role = 'ADMIN'
        """, Boolean.class, groupId, userId);

        return Boolean.TRUE.equals(admin);
    }

    private UserModel getUserByUuid(UUID uuid) {
        return jdbcTemplate.queryForObject("""
            SELECT id FROM users WHERE uuid = ?
        """, (rs, i) -> {
            UserModel u = new UserModel();
            u.setId(rs.getLong("id"));
            return u;
        }, uuid);
    }

    private void audit(Long groupId, String action, Long userId) {
        jdbcTemplate.update("""
            INSERT INTO audit_logs (group_id, action, performed_by)
            VALUES (?, ?, ?)
        """, groupId, action, userId);
    }

    private static org.springframework.jdbc.core.RowMapper<PaymentHistoryResponseDTO> mapper() {
        return (rs, i) -> new PaymentHistoryResponseDTO(
                UUID.fromString(rs.getString("payment_uuid")),
                rs.getString("action"),
                rs.getBoolean("old_value"),
                rs.getBoolean("new_value"),
                UUID.fromString(rs.getString("performed_by")),
                rs.getTimestamp("performed_at").toInstant()
                        .atOffset(java.time.ZoneOffset.UTC)
        );
    }
}
