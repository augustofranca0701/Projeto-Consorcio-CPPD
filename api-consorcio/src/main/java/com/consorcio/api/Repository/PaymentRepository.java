package com.consorcio.api.Repository;

import com.consorcio.api.Model.PaymentModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentModel, Long>
{
}
