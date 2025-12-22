package com.consorcio.api.repository;

import com.consorcio.api.model.GroupModel;
import com.consorcio.api.model.PaymentModel;
import com.consorcio.api.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentModel, Long> {

    List<PaymentModel> findByGroup(GroupModel group);

    List<PaymentModel> findByGroupAndUser(GroupModel group, UserModel user);

    Optional<PaymentModel> findByUuid(UUID uuid);

    boolean existsByGroupAndUserAndParcelaNumero(
            GroupModel group,
            UserModel user,
            Integer parcelaNumero
    );
}
