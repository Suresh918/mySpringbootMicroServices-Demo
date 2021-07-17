package com.example.mirai.services.userservice.profile;

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
import com.example.mirai.services.userservice.profile.model.Profile;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProfileIT extends BaseTest {
	private static final String PROFILE_URL = "/profiles";

	private static String getProfileExpectedContent;

	private static String getExistingProfileExpectedContent;

	static {
		InputStream getProfileInputStream = ProfileIT.class.getResourceAsStream("/expectations/profile/GetProfileResponseContent.json");
		BufferedReader getProfileBufferedReader = new BufferedReader(new InputStreamReader(getProfileInputStream, StandardCharsets.UTF_8));
		getProfileExpectedContent = getProfileBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

		InputStream getExistingProfileInputStream = ProfileIT.class.getResourceAsStream("/expectations/profile/GetExistingProfileResponseContent.json");
		BufferedReader getExistingProfileBufferedReader = new BufferedReader(new InputStreamReader(getExistingProfileInputStream, StandardCharsets.UTF_8));
		getExistingProfileExpectedContent = getExistingProfileBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
	}

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void getProfileWorks() throws Exception {
		String userId = TestUtil.generateRandomAlphanumericString(10);
		// Generate State object populated with dummy data
		Profile profile = EntityPojoGenerationFactory.generateProfile(userId);
		// Generate Bearer Token
		Jwt jwt = JwtFactory.getJwtToken(userId, List.of("user", "unspecialized"));
		SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

		// Call update state service
		MvcResult result = getMockMvc().perform(
				get(PROFILE_URL)
						.contentType(CONTENT_TYPE)
						.content(objectMapper.writeValueAsString(profile)))
				// Verify returned HTTP code to be 200
				.andExpect(status().isOk()).andReturn();
		// Verify that states saved in database and states returned by service are exact match
		String responseContent = result.getResponse().getContentAsString();
		getProfileExpectedContent = getProfileExpectedContent.replaceAll("<user_id>", userId);
		JSONAssert.assertEquals("profile is as expected", getProfileExpectedContent, responseContent, JSONCompareMode.NON_EXTENSIBLE);

	}

	@Test
	void getExistingProfileWorks() throws Exception {
		String userId = TestUtil.generateRandomAlphanumericString(10);
		// Generate State object populated with dummy data
		Profile profile = EntityPojoGenerationFactory.generateProfile(userId);
		profileRepository.save(profile);

		// Generate Bearer Token
		Jwt jwt = JwtFactory.getJwtToken(userId, List.of("user", "unspecialized"));
		SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

		// Call update state service
		MvcResult result = getMockMvc().perform(
				get(PROFILE_URL)
						.contentType(CONTENT_TYPE)
						.content(objectMapper.writeValueAsString(profile)))
				// Verify returned HTTP code to be 200
				.andExpect(status().isOk()).andReturn();
		// Verify that states saved in database and states returned by service are exact match
		String responseContent = result.getResponse().getContentAsString();
		getExistingProfileExpectedContent = getExistingProfileExpectedContent.replaceAll("<user_id>", userId);
		JSONAssert.assertEquals("profile is as expected", getExistingProfileExpectedContent, responseContent, JSONCompareMode.NON_EXTENSIBLE);
	}
}
