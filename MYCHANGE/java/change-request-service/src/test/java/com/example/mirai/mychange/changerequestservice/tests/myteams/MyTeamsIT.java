package com.example.mirai.projectname.changerequestservice.tests.myteams;



import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import com.example.mirai.libraries.myteam.model.dto.MyTeamBulkUpdate;
import com.example.mirai.libraries.myteam.repository.MyTeamMemberRepository;

import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestStatus;
import com.example.mirai.projectname.changerequestservice.fixtures.EntityPojoFactory;
import com.example.mirai.projectname.changerequestservice.fixtures.JsonNodeFactory;
import com.example.mirai.projectname.changerequestservice.fixtures.JwtFactory;
import com.example.mirai.projectname.changerequestservice.json.ChangeRequestMyTeamJson;
import com.example.mirai.projectname.changerequestservice.json.ExceptionResponse;
import com.example.mirai.projectname.changerequestservice.myteam.model.ChangeRequestMyTeam;
import com.example.mirai.projectname.changerequestservice.myteam.model.aggregate.ChangeRequestMyTeamMemberAggregate;
import com.example.mirai.projectname.changerequestservice.tests.BaseTest;

import com.example.mirai.projectname.changerequestservice.tests.ExceptionValidator;
import com.example.mirai.projectname.changerequestservice.tests.changerequest.Validator;
import com.example.mirai.projectname.changerequestservice.utils.PathGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MvcResult;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class MyTeamsIT extends BaseTest {

    private static String myTeamAggregateExpectedContent;
    private static String myTeamDetailsExpectedContent;
    private static String addMyTeamMemberExpectedContent;
    private static String myTeamSubjectsExpectedContent;

    static {
        InputStream inputStream = MyTeamsIT.class.getResourceAsStream("/expectations/myteam/aggregate/Aggregate.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        myTeamAggregateExpectedContent = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    static {
        InputStream inputStream = MyTeamsIT.class.getResourceAsStream("/expectations/myteam/dto/TeamDetails.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        myTeamDetailsExpectedContent = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    static {
        InputStream myTeamSubjectsInputStream = MyTeamsIT.class.getResourceAsStream("/expectations/myteam/security/MyTeamSubjects.txt");
        BufferedReader myTeamSubjectsBufferedReader = new BufferedReader(new InputStreamReader(myTeamSubjectsInputStream, StandardCharsets.UTF_8));
        myTeamSubjectsExpectedContent = myTeamSubjectsBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    static {
        InputStream inputStream = MyTeamsIT.class.getResourceAsStream("/expectations/myteam/impacteditem/ImpactedItemMyTeamMember.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        addMyTeamMemberExpectedContent = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    }


    @Autowired
    MyTeamMemberRepository myTeamMemberRepository;


    @ParameterizedTest(name = "{0} user is to create myteam member expected to be {2} in change request status {1}")
    @CsvFileSource(resources = "/parameters/myteams/CreateTeamMember.csv", numLinesToSkip = 1)
    void userToCreateMyTeamMember(String user, ChangeRequestStatus originalChangeRequestStatus, String expectedResult) throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalChangeRequestStatus,null);
        Long teamId = entityInstanceManager.findMyTeamIdByChangeRequestId(changeRequestId);
        String path = PathGenerator.getMyTeamMemberCreationPath(teamId);

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        Optional<ChangeRequestMyTeam> optionalChangeRequestMyTeam = changeRequestMyTeamRepository.findById(teamId);
        assertThat(optionalChangeRequestMyTeam.isPresent(), equalTo(true));
        ChangeRequestMyTeam savedChangedRequestMyTeam = optionalChangeRequestMyTeam.get();

        MyTeamMember myTeamMemberRequest = EntityPojoFactory.createMyTeamMemberRequest(dataIdentifier, savedChangedRequestMyTeam);
        List<String> userIds = new ArrayList<>();
        userIds.add(myTeamMemberRequest.getUser().getUserId());
        //userServiceMockServer.mockGetUserPreferredRoles(userIds);

        MvcResult result = getMockMvc().perform(put(path)
                .content(ObjectMapperUtil.getObjectMapper().writeValueAsString(myTeamMemberRequest))
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
                assertThat("Response status code is as expected", result.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
                ChangeRequestMyTeamJson responseChangeRequestMyTeamJson = new ChangeRequestMyTeamJson(content);
                Long id = responseChangeRequestMyTeamJson.getId();
                Optional<MyTeamMember> optionalMyTeamMember = myTeamMemberRepository.findById(id);
                assertThat(optionalMyTeamMember.isPresent(), equalTo(true));
                MyTeamMember myTeamMemberSaved = optionalMyTeamMember.get();
                Validator.createMyTeamMemberIsSuccessful(myTeamMemberRequest, myTeamMemberSaved, responseChangeRequestMyTeamJson);
                break;
        }

    }

    @ParameterizedTest(name = "{0} user is to update myteam member and expected result to be {4} in release package status {1} " +
            "when update old role from {2} to new role {3}")
    @CsvFileSource(resources = "/parameters/myteams/UpdateTeamMemberRole.csv", numLinesToSkip = 1)
    void userToUpdateMyTeamMember(String user, ChangeRequestStatus originalChangeRequestStatus, String oldRole, String newRole,
                                 String expectedResult) throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalChangeRequestStatus,null);
        Long teamId = entityInstanceManager.findMyTeamIdByChangeRequestId(changeRequestId);
        List<String> roles = new ArrayList<>();
        roles.add(oldRole);
        Long teamMemberId = entityInstanceManager.createMyTeamMember(dataIdentifier, teamId,roles);
        String path = PathGenerator.getMyTeamMemberUpdatePath(teamMemberId);

       //Long memberRoleId = entityInstanceManager.addMemberRole(oldRole, teamMemberId, 0);
        String updateRequest = EntityPojoFactory.getUpdateRequest(oldRole, newRole);

        Optional<MyTeamMember> optionalMyTeamMember = myTeamMemberRepository.findById(teamMemberId);
        assertThat(optionalMyTeamMember.isPresent(), equalTo(true));
        MyTeamMember myTeamMemberBeforeUpdate = optionalMyTeamMember.get();

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult mvcResult = getMockMvc().perform(patch(path)
                .content(updateRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        ExceptionResponse exceptionResponse = null;
        optionalMyTeamMember = myTeamMemberRepository.findById(teamMemberId);
        assertThat(optionalMyTeamMember.isPresent(), equalTo(true));
        MyTeamMember myTeamMemberAfterUpdate = optionalMyTeamMember.get();

        switch (expectedResult) {
            case "UNAUTHORIZED":
                assertThat("Response status code is not as expected", mvcResult.getResponse().getStatus(), equalTo(HttpStatus.FORBIDDEN.value()));
                exceptionResponse = new ExceptionResponse(content);
                Validator.updateTeamMemberRoleIsUnSuccessful(myTeamMemberBeforeUpdate, myTeamMemberAfterUpdate);
                ExceptionValidator.exceptionResponseIsUnauthorizedException(exceptionResponse, path);
                break;
            case "AUTHORIZED":
                assertThat("Response status code is as expected", mvcResult.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
                Validator.updateTeamMemberRoleIsSuccessful(myTeamMemberBeforeUpdate, myTeamMemberAfterUpdate);
                break;
        }
    }

    @ParameterizedTest(name = "{0} user is to delete member role expected to be {2} in change request status {1}")
    @CsvFileSource(resources = "/parameters/myteams/DeleteMyTeamMember.csv", numLinesToSkip = 1)
    void userToDeleteMyTeamMemberRole(String user, ChangeRequestStatus originalChangeRequestStatus, String expectedResult) throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalChangeRequestStatus,null);
        Long teamId = entityInstanceManager.findMyTeamIdByChangeRequestId(changeRequestId);
        List<String> roles = new ArrayList<>();
        roles.add("changeSpecialist2");
        Long teamMemberId = entityInstanceManager.createMyTeamMember(dataIdentifier, teamId,roles);
        String path = PathGenerator.getMyTeamMemberDeletionPath(teamMemberId);

        Optional<MyTeamMember> optionalMyTeamMember = myTeamMemberRepository.findById(teamMemberId);
        assertThat(optionalMyTeamMember.isPresent(), equalTo(true));
        MyTeamMember myTeamMemberBeforeDelete = optionalMyTeamMember.get();

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(delete(path)
                .contentType(MediaType.APPLICATION_JSON)).andReturn();

        optionalMyTeamMember = myTeamMemberRepository.findById(teamMemberId);

        switch (expectedResult) {
            case "UNAUTHORIZED":
                MyTeamMember myTeamMemberAfterDelete = optionalMyTeamMember.get();
                String content = result.getResponse().getContentAsString();
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.FORBIDDEN.value()));
                ExceptionResponse exceptionResponse = new ExceptionResponse(content);
                assertThat(optionalMyTeamMember.isPresent(), equalTo(true));
                Validator.unauthorizedExceptionAndMyTeamMemberDidNotChange(myTeamMemberBeforeDelete, myTeamMemberAfterDelete, exceptionResponse, path);
                break;
            case "AUTHORIZED":
                assertThat("Response status code is as expected", result.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
                assertThat(optionalMyTeamMember.isPresent(), equalTo(false));
                break;
        }
    }


    @ParameterizedTest(name = "{0} user is to perform myteam aggregate in change request status {1}")
    @CsvFileSource(resources = "/parameters/myteams/MyTeamsAggregate.csv", numLinesToSkip = 1)
    void getMyTeamAggregate(String user, ChangeRequestStatus originalChangeRequestStatus) throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalChangeRequestStatus,null);
        Long teamId = entityInstanceManager.findMyTeamIdByChangeRequestId(changeRequestId);
        List<String> roles = new ArrayList<>();
        roles.add("changeSpecialist2");
        Long teamMemberId = entityInstanceManager.createMyTeamMember(dataIdentifier, teamId,roles);

        Long memberRoleId = entityInstanceManager.findMemberRoleByTeamId(teamMemberId);//.addMemberRole("changeSpecialist2", teamMemberId, 0);

        Optional<ChangeRequestMyTeam> optionalReleasePackageMyTeam = changeRequestMyTeamRepository.findById(teamId);
        assertThat(optionalReleasePackageMyTeam.isPresent(), equalTo(true));

        String path = PathGenerator.getMyTeamAggregatePath(teamId.toString());

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult mvcResult = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();

        String expectedContent = myTeamAggregateExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier);
        expectedContent = expectedContent.replace("<TEAM_ID>", teamId + "");
        expectedContent = expectedContent.replace("<TEAM_MEMBER_ID>", teamMemberId + "");
        expectedContent = expectedContent.replace("<TEAM_MEMBER_ROLE>", "changeSpecialist2");

        JSONAssert.assertEquals("my team aggregate is as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void getMyTeamDetailsByIdTest() throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED,null);
        assert changeRequestId != null;
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        List<String> userIds = new ArrayList<>();
        userIds.add("user");
        //userServiceMockServer.mockGetUserPreferredRoles(userIds);
        ChangeRequest changeRequest = optionalChangeRequest.get();
        gdsMockServer.mockGdsToGetGroupDetails(changeRequest.getChangeControlBoards());
        String path = PathGenerator.getPathForMyTeamDetails(changeRequestId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult mvcResult = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        Long myTeamId = entityInstanceManager.findMyTeamIdByChangeRequestId(changeRequestId);
        String expectedContent = myTeamDetailsExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier);
        expectedContent = expectedContent.replace("<TEAM_ID>", myTeamId + "");
        expectedContent = expectedContent.replace("<ccb_group>", changeRequest.getChangeControlBoards().get(0));
        JSONAssert.assertEquals("my team details is  as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void addImpactedItemMyTeamMemberTest() throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED,null);

        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        ChangeRequest changeRequest = optionalChangeRequest.get();
        Long myTeamId = entityInstanceManager.findMyTeamIdByChangeRequestId(changeRequestId);
        List<String> roles = new ArrayList<>();
        roles.add("changeSpecialist2");
        Long teamMemberId = entityInstanceManager.createMyTeamMember(dataIdentifier, myTeamId,roles);
        Optional<MyTeamMember> optionalMyTeamMember = myTeamMemberRepository.findById(teamMemberId);
        assertThat(optionalMyTeamMember.isPresent(), equalTo(true));
        MyTeamMember myTeamMemberBeforeUpdate = optionalMyTeamMember.get();
        ChangeRequestMyTeamMemberAggregate changeRequesMyTeamMemberAggregate = new ChangeRequestMyTeamMemberAggregate();
        changeRequesMyTeamMemberAggregate.setMember(myTeamMemberBeforeUpdate);
        JSONArray jsonNode = new JSONArray();
        jsonNode.add(objectMapper.convertValue(changeRequesMyTeamMemberAggregate, JsonNode.class));

        String path = PathGenerator.getPathForAddImpactedItemMyTeamMember( changeRequest.getId());
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult mvcResult = getMockMvc().perform(patch(path)
                .content(objectMapper.writeValueAsString(jsonNode))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        String expectedContent = addMyTeamMemberExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier);
        expectedContent = expectedContent.replace("<my_team_id>", myTeamId + "");
        expectedContent = expectedContent.replace("<member_id>", teamMemberId + "");
       /* expectedContent = expectedContent.replace("<my_team_member_user_id>", dataIdentifier + "_my_team_member_user_id");
        expectedContent = expectedContent.replace("<my_team_member_full_name>", dataIdentifier + "_my_team_member_full_name");
        expectedContent = expectedContent.replace("<my_team_member_department_name>", dataIdentifier + "_my_team_member_department_name");
        expectedContent = expectedContent.replace("<my_team_member_email>", dataIdentifier + "_my_team_member_email");
        expectedContent = expectedContent.replace("<my_team_member_abbreviation>", dataIdentifier + "_my_team_member-abbreviation");*/
        JSONAssert.assertEquals("Add impacted Item MyTeam Member details are as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);

    }

    @Test
    public void getSubjectsByMyTeamIdTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String missingProperty = "NONE";
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, missingProperty, ChangeRequestStatus.DRAFTED, null);
        Long teamId = entityInstanceManager.findMyTeamIdByChangeRequestId(id);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        List<String> userIds = new ArrayList<>();
        userIds.add("user");
        //userServiceMockServer.mockGetUserPreferredRoles(userIds);
        ChangeRequest changeRequest = optionalChangeRequest.get();
        String path = PathGenerator.getSubjectsPathByMyTeamId(teamId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = myTeamSubjectsExpectedContent.replaceAll("LABEL",dataIdentifier);
        JSONAssert.assertEquals("change request subjects are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @ParameterizedTest(name = "{0} user has correct case permissions on change request in status {1}")
    @MethodSource("com.example.mirai.projectname.changerequestservice.fixtures.CasePermissionParametersFactory#getArgumentsForUserHasCorrectCasePermissionOnRelatedEntityInStatus")
    void userHasCorrectCasePermissionOnMyTeamChangeRequestInStatus(String user, ChangeRequestStatus changeRequestStatus, String expectedContent) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", changeRequestStatus, null);
        assert id!=null;
        Long teamId = entityInstanceManager.findMyTeamIdByChangeRequestId(id);

        Optional<ChangeRequestMyTeam> optionalChangeRequestMyTeam = changeRequestMyTeamRepository.findById(teamId);
        ChangeRequestMyTeam myTeamBeforeCasePermission = optionalChangeRequestMyTeam.get();

        String path = PathGenerator.getMyTeamCasePermissionPath(teamId);

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult result = getMockMvc().perform(get(path))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();

        JSONAssert.assertEquals("Change Request MyTeam case permissions are as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void userToAddBulkUpdateMyTeamMembersUsingViewCriteriaIsAllSelectedTrueTest() throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        UUID uuid1= UUID.randomUUID();
        String dataIdentifier1 = uuid1.toString();
        Long changeRequestId1 = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Long changeRequestId2 = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier1, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);

        String caseaction = "BULK_ADD";
        String path = PathGenerator.getPathForBulkUpdateMyTeamMembersIsAllSelectedTrue(caseaction);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        List<Long> idList = new ArrayList<>();
        idList.add(changeRequestId1);
        idList.add(changeRequestId2);
        JsonNode updateRequest = null;
        updateRequest = JsonNodeFactory.getMyTeamBulkUpdateAddRequest(idList,true);

        MvcResult result = getMockMvc().perform(patch(path)
                .content(ObjectMapperUtil.getObjectMapper().writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
            String backgroundJobPath = PathGenerator.getBackgroundJobsPath();
            net.minidev.json.JSONArray processingJob = new JSONArray();
            net.minidev.json.JSONArray completedJob = new JSONArray();
            MvcResult backgroundJobs = null;
            while (processingJob.isEmpty() && completedJob.isEmpty()  ) {

                backgroundJobs = getMockMvc().perform(get(backgroundJobPath)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andReturn();
                processingJob = JsonPath.parse(backgroundJobs.getResponse().getContentAsString()).read("$.categories[?(@.name=='Processing')].sub_categories[0].items[?(@.job.action=='MYTEAM-BULK-ADD')]");
                completedJob = JsonPath.parse(backgroundJobs.getResponse().getContentAsString()).read("$.categories[?(@.name=='Completed')].sub_categories[0].items[?(@.job.action=='MYTEAM-BULK-ADD')]");

            }
            if (completedJob.isEmpty()) {
                while (processingJob.size() > 0 ) {
                    backgroundJobs = getMockMvc().perform(get(backgroundJobPath)
                            .contentType(MediaType.APPLICATION_JSON))
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andReturn();
                    processingJob = JsonPath.parse(backgroundJobs.getResponse().getContentAsString()).read("$.categories[?(@.name=='Processing')].sub_categories[0].items[?(@.job.action=='MYTEAM-BULK-ADD')]");
                    completedJob = JsonPath.parse(backgroundJobs.getResponse().getContentAsString()).read("$.categories[?(@.name=='Completed')].sub_categories[0].items[?(@.job.action=='MYTEAM-BULK-ADD')]");
                }
            }
            MyTeamBulkUpdate myTeamBulkUpdate = (MyTeamBulkUpdate)this.objectMapper.convertValue(updateRequest, MyTeamBulkUpdate.class);
            idList.stream().forEach(id->{
                Long teamId = entityInstanceManager.findMyTeamIdByChangeRequestId(id);
                List<MyTeamMember> myTeamMemberList = myTeamMemberService.getMembersByRole("businessController",teamId);
                Optional<MyTeamMember> teamMember = myTeamMemberList.stream().filter(myTeamMember -> myTeamMember.getUser().getUserId().equals("mychange05")).findFirst();
                assertThat("user id as expected", teamMember.get().getUser().getUserId(), equalTo(myTeamBulkUpdate.getUserToAdd().getUserId()));
            });
    }

    @Test
    void userToAddBulkUpdateMyTeamMembersUsingIsAllSelectedFalse() throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        UUID uuid1= UUID.randomUUID();
        String dataIdentifier1 = uuid1.toString();
        Long changeRequestId1 = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Long changeRequestId2 = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier1, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);

        String caseaction = "BULK_ADD";
        String path = PathGenerator.getPathForBulkUpdateMyTeamMembersIsAllSelectedFalse(caseaction);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        List<Long> idList = new ArrayList<>();
        idList.add(changeRequestId1);
        idList.add(changeRequestId2);
        JsonNode updateRequest = null;
        updateRequest = JsonNodeFactory.getMyTeamBulkUpdateAddRequest(idList,false);

        MvcResult result = getMockMvc().perform(patch(path)
                .content(ObjectMapperUtil.getObjectMapper().writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

            String backgroundJobPath = PathGenerator.getBackgroundJobsPath();
            net.minidev.json.JSONArray processingJob = new JSONArray();
            net.minidev.json.JSONArray completedJob = new JSONArray();
            MvcResult backgroundJobs = null;
            int i = 0;
            while (processingJob.isEmpty() && completedJob.isEmpty()  ) {

                backgroundJobs = getMockMvc().perform(get(backgroundJobPath)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andReturn();
                processingJob = JsonPath.parse(backgroundJobs.getResponse().getContentAsString()).read("$.categories[?(@.name=='Processing')].sub_categories[0].items[?(@.job.action=='MYTEAM-BULK-ADD')]");
                completedJob = JsonPath.parse(backgroundJobs.getResponse().getContentAsString()).read("$.categories[?(@.name=='Completed')].sub_categories[0].items[?(@.job.action=='MYTEAM-BULK-ADD')]");

            }
            int count = 0;
            if (completedJob.isEmpty()) {
                while (processingJob.size() > 0 ) {
                    backgroundJobs = getMockMvc().perform(get(backgroundJobPath)
                            .contentType(MediaType.APPLICATION_JSON))
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andReturn();
                    processingJob = JsonPath.parse(backgroundJobs.getResponse().getContentAsString()).read("$.categories[?(@.name=='Processing')].sub_categories[0].items[?(@.job.action=='MYTEAM-BULK-ADD')]");
                    completedJob = JsonPath.parse(backgroundJobs.getResponse().getContentAsString()).read("$.categories[?(@.name=='Completed')].sub_categories[0].items[?(@.job.action=='MYTEAM-BULK-ADD')]");

                }
            }
            MyTeamBulkUpdate myTeamBulkUpdate = (MyTeamBulkUpdate)this.objectMapper.convertValue(updateRequest, MyTeamBulkUpdate.class);

            idList.stream().forEach(id->{
                Long teamId = entityInstanceManager.findMyTeamIdByChangeRequestId(id);
                List<MyTeamMember> myTeamMemberList = myTeamMemberService.getMembersByRole("businessController",teamId);
                Optional<MyTeamMember> teamMember = myTeamMemberList.stream().filter(myTeamMember -> myTeamMember.getUser().getUserId().equals("mychange05")).findFirst();
                assertThat("user id as expected", teamMember.get().getUser().getUserId(), equalTo(myTeamBulkUpdate.getUserToAdd().getUserId()));
            });
    }

    @Test
    void userToRemoveBulkUpdateMyTeamMembersUsingIsAllSelectedFalse() throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        UUID uuid1= UUID.randomUUID();
        String dataIdentifier1 = uuid1.toString();
        Long changeRequestId1 = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        //Long changeRequestId2 = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier1, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Long teamId1 = entityInstanceManager.findMyTeamIdByChangeRequestId(changeRequestId1);
        List<String> roles = new ArrayList<>();
        roles.add("businessController");
        Long teamMemberId1 = entityInstanceManager.createMyTeamMember(dataIdentifier, teamId1, roles);
        Optional<MyTeamMember> optionalMyTeamMember1 = myTeamMemberRepository.findById(teamMemberId1);
        MyTeamMember myTeamMemberBeforeDelete1 = optionalMyTeamMember1.get();
        assertThat(optionalMyTeamMember1.isPresent(), equalTo(true));
        String caseaction = "BULK_REMOVE";
        String path = PathGenerator.getPathForBulkUpdateMyTeamMembersIsAllSelectedFalse(caseaction);
        List<Long> idList = new ArrayList<>();
        idList.add(changeRequestId1);
        JsonNode updateRequest = null;
        updateRequest = JsonNodeFactory.getMyTeamBulkUpdateRemoveRequest(idList,dataIdentifier,false);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(patch(path)
                .content(ObjectMapperUtil.getObjectMapper().writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

            String backgroundJobPath = PathGenerator.getBackgroundJobsPath();
            net.minidev.json.JSONArray processingJob = new JSONArray();
            net.minidev.json.JSONArray completedJob = new JSONArray();
            MvcResult backgroundJobs = null;
            int i = 0;
            while (processingJob.isEmpty() && completedJob.isEmpty()  ) {

                backgroundJobs = getMockMvc().perform(get(backgroundJobPath)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn();
                processingJob = JsonPath.parse(backgroundJobs.getResponse().getContentAsString()).read("$.categories[?(@.name=='Processing')].sub_categories[0].items[?(@.job.action=='MYTEAM-BULK-REMOVE')]");
                completedJob = JsonPath.parse(backgroundJobs.getResponse().getContentAsString()).read("$.categories[?(@.name=='Completed')].sub_categories[0].items[?(@.job.action=='MYTEAM-BULK-REMOVE')]");

            }
            if (completedJob.isEmpty()) {
                while (processingJob.size() > 0 ) {
                    backgroundJobs = getMockMvc().perform(get(backgroundJobPath)
                            .contentType(MediaType.APPLICATION_JSON))
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andReturn();
                    processingJob = JsonPath.parse(backgroundJobs.getResponse().getContentAsString()).read("$.categories[?(@.name=='Processing')].sub_categories[0].items[?(@.job.action=='MYTEAM-BULK-REMOVE')]");
                    completedJob = JsonPath.parse(backgroundJobs.getResponse().getContentAsString()).read("$.categories[?(@.name=='Completed')].sub_categories[0].items[?(@.job.action=='MYTEAM-BULK-REMOVE')]");

                }
            }
            MyTeamBulkUpdate myTeamBulkUpdate1 = (MyTeamBulkUpdate)this.objectMapper.convertValue(updateRequest, MyTeamBulkUpdate.class);

            idList.stream().forEach(id->{
                Long teamId = entityInstanceManager.findMyTeamIdByChangeRequestId(id);
                List<MyTeamMember> myTeamMemberList = myTeamMemberService.getMembersByRole("businessController",teamId);
                assertThat("user id is not expected as ", myTeamMemberList.size(),equalTo(0));
            });

    }

    @Test
    void userToRemoveBulkUpdateMyTeamMembersUsingIsAllSelectedTrue() throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        UUID uuid1= UUID.randomUUID();
        String dataIdentifier1 = uuid1.toString();
        Long changeRequestId1 = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        //Long changeRequestId2 = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier1, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Long teamId1 = entityInstanceManager.findMyTeamIdByChangeRequestId(changeRequestId1);
        List<String> roles = new ArrayList<>();
        roles.add("businessController");
        Long teamMemberId1 = entityInstanceManager.createMyTeamMember(dataIdentifier, teamId1, roles);
        Optional<MyTeamMember> optionalMyTeamMember1 = myTeamMemberRepository.findById(teamMemberId1);
        MyTeamMember myTeamMemberBeforeDelete1 = optionalMyTeamMember1.get();
        assertThat(optionalMyTeamMember1.isPresent(), equalTo(true));
        String caseaction = "BULK_REMOVE";
        String path = PathGenerator.getPathForBulkUpdateMyTeamMembersIsAllSelectedTrue(caseaction);
        List<Long> idList = new ArrayList<>();
        idList.add(changeRequestId1);
        JsonNode updateRequest = null;
        updateRequest = JsonNodeFactory.getMyTeamBulkUpdateRemoveRequest(idList,dataIdentifier,true);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(patch(path)
                .content(ObjectMapperUtil.getObjectMapper().writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String backgroundJobPath = PathGenerator.getBackgroundJobsPath();
        net.minidev.json.JSONArray processingJob = new JSONArray();
        net.minidev.json.JSONArray completedJob = new JSONArray();
        MvcResult backgroundJobs = null;
        while (processingJob.isEmpty() && completedJob.isEmpty()  ) {

            backgroundJobs = getMockMvc().perform(get(backgroundJobPath)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();
            processingJob = JsonPath.parse(backgroundJobs.getResponse().getContentAsString()).read("$.categories[?(@.name=='Processing')].sub_categories[0].items[?(@.job.action=='MYTEAM-BULK-REMOVE')]");
            completedJob = JsonPath.parse(backgroundJobs.getResponse().getContentAsString()).read("$.categories[?(@.name=='Completed')].sub_categories[0].items[?(@.job.action=='MYTEAM-BULK-REMOVE')]");

        }
        if (completedJob.isEmpty()) {
            while (processingJob.size() > 0 ) {
                backgroundJobs = getMockMvc().perform(get(backgroundJobPath)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andReturn();
                processingJob = JsonPath.parse(backgroundJobs.getResponse().getContentAsString()).read("$.categories[?(@.name=='Processing')].sub_categories[0].items[?(@.job.action=='MYTEAM-BULK-REMOVE')]");
                completedJob = JsonPath.parse(backgroundJobs.getResponse().getContentAsString()).read("$.categories[?(@.name=='Completed')].sub_categories[0].items[?(@.job.action=='MYTEAM-BULK-REMOVE')]");

            }
        }
        MyTeamBulkUpdate myTeamBulkUpdate1 = (MyTeamBulkUpdate)this.objectMapper.convertValue(updateRequest, MyTeamBulkUpdate.class);

        idList.stream().forEach(id->{
            Long teamId = entityInstanceManager.findMyTeamIdByChangeRequestId(id);
            List<MyTeamMember> myTeamMemberList = myTeamMemberService.getMembersByRole("businessController",teamId);
            assertThat("user id is not expected as ", myTeamMemberList.size(),equalTo(0));
        });

    }

    @Test
    void userToReplaceBulkUpdateMyTeamMembersUsingIsAllSelectedFalse() throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        UUID uuid1= UUID.randomUUID();
        String dataIdentifier1 = uuid1.toString();
        Long changeRequestId1 = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        //Long changeRequestId2 = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier1, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Long teamId1 = entityInstanceManager.findMyTeamIdByChangeRequestId(changeRequestId1);
        List<String> roles = new ArrayList<>();
        roles.add("businessController");
        Long teamMemberId1 = entityInstanceManager.createMyTeamMember(dataIdentifier, teamId1,roles);
        Optional<MyTeamMember> optionalMyTeamMember1 = myTeamMemberRepository.findById(teamMemberId1);
        assertThat(optionalMyTeamMember1.isPresent(), equalTo(true));
        MyTeamMember myTeamMemberBeforeDelete1 = optionalMyTeamMember1.get();
        String caseaction = "BULK_REPLACE";
        String path = PathGenerator.getPathForBulkUpdateMyTeamMembersIsAllSelectedFalse(caseaction);
        List<Long> idList = new ArrayList<>();
        idList.add(changeRequestId1);
        JsonNode updateRequest = null;
        updateRequest = JsonNodeFactory.getMyTeamBulkUpdateReplaceRequest(idList,dataIdentifier,false);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(patch(path)
                .content(ObjectMapperUtil.getObjectMapper().writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

            String backgroundJobPath = PathGenerator.getBackgroundJobsPath();
            net.minidev.json.JSONArray processingJob = new JSONArray();
            net.minidev.json.JSONArray completedJob = new JSONArray();
            MvcResult backgroundJobs = null;
            while (processingJob.isEmpty() && completedJob.isEmpty()  ) {

                backgroundJobs = getMockMvc().perform(get(backgroundJobPath)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn();
                processingJob = JsonPath.parse(backgroundJobs.getResponse().getContentAsString()).read("$.categories[?(@.name=='Processing')].sub_categories[0].items[?(@.job.action=='MYTEAM-BULK-REMOVE')]");
                completedJob = JsonPath.parse(backgroundJobs.getResponse().getContentAsString()).read("$.categories[?(@.name=='Completed')].sub_categories[0].items[?(@.job.action=='MYTEAM-BULK-REMOVE')]");

            }
            if (completedJob.isEmpty()) {
                while (processingJob.size() > 0 ) {
                    backgroundJobs = getMockMvc().perform(get(backgroundJobPath)
                            .contentType(MediaType.APPLICATION_JSON))
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andReturn();
                    processingJob = JsonPath.parse(backgroundJobs.getResponse().getContentAsString()).read("$.categories[?(@.name=='Processing')].sub_categories[0].items[?(@.job.action=='MYTEAM-BULK-REMOVE')]");
                    completedJob = JsonPath.parse(backgroundJobs.getResponse().getContentAsString()).read("$.categories[?(@.name=='Completed')].sub_categories[0].items[?(@.job.action=='MYTEAM-BULK-REMOVE')]");

                }
            }
            MyTeamBulkUpdate myTeamBulkUpdate1 = (MyTeamBulkUpdate)this.objectMapper.convertValue(updateRequest, MyTeamBulkUpdate.class);

            idList.stream().forEach(id->{
                Long teamId = entityInstanceManager.findMyTeamIdByChangeRequestId(id);
                List<MyTeamMember> myTeamMemberList = myTeamMemberService.getMembersByRole("businessController",teamId);
                Optional<MyTeamMember> teamMember = myTeamMemberList.stream().filter(myTeamMember -> myTeamMember.getUser().getUserId().equals("mychange05")).findFirst();
                assertThat("user id as expected", teamMember.get().getUser().getUserId(), equalTo(myTeamBulkUpdate1.getUserToAdd().getUserId()));
            });

            idList.stream().forEach(id->{
                Long teamId = entityInstanceManager.findMyTeamIdByChangeRequestId(id);
                List<MyTeamMember> myTeamMemberList = myTeamMemberService.getMembersByRole("businessController",teamId);
                Optional<MyTeamMember> optionalTeamMember = myTeamMemberList.stream().filter(myTeamMember -> myTeamMember.getUser().getUserId().equals(dataIdentifier + "_my_team_member_user_id")).findFirst();
                assertThat("user id not expected as", optionalTeamMember.isEmpty(), is(true));
            });
        }

    @Test
    void userToReplaceBulkUpdateMyTeamMembersUsingIsAllSelectedTrue() throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        UUID uuid1= UUID.randomUUID();
        String dataIdentifier1 = uuid1.toString();
        Long changeRequestId1 = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        //Long changeRequestId2 = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier1, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Long teamId1 = entityInstanceManager.findMyTeamIdByChangeRequestId(changeRequestId1);
        List<String> roles = new ArrayList<>();
        roles.add("businessController");
        Long teamMemberId1 = entityInstanceManager.createMyTeamMember(dataIdentifier, teamId1,roles);
        Optional<MyTeamMember> optionalMyTeamMember1 = myTeamMemberRepository.findById(teamMemberId1);
        assertThat(optionalMyTeamMember1.isPresent(), equalTo(true));
        MyTeamMember myTeamMemberBeforeDelete1 = optionalMyTeamMember1.get();
        String caseaction = "BULK_REPLACE";
        String path = PathGenerator.getPathForBulkUpdateMyTeamMembersIsAllSelectedTrue(caseaction);
        List<Long> idList = new ArrayList<>();
        idList.add(changeRequestId1);
        JsonNode updateRequest = null;
        updateRequest = JsonNodeFactory.getMyTeamBulkUpdateReplaceRequest(idList,dataIdentifier,true);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(patch(path)
                .content(ObjectMapperUtil.getObjectMapper().writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String backgroundJobPath = PathGenerator.getBackgroundJobsPath();
        net.minidev.json.JSONArray processingJob = new JSONArray();
        net.minidev.json.JSONArray completedJob = new JSONArray();
        MvcResult backgroundJobs = null;
        while (processingJob.isEmpty() && completedJob.isEmpty()  ) {

            backgroundJobs = getMockMvc().perform(get(backgroundJobPath)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();
            processingJob = JsonPath.parse(backgroundJobs.getResponse().getContentAsString()).read("$.categories[?(@.name=='Processing')].sub_categories[0].items[?(@.job.action=='MYTEAM-BULK-REMOVE')]");
            completedJob = JsonPath.parse(backgroundJobs.getResponse().getContentAsString()).read("$.categories[?(@.name=='Completed')].sub_categories[0].items[?(@.job.action=='MYTEAM-BULK-REMOVE')]");

        }
        if (completedJob.isEmpty()) {
            while (processingJob.size() > 0 ) {
                backgroundJobs = getMockMvc().perform(get(backgroundJobPath)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andReturn();
                processingJob = JsonPath.parse(backgroundJobs.getResponse().getContentAsString()).read("$.categories[?(@.name=='Processing')].sub_categories[0].items[?(@.job.action=='MYTEAM-BULK-REMOVE')]");
                completedJob = JsonPath.parse(backgroundJobs.getResponse().getContentAsString()).read("$.categories[?(@.name=='Completed')].sub_categories[0].items[?(@.job.action=='MYTEAM-BULK-REMOVE')]");

            }
        }
        MyTeamBulkUpdate myTeamBulkUpdate1 = (MyTeamBulkUpdate)this.objectMapper.convertValue(updateRequest, MyTeamBulkUpdate.class);

        idList.stream().forEach(id->{
            Long teamId = entityInstanceManager.findMyTeamIdByChangeRequestId(id);
            List<MyTeamMember> myTeamMemberList = myTeamMemberService.getMembersByRole("businessController",teamId);
            Optional<MyTeamMember> teamMember = myTeamMemberList.stream().filter(myTeamMember -> myTeamMember.getUser().getUserId().equals("mychange05")).findFirst();
            assertThat("user id as expected", teamMember.get().getUser().getUserId(), equalTo(myTeamBulkUpdate1.getUserToAdd().getUserId()));
        });

        idList.stream().forEach(id->{
            Long teamId = entityInstanceManager.findMyTeamIdByChangeRequestId(id);
            List<MyTeamMember> myTeamMemberList = myTeamMemberService.getMembersByRole("businessController",teamId);
            Optional<MyTeamMember> optionalTeamMember = myTeamMemberList.stream().filter(myTeamMember -> myTeamMember.getUser().getUserId().equals(dataIdentifier + "_my_team_member_user_id")).findFirst();
            assertThat("user id not expected as", optionalTeamMember.isEmpty(), is(true));
        });
    }

}
