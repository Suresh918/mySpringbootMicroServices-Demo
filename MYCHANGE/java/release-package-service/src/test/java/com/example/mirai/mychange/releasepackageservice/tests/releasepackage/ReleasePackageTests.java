package com.example.mirai.projectname.releasepackageservice.tests.releasepackage;


import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.projectname.releasepackageservice.BaseTest;
import com.example.mirai.projectname.releasepackageservice.ExceptionValidator;
import com.example.mirai.projectname.releasepackageservice.fixtures.EntityPojoFactory;
import com.example.mirai.projectname.releasepackageservice.fixtures.JsonNodeFactory;
import com.example.mirai.projectname.releasepackageservice.fixtures.JwtFactory;
import com.example.mirai.projectname.releasepackageservice.json.ExceptionResponse;
import com.example.mirai.projectname.releasepackageservice.json.ReleasePackageJson;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageCaseActions;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageContext;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageStatus;
import com.example.mirai.projectname.releasepackageservice.shared.Constants;
import com.example.mirai.projectname.releasepackageservice.tests.myteams.MyTeamsTests;
import com.example.mirai.projectname.releasepackageservice.utils.PathGenerator;
import com.example.mirai.projectname.releasepackageservice.utils.Validator;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReleasePackageTests extends BaseTest {

    private static String releasePackageOverviewExpectedContent;

    static {
        InputStream inputStream = MyTeamsTests.class.getResourceAsStream("/expectations/releasepackage/prerequisites/Overview.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        releasePackageOverviewExpectedContent = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    @ParameterizedTest(name = "{0} user is to create release package expected to be {1}")
    @CsvFileSource(resources = "/parameters/releasepackage/Create.csv", numLinesToSkip = 1)
    void userToCreateReleasePackage(String user, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        ReleasePackage requestReleasePackage = EntityPojoFactory.createReleasePackage(dataIdentifier, "ALL_PROPERTIES");

        String path = PathGenerator.getReleasePackageCreationPath();
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(post(path)
                .content(objectMapper.writeValueAsString(requestReleasePackage))
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
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.CREATED.value()));
                ReleasePackageJson responseReleasePackage = new ReleasePackageJson(content);
                Long id = responseReleasePackage.getId();
                Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(id);
                assertThat(optionalReleasePackage.isPresent(), equalTo(true));
                ReleasePackage savedReleasePackage = optionalReleasePackage.get();
                com.example.mirai.projectname.releasepackageservice.tests.releasepackage.Validator.createReleasePackageIsSuccessful(requestReleasePackage, savedReleasePackage, responseReleasePackage);
                break;
        }
    }

    @ParameterizedTest(name = "{0} user is to perform {4} case action on release package in status {1},review context in status {2}, with missing property {3} and change owner type {6} and sap change control is {7} , expect to be {5}")
    @CsvFileSource(resources = "/parameters/releasepackage/CaseAction.csv", numLinesToSkip = 1)
    void userToPerformCaseActionOnReleasePackageWhenReleasePackageInStatus(String user, ReleasePackageStatus originalReleasePackageStatus, String reviewStatus, String missingProperty,
                                                                           ReleasePackageCaseActions releasePackageCaseActions, String expectedResult, String changeOwnerType, boolean sapChangeControl) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long releasePackageId = null;

        switch (missingProperty) {
            case "title":
                releasePackageId = entityInstanceManager.createReleasePackageAndSetStatusForCaseAction(dataIdentifier, "ALL_PROPERTIES_EXCEPT_TITLE", originalReleasePackageStatus, releasePackageCaseActions, changeOwnerType, sapChangeControl);
                break;
            case "plannedReleaseDate":
                releasePackageId = entityInstanceManager.createReleasePackageAndSetStatusForCaseAction(dataIdentifier, "ALL_PROPERTIES_EXCEPT_PLANNED_RELEASE_DATE", originalReleasePackageStatus, releasePackageCaseActions, changeOwnerType, sapChangeControl);
                break;
            case "plannedEffectiveDate":
                releasePackageId = entityInstanceManager.createReleasePackageAndSetStatusForCaseAction(dataIdentifier, "ALL_PROPERTIES_EXCEPT_PLANNED_EFFECTIVE_DATE", originalReleasePackageStatus, releasePackageCaseActions, changeOwnerType, sapChangeControl);
                break;
            case "sapChangeControl":
                releasePackageId = entityInstanceManager.createReleasePackageAndSetStatusForCaseAction(dataIdentifier, "ALL_PROPERTIES_EXCEPT_SAP_CHANGE_CONTROL", originalReleasePackageStatus, releasePackageCaseActions, changeOwnerType, sapChangeControl);
                break;
            case "review-context":
                releasePackageId = entityInstanceManager.createReleasePackageAndSetStatusForCaseAction(dataIdentifier, "ALL_PROPERTIES_WITHOUT_REVIEW_CONTEXT", originalReleasePackageStatus, releasePackageCaseActions, changeOwnerType, sapChangeControl);
                break;
            case "types":
                releasePackageId = entityInstanceManager.createReleasePackageAndSetStatusForCaseAction(dataIdentifier, "ALL_PROPERTIES_EXCEPT_TYPES", originalReleasePackageStatus, releasePackageCaseActions, changeOwnerType, sapChangeControl);
                break;
            case "plmCoordinator":
                releasePackageId = entityInstanceManager.createReleasePackageAndSetStatusForCaseAction(dataIdentifier, "ALL_PROPERTIES_EXCEPT_PLMCOORDINATOR", originalReleasePackageStatus, releasePackageCaseActions, changeOwnerType, sapChangeControl);
                break;

            default:
                releasePackageId = entityInstanceManager.createReleasePackageAndSetStatusForCaseAction(dataIdentifier, "ALL_PROPERTIES", originalReleasePackageStatus, releasePackageCaseActions, changeOwnerType, sapChangeControl);

                break;
        }

        if (!missingProperty.equals("review-context")) {
            entityInstanceManager.updateReleasePackageContextAndSetStatus(releasePackageId, reviewStatus, "REVIEW");
        }
        assert releasePackageId != null;
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));

        //team center checks
        if (missingProperty.equals("NONE") || missingProperty.equals("review-context")) {
            if (releasePackageCaseActions.equals(ReleasePackageCaseActions.CREATE)) {
                teamcenterMockServer.mockTeamcenterSuccessfulCreate();
                //TODO: get team center id and update in RP context

                sapMdgMockServer.mockSapMdgChangeRequestSuccessfulCreate(optionalReleasePackage.get());

            } else {
                String teamcenterId = getContextIdByType(optionalReleasePackage.get(), "TEAMCENTER");
                teamcenterMockServer.mockTeamcenterSuccessfulUpdate(teamcenterId);
            }
        }
