package com.recurringflex.payment.service;

import com.recurringflex.payment.dto.PaymentRequest;
import com.recurringflex.payment.dto.PaymentResponse;
import com.recurringflex.payment.entity.Payment;
import com.recurringflex.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // Mock payment authorization: always succeeds for demo purposes.
    // Swap this logic out for a real payment gateway integration later.
    public PaymentResponse authorize(PaymentRequest request) {
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        boolean success = true;

        Payment payment = new Payment();
        payment.setUserId(request.getUserId());
        payment.setAmount(request.getAmount());
        payment.setTransactionId(transactionId);
        payment.setSuccess(success);
        paymentRepository.save(payment);

        return new PaymentResponse(success, transactionId, "Payment authorized successfully (mock)");
    }
}
