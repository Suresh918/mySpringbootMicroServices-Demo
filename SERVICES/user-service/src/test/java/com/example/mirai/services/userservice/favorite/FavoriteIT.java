package com.example.mirai.services.userservice.favorite;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.services.userservice.BaseTest;
import com.example.mirai.services.userservice.favorite.model.Case;
import com.example.mirai.services.userservice.favorite.model.Favorite;
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

class FavoriteIT extends BaseTest {
	private static final String BASE_URL = "/favorites";

	private static final String USER_ID = "<USER_ID>";

	private static String getFavoritesExpectedContent;

	private static String updateFavoritesExpectedContent;

	static {
		InputStream getFavoritesInputStream = FavoriteIT.class.getResourceAsStream("/expectations/favorite/GetFavoritesResponseContent.json");
		BufferedReader getFavoritesBufferedReader = new BufferedReader(new InputStreamReader(getFavoritesInputStream, StandardCharsets.UTF_8));
		getFavoritesExpectedContent = getFavoritesBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

		InputStream updateFavoritesInputStream = FavoriteIT.class.getResourceAsStream("/expectations/favorite/UpdateFavoritesResponseContent.json");
		BufferedReader updagteFavoritesBufferedReader = new BufferedReader(new InputStreamReader(updateFavoritesInputStream, StandardCharsets.UTF_8));
		updateFavoritesExpectedContent = updagteFavoritesBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
	}

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private FavoriteRepository favoriteRepository;

	@Test
	void getFavoritesWorks() throws Exception {
		String userId = TestUtil.generateRandomAlphanumericString(10);

		// Generate Favorite object populated with dummy data
		Favorite favorite = EntityPojoGenerationFactory.generateFavorite();
		favorite.setUserId(userId);

		favoriteRepository.save(favorite);

		// Generate Bearer Token
		Jwt jwt = JwtFactory.getJwtToken(userId, List.of("user"));
		SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

		// Call update favorites service
		MvcResult result = getMockMvc().perform(
				get(BASE_URL)
						.contentType(CONTENT_TYPE)
						.content(objectMapper.writeValueAsString(favorite)))
				// Verify returned HTTP code to be 200
				.andExpect(status().isOk()).andReturn();
		// Verify that favorites saved in database and favorites returned by service are exact match
		String responseContent = result.getResponse().getContentAsString();
		getFavoritesExpectedContent = getFavoritesExpectedContent.replace(USER_ID, userId);
		JSONAssert.assertEquals("state is as expected", getFavoritesExpectedContent, responseContent, JSONCompareMode.NON_EXTENSIBLE);
	}

	@Test
	void getFavoritesReturnsNoContent() throws Exception {
		String userId = TestUtil.generateRandomAlphanumericString(10);

		// Generate Favorite object populated with dummy data
		Favorite favorite = EntityPojoGenerationFactory.generateFavorite();
		favorite.setUserId(userId);

		// Generate Bearer Token
		Jwt jwt = JwtFactory.getJwtToken(userId, List.of("user"));
		SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

		// Call update favorites service
		getMockMvc().perform(
				get(BASE_URL)
						.contentType(CONTENT_TYPE)
						.content(objectMapper.writeValueAsString(favorite)))
				// Verify returned HTTP code to be 200
				.andExpect(status().isNoContent())
				// Verify that nothing is returned
				.andExpect(content().string(""));
	}

	@Test
	void updateFavoritesWorks() throws Exception {
		String userId = TestUtil.generateRandomAlphanumericString(10);

		// Generate Favorite object populated with dummy data
		Favorite favorite = EntityPojoGenerationFactory.generateFavorite();
		favorite.setUserId(userId);
		favoriteRepository.save(favorite);
		Case case2 = new Case();
		case2.setId("2");
		case2.setName("name");
		case2.setType("type");
		favorite.setCases(new Case[] { case2 });

		// Generate Bearer Token
		Jwt jwt = JwtFactory.getJwtToken(userId, List.of("user"));
		SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

		// Call update favorites service
		MvcResult result = getMockMvc().perform(
				post(BASE_URL)
						.contentType(CONTENT_TYPE)
						.content(objectMapper.writeValueAsString(favorite)))
				// Verify returned HTTP code to be 200
				.andExpect(status().isOk()).andReturn();
		// Verify that favorites saved in database and favorites returned by service are exact match
		String responseContent = result.getResponse().getContentAsString();
		updateFavoritesExpectedContent = updateFavoritesExpectedContent.replace(USER_ID, userId);
		updateFavoritesExpectedContent = updateFavoritesExpectedContent.replace("<ID>", "2");
		JSONAssert.assertEquals("state is as expected", updateFavoritesExpectedContent, responseContent, JSONCompareMode.NON_EXTENSIBLE);
	}
}
