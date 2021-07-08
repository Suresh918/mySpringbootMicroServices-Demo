package com.example.mirai.libraries.webclient;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConfigurationPropertiesScan
public class BasicAuthorizationWebClientConfiguration {
	@Bean("basicAuthorizationWebClient")
	WebClient webClient() {
		return null;
	}
}
