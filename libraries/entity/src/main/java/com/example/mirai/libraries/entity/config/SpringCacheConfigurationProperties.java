package com.example.mirai.libraries.entity.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration("entityConfigSpringCacheConfiguration")
@ConfigurationProperties(prefix = "spring.cache")
@Data
public class SpringCacheConfigurationProperties {
	/**
	 * Explicitly disable standard Spring Caching
	 */
	private String type = "none";
}
