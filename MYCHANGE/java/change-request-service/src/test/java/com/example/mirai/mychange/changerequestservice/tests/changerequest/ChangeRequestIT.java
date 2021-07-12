package com.example.mirai.projectname.changerequestservice.tests.changerequest;

import com.example.mirai.libraries.core.model.User;
/*import com.example.mirai.libraries.scm.scia.model.Scia;*/
import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestCaseActions;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestStatus;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestAggregate;
import com.example.mirai.projectname.changerequestservice.changerequest.model.dto.ImportData;
import com.example.mirai.projectname.changerequestservice.completebusinesscase.model.CompleteBusinessCase;
import com.example.mirai.projectname.changerequestservice.customerimpact.model.CustomerImpact;
import com.example.mirai.projectname.changerequestservice.fixtures.EntityPojoFactory;
import com.example.mirai.projectname.changerequestservice.fixtures.JsonNodeFactory;
import com.example.mirai.projectname.changerequestservice.fixtures.JwtFactory;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
import com.example.mirai.projectname.changerequestservice.json.ChangeRequestCaseStatusJson;
import com.example.mirai.projectname.changerequestservice.json.ChangeRequestDetailJson;
import com.example.mirai.projectname.changerequestservice.json.ChangeRequestJson;
import com.example.mirai.projectname.changerequestservice.json.ExceptionResponse;
import com.example.mirai.projectname.changerequestservice.preinstallimpact.model.PreinstallImpact;
import com.example.mirai.projectname.changerequestservice.scope.model.Scope;
import com.example.mirai.projectname.changerequestservice.solutiondefinition.model.SolutionDefinition;
import com.example.mirai.projectname.changerequestservice.tests.BaseTest;
import com.example.mirai.projectname.changerequestservice.tests.ExceptionValidator;
import com.example.mirai.projectname.changerequestservice.tests.myteams.MyTeamsIT;
import com.example.mirai.projectname.changerequestservice.utils.PathGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
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
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ChangeRequestIT extends BaseTest {

    @ParameterizedTest(name = "{0} user is to create change request expected to be {1}")
    @CsvFileSource(resources = "/parameters/changerequest/Create.csv", numLinesToSkip = 1)
    void userToCreateChangeRequest(String user, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        ChangeRequestAggregate requestChangeRequest = EntityPojoFactory.createChangeRequest();
        String path = PathGenerator.getChangeRequestAggregateCreationPath();

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier,user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        User userInContext = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //userServiceMockServer.mockUserServicePreferredRoles(userInContext.getUserId());
        MvcResult result = getMockMvc().perform(post(path)
                .content(objectMapper.writeValueAsString(requestChangeRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        switch (expectedResult) {
            case "UNAUTHORIZED":
                assertThat("Response status code is as expected", result.getResponse().getStatus(), equalTo(HttpStatus.FORBIDDEN.value()));
                break;
            case "AUTHORIZED":
                assertThat("Response status code is as expected", result.getResponse().getStatus(),equalTo(HttpStatus.CREATED.value()));
                ChangeRequestDetailJson responseChangeRequestDetail = new ChangeRequestDetailJson(content);
                ChangeRequest responseChangeRequest  = responseChangeRequestDetail.getChangeRequest();

                Long id = responseChangeRequestDetail.getId();
                Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
                assertThat(optionalChangeRequest.isPresent(), equalTo(true));
                ChangeRequest savedChangeRequest = optionalChangeRequest.get();
                Validator.createChangeRequestIsSuccessful(requestChangeRequest, savedChangeRequest, responseChangeRequest);
                break;
            default:
                return;
        }
    }


    @ParameterizedTest(name = "{0} user perform {3} case action when change request in {1} status and missing property {2} with change notice(if created) status {5}, expect to be {4}")
    @CsvFileSource(resources = "/parameters/changerequest/CaseAction.csv", numLinesToSkip = 1)
    void userToPerformCaseActionOnChangeRequestInStatus(String user, ChangeRequestStatus originalChangeRequestStatus,
                                                 String missingProperty,
                                                 ChangeRequestCaseActions changeRequestCaseAction, String expectedResult,String changeNoticeStatus) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, missingProperty, originalChangeRequestStatus,changeNoticeStatus);

        assert id!=null;

        String path = PathGenerator.getChangeRequestCaseActionPath(id, changeRequestCaseAction);

        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        ChangeRequest changeRequestBeforeCaseAction = optionalChangeRequest.get();

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier,user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        cerberusMockServer.mockCerberusSuccessfulDiaBomCreate(changeRequestBeforeCaseAction.getId());
        
        MvcResult result = getMockMvc().perform(patch(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

      optionalChangeRequest = changeRequestRepository.findById(id);
      assertThat(optionalChangeRequest.isPresent(), equalTo(true));
      ChangeRequest changeRequestAfterCaseAction = optionalChangeRequest.get();

        switch (expectedResult) {
            case "UNAUTHORIZED":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(),equalTo(HttpStatus.FORBIDDEN.value()));
                ExceptionResponse exceptionResponse = new ExceptionResponse(content);
                Validator.unauthorizedExceptionAndChangeRequestDidNotChange(changeRequestBeforeCaseAction, changeRequestAfterCaseAction, exceptionResponse, path, originalChangeRequestStatus.getStatusCode());
                break;
            case "MANDATORYFIELDMISSING":
                assertThat("Response status code is not as expected", result.getResponse().getStatus(),equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value()));
                exceptionResponse = new ExceptionResponse(content);
                ExceptionValidator.exceptionResponseIsMandatoryFieldViolationException(exceptionResponse, path, ChangeRequest.class.getSimpleName());
                break;
            default:
                assertThat("Response status code is not as expected", result.getResponse().getStatus(),equalTo(HttpStatus.OK.value()));
                ChangeRequestStatus newChangeRequestStatus = ChangeRequestStatus.valueOf(expectedResult);
                ChangeRequestCaseStatusJson changeRequestCaseStatusJson = new ChangeRequestCaseStatusJson(content);
                Validator.changeRequestJsonsAreSameWithoutComparingAuditAndStatus(changeRequestBeforeCaseAction, changeRequestAfterCaseAction);
                assertThat("status after case action is not as expected", changeRequestAfterCaseAction.getStatus(), equalTo(newChangeRequestStatus.getStatusCode()));
                assertThat("status in response and database are different", changeRequestCaseStatusJson.getStatus(), equalTo(changeRequestAfterCaseAction.getStatus()));
                break;
        }
    }

    @ParameterizedTest(name = "{0} user access change request in {1} status, and is allowed value for CREATE_CHANGE_NOTICE case action is expected to be {2}")
    @CsvFileSource(resources = "/parameters/changerequest/CreateChangeNotice.csv", numLinesToSkip = 1)
    void userToPerformCreateChangeNoticeOnChangeRequestInStatus(String user, ChangeRequestStatus originalChangeRequestStatus,
                                                                                    String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, null, originalChangeRequestStatus,null);
        assert id!=null;
        String path = PathGenerator.getCaseActionsPath(id);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        ChangeRequest changeRequestBeforeCaseAction = optionalChangeRequest.get();

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier,user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        cerberusMockServer.mockCerberusSuccessfulDiaBomCreate(changeRequestBeforeCaseAction.getId());

        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        JSONArray arr = JsonPath.parse(content).read("$.[?(@.case_action =='CREATE_CHANGE_NOTICE')]");
        String actualResult = ((LinkedHashMap) arr.get(0)).get("is_allowed").toString();

        assertThat("is_allowed", actualResult, equalTo(expectedResult));

    }

    @ParameterizedTest(name = "{0} user access change request in {1} status, and is allowed value for CREATE_AGENDA_ITEM case action is expected to be {2}")
    @CsvFileSource(resources = "/parameters/changerequest/CreateAgendaItem.csv", numLinesToSkip = 1)
    void userToPerformCreateAgendaItemOnChangeRequestInStatus(String user, ChangeRequestStatus originalChangeRequestStatus,
                                                              String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, null, originalChangeRequestStatus,null);
        assert id!=null;
        String path = PathGenerator.getCaseActionsPath(id);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        ChangeRequest changeRequestBeforeCaseAction = optionalChangeRequest.get();

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier,user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        cerberusMockServer.mockCerberusSuccessfulDiaBomCreate(changeRequestBeforeCaseAction.getId());

        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        JSONArray arr = JsonPath.parse(content).read("$.[?(@.case_action =='CREATE_AGENDA_ITEM')]");
        String actualResult = ((LinkedHashMap) arr.get(0)).get("is_allowed").toString();

        assertThat("is_allowed", actualResult, equalTo(expectedResult));

    }

    @ParameterizedTest(name = "{0} user access change request in {1} status, and is allowed value for CREATE_ACTION case action is expected to be {2}")
    @CsvFileSource(resources = "/parameters/changerequest/CreateAction.csv", numLinesToSkip = 1)
    void userToPerformCreateActionOnChangeRequestInStatus(String user, ChangeRequestStatus originalChangeRequestStatus,
                                                          String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, null, originalChangeRequestStatus,null);
        assert id!=null;
        String path = PathGenerator.getCaseActionsPath(id);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        ChangeRequest changeRequestBeforeCaseAction = optionalChangeRequest.get();

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier,user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        cerberusMockServer.mockCerberusSuccessfulDiaBomCreate(changeRequestBeforeCaseAction.getId());

        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        JSONArray arr = JsonPath.parse(content).read("$.[?(@.case_action =='CREATE_ACTION')]");
        String actualResult = ((LinkedHashMap) arr.get(0)).get("is_allowed").toString();

        assertThat("is_allowed", actualResult, equalTo(expectedResult));

    }

    @ParameterizedTest(name = "{0} user update change owner type when change request in {1} status,  and is allowed value for UPDATE_CHANGE_OWNER_TYPE case action is expected to be  {2}")
    @CsvFileSource(resources = "/parameters/changerequest/UpdateChangeOwnerType.csv", numLinesToSkip = 1)
    void userToPerformUpdateChangeOwnerTypeOnChangeRequestInStatus(String user, ChangeRequestStatus originalChangeRequestStatus,
                                                                   String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, null, originalChangeRequestStatus,null);
        assert id!=null;
        String path = PathGenerator.getCaseActionsPath(id);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        ChangeRequest changeRequestBeforeCaseAction = optionalChangeRequest.get();

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier,user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        cerberusMockServer.mockCerberusSuccessfulDiaBomCreate(changeRequestBeforeCaseAction.getId());

        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        JSONArray arr = JsonPath.parse(content).read("$.[?(@.case_action =='UPDATE_CHANGE_OWNER_TYPE')]");
        String actualResult = ((LinkedHashMap) arr.get(0)).get("is_allowed").toString();

        assertThat("is_allowed", actualResult, equalTo(expectedResult));

    }

    @ParameterizedTest(name = "{0} user access change request in {1} status, and is allowed value for CREATE_AGENDA_ITEM_OFFLINE_DECISION case action is expected to be {2}")
    @CsvFileSource(resources = "/parameters/changerequest/CreateAgendaItemOfflineDecision.csv", numLinesToSkip = 1)
    void userToPerformCreateAgendaItemOfflineDecisionOnChangeRequestInStatus(String user, ChangeRequestStatus originalChangeRequestStatus,
                                                                             String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, null, originalChangeRequestStatus,null);
        assert id!=null;
        String path = PathGenerator.getCaseActionsPath(id);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        ChangeRequest changeRequestBeforeCaseAction = optionalChangeRequest.get();

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier,user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        cerberusMockServer.mockCerberusSuccessfulDiaBomCreate(changeRequestBeforeCaseAction.getId());

        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        JSONArray arr = JsonPath.parse(content).read("$.[?(@.case_action =='CREATE_AGENDA_ITEM_OFFLINE_DECISION')]");
        String actualResult = ((LinkedHashMap) arr.get(0)).get("is_allowed").toString();

        assertThat("is_allowed", actualResult, equalTo(expectedResult));

    }

    @ParameterizedTest(name = "{0} user access change request in {1} status, and is allowed value for UPDATE_CHANGE_REQUEST_CB_RULESET case action is expected to be {2}")
    @CsvFileSource(resources = "/parameters/changerequest/UpdateChangeRequestCBRuleSet.csv", numLinesToSkip = 1)
    void userToPerformUpdateChangeRequestCBRuleSetOnChangeRequestInStatus(String user, ChangeRequestStatus originalChangeRequestStatus,
                                                                          String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, null, originalChangeRequestStatus,null);
        assert id!=null;
        String path = PathGenerator.getCaseActionsPath(id);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        ChangeRequest changeRequestBeforeCaseAction = optionalChangeRequest.get();

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier,user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        cerberusMockServer.mockCerberusSuccessfulDiaBomCreate(changeRequestBeforeCaseAction.getId());

        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        JSONArray arr = JsonPath.parse(content).read("$.[?(@.case_action =='UPDATE_CHANGE_REQUEST_CB_RULESET')]");
        String actualResult = ((LinkedHashMap) arr.get(0)).get("is_allowed").toString();

        assertThat("is_allowed", actualResult, equalTo(expectedResult));

    }

    @ParameterizedTest(name = "{0} user access change request in {1} status, and is allowed value for CREATE_SCIA case action is expected to be {2}")
    @CsvFileSource(resources = "/parameters/changerequest/CreateScia.csv", numLinesToSkip = 1)
    void userToPerformCreateSciaOnChangeRequestInStatus(String user, ChangeRequestStatus originalChangeRequestStatus,
                                                                String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, null, originalChangeRequestStatus,null);
        assert id!=null;
        String path = PathGenerator.getCaseActionsPath(id);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        ChangeRequest changeRequestBeforeCaseAction = optionalChangeRequest.get();

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier,user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        cerberusMockServer.mockCerberusSuccessfulDiaBomCreate(changeRequestBeforeCaseAction.getId());

        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        JSONArray arr = JsonPath.parse(content).read("$.[?(@.case_action =='CREATE_SCIA')]");
        String actualResult = ((LinkedHashMap) arr.get(0)).get("is_allowed").toString();

        assertThat("is_allowed", actualResult, equalTo(expectedResult));

    }

    @ParameterizedTest(name = "{0} user access change request in {1} status, and is allowed value for COMMUNICATE_CHANGE_REQUEST case action is expected to be {2}")
    @CsvFileSource(resources = "/parameters/changerequest/CommuniateChangeRequest.csv", numLinesToSkip = 1)
    void userToPerformCommunicateChangeRequestOnChangeRequestInStatus(String user, ChangeRequestStatus originalChangeRequestStatus,
                                                        String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, null, originalChangeRequestStatus,null);
        assert id!=null;
        String path = PathGenerator.getCaseActionsPath(id);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        ChangeRequest changeRequestBeforeCaseAction = optionalChangeRequest.get();

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier,user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        cerberusMockServer.mockCerberusSuccessfulDiaBomCreate(changeRequestBeforeCaseAction.getId());

        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        JSONArray arr = JsonPath.parse(content).read("$.[?(@.case_action =='COMMUNICATE_CHANGE_REQUEST')]");
        String actualResult = ((LinkedHashMap) arr.get(0)).get("is_allowed").toString();

        assertThat("is_allowed", actualResult, equalTo(expectedResult));

    }

    @ParameterizedTest(name = "{0} user access when change request in {1} status, expect to be {2}")
    @CsvFileSource(resources = "/parameters/changerequest/LinkCR.csv", numLinesToSkip = 1)
    void userToLinkChangeRequestWhenChangeRequestInStatus(String user, ChangeRequestStatus changeRequestStatus, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        UUID uuid1= UUID.randomUUID();
        String dataIdentifier1 = uuid1.toString();

        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", changeRequestStatus,null);
        assert id!=null;

        Long linkId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier1, "ALL_PROPERTIES", changeRequestStatus,null);
        List<String> linkIds = new ArrayList<>();
        linkIds.add(linkId.toString());

        String path = PathGenerator.getEntityUpdatePath(null, id);

        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        ChangeRequest changeRequestBeforeUpdate = optionalChangeRequest.get();

        JsonNode updateRequest = null;
      /*  switch (property){

            case "dependent_change_request_ids":*/
                List<String> dependentChangeRequestIds = changeRequestBeforeUpdate.getDependentChangeRequestIds();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestFordDependedntChangeRequestIds("dependent_change_request_ids", dependentChangeRequestIds,linkIds);
              /*  break;

            default:
        }*/

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier,user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(patch(path)
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        ChangeRequest changeRequestAfterUpdate = optionalChangeRequest.get();

        Optional<ChangeRequest> optionalChangeRequestLinkId = changeRequestRepository.findById(linkId);
        assertThat(optionalChangeRequestLinkId.isPresent(), equalTo(true));
        ChangeRequest changeRequestLinkId = optionalChangeRequestLinkId.get();

        switch(expectedResult) {
            case "UNAUTHORIZED":
                assertThat("Response status code is as expected", result.getResponse().getStatus(),equalTo(HttpStatus.FORBIDDEN.value()));
                ExceptionResponse exceptionResponse = new ExceptionResponse(content);
                Validator.unauthorizedExceptionAndChangeRequestDidNotChange(changeRequestBeforeUpdate, changeRequestAfterUpdate, exceptionResponse, path, changeRequestStatus.getStatusCode());
                break;
            case "AUTHORIZED":
                assertThat("Response status code is as expected", result.getResponse().getStatus(),equalTo(HttpStatus.OK.value()));
                /*switch (property){

                    case "dependent_change_request_ids":*/
                        Validator.changeRequestAreSameWithoutComparingDependentChangeRequests(changeRequestBeforeUpdate, changeRequestAfterUpdate);
                        assertThat("dependentChangeRequestsIds not same", changeRequestBeforeUpdate.getDependentChangeRequestIds(), Matchers.not(equalTo(changeRequestAfterUpdate.getDependentChangeRequestIds())));
                        assertThat("dependentChangeRequestsIds for LinkId", changeRequestAfterUpdate.getDependentChangeRequestIds().get(0), equalTo(linkId.toString()));
                        assertThat("dependentChangeRequestsIds for CR", id.toString(), equalTo(changeRequestLinkId.getDependentChangeRequestIds().get(0)));
                      /*  break;
                    default:
                }*/
                break;
            default:
                return;
        }
    }

    @ParameterizedTest(name = "{0} user access when change request in {1} status, expect to be {2}")
    @CsvFileSource(resources = "/parameters/changerequest/UnLinkCR.csv", numLinesToSkip = 1)
    void userToPerformUnLinkCROnChangeRequestInStatus(String user, ChangeRequestStatus changeRequestStatus, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        UUID uuid1= UUID.randomUUID();
        String dataIdentifier1 = uuid1.toString();

        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", changeRequestStatus,null);
        assert id!=null;

        Long linkId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier1, "ALL_PROPERTIES", changeRequestStatus,null);
        List<String> linkIds = new ArrayList<>();
        linkIds.add(linkId.toString());

        String path = PathGenerator.getEntityUpdatePath(null, id);

        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        ChangeRequest changeRequestBeforeUpdate = optionalChangeRequest.get();

        JsonNode updateRequest = null;
      /*  switch (property){

            case "dependent_change_request_ids":*/
                List<String> dependentChangeRequestIds = changeRequestBeforeUpdate.getDependentChangeRequestIds();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestFordDependedntChangeRequestIds("dependent_change_request_ids", dependentChangeRequestIds,linkIds);
                /*break;

            default:
        }*/

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier,user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(patch(path)
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        ChangeRequest changeRequestAfterUpdate = optionalChangeRequest.get();

        Optional<ChangeRequest> optionalChangeRequestLinkId = changeRequestRepository.findById(linkId);
        assertThat(optionalChangeRequestLinkId.isPresent(), equalTo(true));
        ChangeRequest changeRequestLinkId = optionalChangeRequestLinkId.get();

        JsonNode updateRequest1 = null;
       /* switch (property){

            case "dependent_change_request_ids":*/
                List<String> dependentChangeRequestIds1 = changeRequestBeforeUpdate.getDependentChangeRequestIds();
                updateRequest1 = JsonNodeFactory.getRequestForDependedentChangeRequestIdsforUnLinkCR("value", dependentChangeRequestIds1,linkIds);
              /*  break;

            default:
        }*/

        String path1 = PathGenerator.getChangeRequestUnLinkCaseActionPath(id,"unlink-CR");

        MvcResult result1 = getMockMvc().perform(patch(path1)
                .content(objectMapper.writeValueAsString(updateRequest1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content1 = result1.getResponse().getContentAsString();

        Optional<ChangeRequest> optionalChangeRequest1 = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest1.isPresent(), equalTo(true));
        ChangeRequest changeRequestAfterUpdate1 = optionalChangeRequest1.get();

        Optional<ChangeRequest> optionalChangeRequestLinkId1 = changeRequestRepository.findById(linkId);
        assertThat(optionalChangeRequestLinkId1.isPresent(), equalTo(true));
        ChangeRequest changeRequestLinkId1 = optionalChangeRequestLinkId1.get();

        switch(expectedResult) {
            case "UNAUTHORIZED":
                assertThat("Response status code is as expected", result1.getResponse().getStatus(),equalTo(HttpStatus.FORBIDDEN.value()));
                ExceptionResponse exceptionResponse = new ExceptionResponse(content1);
                Validator.unauthorizedExceptionAndChangeRequestDidNotChange(changeRequestBeforeUpdate, changeRequestAfterUpdate1, exceptionResponse, path, changeRequestStatus.getStatusCode());
                break;
            case "AUTHORIZED":
                assertThat("Response status code is as expected", result1.getResponse().getStatus(),equalTo(HttpStatus.OK.value()));
              /*  ChangeRequestJson changeRequestJson = new ChangeRequestJson(content1);
                //TODO check for review json
                switch (property){

                    case "dependent_change_request_ids":*/
                        Validator.changeRequestAreSameWithoutComparingDependentChangeRequests(changeRequestBeforeUpdate, changeRequestAfterUpdate1);
                        assertThat("dependentChangeRequestsIds not same", changeRequestBeforeUpdate.getDependentChangeRequestIds(), Matchers.not(equalTo(changeRequestAfterUpdate.getDependentChangeRequestIds())));
                        assertThat("dependentChangeRequestsIds for LinkId CR", changeRequestAfterUpdate1.getDependentChangeRequestIds().size(),equalTo(0));
                        assertThat("dependentChangeRequestsIds for CR",  changeRequestLinkId1.getDependentChangeRequestIds().size(),equalTo(0));

/*
                        break;

                    default:
                }*/
                break;
        }
    }


    @ParameterizedTest(name = "{0} user updates property {1} when change request in {2} status, expect to be {3}")
    @CsvFileSource(resources = "/parameters/changerequest/ChangeRequestPropertyUpdate.csv", numLinesToSkip = 1)
    void userToUpdatePropertyOfChangeRequestInStatus(String user, String property, ChangeRequestStatus changeRequestStatus, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", changeRequestStatus,null);
        assert id!=null;

        String path = PathGenerator.getEntityUpdatePath(null, id);

        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        ChangeRequest changeRequestBeforeUpdate = optionalChangeRequest.get();

        JsonNode updateRequest = null;
        switch (property){
            case "title":
                String titleValue = changeRequestBeforeUpdate.getTitle();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, titleValue);
                break;
            case "is_secure":
                Boolean isSecure = changeRequestBeforeUpdate.getIsSecure();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForBoolean(property, isSecure);
                break;
            case "change_specialist1":
                User changeSpecialist1Value = changeRequestBeforeUpdate.getChangeSpecialist1();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForUser(property, changeSpecialist1Value);
              //  userServiceMockServer.mockUserServicePreferredRoles(updateRequest.get("newIns").get("change_specialist1").get("user_id").textValue());
                break;
            case "change_specialist2":
                User changeSpecialist2Value = changeRequestBeforeUpdate.getChangeSpecialist2();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForUser(property, changeSpecialist2Value);
               // userServiceMockServer.mockUserServicePreferredRoles(updateRequest.get("newIns").get("change_specialist2").get("user_id").textValue());
                break;
            case "change_control_boards":
                List<String> changeControlBoards = changeRequestBeforeUpdate.getChangeControlBoards();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForListOfString(property, changeControlBoards);
                break;
            case "change_boards":
                List<String> changeBoards = changeRequestBeforeUpdate.getChangeBoards();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForListOfString(property, changeBoards);
                break;
            case "issue_types":
                List<String> issueTypes = changeRequestBeforeUpdate.getIssueTypes();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForListOfString(property, issueTypes);
                break;
            case "change_request_type":
                String changeRequestType = changeRequestBeforeUpdate.getChangeRequestType();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, changeRequestType);
                break;
            case "analysis_priority":
                Integer analysisPriority = changeRequestBeforeUpdate.getAnalysisPriority();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForInteger(property, analysisPriority);
                break;
            case "product_id":
                String productId = changeRequestBeforeUpdate.getProductId();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, productId);
                break;
            case "functional_cluster_id":
                String functionalClusterId = changeRequestBeforeUpdate.getFunctionalClusterId();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, functionalClusterId);
                break;
            case "reasons_for_change":
                List<String> reasonsForChange = changeRequestBeforeUpdate.getReasonsForChange();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForListOfString(property, reasonsForChange);
                break;
            case "problem_description":
                String problemDescription = changeRequestBeforeUpdate.getProblemDescription();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, problemDescription);
                break;
            case "proposed_solution":
                String proposedSolution = changeRequestBeforeUpdate.getProposedSolution();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, proposedSolution);
                break;
            case "root_cause":
                String rootCause = changeRequestBeforeUpdate.getRootCause();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, rootCause);
                break;
            case "benefits_of_change":
                String benefitsOfChange = changeRequestBeforeUpdate.getBenefitsOfChange();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, benefitsOfChange);
                break;
            case "dependent_change_request_ids":
                List<String> dependentChangeRequestIds = changeRequestBeforeUpdate.getDependentChangeRequestIds();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForListOfString(property, dependentChangeRequestIds);
                break;

            case "implementation_priority":
                Integer implementationPriority = changeRequestBeforeUpdate.getImplementationPriority();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForInteger(property, implementationPriority);
                break;
            case "requirements_for_implementation_plan":
                String requirementsForImplementationPlan = changeRequestBeforeUpdate.getRequirementsForImplementationPlan();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, requirementsForImplementationPlan);
                break;

            case "change_owner_type":
                String changeOwnerType = changeRequestBeforeUpdate.getChangeOwnerType();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, changeOwnerType);
                break;
            default:
        }

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier,user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(patch(path)
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        ChangeRequest changeRequestAfterUpdate = optionalChangeRequest.get();

        switch(expectedResult) {
            case "UNAUTHORIZED":
                assertThat("Response status code is as expected", result.getResponse().getStatus(),equalTo(HttpStatus.FORBIDDEN.value()));
                ExceptionResponse exceptionResponse = new ExceptionResponse(content);
                Validator.unauthorizedExceptionAndChangeRequestDidNotChange(changeRequestBeforeUpdate, changeRequestAfterUpdate, exceptionResponse, path, changeRequestStatus.getStatusCode());
                break;
            case "AUTHORIZED":
                assertThat("Response status code is as expected", result.getResponse().getStatus(),equalTo(HttpStatus.OK.value()));
                ChangeRequestJson changeRequestJson = new ChangeRequestJson(content);
                //TODO check for review json
                switch (property){
                    case "title":
                        Validator.changeRequestAreSameWithoutComparingTitle(changeRequestBeforeUpdate, changeRequestAfterUpdate);
                        assertThat("title not same", changeRequestBeforeUpdate.getTitle(), Matchers.not(equalTo(changeRequestAfterUpdate.getTitle())));
                        break;
                    case "is_secure":
                        Validator.changeRequestAreSameWithoutComparingisSecure(changeRequestBeforeUpdate, changeRequestAfterUpdate);
                        assertThat("isSecure not same", changeRequestBeforeUpdate.getIsSecure(), Matchers.not(equalTo(changeRequestAfterUpdate.getIsSecure())));
                        break;
                    case "change_specialist1":
                        Validator.changeRequestAreSameWithoutComparingChangeSpecialist1(changeRequestBeforeUpdate, changeRequestAfterUpdate);
                        assertThat("change specialist1 not same", changeRequestBeforeUpdate.getChangeSpecialist1(), Matchers.not(equalTo(changeRequestAfterUpdate.getChangeSpecialist1())));
                        break;
                    case "change_specialist2":
                        Validator.changeRequestAreSameWithoutComparingChangeSpecialist2(changeRequestBeforeUpdate, changeRequestAfterUpdate);
                        assertThat("change specialist2 not same", changeRequestBeforeUpdate.getChangeSpecialist2(), Matchers.not(equalTo(changeRequestAfterUpdate.getChangeSpecialist2())));
                        break;
                    case "change_control_boards":
                        Validator.changeRequestAreSameWithoutComparingchangeControlBoards(changeRequestBeforeUpdate, changeRequestAfterUpdate);
                        assertThat("ChangeControlBoards not same", changeRequestBeforeUpdate.getChangeControlBoards(), Matchers.not(equalTo(changeRequestAfterUpdate.getChangeControlBoards())));
                        break;
                    case "change_boards":
                        Validator.changeRequestAreSameWithoutComparingChangeBoards(changeRequestBeforeUpdate, changeRequestAfterUpdate);
                        assertThat("changeBoards not same", changeRequestBeforeUpdate.getChangeBoards(), Matchers.not(equalTo(changeRequestAfterUpdate.getChangeBoards())));
                        break;
                    case "issue_types":
                        Validator.changeRequestAreSameWithoutComparingIssueTypes(changeRequestBeforeUpdate, changeRequestAfterUpdate);
                        assertThat("IssueTypes not same", changeRequestBeforeUpdate.getIssueTypes(), Matchers.not(equalTo(changeRequestAfterUpdate.getIssueTypes())));
                        break;
                    case "change_request_type":
                        Validator.changeRequestAreSameWithoutComparingChangeRequestType(changeRequestBeforeUpdate, changeRequestAfterUpdate);
                        assertThat("ChangeRequestType not same", changeRequestBeforeUpdate.getChangeRequestType(), Matchers.not(equalTo(changeRequestAfterUpdate.getChangeRequestType())));
                        break;
                    case "analysis_priority":
                        Validator.changeRequestAreSameWithoutComparingAnalysisPriority(changeRequestBeforeUpdate, changeRequestAfterUpdate);
                        assertThat("AnalysisPriority not same", changeRequestBeforeUpdate.getAnalysisPriority(), Matchers.not(equalTo(changeRequestAfterUpdate.getAnalysisPriority())));
                        break;

                    case "product_id":
                        Validator.changeRequestAreSameWithoutComparingProductId(changeRequestBeforeUpdate, changeRequestAfterUpdate);
                        assertThat("ProductId not same", changeRequestBeforeUpdate.getProductId(), Matchers.not(equalTo(changeRequestAfterUpdate.getProductId())));
                        break;
                    case "functional_cluster_id":
                        Validator.changeRequestAreSameWithoutComparingFunctionalClusterId(changeRequestBeforeUpdate, changeRequestAfterUpdate);
                        assertThat("ProductId not same", changeRequestBeforeUpdate.getFunctionalClusterId(), Matchers.not(equalTo(changeRequestAfterUpdate.getFunctionalClusterId())));
                        break;
                    case "problem_description":
                        Validator.changeRequestAreSameWithoutComparingProblemDescription(changeRequestBeforeUpdate, changeRequestAfterUpdate);
                        assertThat("problem description not same", changeRequestBeforeUpdate.getProblemDescription(), Matchers.not(equalTo(changeRequestAfterUpdate.getProblemDescription())));
                        break;
                    case "reasons_for_change":
                        Validator.changeRequestAreSameWithoutComparingReasonsForChange(changeRequestBeforeUpdate, changeRequestAfterUpdate);
                        assertThat("reasons for change not same", changeRequestBeforeUpdate.getReasonsForChange(), Matchers.not(equalTo(changeRequestAfterUpdate.getReasonsForChange())));
                        break;
                    case "proposed_solution":
                        Validator.changeRequestAreSameWithoutComparingProposedSolution(changeRequestBeforeUpdate, changeRequestAfterUpdate);
                        assertThat("proposed solution not same", changeRequestBeforeUpdate.getProposedSolution(), Matchers.not(equalTo(changeRequestAfterUpdate.getProposedSolution())));
                        break;
                    case "root_cause":
                        Validator.changeRequestAreSameWithoutComparingRootCause(changeRequestBeforeUpdate, changeRequestAfterUpdate);
                        assertThat("RootCause not same", changeRequestBeforeUpdate.getRootCause(), Matchers.not(equalTo(changeRequestAfterUpdate.getRootCause())));
                        break;
                    case "benefits_of_change":
                        Validator.changeRequestAreSameWithoutComparingBenefitsOfChange(changeRequestBeforeUpdate, changeRequestAfterUpdate);
                        assertThat("BenefitsOfChange not same", changeRequestBeforeUpdate.getBenefitsOfChange(), Matchers.not(equalTo(changeRequestAfterUpdate.getBenefitsOfChange())));
                        break;
                    case "dependent_change_request_ids":
                        Validator.changeRequestAreSameWithoutComparingDependentChangeRequests(changeRequestBeforeUpdate, changeRequestAfterUpdate);
                        assertThat("dependentChangeRequestsIds not same", changeRequestBeforeUpdate.getDependentChangeRequestIds(), Matchers.not(equalTo(changeRequestAfterUpdate.getDependentChangeRequestIds())));
                        break;
                    case "implementation_priority":
                        Validator.changeRequestAreSameWithoutComparingImplementationPriority(changeRequestBeforeUpdate, changeRequestAfterUpdate);
                        assertThat("ImplementationPriority not same", changeRequestBeforeUpdate.getImplementationPriority(), Matchers.not(equalTo(changeRequestAfterUpdate.getImplementationPriority())));
                        break;
                    case "requirements_for_implementation_plan":
                        Validator.changeRequestAreSameWithoutComparingRequirementsForImplementationPlan(changeRequestBeforeUpdate, changeRequestAfterUpdate);
                        assertThat("RequirementsForImplementationPlan not same", changeRequestBeforeUpdate.getRequirementsForImplementationPlan(), Matchers.not(equalTo(changeRequestAfterUpdate.getRequirementsForImplementationPlan())));
                        break;

                    case "change_owner_type":
                        Validator.changeRequestAreSameWithoutComparingChangeOwnerType(changeRequestBeforeUpdate, changeRequestAfterUpdate);
                        assertThat("ChangeOwnerType not same", changeRequestBeforeUpdate.getChangeOwnerType(), Matchers.not(equalTo(changeRequestAfterUpdate.getChangeOwnerType())));
                        break;

                    default:
                }
                break;
        }
    }


    @ParameterizedTest(name = "{0} user updates property {1} when change request in {2} status, expect to be {3}")
    @CsvFileSource(resources = "/parameters/changerequest/SolutionDefinitionPropertyUpdate.csv", numLinesToSkip = 1)
    void userToUpdatePropertyOfSolutionDefinitionInStatus(String user, String property, ChangeRequestStatus changeRequestStatus, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", changeRequestStatus,null);
        assert id!=null;
        Long solutionDefinitionId = entityInstanceManager.getSolutionDefinitionIdByChangeRequestId(id);
        String path = PathGenerator.getEntityUpdatePath("solution-definition", solutionDefinitionId);

        Optional<SolutionDefinition> optionalsolutionDefinition = solutionDefinitionRepository.findById(solutionDefinitionId);
        assertThat(optionalsolutionDefinition.isPresent(), equalTo(true));
        SolutionDefinition solutionDefinitionBeforeUpdate = optionalsolutionDefinition.get();

        JsonNode updateRequest = null;
        switch (property){
            case "test_and_release_strategy_details":
                String testAndReleaseStrategyDetails = solutionDefinitionBeforeUpdate.getTestAndReleaseStrategyDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, testAndReleaseStrategyDetails);
                break;
            case "products_affected":
                List<String> productsAffected = solutionDefinitionBeforeUpdate.getProductsAffected();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForListOfString(property, productsAffected);
                break;
            case "aligned_with_fo_details":
                String alignedWithFODetails = solutionDefinitionBeforeUpdate.getAlignedWithFoDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, alignedWithFODetails);
                break;
            case "technical_recommendation":
                String technicalRecommendation = solutionDefinitionBeforeUpdate.getTechnicalRecommendation();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, technicalRecommendation);
                break;
            case "products_module_affected":
                String productsModuleAffected = solutionDefinitionBeforeUpdate.getProductsModuleAffected();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, productsModuleAffected);
                break;
            case "functional_software_dependencies_details":
                String functionalSoftwareDependenciesDetails = solutionDefinitionBeforeUpdate.getFunctionalSoftwareDependenciesDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, functionalSoftwareDependenciesDetails);
                break;
            case "functional_hardware_dependencies_details":
                String functionalHardwareDependenciesDetails = solutionDefinitionBeforeUpdate.getFunctionalHardwareDependenciesDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, functionalHardwareDependenciesDetails);
                break;
            case "hardware_software_dependencies_aligned_details":
                String hardwareSoftwareDependenciesAlignedDetails = solutionDefinitionBeforeUpdate.getHardwareSoftwareDependenciesAlignedDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, hardwareSoftwareDependenciesAlignedDetails);
                break;

            default:
        }

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier,user);

        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(patch(path)
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        optionalsolutionDefinition = solutionDefinitionRepository.findById(solutionDefinitionId);
        assertThat(optionalsolutionDefinition.isPresent(), equalTo(true));
        SolutionDefinition solutionDefinitionAfterUpdate = optionalsolutionDefinition.get();

        switch(expectedResult) {
            case "UNAUTHORIZED":
                assertThat("Response status code is as expected", result.getResponse().getStatus(),equalTo(HttpStatus.FORBIDDEN.value()));
                ExceptionResponse exceptionResponse = new ExceptionResponse(content);
                Validator.unauthorizedExceptionAndSolutionDefinitionDidNotChange(solutionDefinitionBeforeUpdate, solutionDefinitionAfterUpdate, exceptionResponse, path, changeRequestStatus.getStatusCode());
                break;
            case "AUTHORIZED":
                assertThat("Response status code is as expected", result.getResponse().getStatus(),equalTo(HttpStatus.OK.value()));
                ChangeRequestJson changeRequestJson = new ChangeRequestJson(content);
                //TODO check for review json
                switch (property){
                    case "test_and_release_strategy_details":
                        Validator.solutionDefinitionAreSameWithoutComparingTestAndReleaseStrategyDetails(solutionDefinitionBeforeUpdate, solutionDefinitionAfterUpdate);
                        assertThat("TestAndReleaseStrategyDetails not same", solutionDefinitionBeforeUpdate.getTestAndReleaseStrategyDetails(), Matchers.not(equalTo(solutionDefinitionAfterUpdate.getTestAndReleaseStrategyDetails())));
                        break;
                    case "products_affected":
                        Validator.solutionDefinitionAreSameWithoutComparingProductsAffected(solutionDefinitionBeforeUpdate, solutionDefinitionAfterUpdate);
                        assertThat("ProductsAffected not same", solutionDefinitionBeforeUpdate.getProductsAffected(), Matchers.not(equalTo(solutionDefinitionAfterUpdate.getProductsAffected())));
                        break;
                    case "aligned_with_fo_details":
                        Validator.solutionDefinitionAreSameWithoutComparingAlignedWithFODetails(solutionDefinitionBeforeUpdate, solutionDefinitionAfterUpdate);
                        assertThat("AlignedWithFODetails not same", solutionDefinitionBeforeUpdate.getAlignedWithFoDetails(), Matchers.not(equalTo(solutionDefinitionAfterUpdate.getAlignedWithFoDetails())));
                        break;
                    case "technical_recommendation":
                        Validator.solutionDefinitionAreSameWithoutComparingTechnicalRecommendation(solutionDefinitionBeforeUpdate, solutionDefinitionAfterUpdate);
                        assertThat("TechnicalRecommendation not same", solutionDefinitionBeforeUpdate.getTechnicalRecommendation(), Matchers.not(equalTo(solutionDefinitionAfterUpdate.getTechnicalRecommendation())));
                        break;
                    case "products_module_affected":
                        Validator.solutionDefinitionAreSameWithoutComparingProductsModuleAffected(solutionDefinitionBeforeUpdate, solutionDefinitionAfterUpdate);
                        assertThat("TechnicalRecommendation not same", solutionDefinitionBeforeUpdate.getProductsModuleAffected(), Matchers.not(equalTo(solutionDefinitionAfterUpdate.getProductsModuleAffected())));
                        break;
                    case "functional_software_dependencies_details":
                        Validator.solutionDefinitionAreSameWithoutComparingFunctionalSoftwareDependenciesDetails(solutionDefinitionBeforeUpdate, solutionDefinitionAfterUpdate);
                        assertThat("FunctionalSoftwareDependenciesDetails not same", solutionDefinitionBeforeUpdate.getFunctionalSoftwareDependenciesDetails(), Matchers.not(equalTo(solutionDefinitionAfterUpdate.getFunctionalSoftwareDependenciesDetails())));
                        break;
                    case "functional_hardware_dependencies_details":
                        Validator.solutionDefinitionAreSameWithoutComparingFunctionalHardwareDependenciesDetails(solutionDefinitionBeforeUpdate, solutionDefinitionAfterUpdate);
                        assertThat("FunctionalHardwareDependenciesDetails not same", solutionDefinitionBeforeUpdate.getFunctionalHardwareDependenciesDetails(), Matchers.not(equalTo(solutionDefinitionAfterUpdate.getFunctionalHardwareDependenciesDetails())));
                        break;
                    case "hardware_software_dependencies_aligned_details":
                        Validator.solutionDefinitionAreSameWithoutComparingHardwareSoftwareDependenciesAlignedDetails(solutionDefinitionBeforeUpdate, solutionDefinitionAfterUpdate);
                        assertThat("HardwareSoftwareDependenciesAlignedDetails not same", solutionDefinitionBeforeUpdate.getHardwareSoftwareDependenciesAlignedDetails(), Matchers.not(equalTo(solutionDefinitionAfterUpdate.getHardwareSoftwareDependenciesAlignedDetails())));
                        break;
                    default:
                }
                break;
        }
    }


    @ParameterizedTest(name = "{0} user updates property {1} when change request in {2} status, expect to be {3}")
    @CsvFileSource(resources = "/parameters/changerequest/CustomerImpactPropertyUpdate.csv", numLinesToSkip = 1)
    void userToUpdatePropertyOfCustormerImpactInStatus(String user, String property, ChangeRequestStatus changeRequestStatus, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", changeRequestStatus,null);
        assert id!=null;
        Long customerImpactId = entityInstanceManager.getCustomerImpactIdByChangeRequestId(id);
        String path = PathGenerator.getEntityUpdatePath("customer-impact", customerImpactId);

        Optional<CustomerImpact> optionalcustomerImpact = customerImpactRepository.findById(customerImpactId);
        assertThat(optionalcustomerImpact.isPresent(), equalTo(true));
        CustomerImpact customerImpactBeforeUpdate = optionalcustomerImpact.get();

        JsonNode updateRequest = null;
        switch (property){
            case "customer_approval_details":
                String customerApprovalDetails = customerImpactBeforeUpdate.getCustomerApprovalDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, customerApprovalDetails);
                break;
            case "customer_communication_details":
                String customerCommunicationDetails = customerImpactBeforeUpdate.getCustomerCommunicationDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, customerCommunicationDetails);
                break;
            case "impact_on_user_interfaces_details":
                String impactOnUserInterfacesDetails = customerImpactBeforeUpdate.getImpactOnUserInterfacesDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, impactOnUserInterfacesDetails);
                break;
            case "impact_on_wafer_process_environment_details":
                String impactOnWaferProcessEnvironmentDetails = customerImpactBeforeUpdate.getImpactOnWaferProcessEnvironmentDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, impactOnWaferProcessEnvironmentDetails);
                break;
            case "change_to_customer_impact_critical_part_details":
                String customerImpactCriticalPartDetails = customerImpactBeforeUpdate.getChangeToCustomerImpactCriticalPartDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, customerImpactCriticalPartDetails);
                break;
            case "change_to_process_impacting_customer_details":
                String changeToProcessImpactingCustomerDetails = customerImpactBeforeUpdate.getChangeToProcessImpactingCustomerDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, changeToProcessImpactingCustomerDetails);
                break;
            case "fco_upgrade_option_csr_implementation_change_details":
                String fcoUpgradeOptionCsrImplementationChangeDetails = customerImpactBeforeUpdate.getFcoUpgradeOptionCsrImplementationChangeDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, fcoUpgradeOptionCsrImplementationChangeDetails);
                break;

            default:
        }

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier,user);

        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(patch(path)
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        optionalcustomerImpact = customerImpactRepository.findById(customerImpactId);
        assertThat(optionalcustomerImpact.isPresent(), equalTo(true));
        CustomerImpact customerImpactAfterUpdate = optionalcustomerImpact.get();

        switch(expectedResult) {
            case "UNAUTHORIZED":
                assertThat("Response status code is as expected", result.getResponse().getStatus(),equalTo(HttpStatus.FORBIDDEN.value()));
                ExceptionResponse exceptionResponse = new ExceptionResponse(content);
                Validator.unauthorizedExceptionAndCustomerImpactDidNotChange(customerImpactBeforeUpdate, customerImpactAfterUpdate, exceptionResponse, path, changeRequestStatus.getStatusCode());
                break;
            case "AUTHORIZED":
                assertThat("Response status code is as expected", result.getResponse().getStatus(),equalTo(HttpStatus.OK.value()));
                ChangeRequestJson changeRequestJson = new ChangeRequestJson(content);
                //TODO check for review json
                switch (property){
                    case "customer_approval_details":
                        Validator.customerImpactAreSameWithoutComparingCustomerApprovalDetails(customerImpactBeforeUpdate, customerImpactAfterUpdate);
                        assertThat("CustomerApprovalDetails not same", customerImpactBeforeUpdate.getCustomerApprovalDetails(), Matchers.not(equalTo(customerImpactAfterUpdate.getCustomerApprovalDetails())));
                        break;
                    case "customer_communication_details":
                        Validator.customerImpactAreSameWithoutComparingCustomerCommunicationDetails(customerImpactBeforeUpdate, customerImpactAfterUpdate);
                        assertThat("CustomerCommunicationDetails not same", customerImpactBeforeUpdate.getCustomerCommunicationDetails(), Matchers.not(equalTo(customerImpactAfterUpdate.getCustomerCommunicationDetails())));
                        break;
                    case "impact_on_user_interfaces_details":
                        Validator.customerImpactAreSameWithoutComparingImpactOnUserInterfacesDetails(customerImpactBeforeUpdate, customerImpactAfterUpdate);
                        assertThat("ImpactOnUserInterfacesDetails not same", customerImpactBeforeUpdate.getImpactOnUserInterfacesDetails(), Matchers.not(equalTo(customerImpactAfterUpdate.getImpactOnUserInterfacesDetails())));
                        break;
                    case "impact_on_wafer_process_environment_details":
                        Validator.customerImpactAreSameWithoutComparingImpactOnWaferProcessEnvironmentDetails(customerImpactBeforeUpdate, customerImpactAfterUpdate);
                        assertThat("ImpactOnWaferProcessEnvironmentDetails not same", customerImpactBeforeUpdate.getImpactOnWaferProcessEnvironmentDetails(), Matchers.not(equalTo(customerImpactAfterUpdate.getImpactOnWaferProcessEnvironmentDetails())));
                        break;
                    case "change_to_customer_impact_critical_part_details":
                        Validator.customerImpactAreSameWithoutComparingChangeToCustomerImpactCriticalPartDetails(customerImpactBeforeUpdate, customerImpactAfterUpdate);
                        assertThat("ChangeToCustomerImpactCriticalPartDetails not same", customerImpactBeforeUpdate.getChangeToCustomerImpactCriticalPartDetails(), Matchers.not(equalTo(customerImpactAfterUpdate.getChangeToCustomerImpactCriticalPartDetails())));
                        break;
                    case "change_to_process_impacting_customer_details":
                        Validator.customerImpactAreSameWithoutComparingChangeToProcessImpactingCustomerDetails(customerImpactBeforeUpdate, customerImpactAfterUpdate);
                        assertThat("ChangeToProcessImpactingCustomerDetails not same", customerImpactBeforeUpdate.getChangeToProcessImpactingCustomerDetails(), Matchers.not(equalTo(customerImpactAfterUpdate.getChangeToProcessImpactingCustomerDetails())));
                        break;
                    case "fco_upgrade_option_csr_implementation_change_details":
                        Validator.customerImpactAreSameWithoutComparingFcoUpgradeOptionCsrImplementationChangeDetails(customerImpactBeforeUpdate, customerImpactAfterUpdate);
                        assertThat("FcoUpgradeOptionCsrImplementationChangeDetails not same", customerImpactBeforeUpdate.getFcoUpgradeOptionCsrImplementationChangeDetails(), Matchers.not(equalTo(customerImpactAfterUpdate.getFcoUpgradeOptionCsrImplementationChangeDetails())));
                        break;

                    default:
                }
                break;
        }
    }

    @ParameterizedTest(name = "{0} user updates property {1} when change request in {2} status, expect to be {3}")
    @CsvFileSource(resources = "/parameters/changerequest/PreinstallImpactPropertyUpdate.csv", numLinesToSkip = 1)
    void userToUpdatePropertyOfPreinstallImpactInStatus(String user, String property, ChangeRequestStatus changeRequestStatus, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", changeRequestStatus,null);
        assert id!=null;
        Long preinstallImpactId = entityInstanceManager.getPreinstallImpactIdByChangeRequestId(id);
        String path = PathGenerator.getEntityUpdatePath("preinstall-impact", preinstallImpactId);
        Optional<PreinstallImpact> optionalPreinstallImpact = preinstallImpactRepository.findById(preinstallImpactId);
        assertThat(optionalPreinstallImpact.isPresent(), equalTo(true));
        PreinstallImpact preinstallImpactBeforeUpdate = optionalPreinstallImpact.get();

        JsonNode updateRequest = null;
        switch (property){
            case "preinstall_impact_result":
                String preinstallImpactResult = preinstallImpactBeforeUpdate.getPreinstallImpactResult();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, preinstallImpactResult);
                break;
            case "change_introduces_new11_nc_details":
                String changeIntroducesNew11NCDetails = preinstallImpactBeforeUpdate.getChangeIntroducesNew11NcDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, changeIntroducesNew11NCDetails);
                break;
            case "impact_on_facility_flows_details":
                String impactOnFacilityFlowsDetails = preinstallImpactBeforeUpdate.getImpactOnFacilityFlowsDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, impactOnFacilityFlowsDetails);
                break;
            case "impact_on_customer_factory_layout_details":
                String impactOnCustomerFactoryLayoutDetails = preinstallImpactBeforeUpdate.getImpactOnCustomerFactoryLayoutDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, impactOnCustomerFactoryLayoutDetails);
                break;
            case "impact_on_preinstall_inter_connect_cables_details":
                String impactOnPreinstallInterConnectCablesDetails = preinstallImpactBeforeUpdate.getImpactOnPreinstallInterConnectCablesDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, impactOnPreinstallInterConnectCablesDetails);
                break;
            case "change_replaces_mentioned_parts_details":
                String changeReplacesMentionedPartsDetails = preinstallImpactBeforeUpdate.getChangeReplacesMentionedPartsDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, changeReplacesMentionedPartsDetails);
                break;


            default:
        }

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier,user);

        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(patch(path)
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        optionalPreinstallImpact = preinstallImpactRepository.findById(preinstallImpactId);
        assertThat(optionalPreinstallImpact.isPresent(), equalTo(true));
        PreinstallImpact preinstallImpactAfterUpdate = optionalPreinstallImpact.get();

        switch(expectedResult) {
            case "UNAUTHORIZED":
                assertThat("Response status code is as expected", result.getResponse().getStatus(),equalTo(HttpStatus.FORBIDDEN.value()));
                ExceptionResponse exceptionResponse = new ExceptionResponse(content);
                Validator.unauthorizedExceptionAndPreinstallImpactDidNotChange(preinstallImpactBeforeUpdate, preinstallImpactAfterUpdate, exceptionResponse, path, changeRequestStatus.getStatusCode());
                break;
            case "AUTHORIZED":
                assertThat("Response status code is as expected", result.getResponse().getStatus(),equalTo(HttpStatus.OK.value()));
                ChangeRequestJson changeRequestJson = new ChangeRequestJson(content);
                //TODO check for review json
                switch (property){
                    case "preinstall_impact_result":
                        Validator.customerImpactAreSameWithoutComparinggetPreinstallImpactResult(preinstallImpactBeforeUpdate, preinstallImpactAfterUpdate);
                        assertThat("CustomerApprovalDetails not same", preinstallImpactBeforeUpdate.getPreinstallImpactResult(), Matchers.not(equalTo(preinstallImpactAfterUpdate.getPreinstallImpactResult())));
                        break;
                    case "change_introduces_new11_nc_details":
                        Validator.customerImpactAreSameWithoutComparingChangeIntroducesNew11NCDetails(preinstallImpactBeforeUpdate, preinstallImpactAfterUpdate);
                        assertThat("ChangeIntroducesNew11NCDetails not same", preinstallImpactBeforeUpdate.getChangeIntroducesNew11NcDetails(), Matchers.not(equalTo(preinstallImpactAfterUpdate.getChangeIntroducesNew11NcDetails())));
                        break;
                    case "impact_on_customer_factory_layout_details":
                        Validator.customerImpactAreSameWithoutComparinImpactOnCustomerFactoryLayoutDetails(preinstallImpactBeforeUpdate, preinstallImpactAfterUpdate);
                        assertThat("ImpactOnCustomerFactoryLayoutDetails not same", preinstallImpactBeforeUpdate.getImpactOnCustomerFactoryLayoutDetails(), Matchers.not(equalTo(preinstallImpactAfterUpdate.getImpactOnCustomerFactoryLayoutDetails())));
                        break;
                    case "impact_on_facility_flows_details":
                        Validator.customerImpactAreSameWithoutComparingImpactOnFacilityFlowsDetails(preinstallImpactBeforeUpdate, preinstallImpactAfterUpdate);
                        assertThat("ImpactOnFacilityFlowsDetails not same", preinstallImpactBeforeUpdate.getImpactOnFacilityFlowsDetails(), Matchers.not(equalTo(preinstallImpactAfterUpdate.getImpactOnFacilityFlowsDetails())));
                        break;
                    case "impact_on_preinstall_inter_connect_cables_details":
                        Validator.customerImpactAreSameWithoutComparingimpactOnPreinstallInterConnectCablesDetails(preinstallImpactBeforeUpdate, preinstallImpactAfterUpdate);
                        assertThat("impactOnPreinstallInterConnectCablesDetails not same", preinstallImpactBeforeUpdate.getImpactOnPreinstallInterConnectCablesDetails(), Matchers.not(equalTo(preinstallImpactAfterUpdate.getImpactOnPreinstallInterConnectCablesDetails())));
                        break;
                    case "change_replaces_mentioned_parts_details":
                        Validator.customerImpactAreSameWithoutComparingChangeReplacesMentionedPartsDetails(preinstallImpactBeforeUpdate, preinstallImpactAfterUpdate);
                        assertThat("changeReplacesMentionedPartsDetails not same", preinstallImpactBeforeUpdate.getChangeReplacesMentionedPartsDetails(), Matchers.not(equalTo(preinstallImpactAfterUpdate.getChangeReplacesMentionedPartsDetails())));
                        break;

                    default:
                }
                break;
        }
    }

    @ParameterizedTest(name = "{0} user updates property {1} when change request in {2} status, expect to be {3}")
    @CsvFileSource(resources = "/parameters/changerequest/ImpactAnalysisPropertyUpdate.csv", numLinesToSkip = 1)
    void userToUpdatePropertyOfImpactAnalysisInStatus(String user, String property, ChangeRequestStatus changeRequestStatus, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", changeRequestStatus,null);
        assert id!=null;
        Long impactAnalysisId = entityInstanceManager.getImpactAnalysisIdByChangeRequestId(id);
        String path = PathGenerator.getEntityUpdatePath("impact-analysis", impactAnalysisId);

        Optional<ImpactAnalysis> optionalImpactAnalysis = impactAnalysisRepository.findById(impactAnalysisId);
        assertThat(optionalImpactAnalysis.isPresent(), equalTo(true));
        ImpactAnalysis impactAnalysisBeforeUpdate = optionalImpactAnalysis.get();

        JsonNode updateRequest = null;
        switch (property){
            case "cbp_strategies_details":
                String cbpStrategiesDetails = impactAnalysisBeforeUpdate.getCbpStrategiesDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, cbpStrategiesDetails);
                break;
            case "impact_on_sequence_details":
                String impactOnSequenceDetails = impactAnalysisBeforeUpdate.getImpactOnSequenceDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, impactOnSequenceDetails);
                break;
            case "impact_on_availability_details":
                String impactOnAvailabilityDetails = impactAnalysisBeforeUpdate.getImpactOnAvailabilityDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, impactOnAvailabilityDetails);
                break;
            case "phase_out_spares_tools_details":
                String phaseOutSparesToolsDetails = impactAnalysisBeforeUpdate.getPhaseOutSparesToolsDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, phaseOutSparesToolsDetails);
                break;
            case "tech_risk_assessment_sra_details":
                String techRiskAssessmentSraDetails = impactAnalysisBeforeUpdate.getTechRiskAssessmentSraDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, techRiskAssessmentSraDetails);
                break;
            case "tech_risk_assessment_fmea_details":
                String techRiskAssessmentFmeaDetails = impactAnalysisBeforeUpdate.getTechRiskAssessmentFmeaDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, techRiskAssessmentFmeaDetails);
                break;
            case "total_instances_affected":
                String totalInstancesAffected = impactAnalysisBeforeUpdate.getTotalInstancesAffected();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, totalInstancesAffected);
                break;
            case "impact_on_system_level_performance_details":
                String ImpactOnSystemLevelPerformanceDetails = impactAnalysisBeforeUpdate.getImpactOnSystemLevelPerformanceDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, ImpactOnSystemLevelPerformanceDetails);
                break;
            case "impact_on_cycle_time_details":
                String impactOnCycleTimeDetails = impactAnalysisBeforeUpdate.getImpactOnCycleTimeDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, impactOnCycleTimeDetails);
                break;
            case "liability_risks":
                List<String> liabilityRisks = impactAnalysisBeforeUpdate.getLiabilityRisks();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForListOfString(property, liabilityRisks);
                break;
            case "implementation_ranges_details":
               String implementationRangesDetails = impactAnalysisBeforeUpdate.getImplementationRangesDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, implementationRangesDetails);
                break;
            case "fco_types":
                List<String> fcoTypes = impactAnalysisBeforeUpdate.getFcoTypes();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForListOfString(property, fcoTypes);
                break;
            default:
        }

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier,user);

        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(patch(path)
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        optionalImpactAnalysis = impactAnalysisRepository.findById(impactAnalysisId);
        assertThat(optionalImpactAnalysis.isPresent(), equalTo(true));
        ImpactAnalysis impactAnalysisAfterUpdate = optionalImpactAnalysis.get();

        switch(expectedResult) {
            case "UNAUTHORIZED":
                assertThat("Response status code is as expected", result.getResponse().getStatus(),equalTo(HttpStatus.FORBIDDEN.value()));
                ExceptionResponse exceptionResponse = new ExceptionResponse(content);
                Validator.unauthorizedExceptionAndimpactAnalysisDidNotChange(impactAnalysisBeforeUpdate, impactAnalysisAfterUpdate, exceptionResponse, path, changeRequestStatus.getStatusCode());
                break;
            case "AUTHORIZED":
                assertThat("Response status code is as expected", result.getResponse().getStatus(),equalTo(HttpStatus.OK.value()));
                ChangeRequestJson changeRequestJson = new ChangeRequestJson(content);

                switch (property){
                    case "cbp_strategies_details":
                        Validator.impactAnalysisAreSameWithoutComparingCbpStrategiesDetails(impactAnalysisBeforeUpdate, impactAnalysisAfterUpdate);
                        assertThat("CbpStrategiesDetails not same", impactAnalysisBeforeUpdate.getCbpStrategiesDetails(), Matchers.not(equalTo(impactAnalysisAfterUpdate.getCbpStrategiesDetails())));
                        break;
                    case "impact_on_sequence_details":
                        Validator.impactAnalysisAreSameWithoutComparingImpactOnSequenceDetails(impactAnalysisBeforeUpdate, impactAnalysisAfterUpdate);
                        assertThat("ImpactOnSequenceDetails not same", impactAnalysisBeforeUpdate.getImpactOnSequenceDetails(), Matchers.not(equalTo(impactAnalysisAfterUpdate.getImpactOnSequenceDetails())));
                        break;
                    case "impact_on_availability_details":
                        Validator.impactAnalysisAreSameWithoutComparingImpactOnAvailabilityDetails(impactAnalysisBeforeUpdate, impactAnalysisAfterUpdate);
                        assertThat("ImpactOnAvailabilityDetails not same", impactAnalysisBeforeUpdate.getImpactOnAvailabilityDetails(), Matchers.not(equalTo(impactAnalysisAfterUpdate.getImpactOnAvailabilityDetails())));
                        break;
                    case "phase_out_spares_tools_details":
                        Validator.impactAnalysisAreSameWithoutComparingPhaseOutSparesToolsDetails(impactAnalysisBeforeUpdate, impactAnalysisAfterUpdate);
                        assertThat("PhaseOutSparesToolsDetails not same", impactAnalysisBeforeUpdate.getPhaseOutSparesToolsDetails(), Matchers.not(equalTo(impactAnalysisAfterUpdate.getPhaseOutSparesToolsDetails())));
                        break;
                    case "tech_risk_assessment_sra_details":
                        Validator.impactAnalysisAreSameWithoutComparingTechRiskAssessmentSraDetails(impactAnalysisBeforeUpdate, impactAnalysisAfterUpdate);
                        assertThat("TechRiskAssessmentSraDetails not same", impactAnalysisBeforeUpdate.getTechRiskAssessmentSraDetails(), Matchers.not(equalTo(impactAnalysisAfterUpdate.getTechRiskAssessmentSraDetails())));
                        break;
                    case "tech_risk_assessment_fmea_details":
                        Validator.impactAnalysisAreSameWithoutComparingTechRiskAssessmentFmeaDetails(impactAnalysisBeforeUpdate, impactAnalysisAfterUpdate);
                        assertThat("TechRiskAssessmentFmeaDetails not same", impactAnalysisBeforeUpdate.getTechRiskAssessmentFmeaDetails(), Matchers.not(equalTo(impactAnalysisAfterUpdate.getTechRiskAssessmentFmeaDetails())));
                        break;
                    case "total_instances_affected":
                        Validator.impactAnalysisAreSameWithoutComparingTotalInstancesAffected(impactAnalysisBeforeUpdate, impactAnalysisAfterUpdate);
                        assertThat("TotalInstancesAffected not same", impactAnalysisBeforeUpdate.getTotalInstancesAffected(), Matchers.not(equalTo(impactAnalysisAfterUpdate.getTotalInstancesAffected())));
                        break;
                    case "impact_on_system_level_performance_details":
                        Validator.impactAnalysisAreSameWithoutComparingImpactOnSystemLevelPerformanceDetails(impactAnalysisBeforeUpdate, impactAnalysisAfterUpdate);
                        assertThat("ImpactOnSystemLevelPerformanceDetails not same", impactAnalysisBeforeUpdate.getImpactOnSystemLevelPerformanceDetails(), Matchers.not(equalTo(impactAnalysisAfterUpdate.getImpactOnSystemLevelPerformanceDetails())));
                        break;
                    case "impact_on_cycle_time_details":
                        Validator.impactAnalysisAreSameWithoutComparingImpactOnCycleTimeDetails(impactAnalysisBeforeUpdate, impactAnalysisAfterUpdate);
                        assertThat("ImpactOnCycleTimeDetails not same", impactAnalysisBeforeUpdate.getImpactOnCycleTimeDetails(), Matchers.not(equalTo(impactAnalysisAfterUpdate.getImpactOnCycleTimeDetails())));
                        break;
                    case "liability_risks":
                        Validator.impactAnalysisAreSameWithoutComparingImpactOnCycleTimeDetails(impactAnalysisBeforeUpdate, impactAnalysisAfterUpdate);
                        assertThat("LiabilityRisks not same", impactAnalysisBeforeUpdate.getLiabilityRisks(), Matchers.not(equalTo(impactAnalysisAfterUpdate.getLiabilityRisks())));
                        break;

                    case "implementation_ranges_details":
                        Validator.impactAnalysisAreSameWithoutComparingImplementationRangesDetails(impactAnalysisBeforeUpdate, impactAnalysisAfterUpdate);
                        assertThat("ImplementationRangesDetails not same", impactAnalysisBeforeUpdate.getImplementationRangesDetails(), Matchers.not(equalTo(impactAnalysisAfterUpdate.getImplementationRangesDetails())));
                        break;
                    case "fco_types":
                        Validator.impactAnalysisAreSameWithoutComparingFcoTypes(impactAnalysisBeforeUpdate, impactAnalysisAfterUpdate);
                        assertThat("FcoTypes not same", impactAnalysisBeforeUpdate.getFcoTypes(), Matchers.not(equalTo(impactAnalysisAfterUpdate.getFcoTypes())));
                        break;

                    default:
                }
                break;
        }
    }

    @ParameterizedTest(name = "{0} user updates property {1} when change request in {2} status, expect to be {3}")
    @CsvFileSource(resources = "/parameters/changerequest/CompleteBusinessCasePropertyUpdate.csv", numLinesToSkip = 1)
    void userToUpdatePropertyOfCompleteBusinessCaseInStatus(String user, String property, ChangeRequestStatus changeRequestStatus, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", changeRequestStatus,null);
        assert changeRequestId!=null;

        Long completeBusinessCaseId = entityInstanceManager.getCompleteBusinessCaseIdByChangeRequestId(changeRequestId);
        String path = PathGenerator.getEntityUpdatePath("complete-business-case", completeBusinessCaseId);

        Optional<CompleteBusinessCase> optionalCompleteBusinessCase = completeBusinessCaseRepository.findById(completeBusinessCaseId);
        assertThat(optionalCompleteBusinessCase.isPresent(), equalTo(true));
        CompleteBusinessCase completeBusinessCaseBeforeUpdate = optionalCompleteBusinessCase.get();

        JsonNode updateRequest = null;
        switch (property){
            case "system_starts_impacted":
                Integer systemStartsImpacted = completeBusinessCaseBeforeUpdate.getSystemStartsImpacted();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForInteger(property, systemStartsImpacted);
                break;
            case "risk":
                Float risk = completeBusinessCaseBeforeUpdate.getRisk();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForFloat(property, risk);
                break;
            case "risk_in_labor_hours":
                Float riskInLaborHours = completeBusinessCaseBeforeUpdate.getRiskInLaborHours();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForFloat(property, riskInLaborHours);
                break;
            case "hardware_commitment":
                Float hardwareCommitment = completeBusinessCaseBeforeUpdate.getHardwareCommitment();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForFloat(property, hardwareCommitment);
                break;
            case "systems_in_wip_and_field_impacted":
                Integer systemsInWipAndFieldImpacted = completeBusinessCaseBeforeUpdate.getSystemsInWipAndFieldImpacted();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForInteger(property, systemsInWipAndFieldImpacted);
                break;
            case "factory_investments":
                Float factoryInvestments = completeBusinessCaseBeforeUpdate.getFactoryInvestments();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForFloat(property, factoryInvestments);
                break;
            case "fs_tooling_investments":
                Float fsToolingInvestments = completeBusinessCaseBeforeUpdate.getFsToolingInvestments();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForFloat(property, fsToolingInvestments);
                break;
            case "supply_chain_management_investments":
                Float supplyChainManagementInvestments = completeBusinessCaseBeforeUpdate.getSupplyChainManagementInvestments();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForFloat(property, supplyChainManagementInvestments);
                break;
            case "supplier_investments":
                Float supplierInvestments = completeBusinessCaseBeforeUpdate.getSupplierInvestments();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForFloat(property, supplierInvestments);
                break;
            case "de_investments":
                Float deInvestments = completeBusinessCaseBeforeUpdate.getDeInvestments();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForFloat(property, deInvestments);
                break;
            case "material_recurring_costs":
                Float materialRecurringCosts = completeBusinessCaseBeforeUpdate.getMaterialRecurringCosts();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForFloat(property, materialRecurringCosts);
                break;
            case "labor_recurring_costs":
                Float laborRecurringCosts = completeBusinessCaseBeforeUpdate.getLaborRecurringCosts();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForFloat(property, laborRecurringCosts);
                break;
            case "cycle_time_recurring_costs":
                Float cycleTimeRecurringCosts = completeBusinessCaseBeforeUpdate.getCycleTimeRecurringCosts();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForFloat(property, cycleTimeRecurringCosts);
                break;
            case "inventory_replace_nonrecurring_costs":
                Float inventoryReplaceNonrecurringCosts = completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForFloat(property, inventoryReplaceNonrecurringCosts);
                break;
            case "inventory_scrap_nonrecurring_costs":
                Float inventoryScrapNonrecurringCosts = completeBusinessCaseBeforeUpdate.getInventoryScrapNonrecurringCosts();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForFloat(property, inventoryScrapNonrecurringCosts);
                break;
            case "supply_chain_adjustments_nonrecurring_costs":
                Float supplyChainAdjustmentsNonrecurringCosts = completeBusinessCaseBeforeUpdate.getSupplyChainAdjustmentsNonrecurringCosts();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForFloat(property, supplyChainAdjustmentsNonrecurringCosts);
                break;
            case "factory_change_order_nonrecurring_costs":
                Float factoryChangeOrderNonrecurringCosts = completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForFloat(property, factoryChangeOrderNonrecurringCosts);
                break;
            case "update_upgrade_product_documentation_nonrecurring_costs":
                Float updateUpgradeProductDocumentationNonrecurringCosts = completeBusinessCaseBeforeUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForFloat(property, updateUpgradeProductDocumentationNonrecurringCosts);
                break;
            case "farm_out_development_nonrecurring_costs":
                Float farmOutDevelopmentNonrecurringCosts = completeBusinessCaseBeforeUpdate.getFarmOutDevelopmentNonrecurringCosts();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForFloat(property, farmOutDevelopmentNonrecurringCosts);
                break;
            case "prototype_materials_nonrecurring_costs":
                Float prototypeMaterialsNonrecurringCosts = completeBusinessCaseBeforeUpdate.getPrototypeMaterialsNonrecurringCosts();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForFloat(property, prototypeMaterialsNonrecurringCosts);
                break;
            case "revenues_benefits":
                Float revenuesBenefits = completeBusinessCaseBeforeUpdate.getRevenuesBenefits();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForFloat(property, revenuesBenefits);
                break;

            default:
        }

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier,user);

        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(patch(path)
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        optionalCompleteBusinessCase = completeBusinessCaseRepository.findById(completeBusinessCaseId);
        assertThat(optionalCompleteBusinessCase.isPresent(), equalTo(true));
        CompleteBusinessCase completeBusinessCaseAfterUpdate = optionalCompleteBusinessCase.get();

        switch(expectedResult) {
            case "UNAUTHORIZED":
                assertThat("Response status code is as expected", result.getResponse().getStatus(),equalTo(HttpStatus.FORBIDDEN.value()));
                ExceptionResponse exceptionResponse = new ExceptionResponse(content);
                Validator.unauthorizedExceptionAndcompleteBusinessCaseisDidNotChange(completeBusinessCaseBeforeUpdate, completeBusinessCaseAfterUpdate, exceptionResponse, path, changeRequestStatus.getStatusCode());
                break;
            case "AUTHORIZED":
                assertThat("Response status code is as expected", result.getResponse().getStatus(),equalTo(HttpStatus.OK.value()));
                ChangeRequestJson changeRequestJson = new ChangeRequestJson(content);

                switch (property){
                    case "system_starts_impacted":
                        Validator.completeBusinessCaseAreSameWithoutComparingSystemStartsImpacted(completeBusinessCaseBeforeUpdate, completeBusinessCaseAfterUpdate);
                        assertThat("SystemStartsImpacted not same", completeBusinessCaseBeforeUpdate.getSystemStartsImpacted(), Matchers.not(equalTo(completeBusinessCaseAfterUpdate.getSystemStartsImpacted())));
                        break;
                    case "risk":
                        Validator.completeBusinessCaseAreSameWithoutComparingRisk(completeBusinessCaseBeforeUpdate, completeBusinessCaseAfterUpdate);
                        assertThat("risk not same", completeBusinessCaseBeforeUpdate.getRisk(), Matchers.not(equalTo(completeBusinessCaseAfterUpdate.getRisk())));
                        break;
                    case "risk_in_labor_hours":
                        Validator.completeBusinessCaseAreSameWithoutComparingRiskInLaborHours(completeBusinessCaseBeforeUpdate, completeBusinessCaseAfterUpdate);
                        assertThat("RiskInLaborHours not same", completeBusinessCaseBeforeUpdate.getRiskInLaborHours(), Matchers.not(equalTo(completeBusinessCaseAfterUpdate.getRiskInLaborHours())));
                        break;
                    case "hardware_commitment":
                        Validator.completeBusinessCaseAreSameWithoutComparingHardwareCommitment(completeBusinessCaseBeforeUpdate, completeBusinessCaseAfterUpdate);
                        assertThat("HardwareCommitment not same", completeBusinessCaseBeforeUpdate.getHardwareCommitment(), Matchers.not(equalTo(completeBusinessCaseAfterUpdate.getHardwareCommitment())));
                        break;
                    case "systems_in_wip_and_field_impacted":
                        Validator.completeBusinessCaseAreSameWithoutComparingSystemsInWipAndFieldImpacted(completeBusinessCaseBeforeUpdate, completeBusinessCaseAfterUpdate);
                        assertThat("SystemsInWipAndFieldImpacted not same", completeBusinessCaseBeforeUpdate.getSystemsInWipAndFieldImpacted(), Matchers.not(equalTo(completeBusinessCaseAfterUpdate.getSystemsInWipAndFieldImpacted())));
                        break;
                    case "factory_investments":
                        Validator.completeBusinessCaseAreSameWithoutComparingFactoryInvestments(completeBusinessCaseBeforeUpdate, completeBusinessCaseAfterUpdate);
                        assertThat("FactoryInvestments not same", completeBusinessCaseBeforeUpdate.getFactoryInvestments(), Matchers.not(equalTo(completeBusinessCaseAfterUpdate.getFactoryInvestments())));
                        break;
                    case "fs_tooling_investments":
                        Validator.completeBusinessCaseAreSameWithoutComparingFsToolingInvestments(completeBusinessCaseBeforeUpdate, completeBusinessCaseAfterUpdate);
                        assertThat("FsToolingInvestments not same", completeBusinessCaseBeforeUpdate.getFsToolingInvestments(), Matchers.not(equalTo(completeBusinessCaseAfterUpdate.getFsToolingInvestments())));
                        break;
                    case "supply_chain_management_investments":
                        Validator.completeBusinessCaseAreSameWithoutComparingSupplyChainManagementInvestments(completeBusinessCaseBeforeUpdate, completeBusinessCaseAfterUpdate);
                        assertThat("SupplyChainManagementInvestments not same", completeBusinessCaseBeforeUpdate.getSupplyChainManagementInvestments(), Matchers.not(equalTo(completeBusinessCaseAfterUpdate.getSupplyChainManagementInvestments())));
                        break;
                    case "supplier_investments":
                        Validator.completeBusinessCaseAreSameWithoutComparingSupplierInvestments(completeBusinessCaseBeforeUpdate, completeBusinessCaseAfterUpdate);
                        assertThat("SupplierInvestments not same", completeBusinessCaseBeforeUpdate.getSupplierInvestments(), Matchers.not(equalTo(completeBusinessCaseAfterUpdate.getSupplierInvestments())));
                        break;
                    case "de_investments":
                        Validator.completeBusinessCaseAreSameWithoutComparingDeInvestments(completeBusinessCaseBeforeUpdate, completeBusinessCaseAfterUpdate);
                        assertThat("SupplierInvestments not same", completeBusinessCaseBeforeUpdate.getDeInvestments(), Matchers.not(equalTo(completeBusinessCaseAfterUpdate.getDeInvestments())));
                        break;
                    case "material_recurring_costs":
                        Validator.completeBusinessCaseAreSameWithoutComparingMaterialRecurringCosts(completeBusinessCaseBeforeUpdate, completeBusinessCaseAfterUpdate);
                        assertThat("MaterialRecurringCosts not same", completeBusinessCaseBeforeUpdate.getMaterialRecurringCosts(), Matchers.not(equalTo(completeBusinessCaseAfterUpdate.getMaterialRecurringCosts())));
                        break;
                    case "labor_recurring_costs":
                        Validator.completeBusinessCaseAreSameWithoutComparingLaborRecurringCosts(completeBusinessCaseBeforeUpdate, completeBusinessCaseAfterUpdate);
                        assertThat("LaborRecurringCosts not same", completeBusinessCaseBeforeUpdate.getLaborRecurringCosts(), Matchers.not(equalTo(completeBusinessCaseAfterUpdate.getLaborRecurringCosts())));
                        break;
                    case "cycle_time_recurring_costs":
                        Validator.completeBusinessCaseAreSameWithoutComparingCycleTimeRecurringCosts(completeBusinessCaseBeforeUpdate, completeBusinessCaseAfterUpdate);
                        assertThat("CycleTimeRecurringCosts not same", completeBusinessCaseBeforeUpdate.getCycleTimeRecurringCosts(), Matchers.not(equalTo(completeBusinessCaseAfterUpdate.getCycleTimeRecurringCosts())));
                        break;
                    case "inventory_replace_nonrecurring_costs":
                        Validator.completeBusinessCaseAreSameWithoutComparingInventoryReplaceNonrecurringCosts(completeBusinessCaseBeforeUpdate, completeBusinessCaseAfterUpdate);
                        assertThat("InventoryReplaceNonrecurringCosts not same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), Matchers.not(equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts())));
                        break;
                    case "inventory_scrap_nonrecurring_costs":
                        Validator.completeBusinessCaseAreSameWithoutComparingInventoryScrapNonrecurringCosts(completeBusinessCaseBeforeUpdate, completeBusinessCaseAfterUpdate);
                        assertThat("InventoryScrapNonrecurringCosts not same", completeBusinessCaseBeforeUpdate.getInventoryScrapNonrecurringCosts(), Matchers.not(equalTo(completeBusinessCaseAfterUpdate.getInventoryScrapNonrecurringCosts())));
                        break;
                    case "supply_chain_adjustments_nonrecurring_costs":
                        Validator.completeBusinessCaseAreSameWithoutComparingSupplyChainAdjustmentsNonrecurringCosts(completeBusinessCaseBeforeUpdate, completeBusinessCaseAfterUpdate);
                        assertThat("SupplyChainAdjustmentsNonrecurringCosts not same", completeBusinessCaseBeforeUpdate.getSupplyChainAdjustmentsNonrecurringCosts(), Matchers.not(equalTo(completeBusinessCaseAfterUpdate.getSupplyChainAdjustmentsNonrecurringCosts())));
                        break;
                    case "factory_change_order_nonrecurring_costs":
                        Validator.completeBusinessCaseAreSameWithoutComparingFactoryChangeOrderNonrecurringCosts(completeBusinessCaseBeforeUpdate, completeBusinessCaseAfterUpdate);
                        assertThat("FactoryChangeOrderNonrecurringCosts not same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), Matchers.not(equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts())));
                        break;
                    case "update_upgrade_product_documentation_nonrecurring_costs":
                        Validator.completeBusinessCaseAreSameWithoutComparingUpdateUpgradeProductDocumentationNonrecurringCosts(completeBusinessCaseBeforeUpdate, completeBusinessCaseAfterUpdate);
                        assertThat("UpdateUpgradeProductDocumentationNonrecurringCosts not same", completeBusinessCaseBeforeUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts(), Matchers.not(equalTo(completeBusinessCaseAfterUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts())));
                        break;
                    case "farm_out_development_nonrecurring_costs":
                        Validator.completeBusinessCaseAreSameWithoutComparingFarmOutDevelopmentNonrecurringCosts(completeBusinessCaseBeforeUpdate, completeBusinessCaseAfterUpdate);
                        assertThat("FarmOutDevelopmentNonrecurringCosts not same", completeBusinessCaseBeforeUpdate.getFarmOutDevelopmentNonrecurringCosts(), Matchers.not(equalTo(completeBusinessCaseAfterUpdate.getFarmOutDevelopmentNonrecurringCosts())));
                        break;
                    case "prototype_materials_nonrecurring_costs":
                        Validator.completeBusinessCaseAreSameWithoutComparingPrototypeMaterialsNonrecurringCosts(completeBusinessCaseBeforeUpdate, completeBusinessCaseAfterUpdate);
                        assertThat("PrototypeMaterialsNonrecurringCosts not same", completeBusinessCaseBeforeUpdate.getPrototypeMaterialsNonrecurringCosts(), Matchers.not(equalTo(completeBusinessCaseAfterUpdate.getPrototypeMaterialsNonrecurringCosts())));
                        break;
                    case "revenues_benefits":
                        Validator.completeBusinessCaseAreSameWithoutComparingRevenuesBenefits(completeBusinessCaseBeforeUpdate, completeBusinessCaseAfterUpdate);
                        assertThat("RevenuesBenefits not same", completeBusinessCaseBeforeUpdate.getRevenuesBenefits(), Matchers.not(equalTo(completeBusinessCaseAfterUpdate.getRevenuesBenefits())));
                        break;

                    default:
                }
                break;
        }
    }


    @ParameterizedTest(name = "{0} user updates property {1} when change request in {2} status, expect to be {3}")
    @CsvFileSource(resources = "/parameters/changerequest/ScopePropertyUpdate.csv", numLinesToSkip = 1)
    void userToUpdatePropertyOfScopeInStatus(String user, String property, ChangeRequestStatus changeRequestStatus, String expectedResult) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", changeRequestStatus,null);
        assert id!=null;
        Long scopeId = entityInstanceManager.getScopeIdByChangeRequestId(id);
        String path = PathGenerator.getEntityUpdatePath("scope", scopeId);

        Optional<Scope> optionalScope = scopeRepository.findById(scopeId);
        assertThat(optionalScope.isPresent(), equalTo(true));
        Scope scopeBeforeUpdate = optionalScope.get();

        JsonNode updateRequest = null;
        switch (property){
            case "parts":
                String  parts = scopeBeforeUpdate.getParts();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, parts);
                break;
            case "tooling":
                String  tooling = scopeBeforeUpdate.getTooling();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, tooling);
                break;
            case "packaging":
                String  packaging = scopeBeforeUpdate.getPackaging();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, packaging);
                break;
            case "bop":
                String  bop = scopeBeforeUpdate.getBop();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, bop);
                break;
            case "scope_details":
                String  scopeDetails = scopeBeforeUpdate.getScopeDetails();
                updateRequest = JsonNodeFactory.getFieldUpdateRequestForString(property, scopeDetails);
                break;

            default:
        }

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier,user);

        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(patch(path)
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        optionalScope = scopeRepository.findById(scopeId);
        assertThat(optionalScope.isPresent(), equalTo(true));
        Scope scopeAfterUpdate = optionalScope.get();

        switch(expectedResult) {
            case "UNAUTHORIZED":
                assertThat("Response status code is as expected", result.getResponse().getStatus(),equalTo(HttpStatus.FORBIDDEN.value()));
                ExceptionResponse exceptionResponse = new ExceptionResponse(content);
                Validator.unauthorizedExceptionAndScopeisDidNotChange(scopeBeforeUpdate, scopeAfterUpdate, exceptionResponse, path, changeRequestStatus.getStatusCode());
                break;
            case "AUTHORIZED":
                assertThat("Response status code is as expected", result.getResponse().getStatus(),equalTo(HttpStatus.OK.value()));
                ChangeRequestJson changeRequestJson = new ChangeRequestJson(content);

                switch (property){
                    case "parts":
                        Validator.scopeAreSameWithoutComparingParts(scopeBeforeUpdate, scopeAfterUpdate);
                        assertThat("SystemStartsImpacted not same", scopeBeforeUpdate.getParts(), Matchers.not(equalTo(scopeAfterUpdate.getParts())));
                        break;
                    case "tooling":
                        Validator.scopeAreSameWithoutComparingTooling(scopeBeforeUpdate, scopeAfterUpdate);
                        assertThat("Tooling not same", scopeBeforeUpdate.getTooling(), Matchers.not(equalTo(scopeAfterUpdate.getTooling())));
                        break;
                    case "packaging":
                        Validator.scopeAreSameWithoutComparingPackaging(scopeBeforeUpdate, scopeAfterUpdate);
                        assertThat("Packaging not same", scopeBeforeUpdate.getPackaging(), Matchers.not(equalTo(scopeAfterUpdate.getPackaging())));
                        break;
                    case "bop":
                        Validator.scopeAreSameWithoutComparingBop(scopeBeforeUpdate, scopeAfterUpdate);
                        assertThat("Bop not same", scopeBeforeUpdate.getBop(), Matchers.not(equalTo(scopeAfterUpdate.getBop())));
                        break;
                    case "scope_details":
                        Validator.scopeAreSameWithoutComparingScopeDetails(scopeBeforeUpdate, scopeAfterUpdate);
                        assertThat("ScopeDetails not same", scopeBeforeUpdate.getScopeDetails(), Matchers.not(equalTo(scopeAfterUpdate.getScopeDetails())));
                        break;

                    default:
                }
                break;
        }
    }

    private static String linkPbsExpectedContent;

    static {
        InputStream linkPbsExpectedContentInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/linkpbs.txt");
        BufferedReader linkPbsBufferedReader = new BufferedReader(new InputStreamReader(linkPbsExpectedContentInputStream, StandardCharsets.UTF_8));
        linkPbsExpectedContent = linkPbsBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    @Test
    public void userToPerformCaseActionOnLinkPbsTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        JsonNode updateRequest = null;
        updateRequest = JsonNodeFactory.getFieldUpdateRequestForlinkpbs();
        String path = PathGenerator.getChangeRequestLinkPbsCaseActionPath(changeRequestId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        gdsMockServer.mockGdsToGetUserDetails();
       // userServiceMockServer.mockUserServicePreferredRoles("user");
        hanaMockServer.mockHanaToGetProjectLead();
        cerberusMockServer.mockCerberusSuccessfulProductBreakdownStructureFetch(changeRequestId,"10000");
        cerberusMockServer.mockCerberusSuccessfulupdateProductBreakdownStructure(changeRequestId,"10000");
        MvcResult result = getMockMvc().perform(put(path)
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = linkPbsExpectedContent;
        Optional<ChangeRequest> optionalChangeRequest1 = changeRequestRepository.findById(changeRequestId);
        String pbsContext = getContextTypeByType(optionalChangeRequest1.get(), "PBS");
        String pbsString = "PBS";
        assertThat("change request has pbs context,link pbs context id 10000 to CR", pbsString, equalTo(pbsContext));
        JSONAssert.assertEquals("change request link pbs details are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);

    }

    @Test
    public void userToPerformCaseActionOnUnLinkPbsTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        JsonNode updateRequest = null;
        updateRequest = JsonNodeFactory.getFieldUpdateRequestForlinkpbs();
        String path = PathGenerator.getChangeRequestLinkPbsCaseActionPath(changeRequestId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        gdsMockServer.mockGdsToGetUserDetails();
       // userServiceMockServer.mockUserServicePreferredRoles("user");
        hanaMockServer.mockHanaToGetProjectLead();
        cerberusMockServer.mockCerberusSuccessfulProductBreakdownStructureFetch(changeRequestId,"10000");
        cerberusMockServer.mockCerberusSuccessfulupdateProductBreakdownStructure(changeRequestId,"10000");
        MvcResult result = getMockMvc().perform(put(path)
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = linkPbsExpectedContent;
        Optional<ChangeRequest> optionalChangeRequest1 = changeRequestRepository.findById(changeRequestId);
        String pbsContext = getContextTypeByType(optionalChangeRequest1.get(), "PBS");
        String pbsString = "PBS";
        assertThat("change request has pbs context", pbsString, equalTo(pbsContext));
        JSONAssert.assertEquals("change request link pbs details are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
        JsonNode updateRequest1 = null;
        updateRequest1 = JsonNodeFactory.getFieldUpdateRequestForUnLinkPbs();
        cerberusMockServer.mockCerberusSuccessfulDeleteOfProductBreakdownStructure(changeRequestId,"10000");
        String path1 = PathGenerator.getChangeRequestUnLinkPbsCaseActionPath(changeRequestId);
        MvcResult result1 = getMockMvc().perform(patch(path1)
                .content(objectMapper.writeValueAsString(updateRequest1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content1 = result1.getResponse().getContentAsString();
        Optional<ChangeRequest> optionalChangeRequest2 = changeRequestRepository.findById(changeRequestId);
        boolean pbsContext1 = optionalChangeRequest2.get().getContexts().stream().filter(context -> context.getType().equals("PBS")).findFirst().isEmpty();
       if(pbsContext1)
        assertThat("change request does not have pbs context, unlink pbs context id 10000 from CR", true, equalTo(pbsContext1));
    }

    private static String linkAirExpectedContent;

    static {
        InputStream linkAirInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/linkair.txt");
        BufferedReader linkAirBufferedReader = new BufferedReader(new InputStreamReader(linkAirInputStream, StandardCharsets.UTF_8));
        linkAirExpectedContent = linkAirBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    @Test
    public void userToPerformCaseActionOnLinkAirTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        ChangeRequest changeRequest = optionalChangeRequest.get();
        JsonNode updateRequest = null;
        updateRequest = JsonNodeFactory.getFieldUpdateRequestForLinkAir();
        String path = PathGenerator.getChangeRequestLinkAirCaseActionPath(changeRequestId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        gdsMockServer.mockGdsToGetUserDetails();
       // userServiceMockServer.mockUserServicePreferredRoles("user");
        hanaMockServer.mockHanaToGetProjectLead();
        ImportData importData = objectMapper.convertValue(updateRequest, ImportData.class);
        List<String> airIds = new ArrayList<>();
        importData.getSources().forEach(importItem -> {
            airIds.add(importItem.getId());
        });
        airMockServer.mockAirSuccessfulgetProblemByNumber("P11110");
        airMockServer.mockAirSuccessfulUpdateProblem(changeRequestId, changeRequest.getStatus().toString(), changeRequest.getTitle(),  airIds);
        MvcResult result = getMockMvc().perform(put(path)
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = linkAirExpectedContent;
        Optional<ChangeRequest> optionalChangeRequest1 = changeRequestRepository.findById(changeRequestId);
        String airContext = getContextTypeByType(optionalChangeRequest1.get(), "AIR");
        String airString = "AIR";
        assertThat("change request has air context, link air context id P11110  to CR", airString, equalTo(airContext));
        JSONAssert.assertEquals("change request link air details are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);

    }


    @Test
    public void userToPerformCaseActionOnUnLinkAirTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        ChangeRequest changeRequest = optionalChangeRequest.get();
        JsonNode updateRequest = null;
        updateRequest = JsonNodeFactory.getFieldUpdateRequestForLinkAir();
        String path = PathGenerator.getChangeRequestLinkAirCaseActionPath(changeRequestId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        gdsMockServer.mockGdsToGetUserDetails();
        // userServiceMockServer.mockUserServicePreferredRoles("user");
        hanaMockServer.mockHanaToGetProjectLead();
        ImportData importData = objectMapper.convertValue(updateRequest, ImportData.class);
        List<String> airIds = new ArrayList<>();
        importData.getSources().forEach(importItem -> {
            airIds.add(importItem.getId());
        });
        airMockServer.mockAirSuccessfulgetProblemByNumber("P11110");
        airMockServer.mockAirSuccessfulUpdateProblem(changeRequestId, changeRequest.getStatus().toString(), changeRequest.getTitle(),  airIds);
        MvcResult result = getMockMvc().perform(put(path)
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = linkAirExpectedContent;
        Optional<ChangeRequest> optionalChangeRequest1 = changeRequestRepository.findById(changeRequestId);
        String airContext = getContextTypeByType(optionalChangeRequest1.get(), "AIR");
        String airString = "AIR";
        assertThat("change request has air context", airString, equalTo(airContext));
        JSONAssert.assertEquals("change request link air details are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
        JsonNode updateRequest1 = null;
        updateRequest1 = JsonNodeFactory.getFieldUpdateRequestForUnLinkAir();
        gdsMockServer.mockGdsToGetUserDetails();
       // userServiceMockServer.mockUserServicePreferredRoles("user");
        hanaMockServer.mockHanaToGetProjectLead();
        ImportData importData1 = objectMapper.convertValue(updateRequest, ImportData.class);
        List<String> airIds1 = new ArrayList<>();
        importData1.getSources().forEach(importItem -> {
            airIds1.add(importItem.getId());
        });
        airMockServer.mockAirSuccessfulgetProblemByNumber("P11110");
        airMockServer.mockAirSuccessfulUpdateProblem(changeRequestId, changeRequest.getStatus().toString(), changeRequest.getTitle(),  airIds1);
        String path1 = PathGenerator.getChangeRequestUnLinkAirCaseActionPath(changeRequestId);
        MvcResult result1 = getMockMvc().perform(patch(path1)
                .content(objectMapper.writeValueAsString(updateRequest1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content1 = result1.getResponse().getContentAsString();
        Optional<ChangeRequest> optionalChangeRequest2 = changeRequestRepository.findById(changeRequestId);
        boolean pbsContext1 = optionalChangeRequest2.get().getContexts().stream().filter(context -> context.getType().equals("AIR")).findFirst().isEmpty();
        if(pbsContext1)
            assertThat("change request does not have air context, UnLink Air P11110 from CR ", true, equalTo(pbsContext1));
    }


    private String getContextTypeByType(ChangeRequest changeRequest, String type) {
        return changeRequest.getContexts().stream().filter(context -> context.getType().equals(type)).findFirst().get().getType();
    }


    @ParameterizedTest(name = "{0} user has correct case permissions on change request in status {1}")
    @MethodSource("com.example.mirai.projectname.changerequestservice.fixtures.CasePermissionParametersFactory#getArgumentsForUserHasCorrectCasePermissionInStatus")
    void userHasCorrectCasePermissionOnChangeRequestInStatus(String user, ChangeRequestStatus changeRequestStatus, String expectedContent) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", changeRequestStatus, null);
        assert id!=null;

        String path = PathGenerator.getChangeRequestCasePermissionsPath(id);

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(get(path))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();

        JSONAssert.assertEquals("Change Request case permissions are as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }


    private static String updateScopeExpectedContent;
    private static String updatePreInstallImpactExpectedContent;
    static {
        InputStream updateScopeInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/updatescope.txt");
        BufferedReader updateScopeBufferedReader = new BufferedReader(new InputStreamReader(updateScopeInputStream, StandardCharsets.UTF_8));
        updateScopeExpectedContent = updateScopeBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream updatePreInstallImpactInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/updatepreinstallimpact.txt");
        BufferedReader updatePreInstallImpactBufferedReader = new BufferedReader(new InputStreamReader(updatePreInstallImpactInputStream, StandardCharsets.UTF_8));
        updatePreInstallImpactExpectedContent = updatePreInstallImpactBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    }
    @Test
    public void updateScopeTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        JsonNode updateRequest = null;
        updateRequest = JsonNodeFactory.getFieldUpdateRequestForUpdateScope();
        Long scopeId = entityInstanceManager.getScopeIdByChangeRequestId(changeRequestId);
        String path = PathGenerator.getUpdateScopePath(scopeId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(put(path)
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = updateScopeExpectedContent;
        expectedContent = expectedContent.replaceAll("<LABEL>",dataIdentifier);
        expectedContent = expectedContent.replaceAll("<CR_ID>",changeRequestId.toString());
        String contextId = optionalChangeRequest.get().getContexts().stream().filter(context -> context.getType().equals("TEAMCENTER")).findFirst().get().getContextId();
        expectedContent = expectedContent.replaceAll("<CONTEXT_ID>",contextId);
        expectedContent = expectedContent.replaceAll("<SCOPE_ID>",scopeId.toString());
        expectedContent = expectedContent.replaceAll("<SD_ID>",entityInstanceManager.getSolutionDefinitionIdByChangeRequestId(changeRequestId).toString());
        expectedContent = expectedContent.replaceAll("<IM_ID>",entityInstanceManager.getImpactAnalysisIdByChangeRequestId(changeRequestId).toString());
        expectedContent = expectedContent.replaceAll("<CI_ID>",entityInstanceManager.getCustomerImpactIdByChangeRequestId(changeRequestId).toString());
        expectedContent = expectedContent.replaceAll("<CB_ID>",entityInstanceManager.getCompleteBusinessCaseIdByChangeRequestId(changeRequestId).toString());
        expectedContent = expectedContent.replaceAll("<PI_ID>",entityInstanceManager.getPreinstallImpactIdByChangeRequestId(changeRequestId).toString());
        expectedContent = expectedContent.replaceAll("<MYTEAM_ID>",entityInstanceManager.findMyTeamIdByChangeRequestId(changeRequestId).toString());

        JSONAssert.assertEquals("update scope details are as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void updatePreInstallImpactTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        JsonNode updateRequest = null;
        updateRequest = JsonNodeFactory.getFieldUpdateRequestForUpdatePreInstallImpact();
        Long preInstallImpactId = entityInstanceManager.getPreinstallImpactIdByChangeRequestId(changeRequestId);
        String path = PathGenerator.getUpdatePreInstallImpactPath(preInstallImpactId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(put(path)
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = updatePreInstallImpactExpectedContent;
        expectedContent = expectedContent.replaceAll("<LABEL>",dataIdentifier);
        expectedContent = expectedContent.replaceAll("<CR_ID>",changeRequestId.toString());
        String contextId = optionalChangeRequest.get().getContexts().stream().filter(context -> context.getType().equals("TEAMCENTER")).findFirst().get().getContextId();
        expectedContent = expectedContent.replaceAll("<CONTEXT_ID>",contextId);
        expectedContent = expectedContent.replaceAll("<SCOPE_ID>",entityInstanceManager.getScopeIdByChangeRequestId(changeRequestId).toString());
        expectedContent = expectedContent.replaceAll("<SD_ID>",entityInstanceManager.getSolutionDefinitionIdByChangeRequestId(changeRequestId).toString());
        expectedContent = expectedContent.replaceAll("<IM_ID>",entityInstanceManager.getImpactAnalysisIdByChangeRequestId(changeRequestId).toString());
        expectedContent = expectedContent.replaceAll("<CI_ID>",entityInstanceManager.getCustomerImpactIdByChangeRequestId(changeRequestId).toString());
        expectedContent = expectedContent.replaceAll("<CB_ID>",entityInstanceManager.getCompleteBusinessCaseIdByChangeRequestId(changeRequestId).toString());
        expectedContent = expectedContent.replaceAll("<PI_ID>",entityInstanceManager.getPreinstallImpactIdByChangeRequestId(changeRequestId).toString());
        expectedContent = expectedContent.replaceAll("<MYTEAM_ID>",entityInstanceManager.findMyTeamIdByChangeRequestId(changeRequestId).toString());

        JSONAssert.assertEquals("preinstallimpact details are as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    private static String createSicaExpectedContent;
    private static String getSciaExpectedContent;
    private static String copySciaExpectedContent;
    static {
        InputStream createSicaInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/createscia.txt");
        BufferedReader createSicaBufferedReader = new BufferedReader(new InputStreamReader(createSicaInputStream, StandardCharsets.UTF_8));
        createSicaExpectedContent = createSicaBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getSciaInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/getscia.txt");
        BufferedReader getSciaBufferedReader = new BufferedReader(new InputStreamReader(getSciaInputStream, StandardCharsets.UTF_8));
        getSciaExpectedContent = getSciaBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream copySciaInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/copyscia.txt");
        BufferedReader copySciaBufferedReader = new BufferedReader(new InputStreamReader(copySciaInputStream, StandardCharsets.UTF_8));
        copySciaExpectedContent = copySciaBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    @Test
    public void createSciaTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.SOLUTION_DEFINED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        JsonNode updateRequest = null;
        updateRequest = JsonNodeFactory.getJsonRequestForCreateScia(optionalChangeRequest.get());
        String path = PathGenerator.getCreateSciaPath(changeRequestId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-1");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        //Scia scia = objectMapper.convertValue(updateRequest, Scia.class);
        //sciaMockServer.mockSciaServerToCreateScia(changeRequestId,scia);
       // sciaMockServer.mockSciaServerToCreateScia(optionalChangeRequest.get());
        MvcResult result = getMockMvc().perform(patch(path)
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        createSicaExpectedContent = createSicaExpectedContent.replaceAll("<contextid>",optionalChangeRequest.get().getId().toString());
        createSicaExpectedContent = createSicaExpectedContent.replaceAll("<DATA_IDENTIFIER>",dataIdentifier);
        String expectedContent = createSicaExpectedContent;

        JSONAssert.assertEquals("create scia details are not same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);

    }

    @Test
    public void getSciaTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.SOLUTION_DEFINED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        JsonNode updateRequest = null;
        updateRequest = JsonNodeFactory.getJsonRequestForCreateScia(optionalChangeRequest.get());
        String path = PathGenerator.getSciaByChangeRequestIdPath(changeRequestId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        //Scia scia = objectMapper.convertValue(updateRequest, Scia.class);
        //sciaMockServer.mockSciaServerToGetScia(changeRequestId,scia);
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        getSciaExpectedContent = getSciaExpectedContent.replaceAll("<DATA_IDENTIFIER>",dataIdentifier);
        String expectedContent = getSciaExpectedContent;

        JSONAssert.assertEquals("get scia details are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);

    }

    @Test
    public void copySciaTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.SOLUTION_DEFINED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        JsonNode updateRequest = null;
        updateRequest = JsonNodeFactory.getJsonRequestForCreateScia(optionalChangeRequest.get());
        //Scia scia = objectMapper.convertValue(updateRequest, Scia.class);
        String path = PathGenerator.getCopySciaPath(changeRequestId,"10");
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-1");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

       // sciaMockServer.mockSciaServerToCopyScia(optionalChangeRequest.get());
        MvcResult result = getMockMvc().perform(patch(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        copySciaExpectedContent = copySciaExpectedContent.replaceAll("<context_id>", optionalChangeRequest.get().getId().toString());
        copySciaExpectedContent = copySciaExpectedContent.replaceAll("<DATA_IDENTIFIER>", dataIdentifier);
        String expectedContent = copySciaExpectedContent;

        JSONAssert.assertEquals("copy scia details are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);

    }

    private static String changeRequestSummaryForScmExpectedContent;
    private static String changeRequestByAgendaItemIdExpectedContent;
    static {
        InputStream changeRequestSummaryForScmInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/changerequestSummaryForScm.txt");
        BufferedReader changeRequestSummaryForScmBufferedReader = new BufferedReader(new InputStreamReader(changeRequestSummaryForScmInputStream, StandardCharsets.UTF_8));
        changeRequestSummaryForScmExpectedContent = changeRequestSummaryForScmBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream changeRequestByAgendaItemIdInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/changerequestByAgendaITemId.txt");
        BufferedReader changeRequestByAgendaItemIdReader = new BufferedReader(new InputStreamReader(changeRequestByAgendaItemIdInputStream, StandardCharsets.UTF_8));
        changeRequestByAgendaItemIdExpectedContent = changeRequestByAgendaItemIdReader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    @Test
    public void getChangeRequestSummaryForScmTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String missingProperty = "NONE";
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, missingProperty, ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String path = PathGenerator.getPathForChangeRequestSummaryForScm(id);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        changeRequestSummaryForScmExpectedContent = changeRequestSummaryForScmExpectedContent.replaceAll("<ID>",id.toString());
        changeRequestSummaryForScmExpectedContent = changeRequestSummaryForScmExpectedContent.replaceAll("<DATA_IDENTIFIER>",dataIdentifier);

        String expectedContent = changeRequestSummaryForScmExpectedContent;
        JSONAssert.assertEquals("ChangeRequest Summary For Scm are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }


    @Test
    public void getChangeRequestDetailsByAgendaItemIdTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String missingProperty = "agenda_item";
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, missingProperty, ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String agendItemId = optionalChangeRequest.get().getContexts().stream().filter(context -> context.getType().equals("AGENDAITEM")).findFirst().get().getContextId();
        String path = PathGenerator.getPathForChangeRequestDetailsByAgendaItemId(agendItemId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        changeRequestByAgendaItemIdExpectedContent = changeRequestByAgendaItemIdExpectedContent.replaceAll("<ID>",id.toString());
        changeRequestByAgendaItemIdExpectedContent = changeRequestByAgendaItemIdExpectedContent.replaceAll("<agenda_item_contextid>",agendItemId);
        changeRequestByAgendaItemIdExpectedContent = changeRequestByAgendaItemIdExpectedContent.replaceAll("<DATA_IDENTIFIER>",dataIdentifier);
        String expectedContent = changeRequestByAgendaItemIdExpectedContent;
        JSONAssert.assertEquals("ChangeRequest by AgendaItem are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);    }


}
