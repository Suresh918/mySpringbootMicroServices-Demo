package com.example.mirai.projectname.releasepackageservice.tests.releasepackagecommentdocument;

import com.example.mirai.libraries.comment.model.CommentStatus;
import com.example.mirai.libraries.document.model.Document;
import com.example.mirai.libraries.document.model.DocumentCaseActions;
import com.example.mirai.libraries.document.model.DocumentStatus;
import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.projectname.releasepackageservice.BaseTest;
import com.example.mirai.projectname.releasepackageservice.comment.model.ReleasePackageComment;
import com.example.mirai.projectname.releasepackageservice.document.model.ReleasePackageCommentDocument;
import com.example.mirai.projectname.releasepackageservice.fixtures.EntityPojoFactory;
import com.example.mirai.projectname.releasepackageservice.fixtures.JwtFactory;
import com.example.mirai.projectname.releasepackageservice.json.ReleasePackageCommentDocumentJson;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageStatus;
import com.example.mirai.projectname.releasepackageservice.utils.PathGenerator;
import com.example.mirai.projectname.releasepackageservice.utils.Validator;
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

public class ReleasePackageCommentDocumentTests extends BaseTest {

    @ParameterizedTest(name = "{0} user is to create release package comment document with comment in {1} status,case action {2} performed, expect to be {3}")
    @CsvFileSource(resources = "/parameters/releasepackage/commentdocument/CommentDocumentCreate.csv", numLinesToSkip = 1)
    void userToCreateCommentDocument(String user, CommentStatus commentStatus, DocumentCaseActions documentCaseActions,
                                     String expectedResult) throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReleasePackageStatus.DRAFTED);
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));

        Long releasePackageCommentId = entityInstanceManager.createReleasePackageCommentAndSetStatus(dataIdentifier, "ALL_PROPERTIES", commentStatus, releasePackageId);
        Optional<ReleasePackageComment> optionalReleasePackageComment = releasePackageCommentRepository.findById(releasePackageCommentId);
        assertThat(optionalReleasePackageComment.isPresent(), equalTo(true));
        assert releasePackageCommentId != null;


        Document document = null;
        switch (expectedResult) {
            case "tags":
                document = EntityPojoFactory.createReleasePackageCommentDocument(dataIdentifier, "ALL_PROPERTIES");
                break;
            default:

                break;
        }


        String path = PathGenerator.getCommentDocumentCreationPath("comments", releasePackageCommentId);

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

                ReleasePackageCommentDocumentJson responseReleasePackageCommentDocument = new ReleasePackageCommentDocumentJson(content);
                Long id = responseReleasePackageCommentDocument.getId();
                Optional<ReleasePackageCommentDocument> optionalReleasePackageCommentDocument = releasePackageCommentDocumentRepository.findById(id);

                assertThat(optionalReleasePackageCommentDocument.isPresent(), equalTo(true));
                ReleasePackageCommentDocument savedCommentDocument = optionalReleasePackageCommentDocument.get();
                Validator.createCommentDocumentIsSuccessful(document, savedCommentDocument, responseReleasePackageCommentDocument);
                break;
        }
    }

    @ParameterizedTest(name = "{0} user perform {3} case action on comment in status {1} with missing property {2}, expect to be {4} with document in status {5}")
    @CsvFileSource(resources = "/parameters/releasepackage/commentdocument/CommentDocumentDelete.csv", numLinesToSkip = 1)
    void userToPerformDeleteOnReleasePackageCommentDocumentWhenCommentDocumentInStatus(String user, CommentStatus commentStatus,
                                                                                       String missingProperty,
                                                                                       DocumentCaseActions documentCaseActions,
                                                                                       String expectedResult, DocumentStatus documentStatus) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReleasePackageStatus.DRAFTED);
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));

        Long releasePackageCommentId = entityInstanceManager.createReleasePackageCommentAndSetStatus(dataIdentifier, "ALL_PROPERTIES", commentStatus, releasePackageId);
        Optional<ReleasePackageComment> optionalReleasePackageComment = releasePackageCommentRepository.findById(releasePackageCommentId);
        assertThat(optionalReleasePackageComment.isPresent(), equalTo(true));
        assert releasePackageCommentId != null;


        Long commentDocumentId = null;

        switch (missingProperty) {
            case "tags":
                commentDocumentId = entityInstanceManager.createReleasePackageCommentDocumentAndSetStatus(dataIdentifier, "ALL_PROPERTIES", commentStatus, releasePackageCommentId, documentStatus);
                break;
            default:
                break;

        }
        assert commentDocumentId != null;

        Optional<ReleasePackageCommentDocument> optionalReleasePackageCommentDocument = releasePackageCommentDocumentRepository.findById(commentDocumentId);
        assertThat(optionalReleasePackageCommentDocument.isPresent(), equalTo(true));
        ReleasePackageCommentDocument commentDocumentBeforeCaseAction = optionalReleasePackageCommentDocument.get();

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        String path = PathGenerator.getCommentDocumentDeletionPath("comments", commentDocumentId);

        MvcResult result = getMockMvc().perform(delete(path)
                .contentType(MediaType.APPLICATION_JSON)).andReturn();

        switch (expectedResult) {

            case "AUTHORIZED":
                optionalReleasePackageCommentDocument = releasePackageCommentDocumentRepository.findById(commentDocumentId);
                assertThat(optionalReleasePackageCommentDocument.isPresent(), equalTo(false));

                break;
            default:
                break;
        }

    }

}
