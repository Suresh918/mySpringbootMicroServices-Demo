package com.example.mirai.libraries.entity.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration("miraiLibrariesEntityInternalApplicationEventConfigurationProperties")
@ConfigurationProperties(prefix = "mirai.libraries.entity.internal-application-event")
@Data
public class InternalApplicationEventConfigurationProperties {
	/**
	 * Enable publication of internal application event, other libraries such as security/cache rely on this.
	 */
	private Boolean enabled = true;

	/**
	 * Enable this to perform publication in a separate thread.
	 */
	private Boolean asynchronous = false;
}
