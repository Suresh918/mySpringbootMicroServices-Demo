package com.example.mirai.services.userservice.preferredrole;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mirai.services.user-service.preferred-role")
@Data
public class PreferredRoleConfigurationProperties {

	/**
	 * Topic on which to publish user profile.
	 */
	private String destination = "com.example.mirai.services.userservice.preferredrole";

	private Boolean publishEnabled = true;
}
