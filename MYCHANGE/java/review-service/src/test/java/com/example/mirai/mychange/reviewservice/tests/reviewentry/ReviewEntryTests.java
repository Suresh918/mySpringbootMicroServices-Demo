package com.example.mirai.projectname.reviewservice.tests.reviewentry;


import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.projectname.reviewservice.fixtures.EntityPojoFactory;
import com.example.mirai.projectname.reviewservice.fixtures.JsonNodeFactory;
import com.example.mirai.projectname.reviewservice.fixtures.JwtFactory;
import com.example.mirai.projectname.reviewservice.json.ExceptionResponse;
import com.example.mirai.projectname.reviewservice.json.ReviewEntryCaseStatusJson;
import com.example.mirai.projectname.reviewservice.json.ReviewEntryJson;
import com.example.mirai.projectname.reviewservice.json.ReviewJson;
import com.example.mirai.projectname.reviewservice.review.model.ReviewStatus;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntry;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntryCaseActions;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntryStatus;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTaskStatus;
import com.example.mirai.projectname.reviewservice.tests.BaseTest;
import com.example.mirai.projectname.reviewservice.tests.ExceptionValidator;
import com.example.mirai.projectname.reviewservice.utils.ObjectMapperUtil;
import com.example.mirai.projectname.reviewservice.utils.PathGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import org.hamcrest.Matchers;
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

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReviewEntryTests extends BaseTest {

    @ParameterizedTest(name = "{0} user is to create review entry with review in {1} status, review task in {2} status, with missing property {3}, expect to be {4}")
    @CsvFileSource(resources = "/parameters/reviewentry/Create.csv", numLinesToSkip = 1)
    void userToCreateReviewEntry(String user, ReviewStatus reviewStatus, ReviewTaskStatus reviewTaskStatus, String missingProperty, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long reviewId = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewStatus);
        assert reviewId != null;

        Long reviewTaskId = entityInstanceManager.createReviewTaskAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewTaskStatus, reviewId);
        assert reviewTaskId != null;

        String path = PathGenerator.getReviewEntryCreationPath(reviewId);

        ReviewEntry requestReviewEntry = null;
        switch (missingProperty) {
            case "classification":
                requestReviewEntry = EntityPojoFactory.createReviewEntry(dataIdentifier, "ALL_PROPERTIES_EXCEPT_AUDIT_AND_CLASSIFICATION");
                break;
            case "description":
                requestReviewEntry = EntityPojoFactory.createReviewEntry(dataIdentifier, "ALL_PROPERTIES_EXCEPT_AUDIT_AND_DESCRIPTION");
                break;
            case "assignee":
                requestReviewEntry = EntityPojoFactory.createReviewEntry(dataIdentifier, "ALL_PROPERTIES_EXCEPT_AUDIT_AND_ASSIGNEE");
                break;
            default:
                requestReviewEntry = EntityPojoFactory.createReviewEntry(dataIdentifier, "ALL_PROPERTIES_EXCEPT_AUDIT");
                break;
        }


        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(post(path)
                .content(ObjectMapperUtil.getObjectMapper().writeValueAsString(requestReviewEntry))
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
                ReviewEntryJson responseReviewEntry = new ReviewEntryJson(content);
                Long id = responseReviewEntry.getId();
                Optional<ReviewEntry> optionalReviewEntry = reviewEntryRepository.findById(id);

                assertThat(optionalReviewEntry.isPresent(), equalTo(true));
                ReviewEntry savedReviewEntry = optionalReviewEntry.get();

                Validator.createReviewEntryIsSuccessful(requestReviewEntry, savedReviewEntry, responseReviewEntry);
                break;
            case "MANDATORYFIELDMISSING":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value()));
                exceptionResponse = new ExceptionResponse(content);
                ExceptionValidator.exceptionResponseIsMandatoryFieldViolationException(exceptionResponse, path, ReviewEntry.class.getSimpleName());
                break;
        }
    }

    @ParameterizedTest(name = "{0} user is to perform {5} case action on review entry in status {3}, when review task in status {2} and review in {1} status, with missing property {4}, expect to be {6}")
    @CsvFileSource(resources = "/parameters/reviewentry/CaseAction.csv", numLinesToSkip = 1)
    void userToPerformCaseActionOnReviewEntryWhenReviewInStatusAndReviewTaskInStatusAndReviewEntryInStatusWithMissingProperty(String user, ReviewStatus reviewStatus, ReviewTaskStatus reviewTaskStatus,
                                                                                                                              ReviewEntryStatus originalReviewEntryStatus, String missingProperty,
                                                                                                                              ReviewEntryCaseActions reviewEntryCaseAction, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long reviewId = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewStatus);
        assert reviewId != null;

        Long reviewTaskId = entityInstanceManager.createReviewTaskAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewTaskStatus, reviewId);
        assert reviewTaskId != null;

        Long reviewEntryId = null;
        switch (missingProperty) {
            case "classification":
                reviewEntryId = entityInstanceManager.createReviewEntryAndSetStatus(dataIdentifier, "ALL_PROPERTIES_EXCEPT_CLASSIFICATION", reviewId, reviewTaskId, originalReviewEntryStatus);
                break;
            case "description":
                reviewEntryId = entityInstanceManager.createReviewEntryAndSetStatus(dataIdentifier, "ALL_PROPERTIES_EXCEPT_DESCRIPTION", reviewId, reviewTaskId, originalReviewEntryStatus);
                break;
            case "assignee":
                reviewEntryId = entityInstanceManager.createReviewEntryAndSetStatus(dataIdentifier, "ALL_PROPERTIES_EXCEPT_ASSIGNEE", reviewId, reviewTaskId, originalReviewEntryStatus);
                break;
            default:
                reviewEntryId = entityInstanceManager.createReviewEntryAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewId, reviewTaskId, originalReviewEntryStatus);
                break;
        }
        assert reviewEntryId != null;
        String path = PathGenerator.getReviewEntryCaseActionPath(reviewEntryId, reviewEntryCaseAction);
        Optional<ReviewEntry> optionalReviewEntry = reviewEntryRepository.findById(reviewEntryId);
        assertThat(optionalReviewEntry.isPresent(), equalTo(true));
        ReviewEntry reviewEntryBeforeCaseAction = optionalReviewEntry.get();

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(patch(path))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        optionalReviewEntry = reviewEntryRepository.findById(reviewEntryId);
        assertThat(optionalReviewEntry.isPresent(), equalTo(true));
        ReviewEntry reviewEntryAfterCaseAction = optionalReviewEntry.get();

        switch (expectedResult) {
            case "UNAUTHORIZED":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.FORBIDDEN.value()));
                ExceptionResponse exceptionResponse = new ExceptionResponse(content);
                Validator.unauthorizedExceptionAndReviewEntryDidNotChange(reviewEntryBeforeCaseAction, reviewEntryAfterCaseAction, exceptionResponse, path, originalReviewEntryStatus);
                break;
            case "MANDATORYFIELDMISSING":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value()));
                exceptionResponse = new ExceptionResponse(content);
                ExceptionValidator.exceptionResponseIsMandatoryFieldViolationException(exceptionResponse, path, ReviewEntry.class.getSimpleName());
                break;
            case "APPLICATIONEXCEPTION":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value()));
                exceptionResponse = new ExceptionResponse(content);
                ExceptionValidator.exceptionResponseIsApplicationException(exceptionResponse, path, ReviewEntry.class.getSimpleName());
                break;
            default:
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
                ReviewEntryStatus reviewEntryStatus = ReviewEntryStatus.valueOf(expectedResult);
                ReviewEntryCaseStatusJson reviewEntryCaseStatusJson = new ReviewEntryCaseStatusJson(content);
                Validator.reviewEntriesAreSameWithoutComparingAuditAndStatus(reviewEntryBeforeCaseAction, reviewEntryAfterCaseAction);
                assertThat("status after case action is not as expected", reviewEntryAfterCaseAction.getStatus(), equalTo(reviewEntryStatus.getStatusCode()));
                assertThat("status in response and database are different", reviewEntryCaseStatusJson.getStatus(), equalTo(reviewEntryAfterCaseAction.getStatus()));
                break;
        }
    }


    @ParameterizedTest(name = "{0} user updates property {1} when review entry in {4} status,review task in {3} status, review in {2} status expect to be {5}")
    @CsvFileSource(resources = "/parameters/reviewentry/PropertyUpdate.csv", numLinesToSkip = 1)
    void userToUpdatePropertyOfReviewEntryInStatus(String user, String property, ReviewStatus reviewStatus, ReviewTaskStatus reviewTaskStatus,
                                                   ReviewEntryStatus reviewEntryStatus, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long reviewId = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewStatus);
        assert reviewId != null;

        Long reviewTaskId = entityInstanceManager.createReviewTaskAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewTaskStatus, reviewId);
        assert reviewTaskId != null;

        Long reviewEntryId = entityInstanceManager.createReviewEntryAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewId, reviewTaskId, reviewEntryStatus);
        assert reviewEntryId != null;

        String path = PathGenerator.getEntityUpdatePath("review-entries", reviewEntryId);

        Optional<ReviewEntry> optionalReviewEntry = reviewEntryRepository.findById(reviewEntryId);
        assertThat(optionalReviewEntry.isPresent(), equalTo(true));
        ReviewEntry reviewEntryBeforeUpdate = optionalReviewEntry.get();

        JsonNode updateRequest = null;
        switch (property) {
            case "assignee":
                User assigneeValue = reviewEntryBeforeUpdate.getAssignee();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForUser(property, assigneeValue);
                break;
            case "classification":
                String classificationValue = reviewEntryBeforeUpdate.getClassification();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, classificationValue);
                break;
            case "description":
                String descriptionValue = reviewEntryBeforeUpdate.getDescription();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, descriptionValue);
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

        optionalReviewEntry = reviewEntryRepository.findById(reviewEntryId);
        assertThat(optionalReviewEntry.isPresent(), equalTo(true));
        ReviewEntry reviewEntryAfterUpdate = optionalReviewEntry.get();

        switch (expectedResult) {
            case "UNAUTHORIZED":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.FORBIDDEN.value()));
                ExceptionResponse exceptionResponse = new ExceptionResponse(content);
                Validator.unauthorizedExceptionAndReviewEntryDidNotChange(reviewEntryBeforeUpdate, reviewEntryAfterUpdate, exceptionResponse, path, reviewEntryStatus);
                break;
            case "AUTHORIZED":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
                ReviewJson reviewJson = new ReviewJson(content);
                switch (property) {
                    case "assignee":
                        Validator.reviewEntriesAreSameWithoutComparingAssignee(reviewEntryBeforeUpdate, reviewEntryAfterUpdate);
                        if (reviewEntryBeforeUpdate.getAssignee() != null || reviewEntryAfterUpdate.getAssignee() != null) {
                            assertThat("Assignee userIds are same", reviewEntryBeforeUpdate.getAssignee().getUserId(), Matchers.not(equalTo(reviewEntryAfterUpdate.getAssignee().getUserId())));
                            assertThat("Assignees Abbreviations are same", reviewEntryBeforeUpdate.getAssignee().getAbbreviation(), Matchers.not(equalTo(reviewEntryAfterUpdate.getAssignee().getAbbreviation())));
                            assertThat("Assignees Full Names are same", reviewEntryBeforeUpdate.getAssignee().getFullName(), Matchers.not(equalTo(reviewEntryAfterUpdate.getAssignee().getFullName())));
                            assertThat("Assignees Department name are same", reviewEntryBeforeUpdate.getAssignee().getDepartmentName(), Matchers.not(equalTo(reviewEntryAfterUpdate.getAssignee().getDepartmentName())));
                            assertThat("Assignees emails are same", reviewEntryBeforeUpdate.getAssignee().getEmail(), Matchers.not(equalTo(reviewEntryAfterUpdate.getAssignee().getEmail())));
                        }
                        break;
                    case "classification":
                        Validator.reviewEntriesAreSameWithoutComparingClassification(reviewEntryBeforeUpdate, reviewEntryAfterUpdate);
                        assertThat("classification not changed", reviewEntryBeforeUpdate.getClassification(), Matchers.not(equalTo(reviewEntryAfterUpdate.getClassification())));
                        break;
                    case "description":
                        Validator.reviewEntriesAreSameWithoutComparingDescription(reviewEntryBeforeUpdate, reviewEntryAfterUpdate);
                        assertThat("description not changed", reviewEntryBeforeUpdate.getDescription(), Matchers.not(equalTo(reviewEntryAfterUpdate.getDescription())));
                        break;
                    default:
                }
                break;
        }
    }

    @ParameterizedTest(name = "{0} user has correct case permissions on review entry in status {2} when review in status {1}")
    @MethodSource("com.example.mirai.projectname.reviewservice.fixtures.CasePermissionParametersFactory#userHasCorrectCasePermissionOnReviewEntryInStatus")
    void userHasCorrectCasePermissionOnReviewInSpecifiedStatusAndReviewEntryInStatus(String user, ReviewStatus reviewStatus, ReviewEntryStatus reviewEntryStatus, String expectedContent) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long reviewId = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewStatus);
        assert reviewId != null;

        Long reviewTaskId = entityInstanceManager.createReviewTaskAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewTaskStatus.ACCEPTED, reviewId);
        assert reviewTaskId != null;

        Long reviewEntryId = entityInstanceManager.createReviewEntryAndSetStatus(dataIdentifier, "ALL_PROPERTIES", reviewId, reviewTaskId, reviewEntryStatus);
        assert reviewEntryId != null;

        String path = PathGenerator.getReviewEntryCasePermissionsPath(reviewEntryId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(get(path))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("CONTENT: " + content);
        JSONAssert.assertEquals("case permissions are not as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    /*@Test
    public void createReviewEntryShouldThrowReviewTaskNotExistExceptionWhenReviewTaskNotFound() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long reviewId = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewStatus.OPENED);
        assert reviewId!=null;
        String path = PathGenerator.getReviewEntryCreationPath(reviewId);

        ReviewEntry requestReviewEntry = EntityPojoFactory.createReviewEntry(dataIdentifier, "ALL_PROPERTIES_EXCEPT_REVIEW_TASK");
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier,"cug-projectname-change-specialist-2");

        MvcResult result = getMockMvc().perform(post(path)
                .with(jwt().jwt(jwt))
                .content(ObjectMapperUtil.getObjectMapper().writeValueAsString(requestReviewEntry))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ExceptionResponse exceptionResponse = new ExceptionResponse(content);
        ExceptionValidator.exceptionResponseIsReviewTaskNotExistException(exceptionResponse, path);
    }*/
}
