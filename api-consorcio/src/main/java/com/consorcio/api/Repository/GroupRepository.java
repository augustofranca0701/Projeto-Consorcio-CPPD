package com.consorcio.api.repository;

import com.consorcio.api.model.GroupModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<GroupModel, Long> {

    Optional<GroupModel> findByUuid(UUID uuid);
}
