package com.example.mirai.libraries.notification.settings.repository;

import com.example.mirai.libraries.notification.settings.model.EntitySubscription;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EntitySubscriptionRepository extends CrudRepository<EntitySubscription, Long> {
    List<EntitySubscription> findEntitySubscriptionsByCaseIdAndCaseType(Long entityId, String entityType);
    List<EntitySubscription> findByCaseIdAndCaseTypeAndUserId(Long caseId, String caseType, String userId);
}
