package com.example.mirai.projectname.releasepackageservice.tests.releasepackagecomment;

import com.example.mirai.libraries.comment.model.Comment;
import com.example.mirai.libraries.comment.model.CommentCaseActions;
import com.example.mirai.libraries.comment.model.CommentStatus;
import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.projectname.releasepackageservice.BaseTest;
import com.example.mirai.projectname.releasepackageservice.comment.model.ReleasePackageComment;
import com.example.mirai.projectname.releasepackageservice.fixtures.EntityPojoFactory;
import com.example.mirai.projectname.releasepackageservice.fixtures.JsonNodeFactory;
import com.example.mirai.projectname.releasepackageservice.fixtures.JwtFactory;
import com.example.mirai.projectname.releasepackageservice.json.ExceptionResponse;
import com.example.mirai.projectname.releasepackageservice.json.ReleasePackageCommentJson;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageStatus;
import com.example.mirai.projectname.releasepackageservice.utils.PathGenerator;
import com.example.mirai.projectname.releasepackageservice.utils.Validator;
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

public class ReleasePackageCommentTests extends BaseTest {

    @ParameterizedTest(name = "{0} user is to perform {2} case action on release package comment in status {1}, expect to be {3} input {4}")
    @CsvFileSource(resources = "/parameters/comment/CommentCaseAction.csv", numLinesToSkip = 1)
    void userToPerformCaseActionOnReleasePackageCommentWhenCommentInStatus(String user, CommentStatus originalCommentStatus,
                                                                           CommentCaseActions commentCaseActions, String expectedResult,
                                                                           String jsonNode) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReleasePackageStatus.DRAFTED);
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));

        Long releasePackageCommentId = entityInstanceManager.createReleasePackageCommentAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalCommentStatus, releasePackageId);
        Optional<ReleasePackageComment> optionalReleasePackageComment = releasePackageCommentRepository.findById(releasePackageCommentId);
        assertThat(optionalReleasePackageComment.isPresent(), equalTo(true));
        assert releasePackageCommentId != null;

        ReleasePackageComment releasePackageCommentBeforeCaseAction = optionalReleasePackageComment.get();
        String path = PathGenerator.getReleasePackageCommentCaseActionPath(releasePackageCommentId, commentCaseActions);

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodeInput = objectMapper.readTree(jsonNode);
        MvcResult result = getMockMvc().perform(put(path)
                .content(String.valueOf(jsonNodeInput))
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        optionalReleasePackageComment = releasePackageCommentRepository.findById(releasePackageCommentId);
        assertThat(optionalReleasePackageComment.isPresent(), equalTo(true));
        ReleasePackageComment releasePackageCommentAfterCaseAction = optionalReleasePackageComment.get();

        switch (expectedResult) {
            case "AUTHORIZED":

                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
                CommentStatus commentStatus = CommentStatus.valueOf(expectedResult);
                Validator.releasePackageCommentsAreSameWithoutComparingAuditAndStatus(releasePackageCommentBeforeCaseAction, releasePackageCommentAfterCaseAction);
                assertThat("status after case action is not as expected", releasePackageCommentAfterCaseAction.getStatus(), equalTo(commentStatus.getStatusCode()));
                break;

        }
    }

    @ParameterizedTest(name = "{0} user is to update property {1} on release package comment in status {2}, expect to be {3}")
    @CsvFileSource(resources = "/parameters/comment/CommentPropertyUpdate.csv", numLinesToSkip = 1)
    void userToPerformUpdateOnReleasePackageCommentWhenCommentInStatus(String user, String property, CommentStatus originalCommentStatus,
                                                                       String expectedResult
    ) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReleasePackageStatus.CREATED);
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));

        Long releasePackageCommentId = entityInstanceManager.createReleasePackageCommentAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalCommentStatus, releasePackageId);
        Optional<ReleasePackageComment> optionalReleasePackageComment = releasePackageCommentRepository.findById(releasePackageCommentId);
        assertThat(optionalReleasePackageComment.isPresent(), equalTo(true));
        assert releasePackageCommentId != null;
        String path = PathGenerator.getChildEntityUpdatePath("comments", releasePackageCommentId);
        ReleasePackageComment releasePackageCommentBeforeCaseAction = optionalReleasePackageComment.get();
        JsonNode updateRequest = null;
        switch (property) {
            case "comment_text":
                String commentValue = releasePackageCommentBeforeCaseAction.getCommentText();
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

        optionalReleasePackageComment = releasePackageCommentRepository.findById(releasePackageCommentId);
        assertThat(optionalReleasePackageComment.isPresent(), equalTo(true));
        ReleasePackageComment releasePackageCommentAfterCaseAction = optionalReleasePackageComment.get();

        switch (expectedResult) {

            case "AUTHORIZED":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
                switch (property) {
                    case "comment_text":
                        Validator.releasePackagesCommentAreSameWithoutComparingCommentText(releasePackageCommentBeforeCaseAction, releasePackageCommentAfterCaseAction);
                        assertThat("comment Text not changed", releasePackageCommentBeforeCaseAction.getCommentText(), Matchers.not(equalTo(releasePackageCommentAfterCaseAction.getCommentText())));
                        break;

                }
        }

    }

    @ParameterizedTest(name = "{0} user perform {3} case action on comment in status {1} with missing property {2}, expect to be {4}")
    @CsvFileSource(resources = "/parameters/comment/CommentDelete.csv", numLinesToSkip = 1)
    void userToPerformDeleteOnReleasePackageCommentWhenCommentInStatus(String user, CommentStatus commentStatus,
                                                                       String missingProperty,
                                                                       CommentCaseActions commentCaseAction, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReleasePackageStatus.DRAFTED);
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));

        Long releasePackageCommentId = null;

        switch (missingProperty) {
            case "comment_text":
                releasePackageCommentId = entityInstanceManager.createReleasePackageCommentAndSetStatus(dataIdentifier, "ALL_PROPERTIES", commentStatus, releasePackageId);
                break;
            default:

                break;
        }
        assert releasePackageCommentId != null;

        Optional<ReleasePackageComment> optionalReleasePackageComment = releasePackageCommentRepository.findById(releasePackageCommentId);
        assertThat(optionalReleasePackageComment.isPresent(), equalTo(true));
        ReleasePackageComment releasePackageCommentBeforeCaseAction = optionalReleasePackageComment.get();

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));


        String path = PathGenerator.getChildEntityUpdatePath("comments", releasePackageCommentId);

        MvcResult result = getMockMvc().perform(delete(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        optionalReleasePackageComment = releasePackageCommentRepository.findById(releasePackageCommentId);
        switch (expectedResult) {
            case "UNAUTHORIZED":
                ReleasePackageComment commentAfterCaseAction = optionalReleasePackageComment.get();
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.NOT_EXTENDED.value()));
                assertThat("commentText are not same", releasePackageCommentBeforeCaseAction.getCommentText(), equalTo(commentAfterCaseAction.getCommentText()));
                assertThat("status are not same", releasePackageCommentBeforeCaseAction.getStatus(), equalTo(commentAfterCaseAction.getStatus()));
                break;
            case "AUTHORIZED":
                optionalReleasePackageComment = releasePackageCommentRepository.findById(releasePackageCommentId);
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
                assertThat(optionalReleasePackageComment.isPresent(), equalTo(false));
                break;
            default:
                break;
        }

    }

    @ParameterizedTest(name = "{0} user is to create release package comment with comment in {1} status,case action {2} performed, expect to be {3},input {4}")
    @CsvFileSource(resources = "/parameters/comment/CommentCreate.csv", numLinesToSkip = 1)
    void userToCreateCommentReply(String user, CommentStatus commentStatus, CommentCaseActions commentCaseActions,
                                  String expectedResult, String jsonNode) throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReleasePackageStatus.DRAFTED);
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));

        Long releasePackageCommentId = entityInstanceManager.createReleasePackageCommentAndSetStatus(dataIdentifier, "ALL_PROPERTIES", commentStatus, releasePackageId);
        Optional<ReleasePackageComment> optionalReleasePackageComment = releasePackageCommentRepository.findById(releasePackageCommentId);
        assertThat(optionalReleasePackageComment.isPresent(), equalTo(true));
        assert releasePackageCommentId != null;

        String path = PathGenerator.getEntityCreationPath("comments", releasePackageCommentId);

        Comment comment = null;
        switch (expectedResult) {
            case "comment_text":
                comment = EntityPojoFactory.createReleasePackageComment(dataIdentifier, "ALL_PROPERTIES");
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

        ExceptionResponse exceptionResponse = null;
        switch (expectedResult.toUpperCase()) {
            case "AUTHORIZED":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.CREATED.value()));
                ReleasePackageCommentJson responseReleasePackageComment = new ReleasePackageCommentJson(content);
                Long id = responseReleasePackageComment.getId();
                Optional<ReleasePackageComment> optionalReleasePackageCommentReply = releasePackageCommentRepository.findById(id);

                assertThat(optionalReleasePackageCommentReply.isPresent(), equalTo(true));
                ReleasePackageComment savedCommentReply = optionalReleasePackageCommentReply.get();
                Validator.createCommentIsSuccessful(comment, savedCommentReply, responseReleasePackageComment);
                break;
        }
    }


}
