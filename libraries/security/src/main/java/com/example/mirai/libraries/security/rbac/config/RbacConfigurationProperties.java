package com.example.mirai.libraries.security.rbac.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration("mirai.libraries.security.rbac.RbacConfigurationProperties")
@ConfigurationProperties(prefix = "mirai.libraries.security.rbac")
@Data
public class RbacConfigurationProperties {
	/**
	 * Enable Resource Based Access Control for CRUD services.
	 */
	private Boolean enabled = true;
}
