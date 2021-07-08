package com.example.mirai.libraries.notification.error.model;

import com.example.mirai.libraries.core.model.Event;
import lombok.Data;

@Data
public class UnableToFetchDelegates extends IrrecoverableNotificationException {
    private String userId;

    public UnableToFetchDelegates(Exception exception, String description, String userId, Event event) {
        super(exception, description, event);
        this.userId = userId;
    }
}