//impacted Item service check

        if (releasePackageCaseActions.equals(ReleasePackageCaseActions.CREATE) || releasePackageCaseActions.equals(ReleasePackageCaseActions.READY)) {
            String releasePackageNumber = entityInstanceManager.getReleasePackageIdById(releasePackageId);

            String caseAction = null;
            if (releasePackageCaseActions.equals(ReleasePackageCaseActions.CREATE)) {
                caseAction = "CREATE";
            } else if (releasePackageCaseActions.equals(ReleasePackageCaseActions.READY)) {
                caseAction = "READY";
            }
            impactedItemMockServer.mockImpactedItemForSuccessfulPerformCaseAction(caseAction, releasePackageNumber);
        }

        ReleasePackage releasePackageBeforeCaseAction = optionalReleasePackage.get();
        String path = PathGenerator.getReleasePackageCaseActionPath(releasePackageId, releasePackageCaseActions);

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);

        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(patch(path).param("case-action",releasePackageCaseActions.name()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();

        optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));
        ReleasePackage releasePackageAfterCaseAction = optionalReleasePackage.get();

        switch (expectedResult) {
            case "UNAUTHORIZED":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.FORBIDDEN.value()));
                ExceptionResponse exceptionResponse = new ExceptionResponse(content);
                com.example.mirai.projectname.releasepackageservice.tests.releasepackage.Validator.unauthorizedExceptionAndReleasePackageDidNotChange(releasePackageBeforeCaseAction, releasePackageAfterCaseAction, exceptionResponse, path, originalReleasePackageStatus.getStatusCode());
                break;
            case "MANDATORYFIELDMISSING":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value()));
                exceptionResponse = new ExceptionResponse(content);
                ExceptionValidator.exceptionResponseIsMandatoryFieldViolationException(exceptionResponse, path, ReleasePackage.class.getSimpleName());
                break;
            case "APPLICATIONEXCEPTION":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value()));
                exceptionResponse = new ExceptionResponse(content);
                ExceptionValidator.exceptionResponseIsApplicationException(exceptionResponse, path, ReleasePackage.class.getSimpleName());
                break;
            default:
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
                ReleasePackageStatus releasePackageStatus = ReleasePackageStatus.valueOf(expectedResult);
                com.example.mirai.projectname.releasepackageservice.tests.releasepackage.Validator.releasePackagesAreSameWithoutComparingAuditAndStatus(releasePackageBeforeCaseAction, releasePackageAfterCaseAction);
                assertThat("status after case action is not as expected", releasePackageAfterCaseAction.getStatus(), equalTo(releasePackageStatus.getStatusCode()));

                ReleasePackage releasePackage = optionalReleasePackage.get();
                Optional<ReleasePackageContext> changeObjectContext = releasePackage.getContexts().stream().filter(item -> Objects.equals(item.getType(), "CHANGEOBJECT")).findFirst();
                if(changeObjectContext.isPresent()) {
                    String changeObjectStatus = changeObjectContext.get().getStatus();
                    if (releasePackageCaseActions.equals(ReleasePackageCaseActions.CREATE)) {
                        assertThat("change Object status after case action is not as expected", changeObjectStatus, equalTo("3"));

                    } else if (releasePackageCaseActions.equals(ReleasePackageCaseActions.READY)) {
                        if (changeObjectContext.isPresent()) {
                            assertThat("change Object status after case action is not as expected", changeObjectStatus, equalTo("4"));
                        }
                    }
                }
                break;
        }
    }

    private String getContextIdByType(ReleasePackage releasePackage, String type) {
        return releasePackage.getContexts().stream().filter(context -> context.getType().equals(type)).findFirst().get().getContextId();
    }

    //@Test
    public void userToPerformCaseActionOnReleasePackageWhenSapMdgCrIdAllreadyExistException() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long releasePackageId = null;

        releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReleasePackageStatus.DRAFTED);
        assert releasePackageId != null;
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));
        if (optionalReleasePackage.get().getStatus().equals(ReleasePackageStatus.DRAFTED.getStatusCode())) {
            teamcenterMockServer.mockTeamcenterSuccessfulCreate();
            sapMdgMockServer.mockSapMdgThrowsSapMdgCrAlreadyExistsExceptionOnMdgChangeRequestCreation(optionalReleasePackage.get());
        }
        //ReleasePackage releasePackageBeforeCaseAction = optionalReleasePackage.get();
        String path = PathGenerator.getReleasePackageCaseActionPath(releasePackageId, ReleasePackageCaseActions.CREATE);

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(patch(path))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));
    }


    @ParameterizedTest(name = "{0} user updates property {1} when release package in {2} status, expect to be {3}")
    @CsvFileSource(resources = "/parameters/releasepackage/PropertyUpdate.csv", numLinesToSkip = 1)
    void userToUpdatePropertyOfReleasePackageInStatus(String user, String property, ReleasePackageStatus releasePackageStatus, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", releasePackageStatus);
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));

        String path = PathGenerator.getEntityUpdatePath("release-packages", releasePackageId);

        ReleasePackage releasePackageBeforeUpdate = optionalReleasePackage.get();

        JsonNode updateRequest = null;
        switch (property) {
            case "title":
                String titleValue = releasePackageBeforeUpdate.getTitle();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, titleValue);
                break;
            case "change_specialist3":
                User changeSpecialist3Value = releasePackageBeforeUpdate.getChangeSpecialist3();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForUser(property, changeSpecialist3Value);
                break;
            case "executor":
                User executorValue = releasePackageBeforeUpdate.getExecutor();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForUser(property, executorValue);
                break;
            case "planned_release_date":
                LocalDateTime plannedReleaseDate = new Timestamp(releasePackageBeforeUpdate.getPlannedReleaseDate().getTime()).toLocalDateTime();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForLocalDateTime(property, plannedReleaseDate);
                break;
            case "planned_effective_date":
                LocalDateTime plannedEffectiveDate = new Timestamp(releasePackageBeforeUpdate.getPlannedEffectiveDate().getTime()).toLocalDateTime();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForLocalDateTime(property, plannedEffectiveDate);
                break;
            case "sap_change_control":
                Boolean sapChangeControlValue = releasePackageBeforeUpdate.getSapChangeControl();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForBoolean(property, sapChangeControlValue);
                break;
            case "prerequisites_applicable":
                String prerequisitesApplicableValue = releasePackageBeforeUpdate.getPrerequisitesApplicable();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, prerequisitesApplicableValue);
                break;
            case "prerequisites_detail":
                String prerequisitesDetailValue = releasePackageBeforeUpdate.getPrerequisitesDetail();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, prerequisitesDetailValue);
                break;
            case "product_id":
                String productIdValue = releasePackageBeforeUpdate.getProductId();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, productIdValue);
                break;
            case "project_id":
                String projectIdValue = releasePackageBeforeUpdate.getProjectId();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, projectIdValue);
                break;
            case "tags":
                List<String> tags = releasePackageBeforeUpdate.getTags();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForListOfString(property,tags);
                break;
            case "types":
                List<String> types = releasePackageBeforeUpdate.getTypes();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForListOfString(property,types);
                break;
            default:
        }


           if(property.equalsIgnoreCase("title") ||  property.equalsIgnoreCase("planned_effective_date")) {
               String teamcenterId = getContextIdByType(optionalReleasePackage.get(), "TEAMCENTER");
               teamcenterMockServer.mockTeamcenterSuccessfulUpdate(teamcenterId);
           }

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(patch(path)
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));
        ReleasePackage releasePackageAfterUpdate = optionalReleasePackage.get();

        switch (expectedResult) {
            case "UNAUTHORIZED":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.FORBIDDEN.value()));
                ExceptionResponse exceptionResponse = new ExceptionResponse(content);
                Validator.unauthorizedExceptionAndReleasePackageDidNotChange(releasePackageBeforeUpdate, releasePackageAfterUpdate, exceptionResponse, path, releasePackageStatus.getStatusCode());
                break;
            case "AUTHORIZED":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
                ReleasePackageJson releasePackageJson = new ReleasePackageJson(content);

                switch (property) {
                    case "title":
                        Validator.releasePackagesAreSameWithoutComparingTitle(releasePackageBeforeUpdate, releasePackageAfterUpdate);
                        assertThat("title not changed", releasePackageBeforeUpdate.getTitle(), Matchers.not(equalTo(releasePackageAfterUpdate.getTitle())));
                        break;
                    case "status":
                        Validator.releasePackagesAreSameWithoutComparingStatus(releasePackageBeforeUpdate, releasePackageAfterUpdate);
                        assertThat("status not changed", releasePackageBeforeUpdate.getStatus(), Matchers.not(equalTo(releasePackageAfterUpdate.getStatus())));
                        break;

                    case "change_specialist3":
                        Validator.releasePackagesAreSameWithoutComparingChangeSpecialist3(releasePackageBeforeUpdate, releasePackageAfterUpdate);
                        assertThat("changespecialist3 is null", releasePackageBeforeUpdate.getChangeSpecialist3(), is(notNullValue()));
                        //assertThat("changespecialist3 are not same", releasePackageBeforeUpdate.getChangeSpecialist3(), Matchers.not(samePropertyValuesAs(releasePackageAfterUpdate.getChangeSpecialist3())));
                        break;
                    case "executor":
                        Validator.releasePackagesAreSameWithoutComparingExecutor(releasePackageBeforeUpdate, releasePackageAfterUpdate);
                        assertThat("executors is null", releasePackageBeforeUpdate.getExecutor(), is(notNullValue()));
                       // assertThat("executors are not same", releasePackageBeforeUpdate.getExecutor(), Matchers.not(samePropertyValuesAs(releasePackageAfterUpdate.getExecutor())));
                        break;
                    case "planned_release_date":
                        Validator.releasePackagesAreSameWithoutComparingPlannedReleaseDate(releasePackageBeforeUpdate, releasePackageAfterUpdate);
                        assertThat("planned release date not changed", releasePackageBeforeUpdate.getPlannedReleaseDate(), Matchers.not(equalTo(releasePackageAfterUpdate.getPlannedReleaseDate())));
                        break;
                    case "planned_effective_date":
                        Validator.releasePackagesAreSameWithoutComparingPlannedEffectiveDate(releasePackageBeforeUpdate, releasePackageAfterUpdate);
                        assertThat("planned effective not changed", releasePackageBeforeUpdate.getPlannedEffectiveDate(), Matchers.not(equalTo(releasePackageAfterUpdate.getPlannedEffectiveDate())));
                        break;
                    case "prerequisites_applicable":
                        Validator.releasePackagesAreSameWithoutComparingPrerequisitesApplicable(releasePackageBeforeUpdate, releasePackageAfterUpdate);
                        assertThat("prerequisites applicable not changed", releasePackageBeforeUpdate.getPrerequisitesApplicable(), Matchers.not(equalTo(releasePackageAfterUpdate.getPrerequisitesApplicable())));
                        break;
                    case "prerequisites_detail":
                        Validator.releasePackagesAreSameWithoutComparingPrerequisitesDetail(releasePackageBeforeUpdate, releasePackageAfterUpdate);
                        assertThat("prerequisites detail not changed", releasePackageBeforeUpdate.getPrerequisitesDetail(), Matchers.not(equalTo(releasePackageAfterUpdate.getPrerequisitesDetail())));
                        break;
                    case "product_id":
                        Validator.releasePackagesAreSameWithoutComparingProductId(releasePackageBeforeUpdate, releasePackageAfterUpdate);
                        assertThat("product id not changed", releasePackageBeforeUpdate.getProductId(), Matchers.not(equalTo(releasePackageAfterUpdate.getProductId())));
                        break;
                    case "project_id":
                        Validator.releasePackagesAreSameWithoutComparingProjectId(releasePackageBeforeUpdate, releasePackageAfterUpdate);
                        assertThat("project id not changed", releasePackageBeforeUpdate.getProjectId(), Matchers.not(equalTo(releasePackageAfterUpdate.getProjectId())));
                        break;
                    case "tags":
                        Validator.releasePackagesAreSameWithoutComparingTags(releasePackageBeforeUpdate, releasePackageAfterUpdate);
                        assertThat("tags not changed", releasePackageBeforeUpdate.getTags(), Matchers.not(equalTo(releasePackageAfterUpdate.getTags())));
                        break;

                    default:
                }
                break;
        }
    }

    @ParameterizedTest(name = "{0} user has correct case permissions on release package in status {1}")
    @MethodSource("com.example.mirai.projectname.releasepackageservice.fixtures.CasePermissionParametersFactory#getArgumentsForUserHasCorrectCasePermissionInStatus")
    void userToGetReleasePackageCasePermissions(String user, ReleasePackageStatus releasePackageStatus, String isSecure, String expectedContent) throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", releasePackageStatus);

        if (!user.equalsIgnoreCase("ReleasePackage.change-control-board-member")) {
            isSecure = "false";
        }
        entityInstanceManager.updateReleasePackageAndSetIsSecure(releasePackageId, isSecure.equalsIgnoreCase("false") ? false : true);


        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));


        String path = PathGenerator.getReleasePackageCasePermissionPath(releasePackageId);

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult mvcResult = getMockMvc().perform(get(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();


        String content = mvcResult.getResponse().getContentAsString();
        System.out.println("CONTENT: " + content+" status-"+releasePackageStatus.getStatusLabel());
        JSONAssert.assertEquals("case permissions are not as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @ParameterizedTest(name = "user is able to fetch release package overview when release package is in {1}")
    @CsvFileSource(resources = "/parameters/releasepackage/prerequisites/Overview.csv", numLinesToSkip = 1)
    void releasePackageOverview(String user, ReleasePackageStatus originalReleasePackageStatus) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);
        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalReleasePackageStatus);
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));
        String path = PathGenerator.getPathForReleasePackageOverview("release-packages", releasePackageId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(get(path))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();

        ReleasePackage releasePackage = optionalReleasePackage.get();
        Optional<ReleasePackageContext> ecnContext = releasePackage.getContexts().stream().filter(releasePackageContext -> releasePackageContext.getType().equalsIgnoreCase("ECN")).findFirst();
        Optional<ReleasePackageContext> teamcenterContext = releasePackage.getContexts().stream().filter(releasePackageContext -> releasePackageContext.getType().equalsIgnoreCase("TEAMCENTER")).findFirst();
        String ecn = ecnContext.get().getContextId();
        String teamcenterId = teamcenterContext.get().getContextId();
        String title = releasePackage.getTitle();
        String plannedEffectiveDate = formatter.format(releasePackage.getPlannedEffectiveDate());
        String plannedReleaseDate = formatter.format(releasePackage.getPlannedReleaseDate());
        String releasePackageNumber = releasePackage.getReleasePackageNumber();

        String expectedContent = releasePackageOverviewExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier);
        expectedContent = expectedContent.replace("<ID>", releasePackageId + "");
        expectedContent = expectedContent.replace("<title>", title);
        expectedContent = expectedContent.replace("<releasePackageNumber>", releasePackageNumber);
        expectedContent = expectedContent.replace("<ecn>", ecn);
        expectedContent = expectedContent.replace("<teamcenterId>", teamcenterId);
        expectedContent = expectedContent.replace("<planned_release_date>", plannedReleaseDate.replaceAll("Z$", "+00:00"));
        expectedContent = expectedContent.replace("<planned_effective_date>", plannedEffectiveDate.replaceAll("Z$", "+00:00"));
        JSONAssert.assertEquals("release package prerequisites overview is not as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }
}
