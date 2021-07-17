package com.example.mirai.projectname.releasepackageservice.tests.releasepackage;

import com.example.mirai.libraries.teamcenter.ecn.model.Ecn;
import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.projectname.releasepackageservice.BaseTest;
import com.example.mirai.projectname.releasepackageservice.fixtures.JwtFactory;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageCaseActions;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageContext;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageStatus;
import com.example.mirai.projectname.releasepackageservice.utils.PathGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

public class TeamcenterIT extends BaseTest {

    @ParameterizedTest(name = "User {0}, On performing Create case action on RP, Teamcenter throws exception with error code {1} and status code {2}")
    @CsvFileSource(resources = "/parameters/releasepackage/linkedentities/Teamcenter.csv", numLinesToSkip = 1)
    void engineeringChangeNoticeCreationThrowsExceptionWhenReleasePackageIsCreated(String user, String exceptionCode, int statusCode) throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        ReleasePackageStatus releasePackageStatus = ReleasePackageStatus.DRAFTED;
        String reviewStatus = "4";
        ReleasePackageCaseActions releasePackageCaseAction = ReleasePackageCaseActions.CREATE;

        Long releasePackageId = null;

        releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", releasePackageStatus);

        entityInstanceManager.updateReleasePackageContextAndSetStatus(releasePackageId, reviewStatus, "REVIEW");

        assert releasePackageId != null;
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));
        List<ReleasePackageContext> releasePackageContextList = optionalReleasePackage.get().getContexts();

        //creating Engineering change notice request for team center
        Ecn engineeringChangeNotice = new Ecn();
        String ecnId = releasePackageContextList.stream().filter(releasePackageContext -> releasePackageContext.getType().toUpperCase().equals("ECN")).findFirst().get().getContextId();
        engineeringChangeNotice.setId(ecnId);
        engineeringChangeNotice.setReleasePackageStatus(ReleasePackageStatus.CREATED.getStatusLabel());
        engineeringChangeNotice.setTitle(optionalReleasePackage.get().getTitle());
        engineeringChangeNotice.setSapChangeControl(optionalReleasePackage.get().getSapChangeControl());
        engineeringChangeNotice.setValidFrom(optionalReleasePackage.get().getPlannedEffectiveDate());
        //team center checks
        teamcenterMockServer.mockTeamcenterThrowsExceptionOnEcnCreation(engineeringChangeNotice, exceptionCode, statusCode);

        String path = PathGenerator.getReleasePackageCaseActionPath(releasePackageId, releasePackageCaseAction);

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(patch(path))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        //read the content and compare with expected content in file
        InputStream inputStream = TeamcenterIT.class.getResourceAsStream("/expectations/releasepackage/linkedentities/teamcenter/errorcodes/" + exceptionCode + ".txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String exceptionExpectedContent = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
        exceptionExpectedContent = exceptionExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier);
        String response = result.getResponse().getContentAsString();
        String timestamp = new ObjectMapper().readValue(response, JsonNode.class).get("timestamp").toString();
        exceptionExpectedContent = exceptionExpectedContent.replace("\"<TIME-STAMP>\"", timestamp);
        exceptionExpectedContent = exceptionExpectedContent.replace("<RELEASE-PACKAGE-ID>", releasePackageId.toString());
        exceptionExpectedContent = exceptionExpectedContent.replace("<CASE-ACTION>", releasePackageCaseAction.name());
        assertThat(response, equalTo(exceptionExpectedContent));

    }


    @ParameterizedTest(name = " user {0} is to perform case action on release package, Teamcenter throws exception with error code {1} and status code {2}")
    @CsvFileSource(resources = "/parameters/releasepackage/linkedentities/Teamcenter.csv", numLinesToSkip = 1)
    void userToPerformCaseActionOnReleasePackageWhenReleasePackageInStatus(String user, String exceptionCode, int statusCode) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        ReleasePackageStatus releasePackageStatus = ReleasePackageStatus.CREATED;
        ReleasePackageCaseActions releasePackageCaseAction = ReleasePackageCaseActions.READY;
        String reviewStatus = "4";

        Long releasePackageId = null;

        releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", releasePackageStatus);

        entityInstanceManager.updateReleasePackageContextAndSetStatus(releasePackageId, reviewStatus, "REVIEW");

        assert releasePackageId != null;
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));

        //team center checks
        ReleasePackage releasePackage = optionalReleasePackage.get();
        Ecn engineeringChangeNotice = new Ecn();
        String ecnId = releasePackage.getContexts().stream().filter(releasePackageContext -> releasePackageContext.getType().toUpperCase().equals("ECN")).findFirst().get().getContextId();
        String teamcenterId =  getContextIdByType(optionalReleasePackage.get(), "TEAMCENTER");
        engineeringChangeNotice.setId(ecnId);
        engineeringChangeNotice.setTeamcenterId(teamcenterId);
        engineeringChangeNotice.setReleasePackageStatus(ReleasePackageStatus.READY_FOR_RELEASE.getStatusLabel());
        engineeringChangeNotice.setTitle(releasePackage.getTitle());
        engineeringChangeNotice.setSapChangeControl(releasePackage.getSapChangeControl());
        engineeringChangeNotice.setValidFrom(releasePackage.getPlannedEffectiveDate());

        teamcenterMockServer.mockTeamcenterThrowsExceptionOnUpdate(engineeringChangeNotice, exceptionCode, statusCode);

        ReleasePackage releasePackageBeforeCaseAction = optionalReleasePackage.get();
        String path = PathGenerator.getReleasePackageCaseActionPath(releasePackageId, releasePackageCaseAction);

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(patch(path))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        //read the content and compare with expected content in file
        InputStream inputStream = TeamcenterIT.class.getResourceAsStream("/expectations/releasepackage/linkedentities/teamcenter/errorcodes/" + exceptionCode + ".txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String exceptionExpectedContent = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
        exceptionExpectedContent = exceptionExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier);
        String response = result.getResponse().getContentAsString();
        String timestamp = new ObjectMapper().readValue(response, JsonNode.class).get("timestamp").toString();
        exceptionExpectedContent = exceptionExpectedContent.replace("\"<TIME-STAMP>\"", timestamp);
        exceptionExpectedContent = exceptionExpectedContent.replace("<RELEASE-PACKAGE-ID>", releasePackageId.toString());
        exceptionExpectedContent = exceptionExpectedContent.replace("<CASE-ACTION>", releasePackageCaseAction.name());

        assertThat(response, equalTo(exceptionExpectedContent));
    }

    private String getContextIdByType(ReleasePackage releasePackage, String type) {
        return releasePackage.getContexts().stream().filter(context -> context.getType().equals(type)).findFirst().get().getContextId();
    }
}
