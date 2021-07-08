package com.example.mirai.libraries.notification.error.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class RecoverableNotificationException extends RuntimeException {
    private Exception exception;
    private String description;
}
