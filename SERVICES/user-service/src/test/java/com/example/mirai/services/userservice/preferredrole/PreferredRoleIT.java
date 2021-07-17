package com.example.mirai.services.userservice.preferredrole;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

class PreferredRoleIT extends BaseTest {
	private static final String PREFERREDROLES_URL = "/preferred-roles";

	private static final String USER_ID = "<USER_ID>";

	private static String getPreferredRoleExpectedContent;

	private static String getPreferredRoleListExpectedContent;

	private static String updatePreferredRoleExpectedContent;

	static {
		InputStream getPreferredRoleInputStream = PreferredRoleIT.class.getResourceAsStream("/expectations/preferredrole/GetPreferredRoleResponseContent.json");
		BufferedReader getPreferredRoleBufferedReader = new BufferedReader(new InputStreamReader(getPreferredRoleInputStream, StandardCharsets.UTF_8));
		getPreferredRoleExpectedContent = getPreferredRoleBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

		InputStream getPreferredRoleListInputStream = PreferredRoleIT.class.getResourceAsStream("/expectations/preferredrole/GetPreferredRoleListResponseContent.json");
		BufferedReader getPreferredRoleListBufferedReader = new BufferedReader(new InputStreamReader(getPreferredRoleListInputStream, StandardCharsets.UTF_8));
		getPreferredRoleListExpectedContent = getPreferredRoleListBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

		InputStream UpdatePreferredRoleInputStream = PreferredRoleIT.class.getResourceAsStream("/expectations/preferredrole/UpdatePreferredRoleResponseContent.json");
		BufferedReader UpdatePreferredRoleBufferedReader = new BufferedReader(new InputStreamReader(UpdatePreferredRoleInputStream, StandardCharsets.UTF_8));
		updatePreferredRoleExpectedContent = UpdatePreferredRoleBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
	}

	@Autowired
	private PreferredRoleRepository preferredRoleRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void getPreferredRoleWorks() throws Exception {
		String userId = TestUtil.generateRandomAlphanumericString(10);

		// Generate PreferredRole object populated with dummy data
		PreferredRole preferredRole = EntityPojoGenerationFactory.generatePreferredRole();
		preferredRole.setUserId(userId);

		preferredRoleRepository.save(preferredRole);

		// Generate Bearer Token
		Jwt jwt = JwtFactory.getJwtToken(userId, List.of("user"));
		SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

		// Call update preferred-roles service
		MvcResult result = getMockMvc().perform(
				get(PREFERREDROLES_URL)
						.contentType(CONTENT_TYPE)
						.content(objectMapper.writeValueAsString(preferredRole)))
				// Verify returned HTTP code to be 200
				.andExpect(status().isOk()).andReturn();
		// Verify that preferredRole saved in database and preferredRole returned by service are exact match
		String responseContent = result.getResponse().getContentAsString();
		getPreferredRoleExpectedContent = getPreferredRoleExpectedContent.replace(USER_ID, userId);
		JSONAssert.assertEquals("Preferred Role is as expected", getPreferredRoleExpectedContent, responseContent, JSONCompareMode.NON_EXTENSIBLE);

	}

	@Test
	void getPreferredRoleReturnsNoContent() throws Exception {
		String userId = TestUtil.generateRandomAlphanumericString(10);

		// Generate PreferredRole object populated with dummy data
		PreferredRole preferredRole = EntityPojoGenerationFactory.generatePreferredRole();
		preferredRole.setUserId(userId);

		// Generate Bearer Token
		Jwt jwt = JwtFactory.getJwtToken(userId, List.of("user"));
		SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

		// Call update PreferredRole service
		getMockMvc().perform(
				get(PREFERREDROLES_URL)
						.contentType(CONTENT_TYPE)
						.content(objectMapper.writeValueAsString(preferredRole)))
				// Verify returned HTTP code to be 200
				.andExpect(status().isNoContent())
				// Verify that nothing is returned
				.andExpect(content().string(""));
	}

