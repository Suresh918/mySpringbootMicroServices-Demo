package com.example.mirai.projectname.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;

@Configuration
public class SecurityConfiguration {

	@Bean
	SecurityWebFilterChain configure(ServerHttpSecurity http) {
		http
				.csrf().disable()
				.authorizeExchange()
				.pathMatchers("/actuator/**").permitAll()
				.anyExchange().authenticated()
				.and()
				.oauth2Login()
				.and()
				.oauth2ResourceServer().jwt();
		return http.build();
	}

	/**
	 * Spring Security stores tokens in-memory by default.
	 * This forces to store OAuth2 tokens in the session.
	 */
	@Bean
	ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
		return new WebSessionServerOAuth2AuthorizedClientRepository();
	}

	/**
	 * Explicitly set session cookie SameSite attribute to Strict as hardening measure
	 */
	@Bean
	public WebSessionIdResolver webSessionIdResolver() {
		CookieWebSessionIdResolver resolver = new CookieWebSessionIdResolver();
		resolver.addCookieInitializer(builder -> builder.sameSite("Strict"));
		return resolver;
	}
}
