package com.example.mirai.libraries.notification.engine.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mirai.notification-service.link")
@Data
public class LinkConfigurationProperties {
	/**
	 * URL to be used in notifications to construct link to fetch people profile photo's.
	 */
	private String photoUrl = "https://people.example.com/_layouts/15/userphoto.aspx?size=M&accountname=example-com%5C{USER-ID}";

	/**
	 * Environment specific base URL to be used in notifications to construct deep link into services.
	 */
	private String baseUrl;

	/**
	 * Partial URL to be used in notifications to construct deep link into notifications settings page.
	 */
	private String notifications = "/settings/notifications";
}