	@Test
	void getPreferredRolesByUserIdsWorks() throws Exception {

		String userId = TestUtil.generateRandomAlphanumericString(10);
		String[] queryUserIds = new String[2];
		List<PreferredRole> preferredRoleList = new ArrayList<PreferredRole>();
		for (int i = 0; i < 2; i++) {
			String queryUserId = TestUtil.generateRandomAlphanumericString(10);
			queryUserIds[i] = queryUserId;
			// Generate PreferredRole object populated with dummy data
			PreferredRole preferredRole = EntityPojoGenerationFactory.generatePreferredRole();
			preferredRole.setUserId(queryUserId);
			preferredRoleRepository.save(preferredRole);
			preferredRoleList.add(preferredRole);
		}
		// Generate Bearer Token
		Jwt jwt = JwtFactory.getJwtToken(userId, List.of("user"));
		SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

		// Call update preferred-roles service
		MvcResult result = getMockMvc().perform(
				get("/preferred-roles?user-ids=" + queryUserIds[0] + "," + queryUserIds[1])
						.contentType(CONTENT_TYPE)
						.content(objectMapper.writeValueAsString(preferredRoleList)))
				// Verify returned HTTP code to be 200
				.andExpect(status().isOk()).andReturn();
		// Verify that preferredRole saved in database and preferredRole returned by service are exact match
		String responseContent = result.getResponse().getContentAsString();
		getPreferredRoleListExpectedContent = getPreferredRoleListExpectedContent.replace("<USER_ID1>", queryUserIds[0]);
		getPreferredRoleListExpectedContent = getPreferredRoleListExpectedContent.replace("<USER_ID2>", queryUserIds[1]);
		JSONAssert.assertEquals("Preferred Roles by user ids are as expected", getPreferredRoleListExpectedContent, responseContent, JSONCompareMode.NON_EXTENSIBLE);

	}

	@Test
	void getPreferredRolesByUserIdsNoContent() throws Exception {

		String userId = TestUtil.generateRandomAlphanumericString(10);
		String[] queryUserIds = new String[2];
		List<PreferredRole> preferredRoleList = new ArrayList<PreferredRole>();
		for (int i = 0; i < 2; i++) {
			String queryUserId = TestUtil.generateRandomAlphanumericString(10);
			queryUserIds[i] = queryUserId;
			// Generate PreferredRole object populated with dummy data
			PreferredRole preferredRole = EntityPojoGenerationFactory.generatePreferredRole();
			preferredRole.setUserId(queryUserId);
			preferredRoleRepository.save(preferredRole);
			preferredRoleList.add(preferredRole);
		}

		// Generate Bearer Token
		Jwt jwt = JwtFactory.getJwtToken(userId, List.of("user"));
		SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

		// Call update preferred-roles service
		getMockMvc().perform(
				get("/preferred-roles?user-ids=")
						.contentType(CONTENT_TYPE)
						.content(objectMapper.writeValueAsString(preferredRoleList)))
				// Verify returned HTTP code to be 200
				.andExpect(status().isNoContent());
		// Verify that preferredRole saved in database and preferredRole returned by service are exact match

	}


	@Test
	void updatePreferredRoleWorks() throws Exception {
		String userId = TestUtil.generateRandomAlphanumericString(10);

		// Generate PreferredRole object populated with dummy data
		PreferredRole preferredRole = EntityPojoGenerationFactory.generatePreferredRole();
		preferredRole.setUserId(userId);

		preferredRoleRepository.save(preferredRole);
		//update the preferred roles in preferredRole object
		preferredRole.setPreferredRoles(new String[] { "role1", "role2", "role4" });

		// Generate Bearer Token
		Jwt jwt = JwtFactory.getJwtToken(userId, List.of("user"));
		SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

		// Call update preferred-roles service
		MvcResult result = getMockMvc().perform(
				post(PREFERREDROLES_URL)
						.contentType(CONTENT_TYPE)
						.content(objectMapper.writeValueAsString(preferredRole)))
				// Verify returned HTTP code to be 200
				.andExpect(status().isOk()).andReturn();
		// Verify that preferredRole saved in database and preferredRole returned by service are exact match
		String responseContent = result.getResponse().getContentAsString();
		updatePreferredRoleExpectedContent = updatePreferredRoleExpectedContent.replace(USER_ID, userId);
		updatePreferredRoleExpectedContent = updatePreferredRoleExpectedContent.replace("<ROLE>", "role4");
		JSONAssert.assertEquals("Preferred Role is updated as expected", updatePreferredRoleExpectedContent, responseContent, JSONCompareMode.NON_EXTENSIBLE);
	}
}
