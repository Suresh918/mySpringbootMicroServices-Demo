package com.example.mirai.libraries.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${spring.security.oauth2.client.registration.nam.client-id:}')")
public class ClientCredentialWebClientConfiguration {
	@Bean
	ReactiveClientRegistrationRepository getRegistration(
			@Value("${spring.security.oauth2.client.registration.nam.client-id}") String clientId,
			@Value("${spring.security.oauth2.client.registration.nam.client-secret}") String clientSecret,
			@Value("${spring.security.oauth2.client.provider.nam.issuer-uri}") String issuerUri,
			@Value("${spring.security.oauth2.client.provider.nam.token-uri}") String tokenUri,
			@Value("${spring.security.oauth2.client.provider.nam.authorization-uri}") String authorizationUri
	) {
		ClientRegistration registration = ClientRegistration
				.withRegistrationId("nam")
				.issuerUri(issuerUri)
				.tokenUri(tokenUri)
				.authorizationUri(authorizationUri)
				.clientId(clientId)
				.clientSecret(clientSecret)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.build();
		return new InMemoryReactiveClientRegistrationRepository(registration);
	}

	@Bean
	public AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager(
			ReactiveClientRegistrationRepository clientRegistrationRepository) {

		ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider =
				ReactiveOAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build();

		ReactiveOAuth2AuthorizedClientService authorizedClientService =
				new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationRepository);

		AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager =
				new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientService);

		authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

		return authorizedClientManager;
	}

	@Bean("clientCredentialWebClient")
	WebClient webClient(ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {

		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth =
				new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
		oauth.setDefaultOAuth2AuthorizedClient(true);
		oauth.setDefaultClientRegistrationId("nam");
		return WebClient.builder()
				.filter(oauth)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.build();
	}
}
