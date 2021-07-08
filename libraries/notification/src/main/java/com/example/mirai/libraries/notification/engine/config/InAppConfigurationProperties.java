package com.example.mirai.libraries.notification.engine.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mirai.notification-service.in-app")
@Data
public class InAppConfigurationProperties {
	/**
	 * Enable sending of in app notifications.
	 */
	private Boolean enabled = true;
}
