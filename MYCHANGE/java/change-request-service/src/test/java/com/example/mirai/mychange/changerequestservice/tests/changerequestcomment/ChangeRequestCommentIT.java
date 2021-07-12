package com.example.mirai.projectname.changerequestservice.tests.changerequestcomment;

import com.example.mirai.libraries.comment.model.Comment;
import com.example.mirai.libraries.comment.model.CommentCaseActions;
import com.example.mirai.libraries.comment.model.CommentStatus;
import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestStatus;
import com.example.mirai.projectname.changerequestservice.comment.model.ChangeRequestComment;
import com.example.mirai.projectname.changerequestservice.fixtures.EntityPojoFactory;
import com.example.mirai.projectname.changerequestservice.fixtures.JsonNodeFactory;
import com.example.mirai.projectname.changerequestservice.fixtures.JwtFactory;
import com.example.mirai.projectname.changerequestservice.json.ChangeRequestCommentJson;
import com.example.mirai.projectname.changerequestservice.tests.BaseTest;
import com.example.mirai.projectname.changerequestservice.tests.changerequest.Validator;
import com.example.mirai.projectname.changerequestservice.utils.PathGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
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


public class ChangeRequestCommentIT extends BaseTest {

    @ParameterizedTest(name = "{0} user is to perform {2} case action on change request comment in status {1}, expect to be {3} input {4}")
    @CsvFileSource(resources = "/parameters/comment/CommentCaseAction.csv", numLinesToSkip = 1)
//TODO path change for all csv files
    void userToPerformCaseActionOnChangeRequestCommentWhenCommentInStatus(String user, CommentStatus originalCommentStatus,
                                                                          CommentCaseActions commentCaseActions, String expectedResult,
                                                                          String jsonNode) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String missingProperty = "NONE";
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, missingProperty, ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));

        Long changeRequestCommentId = entityInstanceManager.createChangeRequestCommentAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalCommentStatus, id);
        Optional<ChangeRequestComment> optionalChangeRequestComment = changeRequestCommentRepository.findById(changeRequestCommentId);
        assertThat(optionalChangeRequestComment.isPresent(), equalTo(true));
        assert changeRequestCommentId != null;

        ChangeRequestComment changeRequestCommentBeforeCaseAction = optionalChangeRequestComment.get();
        String path = PathGenerator.getChangeRequestCommentCaseActionPath(changeRequestCommentId, commentCaseActions);

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodeInput = objectMapper.readTree(jsonNode);
        MvcResult result = getMockMvc().perform(put(path)
                .content(String.valueOf(jsonNodeInput))
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        optionalChangeRequestComment = changeRequestCommentRepository.findById(changeRequestCommentId);
        assertThat(optionalChangeRequestComment.isPresent(), equalTo(true));
        ChangeRequestComment changeRequestCommentAfterCaseAction = optionalChangeRequestComment.get();

        switch (expectedResult) {
            case "AUTHORIZED":

                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
                CommentStatus commentStatus = CommentStatus.valueOf(expectedResult);
                Validator.changeRequestCommentsAreSameWithoutComparingAuditAndStatus(changeRequestCommentBeforeCaseAction, changeRequestCommentAfterCaseAction);
                assertThat("status after case action is not as expected", changeRequestCommentAfterCaseAction.getStatus(), equalTo(commentStatus.getStatusCode()));
                break;

        }
    }

    @ParameterizedTest(name = "{0} user is to update property {1} on change request comment in status {2}, expect to be {3}")
    @CsvFileSource(resources = "/parameters/comment/CommentPropertyUpdate.csv", numLinesToSkip = 1)
    void userToPerformUpdateOnChangeRequestCommentWhenCommentInStatus(String user, String property, CommentStatus originalCommentStatus,
                                                                      String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String missingProperty = "NONE";
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, missingProperty, ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));

        Long changeRequestCommentId = entityInstanceManager.createChangeRequestCommentAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalCommentStatus, id);
        Optional<ChangeRequestComment> optionalChangeRequestComment = changeRequestCommentRepository.findById(changeRequestCommentId);
        assertThat(optionalChangeRequestComment.isPresent(), equalTo(true));
        assert changeRequestCommentId != null;
        String path = PathGenerator.getChildEntityUpdatePath("comments", changeRequestCommentId);
        ChangeRequestComment changeRequestCommentBeforeCaseAction = optionalChangeRequestComment.get();
        JsonNode updateRequest = null;
        switch (property) {
            case "comment_text":
                String commentValue = changeRequestCommentBeforeCaseAction.getCommentText();
                updateRequest = JsonNodeFactory.getCommentFieldUpdateRequestForString(property, commentValue);
                break;
            default:
        }
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(put(path)
                .content(String.valueOf(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        optionalChangeRequestComment = changeRequestCommentRepository.findById(changeRequestCommentId);
        assertThat(optionalChangeRequestComment.isPresent(), equalTo(true));
        ChangeRequestComment changeRequestCommentAfterCaseAction = optionalChangeRequestComment.get();

        switch (expectedResult) {

            case "AUTHORIZED":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
                switch (property) {
                    case "comment_text":
                        Validator.changeRequestCommentsAreSameWithoutComparingCommentText(changeRequestCommentBeforeCaseAction, changeRequestCommentAfterCaseAction);
                        assertThat("comment Text not changed", changeRequestCommentBeforeCaseAction.getCommentText(), Matchers.not(equalTo(changeRequestCommentAfterCaseAction.getCommentText())));
                        break;

                }
        }

    }

    @ParameterizedTest(name = "{0} user perform {3} case action on comment in status {1} with missing property {2}, expect to be {4}")
    @CsvFileSource(resources = "/parameters/comment/CommentDelete.csv", numLinesToSkip = 1)
    void userToPerformDeleteOnChangeRequestCommentWhenCommentInStatus(String user, CommentStatus commentStatus,
                                                                      String missingProperty,
                                                                      CommentCaseActions commentCaseAction, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, missingProperty, ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));

        Long changeRequestCommentId = null;

        switch (missingProperty) {
            case "comment_text":
                changeRequestCommentId = entityInstanceManager.createChangeRequestCommentAndSetStatus(dataIdentifier, "ALL_PROPERTIES", commentStatus, id);
                break;
            default:

                break;
        }
        assert changeRequestCommentId != null;

        Optional<ChangeRequestComment> optionalChangeRequestComment = changeRequestCommentRepository.findById(changeRequestCommentId);
        assertThat(optionalChangeRequestComment.isPresent(), equalTo(true));
        ChangeRequestComment changeRequestCommentBeforeCaseAction = optionalChangeRequestComment.get();

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));


        String path = PathGenerator.getChildEntityUpdatePath("comments", changeRequestCommentId);

        MvcResult result = getMockMvc().perform(delete(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        optionalChangeRequestComment = changeRequestCommentRepository.findById(changeRequestCommentId);
        switch (expectedResult) {
            case "UNAUTHORIZED":
                ChangeRequestComment commentAfterCaseAction = optionalChangeRequestComment.get();
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.NOT_EXTENDED.value()));
                assertThat("commentText are not same", changeRequestCommentBeforeCaseAction.getCommentText(), equalTo(commentAfterCaseAction.getCommentText()));
                assertThat("status are not same", changeRequestCommentBeforeCaseAction.getStatus(), equalTo(commentAfterCaseAction.getStatus()));
                break;
            case "AUTHORIZED":
                optionalChangeRequestComment = changeRequestCommentRepository.findById(changeRequestCommentId);
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
                assertThat(optionalChangeRequestComment.isPresent(), equalTo(false));
                break;
            default:
                break;
        }

    }

    @ParameterizedTest(name = "{0} user is to create change request comment with comment in {1} status,case action {2} performed, expect to be {3},input {4}")
    @CsvFileSource(resources = "/parameters/comment/CommentCreate.csv", numLinesToSkip = 1)
    void userToCreateCommentReply(String user, CommentStatus commentStatus, CommentCaseActions commentCaseActions,
                                  String expectedResult, String jsonNode) throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String missingProperty = "NONE";
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, missingProperty, ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        Long changeRequestCommentId = entityInstanceManager.createChangeRequestCommentAndSetStatus(dataIdentifier, "ALL_PROPERTIES", commentStatus, id);
        Optional<ChangeRequestComment> optionalChangeRequestComment = changeRequestCommentRepository.findById(changeRequestCommentId);
        assertThat(optionalChangeRequestComment.isPresent(), equalTo(true));
        assert changeRequestCommentId != null;

        String path = PathGenerator.getChildEntityPathByParentEntity("comments", changeRequestCommentId, "comments");

        Comment comment = null;
        switch (expectedResult) {
            case "comment_text":
                comment = EntityPojoFactory.createChangeRequestComment(dataIdentifier, "ALL_PROPERTIES");
                break;
            default:
                break;
        }


        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodeInput = objectMapper.readTree(jsonNode);
        MvcResult result = getMockMvc().perform(post(path)
                .content(String.valueOf(jsonNodeInput))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        switch (expectedResult.toUpperCase()) {
            case "AUTHORIZED":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.CREATED.value()));
                ChangeRequestCommentJson changeRequestCommentResponse = new ChangeRequestCommentJson(content);
                Long commentId = changeRequestCommentResponse.getId();
                Optional<ChangeRequestComment> optionalChangeRequestCommentReply = changeRequestCommentRepository.findById(commentId);
                assertThat(optionalChangeRequestCommentReply.isPresent(), equalTo(true));
                ChangeRequestComment savedCommentReply = optionalChangeRequestCommentReply.get();
                Validator.createCommentIsSuccessful(comment, savedCommentReply, changeRequestCommentResponse);
                break;
        }
    }


}
