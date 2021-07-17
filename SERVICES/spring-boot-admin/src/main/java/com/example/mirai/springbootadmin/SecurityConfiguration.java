package com.example.mirai.springbootadmin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	SpringBootAdminConfigurationProperties springBootAdminConfigurationProperties;

	public SecurityConfiguration(SpringBootAdminConfigurationProperties springBootAdminConfigurationProperties) {
		this.springBootAdminConfigurationProperties = springBootAdminConfigurationProperties;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
				.antMatchers(HttpMethod.GET, "/actuator/health").permitAll()
				.antMatchers(HttpMethod.GET, "/assets/**").permitAll()
				.anyRequest().hasAnyRole(springBootAdminConfigurationProperties.getAdminRoles())
				.and()
				.csrf()
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				.ignoringRequestMatchers(
						new AntPathRequestMatcher("/instances", HttpMethod.POST.toString()),
						new AntPathRequestMatcher("/instances/*", HttpMethod.DELETE.toString()),
						new AntPathRequestMatcher("/actuator/**")
				)
				.and()
				.oauth2Login()
				.userInfoEndpoint().userAuthoritiesMapper(this.userAuthoritiesMapper());
	}

	private GrantedAuthoritiesMapper userAuthoritiesMapper() {
		return authorities -> {
			Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

			authorities.forEach(authority -> {
				if (authority instanceof OAuth2UserAuthority) {
					OAuth2UserAuthority oauth2UserAuthority = (OAuth2UserAuthority) authority;
					ArrayList<String> userRoles = (ArrayList<String>) oauth2UserAuthority.getAttributes().get("group_membership");
					if (userRoles != null && !userRoles.isEmpty())
						userRoles.forEach(userRole -> mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + userRole.toLowerCase())));
				}
			});
			return mappedAuthorities;
		};
	}
}
