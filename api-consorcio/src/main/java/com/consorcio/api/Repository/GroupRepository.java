package com.consorcio.api.repository;

import com.consorcio.api.model.GroupModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<GroupModel, Long> {
}
