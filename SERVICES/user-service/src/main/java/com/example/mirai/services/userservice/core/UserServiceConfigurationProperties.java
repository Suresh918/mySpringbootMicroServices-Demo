package com.example.mirai.services.userservice.core;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration("mychangeUserServiceConfigurationProperties")
@ConfigurationProperties(prefix = "projectname.user-service")
public class UserServiceConfigurationProperties {
	/**
	 * CUG names authorized to perform write operations on form
	 */
	String[] sdlTibcoRoles = new String[] { "ROLE_tibco" };
}
