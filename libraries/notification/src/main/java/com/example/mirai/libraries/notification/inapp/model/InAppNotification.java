package com.example.mirai.libraries.notification.inapp.model;

import com.example.mirai.libraries.core.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InAppNotification {
	String category;
	String role;
	String entity;
	User actor;
	User recipient;
	Long timestamp;
	Long entityId;
	String title;
}
