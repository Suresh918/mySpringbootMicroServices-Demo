package com.example.mirai.projectname.releasepackageservice.mockservers;

import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.libraries.teamcenter.ecn.model.Ecn;
import com.example.mirai.libraries.teamcenter.ecn.model.Result;
import com.example.mirai.libraries.teamcenter.shared.ErrorResponse;
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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

@Component
public class ImpactedItemMockServer {
    private ClientAndServer mockClientAndServer;
   @Value("${projectname.libraries.impacteditem.mock-server-port}")
   private Integer port;

    public void startMockServer() {
        mockClientAndServer = ClientAndServer.startClientAndServer(9996);
    }

    public void stopMockServer() { mockClientAndServer.stop(); }

    public void resetMockServer() {
        mockClientAndServer.reset();
    }

    public void mockImpactedItemForSuccessfulPerformCaseAction(String caseAction,String releasePackageNumber) {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
       // String releasePackageNumber = dataIdentifier + "_release_package_number";
        //String caseAction=dataIdentifier+"_case_action";
        String changeObjectStatus =null;
        if(caseAction.equalsIgnoreCase("CREATE")){
            changeObjectStatus="3";
        }
        else if (caseAction.equalsIgnoreCase("READY")){
            caseAction="RELEASE";
            changeObjectStatus="4";
        }

        // Configure Mock Server http request configuration
       HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.PATCH.name())
                .withHeader(HttpHeaders.ACCEPT, org.mockserver.model.MediaType.APPLICATION_JSON.toString())
                .withPath("/change-objects/scope-items")
               //.withPathParameter("change-object-number", releasePackageNumber)
               //.withPathParameter("case-action", caseAction);
                .withQueryStringParameter("change-object-number", releasePackageNumber)
                .withQueryStringParameter("case-action", caseAction);
        //http://projectName-gateway.projectName:80/api/impacted-item-service/change-objects/scope-items?
        // change-object-number=6452c371-82a4-418c-b4f7-8ffd7409aca0_release_package_id&case-action=RELEASE
          // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(MediaType.APPLICATION_JSON)
               .withBody(changeObjectStatus);

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }


 /*   public void mockImpactedItemThrowsExceptionOnEcnCreation(String releasePackageNumber, String exceptionCode, int statusCode) throws com.fasterxml.jackson.core.JsonProcessingException {
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
    }*/


}
