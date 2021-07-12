package com.example.mirai.projectname.changerequestservice.tests.changerequest;

import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestStatus;
import com.example.mirai.projectname.changerequestservice.fixtures.JwtFactory;
import com.example.mirai.projectname.changerequestservice.tests.BaseTest;
import com.example.mirai.projectname.changerequestservice.utils.PathGenerator;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
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

public class ChangeRequestSecurityIT extends BaseTest {


    private static String crSubjectsExpectedContent;
    private static String getCaseActionsExpectedContent;
    private static String getAllCaseActionsExpectedContent;
    private static String getCasePropertiesExpectedContent;
    private static String getCaseStatusAggregateExpectedContent;
    private static String getCaseActionsByContextExpectedContent;
    private static String getCasePermissionsByContextExpectedContent;
    private static String getCasePermissionsExpectedContent;

    static {
        InputStream crSubjectsInputStream = ChangeRequestIT.class.getResourceAsStream("/expectations.changerequest.security/subjects.txt");
        BufferedReader crSubjectsBufferedReader = new BufferedReader(new InputStreamReader(crSubjectsInputStream, StandardCharsets.UTF_8));
        crSubjectsExpectedContent = crSubjectsBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getCaseActionsInputStream = ChangeRequestIT.class.getResourceAsStream("/expectations.changerequest.security/caseactions.txt");
        BufferedReader getCaseActionsBufferedReader = new BufferedReader(new InputStreamReader(getCaseActionsInputStream, StandardCharsets.UTF_8));
        getCaseActionsExpectedContent = getCaseActionsBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getAllCaseActionsInputStream = ChangeRequestIT.class.getResourceAsStream("/expectations.changerequest.security/allcaseactions.txt");
        BufferedReader getAllCaseActionsBufferedReader = new BufferedReader(new InputStreamReader(getAllCaseActionsInputStream, StandardCharsets.UTF_8));
        getAllCaseActionsExpectedContent = getAllCaseActionsBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getCasePropertiesInputStream = ChangeRequestIT.class.getResourceAsStream("/expectations.changerequest.security/getCaseProperties.txt");
        BufferedReader getCasePropertiesBufferedReader = new BufferedReader(new InputStreamReader(getCasePropertiesInputStream, StandardCharsets.UTF_8));
        getCasePropertiesExpectedContent = getCasePropertiesBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getCaseStatusAggregateInputStream = ChangeRequestIT.class.getResourceAsStream("/expectations.changerequest.security/getCaseStatusAggregate.txt");
        BufferedReader getCaseStatusAggregateBufferedReader = new BufferedReader(new InputStreamReader(getCaseStatusAggregateInputStream, StandardCharsets.UTF_8));
        getCaseStatusAggregateExpectedContent = getCaseStatusAggregateBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getCaseActionsByContextInputStream = ChangeRequestIT.class.getResourceAsStream("/expectations.changerequest.security/getCaseActionsByContext.txt");
        BufferedReader getCaseActionsByContextBufferedReader = new BufferedReader(new InputStreamReader(getCaseActionsByContextInputStream, StandardCharsets.UTF_8));
        getCaseActionsByContextExpectedContent = getCaseActionsByContextBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getCasePermissionsByContextInputStream = ChangeRequestIT.class.getResourceAsStream("/expectations.changerequest.security/getCasePermissionsByContext.txt");
        BufferedReader getCasePermissionsByContextBufferedReader = new BufferedReader(new InputStreamReader(getCasePermissionsByContextInputStream, StandardCharsets.UTF_8));
        getCasePermissionsByContextExpectedContent = getCasePermissionsByContextBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getCasePermissionsInputStream = ChangeRequestIT.class.getResourceAsStream("/expectations.changerequest.security/getCasePermissions.txt");
        BufferedReader getCasePermissionsBufferedReader = new BufferedReader(new InputStreamReader(getCasePermissionsInputStream, StandardCharsets.UTF_8));
        getCasePermissionsExpectedContent = getCasePermissionsBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    }


