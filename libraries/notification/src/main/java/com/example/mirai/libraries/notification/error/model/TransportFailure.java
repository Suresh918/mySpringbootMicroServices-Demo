package com.example.mirai.libraries.notification.error.model;

import lombok.Data;

@Data
public class TransportFailure extends RecoverableNotificationException implements UserAwareException {
    private String userId;

    public TransportFailure(Exception exception, String description, String userId) {
        super(exception, description);
        this.userId = userId;
    }
}
