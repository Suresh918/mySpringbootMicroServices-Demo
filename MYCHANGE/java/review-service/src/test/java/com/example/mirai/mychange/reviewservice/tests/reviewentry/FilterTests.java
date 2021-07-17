package com.example.mirai.projectname.reviewservice.tests.reviewentry;

import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.projectname.reviewservice.fixtures.JwtFactory;
import com.example.mirai.projectname.reviewservice.json.BaseEntityListJson;
import com.example.mirai.projectname.reviewservice.review.model.ReviewStatus;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntry;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntryStatus;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTaskStatus;
import com.example.mirai.projectname.reviewservice.tests.BaseTest;
import com.example.mirai.projectname.reviewservice.utils.PathGenerator;
import net.minidev.json.JSONObject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MvcResult;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class FilterTests extends BaseTest {

    private static String reviewEntryOverviewExpectedContent;

    static {
        InputStream inputStream = FilterTests.class.getResourceAsStream("/expectations/reviewentry/dto/ReviewEntryOverview.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        reviewEntryOverviewExpectedContent = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    @ParameterizedTest(name = "user is able to fetch review entry overview when {1}")
    @CsvFileSource(resources = "/parameters/reviewentry/FilterCriteria.psv", numLinesToSkip = 1, delimiter = '|')
    void checkReviewEntryOverviews(String user, String testCaseDescription, String criteria, String viewCriteria) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long reviewId = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewStatus.COMPLETED);
        assert reviewId != null;
        Long reviewTaskId = entityInstanceManager.createReviewTaskAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewTaskStatus.OPENED, reviewId);
        assert reviewTaskId != null;
        Long reviewEntryId = entityInstanceManager.createReviewEntryAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewId, reviewTaskId, ReviewEntryStatus.COMPLETED);
        assert reviewEntryId != null;

        Optional<ReviewEntry> optionalReviewEntry = reviewEntryRepository.findById(reviewEntryId);
        assertThat(optionalReviewEntry.isPresent(), equalTo(true));
        ReviewEntry reviewEntry = optionalReviewEntry.get();

        if (criteria != null) {
            criteria = criteria.replace("<ID>", "" + reviewEntryId);
            criteria = criteria.replace("<DATA_IDENTIFIER>", dataIdentifier);
        }
        if (viewCriteria != null) {
            criteria = criteria.replace("<ID>", "" + reviewEntryId);
            viewCriteria = viewCriteria.replace("<DATA_IDENTIFIER>", dataIdentifier);
        }
        String path = PathGenerator.getReviewEntryOverviewPath(criteria, viewCriteria, reviewId);

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(get(path))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        BaseEntityListJson baseEntityListJson = new BaseEntityListJson(content);
        List overviewList = baseEntityListJson.getResults();
        String receivedOverviewContent = "";
        if (overviewList.size() == 1)
            receivedOverviewContent = new JSONObject((Map) overviewList.get(0)).toString();

        String expectedContent = reviewEntryOverviewExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier);
        expectedContent = expectedContent.replace("<ID>", reviewEntryId + "");

        JSONAssert.assertEquals("review entry overview is not as expected", expectedContent, receivedOverviewContent, JSONCompareMode.NON_EXTENSIBLE);
    }
}
