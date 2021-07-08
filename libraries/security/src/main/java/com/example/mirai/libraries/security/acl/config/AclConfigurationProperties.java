package com.example.mirai.libraries.security.acl.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration("mirai.libraries.security.acl")
@ConfigurationProperties(prefix = "mirai.libraries.security.acl")
@Data
public class AclConfigurationProperties {
	/**
	 * Set location of the JSON file that contains the configuration of all possible case actions on the entities.
	 */
	private String caseActionList;

	/**
	 * Set location of the JSON file that contains the configuration of all permissible case actions on the entities.
	 */
	private String entityAcl;

	/**
	 * Set location of the JSON file that contains the configuration of all permissible operations that can be performed on the properties of the entities.
	 */
	private String propertyAcl;
}
