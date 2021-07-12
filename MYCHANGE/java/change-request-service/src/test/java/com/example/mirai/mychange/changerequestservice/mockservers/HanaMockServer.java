package com.example.mirai.projectname.changerequestservice.mockservers;

import com.example.mirai.libraries.hana.config.HanaConfigurationProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import er.ErDto;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import pmo.PmoDto;
import project.ProjectDto;
import projectlead.ProjectLeadDto;
import wbs.WbsDto;

@Component
public class HanaMockServer extends BaseMockServer {

    private final HanaConfigurationProperties hanaConfigurationProperties;
    private final ObjectMapper objectMapper;

    public HanaMockServer(HanaConfigurationProperties hanaConfigurationProperties, ObjectMapper objectMapper) {
        this.hanaConfigurationProperties = hanaConfigurationProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    protected int getMockServerPort() {
        return getMockServerPort(this.hanaConfigurationProperties.getBaseUrl());
    }

    public void mockHanaToGetProjectDetails() throws JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {

        String projectId="2237-0035";
        // Configure Mock Server http request configuration
        WbsDto wbsDto = new WbsDto();
        wbsDto.setWbsId("2237-0035");
        wbsDto.setDescription("projectdetails");
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .withPath("/wbs/" + projectId);
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(objectMapper.writeValueAsString(wbsDto));

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockHanaToGetProductDetails() throws JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {

        String productId = "1113";
        // Configure Mock Server http request configuration
        ProjectDto projectDto = new ProjectDto();
        projectDto.setProjectId("1113");
        projectDto.setDescription("productdetails");
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .withPath("/projects/" + productId);
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(objectMapper.writeValueAsString(projectDto));

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockHanaToGetPmoDetails() throws JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {

        String pmoId="2237-0035";
        // Configure Mock Server http request configuration
        PmoDto pmoDto = new PmoDto();
        pmoDto.setWbsId("2237-0035");
        pmoDto.setDescription("pmodetails");
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .withPath("/wbs/" + pmoId+"/pmo");
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(objectMapper.writeValueAsString(pmoDto));

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockHanaToGetProjectLead() throws JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {

        String projectId="2237-0035";
        // Configure Mock Server http request configuration
        ProjectLeadDto projectLeadDto = new ProjectLeadDto();
        projectLeadDto.setProjectId("2237-0035");
        projectLeadDto.setUserId("user");
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .withPath("/wbs/" + projectId + "/project-lead");
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(objectMapper.writeValueAsString(projectLeadDto));

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockHanaToGetSapErDetails(String erId) throws JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {

        String[] ecn = erId.split("-");
        ErDto erDto = new ErDto();
        erDto.setErId(ecn[1]);
        erDto.setStatus("40");
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .withPath("/er/" + ecn[1]);
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(objectMapper.writeValueAsString(erDto));

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockHanaToGetErDetails(String erId) throws JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {

        String[] ecn = erId.split("-");

        // Configure Mock Server http request configuration
        ErDto erDto = new ErDto();
        erDto.setErId(ecn[1]);
        erDto.setStatus("40");
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .withPath("/er/" + ecn[1]);
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(objectMapper.writeValueAsString(erDto));

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockHanaToGetProjectLeadForStatusUpdate(String projectId) throws JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {

        //String projectId="2237-0035";
        // Configure Mock Server http request configuration
        ProjectLeadDto projectLeadDto = new ProjectLeadDto();
        projectLeadDto.setProjectId(projectId);
        projectLeadDto.setUserId("user");
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .withPath("/wbs/" + projectId + "/project-lead");
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(objectMapper.writeValueAsString(projectLeadDto));

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

}
