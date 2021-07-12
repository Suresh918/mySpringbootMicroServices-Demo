package com.example.mirai.projectname.changerequestservice.mockservers;

import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
/*
import com.example.mirai.libraries.scm.scia.config.SciaConfigurationProperties;
import com.example.mirai.libraries.scm.scia.model.*;
*/
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class SciaMockServer {

    /*private final SciaConfigurationProperties sciaConfigurationProperties;

    public SciaMockServer(SciaConfigurationProperties sciaConfigurationProperties) {
        this.sciaConfigurationProperties = sciaConfigurationProperties;
    }

    @Override
    protected int getMockServerPort() {
        return getMockServerPort(this.sciaConfigurationProperties.getBaseUrl());
    }

    public void mockSciaServerToCreateScia(ChangeRequest changeRequest) throws com.fasterxml.jackson.core.JsonProcessingException {

        Scia scia = new Scia();
        //scia.setId(10L);
        scia.setTitle(changeRequest.getTitle());
        scia.setStatus(null);
        scia.setRevision(null);
        SciaContext sciaContext = new SciaContext();
        sciaContext.setType("CHANGEREQUEST");
        sciaContext.setName(changeRequest.getTitle());
        sciaContext.setContextId(changeRequest.getId().toString());
        sciaContext.setStatus(changeRequest.getStatus().toString());
        List<SciaContext> sciaContextList = new ArrayList<>();
        sciaContextList.add(sciaContext);
        scia.setContexts(sciaContextList);
        User user  = new User();
        user.setUserId("user");
        user.setFullName("test user");
        user.setEmail("test.user@example.com");
        user.setDepartmentName("IT BAS CC Corporate BPI & Automation");
        user.setAbbreviation("SaIn");
        scia.setPlmCoordinator(user);

        SciaSummary sciaSummary = new SciaSummary();
        sciaSummary.setId(scia.getId());
        sciaSummary.setRevision(scia.getRevision());
        sciaSummary.setContexts(scia.getContexts());
        sciaSummary.setStatus(scia.getStatus());
        sciaSummary.setPlmCoordinator(scia.getPlmCoordinator());
        sciaSummary.setTitle(scia.getTitle());
        sciaSummary.setStatusLabel("statusLabel");
        sciaSummary.setCreatedOn( null);

        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.POST.name())
                .withHeader(HttpHeaders.ACCEPT, org.mockserver.model.MediaType.APPLICATION_JSON.toString())
                .withPath("/scias").withQueryStringParameter("view", "summary").withBody(ObjectMapperUtil.getObjectMapper().writeValueAsString(scia));
        String mockResponse = ObjectMapperUtil.getObjectMapper().writeValueAsString(sciaSummary);

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                .withBody(mockResponse);

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockSciaServerToGetScia(Long id, Scia scia) throws JsonProcessingException {
        SciaSummaryList sciaSummaryList= new SciaSummaryList();
        List<SciaSummary> summaryList = new ArrayList<>();
        SciaSummary sciaSummary = new SciaSummary();
        sciaSummary.setId(scia.getId());
        sciaSummary.setRevision(scia.getRevision());
        sciaSummary.setContexts(scia.getContexts());
        sciaSummary.setStatus(scia.getStatus());
        sciaSummary.setPlmCoordinator(scia.getPlmCoordinator());
        sciaSummary.setTitle(scia.getTitle());
        sciaSummary.setStatusLabel("statusLabel");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        //dateFormat.format(date)
        //sciaSummary.setCreatedOn( new Date(dateFormat.format(date)));
        sciaSummary.setCreatedOn(null);
        summaryList.add(sciaSummary);

        sciaSummaryList.setTotalElements(1);
        sciaSummaryList.setTotalPages(1);
        sciaSummaryList.setHasNext(false);
        sciaSummaryList.setResults(summaryList);

        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withPath("/scias")
                .withQueryStringParameter("view","summary")
                .withQueryStringParameter("view-criteria","change_request_id:"+id.toString())
                .withQueryStringParameter("page","0")
                .withQueryStringParameter("size","2147483646");
        String mockResponse = new ObjectMapper().writeValueAsString(sciaSummaryList);

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                .withBody(mockResponse);

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

   *//* public void mockSciaServerToCopyScia(ChangeRequest changeRequest) throws JsonProcessingException {

        Scia scia = new Scia();
        scia.setId(10L);
        scia.setTitle(changeRequest.getTitle());
        scia.setStatus(null);
        scia.setRevision(null);
        SciaContext sciaContext = new SciaContext();
        sciaContext.setType("CHANGEREQUEST");
        sciaContext.setName(changeRequest.getTitle());
        sciaContext.setContextId(changeRequest.getId().toString());
        sciaContext.setStatus(changeRequest.getStatus().toString());
        List<SciaContext> sciaContextList = new ArrayList<>();
        sciaContextList.add(sciaContext);
        scia.setContexts(sciaContextList);
        User user  = new User();
        user.setUserId("user");
        user.setFullName("test user");
        user.setEmail("test.user@example.com");
        user.setDepartmentName("IT BAS CC Corporate BPI & Automation");
        user.setAbbreviation("SaIn");
        scia.setPlmCoordinator(user);

        SciaSummary sciaSummary = new SciaSummary();
        sciaSummary.setId(10L);
        sciaSummary.setRevision(scia.getRevision());
        sciaSummary.setContexts(scia.getContexts());
        sciaSummary.setStatus(1);
        sciaSummary.setPlmCoordinator(scia.getPlmCoordinator());
        sciaSummary.setTitle("Test");
        sciaSummary.setStatusLabel("statusLabel");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        //dateFormat.format(date)
        sciaSummary.setCreatedOn( new Date(dateFormat.format(date)));

        // Configure Mock Server http request configurationsciaSummary = {SciaSummary@24308} "SciaSummary(statusLabel=statusLabel, createdOn=Thu Jun 10 12:33:27 UTC 2021)"
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.POST.name())
                .withHeader(HttpHeaders.ACCEPT, org.mockserver.model.MediaType.APPLICATION_JSON.toString())
                .withPath("/scias?view=summary&scia_id=" + scia.getId());
        String mockResponse = new ObjectMapper().writeValueAsString(sciaSummary);

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                .withBody(mockResponse);

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }*//*

    public void mockSciaServerToCopyScia(ChangeRequest changeRequest) throws com.fasterxml.jackson.core.JsonProcessingException {

        Scia scia = new Scia();
        scia.setId(10L);
        scia.setTitle(changeRequest.getTitle());
        scia.setStatus(null);
        scia.setRevision(null);
        SciaContext sciaContext = new SciaContext();
        sciaContext.setType("CHANGEREQUEST");
        sciaContext.setName(changeRequest.getTitle());
        sciaContext.setContextId(changeRequest.getId().toString());
        sciaContext.setStatus(changeRequest.getStatus().toString());
        List<SciaContext> sciaContextList = new ArrayList<>();
        sciaContextList.add(sciaContext);
        scia.setContexts(sciaContextList);
        User user  = new User();
        user.setUserId("user");
        user.setFullName("test user");
        user.setEmail("test.user@example.com");
        user.setDepartmentName("IT BAS CC Corporate BPI & Automation");
        user.setAbbreviation("SaIn");
        scia.setPlmCoordinator(user);

        SciaSummary sciaSummary = new SciaSummary();
        sciaSummary.setId(scia.getId());
        sciaSummary.setRevision(scia.getRevision());
        sciaSummary.setContexts(scia.getContexts());
        sciaSummary.setStatus(scia.getStatus());
        sciaSummary.setPlmCoordinator(scia.getPlmCoordinator());
        sciaSummary.setTitle(scia.getTitle());
        sciaSummary.setStatusLabel("statusLabel");
        sciaSummary.setCreatedOn( null);

        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.POST.name())
                .withHeader(HttpHeaders.ACCEPT, org.mockserver.model.MediaType.APPLICATION_JSON.toString())
                .withPath("/scias").withQueryStringParameter("view", "summary")
                .withQueryStringParameter("scia_id", scia.getId().toString());
//                .withBody(ObjectMapperUtil.getObjectMapper().writeValueAsString(scia));
        String mockResponse = ObjectMapperUtil.getObjectMapper().writeValueAsString(sciaSummary);

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                .withBody(mockResponse);

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

*/
}
