package com.example.mirai.services.userservice;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("it")
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public abstract class BaseTest {
	public static final String CONTENT_TYPE = "application/json";

	@Container
	private static final GenericContainer emsContainer = new GenericContainer("artifactory-iwf.example.com/mirai-docker/ems:8.5.1")
			.withExposedPorts(7222)
			.withEnv("EMS_CONFIG", "/opt/tibco/ems/docker/tibemsd-configbase.json");

	/**
	 * Prevent call to `issuer-uri`.
	 */
	@MockBean
	protected JwtDecoder jwtDecoder;

	@Autowired
	protected WebApplicationContext webApplicationContext;

	MockMvc mockMvc;

	@BeforeAll
	private static void setEmsProperties() {
		System.setProperty("mirai.libraries.jms.url", "tcp://" + emsContainer.getContainerIpAddress() + ":" + emsContainer.getFirstMappedPort());
	}

	protected MockMvc getMockMvc() {
		if (mockMvc == null) {
			mockMvc = MockMvcBuilders
					.webAppContextSetup(webApplicationContext)
					.apply(SecurityMockMvcConfigurers.springSecurity())
					.build();
		}
		return mockMvc;
	}
}
