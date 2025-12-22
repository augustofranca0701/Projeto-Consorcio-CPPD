package com.consorcio.api.controller;

import com.consorcio.api.dto.PaymentDTO.PaymentCreateRequestDTO;
import com.consorcio.api.dto.PaymentDTO.PaymentResponseDTO;
import com.consorcio.api.model.PaymentModel;
import com.consorcio.api.model.UserModel;
import com.consorcio.api.service.PaymentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /* ======================================================
       CREATE PAYMENT
       POST /api/groups/{uuid}/payments
    ====================================================== */
    @PostMapping("/groups/{groupUuid}/payments")
    public PaymentResponseDTO create(
            @PathVariable UUID groupUuid,
            @RequestBody PaymentCreateRequestDTO dto,
            @AuthenticationPrincipal UserModel admin
    ) {
        PaymentModel payment = paymentService.createPayment(
                groupUuid,
                dto.getUserUuid(),
                dto.getParcelaNumero(),
                dto.getValor(),
                dto.getDataVencimento(),
                admin
        );

        return PaymentResponseDTO.from(payment);
    }

    /* ======================================================
       LIST PAYMENTS
       GET /api/groups/{uuid}/payments
    ====================================================== */
    @GetMapping("/groups/{groupUuid}/payments")
    public List<PaymentResponseDTO> list(
            @PathVariable UUID groupUuid,
            @AuthenticationPrincipal UserModel user
    ) {
        return paymentService.listPayments(groupUuid, user)
                .stream()
                .map(PaymentResponseDTO::from)
                .toList();
    }

    /* ======================================================
       MARK AS PAID
       PATCH /api/payments/{uuid}
    ====================================================== */
    @PatchMapping("/payments/{paymentUuid}")
    public void markAsPaid(
            @PathVariable UUID paymentUuid,
            @AuthenticationPrincipal UserModel admin
    ) {
        paymentService.markAsPaid(paymentUuid, admin);
    }
}
