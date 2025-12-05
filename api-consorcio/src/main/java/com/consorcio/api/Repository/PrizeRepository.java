package com.consorcio.api.repository;

import com.consorcio.api.model.PrizeModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrizeRepository extends JpaRepository<PrizeModel, Long>
{

}
