package com.example.mirai.projectname.notificationservice.engine.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration("projectname.notification-service.in-app.InAppConfigurationProperties")
@ConfigurationProperties(prefix = "projectname.notification-service.in-app")
@Data
public class InAppConfigurationProperties {
	/**
	 * Enable sending of projectname in app notifications.
	 */
	private Boolean enabled = true;
}
