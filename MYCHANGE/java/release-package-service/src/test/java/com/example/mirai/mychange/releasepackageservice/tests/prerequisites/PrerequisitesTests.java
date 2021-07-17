package com.example.mirai.projectname.releasepackageservice.tests.prerequisites;


import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.projectname.releasepackageservice.BaseTest;
import com.example.mirai.projectname.releasepackageservice.ExceptionValidator;
import com.example.mirai.projectname.releasepackageservice.fixtures.JwtFactory;
import com.example.mirai.projectname.releasepackageservice.json.ExceptionResponse;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.PrerequisiteReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageContext;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageStatus;
import com.example.mirai.projectname.releasepackageservice.shared.Constants;
import com.example.mirai.projectname.releasepackageservice.tests.myteams.MyTeamsTests;
import com.example.mirai.projectname.releasepackageservice.utils.PathGenerator;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrerequisitesTests extends BaseTest {

    private static String releasePackagePrerequisitesOverviewExpectedContent;

    private static String prerequisitesSearchSummaryExpectedContent;

    static {
        InputStream inputStream = MyTeamsTests.class.getResourceAsStream("/expectations/releasepackage/prerequisites/PrerequisitesOverview.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        releasePackagePrerequisitesOverviewExpectedContent = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    static {
        InputStream inputStream = MyTeamsTests.class.getResourceAsStream("/expectations/releasepackage/prerequisites/SearchSummary.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        prerequisitesSearchSummaryExpectedContent = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    @ParameterizedTest(name = "{0} user is {3} to add prerequisites")
    @CsvFileSource(resources = "/parameters/releasepackage/prerequisites/Prerequisites.csv", numLinesToSkip = 1)
    void userToAddPrerequisites(String user, ReleasePackageStatus originalReleasePackageStatus, String caseAction, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();//flag
        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalReleasePackageStatus);
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));
        List<PrerequisiteReleasePackage> inputPrerequisites = entityInstanceManager.getAddPrerequisiteRequest(originalReleasePackageStatus);
        String path = PathGenerator.getAddPrerequisitesPath("release-packages", releasePackageId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(post(path)
                .content(ObjectMapperUtil.getObjectMapper().writeValueAsString(inputPrerequisites))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ExceptionResponse exceptionResponse = null;
        switch (expectedResult) {
            case "UNAUTHORIZED":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.FORBIDDEN.value()));
                exceptionResponse = new ExceptionResponse(content);
                ExceptionValidator.exceptionResponseIsUnauthorizedException(exceptionResponse, path);
                break;
            case "AUTHORIZED":
                Optional<ReleasePackage> optionalReleasePackageResponse = releasePackageRepository.findById(releasePackageId);
                List<PrerequisiteReleasePackage> prerequisitesResponse = optionalReleasePackageResponse.get().getPrerequisiteReleasePackages();
                assertThat(optionalReleasePackageResponse.isPresent(), equalTo(true));
                com.example.mirai.projectname.releasepackageservice.tests.prerequisites.Validator.addPrerequisitesIsSuccessful(content, prerequisitesResponse);
                break;
        }
    }

    @ParameterizedTest(name = "{0} user is {3} to reorder prerequisites")
    @CsvFileSource(resources = "/parameters/releasepackage/prerequisites/Prerequisites.csv", numLinesToSkip = 1)
    void userToReorderPrerequisites(String user, ReleasePackageStatus originalReleasePackageStatus, String caseAction, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalReleasePackageStatus);
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));
        List<PrerequisiteReleasePackage> inputPrerequisites = entityInstanceManager.getAddPrerequisiteRequest(originalReleasePackageStatus);
        entityInstanceManager.createPrerequisites(releasePackageId, inputPrerequisites);
        Optional<ReleasePackage> optionalReleasePackagePrerequisites = releasePackageRepository.findById(releasePackageId);
        PrerequisiteReleasePackage reorderInputPrerequisites = new PrerequisiteReleasePackage();
        boolean isImpactCheckRequired = false;
        reorderInputPrerequisites.setReleasePackageId(optionalReleasePackagePrerequisites.get().getPrerequisiteReleasePackages().get(1).getReleasePackageId());
        reorderInputPrerequisites.setReleasePackageNumber(optionalReleasePackagePrerequisites.get().getPrerequisiteReleasePackages().get(1).getReleasePackageNumber());
        reorderInputPrerequisites.setSequence(Constants.NEW_SEQUENCE);
        String updatePath = PathGenerator.getUpdatePrerequisitesPath("release-packages", releasePackageId, "UPDATE_PREREQUISITE", isImpactCheckRequired);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(put(updatePath)
                .content(ObjectMapperUtil.getObjectMapper().writeValueAsString(reorderInputPrerequisites))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionResponse exceptionResponse = null;
        String exceptionPath = PathGenerator.getUnauthorisedExceptionPath("release-packages", releasePackageId);
        switch (expectedResult) {
            case "UNAUTHORIZED":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.FORBIDDEN.value()));
                exceptionResponse = new ExceptionResponse(content);
                ExceptionValidator.exceptionResponseIsUnauthorizedException(exceptionResponse, exceptionPath);
                break;
            case "AUTHORIZED":
                Optional<ReleasePackage> optionalReleasePackageResponse = releasePackageRepository.findById(releasePackageId);
                List<PrerequisiteReleasePackage> prerequisiteResponse = optionalReleasePackageResponse.get().getPrerequisiteReleasePackages();
                assertThat(optionalReleasePackageResponse.isPresent(), equalTo(true));
                com.example.mirai.projectname.releasepackageservice.tests.prerequisites.Validator.reorderPrerequisitesIsSuccessful(content, prerequisiteResponse);
                break;
        }
    }

    @ParameterizedTest(name = "{0} user is {3} to delete prerequisites")
    @CsvFileSource(resources = "/parameters/releasepackage/prerequisites/Prerequisites.csv", numLinesToSkip = 1)
    void userToDeletePrerequisites(String user, ReleasePackageStatus originalReleasePackageStatus, String caseAction, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalReleasePackageStatus);
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));
        List<PrerequisiteReleasePackage> inputPrerequisites = entityInstanceManager.getAddPrerequisiteRequest(originalReleasePackageStatus);
        entityInstanceManager.createPrerequisites(releasePackageId, inputPrerequisites);
        Optional<ReleasePackage> optionalReleasePackagePrerequisites = releasePackageRepository.findById(releasePackageId);
        PrerequisiteReleasePackage toBeRemovedPrerequisite = new PrerequisiteReleasePackage();
        boolean isImpactCheckRequired = false;
        toBeRemovedPrerequisite.setReleasePackageId(optionalReleasePackagePrerequisites.get().getPrerequisiteReleasePackages().get(1).getReleasePackageId());
        toBeRemovedPrerequisite.setReleasePackageNumber(optionalReleasePackagePrerequisites.get().getPrerequisiteReleasePackages().get(1).getReleasePackageNumber());
        String path = PathGenerator.getUpdatePrerequisitesPath("release-packages", releasePackageId, "REMOVE_PREREQUISITE", isImpactCheckRequired);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(put(path)
                .content(ObjectMapperUtil.getObjectMapper().writeValueAsString(toBeRemovedPrerequisite))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ExceptionResponse exceptionResponse = null;
        String exceptionPath = PathGenerator.getUnauthorisedExceptionPath("release-packages", releasePackageId);
        switch (expectedResult) {
            case "UNAUTHORIZED":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.FORBIDDEN.value()));
                exceptionResponse = new ExceptionResponse(content);
                ExceptionValidator.exceptionResponseIsUnauthorizedException(exceptionResponse, exceptionPath);
                break;
            case "AUTHORIZED":
                Optional<ReleasePackage> optionalReleasePackageResponse = releasePackageRepository.findById(releasePackageId);
                List<PrerequisiteReleasePackage> prerequisiteResponse = optionalReleasePackageResponse.get().getPrerequisiteReleasePackages();
                assertThat(optionalReleasePackageResponse.isPresent(), equalTo(true));
                com.example.mirai.projectname.releasepackageservice.tests.prerequisites.Validator.deletePrerequisitesIsSuccessful(content, prerequisiteResponse);
                break;
        }
    }

    @ParameterizedTest(name = "get prerequisites release package numbers in release package status {1}")
    @CsvFileSource(resources = "/parameters/releasepackage/prerequisites/FetchPrerequisites.csv", numLinesToSkip = 1)
    public void getPrerequisteReleasePackageNumbersByReleasePackageIdTest(ReleasePackageStatus originalReleasePackageStatus) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        List<String> releasePackageNumbersList = new ArrayList<>();
        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalReleasePackageStatus);
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));
        List<PrerequisiteReleasePackage> inputPrerequisites = entityInstanceManager.getAddPrerequisiteRequest(ReleasePackageStatus.DRAFTED);
        entityInstanceManager.createPrerequisites(releasePackageId, inputPrerequisites);
        Optional<ReleasePackage> optionalReleasePackagePrerequisites = releasePackageRepository.findById(releasePackageId);
        String path = PathGenerator.getPrerequisitesRelasePackageNumbersPath("release-packages", releasePackageId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Optional<ReleasePackage> optionalReleasePackageResponse = releasePackageRepository.findById(releasePackageId);
        List<PrerequisiteReleasePackage> prerequisiteResponse = optionalReleasePackageResponse.get().getPrerequisiteReleasePackages();
        assertThat(optionalReleasePackageResponse.isPresent(), equalTo(true));
        for (PrerequisiteReleasePackage prerequisiteReleasePackage : prerequisiteResponse) {
            releasePackageNumbersList.add(prerequisiteReleasePackage.getReleasePackageNumber());
        }

        assertThat("release package numbers are not same", JsonPath.parse(content).read("$[0]"), equalTo(releasePackageNumbersList.get(0)));
        assertThat("release package numbers are not same", JsonPath.parse(content).read("$[1]"), equalTo(releasePackageNumbersList.get(1)));
        assertThat("release package numbers are not same", JsonPath.parse(content).read("$[2]"), equalTo(releasePackageNumbersList.get(2)));
    }

    @ParameterizedTest(name = "get prerequisites release package numbers in release package status {1}")
    @CsvFileSource(resources = "/parameters/releasepackage/prerequisites/FetchPrerequisites.csv", numLinesToSkip = 1)
    public void getPrerequisteReleasePackageNumbersByReleasePackageNumberTest(ReleasePackageStatus originalReleasePackageStatus) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        List<String> releasePackageNumbersList = new ArrayList<>();
        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalReleasePackageStatus);
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));
        List<PrerequisiteReleasePackage> inputPrerequisites = entityInstanceManager.getAddPrerequisiteRequest(ReleasePackageStatus.DRAFTED);
        entityInstanceManager.createPrerequisites(releasePackageId, inputPrerequisites);
        Optional<ReleasePackage> optionalReleasePackagePrerequisites = releasePackageRepository.findById(releasePackageId);
        String releasePackageNumber = entityInstanceManager.getReleasePackageIdById(releasePackageId);
        entityInstanceManager.updateReleasePackageNumber(releasePackageId);
        String releasePackageNumberAfterUpdate = entityInstanceManager.getReleasePackageIdById(releasePackageId);
        String path = PathGenerator.getPathByReleasePackageNumber("release-packages", releasePackageNumberAfterUpdate);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Optional<ReleasePackage> optionalReleasePackageResponse = releasePackageRepository.findById(releasePackageId);
        List<PrerequisiteReleasePackage> prerequisiteResponse = optionalReleasePackageResponse.get().getPrerequisiteReleasePackages();
        assertThat(optionalReleasePackageResponse.isPresent(), equalTo(true));
        for (PrerequisiteReleasePackage prerequisiteReleasePackage : prerequisiteResponse) {
            releasePackageNumbersList.add(prerequisiteReleasePackage.getReleasePackageNumber());
        }
        List<String> responseList = new ArrayList<String>(Arrays.asList(content.split(",")));
        assertThat("release package numbers are not same", responseList.size(), equalTo(releasePackageNumbersList.size()));
    }

    @ParameterizedTest(name = "get prerequisites release package numbers in release package status {1}")
    @CsvFileSource(resources = "/parameters/releasepackage/prerequisites/FetchPrerequisites.csv", numLinesToSkip = 1)
    public void getPrerequisteReleasePackageNumbersByECNTest(ReleasePackageStatus originalReleasePackageStatus) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        List<String> releasePackageNumbersList = new ArrayList<>();
        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalReleasePackageStatus);
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));
        List<PrerequisiteReleasePackage> inputPrerequisites = entityInstanceManager.getAddPrerequisiteRequest(ReleasePackageStatus.DRAFTED);
        entityInstanceManager.createPrerequisites(releasePackageId, inputPrerequisites);
        Optional<ReleasePackage> optionalReleasePackagePrerequisites = releasePackageRepository.findById(releasePackageId);

        entityInstanceManager.updateECN(releasePackageId);
        String ecn = entityInstanceManager.getECNById(releasePackageId);

        String path = PathGenerator.getPathForPrerequisiteRelasePackageNumbersByECN("release-packages", ecn);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Optional<ReleasePackage> optionalReleasePackageResponse = releasePackageRepository.findById(releasePackageId);
        List<PrerequisiteReleasePackage> prerequisiteResponse = optionalReleasePackageResponse.get().getPrerequisiteReleasePackages();
        assertThat(optionalReleasePackageResponse.isPresent(), equalTo(true));
        for (PrerequisiteReleasePackage prerequisiteReleasePackage : prerequisiteResponse) {
            releasePackageNumbersList.add(prerequisiteReleasePackage.getReleasePackageNumber());
        }

        List<String> responseList = new ArrayList<String>(Arrays.asList(content.split(",")));
        assertThat("release package numbers are not same", responseList.size(), equalTo(releasePackageNumbersList.size()));
    }

    @ParameterizedTest(name = "user is able to fetch release package prerequisites overview when release package is in {1}")
    @CsvFileSource(resources = "/parameters/releasepackage/prerequisites/Overview.csv", numLinesToSkip = 1)
    void releasePackagePrerequisitesOverviewById(String user, ReleasePackageStatus originalReleasePackageStatus) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);
        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalReleasePackageStatus);
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));
        List<PrerequisiteReleasePackage> inputPrerequisites = entityInstanceManager.getAddPrerequisiteRequest(originalReleasePackageStatus);
        entityInstanceManager.createPrerequisites(releasePackageId, inputPrerequisites);
        Optional<ReleasePackage> optionalReleasePackagePrerequisites = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackagePrerequisites.isPresent(), equalTo(true));
        String path = PathGenerator.getPathForPrerequisitesOverviewById("release-packages", releasePackageId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(get(path))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        List<String> releasePackagePrerequisites = entityInstanceManager.getReleasePackagePrerequisites(releasePackageId);
        Optional<ReleasePackage> firstPrerequisiteDetails = releasePackageRepository.findById(Long.parseLong(releasePackagePrerequisites.get(0)));
        ReleasePackage releasePackageOne = firstPrerequisiteDetails.get();
        Optional<ReleasePackage> secondPrerequisiteDetails = releasePackageRepository.findById(Long.parseLong(releasePackagePrerequisites.get(1)));
        ReleasePackage releasePackageTwo = secondPrerequisiteDetails.get();
        Optional<ReleasePackage> thirdPrerequisiteDetails = releasePackageRepository.findById(Long.parseLong(releasePackagePrerequisites.get(2)));
        ReleasePackage releasePackageThree = thirdPrerequisiteDetails.get();

        String plannedEffectiveDate1 = formatter.format(releasePackageOne.getPlannedEffectiveDate());
        String plannedEffectiveDate2 = formatter.format(releasePackageTwo.getPlannedEffectiveDate());
        String plannedEffectiveDate3 = formatter.format(releasePackageThree.getPlannedEffectiveDate());
        String plannedReleaseDate1 = formatter.format(releasePackageOne.getPlannedReleaseDate());
        String plannedReleaseDate2 = formatter.format(releasePackageTwo.getPlannedReleaseDate());
        String plannedReleaseDate3 = formatter.format(releasePackageThree.getPlannedReleaseDate());
        String releasePackageNumber1 = releasePackageOne.getReleasePackageNumber();
        String releasePackageNumber2 = releasePackageTwo.getReleasePackageNumber();
        String releasePackageNumber3 = releasePackageThree.getReleasePackageNumber();
        String title1 = releasePackageOne.getTitle();
        String title2 = releasePackageTwo.getTitle();
        String title3 = releasePackageThree.getTitle();

        String expectedContent = releasePackagePrerequisitesOverviewExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier);
        expectedContent = expectedContent.replace("<ID>", releasePackageId + "");
        expectedContent = expectedContent.replace("<title1>", title1);
        expectedContent = expectedContent.replace("<title2>", title2);
        expectedContent = expectedContent.replace("<title3>", title3);
        expectedContent = expectedContent.replace("<releasePackageNumber1>", releasePackageNumber1);
        expectedContent = expectedContent.replace("<releasePackageNumber2>", releasePackageNumber2);
        expectedContent = expectedContent.replace("<releasePackageNumber3>", releasePackageNumber3);
        expectedContent = expectedContent.replace("<ID1>", releasePackagePrerequisites.get(0) + "");
        expectedContent = expectedContent.replace("<ID2>", releasePackagePrerequisites.get(1) + "");
        expectedContent = expectedContent.replace("<ID3>", releasePackagePrerequisites.get(2) + "");
        expectedContent = expectedContent.replace("<planned_release_date1>", plannedReleaseDate1.replaceAll("Z$", "+00:00"));
        expectedContent = expectedContent.replace("<planned_release_date2>", plannedReleaseDate2.replaceAll("Z$", "+00:00"));
        expectedContent = expectedContent.replace("<planned_release_date3>", plannedReleaseDate3.replaceAll("Z$", "+00:00"));
        expectedContent = expectedContent.replace("<planned_effective_date1>", plannedEffectiveDate1.replaceAll("Z$", "+00:00"));
        expectedContent = expectedContent.replace("<planned_effective_date2>", plannedEffectiveDate2.replaceAll("Z$", "+00:00"));
        expectedContent = expectedContent.replace("<planned_effective_date3>", plannedEffectiveDate3.replaceAll("Z$", "+00:00"));
        JSONAssert.assertEquals("release package prerequisites overview is not as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @ParameterizedTest(name = "user is able to fetch release package prerequisites overview when release package is in {1}")
    @CsvFileSource(resources = "/parameters/releasepackage/prerequisites/Overview.csv", numLinesToSkip = 1)
    void releasePackagePrerequisitesOverviewByNumber(String user, ReleasePackageStatus originalReleasePackageStatus) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);
        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalReleasePackageStatus);
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));
        entityInstanceManager.updateReleasePackageNumberForOverviewByNumber(releasePackageId);
        String releasePackageNumber = entityInstanceManager.getReleasePackageIdById(releasePackageId);
        List<PrerequisiteReleasePackage> inputPrerequisites = entityInstanceManager.getAddPrerequisiteRequest(originalReleasePackageStatus);
        entityInstanceManager.createPrerequisites(releasePackageId, inputPrerequisites);
        Optional<ReleasePackage> optionalReleasePackagePrerequisites = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackagePrerequisites.isPresent(), equalTo(true));
        String path = PathGenerator.getPathForPrerequisitesOverviewByNumber("release-packages",releasePackageNumber);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(get(path))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        List<String> releasePackagePrerequisites =entityInstanceManager.getReleasePackagePrerequisites(releasePackageId);
        Optional<ReleasePackage> firstPrerequisiteDetails = releasePackageRepository.findById(Long.parseLong(releasePackagePrerequisites.get(0)));
        ReleasePackage releasePackageOne = firstPrerequisiteDetails.get();
        Optional<ReleasePackage> secondPrerequisiteDetails = releasePackageRepository.findById(Long.parseLong(releasePackagePrerequisites.get(1)));
        ReleasePackage releasePackageTwo = secondPrerequisiteDetails.get();
        Optional<ReleasePackage> thirdPrerequisiteDetails = releasePackageRepository.findById(Long.parseLong(releasePackagePrerequisites.get(2)));
        ReleasePackage releasePackageThree = thirdPrerequisiteDetails.get();

        String plannedEffectiveDate1 = formatter.format(releasePackageOne.getPlannedEffectiveDate());
        String plannedEffectiveDate2 = formatter.format(releasePackageTwo.getPlannedEffectiveDate());
        String plannedEffectiveDate3 = formatter.format(releasePackageThree.getPlannedEffectiveDate());
        String plannedReleaseDate1 = formatter.format(releasePackageOne.getPlannedReleaseDate());
        String plannedReleaseDate2 = formatter.format(releasePackageTwo.getPlannedReleaseDate());
        String plannedReleaseDate3 = formatter.format(releasePackageThree.getPlannedReleaseDate());
        String releasePackageNumber1 = releasePackageOne.getReleasePackageNumber();
        String releasePackageNumber2 = releasePackageTwo.getReleasePackageNumber();
        String releasePackageNumber3 = releasePackageThree.getReleasePackageNumber();
        String title1=releasePackageOne.getTitle();
        String title2=releasePackageTwo.getTitle();
        String title3=releasePackageThree.getTitle();

        String expectedContent = releasePackagePrerequisitesOverviewExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier);
        expectedContent = expectedContent.replace("<ID>", releasePackageId + "");
        expectedContent = expectedContent.replace("<title1>", title1);
        expectedContent = expectedContent.replace("<title2>", title2);
        expectedContent = expectedContent.replace("<title3>", title3);
        expectedContent = expectedContent.replace("<releasePackageNumber1>", releasePackageNumber1);
        expectedContent = expectedContent.replace("<releasePackageNumber2>", releasePackageNumber2);
        expectedContent = expectedContent.replace("<releasePackageNumber3>", releasePackageNumber3);
        expectedContent = expectedContent.replace("<ID1>", releasePackagePrerequisites.get(0) + "");
        expectedContent = expectedContent.replace("<ID2>", releasePackagePrerequisites.get(1) + "");
        expectedContent = expectedContent.replace("<ID3>", releasePackagePrerequisites.get(2) + "");
        expectedContent = expectedContent.replace("<planned_release_date1>", plannedReleaseDate1.replaceAll("Z$", "+00:00"));
        expectedContent = expectedContent.replace("<planned_release_date2>", plannedReleaseDate2.replaceAll("Z$", "+00:00"));
        expectedContent = expectedContent.replace("<planned_release_date3>", plannedReleaseDate3.replaceAll("Z$", "+00:00"));
        expectedContent = expectedContent.replace("<planned_effective_date1>",plannedEffectiveDate1.replaceAll("Z$", "+00:00"));
        expectedContent = expectedContent.replace("<planned_effective_date2>",plannedEffectiveDate2.replaceAll("Z$", "+00:00"));
        expectedContent = expectedContent.replace("<planned_effective_date3>",plannedEffectiveDate3.replaceAll("Z$", "+00:00"));
        JSONAssert.assertEquals("release package prerequisites overview is not as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @ParameterizedTest(name = "user is able to fetch release package prerequisites overview when release package is in {1}")
    @CsvFileSource(resources = "/parameters/releasepackage/prerequisites/Overview.csv", numLinesToSkip = 1)
    void releasePackagePrerequisitesOverviewByEcn(String user, ReleasePackageStatus originalReleasePackageStatus) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);
        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalReleasePackageStatus);
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));
        entityInstanceManager.updateECNForOverviewByEcn(releasePackageId);
        String ecn = entityInstanceManager.getECNById(releasePackageId);

        List<PrerequisiteReleasePackage> inputPrerequisites = entityInstanceManager.getAddPrerequisiteRequest(originalReleasePackageStatus);
        entityInstanceManager.createPrerequisites(releasePackageId, inputPrerequisites);
        Optional<ReleasePackage> optionalReleasePackagePrerequisites = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackagePrerequisites.isPresent(), equalTo(true));
        String path = PathGenerator.getPathForPrerequisitesOverviewByEcn("release-packages",ecn);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(get(path))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        List<String> releasePackagePrerequisites =entityInstanceManager.getReleasePackagePrerequisites(releasePackageId);
        Optional<ReleasePackage> firstPrerequisiteDetails = releasePackageRepository.findById(Long.parseLong(releasePackagePrerequisites.get(0)));
        ReleasePackage releasePackageOne = firstPrerequisiteDetails.get();
        Optional<ReleasePackage> secondPrerequisiteDetails = releasePackageRepository.findById(Long.parseLong(releasePackagePrerequisites.get(1)));
        ReleasePackage releasePackageTwo = secondPrerequisiteDetails.get();
        Optional<ReleasePackage> thirdPrerequisiteDetails = releasePackageRepository.findById(Long.parseLong(releasePackagePrerequisites.get(2)));
        ReleasePackage releasePackageThree = thirdPrerequisiteDetails.get();

        String plannedEffectiveDate1 = formatter.format(releasePackageOne.getPlannedEffectiveDate());
        String plannedEffectiveDate2 = formatter.format(releasePackageTwo.getPlannedEffectiveDate());
        String plannedEffectiveDate3 = formatter.format(releasePackageThree.getPlannedEffectiveDate());
        String plannedReleaseDate1 = formatter.format(releasePackageOne.getPlannedReleaseDate());
        String plannedReleaseDate2 = formatter.format(releasePackageTwo.getPlannedReleaseDate());
        String plannedReleaseDate3 = formatter.format(releasePackageThree.getPlannedReleaseDate());
        String releasePackageNumber1 = releasePackageOne.getReleasePackageNumber();
        String releasePackageNumber2 = releasePackageTwo.getReleasePackageNumber();
        String releasePackageNumber3 = releasePackageThree.getReleasePackageNumber();
        String title1=releasePackageOne.getTitle();
        String title2=releasePackageTwo.getTitle();
        String title3=releasePackageThree.getTitle();

        String expectedContent = releasePackagePrerequisitesOverviewExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier);
        expectedContent = expectedContent.replace("<ID>", releasePackageId + "");
        expectedContent = expectedContent.replace("<title1>", title1);
        expectedContent = expectedContent.replace("<title2>", title2);
        expectedContent = expectedContent.replace("<title3>", title3);
        expectedContent = expectedContent.replace("<releasePackageNumber1>", releasePackageNumber1);
        expectedContent = expectedContent.replace("<releasePackageNumber2>", releasePackageNumber2);
        expectedContent = expectedContent.replace("<releasePackageNumber3>", releasePackageNumber3);
        expectedContent = expectedContent.replace("<ID1>", releasePackagePrerequisites.get(0) + "");
        expectedContent = expectedContent.replace("<ID2>", releasePackagePrerequisites.get(1) + "");
        expectedContent = expectedContent.replace("<ID3>", releasePackagePrerequisites.get(2) + "");
        expectedContent = expectedContent.replace("<planned_release_date1>", plannedReleaseDate1.replaceAll("Z$", "+00:00"));
        expectedContent = expectedContent.replace("<planned_release_date2>", plannedReleaseDate2.replaceAll("Z$", "+00:00"));
        expectedContent = expectedContent.replace("<planned_release_date3>", plannedReleaseDate3.replaceAll("Z$", "+00:00"));
        expectedContent = expectedContent.replace("<planned_effective_date1>",plannedEffectiveDate1.replaceAll("Z$", "+00:00"));
        expectedContent = expectedContent.replace("<planned_effective_date2>",plannedEffectiveDate2.replaceAll("Z$", "+00:00"));
        expectedContent = expectedContent.replace("<planned_effective_date3>",plannedEffectiveDate3.replaceAll("Z$", "+00:00"));
        JSONAssert.assertEquals("release package prerequisites overview is not as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @ParameterizedTest(name = "user is able to fetch release package prerequisites search summary when release package is in {1}")
    @CsvFileSource(resources = "/parameters/releasepackage/prerequisites/Overview.csv", numLinesToSkip = 1)
    void releasePackageSearchSummary(String user, ReleasePackageStatus originalReleasePackageStatus) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalReleasePackageStatus);
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));
        List<PrerequisiteReleasePackage> inputPrerequisites = entityInstanceManager.getAddPrerequisiteRequest(originalReleasePackageStatus);
        entityInstanceManager.createPrerequisites(releasePackageId, inputPrerequisites);
        Optional<ReleasePackage> optionalReleasePackagePrerequisites = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackagePrerequisites.isPresent(), equalTo(true));
        entityInstanceManager.updateReleasePackageNumberForSearchSummary(releasePackageId);
        String releasePackageNumber = entityInstanceManager.getReleasePackageIdById(releasePackageId);
        ReleasePackage releasePackageParent = optionalReleasePackagePrerequisites.get();
        Optional<ReleasePackageContext> ecnContext = releasePackageParent.getContexts().stream().filter(releasePackageContext -> releasePackageContext.getType().equalsIgnoreCase("ECN")).findFirst();
        String ecn = ecnContext.get().getContextId();
        String title = releasePackageParent.getTitle();
        String path = PathGenerator.getPathForPrerequisitesSearchSummary("release-packages", releasePackageNumber);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(get(path))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        List<String> releasePackagePrerequisites = entityInstanceManager.getReleasePackagePrerequisites(releasePackageId);
        Optional<ReleasePackage> firstPrerequisiteDetails = releasePackageRepository.findById(Long.parseLong(releasePackagePrerequisites.get(0)));
        ReleasePackage releasePackageOne = firstPrerequisiteDetails.get();
        Optional<ReleasePackage> secondPrerequisiteDetails = releasePackageRepository.findById(Long.parseLong(releasePackagePrerequisites.get(1)));
        ReleasePackage releasePackageTwo = secondPrerequisiteDetails.get();
        Optional<ReleasePackage> thirdPrerequisiteDetails = releasePackageRepository.findById(Long.parseLong(releasePackagePrerequisites.get(2)));
        ReleasePackage releasePackageThree = thirdPrerequisiteDetails.get();

        String releasePackageNumber1 = releasePackageOne.getReleasePackageNumber();
        String releasePackageNumber2 = releasePackageTwo.getReleasePackageNumber();
        String releasePackageNumber3 = releasePackageThree.getReleasePackageNumber();
        Long releasePackageId1 = releasePackageOne.getId();
        Long releasePackageId2 = releasePackageTwo.getId();
        Long releasePackageId3 = releasePackageThree.getId();
        String title1 = releasePackageOne.getTitle();
        String title2 = releasePackageTwo.getTitle();
        String title3 = releasePackageThree.getTitle();
        Optional<ReleasePackageContext> ecnContext1 = releasePackageOne.getContexts().stream().filter(releasePackageContext -> releasePackageContext.getType().equalsIgnoreCase("ECN")).findFirst();
        Optional<ReleasePackageContext> ecnContext2 = releasePackageTwo.getContexts().stream().filter(releasePackageContext -> releasePackageContext.getType().equalsIgnoreCase("ECN")).findFirst();
        Optional<ReleasePackageContext> ecnContext3 = releasePackageThree.getContexts().stream().filter(releasePackageContext -> releasePackageContext.getType().equalsIgnoreCase("ECN")).findFirst();
        String ecn1 = ecnContext1.get().getContextId();
        String ecn2 = ecnContext2.get().getContextId();
        String ecn3 = ecnContext3.get().getContextId();

        String expectedContent = prerequisitesSearchSummaryExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier);
        expectedContent = expectedContent.replace("<ID>", releasePackageId + "");
        expectedContent = expectedContent.replace("<ID1>", releasePackageId1 + "");
        expectedContent = expectedContent.replace("<ID2>", releasePackageId2 + "");
        expectedContent = expectedContent.replace("<ID3>", releasePackageId3 + "");
        expectedContent = expectedContent.replace("<title1>", title1);
        expectedContent = expectedContent.replace("<title2>", title2);
        expectedContent = expectedContent.replace("<title3>", title3);
        expectedContent = expectedContent.replace("<ecn1>", ecn1);
        expectedContent = expectedContent.replace("<ecn2>", ecn2);
        expectedContent = expectedContent.replace("<ecn3>", ecn3);
        expectedContent = expectedContent.replace("<ecn>", ecn);
        expectedContent = expectedContent.replace("<title>", title);
        expectedContent = expectedContent.replace("<releasePackageNumber>", releasePackageNumber);


        expectedContent = expectedContent.replace("<releasePackageNumber1>", releasePackageNumber1);
        expectedContent = expectedContent.replace("<releasePackageNumber2>", releasePackageNumber2);
        expectedContent = expectedContent.replace("<releasePackageNumber3>", releasePackageNumber3);


        JSONAssert.assertEquals("release package prerequisites overview is not as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

}
