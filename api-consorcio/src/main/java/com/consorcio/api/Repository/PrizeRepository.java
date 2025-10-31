package com.consorcio.api.Repository;

import com.consorcio.api.Model.PrizeModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrizeRepository extends JpaRepository<PrizeModel, Long>
{

}
