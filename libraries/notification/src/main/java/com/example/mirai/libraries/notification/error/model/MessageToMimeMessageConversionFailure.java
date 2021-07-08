package com.example.mirai.libraries.notification.error.model;

import com.example.mirai.libraries.core.model.Event;
import lombok.Data;

@Data
public class MessageToMimeMessageConversionFailure extends IrrecoverableNotificationException {
    public MessageToMimeMessageConversionFailure(Exception exception, String description, Event event) {
        super(exception,description, event);
    }
}
