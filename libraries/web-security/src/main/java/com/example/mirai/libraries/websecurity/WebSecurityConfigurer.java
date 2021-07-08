package com.example.mirai.libraries.websecurity;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.mirai.libraries.websecurity.conf.RoleConfiguration;
import com.example.mirai.libraries.websecurity.roleinjector.RoleInjectorServiceInterface;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

@Configuration
@EnableGlobalMethodSecurity(
		prePostEnabled = true,
		securedEnabled = true,
		jsr250Enabled = true)
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {
	private static final String ROLE_PREFIX = "ROLE_";

	private static ApplicationContext applicationContext;

	public WebSecurityConfigurer(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * https://github.com/spring-projects/spring-security/issues/7834
	 */
	public static Converter<Jwt, PrincipalAwareJwtAuthenticationToken> authenticationConverter() {
		return jwt -> {
			RoleConfiguration roleConfiguration = applicationContext.getBean(RoleConfiguration.class);
			String subject = jwt.getClaimAsString("sub");
			String role = roleConfiguration.getRole(subject);
			if (Objects.nonNull(role)) {
				Set<GrantedAuthority> authorities = new HashSet<>();
				SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(ROLE_PREFIX + role);
				authorities.add(simpleGrantedAuthority);
				return new PrincipalAwareJwtAuthenticationToken(jwt, authorities, true);
			}
			else {
				Collection<String> groupMemberships = jwt.getClaimAsStringList("group_membership");
				Collection<GrantedAuthority> authorities = Collections.emptySet();
				if (groupMemberships != null && !groupMemberships.isEmpty()) {
					authorities = groupMemberships.stream()
							.map(groupMembership -> new SimpleGrantedAuthority(ROLE_PREFIX + getMembership(roleConfiguration, groupMembership)))
							.filter(authority -> !authority.getAuthority().equals(ROLE_PREFIX + "null"))
							.collect(Collectors.toSet());
				}

				RoleInjectorServiceInterface roleInjectorService = null;
				try {
					roleInjectorService = applicationContext.getBean(RoleInjectorServiceInterface.class);
				}
				catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
					//do nothing, not all microservices implement RoleInjectorServiceInterface
				}
				if (Objects.nonNull(roleInjectorService)) {
					String userId = (String) Util.getClaim(jwt, "user_id");
					if (Objects.nonNull(roleInjectorService.getRoles(userId))) {
						Collection<GrantedAuthority> injectedAuthorities = roleInjectorService.getRoles(userId).stream()
								.map(injectedRole -> new SimpleGrantedAuthority(ROLE_PREFIX + injectedRole))
								.collect(Collectors.toSet());
						authorities.addAll(injectedAuthorities);
					}
				}
				return new PrincipalAwareJwtAuthenticationToken(jwt, authorities, false);
			}
		};
	}

	private static String getMembership(RoleConfiguration roleConfiguration, String groupMembership) {
		String role = roleConfiguration.getRole(groupMembership.toLowerCase());
		if (Objects.isNull(role))
			return groupMembership;
		return role;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.csrf().disable()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.authorizeRequests()
				.antMatchers("/actuator/**").permitAll()
				.anyRequest().authenticated()
				.and()
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(authenticationConverter())
						)
				);
	}
}
