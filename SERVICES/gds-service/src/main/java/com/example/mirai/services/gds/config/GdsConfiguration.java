package com.example.mirai.services.gds.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.ldap.repository.config.EnableLdapRepositories;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.pool2.factory.PoolConfig;
import org.springframework.ldap.pool2.factory.PooledContextSource;

@Configuration
@ConfigurationPropertiesScan
@EnableLdapRepositories(basePackages = { "com.example.mirai.services.gds.repository" })
public class GdsConfiguration {
	private final GdsConfigurationProperties gdsConfigurationProperties;

	public GdsConfiguration(GdsConfigurationProperties gdsConfigurationProperties) {
		this.gdsConfigurationProperties = gdsConfigurationProperties;
	}

	@Bean
	ContextSource contextSource(LdapContextSource ldapContextSource) {
		// Define Connection Pool Configuration
		PoolConfig poolConfig = new PoolConfig();
		poolConfig.setMaxTotalPerKey(-1);
		poolConfig.setTestWhileIdle(true);

		// Initialize Connection Pool
		PooledContextSource pooledContextSource = new PooledContextSource(poolConfig);
		pooledContextSource.setContextSource(ldapContextSource);
		return pooledContextSource;
	}

	@Bean
	LdapTemplate ldapTemplate(ContextSource contextSource) {
		LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
		ldapTemplate.setDefaultTimeLimit(gdsConfigurationProperties.getTimeLimit());
		ldapTemplate.setDefaultCountLimit(gdsConfigurationProperties.getCountLimit());
		return ldapTemplate;
	}
}
