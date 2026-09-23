package com.recurringflex.payment.controller;

import com.recurringflex.payment.dto.PaymentRequest;
import com.recurringflex.payment.dto.PaymentResponse;
import com.recurringflex.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/authorize")
    public ResponseEntity<PaymentResponse> authorize(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.authorize(request));
    }
}
