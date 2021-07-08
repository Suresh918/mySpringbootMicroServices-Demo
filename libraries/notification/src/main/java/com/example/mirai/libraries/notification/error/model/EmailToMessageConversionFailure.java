package com.example.mirai.libraries.notification.error.model;

import com.example.mirai.libraries.core.model.Event;
import lombok.Data;

@Data
public class EmailToMessageConversionFailure extends IrrecoverableNotificationException implements UserAwareException {
    private String userId;

    public EmailToMessageConversionFailure(Exception exception, String description, String userId, Event event) {
        super(exception, description, event);
        this.userId = userId;
    }
}
