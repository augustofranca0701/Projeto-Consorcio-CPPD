package com.consorcio.api.repository;

import com.consorcio.api.model.PrizeModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PrizeRepository extends JpaRepository<PrizeModel, Long> {

    Optional<PrizeModel> findByUuid(UUID uuid);

    List<PrizeModel> findByGroupId(Long groupId);
}
