package com.example.mirai.projectname.services.configuration;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration("configurationServiceConfigurationProperties")
@ConfigurationProperties(prefix = "projectname.services.configuration-service")
public class ConfigurationServiceConfigurationProperties {
	/**
	 * CUG names authorized to perform write operations on form
	 */
	String[] formAdminRoles = new String[] { "administrator" };

	/**
	 * CUG names authorized to perform write operations on link
	 */
	String[] linkAdminRoles = new String[] { "administrator" };

	/**
	 * CUG names authorized to perform write operations on product category
	 */
	String[] productCategoryAdminRoles = new String[] { "administrator" };

	/**
	 * CUG names authorized to perform write operations on rule set
	 */
	String[] ruleSetAdminRoles = new String[] { "administrator" };

	/**
	 * CUG names authorized to perform write operations on tag
	 */
	String[] tagAdminRoles = new String[] { "administrator" };
}
