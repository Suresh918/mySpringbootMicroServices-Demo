package com.example.mirai.projectname.changerequestservice.tests.changerequest;

import com.example.mirai.libraries.comment.model.CommentStatus;
import com.example.mirai.libraries.util.Constants;
import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestContext;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestStatus;
import com.example.mirai.projectname.changerequestservice.comment.model.ChangeRequestComment;
import com.example.mirai.projectname.changerequestservice.fixtures.JsonNodeFactory;
import com.example.mirai.projectname.changerequestservice.fixtures.JwtFactory;
import com.example.mirai.projectname.changerequestservice.tests.BaseTest;
import com.example.mirai.projectname.changerequestservice.tests.myteams.MyTeamsIT;
import com.example.mirai.projectname.changerequestservice.utils.PathGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

public class ChangeRequestDtoIT extends BaseTest {

    private static String changeRequestProjectExpectedContent;
    private static String getProductByChangeRequestIdExpectedContent;
    private static String getProjectLeadExpectedContent;
    private static String getProblemsExpectedContent;
    private static String getPmoDetailsExpectedContent;
    private static String getDiaBomExpectedContent;
    private static String getCollaborationObjectsCountExpectedContent;
    private static String getChangeRequestsListExpectedContent;
    private static String getChangeRequestAsLinkedObjExpectedContent;
    private static String getChangeRequestOverviewExpectedContent;
    private static String getChangeRequestStatusCountExpectedContent;
    private static String getStatusCountByPriorityExpectedContent;
    private static String getChangeRequestsSummaryExpectedContent;
    private static String getStateOverviewExpectedContent;
    private static String functionalClusterExpectedContent;
    private static String changeRequestSearchSummaryExpectedContent;
    private static String changeRequestsForGlobalSearchExpectedContent;
    private static String pbsBychangerequestIdExpectedContent;
    private static String findfunctionalClusterExpectedContent;
    private static String crTrackerboardSummaryExpectedContent;

