package com.example.mirai.projectname.reviewservice.tests.review;


import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.projectname.reviewservice.fixtures.EntityPojoFactory;
import com.example.mirai.projectname.reviewservice.fixtures.JsonNodeFactory;
import com.example.mirai.projectname.reviewservice.fixtures.JwtFactory;
import com.example.mirai.projectname.reviewservice.json.ExceptionResponse;
import com.example.mirai.projectname.reviewservice.json.ReviewCaseStatusJson;
import com.example.mirai.projectname.reviewservice.json.ReviewJson;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.review.model.ReviewCaseActions;
import com.example.mirai.projectname.reviewservice.review.model.ReviewStatus;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntry;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntryStatus;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTaskStatus;
import com.example.mirai.projectname.reviewservice.tests.BaseTest;
import com.example.mirai.projectname.reviewservice.tests.ExceptionValidator;
import com.example.mirai.projectname.reviewservice.utils.ObjectMapperUtil;
import com.example.mirai.projectname.reviewservice.utils.PathGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReviewTests extends BaseTest {

    private static String overviewExpectedContent;

    static {
        InputStream inputStream = ReviewTests.class.getResourceAsStream("/expectations/review/dto/ReviewOverview.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        overviewExpectedContent = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    @ParameterizedTest(name = "{0} user is to create review with missing property {1}, expect to be {2}")
    @CsvFileSource(resources = "/parameters/review/Create.csv", numLinesToSkip = 1)
    void userToCreateReview(String user, String missingProperty, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Review requestReview = null;
        switch (missingProperty) {
            case "title":
                requestReview = EntityPojoFactory.createReview(dataIdentifier, "ALL_PROPERTIES_EXCEPT_AUDIT_AND_TITLE");
                break;
            case "executor":
                requestReview = EntityPojoFactory.createReview(dataIdentifier, "ALL_PROPERTIES_EXCEPT_AUDIT_AND_EXECUTOR");
                break;
            case "completion_date":
                requestReview = EntityPojoFactory.createReview(dataIdentifier, "ALL_PROPERTIES_EXCEPT_AUDIT_AND_COMPLETION_DATE");
                break;
            default:
                requestReview = EntityPojoFactory.createReview(dataIdentifier, "ALL_PROPERTIES_EXCEPT_AUDIT");
                break;
        }

        String path = PathGenerator.getReviewCreationPath();

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(post(path)
                .content(ObjectMapperUtil.getObjectMapper().writeValueAsString(requestReview))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        ExceptionResponse exceptionResponse = null;
        switch (expectedResult.toUpperCase()) {
            case "UNAUTHORIZED":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.FORBIDDEN.value()));
                exceptionResponse = new ExceptionResponse(content);
                ExceptionValidator.exceptionResponseIsUnauthorizedException(exceptionResponse, path);
                break;
            case "AUTHORIZED":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.CREATED.value()));
                ReviewJson responseReview = new ReviewJson(content);
                Long id = responseReview.getId();
                Optional<Review> optionalReview = reviewRepository.findById(id);

                assertThat(optionalReview.isPresent(), equalTo(true));
                Review savedReview = optionalReview.get();

                Validator.createReviewIsSuccessful(requestReview, savedReview, responseReview);
                break;
            case "MANDATORYFIELDMISSING":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value()));
                exceptionResponse = new ExceptionResponse(content);
                ExceptionValidator.exceptionResponseIsMandatoryFieldViolationException(exceptionResponse, path, Review.class.getSimpleName());
                break;
        }
    }


    @ParameterizedTest(name = "{0} user perform {3} case action when review in {1} status and missing property {2}, expect to be {4}")
    @CsvFileSource(resources = "/parameters/review/CaseAction.csv", numLinesToSkip = 1)
    void userToPerformCaseActionOnReviewInStatus(String user, ReviewStatus originalReviewStatus,
                                                 String missingProperty,
                                                 ReviewCaseActions reviewCaseAction, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long id = null;
        switch (missingProperty) {
            case "title":
                id = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES_EXCEPT_AUDIT_AND_TITLE", originalReviewStatus);
                break;
            case "executor":
                id = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES_EXCEPT_AUDIT_AND_EXECUTOR", originalReviewStatus);
                break;
            case "completion_date":
                id = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES_EXCEPT_AUDIT_AND_COMPLETION_DATE", originalReviewStatus);
                break;
            default:
                id = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalReviewStatus);
                break;
        }

        assert id != null;

        String path = PathGenerator.getReviewCaseActionPath(id, reviewCaseAction);

        Optional<Review> optionalReview = reviewRepository.findById(id);
        assertThat(optionalReview.isPresent(), equalTo(true));
        Review reviewBeforeCaseAction = optionalReview.get();


        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(patch(path))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        optionalReview = reviewRepository.findById(id);
        assertThat(optionalReview.isPresent(), equalTo(true));
        Review reviewAfterCaseAction = optionalReview.get();

        switch (expectedResult) {
            case "UNAUTHORIZED":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.FORBIDDEN.value()));
                ExceptionResponse exceptionResponse = new ExceptionResponse(content);
                Validator.unauthorizedExceptionAndReviewDidNotChange(reviewBeforeCaseAction, reviewAfterCaseAction, exceptionResponse, path, originalReviewStatus.getStatusCode());
                break;
            case "MANDATORYFIELDMISSING":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value()));
                exceptionResponse = new ExceptionResponse(content);
                ExceptionValidator.exceptionResponseIsMandatoryFieldViolationException(exceptionResponse, path, Review.class.getSimpleName());
                break;
            default:
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
                ReviewStatus newReviewStatus = ReviewStatus.valueOf(expectedResult);
                ReviewCaseStatusJson reviewCaseStatusJson = new ReviewCaseStatusJson(content);
                Validator.reviewsAreSameWithoutComparingAuditAndStatus(reviewBeforeCaseAction, reviewAfterCaseAction);
                assertThat("status after case action is not as expected", reviewAfterCaseAction.getStatus(), equalTo(newReviewStatus.getStatusCode()));
                assertThat("status in response and database are different", reviewCaseStatusJson.getStatus(), equalTo(reviewAfterCaseAction.getStatus()));
                break;
        }
    }


    @ParameterizedTest(name = "{0} user updates property {1} when review in {2} status, expect to be {3}")
    @CsvFileSource(resources = "/parameters/review/PropertyUpdate.csv", numLinesToSkip = 1)
    void userToUpdatePropertyOfReviewInStatus(String user, String property, ReviewStatus reviewStatus, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long id = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewStatus);
        assert id != null;

        String path = PathGenerator.getEntityUpdatePath("reviews", id);

        Optional<Review> optionalReview = reviewRepository.findById(id);
        assertThat(optionalReview.isPresent(), equalTo(true));
        Review reviewBeforeUpdate = optionalReview.get();

        JsonNode updateRequest = null;
        switch (property) {
            case "title":
                String titleValue = reviewBeforeUpdate.getTitle();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, titleValue);
                break;
            case "executor":
                User executorValue = reviewBeforeUpdate.getExecutor();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForUser(property, executorValue);
                break;
            case "completion_date":
                LocalDateTime completionDate = new Timestamp(reviewBeforeUpdate.getCompletionDate().getTime()).toLocalDateTime();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForLocalDateTime(property, completionDate);
                break;
            default:
        }

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(patch(path)
                .content(ObjectMapperUtil.getObjectMapper().writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        optionalReview = reviewRepository.findById(id);
        assertThat(optionalReview.isPresent(), equalTo(true));
        Review reviewAfterUpdate = optionalReview.get();

        switch (expectedResult) {
            case "UNAUTHORIZED":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.FORBIDDEN.value()));
                ExceptionResponse exceptionResponse = new ExceptionResponse(content);
                Validator.unauthorizedExceptionAndReviewDidNotChange(reviewBeforeUpdate, reviewAfterUpdate, exceptionResponse, path, reviewStatus.getStatusCode());
                break;
            case "AUTHORIZED":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
                ReviewJson reviewJson = new ReviewJson(content);
                //TODO check for review json
                switch (property) {
                    case "title":
                        Validator.reviewsAreSameWithoutComparingTitle(reviewBeforeUpdate, reviewAfterUpdate);
                        assertThat("title not changed", reviewBeforeUpdate.getTitle(), Matchers.not(equalTo(reviewAfterUpdate.getTitle())));
                        break;
                    case "executor":
                        Validator.reviewsAreSameWithoutComparingExecutor(reviewBeforeUpdate, reviewAfterUpdate);
                        assertThat("executors is null", reviewBeforeUpdate.getExecutor(), is(notNullValue()));
                        assertThat("executors are not same", reviewBeforeUpdate.getExecutor(), Matchers.not(samePropertyValuesAs(reviewAfterUpdate.getExecutor())));
                        break;
                    case "completion_date":
                        Validator.reviewsAreSameWithoutComparingCompletionDate(reviewBeforeUpdate, reviewAfterUpdate);
                        assertThat("completion date not changed", reviewBeforeUpdate.getCompletionDate(), Matchers.not(equalTo(reviewAfterUpdate.getCompletionDate())));
                        break;
                    default:
                }
                break;
        }
    }


    @ParameterizedTest(name = "{0} user has correct case permissions on review in status {1}")
    @MethodSource("com.example.mirai.projectname.reviewservice.fixtures.CasePermissionParametersFactory#userHasCorrectCasePermissionOnReviewInStatus")
    void userHasCorrectCasePermissionOnReviewInStatus(String user, ReviewStatus reviewStatus, String expectedContent) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long id = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewStatus);
        assert id != null;

        String path = PathGenerator.getReviewCasePermissionsPath(id);

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(get(path))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        JSONAssert.assertEquals("case permissions are not as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void lockReviewShouldLockReviewTasks() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long reviewId = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewStatus.OPENED);
        assert reviewId != null;
        Long reviewTaskId = entityInstanceManager.createReviewTaskAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewTaskStatus.OPENED, reviewId);
        assert reviewTaskId != null;
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        Optional<ReviewTask> optionalReviewTask = reviewTaskRepository.findById(reviewTaskId);
        assertThat(optionalReview.isPresent(), equalTo(true));
        assertThat(optionalReviewTask.isPresent(), equalTo(true));
        Review reviewBeforeCaseAction = optionalReview.get();
        ReviewTask reviewTaskBeforeCaseAction = optionalReviewTask.get();

        String path = PathGenerator.getReviewCaseActionPath(reviewId, ReviewCaseActions.LOCK);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));


        MvcResult result = getMockMvc().perform(patch(path))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        optionalReview = reviewRepository.findById(reviewId);
        optionalReviewTask = reviewTaskRepository.findById(reviewTaskId);
        assertThat(optionalReview.isPresent(), equalTo(true));
        Review reviewAfterCaseAction = optionalReview.get();
        ReviewTask reviewTaskAfterCaseAction = optionalReviewTask.get();
        ReviewStatus newReviewStatus = ReviewStatus.valueOf("LOCKED");
        ReviewCaseStatusJson reviewCaseStatusJson = new ReviewCaseStatusJson(content);
        Validator.reviewsAreSameWithoutComparingAuditAndStatus(reviewBeforeCaseAction, reviewAfterCaseAction);
        assertThat("status after case action is not as expected", reviewAfterCaseAction.getStatus(), equalTo(newReviewStatus.getStatusCode()));
        assertThat("status in response and database are different", reviewCaseStatusJson.getStatus(), equalTo(reviewAfterCaseAction.getStatus()));
        ReviewTaskStatus newReviewTaskStatus = ReviewTaskStatus.valueOf("NOTFINALIZED");
        com.example.mirai.projectname.reviewservice.tests.reviewtask.Validator.reviewTasksAreSameWithoutComparingStatus(reviewTaskBeforeCaseAction, reviewTaskAfterCaseAction);
        assertThat("status after case action is not as expected", reviewTaskAfterCaseAction.getStatus(), equalTo(newReviewTaskStatus.getStatusCode()));
        assertThat("status in response and database are different", reviewTaskAfterCaseAction.getStatus(), equalTo(reviewTaskAfterCaseAction.getStatus()));
    }

    @Test
    public void reopenReviewShouldChangeReviewTaskStatusToAcceptedWhenReviewEntryExist() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long reviewId = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewStatus.LOCKED);
        assert reviewId != null;
        Long reviewTaskId = entityInstanceManager.createReviewTaskAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewTaskStatus.NOTFINALIZED, reviewId);
        assert reviewTaskId != null;
        Long reviewEntryId = entityInstanceManager.createReviewEntryAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewId, reviewTaskId, ReviewEntryStatus.OPENED);
        assert reviewEntryId != null;
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        Optional<ReviewTask> optionalReviewTask = reviewTaskRepository.findById(reviewTaskId);
        Optional<ReviewEntry> optionalReviewEntry = reviewEntryRepository.findById(reviewEntryId);
        assertThat(optionalReview.isPresent(), equalTo(true));
        assertThat(optionalReviewTask.isPresent(), equalTo(true));
        assertThat(optionalReviewEntry.isPresent(), equalTo(true));
        Review reviewBeforeCaseAction = optionalReview.get();
        ReviewTask reviewTaskBeforeCaseAction = optionalReviewTask.get();

        String path = PathGenerator.getReviewCaseActionPath(reviewId, ReviewCaseActions.REOPEN);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(patch(path))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        optionalReview = reviewRepository.findById(reviewId);
        optionalReviewTask = reviewTaskRepository.findById(reviewTaskId);
        assertThat(optionalReview.isPresent(), equalTo(true));
        Review reviewAfterCaseAction = optionalReview.get();
        ReviewTask reviewTaskAfterCaseAction = optionalReviewTask.get();
        ReviewStatus newReviewStatus = ReviewStatus.valueOf("OPENED");
        ReviewCaseStatusJson reviewCaseStatusJson = new ReviewCaseStatusJson(content);
        Validator.reviewsAreSameWithoutComparingAuditAndStatus(reviewBeforeCaseAction, reviewAfterCaseAction);
        assertThat("status after case action is not as expected", reviewAfterCaseAction.getStatus(), equalTo(newReviewStatus.getStatusCode()));
        assertThat("status in response and database are different", reviewCaseStatusJson.getStatus(), equalTo(reviewAfterCaseAction.getStatus()));
        ReviewTaskStatus newReviewTaskStatus = ReviewTaskStatus.valueOf("ACCEPTED");
        com.example.mirai.projectname.reviewservice.tests.reviewtask.Validator.reviewTasksAreSameWithoutComparingStatus(reviewTaskBeforeCaseAction, reviewTaskAfterCaseAction);
        assertThat("status after case action is not as expected", reviewTaskAfterCaseAction.getStatus(), equalTo(newReviewTaskStatus.getStatusCode()));
        assertThat("status in response and database are different", reviewTaskAfterCaseAction.getStatus(), equalTo(reviewTaskAfterCaseAction.getStatus()));
    }

    @Test
    public void reopenReviewShouldChangeReviewTaskStatusToOpenedWhenReviewEntryNotExist() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long reviewId = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewStatus.LOCKED);
        assert reviewId != null;
        Long reviewTaskId = entityInstanceManager.createReviewTaskAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewTaskStatus.NOTFINALIZED, reviewId);
        assert reviewTaskId != null;
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        Optional<ReviewTask> optionalReviewTask = reviewTaskRepository.findById(reviewTaskId);
        assertThat(optionalReview.isPresent(), equalTo(true));
        assertThat(optionalReviewTask.isPresent(), equalTo(true));
        Review reviewBeforeCaseAction = optionalReview.get();
        ReviewTask reviewTaskBeforeCaseAction = optionalReviewTask.get();

        String path = PathGenerator.getReviewCaseActionPath(reviewId, ReviewCaseActions.REOPEN);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(patch(path))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        optionalReview = reviewRepository.findById(reviewId);
        optionalReviewTask = reviewTaskRepository.findById(reviewTaskId);
        assertThat(optionalReview.isPresent(), equalTo(true));
        Review reviewAfterCaseAction = optionalReview.get();
        ReviewTask reviewTaskAfterCaseAction = optionalReviewTask.get();
        ReviewStatus newReviewStatus = ReviewStatus.valueOf("OPENED");
        ReviewCaseStatusJson reviewCaseStatusJson = new ReviewCaseStatusJson(content);
        Validator.reviewsAreSameWithoutComparingAuditAndStatus(reviewBeforeCaseAction, reviewAfterCaseAction);
        assertThat("status after case action is not as expected", reviewAfterCaseAction.getStatus(), equalTo(newReviewStatus.getStatusCode()));
        assertThat("status in response and database are different", reviewCaseStatusJson.getStatus(), equalTo(reviewAfterCaseAction.getStatus()));
        ReviewTaskStatus newReviewTaskStatus = ReviewTaskStatus.valueOf("OPENED");
        com.example.mirai.projectname.reviewservice.tests.reviewtask.Validator.reviewTasksAreSameWithoutComparingStatus(reviewTaskBeforeCaseAction, reviewTaskAfterCaseAction);
        assertThat("status after case action is not as expected", reviewTaskAfterCaseAction.getStatus(), equalTo(newReviewTaskStatus.getStatusCode()));
        assertThat("status in response and database are different", reviewTaskAfterCaseAction.getStatus(), equalTo(reviewTaskAfterCaseAction.getStatus()));
    }

    @Test
    public void reviewCompleteShouldThrowReviewCompletionExceptionWhenReviewEntriesNotInFinalStatus() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long reviewId = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewStatus.VALIDATIONSTARTED);
        assert reviewId != null;
        Long reviewTaskId = entityInstanceManager.createReviewTaskAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewTaskStatus.COMPLETED, reviewId);
        assert reviewTaskId != null;
        Long reviewEntryId = entityInstanceManager.createReviewEntryAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewId, reviewTaskId, ReviewEntryStatus.OPENED);
        assert reviewEntryId != null;
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        Optional<ReviewTask> optionalReviewTask = reviewTaskRepository.findById(reviewTaskId);
        Optional<ReviewEntry> optionalReviewEntry = reviewEntryRepository.findById(reviewEntryId);
        assertThat(optionalReview.isPresent(), equalTo(true));
        assertThat(optionalReviewTask.isPresent(), equalTo(true));
        assertThat(optionalReviewEntry.isPresent(), equalTo(true));
        Review reviewBeforeCaseAction = optionalReview.get();
        ReviewTask reviewTaskBeforeCaseAction = optionalReviewTask.get();
        ReviewEntry reviewEntryBeforeCaseAction = optionalReviewEntry.get();

        String path = PathGenerator.getReviewCaseActionPath(reviewId, ReviewCaseActions.COMPLETE);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(patch(path))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        optionalReview = reviewRepository.findById(reviewId);
        optionalReviewTask = reviewTaskRepository.findById(reviewTaskId);
        optionalReviewEntry = reviewEntryRepository.findById(reviewEntryId);

        assertThat(optionalReview.isPresent(), equalTo(true));
        assertThat(optionalReviewTask.isPresent(), equalTo(true));
        assertThat(optionalReviewEntry.isPresent(), equalTo(true));

        ExceptionResponse exceptionResponse = new ExceptionResponse(content);
        ExceptionValidator.exceptionResponseIsReviewCompletionException(exceptionResponse, path);
    }

    @Test
    public void forcedReviewCompletionWhenReviewEntriesNotInFinalStatusIsSuccessful() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long reviewId = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewStatus.VALIDATIONSTARTED);
        assert reviewId != null;
        Long reviewTaskId = entityInstanceManager.createReviewTaskAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewTaskStatus.COMPLETED, reviewId);
        assert reviewTaskId != null;
        Long reviewEntryId = entityInstanceManager.createReviewEntryAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewId, reviewTaskId, ReviewEntryStatus.OPENED);
        assert reviewEntryId != null;
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        Optional<ReviewTask> optionalReviewTask = reviewTaskRepository.findById(reviewTaskId);
        Optional<ReviewEntry> optionalReviewEntry = reviewEntryRepository.findById(reviewEntryId);
        assertThat(optionalReview.isPresent(), equalTo(true));
        assertThat(optionalReviewTask.isPresent(), equalTo(true));
        assertThat(optionalReviewEntry.isPresent(), equalTo(true));
        Review reviewBeforeCaseAction = optionalReview.get();
        ReviewTask reviewTaskBeforeCaseAction = optionalReviewTask.get();
        ReviewEntry reviewEntryBeforeCaseAction = optionalReviewEntry.get();

        String path = PathGenerator.getReviewCaseActionPathWithForceComplete(reviewId, ReviewCaseActions.COMPLETE);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(patch(path))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        optionalReview = reviewRepository.findById(reviewId);
        optionalReviewTask = reviewTaskRepository.findById(reviewTaskId);
        optionalReviewEntry = reviewEntryRepository.findById(reviewEntryId);

        assertThat(optionalReview.isPresent(), equalTo(true));
        assertThat(optionalReviewTask.isPresent(), equalTo(true));
        assertThat(optionalReviewEntry.isPresent(), equalTo(true));

        Review reviewAfterCaseAction = optionalReview.get();
        ReviewTask reviewTaskAfterCaseAction = optionalReviewTask.get();
        ReviewEntry reviewEntryAfterCaseAction = optionalReviewEntry.get();

        ReviewStatus newReviewStatus = ReviewStatus.valueOf("COMPLETED");
        ReviewCaseStatusJson reviewCaseStatusJson = new ReviewCaseStatusJson(content);
        Validator.reviewsAreSameWithoutComparingAuditAndStatus(reviewBeforeCaseAction, reviewAfterCaseAction);
        assertThat("status after case action is not as expected", reviewAfterCaseAction.getStatus(), equalTo(newReviewStatus.getStatusCode()));
        assertThat("status in response and database are different", reviewCaseStatusJson.getStatus(), equalTo(reviewAfterCaseAction.getStatus()));
        com.example.mirai.projectname.reviewservice.tests.reviewtask.Validator.reviewTasksAreSame(reviewTaskBeforeCaseAction, reviewTaskAfterCaseAction);
        com.example.mirai.projectname.reviewservice.tests.reviewentry.Validator.reviewEntriesAreSame(reviewEntryBeforeCaseAction, reviewEntryAfterCaseAction);
    }

    @Test
    public void startValidationOnReviewIsNotPossibleWhenReleasePackageIsClosed() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long reviewId = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewStatus.LOCKED);
        assert reviewId != null;

        entityInstanceManager.setReleasePackageStatus(reviewId, "CLOSED");
        String path = PathGenerator.getReviewCaseActionPath(reviewId, ReviewCaseActions.STARTVALIDATION);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(patch(path))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        assertThat(optionalReview.isPresent(), equalTo(true));

        String content = result.getResponse().getContentAsString();
        ExceptionResponse exceptionResponse = new ExceptionResponse(content);
        ExceptionValidator.exceptionResponseIsUnauthorizedException(exceptionResponse, path);
    }
}
