package com.example.mirai.services.gds.tests;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import com.example.mirai.services.gds.service.GdsUserService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class GdsUserServiceIT extends BaseTest {
	private static final String BASE_PATH = "/users";

	private static final String getUserByUserId;

	private static final String getUserByAbbreviation;

	private static final String findUserSingleResult;

	private static final String findUserMultipleResults;

	static {
		InputStream getUserByUserIdInputStream = GdsUserServiceIT.class.getResourceAsStream("/expectations/user/getUserByUserId.json");
		BufferedReader getUserByUserIdBufferedReader = new BufferedReader(new InputStreamReader(getUserByUserIdInputStream, StandardCharsets.UTF_8));
		getUserByUserId = getUserByUserIdBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

		InputStream getUserByAbbreviationInputStream = GdsUserServiceIT.class.getResourceAsStream("/expectations/user/getUserByAbbreviation.json");
		BufferedReader getUserByAbbreviationBufferedReader = new BufferedReader(new InputStreamReader(getUserByAbbreviationInputStream, StandardCharsets.UTF_8));
		getUserByAbbreviation = getUserByAbbreviationBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

		InputStream findUserSingleResultInputStream = GdsUserServiceIT.class.getResourceAsStream("/expectations/user/findUserSingleResult.json");
		BufferedReader findUserSingleResultBufferedReader = new BufferedReader(new InputStreamReader(findUserSingleResultInputStream, StandardCharsets.UTF_8));
		findUserSingleResult = findUserSingleResultBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

		InputStream findUserMultipleResultsInputStream = GdsUserServiceIT.class.getResourceAsStream("/expectations/user/findUserMultipleResults.json");
		BufferedReader findUserMultipleResultsBufferedReader = new BufferedReader(new InputStreamReader(findUserMultipleResultsInputStream, StandardCharsets.UTF_8));
		findUserMultipleResults = findUserMultipleResultsBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
	}

	@Autowired
	GdsUserService gdsUserService;

	@ParameterizedTest(name = "Get user using user id: {0} and expect result: {1}")
	@CsvFileSource(resources = "/parameters/user/getUserByUserId.csv", numLinesToSkip = 1)
	void getUserByUserId(String userId, String expectedResult) throws Exception {
		MockHttpServletResponse response = getMockMvc().perform(get(BASE_PATH + "/" + userId)).andReturn().getResponse();
		switch (expectedResult) {
			case "found":
				assertThat("Response status code 200", response.getStatus(), equalTo(HttpStatus.OK.value()));
				assertThat("Content-Type header is application/json", response.getContentType(), equalTo(MediaType.APPLICATION_JSON.toString()));
				JSONAssert.assertEquals("Response body is correct", getUserByUserId, response.getContentAsString(), JSONCompareMode.NON_EXTENSIBLE);
				break;
			case "not-found":
				assertThat("Response status code 404", response.getStatus(), equalTo(HttpStatus.NOT_FOUND.value()));
				//assertThat("Response body is empty", result.getResponse().getContentAsString(), emptyOrNullString());
				break;
			case "bad-request":
				assertThat("Response status code 400", response.getStatus(), equalTo(HttpStatus.BAD_REQUEST.value()));
				break;
		}
	}

	@ParameterizedTest(name = "Get user using abbreviation: {0} and expect result: {1}")
	@CsvFileSource(resources = "/parameters/user/getUserByAbbreviation.csv", numLinesToSkip = 1)
	void getUserByAbbreviation(String abbreviation, String expectedResult) throws Exception {
		MockHttpServletResponse response = getMockMvc().perform(
				get(BASE_PATH).queryParam("abbreviation", abbreviation)
		).andReturn().getResponse();

		switch (expectedResult) {
			case "found":
				assertThat("Response status code 200", response.getStatus(), equalTo(HttpStatus.OK.value()));
				assertThat("Content-Type header is application/json", response.getContentType(), equalTo(MediaType.APPLICATION_JSON.toString()));
				JSONAssert.assertEquals("Response body is correct", getUserByAbbreviation, response.getContentAsString(), JSONCompareMode.NON_EXTENSIBLE);
				break;
			case "not-found":
				assertThat("Response status code 404", response.getStatus(), equalTo(HttpStatus.NOT_FOUND.value()));
				//assertThat("Response body is empty", result.getResponse().getContentAsString(), emptyOrNullString());
				break;
			case "bad-request":
				assertThat("Response status code 400", response.getStatus(), equalTo(HttpStatus.BAD_REQUEST.value()));
				break;
		}
	}

	@ParameterizedTest(name = "Find user using search query: {0} and expect result: {1}")
	@CsvFileSource(resources = "/parameters/user/findUser.csv", numLinesToSkip = 1)
	void findUser(String searchQuery, String expectedResult) throws Exception {
		MockHttpServletResponse response = getMockMvc().perform(
				get(BASE_PATH).queryParam("q", searchQuery)
		).andReturn().getResponse();

		switch (expectedResult) {
			case "found-single":
				assertThat("Response status code 200", response.getStatus(), equalTo(HttpStatus.OK.value()));
				assertThat("Content-Type header is application/json", response.getContentType(), equalTo(MediaType.APPLICATION_JSON.toString()));
				JSONAssert.assertEquals("Response body is correct", findUserSingleResult, response.getContentAsString(), JSONCompareMode.NON_EXTENSIBLE);
				break;
			case "found-multiple":
				assertThat("Response status code 200", response.getStatus(), equalTo(HttpStatus.OK.value()));
				assertThat("Content-Type header is application/json", response.getContentType(), equalTo(MediaType.APPLICATION_JSON.toString()));
				JSONAssert.assertEquals("Response body is correct", findUserMultipleResults, response.getContentAsString(), JSONCompareMode.NON_EXTENSIBLE);
				break;
			case "no-content":
				assertThat("Response status code 204", response.getStatus(), equalTo(HttpStatus.NO_CONTENT.value()));
				assertThat("Response body is empty", response.getContentAsString(), emptyOrNullString());
				break;
			case "bad-request":
				assertThat("Response status code 400", response.getStatus(), equalTo(HttpStatus.BAD_REQUEST.value()));
				break;
		}
	}
}
