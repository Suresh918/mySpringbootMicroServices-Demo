package com.example.mirai.projectname.releasepackageservice.mockservers;

import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.libraries.sapmdg.changerequest.model.ChangeRequest;
import com.example.mirai.libraries.sapmdg.changerequest.model.SapMdgChangeRequest;
import com.example.mirai.libraries.sapmdg.shared.model.ApplicationStatusCode;
import com.example.mirai.libraries.sapmdg.shared.model.ErrorResponse;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
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
public class SapMdgMockServer {
    private ClientAndServer mockClientAndServer;
@Value("${projectname.libraries.sapmdg.mock-server-port}")
    private Integer port;



    public void startMockServer() {
        mockClientAndServer = ClientAndServer.startClientAndServer(9997);
    }

    public void stopMockServer() { mockClientAndServer.stop(); }

    public void resetMockServer() {
        mockClientAndServer.reset();
    }


    public void mockSapMdgChangeRequestSuccessfulCreate(ReleasePackage releasePackage) throws JsonProcessingException {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String sapMdgCrId = dataIdentifier + "_sapmdgcr-id";
        ChangeRequest mockchangeRequest = new ChangeRequest();
        mockchangeRequest.setId(releasePackage.getId().toString());
        mockchangeRequest.setReleasePackageNumber(releasePackage.getReleasePackageNumber());
        mockchangeRequest.setDescription(releasePackage.getTitle());
        mockchangeRequest.setPlmUserId(null);

        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.POST.name())
                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .withPath("/change-requests");

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(new ObjectMapper().writeValueAsString(mockchangeRequest));

        // Configure Mock Server for the test
        //mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockSapMdgThrowsSapMdgCrAlreadyExistsExceptionOnMdgChangeRequestCreation(ReleasePackage releasePackage) throws JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        String sapMdgCrId = dataIdentifier + "_sapmdgcr-id";
        ChangeRequest mockchangeRequest = new ChangeRequest();
        mockchangeRequest.setId(null);
        mockchangeRequest.setReleasePackageNumber(releasePackage.getReleasePackageNumber());
        mockchangeRequest.setDescription(releasePackage.getTitle());
        mockchangeRequest.setPlmUserId(releasePackage.getPlmCoordinator().getUserId());

        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.POST.name())
                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .withPath("/change-requests")
                .withBody(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(mockchangeRequest));

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setDetail(new ErrorResponse.ErrorDetail());
        errorResponse.getDetail().setCode(ApplicationStatusCode.SAPMDG_CR_ALREADY_EXISTS);
        JsonNode errorResponseJson = ObjectMapperUtil.getObjectMapper().convertValue(errorResponse, JsonNode.class);

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.NOT_EXTENDED.value())
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(new ObjectMapper().writeValueAsString(errorResponseJson.toString()));

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockSapMdggetMdgCrIdSuccessfulFetch(ReleasePackage releasePackage) throws JsonProcessingException {

        String releasePackageNumber = releasePackage.getReleasePackageNumber();
        SapMdgChangeRequest mockResult = new SapMdgChangeRequest();
        mockResult.setMdgCrId("000000217145");
        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .withPath("/change-requests/view/release-package").withQueryStringParameter("release-package-number",releasePackageNumber);

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(new ObjectMapper().writeValueAsString(mockResult));

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);

    }



}
