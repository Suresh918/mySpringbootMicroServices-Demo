package com.example.mirai.projectname.releasepackageservice.tests.myteams;


import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import com.example.mirai.libraries.myteam.repository.MyTeamMemberRepository;
import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.projectname.releasepackageservice.BaseTest;
import com.example.mirai.projectname.releasepackageservice.ExceptionValidator;
import com.example.mirai.projectname.releasepackageservice.fixtures.EntityPojoFactory;
import com.example.mirai.projectname.releasepackageservice.fixtures.JwtFactory;
import com.example.mirai.projectname.releasepackageservice.json.ExceptionResponse;
import com.example.mirai.projectname.releasepackageservice.json.ReleasePackageMyTeamJson;
import com.example.mirai.projectname.releasepackageservice.myteam.model.ReleasePackageMyTeam;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageStatus;
import com.example.mirai.projectname.releasepackageservice.utils.PathGenerator;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

public class MyTeamsTests extends BaseTest {

    private static String myTeamAggregateExpectedContent;

    static {
        InputStream inputStream = MyTeamsTests.class.getResourceAsStream("/expectations/myteam/aggregate/Aggregate.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        myTeamAggregateExpectedContent = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    @Autowired
    MyTeamMemberRepository myTeamMemberRepository;


    @ParameterizedTest(name = "{0} user is to create myteam member expected to be {2} in release package status {1}")
    @CsvFileSource(resources = "/parameters/myteams/CreateTeamMember.csv", numLinesToSkip = 1)
    void userToCreateMyTeamMember(String user, ReleasePackageStatus originalReleasePackageStatus, String isSecure, String expectedResult) throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalReleasePackageStatus);

        entityInstanceManager.updateReleasePackageAndSetIsSecure(releasePackageId, isSecure.equalsIgnoreCase("true") ? true : false);

        Long teamId = entityInstanceManager.findMyTeamIdByReleasePackageId(releasePackageId);
        String path = PathGenerator.getMyTeamMemberCreationPath(teamId);

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));


        Optional<ReleasePackageMyTeam> optionalReleasePackageMyTeam = releasePackageMyTeamRepository.findById(teamId);
        assertThat(optionalReleasePackageMyTeam.isPresent(), equalTo(true));
        ReleasePackageMyTeam savedReleasePackageMyTeam = optionalReleasePackageMyTeam.get();

        MyTeamMember myTeamMemberRequest = EntityPojoFactory.createMyTeamMemberRequest(dataIdentifier, savedReleasePackageMyTeam);

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
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
                ReleasePackageMyTeamJson responseReleasePackageMyTeamJson = new ReleasePackageMyTeamJson(content);
                Long id = responseReleasePackageMyTeamJson.getId();
                Optional<MyTeamMember> optionalMyTeamMember = myTeamMemberRepository.findById(id);
                assertThat(optionalMyTeamMember.isPresent(), equalTo(true));
                MyTeamMember myTeamMemberSaved = optionalMyTeamMember.get();
                Validator.createMyTeamMemberIsSuccessful(myTeamMemberRequest, myTeamMemberSaved, responseReleasePackageMyTeamJson);
                break;
        }

    }

    @ParameterizedTest(name = "{0} user is to update myteam member and expected result to be {4} in release package status {1} " +
            "and update old role from {2} to new role {3}")
    @CsvFileSource(resources = "/parameters/myteams/UpdateTeamMemberRole.csv", numLinesToSkip = 1)
    void userToUpdateMyTeamMember(String user, ReleasePackageStatus originalReleasePackageStatus, String oldRole, String newRole,
                                  String isSecure, String expectedResult) throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalReleasePackageStatus);

        entityInstanceManager.updateReleasePackageAndSetIsSecure(releasePackageId, isSecure.equalsIgnoreCase("true") ? true : false);

        Long teamId = entityInstanceManager.findMyTeamIdByReleasePackageId(releasePackageId);

        Long teamMemberId = entityInstanceManager.createMyTeamMember(dataIdentifier, teamId);
        String path = PathGenerator.getMyTeamMemberDeletionPath(teamMemberId);

        Long memberRoleId = entityInstanceManager.addMemberRole(oldRole, teamMemberId, 0);
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
                assertThat("Response status code is not as expected", mvcResult.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
                Validator.updateTeamMemberRoleIsSuccessful(myTeamMemberBeforeUpdate, myTeamMemberAfterUpdate);
                break;
        }
    }

    @ParameterizedTest(name = "{0} user is to delete member role expected to be {2} in release package status {1}")
    @CsvFileSource(resources = "/parameters/myteams/DeleteMyTeamMember.csv", numLinesToSkip = 1)
    void userToDeleteMyTeamMemberRole(String user, ReleasePackageStatus originalReleasePackageStatus, String isSecure, String expectedResult) throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalReleasePackageStatus);
        entityInstanceManager.updateReleasePackageAndSetIsSecure(releasePackageId, isSecure.equalsIgnoreCase("true") ? true : false);

        Long teamId = entityInstanceManager.findMyTeamIdByReleasePackageId(releasePackageId);
        Long teamMemberId = entityInstanceManager.createMyTeamMember(dataIdentifier, teamId);
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
                assertThat("Response status code is not as expected", result.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
                assertThat(optionalMyTeamMember.isPresent(), equalTo(false));
                break;
        }
    }

    @ParameterizedTest(name = "{0} user has correct case permissions on release package in status {1}")
    @MethodSource("com.example.mirai.projectname.releasepackageservice.fixtures.CasePermissionParametersFactory#getArgumentsForUserHasCorrectCasePermissionOnRelatedEntityInStatus")
    void userToGetMyTeamCasePermissions(String user, ReleasePackageStatus releasePackageStatus, String isSecure, String expectedContent) throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long releasePackageId = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", releasePackageStatus);

        if (!user.equalsIgnoreCase("ReleasePackage.change-control-board-member")) {
            isSecure = "false";
        }
        entityInstanceManager.updateReleasePackageAndSetIsSecure(releasePackageId, isSecure.equalsIgnoreCase("false") ? false : true);

        Long teamId = entityInstanceManager.findMyTeamIdByReleasePackageId(releasePackageId);

        Optional<ReleasePackageMyTeam> optionalReleasePackageMyTeam = releasePackageMyTeamRepository.findById(teamId);
        ReleasePackageMyTeam myTeamBeforeCasePermission = optionalReleasePackageMyTeam.get();


        String path = PathGenerator.getMyTeamCasePermissionPath(teamId);

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult mvcResult = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();


        String content = mvcResult.getResponse().getContentAsString();
        System.out.println("CONTENT: " + content);
        JSONAssert.assertEquals("case permissions are not as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @ParameterizedTest(name = "{0} user is to perform myteam aggregate in release package status {1}")
    @CsvFileSource(resources = "/parameters/myteams/MyTeamsAggregate.csv", numLinesToSkip = 1)
    void getMyTeamAggregate(String user, ReleasePackageStatus originalReleasePackageStatus) throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long id = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalReleasePackageStatus);
        String releasePackageId = entityInstanceManager.getReleasePackageIdById(id);
        Long teamId = entityInstanceManager.findMyTeamIdByReleasePackageId(id);

        Long teamMemberId = entityInstanceManager.createMyTeamMember(dataIdentifier, teamId);

        Long memberRoleId = entityInstanceManager.addMemberRole("changeSpecialist2", teamMemberId, 0);

        Optional<ReleasePackageMyTeam> optionalReleasePackageMyTeam = releasePackageMyTeamRepository.findById(teamId);
        assertThat(optionalReleasePackageMyTeam.isPresent(), equalTo(true));

        String path = PathGenerator.getMyTeamAggregatePath(releasePackageId);

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult mvcResult = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();

        String expectedContent = myTeamAggregateExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier);
        expectedContent = expectedContent.replace("<TEAM_ID>", teamId + "");
        expectedContent = expectedContent.replace("<TEAM_MEMBER_ID>", teamMemberId + "");
        expectedContent = expectedContent.replace("<TEAM_MEMBER_ROLE>", "changeSpecialist2");

        JSONAssert.assertEquals("my team aggregate is not as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

}
