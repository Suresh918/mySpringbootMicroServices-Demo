package com.example.mirai.libraries.websecurity.conf;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class RoleConfiguration {
	private final Map<String, String> roleMap;

	public RoleConfiguration(WebSecurityConfigurationProperties webSecurityConfigurationProperties) {
		roleMap = new HashMap<>();
		Arrays.stream(webSecurityConfigurationProperties.getAuthorizedUser()).forEach(cug -> roleMap.put(cug.toLowerCase(), "user"));
		Arrays.stream(webSecurityConfigurationProperties.getChangeSpecialist1()).forEach(cug -> roleMap.put(cug.toLowerCase(), "change-specialist-1"));
		Arrays.stream(webSecurityConfigurationProperties.getChangeSpecialist2()).forEach(cug -> roleMap.put(cug.toLowerCase(), "change-specialist-2"));
		Arrays.stream(webSecurityConfigurationProperties.getChangeSpecialist3()).forEach(cug -> roleMap.put(cug.toLowerCase(), "change-specialist-3"));
		Arrays.stream(webSecurityConfigurationProperties.getAdministrator()).forEach(cug -> roleMap.put(cug.toLowerCase(), "administrator"));
		Arrays.stream(webSecurityConfigurationProperties.getTibco()).forEach(clientId -> roleMap.put(clientId, "tibco"));
		Arrays.stream(webSecurityConfigurationProperties.getMychange()).forEach(clientId -> roleMap.put(clientId, "projectname"));
		Arrays.stream(webSecurityConfigurationProperties.getSupplyChainProjectCoordinator()).forEach(cug -> roleMap.put(cug, "supply-chain-project-coordinator"));
	}

	public String getRole(String cug) {
		return roleMap.get(cug);
	}
}
