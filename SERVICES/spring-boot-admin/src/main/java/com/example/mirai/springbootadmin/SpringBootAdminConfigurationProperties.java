package com.example.mirai.springbootadmin;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mirai.spring-boot-admin")
@Data
public class SpringBootAdminConfigurationProperties {
	/**
	 * CUG names authorized to access Spring Boot Admin
	 */
	private String[] adminRoles = { "cug-mirai-development", "cug-mirai-support" };
}
