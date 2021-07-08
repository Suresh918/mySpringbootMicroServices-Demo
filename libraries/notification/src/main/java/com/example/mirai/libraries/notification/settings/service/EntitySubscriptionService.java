package com.example.mirai.libraries.notification.settings.service;

import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.notification.settings.repository.EntitySubscriptionRepository;
import com.example.mirai.libraries.notification.core.AuthenticatedContext;
import com.example.mirai.libraries.notification.settings.model.EntitySubscription;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntitySubscriptionService {

    private final AuthenticatedContext authenticatedContext;
    private final EntitySubscriptionRepository entitySubscriptionRepository;

    EntitySubscriptionService(EntitySubscriptionRepository entitySubscriptionRepository, AuthenticatedContext authenticatedContext) {
        this.authenticatedContext = authenticatedContext;
        this.entitySubscriptionRepository = entitySubscriptionRepository;
    }
    public void subscribeToEntity(EntitySubscription entitySubscription) {
        String userId = authenticatedContext.getUserId();
        User user = authenticatedContext.getAuditableUser();
        entitySubscription.setUserId(userId);
        entitySubscription.setCaseType(entitySubscription.getCaseType().toUpperCase());
        entitySubscription.setEmail(authenticatedContext.getEmail());
        //entitySubscription.setUser(user);
        entitySubscriptionRepository.save(entitySubscription);
    }

    public void unsubscribeToEntity(EntitySubscription entitySubscription) {
        String userId = authenticatedContext.getUserId();
        entitySubscription.setUserId(userId);
        entitySubscription.setEmail(authenticatedContext.getEmail());
        entitySubscription.setCaseType(entitySubscription.getCaseType().toUpperCase());
        entitySubscriptionRepository.delete(entitySubscription);
    }

    public List<EntitySubscription> getSubscriptionsToEvent(Long entityId, String entityType) {
       return entitySubscriptionRepository.findEntitySubscriptionsByCaseIdAndCaseType(entityId, entityType);
    }

    public List<EntitySubscription> getEntitySubscription(Long caseId, String caseType) {
        String userId = authenticatedContext.getUserId();
        List<EntitySubscription> entitySubscriptions = entitySubscriptionRepository.findByCaseIdAndCaseTypeAndUserId(caseId, caseType.toUpperCase(), userId);
        return entitySubscriptions;
    }

}
