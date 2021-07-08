package com.example.mirai.libraries.webclient;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServletBearerExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConfigurationPropertiesScan
public class BearerTokenRelayWebClientConfiguration {
	@Bean("bearerTokenRelayWebClient")
	WebClient webClient() {
		return WebClient.builder()
				.filter(new ServletBearerExchangeFilterFunction())
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.build();
	}
}
