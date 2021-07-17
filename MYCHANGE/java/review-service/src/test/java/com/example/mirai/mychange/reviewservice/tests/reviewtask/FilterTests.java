package com.example.mirai.projectname.reviewservice.tests.reviewtask;

import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.projectname.reviewservice.fixtures.JwtFactory;
import com.example.mirai.projectname.reviewservice.json.ReviewTaskSummaryJson;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.review.model.ReviewStatus;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntryStatus;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTaskStatus;
import com.example.mirai.projectname.reviewservice.tests.BaseTest;
import com.example.mirai.projectname.reviewservice.utils.Constants;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class FilterTests extends BaseTest {

    private static String reviewTaskSummaryExpectedContent;

    static {
        InputStream inputStream = FilterTests.class.getResourceAsStream("/expectations/reviewtask/dto/ReviewTaskSummary.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        reviewTaskSummaryExpectedContent = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    @ParameterizedTest(name = "user is able to fetch review task summary when {1}")
    @CsvFileSource(resources = "/parameters/reviewtask/FilterCriteria.psv", numLinesToSkip = 1, delimiter = '|')
    void checkReviewTaskSummaries(String user, String testCaseDescription, String criteria, String viewCriteria) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);

        Long reviewId = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewStatus.COMPLETED);
        assert reviewId != null;
        Long reviewTaskId_1 = entityInstanceManager.createReviewTaskAndSetStatus(dataIdentifier + "-1", "ALL_PROPERTIES", ReviewTaskStatus.OPENED, reviewId);
        assert reviewTaskId_1 != null;
        Long reviewEntryId_1_1 = entityInstanceManager.createReviewEntryAndSetStatus(dataIdentifier + "-1", "ALL_PROPERTIES", reviewId, reviewTaskId_1, ReviewEntryStatus.OPENED);
        assert reviewEntryId_1_1 != null;
        /*Long reviewEntryId_1_2 = entityInstanceManager.createReviewEntryAndSetStatus(dataIdentifier+"-1", "ALL_PROPERTIES", reviewId, reviewTaskId_1, ReviewEntryStatus.COMPLETED);
        assert reviewEntryId_1_2!=null;*/

        Long reviewTaskId_2 = entityInstanceManager.createReviewTaskAndSetStatus(dataIdentifier + "-2", "ALL_PROPERTIES", ReviewTaskStatus.COMPLETED, reviewId);
        assert reviewTaskId_2 != null;
        Long reviewEntryId_2_1 = entityInstanceManager.createReviewEntryAndSetStatus(dataIdentifier + "-2", "ALL_PROPERTIES", reviewId, reviewTaskId_2, ReviewEntryStatus.COMPLETED);
        assert reviewEntryId_2_1 != null;

        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        assertThat(optionalReview.isPresent(), equalTo(true));
        Optional<ReviewTask> optionalReviewTask = reviewTaskRepository.findById(reviewTaskId_1);
        assertThat(optionalReviewTask.isPresent(), equalTo(true));
        ReviewTask reviewTask = optionalReviewTask.get();
        String dueDate = formatter.format(new Date(reviewTask.getDueDate().getTime()));
        if (criteria != null) {
            criteria = criteria.replace("<ID>", "" + reviewTaskId_1);
            criteria = criteria.replace("<DATA_IDENTIFIER>", dataIdentifier + "-1");
            criteria = criteria.replace("<DUE_DATE>", dueDate.replaceAll("Z$", ".00:00"));
        }
        String path = PathGenerator.getReviewTaskSummaryPath(criteria, viewCriteria, reviewId);

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(get(path))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ReviewTaskSummaryJson reviewTaskSummaryJson = new ReviewTaskSummaryJson(content);
        List overviewList = reviewTaskSummaryJson.getReviewTaskSummaries();
        String receivedOverviewContent = "";
        if (overviewList.size() == 1)
            receivedOverviewContent = new JSONObject((Map) overviewList.get(0)).toString();

        String expectedContent = reviewTaskSummaryExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier + "-1");
        expectedContent = expectedContent.replace("<ID>", reviewTaskId_1 + "");
        expectedContent = expectedContent.replace("<DUE_DATE>", dueDate.replaceAll("Z$", "+00:00"));
        //receivedOverviewContent = receivedOverviewContent.replace("+00:00", ".00:00");
        JSONAssert.assertEquals("review task summary is not as expected", expectedContent, receivedOverviewContent, JSONCompareMode.NON_EXTENSIBLE);
    }

}