    static {
        InputStream inputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/projectdetails.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        changeRequestProjectExpectedContent = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getProductByChangeRequestIdInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/productdetails.txt");
        BufferedReader getProductByChangeRequestIdBufferedReader = new BufferedReader(new InputStreamReader(getProductByChangeRequestIdInputStream, StandardCharsets.UTF_8));
        getProductByChangeRequestIdExpectedContent = getProductByChangeRequestIdBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getProjectLeadInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/projectleaddetails.txt");
        BufferedReader getProjectLeadBufferedReader = new BufferedReader(new InputStreamReader(getProjectLeadInputStream, StandardCharsets.UTF_8));
        getProjectLeadExpectedContent = getProjectLeadBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getProblemsInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/getproblemsbychangerequest.txt");
        BufferedReader getProblemsBufferedReader = new BufferedReader(new InputStreamReader(getProblemsInputStream, StandardCharsets.UTF_8));
        getProblemsExpectedContent = getProblemsBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getPmoDetailsExpectedContentInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/pmodetails.txt");
        BufferedReader getPmoDetailsExpectedContentBufferedReader = new BufferedReader(new InputStreamReader(getPmoDetailsExpectedContentInputStream, StandardCharsets.UTF_8));
        getPmoDetailsExpectedContent = getPmoDetailsExpectedContentBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getDiaBomInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/diabom.txt");
        BufferedReader getDiaBomBufferedReader = new BufferedReader(new InputStreamReader(getDiaBomInputStream, StandardCharsets.UTF_8));
        getDiaBomExpectedContent = getDiaBomBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getCollaborationObjectsCountInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/colloborationobject.txt");
        BufferedReader getCollaborationObjectsCountBufferedReader = new BufferedReader(new InputStreamReader(getCollaborationObjectsCountInputStream, StandardCharsets.UTF_8));
        getCollaborationObjectsCountExpectedContent = getCollaborationObjectsCountBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getChangeRequestsListInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/changerequestlist.txt");
        BufferedReader getChangeRequestsListBufferedReader = new BufferedReader(new InputStreamReader(getChangeRequestsListInputStream, StandardCharsets.UTF_8));
        getChangeRequestsListExpectedContent = getChangeRequestsListBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getChangeRequestAsLinkedObjInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/ChangeRequestAsLinkedObj.txt");
        BufferedReader getChangeRequestAsLinkedObjBufferedReader = new BufferedReader(new InputStreamReader(getChangeRequestAsLinkedObjInputStream, StandardCharsets.UTF_8));
        getChangeRequestAsLinkedObjExpectedContent = getChangeRequestAsLinkedObjBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getChangeRequestOverviewInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/changerequestoverview.txt");
        BufferedReader getChangeRequestOverviewBufferedReader = new BufferedReader(new InputStreamReader(getChangeRequestOverviewInputStream, StandardCharsets.UTF_8));
        getChangeRequestOverviewExpectedContent = getChangeRequestOverviewBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getChangeRequestStatusCountInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/ChangeRequestStatusCount.txt");
        BufferedReader getChangeRequestStatusCountBufferedReader = new BufferedReader(new InputStreamReader(getChangeRequestStatusCountInputStream, StandardCharsets.UTF_8));
        getChangeRequestStatusCountExpectedContent = getChangeRequestStatusCountBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getStatusCountByPriorityInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/getStatusCountByPriority.txt");
        BufferedReader getStatusCountByPriorityBufferedReader = new BufferedReader(new InputStreamReader(getStatusCountByPriorityInputStream, StandardCharsets.UTF_8));
        getStatusCountByPriorityExpectedContent = getStatusCountByPriorityBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream changeRequestsForGlobalSearchInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/changerequestglobalsearch.txt");
        BufferedReader changeRequestsForGlobalSearchBufferedReader = new BufferedReader(new InputStreamReader(changeRequestsForGlobalSearchInputStream, StandardCharsets.UTF_8));
        changeRequestsForGlobalSearchExpectedContent = changeRequestsForGlobalSearchBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getChangeRequestsSummaryInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/changerequestsummary.txt");
        BufferedReader getChangeRequestsSummaryBufferedReader = new BufferedReader(new InputStreamReader(getChangeRequestsSummaryInputStream, StandardCharsets.UTF_8));
        getChangeRequestsSummaryExpectedContent = getChangeRequestsSummaryBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getStateOverviewInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/stateoverview.txt");
        BufferedReader getStateOverviewBufferedReader = new BufferedReader(new InputStreamReader(getStateOverviewInputStream, StandardCharsets.UTF_8));
        getStateOverviewExpectedContent = getStateOverviewBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream changeRequestSearchSummaryInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/changerequestsearchsummary.txt");
        BufferedReader changeRequestSearchSummaryBufferedReader = new BufferedReader(new InputStreamReader(changeRequestSearchSummaryInputStream, StandardCharsets.UTF_8));
        changeRequestSearchSummaryExpectedContent = changeRequestSearchSummaryBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream functionalClusterInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/functionalcluster.txt");
        BufferedReader functionalClusterBufferedReader = new BufferedReader(new InputStreamReader(functionalClusterInputStream, StandardCharsets.UTF_8));
        functionalClusterExpectedContent = functionalClusterBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream findfunctionalClusterInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/searchfunctionalcluster.txt");
        BufferedReader findfunctionalClusterBufferedReader = new BufferedReader(new InputStreamReader(findfunctionalClusterInputStream, StandardCharsets.UTF_8));
        findfunctionalClusterExpectedContent = findfunctionalClusterBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream pbsBychangerequestIdInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/productbreakdownstructureByChangeRequest.txt");
        BufferedReader pbsBychangerequestIdBufferedReader = new BufferedReader(new InputStreamReader(pbsBychangerequestIdInputStream, StandardCharsets.UTF_8));
        pbsBychangerequestIdExpectedContent = pbsBychangerequestIdBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream crTrackerboardSummaryInputStream = MyTeamsIT.class.getResourceAsStream("/expectations.changerequestdto/crtrackerboardsummary.txt");
        BufferedReader crTrackerboardSummaryBufferedReader = new BufferedReader(new InputStreamReader(crTrackerboardSummaryInputStream, StandardCharsets.UTF_8));
        crTrackerboardSummaryExpectedContent = crTrackerboardSummaryBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

    }

