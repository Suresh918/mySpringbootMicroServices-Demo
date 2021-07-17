package com.example.mirai.services.gds.tests;

import java.util.UUID;

import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.services.gds.fixtures.JwtFactory;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public abstract class BaseTest {
	@MockBean
	protected JwtDecoder jwtDecoder;

	@Autowired
	protected WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	protected MockMvc getMockMvc() {
		if (mockMvc == null) {
			mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		}
		return mockMvc;
	}

	@BeforeEach
	void initializeSecurityContext() {
		String dataIdentifier = UUID.randomUUID().toString();
		Jwt jwt = JwtFactory.getJwtToken(dataIdentifier);
		SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
	}

}
