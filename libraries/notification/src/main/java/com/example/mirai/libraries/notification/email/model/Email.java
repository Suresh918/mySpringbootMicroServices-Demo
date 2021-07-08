package com.example.mirai.libraries.notification.email.model;

import com.example.mirai.libraries.core.model.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Email {
    private String to;
    private String subject;
    private String content;
    private String[] cc;
    private Event event;
}
