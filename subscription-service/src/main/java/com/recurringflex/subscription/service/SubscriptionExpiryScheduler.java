package com.recurringflex.subscription.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionExpiryScheduler {

    private final SubscriptionService subscriptionService;

    public SubscriptionExpiryScheduler(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    // Every hour, check for subscriptions whose end date has passed
    @Scheduled(fixedRate = 3600000)
    public void checkExpiredSubscriptions() {
        subscriptionService.expireOverdueSubscriptions();
    }
}
