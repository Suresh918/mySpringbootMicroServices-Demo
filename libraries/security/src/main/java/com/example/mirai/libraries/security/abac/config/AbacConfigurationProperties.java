package com.example.mirai.libraries.security.abac.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mirai.libraries.security.abac")
@Data
public class AbacConfigurationProperties {
	/**
	 * Enable Attribute Based Access Control for CRUD services.
	 */
	private Boolean enabled = true;

	/**
	 * This property only comes into affect when spring cache is set to any value except NONE.
	 * When this property is set, caching is performed in a separate thread.
	 */
	private Boolean cacheAsynchronously = false;
}
