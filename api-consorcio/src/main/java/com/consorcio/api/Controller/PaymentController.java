package com.consorcio.api.Controller;

import com.consorcio.api.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("payments")
public class PaymentController
{
    @Autowired
    private PaymentService paymentService;

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserPayments(@PathVariable("userId") Long userId)
    {
        return paymentService.findAllUserPayments(userId);
    }

    @PutMapping("/{userId}/{paymentId}")
    public ResponseEntity<Object> makePayment(@PathVariable("userId") Long userId, @PathVariable("paymentId") Long paymentId)
    {
        return paymentService.makePayment(userId, paymentId);
    }
}
