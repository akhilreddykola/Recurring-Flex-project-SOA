package com.recurringflex.subscription.repository;

import com.recurringflex.subscription.entity.Subscription;
import com.recurringflex.subscription.entity.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUserId(Long userId);
    List<Subscription> findByStatusAndEndDateBefore(SubscriptionStatus status, LocalDateTime dateTime);
}
