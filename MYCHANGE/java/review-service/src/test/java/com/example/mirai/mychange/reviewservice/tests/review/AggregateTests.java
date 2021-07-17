package com.example.mirai.projectname.reviewservice.tests.review;

import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.projectname.reviewservice.fixtures.JwtFactory;
import com.example.mirai.projectname.reviewservice.review.model.ReviewStatus;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntryStatus;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTaskStatus;
import com.example.mirai.projectname.reviewservice.tests.BaseTest;
import com.example.mirai.projectname.reviewservice.utils.Constants;
import com.example.mirai.projectname.reviewservice.utils.PathGenerator;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MvcResult;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

public class AggregateTests extends BaseTest {

    private static String reviewAggregateExpectedContent;

    static {
        InputStream inputStream = FilterTests.class.getResourceAsStream("/expectations/review/aggregate/ReviewAggregate.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        reviewAggregateExpectedContent = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    //@ParameterizedTest(name = "{0} user is to retrieve aggregate on review entry in status {3}, when review task in status {2} and review in {1} status")
    @CsvFileSource(resources = "/parameters/review/ReviewAggregate.csv", numLinesToSkip = 1)
    void userToRetrieveReviewAggregate(String user, ReviewStatus reviewStatus,
                                       ReviewTaskStatus reviewTaskStatus,
                                       ReviewEntryStatus originalReviewEntryStatus) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = "" + (new Date()).getTime();

        Long reviewId = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewStatus.COMPLETED);
        assert reviewId != null;
        Long reviewTaskId_1 = entityInstanceManager.createReviewTaskAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewTaskStatus.OPENED, reviewId);
        assert reviewTaskId_1 != null;
        Long reviewEntryId_1_1 = entityInstanceManager.createReviewEntryAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewId, reviewTaskId_1, ReviewEntryStatus.OPENED);
        assert reviewEntryId_1_1 != null;

        Long reviewTaskId_2 = entityInstanceManager.createReviewTaskAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewTaskStatus.COMPLETED, reviewId);
        assert reviewTaskId_2 != null;
        Long reviewEntryId_2_1 = entityInstanceManager.createReviewEntryAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewId, reviewTaskId_2, ReviewEntryStatus.COMPLETED);
        assert reviewEntryId_2_1 != null;

        String path = PathGenerator.getReviewAggregatePath(reviewId);


        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));


        MvcResult result = getMockMvc().perform(get(path))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        Date date = null;
        try {
            Long timestamp = Long.parseLong(dataIdentifier);
            date = new Date(timestamp);
        } catch (NumberFormatException nfe) {
            date = new Date();
        }
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);
        String strDate = formatter.format(date);

        String expectedContent = reviewAggregateExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier);
        expectedContent = expectedContent.replace("<ID>", reviewId + "");
        expectedContent = expectedContent.replace("<STATIC_DATE>", strDate.replaceAll("Z$", "+00:00"));

        JSONAssert.assertEquals("review summary is not as expected", expectedContent, reviewAggregateExpectedContent, JSONCompareMode.NON_EXTENSIBLE);

    }
}
