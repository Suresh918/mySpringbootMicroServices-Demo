package com.example.mirai.libraries.notification.settings.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@Getter
@Setter
@IdClass(SubscriptionId.class)
@AllArgsConstructor
public class EntitySubscription {
    @Id
    private String userId;
    @Id
    private Long caseId;
    @Id
    private String caseType;
    private String email;
    public void setCaseType(String type) {
        this.caseType = type.toUpperCase();
    }
    public  EntitySubscription() {
    }
}
