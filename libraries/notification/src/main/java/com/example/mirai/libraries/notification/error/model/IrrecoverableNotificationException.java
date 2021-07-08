package com.example.mirai.libraries.notification.error.model;

import com.example.mirai.libraries.core.model.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class IrrecoverableNotificationException extends RuntimeException {
    private Exception exception;
    private String description;
    private Event event;
}
