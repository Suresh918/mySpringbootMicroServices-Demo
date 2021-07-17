package com.example.mirai.projectname.releasepackageservice.mockservers;

import com.example.mirai.libraries.cerberus.diabom.model.DiaBom;
import com.example.mirai.libraries.cerberus.shared.ErrorResponse;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.fasterxml.jackson.databind.JsonNode;

import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@Component
public class CerberusMockServer {
    private ClientAndServer mockClientAndServer;
 @Value("${projectname.libraries.cerberus.mock-server-port}")
    private Integer port;

   public void startMockServer() {
        mockClientAndServer = ClientAndServer.startClientAndServer(9998);
    }
    
    public void stopMockServer() { mockClientAndServer.stop(); }
    
    public void resetMockServer() {
        mockClientAndServer.reset();
    }

    public void mockCerberusSuccessfulDiaBomFetch(String changeNoticeId) throws JsonProcessingException {
        DiaBom mockResult = new DiaBom();
        mockResult.setChangeNoticeId(Long.parseLong(changeNoticeId));
        mockResult.setLastModifiedBy(new com.example.mirai.libraries.cerberus.diabom.model.User());
        mockResult.setLastModifiedOn(new Date());
        mockResult.setId(1L);
        mockResult.setRevisions(new ArrayList<>());

        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, org.mockserver.model.MediaType.APPLICATION_JSON.toString())
                .withPath("/dia-boms").withQueryStringParameter("change-notice-id", changeNoticeId);
        String mockResponse = new ObjectMapper().writeValueAsString(mockResult);
        mockResponse = mockResponse.replace("changeNoticeId", "changeNoticeID");
        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                .withBody(mockResponse);

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }


    public void mockCerberusThrowException(String changeNoticeId, String exceptionCode, int statusCode) {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, org.mockserver.model.MediaType.APPLICATION_JSON.toString())
                .withPath("/dia-boms").withQueryStringParameter("change-notice-id", changeNoticeId);

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
