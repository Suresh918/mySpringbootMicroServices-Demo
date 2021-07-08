package com.example.mirai.libraries.notification.settings.model;

import java.io.Serializable;
import java.util.List;

import com.example.mirai.libraries.core.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Subscription implements Serializable {
	private String event;

	private String role;

	private EmailChannel emailChannel;

	private InAppChannel inAppChannel;

	private List<User> delegates;
}
