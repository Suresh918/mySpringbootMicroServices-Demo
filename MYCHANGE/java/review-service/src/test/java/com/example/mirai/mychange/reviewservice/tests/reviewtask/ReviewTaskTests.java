package com.example.mirai.projectname.reviewservice.tests.reviewtask;


import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.projectname.reviewservice.fixtures.EntityPojoFactory;
import com.example.mirai.projectname.reviewservice.fixtures.JsonNodeFactory;
import com.example.mirai.projectname.reviewservice.fixtures.JwtFactory;
import com.example.mirai.projectname.reviewservice.json.ExceptionResponse;
import com.example.mirai.projectname.reviewservice.json.ReviewJson;
import com.example.mirai.projectname.reviewservice.json.ReviewTaskCaseStatusJson;
import com.example.mirai.projectname.reviewservice.json.ReviewTaskJson;
import com.example.mirai.projectname.reviewservice.review.model.ReviewStatus;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntryStatus;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTaskCaseActions;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReviewTaskTests extends BaseTest {

    @ParameterizedTest(name = "{0} user is to create review task with review in {1} status with missing property {2}, expect to be {3}")
    @CsvFileSource(resources = "/parameters/reviewtask/Create.csv", numLinesToSkip = 1)
    void userToCreateReviewTask(String user, ReviewStatus reviewStatus, String missingProperty, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long reviewId = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewStatus);

        String path = PathGenerator.getReviewTaskCreationPath(reviewId);

        ReviewTask requestReviewTask = null;
        switch (missingProperty) {
            case "assignee":
                requestReviewTask = EntityPojoFactory.createReviewTask(dataIdentifier, "ALL_PROPERTIES_EXCEPT_AUDIT_AND_ASSIGNEE");
                break;
            case "due_date":
                requestReviewTask = EntityPojoFactory.createReviewTask(dataIdentifier, "ALL_PROPERTIES_EXCEPT_AUDIT_AND_DUEDATE");
                break;
            default:
                requestReviewTask = EntityPojoFactory.createReviewTask(dataIdentifier, "ALL_PROPERTIES_EXCEPT_AUDIT");
                break;
        }


        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(post(path)
                .content(ObjectMapperUtil.getObjectMapper().writeValueAsString(requestReviewTask))
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
                ReviewTaskJson responseReviewTask = new ReviewTaskJson(content);
                Long id = responseReviewTask.getId();
                Optional<ReviewTask> optionalReviewTask = reviewTaskRepository.findById(id);

                assertThat(optionalReviewTask.isPresent(), equalTo(true));
                ReviewTask savedReviewTask = optionalReviewTask.get();

                Validator.createReviewTaskIsSuccessful(requestReviewTask, savedReviewTask, responseReviewTask);
                break;
            case "MANDATORYFIELDMISSING":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value()));
                exceptionResponse = new ExceptionResponse(content);
                ExceptionValidator.exceptionResponseIsMandatoryFieldViolationException(exceptionResponse, path, ReviewTask.class.getSimpleName());
                break;
        }
    }

    @ParameterizedTest(name = "{0} user perform {5} case action on review task in status {2} with missing property {4} when review in {1} status and has {3} review entry, expect to be {6}")
    @CsvFileSource(resources = "/parameters/reviewtask/CaseAction.csv", numLinesToSkip = 1)
    void userToPerformCaseActionOnReviewTaskWhenReviewInStatusAndReviewTaskInStatus(String user, ReviewStatus reviewStatus, ReviewTaskStatus originalReviewTaskStatus,
                                                                                    String reviewEntryStatusString, String missingProperty,
                                                                                    ReviewTaskCaseActions reviewTaskCaseAction, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long reviewId = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewStatus);
        assert reviewId != null;

        Long reviewTaskId = null;
        switch (missingProperty) {
            case "assignee":
                reviewTaskId = entityInstanceManager.createReviewTaskAndSetStatus(dataIdentifier, "ALL_PROPERTIES_EXCEPT_ASSIGNEE", originalReviewTaskStatus, reviewId);
                break;
            case "due_date":
                reviewTaskId = entityInstanceManager.createReviewTaskAndSetStatus(dataIdentifier, "ALL_PROPERTIES_EXCEPT_DUEDATE", originalReviewTaskStatus, reviewId);
                break;
            default:
                reviewTaskId = entityInstanceManager.createReviewTaskAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalReviewTaskStatus, reviewId);
                break;
        }
        assert reviewTaskId != null;

        switch (reviewEntryStatusString) {
            case "NOT_CREATED":
                break;
            default:
                ReviewEntryStatus reviewEntryStatus = ReviewEntryStatus.valueOf(reviewEntryStatusString);
                Long reviewEntryId = entityInstanceManager.createReviewEntryAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewId, reviewTaskId, reviewEntryStatus);
                assert reviewEntryId != null;
                break;
        }

        Optional<ReviewTask> optionalReviewTask = reviewTaskRepository.findById(reviewTaskId);
        assertThat(optionalReviewTask.isPresent(), equalTo(true));
        ReviewTask reviewTaskBeforeCaseAction = optionalReviewTask.get();

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = null;
        String path = null;
        path = PathGenerator.getReviewTaskCaseActionPath(reviewTaskId, reviewTaskCaseAction);

        result = getMockMvc().perform(patch(path))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();

        optionalReviewTask = reviewTaskRepository.findById(reviewTaskId);
        assertThat(optionalReviewTask.isPresent(), equalTo(true));
        ReviewTask reviewTaskAfterCaseAction = optionalReviewTask.get();

        switch (expectedResult) {
            case "UNAUTHORIZED":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.FORBIDDEN.value()));
                ExceptionResponse exceptionResponse = new ExceptionResponse(content);
                Validator.unauthorizedExceptionAndReviewTaskDidNotChange(reviewTaskBeforeCaseAction, reviewTaskAfterCaseAction, exceptionResponse, path, originalReviewTaskStatus.getStatusCode());
                break;
            case "MANDATORYFIELDMISSING":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value()));
                exceptionResponse = new ExceptionResponse(content);
                //TODO check for review task did not change
                ExceptionValidator.exceptionResponseIsMandatoryFieldViolationException(exceptionResponse, path, ReviewTask.class.getSimpleName());
                break;
            default:
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
                ReviewTaskStatus reviewTaskStatus = ReviewTaskStatus.valueOf(expectedResult);
                ReviewTaskCaseStatusJson reviewTaskCaseStatusJson = new ReviewTaskCaseStatusJson(content);
                Validator.reviewTaskAreSameWithoutComparingAuditAndStatus(reviewTaskBeforeCaseAction, reviewTaskAfterCaseAction);
                assertThat("status after case action is not as expected", reviewTaskAfterCaseAction.getStatus(), equalTo(reviewTaskStatus.getStatusCode()));
                assertThat("status in response and database are different", reviewTaskCaseStatusJson.getStatus(), equalTo(reviewTaskAfterCaseAction.getStatus()));
                break;
        }
    }

    @ParameterizedTest(name = "{0} user perform {5} case action on review task in status {2} with missing property {4} when review in {1} status and has {3} review entry, expect to be {6}")
    @CsvFileSource(resources = "/parameters/reviewtask/Delete.csv", numLinesToSkip = 1)
    void userToPerformDeleteOnReviewTaskWhenReviewInStatusAndReviewTaskInStatus(String user, ReviewStatus reviewStatus, ReviewTaskStatus originalReviewTaskStatus,
                                                                                String reviewEntryStatusString, String missingProperty,
                                                                                ReviewTaskCaseActions reviewTaskCaseAction, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long reviewId = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewStatus);
        assert reviewId != null;

        Long reviewTaskId = null;
        switch (missingProperty) {
            case "assignee":
                reviewTaskId = entityInstanceManager.createReviewTaskAndSetStatus(dataIdentifier, "ALL_PROPERTIES_EXCEPT_AUDIT_AND_ASSIGNEE", originalReviewTaskStatus, reviewId);
                break;
            case "due_date":
                reviewTaskId = entityInstanceManager.createReviewTaskAndSetStatus(dataIdentifier, "ALL_PROPERTIES_EXCEPT_AUDIT_AND_DUEDATE", originalReviewTaskStatus, reviewId);
                break;
            default:
                reviewTaskId = entityInstanceManager.createReviewTaskAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalReviewTaskStatus, reviewId);
                break;
        }
        assert reviewTaskId != null;

        switch (reviewEntryStatusString) {
            case "NOT_CREATED":
                break;
            default:
                ReviewEntryStatus reviewEntryStatus = ReviewEntryStatus.valueOf(reviewEntryStatusString);
                Long reviewEntryId = entityInstanceManager.createReviewEntryAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewId, reviewTaskId, reviewEntryStatus);
                assert reviewEntryId != null;
                break;
        }

        Optional<ReviewTask> optionalReviewTask = reviewTaskRepository.findById(reviewTaskId);
        assertThat(optionalReviewTask.isPresent(), equalTo(true));
        ReviewTask reviewTaskBeforeCaseAction = optionalReviewTask.get();

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = null;
        String path;
        path = PathGenerator.getReviewTaskDeletePath(reviewTaskId);
        result = getMockMvc().perform(delete(path))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        optionalReviewTask = reviewTaskRepository.findById(reviewTaskId);
        assertThat(optionalReviewTask.isPresent(), equalTo(true));
        ReviewTask reviewTaskAfterCaseAction = optionalReviewTask.get();

        switch (expectedResult) {
            case "UNAUTHORIZED":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.FORBIDDEN.value()));
                ExceptionResponse exceptionResponse = new ExceptionResponse(content);
                Validator.unauthorizedExceptionAndReviewTaskDidNotChange(reviewTaskBeforeCaseAction, reviewTaskAfterCaseAction, exceptionResponse, path, originalReviewTaskStatus.getStatusCode());
                break;
            default:
                optionalReviewTask = reviewTaskRepository.findById(reviewTaskId);
                assertThat(optionalReviewTask.isPresent(), equalTo(false));
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
                break;
        }

    }

    @ParameterizedTest(name = "{0} user updates property {1} when review task in {2} status, expect to be {3}")
    @CsvFileSource(resources = "/parameters/reviewtask/PropertyUpdate.csv", numLinesToSkip = 1)
    void userToUpdatePropertyOfReviewTaskInStatus(String user, String property, ReviewTaskStatus reviewTaskStatus, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long reviewId = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewStatus.OPENED);
        assert reviewId != null;

        Long reviewTaskId = entityInstanceManager.createReviewTaskAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewTaskStatus, reviewId);

        String path = PathGenerator.getEntityUpdatePath("review-tasks", reviewTaskId);

        Optional<ReviewTask> optionalReviewTask = reviewTaskRepository.findById(reviewTaskId);
        assertThat(optionalReviewTask.isPresent(), equalTo(true));
        ReviewTask reviewTaskBeforeUpdate = optionalReviewTask.get();

        JsonNode updateRequest = null;
        switch (property) {
            case "assignee":
                User assigneeValue = reviewTaskBeforeUpdate.getAssignee();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForUser(property, assigneeValue);
                break;
            case "due_date":
                LocalDateTime dueDate = new Timestamp(reviewTaskBeforeUpdate.getDueDate().getTime()).toLocalDateTime();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForLocalDateTime(property, dueDate);
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

        optionalReviewTask = reviewTaskRepository.findById(reviewTaskId);
        assertThat(optionalReviewTask.isPresent(), equalTo(true));
        ReviewTask reviewTaskAfterUpdate = optionalReviewTask.get();

        switch (expectedResult) {
            case "UNAUTHORIZED":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.FORBIDDEN.value()));
                ExceptionResponse exceptionResponse = new ExceptionResponse(content);
                Validator.unauthorizedExceptionAndReviewTaskDidNotChange(reviewTaskBeforeUpdate, reviewTaskAfterUpdate, exceptionResponse, path, reviewTaskStatus.getStatusCode());
                break;
            case "AUTHORIZED":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
                ReviewJson reviewJson = new ReviewJson(content);
                switch (property) {
                    case "assignee":
                        Validator.reviewTasksAreSameWithoutComparingAssignee(reviewTaskBeforeUpdate, reviewTaskAfterUpdate);
                        assertThat("assignees is null", reviewTaskBeforeUpdate.getAssignee(), is(notNullValue()));
                        assertThat("assignees are not same", reviewTaskBeforeUpdate.getAssignee(), Matchers.not(samePropertyValuesAs(reviewTaskAfterUpdate.getAssignee())));
                        break;
                    case "due_date":
                        Validator.reviewTasksAreSameWithoutComparingDueDate(reviewTaskBeforeUpdate, reviewTaskAfterUpdate);
                        assertThat("due date not changed", reviewTaskBeforeUpdate.getDueDate(), Matchers.not(equalTo(reviewTaskAfterUpdate.getDueDate())));
                        break;
                    default:
                }
                break;
        }
    }

    @ParameterizedTest(name = "{0} user has correct case permissions on review task in status {1}")
    @MethodSource("com.example.mirai.projectname.reviewservice.fixtures.CasePermissionParametersFactory#userHasCorrectCasePermissionOnReviewTaskInStatus")
    void userHasCorrectCasePermissionOnReviewTaskInStatus(String user, ReviewTaskStatus reviewTaskStatus, String expectedContent) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long reviewId = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewStatus.OPENED);
        assert reviewId != null;

        Long reviewTaskId = entityInstanceManager.createReviewTaskAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewTaskStatus, reviewId);
        assert reviewTaskId != null;

        if (user.equals("review-entry-assignee")) {
            Long reviewEntryId = entityInstanceManager.createReviewEntryAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewId, reviewTaskId, ReviewEntryStatus.OPENED);
            assert reviewEntryId != null;
        }

        String path = PathGenerator.getReviewTaskCasePermissionsPath(reviewTaskId);
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
    public void notAllowedToDeleteReviewTaskWhenReviewEntryIsCreated() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long reviewId = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewStatus.OPENED);
        assert reviewId != null;

        Long reviewTaskId = entityInstanceManager.createReviewTaskAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewTaskStatus.OPENED, reviewId);
        assert reviewTaskId != null;

        Long reviewEntryId = entityInstanceManager.createReviewEntryAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewId, reviewTaskId, ReviewEntryStatus.OPENED);
        assert reviewEntryId != null;

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        String path = PathGenerator.getReviewTaskDeletePath(reviewTaskId);

        MvcResult result = getMockMvc().perform(delete(path))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionResponse exceptionResponse = new ExceptionResponse(content);
        ExceptionValidator.exceptionResponseIsUnauthorizedException(exceptionResponse, path);
    }

    @Test
    public void deleteReviewTaskIsNotAllowedWhenReviewEntryIsCreated() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        InputStream inputStream = ReviewTaskTests.class.getResourceAsStream("/expectations/reviewtask/casepermissionsothers/ReviewEntryCreated.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String reviewTaskCasePermissions = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        Long reviewId = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewStatus.OPENED);
        assert reviewId != null;

        Long reviewTaskId = entityInstanceManager.createReviewTaskAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewTaskStatus.OPENED, reviewId);
        assert reviewTaskId != null;

        Long reviewEntryId = entityInstanceManager.createReviewEntryAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewId, reviewTaskId, ReviewEntryStatus.OPENED);
        assert reviewEntryId != null;

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        String path = PathGenerator.getReviewTaskCasePermissionsPath(reviewTaskId);

        MvcResult result = getMockMvc().perform(get(path))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JSONAssert.assertEquals("case permissions are not as expected", reviewTaskCasePermissions, content, JSONCompareMode.NON_EXTENSIBLE);
    }
    //not allowed to deleted task when a review entry is created
    //check for case permissions to not allowe to deleted task when a review entry is created

    @Test
    public void shouldThrowReviewTaskExistsExceptionWhenReviewTaskAlreadyExistsForAReview() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long reviewId = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewStatus.OPENED);
        assert reviewId != null;

        Long reviewTaskId = entityInstanceManager.createReviewTaskAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewTaskStatus.OPENED, reviewId);
        assert reviewTaskId != null;

        String path = PathGenerator.getReviewTaskCreationPath(reviewId);
        ReviewTask requestReviewTask = EntityPojoFactory.createReviewTask(dataIdentifier, "ALL_PROPERTIES_EXCEPT_AUDIT");
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(post(path)
                .with(jwt().jwt(jwt))
                .content(ObjectMapperUtil.getObjectMapper().writeValueAsString(requestReviewTask))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ExceptionResponse exceptionResponse = new ExceptionResponse(content);
        ExceptionValidator.exceptionResponseIsReviewTaskExistsException(exceptionResponse, path);
    }

}
