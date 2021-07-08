package com.example.mirai.libraries.notification.engine.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mirai.notification-service.mail")
@Data
public class MailConfigurationProperties {
	/**
	 * Enable sending of mail notifications.
	 */
	private Boolean enabled = true;

	/**
	 * From mail address to use when sending mail notifications.
	 */
	private String from;
}
