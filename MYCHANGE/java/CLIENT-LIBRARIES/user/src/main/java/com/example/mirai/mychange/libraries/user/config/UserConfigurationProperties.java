package com.example.mirai.projectname.libraries.user.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mirai.projectname.libraries.user")
@Data
public class UserConfigurationProperties {
	/**
	 * Base URL to be used to access service endpoint.
	 */
	String baseUrl = "http://projectname-gateway.projectname:80/api/user-service";
}
