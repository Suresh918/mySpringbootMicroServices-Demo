package com.example.mirai.services.userservice.state;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.services.userservice.BaseTest;
import com.example.mirai.services.userservice.fixtures.EntityPojoGenerationFactory;
import com.example.mirai.services.userservice.fixtures.JwtFactory;
import com.example.mirai.services.userservice.fixtures.TestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StateIT extends BaseTest {
	private static final String STATES_URL = "/states";

	private static final String USER_ID = "<USER_ID>";

	private static String getStateExpectedContent;

	private static String updateStateExpectedContent;

	static {
		InputStream getStateInputStream = StateIT.class.getResourceAsStream("/expectations/state/GetStateResponseContent.json");
		BufferedReader getStateBufferedReader = new BufferedReader(new InputStreamReader(getStateInputStream, StandardCharsets.UTF_8));
		getStateExpectedContent = getStateBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

		InputStream updateStateInputStream = StateIT.class.getResourceAsStream("/expectations/state/UpdateStateResponseContent.json");
		BufferedReader updateStateBufferedReader = new BufferedReader(new InputStreamReader(updateStateInputStream, StandardCharsets.UTF_8));
		updateStateExpectedContent = updateStateBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
	}

	@Autowired
	private StateRepository stateRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void getStateWorks() throws Exception {
		String userId = TestUtil.generateRandomAlphanumericString(10);

		// Generate State object populated with dummy data
		State state = EntityPojoGenerationFactory.generateState();
		state.setUserId(userId);

		stateRepository.save(state);

		// Generate Bearer Token
		Jwt jwt = JwtFactory.getJwtToken(userId, List.of("user"));
		SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

		// Call update state service
		MvcResult result = getMockMvc().perform(
				get(STATES_URL)
						.contentType(CONTENT_TYPE)
						.content(objectMapper.writeValueAsString(state)))
				// Verify returned HTTP code to be 200
				.andExpect(status().isOk()).andReturn();
		// Verify that states saved in database and states returned by service are exact match
		String responseContent = result.getResponse().getContentAsString();
		getStateExpectedContent = getStateExpectedContent.replace(USER_ID, userId);
		JSONAssert.assertEquals("state is as expected", getStateExpectedContent, responseContent, JSONCompareMode.NON_EXTENSIBLE);

	}

	@Test
	void getStateReturnsNoContent() throws Exception {
		String userId = TestUtil.generateRandomAlphanumericString(10);

		// Generate state object populated with dummy data
		State state = EntityPojoGenerationFactory.generateState();
		state.setUserId(userId);

		// Generate Bearer Token
		Jwt jwt = JwtFactory.getJwtToken(userId, List.of("user"));
		SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

		// Call update states service
		getMockMvc().perform(
				get(STATES_URL)
						.contentType(CONTENT_TYPE)
						.content(objectMapper.writeValueAsString(state)))
				// Verify returned HTTP code to be 200
				.andExpect(status().isNoContent())
				// Verify that nothing is returned
				.andExpect(content().string(""));
	}

	@Test
	void updateStateWorks() throws Exception {
		String userId = TestUtil.generateRandomAlphanumericString(10);

		// Generate state object populated with dummy data
		State state = EntityPojoGenerationFactory.generateState();
		state.setUserId(userId);

		stateRepository.save(state);
		//update the sate in state object
		state.setUserId(userId + "1");

		// Generate Bearer Token
		Jwt jwt = JwtFactory.getJwtToken(userId, List.of("user"));
		SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

		// Call update states service
		MvcResult result = getMockMvc().perform(
				post(STATES_URL)
						.contentType(CONTENT_TYPE)
						.content(objectMapper.writeValueAsString(state)))
				// Verify returned HTTP code to be 200
				.andExpect(status().isOk()).andReturn();
		// Verify that states saved in database and states returned by service are exact match
		String responseContent = result.getResponse().getContentAsString();
		updateStateExpectedContent = updateStateExpectedContent.replace(USER_ID, userId);
		JSONAssert.assertEquals("state is updated as expected", updateStateExpectedContent, responseContent, JSONCompareMode.NON_EXTENSIBLE);
	}
}