    @Test
    public void getProjectByChangeRequestIdTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String missingProperty = "NONE";
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, missingProperty, ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        hanaMockServer.mockHanaToGetProjectDetails();
        String path = PathGenerator.getProjectPathById(id);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = changeRequestProjectExpectedContent;
        JSONAssert.assertEquals("change request project details are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getProductByChangeRequestIdTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String missingProperty = "NONE";
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, missingProperty, ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        hanaMockServer.mockHanaToGetProductDetails();
        String path = PathGenerator.getProductPathById(id);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = getProductByChangeRequestIdExpectedContent;
        JSONAssert.assertEquals("change request project details are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getProjectLeadByChangeRequestIdTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String missingProperty = "NONE";
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, missingProperty, ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        hanaMockServer.mockHanaToGetProjectLead();
        gdsMockServer.mockGdsToGetUserDetails();
        String path = PathGenerator.getProjectLeadPathById(id);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = getProjectLeadExpectedContent;
        JSONAssert.assertEquals("change request project details are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getProblemsByChangeRequestIdTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "air_context", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String aircontextId = optionalChangeRequest.get().getContexts().stream().filter(context -> context.getType().equals("AIR")).findFirst().get().getContextId();

        airMockServer.mockAirSuccessfulgetProblemByNumbers(aircontextId);
        String path = PathGenerator.getProblemsByChangeRequestIdPathById(id);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        getProblemsExpectedContent = getProblemsExpectedContent.replaceAll("<number>", aircontextId);
        String expectedContent = getProblemsExpectedContent;
        JSONAssert.assertEquals("change request project details are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }


    @Test
    public void getDiaBomByChangeRequestIdTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String missingProperty = "NONE";
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, missingProperty, ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        cerberusMockServer.mockCerberusSuccessfulFetchOfDiaBom(id);
        String path = PathGenerator.getDiaBomPathById("change-requests", id);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = getDiaBomExpectedContent.replace("<CRID>", id.toString());
        JSONAssert.assertEquals("DiaBom details are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @ParameterizedTest(name = "get colloboration object count by releasepackage number when release package status {0} and comment status in {1} ")
    @CsvFileSource(resources = "/parameters/changerequest/ColloborationObjects.csv", numLinesToSkip = 1)
    public void getCollaborationObjectsCountByIdTest(ChangeRequestStatus originalChangeRequestStatus, CommentStatus originalCommentStatus) throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String missingProperty = "NONE";
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, missingProperty, ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));

        Long changeRequestCommentId = entityInstanceManager.createChangeRequestCommentAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalCommentStatus, id);
        Optional<ChangeRequestComment> optionalReleasePackageComment = changeRequestCommentRepository.findById(changeRequestCommentId);
        assertThat(optionalReleasePackageComment.isPresent(), equalTo(true));
        assert changeRequestCommentId != null;

        String path = PathGenerator.getCollaborationObjectsCountPathById(id);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = getCollaborationObjectsCountExpectedContent;
        JSONAssert.assertEquals("change request getCollaborationObjectsCount are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getChangeRequestListTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String path = PathGenerator.getPathForChangeRequestList(changeRequestId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Optional<ChangeRequest> optionalChangeRequestAfterExecution = changeRequestRepository.findById(changeRequestId);
        ChangeRequest changeRequestResponse = optionalChangeRequestAfterExecution.get();
        Optional<ChangeRequestContext> ecnContext = changeRequestResponse.getContexts().stream().filter(changeRequestContext -> changeRequestContext.getType().equalsIgnoreCase("ECN")).findFirst();
        Optional<ChangeRequestContext> teamcenterContext = changeRequestResponse.getContexts().stream().filter(changeRequestContext -> changeRequestContext.getType().equalsIgnoreCase("TEAMCENTER")).findFirst();
        String teamcenterId = teamcenterContext.get().getContextId();
        String title = changeRequestResponse.getTitle();
        String expectedContent = getChangeRequestsListExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier);
        expectedContent = expectedContent.replace("<ID>", changeRequestId + "");
        expectedContent = expectedContent.replace("<title>", title);
        JSONAssert.assertEquals("ChangeRequestList list details are as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getChangeRequestAsLinkedObjectTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String path = PathGenerator.getPathForChangeRequestAsLinkedObject(changeRequestId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Optional<ChangeRequest> optionalChangeRequestAfterExecution = changeRequestRepository.findById(changeRequestId);
        ChangeRequest changeRequestResponse = optionalChangeRequestAfterExecution.get();
        Optional<ChangeRequestContext> ecnContext = changeRequestResponse.getContexts().stream().filter(changeRequestContext -> changeRequestContext.getType().equalsIgnoreCase("ECN")).findFirst();
        Optional<ChangeRequestContext> teamcenterContext = changeRequestResponse.getContexts().stream().filter(changeRequestContext -> changeRequestContext.getType().equalsIgnoreCase("TEAMCENTER")).findFirst();
        String expectedContent = getChangeRequestAsLinkedObjExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier);
        expectedContent = expectedContent.replace("<ID>", changeRequestId + "");
        JSONAssert.assertEquals("ChangeRequestList list details are as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getChangeRequestIsFirstDraftTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String path = PathGenerator.getChangeRequestIsFirstDraft(changeRequestId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = "{\"is_first_draft\":true}";
        JSONAssert.assertEquals("ChangeRequestList IsFirstDraft details are as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getChangeRequestOverviewTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String path = PathGenerator.getPathForChangeRequestOverview(changeRequestId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Optional<ChangeRequest> optionalChangeRequestAfterExecution = changeRequestRepository.findById(changeRequestId);
        ChangeRequest changeRequestResponse = optionalChangeRequestAfterExecution.get();
        Optional<ChangeRequestContext> ecnContext = changeRequestResponse.getContexts().stream().filter(changeRequestContext -> changeRequestContext.getType().equalsIgnoreCase("ECN")).findFirst();
        Optional<ChangeRequestContext> teamcenterContext = changeRequestResponse.getContexts().stream().filter(changeRequestContext -> changeRequestContext.getType().equalsIgnoreCase("TEAMCENTER")).findFirst();
        String expectedContent = getChangeRequestOverviewExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier);
        expectedContent = expectedContent.replace("<ID>", changeRequestId + "");
        JSONAssert.assertEquals("ChangeRequestOverview list details are as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void insecureFetchChangeRequestsSummary() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String path = PathGenerator.getPathForInsecureFetchChangeRequestsSummary(changeRequestId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        getChangeRequestsSummaryExpectedContent = getChangeRequestsSummaryExpectedContent.replaceAll("<ID>", changeRequestId.toString());
        getChangeRequestsSummaryExpectedContent = getChangeRequestsSummaryExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier);
        String expectedContent = getChangeRequestsSummaryExpectedContent;
        JSONAssert.assertEquals("ChangeRequest Summary details are as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getChangeRequestsSummaryTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String path = PathGenerator.getPathForChangeRequestsSummary(changeRequestId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        getChangeRequestsSummaryExpectedContent = getChangeRequestsSummaryExpectedContent.replaceAll("<ID>", changeRequestId.toString());
        getChangeRequestsSummaryExpectedContent = getChangeRequestsSummaryExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier);
        String expectedContent = getChangeRequestsSummaryExpectedContent;
        JSONAssert.assertEquals("ChangeRequest Summary details are as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getChangeRequestStatusCountTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String path = PathGenerator.getPathChangeRequestStatusCount(changeRequestId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = getChangeRequestStatusCountExpectedContent;
        JSONAssert.assertEquals("ChangeRequest Status Count  details are as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getStatusCountByPriorityTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String path = PathGenerator.getStatusCountByPriorityPath(changeRequestId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = getStatusCountByPriorityExpectedContent;
        JSONAssert.assertEquals("ChangeRequest Status Count by Priority  details are as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getStateOverviewTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.APPROVED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String path = PathGenerator.getStateOverviewPath(changeRequestId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = getStateOverviewExpectedContent;
        JSONAssert.assertEquals("state overview details are as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getChangeRequestSearchSummaryTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String path = PathGenerator.getPathForChangeRequestSearchSummary(changeRequestId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        changeRequestSearchSummaryExpectedContent = changeRequestSearchSummaryExpectedContent.replaceAll("<ID>", changeRequestId.toString());
        changeRequestSearchSummaryExpectedContent = changeRequestSearchSummaryExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier);
        String expectedContent = changeRequestSearchSummaryExpectedContent;
        JSONAssert.assertEquals("ChangeRequest Summary details are as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getProjectByAgendaItemIdTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "agenda_item", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String agendItemId = optionalChangeRequest.get().getContexts().stream().filter(context -> context.getType().equals("AGENDAITEM")).findFirst().get().getContextId();
        String path = PathGenerator.getPathForProjectByAgendaItemId(agendItemId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        hanaMockServer.mockHanaToGetProjectDetails();
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = changeRequestProjectExpectedContent;
        JSONAssert.assertEquals("change request project details are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getPmoDetailsByAgendaItemIdTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "agenda_item", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String agendItemId = optionalChangeRequest.get().getContexts().stream().filter(context -> context.getType().equals("AGENDAITEM")).findFirst().get().getContextId();
        String path = PathGenerator.getPathForPmoDetailsByAgendaItemId(agendItemId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        hanaMockServer.mockHanaToGetPmoDetails();
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = getPmoDetailsExpectedContent;
        JSONAssert.assertEquals("pmo details by agenda item are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getProductByAgendaItemIdTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "agenda_item", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String agendItemId = optionalChangeRequest.get().getContexts().stream().filter(context -> context.getType().equals("AGENDAITEM")).findFirst().get().getContextId();
        String path = PathGenerator.getPathForProductByAgendaItemId(agendItemId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        hanaMockServer.mockHanaToGetProductDetails();
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = getProductByChangeRequestIdExpectedContent;
        JSONAssert.assertEquals("change request product details by agenda item are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getProblemsByAgendaItemIdTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "agenda_item", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String agendaitemId = optionalChangeRequest.get().getContexts().stream().filter(context -> context.getType().equals("AGENDAITEM")).findFirst().get().getContextId();
        String aircontextId = optionalChangeRequest.get().getContexts().stream().filter(context -> context.getType().equals("AIR")).findFirst().get().getContextId();
        airMockServer.mockAirSuccessfulgetProblemByNumbers(aircontextId);
        String path = PathGenerator.getPathForProblemsByAgendaItemId(agendaitemId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        getProblemsExpectedContent = getProblemsExpectedContent.replaceAll("<number>", aircontextId);
        String expectedContent = getProblemsExpectedContent;
        JSONAssert.assertEquals("problems by agenda item  details are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getProductBreakdownStructuresTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "pbs_context", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String pbsId = optionalChangeRequest.get().getContexts().stream().filter(context -> context.getType().equals("PBS")).findFirst().get().getContextId();
        cerberusMockServer.mockCerberusSuccessfulProductBreakdownStructureFetch(id,pbsId);
        String path = PathGenerator.getPathForgetProductBreakdownStructures(id);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        pbsBychangerequestIdExpectedContent = pbsBychangerequestIdExpectedContent.replaceAll("<PBS_ID>", pbsId);
        pbsBychangerequestIdExpectedContent = pbsBychangerequestIdExpectedContent.replaceAll("<CR_ID>", id.toString());
        String expectedContent = pbsBychangerequestIdExpectedContent;
        JSONAssert.assertEquals("ProductBreakdownStructures by change request  details are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getScopeFieldVisibilityFactorTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "NONE", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String path = PathGenerator.getPathForgetScopeFieldEnablement(id);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String expectedContent = "{\"show_existing_part_question\":false,\"show_other_questions\":false}";
        JSONAssert.assertEquals("ScopeFieldVisibilityFactor details are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getFunctionalClusterDetailsTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String path = PathGenerator.getFunctionalClusterDetailsPathById(changeRequestId);
        String functionalClusterId = optionalChangeRequest.get().getFunctionalClusterId();
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        cerberusMockServer.mockCerberusSuccessfulFunctionalClusterByIdFetch(functionalClusterId);
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        functionalClusterExpectedContent = functionalClusterExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier);
        String expectedContent = functionalClusterExpectedContent;
        JSONAssert.assertEquals("Functional Cluster details are as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void findAirProblemsByPartialIdTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "agenda_item", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        airMockServer.mockAirSuccessfulfindProblemsByPartialNumber("P1110");
        String path = PathGenerator.getPathForFindAirProblemsByPartialId("P1110");
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
    }

    @Test
    public void searchFunctionalClusterTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String functionalClusterId = optionalChangeRequest.get().getFunctionalClusterId();
        String path = PathGenerator.searchFunctionalClusterPathById(functionalClusterId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        cerberusMockServer.mockCerberusSuccessfulfindFunctionalClusterByPartialIdFetch(functionalClusterId);
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        findfunctionalClusterExpectedContent = findfunctionalClusterExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier);
        String expectedContent = findfunctionalClusterExpectedContent;
        JSONAssert.assertEquals("Functional Cluster details are as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void fetchMultipleProductBreakDownStructuresTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "pbs_context", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String pbsId = optionalChangeRequest.get().getContexts().stream().filter(context -> context.getType().equals("PBS")).findFirst().get().getContextId();
        cerberusMockServer.mockCerberusSuccessfulfindProductBreakdownStructuresById(id,pbsId);
        String path = PathGenerator.getPathForfetchMultipleProductBreakDownStructures(pbsId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        pbsBychangerequestIdExpectedContent = pbsBychangerequestIdExpectedContent.replaceAll("<PBS_ID>", pbsId);
        pbsBychangerequestIdExpectedContent = pbsBychangerequestIdExpectedContent.replaceAll("<CR_ID>", id.toString());
        String expectedContent = pbsBychangerequestIdExpectedContent;
        JSONAssert.assertEquals("ProductBreakdownStructures details are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getChangeRequestsForGlobalSearchTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String path = PathGenerator.getPathForChangeRequestsForGlobalSearch(changeRequestId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        changeRequestsForGlobalSearchExpectedContent = changeRequestsForGlobalSearchExpectedContent.replaceAll("<ID>", changeRequestId.toString());
        changeRequestsForGlobalSearchExpectedContent = changeRequestsForGlobalSearchExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier);
        String expectedContent = changeRequestsForGlobalSearchExpectedContent;
        JSONAssert.assertEquals("ChangeRequest for Global Summary details are as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getProductBreakdownStructuresByAgendaItemIdTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long id = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "agenda_item", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(id);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String agendaItemId = optionalChangeRequest.get().getContexts().stream().filter(context -> context.getType().equals("AGENDAITEM")).findFirst().get().getContextId();
        String pbsId = optionalChangeRequest.get().getContexts().stream().filter(context -> context.getType().equals("PBS")).findFirst().get().getContextId();
        cerberusMockServer.mockCerberusSuccessfulProductBreakdownStructureFetch(id,pbsId);
        String path = PathGenerator.getPathForProductBreakdownStructuresByAgendaItemId(agendaItemId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        pbsBychangerequestIdExpectedContent = pbsBychangerequestIdExpectedContent.replaceAll("<PBS_ID>", pbsId);
        pbsBychangerequestIdExpectedContent = pbsBychangerequestIdExpectedContent.replaceAll("<CR_ID>", id.toString());
        String expectedContent = pbsBychangerequestIdExpectedContent;
        JSONAssert.assertEquals("ProductBreakdownStructures by change request  details are same as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void getChangeRequestTrackerboardSummaryTest() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        String path = PathGenerator.getPathForChangeRequestTrackerboardSummary(changeRequestId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        hanaMockServer.mockHanaToGetProjectDetails();
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        crTrackerboardSummaryExpectedContent = crTrackerboardSummaryExpectedContent.replaceAll("<ID>", changeRequestId.toString());
        crTrackerboardSummaryExpectedContent = crTrackerboardSummaryExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier);
        String expectedContent = crTrackerboardSummaryExpectedContent;
        JSONAssert.assertEquals("ChangeRequest Trackerboard Summary details are as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }




    @ParameterizedTest(name = "parts-{0},tooling-{1},machineBomPart-{2},fcoUpgradeOptionCsr-{3},servicePart-{4},preinstallPart-{5}," +
                              "serviceTooling-{6},impactOnExistingParts-{7},preinstallImpactResult-{8},impactOnSystemLevelPerformance-{9},impactOnAvailability-{10},issueTypes-{11}," +
                              "functionalSoftwareDependencies-{12},impactOnUserInterfaces-{13},impactOnWaferProcessEnvironment-{14},changeToCustomerImpactCriticalPart-{15}," +
                              "changeToProcessImpactingCustomer-{16},fcoUpgradeOptionCsrImplementationChange-{17}, expect to be ciaExpectedResult- {18}")
    @CsvFileSource(resources = "/parameters/changerequest/CIADetails.csv", numLinesToSkip = 1)
    void getCIADetailsTest(String parts,String tooling,String machineBomPart,String fcoUpgradeOptionCsr,
                             String servicePart,String preinstallPart,String serviceTooling,String impactOnExistingParts,
                             String preinstallImpactResult, String impactOnSystemLevelPerformance,
                             String impactOnAvailability, String issueTypes,String functionalSoftwareDependencies,
                             String impactOnUserInterfaces, String impactOnWaferProcessEnvironment,String changeToCustomerImpactCriticalPart,
                             String changeToProcessImpactingCustomer,String fcoUpgradeOptionCsrImplementationChange,String ciaExpectedResult ) throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Long changeRequestId = entityInstanceManager.createChangeRequestAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ChangeRequestStatus.DRAFTED, null);
        Optional<ChangeRequest> optionalChangeRequest = changeRequestRepository.findById(changeRequestId);
        assertThat(optionalChangeRequest.isPresent(), equalTo(true));
        entityInstanceManager.updateScopeforCia(changeRequestId,parts,tooling,machineBomPart,
                                                fcoUpgradeOptionCsr,servicePart,preinstallPart,serviceTooling);
        entityInstanceManager.updateImpactAnalysisforCia(changeRequestId,impactOnExistingParts,impactOnSystemLevelPerformance,impactOnAvailability);
        entityInstanceManager.updateSolutionDefinitionforCia(changeRequestId,functionalSoftwareDependencies);
        entityInstanceManager.updatePreinstallImpactforCia(changeRequestId,preinstallImpactResult);
        entityInstanceManager.updateCustomerImpactforCia(changeRequestId,impactOnUserInterfaces, impactOnWaferProcessEnvironment,
                                                         changeToCustomerImpactCriticalPart,changeToProcessImpactingCustomer, fcoUpgradeOptionCsrImplementationChange);
        entityInstanceManager.updateIssueTypesforCia(changeRequestId,issueTypes);
        String path = PathGenerator.getCIADetailsPathById(changeRequestId);
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-change-specialist-2");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        MvcResult result = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();

        String customer_impact_result =JsonPath.parse(content).read("$.customer_impact_result");
        if(Objects.nonNull(customer_impact_result) ){
            assertThat("CIA Result as expected", ciaExpectedResult, equalTo(customer_impact_result));
        }else{
            assertThat("CIA Result as expected", null, equalTo(customer_impact_result));
        }
    }

}
