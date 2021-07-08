package com.example.mirai.libraries.event.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mirai.libraries.event")
@Data
public class EventConfigurationProperties {
	/**
	 * Enable this to perform event publication in separate thread.
	 */
	private Boolean asynchronous = false;

}
