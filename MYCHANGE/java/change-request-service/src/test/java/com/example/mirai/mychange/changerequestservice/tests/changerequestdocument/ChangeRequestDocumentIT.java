package com.example.mirai.projectname.changerequestservice.tests.changerequestdocument;

import com.example.mirai.libraries.document.model.Document;
import com.example.mirai.libraries.document.model.DocumentCaseActions;
import com.example.mirai.libraries.document.model.DocumentStatus;
import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestStatus;
import com.example.mirai.projectname.changerequestservice.document.model.ChangeRequestDocument;
import com.example.mirai.projectname.changerequestservice.fixtures.EntityPojoFactory;
import com.example.mirai.projectname.changerequestservice.fixtures.JsonNodeFactory;
import com.example.mirai.projectname.changerequestservice.fixtures.JwtFactory;
import com.example.mirai.projectname.changerequestservice.json.ChangeRequestDocumentJson;
import com.example.mirai.projectname.changerequestservice.tests.BaseTest;
import com.example.mirai.projectname.changerequestservice.utils.ChangeRequestDocument.Validator;
import com.example.mirai.projectname.changerequestservice.utils.PathGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.http.HttpStatus;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

public class ChangeRequestDocumentIT extends BaseTest {

    @ParameterizedTest(name = "{0} user is to create change request document with document in {1} status,case action {2} performed, expect to be {3},Change Request in status{4}")
    @CsvFileSource(resources = "/parameters/document/DocumentCreate.csv", numLinesToSkip = 1)
    void userToCreateChangeRequestDocument(String user, DocumentStatus documentStatus, DocumentCaseActions documentCaseActions,
                                           String expectedResult, ChangeRequestStatus changeRequestStatus) throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String missingProperty = "NONE";
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, missingProperty, ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String path = PathGenerator.getChildEntityPathByParentEntity("change-requests", id, "documents");
        Document document = null;
        switch (expectedResult) {
            case "description":
                document = EntityPojoFactory.createChangeRequestDocument(dataIdentifier, "ALL_PROPERTIES");
                break;
            default:
                break;
        }


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
                ChangeRequestDocumentJson documentJson = new ChangeRequestDocumentJson(content);
                Long documentId = documentJson.getId();
                Optional<ChangeRequestDocument> optionalChangeRequestDocument = changeRequestDocumentRepository.findById(documentId);
                assertThat(optionalChangeRequestDocument.isPresent(), equalTo(true));
                ChangeRequestDocument savedChangeRequestDocument = optionalChangeRequestDocument.get();
                Validator.createChangeRequestDocumentIsSuccessful(document, savedChangeRequestDocument, documentJson);
                break;
        }
    }

    @ParameterizedTest(name = "{0} user perform {2} case action on document in status {1}, expect to be {3} with change request in status {4}")
    @CsvFileSource(resources = "/parameters/document/DocumentDelete.csv", numLinesToSkip = 1)
    void userToPerformDeleteOnChangeRequestDocumentWhenDocumentInStatus(String user, DocumentStatus documentStatus,
                                                                        String caseAction,
                                                                        String expectedResult,
                                                                        ChangeRequestStatus changeRequestStatus) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String missingProperty = "NONE";
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, missingProperty, ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String path = PathGenerator.getChildEntityPathByParentEntity("change-requests", id, "documents");
        Long changeRequestDocumentId = null;
        switch (caseAction) {
            case "DELETE":
                changeRequestDocumentId = entityInstanceManager.createChangeRequestDocumentAndSetStatus(dataIdentifier, "ALL_PROPERTIES", id, documentStatus);
                break;
            default:
                break;

        }
        assert changeRequestDocumentId != null;

        Optional<ChangeRequestDocument> optionalChangeRequestDocument = changeRequestDocumentRepository.findById(changeRequestDocumentId);
        assertThat(optionalChangeRequestDocument.isPresent(), equalTo(true));
        //ChangeRequestDocument changeRequestDocumentBeforeCaseAction = optionalChangeRequestDocument.get();

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        String deletePath = PathGenerator.getChildEntityUpdatePath("documents", changeRequestDocumentId);
        MvcResult result = getMockMvc().perform(delete(deletePath)
                .contentType(MediaType.APPLICATION_JSON)).andReturn();

        switch (expectedResult) {

            case "AUTHORIZED":
                optionalChangeRequestDocument = changeRequestDocumentRepository.findById(changeRequestDocumentId);
                assertThat(optionalChangeRequestDocument.isPresent(), equalTo(false));
                break;
            default:
                break;
        }

    }

    @ParameterizedTest(name = "{0} user is to update property {1} on change request document in status {2}, expect to be {3} and change request status in {4}")
    @CsvFileSource(resources = "/parameters/document/DocumentPropertyUpdate.csv", numLinesToSkip = 1)
    void userToPerformUpdateOnChangeRequestDocumentWhenDocumentInStatus(String user, String property, DocumentStatus originalDocumentStatus,
                                                                        String expectedResult, ChangeRequestStatus changeRequestStatus) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String missingProperty = "NONE";
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, missingProperty, ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));

        Long changeRequestDocumentId = entityInstanceManager.createChangeRequestDocumentAndSetStatus(dataIdentifier, "ALL_PROPERTIES", id, originalDocumentStatus);
        Optional<ChangeRequestDocument> optionalChangeRequestDocument = changeRequestDocumentRepository.findById(changeRequestDocumentId);
        assertThat(optionalChangeRequestDocument.isPresent(), equalTo(true));

        assert changeRequestDocumentId != null;

        String path = PathGenerator.getChildEntityUpdatePath("documents", changeRequestDocumentId);
        ChangeRequestDocument changeRequestDocumentBeforeCaseAction = optionalChangeRequestDocument.get();
        JsonNode updateRequest = null;
        switch (property) {
            case "description":
                String descriptionValue = changeRequestDocumentBeforeCaseAction.getDescription();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, descriptionValue);
                break;
            default:
        }
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(patch(path)
                .content(String.valueOf(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        optionalChangeRequestDocument = changeRequestDocumentRepository.findById(changeRequestDocumentId);
        assertThat(optionalChangeRequestDocument.isPresent(), equalTo(true));
        ChangeRequestDocument changeRequestDocumentAfterCaseAction = optionalChangeRequestDocument.get();

        switch (expectedResult) {

            case "AUTHORIZED":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
                ChangeRequestDocumentJson changeRequestDocumentJson = new ChangeRequestDocumentJson(content);

                switch (property) {
                    case "description":
                        com.example.mirai.projectname.changerequestservice.utils.ChangeRequestDocument.Validator.changeRequestsDocumentAreSameWithoutComparingDescription(changeRequestDocumentBeforeCaseAction, changeRequestDocumentAfterCaseAction);
                        assertThat("Description not changed", changeRequestDocumentBeforeCaseAction.getDescription(), Matchers.not(equalTo(changeRequestDocumentAfterCaseAction.getDescription())));
                        break;

                }

        }

    }

}
