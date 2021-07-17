package com.example.mirai.projectname.notificationservice.engine.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration("projectname.notification-service.mail.MailConfigurationProperties")
@ConfigurationProperties(prefix = "projectname.notification-service.mail")
@Data
public class MailConfigurationProperties {
	/**
	 * Enable sending of projectname mail notifications.
	 */
	private Boolean enabled = true;

	/**
	 * From mail address to use when sending projectname mail notifications.
	 */
	private String from;
}
