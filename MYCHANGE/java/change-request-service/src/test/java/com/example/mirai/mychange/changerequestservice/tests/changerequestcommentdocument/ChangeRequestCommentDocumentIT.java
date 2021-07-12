package com.example.mirai.projectname.changerequestservice.tests.changerequestcommentdocument;

import com.example.mirai.libraries.comment.model.CommentStatus;
import com.example.mirai.libraries.document.model.Document;
import com.example.mirai.libraries.document.model.DocumentCaseActions;
import com.example.mirai.libraries.document.model.DocumentStatus;
import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestStatus;
import com.example.mirai.projectname.changerequestservice.comment.model.ChangeRequestComment;
import com.example.mirai.projectname.changerequestservice.document.model.ChangeRequestCommentDocument;
import com.example.mirai.projectname.changerequestservice.fixtures.EntityPojoFactory;
import com.example.mirai.projectname.changerequestservice.fixtures.JwtFactory;
import com.example.mirai.projectname.changerequestservice.json.ChangeRequestCommentDocumentJson;
import com.example.mirai.projectname.changerequestservice.tests.BaseTest;
import com.example.mirai.projectname.changerequestservice.tests.changerequest.Validator;
import com.example.mirai.projectname.changerequestservice.utils.PathGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

public class ChangeRequestCommentDocumentIT extends BaseTest {

    @ParameterizedTest(name = "{0} user is to create change request comment document with comment in {1} status,case action {2} performed, expect to be {3}")
    @CsvFileSource(resources = "/parameters/commentdocument/CommentDocumentCreate.csv", numLinesToSkip = 1)
    void userToCreateCommentDocument(String user, CommentStatus commentStatus, DocumentCaseActions documentCaseActions,
                                     String expectedResult) throws Exception {

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


        Document document = null;
        switch (expectedResult) {
            case "tags":
                document = EntityPojoFactory.createChangeRequestCommentDocument(dataIdentifier, "ALL_PROPERTIES");
                break;
            default:

                break;
        }


        String path = PathGenerator.getChildEntityPathByParentEntity("comments", changeRequestCommentId, "documents");

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));


        String tags = "Notes";
        String description = "description";
        MockMultipartFile inputFile = new MockMultipartFile("file", "file.txt", MediaType.TEXT_PLAIN_VALUE, "File Content".getBytes());

        MockMvc result = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Object response = result.perform(MockMvcRequestBuilders.multipart(path)

                .file(inputFile)
                .param("description", description)
                .param("tags", tags)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        String content = response.toString();


        switch (expectedResult.toUpperCase()) {
            case "AUTHORIZED":

                ChangeRequestCommentDocumentJson responseChangeRequestCommentDocument = new ChangeRequestCommentDocumentJson(content);
                Long commentDocumentId = responseChangeRequestCommentDocument.getId();
                Optional<ChangeRequestCommentDocument> optionalChangeRequestCommentDocument = changeRequestCommentDocumentRepository.findById(commentDocumentId);

                assertThat(optionalChangeRequestCommentDocument.isPresent(), equalTo(true));
                ChangeRequestCommentDocument savedCommentDocument = optionalChangeRequestCommentDocument.get();
                Validator.createCommentDocumentIsSuccessful(document, savedCommentDocument, responseChangeRequestCommentDocument);
                break;
        }
    }

    @ParameterizedTest(name = "{0} user perform {3} case action on comment in status {1} with missing property {2}, expect to be {4} with document in status {5}")
    @CsvFileSource(resources = "/parameters/commentdocument/CommentDocumentDelete.csv", numLinesToSkip = 1)
    void userToDeleteOnChangeRequestCommentDocumentWhenCommentDocumentInStatus(String user, CommentStatus commentStatus,
                                                                                       String missingProperty,
                                                                                       DocumentCaseActions documentCaseActions,
                                                                                       String expectedResult, DocumentStatus documentStatus) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String changeRequestMissingProperty = "NONE";
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, changeRequestMissingProperty, ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        Long changeRequestCommentId = entityInstanceManager.createChangeRequestCommentAndSetStatus(dataIdentifier, "ALL_PROPERTIES", commentStatus, id);
        Optional<ChangeRequestComment> optionalChangeRequestComment = changeRequestCommentRepository.findById(changeRequestCommentId);
        assertThat(optionalChangeRequestComment.isPresent(), equalTo(true));
        assert changeRequestCommentId != null;


        Long commentDocumentId = null;

        switch (missingProperty) {
            case "tags":
                commentDocumentId = entityInstanceManager.createChangeRequestCommentDocumentAndSetStatus(dataIdentifier, "ALL_PROPERTIES", commentStatus, changeRequestCommentId, documentStatus);
                break;
            default:
                break;

        }
        assert commentDocumentId != null;

        Optional<ChangeRequestCommentDocument> optionalChangeRequestCommentDocument = changeRequestCommentDocumentRepository.findById(commentDocumentId);
        assertThat(optionalChangeRequestCommentDocument.isPresent(), equalTo(true));
        ChangeRequestCommentDocument commentDocumentBeforeCaseAction = optionalChangeRequestCommentDocument.get();

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        String path = PathGenerator.getCommentDocumentDeletionPath("comments", commentDocumentId);

        MvcResult result = getMockMvc().perform(delete(path)
                .contentType(MediaType.APPLICATION_JSON)).andReturn();

        switch (expectedResult) {

            case "AUTHORIZED":
                optionalChangeRequestCommentDocument = changeRequestCommentDocumentRepository.findById(commentDocumentId);
                assertThat(optionalChangeRequestCommentDocument.isPresent(), equalTo(false));

                break;
            default:
                break;
        }

    }

}
