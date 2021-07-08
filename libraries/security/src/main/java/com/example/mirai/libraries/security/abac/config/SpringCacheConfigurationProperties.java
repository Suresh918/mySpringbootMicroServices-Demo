package com.example.mirai.libraries.security.abac.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration("securityAbacConfigSpringCacheConfiguration")
@ConfigurationProperties(prefix = "spring.cache")
@Data
public class SpringCacheConfigurationProperties {
	//extract spring.cache properties
	private String type = "NONE";

}
