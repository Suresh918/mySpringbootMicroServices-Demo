package com.example.mirai.libraries.notification.settings.controller;

import com.example.mirai.libraries.notification.settings.model.EntitySubscription;
import com.example.mirai.libraries.notification.settings.service.EntitySubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EntitySubscriptionController {

    private final EntitySubscriptionService entitySubscriptionService;

    EntitySubscriptionController(EntitySubscriptionService entitySubscriptionService) {
        this.entitySubscriptionService = entitySubscriptionService;
    }

    @PostMapping({
            "/notifications/subscribe"
    })
    @ResponseStatus(HttpStatus.OK)
    public void subscribeToEntity(@RequestBody EntitySubscription entitySubscription) {
        entitySubscriptionService.subscribeToEntity(entitySubscription);
    }
    @GetMapping({
            "/notifications/subscription"
    })
    @ResponseStatus(HttpStatus.OK)
    public List<EntitySubscription> getEntitySubscription(@RequestParam(name="case-id") Long caseId, @RequestParam(name="case-type") String caseType) {
        return entitySubscriptionService.getEntitySubscription(caseId, caseType);
    }
    @DeleteMapping({
            "/notifications/unsubscribe"
    })
    @ResponseStatus(HttpStatus.OK)
    public void unsubscribeToEntity(@RequestBody EntitySubscription entitySubscription) {
        entitySubscriptionService.unsubscribeToEntity(entitySubscription);
    }
}
