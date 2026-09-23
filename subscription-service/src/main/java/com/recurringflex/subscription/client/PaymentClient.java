package com.recurringflex.subscription.client;

import com.recurringflex.subscription.dto.PaymentRequest;
import com.recurringflex.subscription.dto.PaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PaymentClient {

    @Autowired
    private RestTemplate restTemplate;

    // PAYMENT-SERVICE is resolved via Eureka using the @LoadBalanced RestTemplate
    private static final String PAYMENT_SERVICE_URL = "http://PAYMENT-SERVICE/api/payments/authorize";

    public PaymentResponse authorizePayment(Long userId, java.math.BigDecimal amount) {
        PaymentRequest request = new PaymentRequest(userId, amount);
        return restTemplate.postForObject(PAYMENT_SERVICE_URL, request, PaymentResponse.class);
    }
}
