package com.consorcio.api.service;

import com.consorcio.api.domain.enums.GroupStatus;
import com.consorcio.api.domain.exception.ForbiddenDomainException;
import com.consorcio.api.domain.exception.InvalidStateDomainException;
import com.consorcio.api.domain.exception.NotFoundDomainException;
import com.consorcio.api.dto.PrizeDTO.PrizeResponseDTO;
import com.consorcio.api.model.GroupModel;
import com.consorcio.api.model.PrizeModel;
import com.consorcio.api.model.UserModel;
import com.consorcio.api.repository.GroupRepository;
import com.consorcio.api.repository.PrizeRepository;
import com.consorcio.api.repository.UserRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PrizeService {

    private final PrizeRepository prizeRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    public PrizeService(
            PrizeRepository prizeRepository,
            GroupRepository groupRepository,
            UserRepository userRepository,
            JdbcTemplate jdbcTemplate
    ) {
        this.prizeRepository = prizeRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /* ======================================================
       REGISTER PRIZE
    ====================================================== */
    @Transactional
    public PrizeResponseDTO registerPrize(
            UUID groupUuid,
            UUID userUuid,
            LocalDate datePrize,
            UserModel admin
    ) {
        GroupModel group = getGroup(groupUuid);
        assertAdmin(group.getId(), admin.getId());

        if (group.getStatus() != GroupStatus.ATIVO) {
            throw new InvalidStateDomainException("group_not_active");
        }

        Long userId = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new NotFoundDomainException("user_not_found"))
                .getId();

        assertParticipant(group.getId(), userId);

        PrizeModel prize = new PrizeModel();
        prize.setGroup(group);
        prize.setUser(userRepository.getReferenceById(userId));
        prize.setDatePrize(datePrize);

        PrizeModel saved = prizeRepository.save(prize);

        audit(group.getId(), "PRIZE_REGISTERED", admin.getId());

        return PrizeResponseDTO.from(saved);
    }

    /* ======================================================
       LIST GROUP PRIZES
    ====================================================== */
    public List<PrizeResponseDTO> listGroupPrizes(UUID groupUuid, UserModel user) {

        GroupModel group = getGroup(groupUuid);
        assertParticipant(group.getId(), user.getId());

        return prizeRepository.findByGroupId(group.getId())
                .stream()
                .map(PrizeResponseDTO::from)
                .toList();
    }

    /* ======================================================
       GET PRIZE
    ====================================================== */
    public PrizeResponseDTO getPrize(UUID prizeUuid, UserModel user) {

        PrizeModel prize = prizeRepository.findByUuid(prizeUuid)
                .orElseThrow(() -> new NotFoundDomainException("prize_not_found"));

        assertParticipant(prize.getGroup().getId(), user.getId());

        return PrizeResponseDTO.from(prize);
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

    private void assertParticipant(Long groupId, Long userId) {
        Boolean exists = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) > 0 FROM user_group
            WHERE group_id = ? AND user_id = ?
        """, Boolean.class, groupId, userId);

        if (!Boolean.TRUE.equals(exists)) {
            throw new ForbiddenDomainException("forbidden");
        }
    }

    private void audit(Long groupId, String action, Long userId) {
        jdbcTemplate.update("""
            INSERT INTO audit_logs (group_id, action, performed_by)
            VALUES (?, ?, ?)
        """, groupId, action, userId);
    }
}
