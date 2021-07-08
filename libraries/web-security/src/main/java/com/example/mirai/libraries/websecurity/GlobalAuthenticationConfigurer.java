package com.example.mirai.libraries.websecurity;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class GlobalAuthenticationConfigurer extends GlobalAuthenticationConfigurerAdapter {
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(new AuthenticationProvider() {
			@Override
			public Authentication authenticate(Authentication authentication) throws AuthenticationException {
				return authentication;
			}

			@Override
			public boolean supports(Class<?> authentication) {
				return PrincipalAwareJwtAuthenticationToken.class.isAssignableFrom(authentication);
			}
		});
	}
}
