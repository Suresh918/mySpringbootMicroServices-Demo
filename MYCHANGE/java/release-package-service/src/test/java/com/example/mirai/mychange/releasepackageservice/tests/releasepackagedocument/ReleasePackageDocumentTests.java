package com.example.mirai.projectname.releasepackageservice.tests.releasepackagedocument;

import com.example.mirai.libraries.document.model.Document;
import com.example.mirai.libraries.document.model.DocumentCaseActions;
import com.example.mirai.libraries.document.model.DocumentStatus;
import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.projectname.releasepackageservice.BaseTest;
import com.example.mirai.projectname.releasepackageservice.document.model.ReleasePackageDocument;
import com.example.mirai.projectname.releasepackageservice.fixtures.EntityPojoFactory;
import com.example.mirai.projectname.releasepackageservice.fixtures.JsonNodeFactory;
import com.example.mirai.projectname.releasepackageservice.fixtures.JwtFactory;
import com.example.mirai.projectname.releasepackageservice.json.ReleasePackageDocumentJson;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageStatus;
import com.example.mirai.projectname.releasepackageservice.utils.PathGenerator;
import com.example.mirai.projectname.releasepackageservice.utils.ReleasePackageDocument.Validator;
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

public class ReleasePackageDocumentTests extends BaseTest {

    @ParameterizedTest(name = "{0} user is to create release package document with document in {1} status,case action {2} performed, expect to be {3},ReleasePackage in status{4}")
    @CsvFileSource(resources = "/parameters/releasepackage/document/DocumentCreate.csv", numLinesToSkip = 1)
    void userToCreateReleasePackageDocument(String user, DocumentStatus documentStatus, DocumentCaseActions documentCaseActions,
                                            String expectedResult, ReleasePackageStatus releasePackageStatus) throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", releasePackageStatus);
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));
        String path = PathGenerator.getReleasePackageDocumentCreationPath("release-packages", releasePackageId);
        Document document = null;
        switch (expectedResult) {
            case "description":
                document = EntityPojoFactory.createReleasePackageDocument(dataIdentifier, "ALL_PROPERTIES");
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
                ReleasePackageDocumentJson responseReleasePackageDocument = new ReleasePackageDocumentJson(content);
                Long id = responseReleasePackageDocument.getId();
                Optional<ReleasePackageDocument> optionalReleasePackageDocument = releasePackageDocumentRepository.findById(id);
                assertThat(optionalReleasePackageDocument.isPresent(), equalTo(true));
                ReleasePackageDocument savedReleasePackageDocument = optionalReleasePackageDocument.get();
                Validator.createReleasePackageDocumentIsSuccessful(document, savedReleasePackageDocument, responseReleasePackageDocument);
                break;
        }
    }

    @ParameterizedTest(name = "{0} user perform {2} case action on document in status {1}, expect to be {3} with release package in status {4}")
    @CsvFileSource(resources = "/parameters/releasepackage/document/DocumentDelete.csv", numLinesToSkip = 1)
    void userToPerformDeleteOnReleasePackageDocumentWhenDocumentInStatus(String user, DocumentStatus documentStatus,
                                                                         String caseAction,
                                                                         String expectedResult,
                                                                         ReleasePackageStatus releasePackageStatus) throws Exception {
        UUID uuid = UUID.randomUUID();


        String dataIdentifier = uuid.toString();
        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", releasePackageStatus);
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));

        String path = PathGenerator.getReleasePackageDocumentCreationPath("release-packages", releasePackageId);
        Long releasePackageDocumentId = null;
        switch (caseAction) {
            case "DELETE":
                releasePackageDocumentId = entityInstanceManager.createReleasePackageDocumentAndSetStatus(dataIdentifier, "ALL_PROPERTIES", releasePackageId, documentStatus);
                break;
            default:
                break;

        }
        assert releasePackageDocumentId != null;

        Optional<ReleasePackageDocument> optionalReleasePackageDocument = releasePackageDocumentRepository.findById(releasePackageDocumentId);
        assertThat(optionalReleasePackageDocument.isPresent(), equalTo(true));
        ReleasePackageDocument releasePackageDocumentBeforeCaseAction = optionalReleasePackageDocument.get();

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        String deletePath = PathGenerator.getChildEntityUpdatePath("documents", releasePackageDocumentId);
        MvcResult result = getMockMvc().perform(delete(deletePath)
                .contentType(MediaType.APPLICATION_JSON)).andReturn();

        switch (expectedResult) {

            case "AUTHORIZED":
                optionalReleasePackageDocument = releasePackageDocumentRepository.findById(releasePackageDocumentId);
                assertThat(optionalReleasePackageDocument.isPresent(), equalTo(false));
                break;
            default:
                break;
        }

    }

    @ParameterizedTest(name = "{0} user is to update property {1} on release package document in status {2}, expect to be {3} and release package status in {4}")
    @CsvFileSource(resources = "/parameters/releasepackage/document/DocumentPropertyUpdate.csv", numLinesToSkip = 1)
    void userToPerformUpdateOnReleasePackageDocumentWhenDocumentInStatus(String user, String property, DocumentStatus originalDocumentStatus,
                                                                         String expectedResult, ReleasePackageStatus releasePackageStatus) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", releasePackageStatus);
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));

        Long releasePackageDocumentId = entityInstanceManager.createReleasePackageDocumentAndSetStatus(dataIdentifier, "ALL_PROPERTIES", releasePackageId, originalDocumentStatus);
        Optional<ReleasePackageDocument> optionalReleasePackageDocument = releasePackageDocumentRepository.findById(releasePackageDocumentId);
        assertThat(optionalReleasePackageDocument.isPresent(), equalTo(true));

        assert releasePackageDocumentId != null;

        String path = PathGenerator.getChildEntityUpdatePath("documents", releasePackageDocumentId);
        ReleasePackageDocument releasePackageDocumentBeforeCaseAction = optionalReleasePackageDocument.get();
        JsonNode updateRequest = null;
        switch (property) {
            case "description":
                String descriptionValue = releasePackageDocumentBeforeCaseAction.getDescription();
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

        optionalReleasePackageDocument = releasePackageDocumentRepository.findById(releasePackageDocumentId);
        assertThat(optionalReleasePackageDocument.isPresent(), equalTo(true));
        ReleasePackageDocument releasePackageDocumentAfterCaseAction = optionalReleasePackageDocument.get();

        switch (expectedResult) {

            case "AUTHORIZED":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
                ReleasePackageDocumentJson releasePackageDocumentJson = new ReleasePackageDocumentJson(content);

                switch (property) {
                    case "description":
                        com.example.mirai.projectname.releasepackageservice.utils.ReleasePackageDocument.Validator.releasePackagesDocumentAreSameWithoutComparingDescription(releasePackageDocumentBeforeCaseAction, releasePackageDocumentAfterCaseAction);
                        assertThat("Description not changed", releasePackageDocumentBeforeCaseAction.getDescription(), Matchers.not(equalTo(releasePackageDocumentAfterCaseAction.getDescription())));
                        break;

                }
        }

    }

}