    @Test
    public void getSubjectsByChangeRequestIdTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String missingProperty = "NONE";
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, missingProperty, ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        hanaMockServer.mockHanaToGetProjectDetails();
        String path = PathGenerator.getSubjectsPathById("change-requests", id);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-1");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = crSubjectsExpectedContent.replaceAll("LABEL",dataIdentifier);
        JSONAssert.assertEquals("change request subjects are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getCaseActionsByChangeRequestIdTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String missingProperty = "NONE";
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, missingProperty, ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        hanaMockServer.mockHanaToGetProjectDetails();
        String path = PathGenerator.getCaseActionsPathById(id);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-1");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = getCaseActionsExpectedContent;
        JSONAssert.assertEquals("change request case actions are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }


    @Test
    public void getAllCaseActionsByChangeRequestIdTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String missingProperty = "NONE";
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, missingProperty, ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        hanaMockServer.mockHanaToGetProjectDetails();
        String path = PathGenerator.getAllCaseActionsPath();
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-1");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = getAllCaseActionsExpectedContent;
        JSONAssert.assertEquals("change request all case actions are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getCasePropertiesByChangeRequestIdTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String missingProperty = "NONE";
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, missingProperty, ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        hanaMockServer.mockHanaToGetProjectDetails();
        String path = PathGenerator.getCasePropertiesPathById(id);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-1");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = getCasePropertiesExpectedContent;
        JSONAssert.assertEquals("change request case properties are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getCaseActionsAndCasePropertiesByChangeRequestIdTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String missingProperty = "NONE";
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, missingProperty, ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        hanaMockServer.mockHanaToGetProjectDetails();
        String path = PathGenerator.getCasePropertiesPathById(id);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-1");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = getCasePropertiesExpectedContent;
        JSONAssert.assertEquals("change request CaseActionsAndCaseProperties are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }


    @Test
    public void getCaseStatusAggregateByChangeRequestIdTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String missingProperty = "NONE";
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, missingProperty, ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        hanaMockServer.mockHanaToGetProjectDetails();
        String path = PathGenerator.getCaseStatusAggregatePathById(id);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-1");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = getCaseStatusAggregateExpectedContent.replaceAll("LABEL", id.toString());
        JSONAssert.assertEquals("change request CaseStatusAggregate are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getCaseActionsByContextByChangeRequestIdTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String missingProperty = "NONE";
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, missingProperty, ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        hanaMockServer.mockHanaToGetProjectDetails();
        String contextId = getContextIdByType(optionalChangeRequest.get(), "TEAMCENTER");
        String path = PathGenerator.getCaseActionsByContextPath(contextId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-1");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = getCaseActionsByContextExpectedContent.replaceAll("LABEL",id.toString());
        JSONAssert.assertEquals("change request CaseStatusAggregate are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getCasePermissionsByContextByChangeRequestIdTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String missingProperty = "NONE";
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, missingProperty, ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        hanaMockServer.mockHanaToGetProjectDetails();
        String contextId = getContextIdByType(optionalChangeRequest.get(), "TEAMCENTER");
        String path = PathGenerator.getCasePermissionsByContextPath(contextId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-1");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = getCasePermissionsByContextExpectedContent.replaceAll("LABEL",id.toString());
        JSONAssert.assertEquals("change request CaseStatusAggregate are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getCasePermissionsByChangeRequestStatusTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String missingProperty = "NONE";
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, missingProperty, ChangeRequestStatus.IMPACT_ANALYZED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        hanaMockServer.mockHanaToGetProjectDetails();
        String path = PathGenerator.getCasePermissionsByChangeRequestPath(id);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-1");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = getCasePermissionsExpectedContent;
        JSONAssert.assertEquals("change request CaseStatusAggregate are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    private String getContextIdByType(ChangeRequest changeRequest, String type) {
        return changeRequest.getContexts().stream().filter(context -> context.getType().equals(type)).findFirst().get().getContextId();
    }

}
