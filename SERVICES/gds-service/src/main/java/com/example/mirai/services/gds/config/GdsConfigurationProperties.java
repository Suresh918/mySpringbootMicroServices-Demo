package com.example.mirai.services.gds.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mirai.services.gds")
@Data
public class GdsConfigurationProperties {
	/**
	 * Time limit for searches, in milliseconds. 0 means no limit.
	 */
	private Integer timeLimit = 5000;

	/**
	 * Default count limit for searches. 0 means no limit.
	 */
	private Integer countLimit = 1000;
}
