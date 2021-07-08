package com.example.mirai.libraries.websecurity.conf;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mirai.libraries.web-security")
@Data
public class WebSecurityConfigurationProperties {
	/**
	 * CUG that will be used to assign Authorized User role
	 */
	private String[] authorizedUser = {};

	/**
	 * CUGs that will be used to assign Change Specialist 1 role
	 */
	private String[] changeSpecialist1 = { "cug-projectname-change-specialist-1", "cug-projectname-cs-1-dt" };

	/**
	 * CUGs that will be used to assign Change Specialist 2 User role
	 */
	private String[] changeSpecialist2 = { "cug-projectname-change-specialist-2", "cug-projectname-cs-2-dt" };

	/**
	 * CUGs that will be used to assign Change Specialist 3 User role
	 */
	private String[] changeSpecialist3 = { "cug-projectname-change-specialist-3", "cug-projectname-cs-3-dt" };

	/**
	 * CUGs that will be used to assign Administrator role
	 */
	private String[] administrator = {};

	/**
	 * Subs that will be used to assign TIBCO role
	 */
	private String[] tibco = {};

	/**
	 * Subs that will be used to assign TIBCO role
	 */
	private String[] projectname = {};

	/**
	 * CUGs that will be used to assign Supply Chain Project Coordinator User role
	 */
	private String[] supplyChainProjectCoordinator = { "cug-sc-project-coordinator" };
}
