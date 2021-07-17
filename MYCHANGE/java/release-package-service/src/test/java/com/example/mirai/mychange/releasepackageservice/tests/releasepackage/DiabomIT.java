package com.example.mirai.projectname.releasepackageservice.tests.releasepackage;

import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.projectname.releasepackageservice.BaseTest;
import com.example.mirai.projectname.releasepackageservice.fixtures.JwtFactory;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageStatus;
import com.example.mirai.projectname.releasepackageservice.utils.PathGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MvcResult;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

public class DiabomIT extends BaseTest {

    @Test
    void getDiaBomByChangeNoticeIdTest() throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long id = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReleasePackageStatus.CREATED);
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(id);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));
        String changeNoticeId = "400280";
        entityInstanceManager.updateReleasePackageContextIdByType(id, changeNoticeId, "CHANGENOTICE");
        ReleasePackage releasePackage = optionalReleasePackage.get();
        cerberusMockServer.mockCerberusSuccessfulDiaBomFetch(changeNoticeId);
        String path = PathGenerator.getDiaBomPath(releasePackage.getReleasePackageNumber());

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult mvcResult = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        //read the content and compare with expected content in file
        InputStream inputStream = DiabomIT.class.getResourceAsStream("/expectations/releasepackage/linkedentities/diabom/diabom.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String diabomExpectedContent = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
        diabomExpectedContent = diabomExpectedContent.replace("<CHANGE-NOTICE-ID>", changeNoticeId);

        String timestamp = new ObjectMapper().readValue(response, JsonNode.class).get("last_modified_on").toString();
        diabomExpectedContent = diabomExpectedContent.replace("\"<TIME-STAMP>\"", timestamp);
        assertThat(response, equalTo(diabomExpectedContent));
    }

    @ParameterizedTest(name = "user should get exception {0} when fetch the Diabom")
    @CsvFileSource(resources = "/parameters/releasepackage/linkedentities/Diabom.csv", numLinesToSkip = 1)
    public void getDiaBomExceptionsTest(String exceptionCode, int statusCode) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long id = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReleasePackageStatus.CREATED);
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(id);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));
        String changeNoticeId = "400280";
        entityInstanceManager.updateReleasePackageContextIdByType(id, changeNoticeId, "CHANGENOTICE");
        ReleasePackage releasePackage = optionalReleasePackage.get();
        cerberusMockServer.mockCerberusThrowException(changeNoticeId, exceptionCode, statusCode);
        String path = PathGenerator.getDiaBomPath(releasePackage.getReleasePackageNumber());

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult mvcResult = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        //read the content and compare with expected content in file
        InputStream inputStream = DiabomIT.class.getResourceAsStream("/expectations/releasepackage/linkedentities/diabom/errorcodes/" + exceptionCode + ".txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String exceptionExpectedContent = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
        exceptionExpectedContent = exceptionExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier);
        String response = mvcResult.getResponse().getContentAsString();
        String timestamp = new ObjectMapper().readValue(response, JsonNode.class).get("timestamp").toString();
        exceptionExpectedContent = exceptionExpectedContent.replace("\"<TIME-STAMP>\"", timestamp);

        assertThat(response, equalTo(exceptionExpectedContent));
    }
}
