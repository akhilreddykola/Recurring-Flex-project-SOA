package com.recurringflex.subscription.service;

import com.recurringflex.subscription.client.PaymentClient;
import com.recurringflex.subscription.dto.PaymentResponse;
import com.recurringflex.subscription.dto.SubscriptionRequest;
import com.recurringflex.subscription.entity.Subscription;
import com.recurringflex.subscription.entity.SubscriptionStatus;
import com.recurringflex.subscription.repository.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionService.class);
    private static final int BILLING_CYCLE_DAYS = 30;

    private final SubscriptionRepository subscriptionRepository;
    private final PaymentClient paymentClient;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, PaymentClient paymentClient) {
        this.subscriptionRepository = subscriptionRepository;
        this.paymentClient = paymentClient;
    }

    public Subscription activateSubscription(SubscriptionRequest request) {
        PaymentResponse paymentResponse = paymentClient.authorizePayment(request.getUserId(), request.getAmount());

        if (paymentResponse == null || !paymentResponse.isSuccess()) {
            throw new IllegalStateException("Payment authorization failed. Subscription not activated.");
        }

        Subscription subscription = new Subscription();
        subscription.setUserId(request.getUserId());
        subscription.setPlanName(request.getPlanName());
        subscription.setAmount(request.getAmount());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusDays(BILLING_CYCLE_DAYS));

        return subscriptionRepository.save(subscription);
    }

    public Subscription renewSubscription(Long id) {
        Subscription subscription = getById(id);

        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            throw new IllegalStateException("Cannot renew a cancelled subscription.");
        }

        PaymentResponse paymentResponse = paymentClient.authorizePayment(subscription.getUserId(), subscription.getAmount());

        if (paymentResponse == null || !paymentResponse.isSuccess()) {
            throw new IllegalStateException("Payment authorization failed. Subscription not renewed.");
        }

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setEndDate(LocalDateTime.now().plusDays(BILLING_CYCLE_DAYS));
        return subscriptionRepository.save(subscription);
    }

    public Subscription cancelSubscription(Long id) {
        Subscription subscription = getById(id);
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        return subscriptionRepository.save(subscription);
    }

    public Subscription getById(Long id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found with id: " + id));
    }

    public List<Subscription> getByUserId(Long userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    public List<Subscription> getAll() {
        return subscriptionRepository.findAll();
    }

    // Runs every hour: marks ACTIVE subscriptions past their endDate as EXPIRED
    public void expireOverdueSubscriptions() {
        List<Subscription> overdue = subscriptionRepository
                .findByStatusAndEndDateBefore(SubscriptionStatus.ACTIVE, LocalDateTime.now());

        for (Subscription sub : overdue) {
            sub.setStatus(SubscriptionStatus.EXPIRED);
            subscriptionRepository.save(sub);
            log.info("Subscription id={} marked as EXPIRED", sub.getId());
        }
    }
}
