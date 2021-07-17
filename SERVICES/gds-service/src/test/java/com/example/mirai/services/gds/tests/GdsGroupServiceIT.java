package com.example.mirai.services.gds.tests;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import com.example.mirai.services.gds.service.GdsGroupService;
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

class GdsGroupServiceIT extends BaseTest {
	private static final String BASE_PATH = "/groups";

	private static final String getGroupByGroupId;

	private static final String getGroupsByGroupIdsSingleResult;

	private static final String getGroupsByGroupIdsMultipleResults;

	private static final String findGroupSingleResult;

	private static final String findGroupsMultipleResults;

	private static final String findPrefixedGroupSingleResult;

	private static final String findPrefixedGroupMultipleResults;

	static {
		InputStream getGroupByGroupIdInputStream = GdsUserServiceIT.class.getResourceAsStream("/expectations/group/getGroupByGroupId.json");
		BufferedReader getGroupByGroupIdBufferedReader = new BufferedReader(new InputStreamReader(getGroupByGroupIdInputStream, StandardCharsets.UTF_8));
		getGroupByGroupId = getGroupByGroupIdBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

		InputStream getGroupsByGroupIdsSingleResultInputStream = GdsUserServiceIT.class.getResourceAsStream("/expectations/group/getGroupsByGroupIdsSingleResult.json");
		BufferedReader getGroupsByGroupIdsSingleResultBufferedReader = new BufferedReader(new InputStreamReader(getGroupsByGroupIdsSingleResultInputStream, StandardCharsets.UTF_8));
		getGroupsByGroupIdsSingleResult = getGroupsByGroupIdsSingleResultBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

		InputStream getGroupsByGroupIdsMultipleResultsInputStream = GdsUserServiceIT.class.getResourceAsStream("/expectations/group/getGroupsByGroupIdsMultipleResults.json");
		BufferedReader getGroupsByGroupIdsMultipleResultsBufferedReader = new BufferedReader(new InputStreamReader(getGroupsByGroupIdsMultipleResultsInputStream, StandardCharsets.UTF_8));
		getGroupsByGroupIdsMultipleResults = getGroupsByGroupIdsMultipleResultsBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

		InputStream findGroupSingleResultInputStream = GdsUserServiceIT.class.getResourceAsStream("/expectations/group/findGroupSingleResult.json");
		BufferedReader findGroupSingleResultBufferedReader = new BufferedReader(new InputStreamReader(findGroupSingleResultInputStream, StandardCharsets.UTF_8));
		findGroupSingleResult = findGroupSingleResultBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

		InputStream findGroupsMultipleResultsInputStream = GdsUserServiceIT.class.getResourceAsStream("/expectations/group/findGroupMultipleResults.json");
		BufferedReader findGroupsMultipleResultsBufferedReader = new BufferedReader(new InputStreamReader(findGroupsMultipleResultsInputStream, StandardCharsets.UTF_8));
		findGroupsMultipleResults = findGroupsMultipleResultsBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

		InputStream findPrefixedGroupSingleResultInputStream = GdsUserServiceIT.class.getResourceAsStream("/expectations/group/findPrefixedGroupSingleResult.json");
		BufferedReader findPrefixedGroupSingleResultBufferedReader = new BufferedReader(new InputStreamReader(findPrefixedGroupSingleResultInputStream, StandardCharsets.UTF_8));
		findPrefixedGroupSingleResult = findPrefixedGroupSingleResultBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

		InputStream findPrefixedGroupMultipleResultsInputStream = GdsUserServiceIT.class.getResourceAsStream("/expectations/group/findPrefixedGroupMultipleResults.json");
		BufferedReader findPrefixedGroupMultipleResultsBufferedReader = new BufferedReader(new InputStreamReader(findPrefixedGroupMultipleResultsInputStream, StandardCharsets.UTF_8));
		findPrefixedGroupMultipleResults = findPrefixedGroupMultipleResultsBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
	}

	@Autowired
	GdsGroupService gdsGroupService;

