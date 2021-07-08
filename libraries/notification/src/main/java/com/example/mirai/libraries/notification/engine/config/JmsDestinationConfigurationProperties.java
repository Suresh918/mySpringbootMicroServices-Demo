package com.example.mirai.libraries.notification.engine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mirai.notification-service.jms")
@Data
public class JmsDestinationConfigurationProperties {
	/**
	 * Destination to be used to send in-app notifications.
	 */
	private String inAppDestination = "com.example.mirai.notificationservice.notification.inapp";
	private String settingsDestination = "com.example.mirai.notificationservice.notification.settings";
}
