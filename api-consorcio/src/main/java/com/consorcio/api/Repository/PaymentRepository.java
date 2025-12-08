package com.consorcio.api.repository;

import com.consorcio.api.model.PaymentModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentModel, Long>
{
}
