package com.example.mirai.projectname.reviewservice.mockservers;

import com.example.mirai.libraries.sapmdg.changerequest.model.DeleteMaterialResponse;
import com.example.mirai.projectname.reviewservice.fixtures.EntityPojoFactory;
import com.example.mirai.projectname.reviewservice.review.model.dto.ecn.ReviewMaterial;
import com.example.mirai.projectname.reviewservice.review.model.dto.ecn.ReviewMaterialDelta;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;


@Component
public class SapMdgMockServer {
    private ClientAndServer mockClientAndServer;
  /*  @Value("projectname.libraries.SAPMDGMockServer.mock-server-port")
    String port;*/

    public void startMockServer() {
        mockClientAndServer = ClientAndServer.startClientAndServer(9998);
    }

    public void stopMockServer() {
        mockClientAndServer.stop();
    }

    public void resetMockServer() {
        mockClientAndServer.reset();
    }

    public void mockSAPMDGMaterialSuccessFulFetchForMaterialList(String sapId) throws JsonProcessingException {

        List<ReviewMaterialDelta> mockmaterialList = EntityPojoFactory.createMaterialList();

        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .withPath("/change-requests/" + "216810" + "/related-materials");

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(new ObjectMapper().writeValueAsString(mockmaterialList));

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockSapMdgSuccessFullDeleteForMaterialFromChangeRequest(String mdgCrId) throws JsonProcessingException {

        DeleteMaterialResponse deleteMaterialResponse = new DeleteMaterialResponse();
        deleteMaterialResponse.setStatus("SUCCESS");
        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.DELETE.name())
                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .withPath("/change-requests/" + mdgCrId + "/related-materials").withQueryStringParameter("material-number", "122");

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(new ObjectMapper().writeValueAsString(deleteMaterialResponse));

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

}
