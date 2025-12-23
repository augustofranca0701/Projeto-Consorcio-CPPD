package com.consorcio.api.controller;

import com.consorcio.api.dto.PaymentDTO.PaymentCreateRequestDTO;
import com.consorcio.api.dto.PaymentDTO.PaymentResponseDTO;
import com.consorcio.api.model.UserModel;
import com.consorcio.api.service.PaymentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.consorcio.api.dto.PaymentDTO.PaymentHistoryResponseDTO;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/groups/{groupUuid}/payments")
    public PaymentResponseDTO create(
            @PathVariable UUID groupUuid,
            @RequestBody PaymentCreateRequestDTO dto,
            @AuthenticationPrincipal UserModel admin
    ) {
        return PaymentResponseDTO.from(
                paymentService.createPayment(
                        groupUuid,
                        dto.getUserUuid(),
                        dto.getParcelaNumero(),
                        dto.getValor(),
                        dto.getDataVencimento(),
                        admin
                )
        );
    }

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
   PAYMENT HISTORY
   GET /api/groups/{uuid}/payments/history
====================================================== */
@GetMapping("/groups/{groupUuid}/payments/history")
public List<PaymentHistoryResponseDTO> history(
        @PathVariable UUID groupUuid,
        @AuthenticationPrincipal UserModel user
) {
    return paymentService.listPaymentHistory(groupUuid, user);
}


    @PatchMapping("/payments/{paymentUuid}")
    public PaymentResponseDTO markAsPaid(
            @PathVariable UUID paymentUuid,
            @AuthenticationPrincipal UserModel admin
    ) {
        return PaymentResponseDTO.from(
                paymentService.markAsPaid(paymentUuid, admin)
        );
    }
}