	@ParameterizedTest(name = "Get group using group id: {0} and expect result: {1}")
	@CsvFileSource(resources = "/parameters/group/getGroupByGroupId.csv", numLinesToSkip = 1)
	void getGroupByGroupId(String groupId, String expectedResult) throws Exception {
		MockHttpServletResponse response = getMockMvc().perform(get(BASE_PATH + "/" + groupId)).andReturn().getResponse();
		switch (expectedResult) {
			case "found":
				assertThat("Response status code 200", response.getStatus(), equalTo(HttpStatus.OK.value()));
				assertThat("Content-Type header is application/json", response.getContentType(), equalTo(MediaType.APPLICATION_JSON.toString()));
				JSONAssert.assertEquals("Response body is correct", getGroupByGroupId, response.getContentAsString(), JSONCompareMode.NON_EXTENSIBLE);
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

	@ParameterizedTest(name = "Get groups using group ids: {0} and expect result: {1}")
	@CsvFileSource(resources = "/parameters/group/getGroupsByGroupIds.csv", numLinesToSkip = 1)
	void getGroupsByGroupIds(String groupIds, String expectedResult) throws Exception {
		MockHttpServletResponse response = getMockMvc().perform(
				get(BASE_PATH).queryParam("group_id", groupIds)
		).andReturn().getResponse();
		switch (expectedResult) {
			case "found-single":
				assertThat("Response status code 200", response.getStatus(), equalTo(HttpStatus.OK.value()));
				assertThat("Content-Type header is application/json", response.getContentType(), equalTo(MediaType.APPLICATION_JSON.toString()));
				JSONAssert.assertEquals("Response body is correct", getGroupsByGroupIdsSingleResult, response.getContentAsString(), JSONCompareMode.NON_EXTENSIBLE);
				break;
			case "found-multiple":
				assertThat("Response status code 200", response.getStatus(), equalTo(HttpStatus.OK.value()));
				assertThat("Content-Type header is application/json", response.getContentType(), equalTo(MediaType.APPLICATION_JSON.toString()));
				JSONAssert.assertEquals("Response body is correct", getGroupsByGroupIdsMultipleResults, response.getContentAsString(), JSONCompareMode.NON_EXTENSIBLE);
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

	@ParameterizedTest(name = "Find group using search query: {0} and expect result: {1}")
	@CsvFileSource(resources = "/parameters/group/findGroup.csv", numLinesToSkip = 1)
	void findGroup(String searchQuery, String expectedResult) throws Exception {
		MockHttpServletResponse response = getMockMvc().perform(
				get(BASE_PATH).queryParam("q", searchQuery)
		).andReturn().getResponse();

		switch (expectedResult) {
			case "found-single":
				assertThat("Response status code 200", response.getStatus(), equalTo(HttpStatus.OK.value()));
				assertThat("Content-Type header is application/json", response.getContentType(), equalTo(MediaType.APPLICATION_JSON.toString()));
				JSONAssert.assertEquals("Response body is correct", findGroupSingleResult, response.getContentAsString(), JSONCompareMode.NON_EXTENSIBLE);
				break;
			case "found-multiple":
				assertThat("Response status code 200", response.getStatus(), equalTo(HttpStatus.OK.value()));
				assertThat("Content-Type header is application/json", response.getContentType(), equalTo(MediaType.APPLICATION_JSON.toString()));
				JSONAssert.assertEquals("Response body is correct", findGroupsMultipleResults, response.getContentAsString(), JSONCompareMode.NON_EXTENSIBLE);
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

	@ParameterizedTest(name = "Find prefixed group using group id prefix: {0}, search query: {1} and expect result: {2}")
	@CsvFileSource(resources = "/parameters/group/findPrefixedGroup.csv", numLinesToSkip = 1)
	void findPrefixedGroup(String groupIdPrefix, String searchQuery, String expectedResult) throws Exception {
		MockHttpServletResponse response = getMockMvc().perform(
				get(BASE_PATH)
						.queryParam("group_id_prefix", groupIdPrefix)
						.queryParam("q", searchQuery)
		).andReturn().getResponse();

		switch (expectedResult) {
			case "found-single":
				assertThat("Response status code 200", response.getStatus(), equalTo(HttpStatus.OK.value()));
				assertThat("Content-Type header is application/json", response.getContentType(), equalTo(MediaType.APPLICATION_JSON.toString()));
				JSONAssert.assertEquals("Response body is correct", findPrefixedGroupSingleResult, response.getContentAsString(), JSONCompareMode.NON_EXTENSIBLE);
				break;
			case "found-multiple":
				assertThat("Response status code 200", response.getStatus(), equalTo(HttpStatus.OK.value()));
				assertThat("Content-Type header is application/json", response.getContentType(), equalTo(MediaType.APPLICATION_JSON.toString()));
				JSONAssert.assertEquals("Response body is correct", findPrefixedGroupMultipleResults, response.getContentAsString(), JSONCompareMode.NON_EXTENSIBLE);
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
