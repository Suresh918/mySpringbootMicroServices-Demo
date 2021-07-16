package com.example.mirai.projectname.libraries.user.service;

import com.example.mirai.projectname.libraries.user.config.UserConfigurationProperties;
import com.example.mirai.projectname.libraries.user.model.PreferredRole;
import com.example.mirai.projectname.libraries.user.shared.exception.ExceptionFactory;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServletBearerExchangeFilterFunction;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
@Slf4j
public class UserService {
	private WebClient webClient;

	public UserService(UserConfigurationProperties userConfigurationProperties) {
		this.webClient = WebClient.builder()
				.baseUrl(userConfigurationProperties.getBaseUrl())
				.filter(new ServletBearerExchangeFilterFunction())
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.build();
	}

	public List<PreferredRole> getPreferredRolesByUserIds(List<String> userIds) {
		try {
			return this.webClient.get().uri(uriBuilder -> uriBuilder.path("/preferred-roles")
					.queryParam("user-ids", userIds)
					.build()).retrieve()
					.bodyToFlux(PreferredRole.class).collectList().block();
		}
		catch (WebClientResponseException webClientResponseException) {
			throw ExceptionFactory.getExceptionInstance(webClientResponseException);
		}
	}
}

