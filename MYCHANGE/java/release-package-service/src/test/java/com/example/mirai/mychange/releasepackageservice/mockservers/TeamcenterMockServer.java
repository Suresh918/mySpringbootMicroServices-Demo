package com.example.mirai.projectname.releasepackageservice.mockservers;

import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.libraries.teamcenter.ecn.model.Ecn;
import com.example.mirai.libraries.teamcenter.ecn.model.Result;
import com.example.mirai.libraries.teamcenter.shared.ErrorResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

@Component
public class TeamcenterMockServer {
    private ClientAndServer mockClientAndServer;
   @Value("${projectname.libraries.teamcenter.mock-server-port}")
   private Integer port;

    public void startMockServer() {
        mockClientAndServer = ClientAndServer.startClientAndServer(9999);
    }
    
    public void stopMockServer() { mockClientAndServer.stop(); }
    
    public void resetMockServer() {
        mockClientAndServer.reset();
    }

    public void mockTeamcenterSuccessfulUpdate(String teamcenterId) throws JsonProcessingException {
        
        Result mockResult = new Result();
        mockResult.setDetails("details");
        mockResult.setStatus("SUCCESS");
        mockResult.setTeamcenterId(teamcenterId);

        // Configure Mock Server http request configuration
       HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.PUT.name())
                .withHeader(HttpHeaders.ACCEPT, org.mockserver.model.MediaType.APPLICATION_JSON.toString())
                .withPath("/engineering-change-notices/" + teamcenterId);

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(new ObjectMapper().writeValueAsString(mockResult));

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockTeamcenterSuccessfulCreate() throws JsonProcessingException {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String teamcenterId = dataIdentifier + "_teamcenter-id";

        Result mockResult = new Result();
        mockResult.setDetails("details");
        mockResult.setStatus("SUCCESS");
        mockResult.setTeamcenterId(teamcenterId);

        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.POST.name())
                .withHeader(HttpHeaders.ACCEPT, org.mockserver.model.MediaType.APPLICATION_JSON.toString())
                .withPath("/engineering-change-notices");

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                .withBody(new ObjectMapper().writeValueAsString(mockResult));

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockTeamcenterThrowsExceptionOnEcnCreation(Ecn engineeringChangeNotice, String exceptionCode, int statusCode) throws com.fasterxml.jackson.core.JsonProcessingException {
        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.POST.name())
                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .withPath("/engineering-change-notices")
                .withBody(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(engineeringChangeNotice));

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setDetail(new ErrorResponse.ErrorDetail());
        errorResponse.getDetail().setCode(exceptionCode);
        JsonNode errorResponseJson = ObjectMapperUtil.getObjectMapper().convertValue(errorResponse, JsonNode.class);

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(statusCode)
                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                .withBody(errorResponseJson.toString());

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockTeamcenterThrowsExceptionOnUpdate(Ecn engineeringChangeNotice, String exceptionCode, int statusCode) throws com.fasterxml.jackson.core.JsonProcessingException {
        // Configure Mock Server http request configuration
        String path = "/engineering-change-notices/" + engineeringChangeNotice.getTeamcenterId();
       HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.PUT.name())
                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .withPath(path)
                .withBody(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(engineeringChangeNotice));

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setDetail(new ErrorResponse.ErrorDetail());
        errorResponse.getDetail().setCode(exceptionCode);
        JsonNode errorResponseJson = ObjectMapperUtil.getObjectMapper().convertValue(errorResponse, JsonNode.class);

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(statusCode)
                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                .withBody(errorResponseJson.toString());

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }
}
