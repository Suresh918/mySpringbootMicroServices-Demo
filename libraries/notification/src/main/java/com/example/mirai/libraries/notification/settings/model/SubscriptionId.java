package com.example.mirai.libraries.notification.settings.model;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class SubscriptionId implements Serializable {
    private String userId;
    private Long caseId;
    private String caseType;
}
