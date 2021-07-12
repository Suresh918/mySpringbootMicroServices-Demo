package com.example.mirai.projectname.changerequestservice.mockservers;

import com.example.mirai.libraries.cerberus.diabom.config.DiaBomConfigurationProperties;
import com.example.mirai.libraries.cerberus.diabom.model.DiaBom;
import com.example.mirai.libraries.cerberus.diabom.model.Source;
import com.example.mirai.libraries.cerberus.functionalcluster.model.FunctionalCluster;
import com.example.mirai.libraries.cerberus.productbrakedownstructure.model.ProductBreakdownStructure;
import com.example.mirai.libraries.cerberus.shared.ErrorResponse;
import com.example.mirai.libraries.cerberus.shared.SuccessResponse;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

@Component
public class CerberusMockServer extends BaseMockServer {

    private final DiaBomConfigurationProperties diaBomConfigurationProperties;

    public CerberusMockServer(DiaBomConfigurationProperties diaBomConfigurationProperties) {
        this.diaBomConfigurationProperties = diaBomConfigurationProperties;
    }

    @Override
    protected int getMockServerPort() {
        return getMockServerPort(this.diaBomConfigurationProperties.getBaseUrl());
    }

    public void mockCerberusSuccessfulFetchOfDiaBom(Long changeRequestId) throws JsonProcessingException {

        DiaBom diaBom = new DiaBom();
        diaBom.setId(10L);
        diaBom.setChangeRequestId(changeRequestId);
        Source request = new Source(changeRequestId);
        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withPath("/dia-boms").withQueryStringParameter("change-request-id",changeRequestId.toString());
        String mockResponse = new ObjectMapper().writeValueAsString(diaBom);

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                .withBody(mockResponse);

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockCerberusSuccessfulDiaBomCreate(Long changeRequestId) throws JsonProcessingException {

        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setStatus("SUCCESS");
        Source request = new Source(changeRequestId);
        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.POST.name())
                .withHeader(HttpHeaders.ACCEPT, org.mockserver.model.MediaType.APPLICATION_JSON.toString())
                .withPath("/dia-boms").withBody(new ObjectMapper().writeValueAsString(request));
        String mockResponse = new ObjectMapper().writeValueAsString(successResponse);

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                .withBody(mockResponse);

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockCerberusSuccessfulProductBreakdownStructureFetch(Long changeRequestId,String productBreakdownStructureId) throws JsonProcessingException {

        ProductBreakdownStructure productBreakDownStructure = new ProductBreakdownStructure();
        productBreakDownStructure.setId(productBreakdownStructureId);
        productBreakDownStructure.setChangeRequestID(changeRequestId);
        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, org.mockserver.model.MediaType.APPLICATION_JSON.toString())
                .withPath("/product-breakdown-structures/"+productBreakdownStructureId);
        String mockResponse = new ObjectMapper().writeValueAsString(productBreakDownStructure);

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                .withBody(mockResponse);

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockCerberusSuccessfulFunctionalClusterByIdFetch(String functionalClusterId) throws JsonProcessingException {

        FunctionalCluster functionalCluster = new FunctionalCluster();
        functionalCluster.setName("FunctionalCluster");
        functionalCluster.setNumber(functionalClusterId);
        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, org.mockserver.model.MediaType.APPLICATION_JSON.toString())
                .withPath("/functional-clusters/"+functionalClusterId);
        String mockResponse = new ObjectMapper().writeValueAsString(functionalCluster);

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                .withBody(mockResponse);

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockCerberusSuccessfulfindFunctionalClusterByPartialIdFetch(String functionalClusterId) throws JsonProcessingException {

        FunctionalCluster functionalCluster = new FunctionalCluster();
        functionalCluster.setName("FunctionalCluster");
        functionalCluster.setNumber(functionalClusterId);
        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, org.mockserver.model.MediaType.APPLICATION_JSON.toString())
                .withPath("/functional-clusters")
                .withQueryStringParameter("id",functionalClusterId);
        String mockResponse = new ObjectMapper().writeValueAsString(functionalCluster);

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                .withBody(mockResponse);

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockCerberusSuccessfulfindProductBreakdownStructuresById(Long changeRequestId,String productBreakdownStructureId) throws JsonProcessingException {

        ProductBreakdownStructure productBreakDownStructure = new ProductBreakdownStructure();
        productBreakDownStructure.setId(productBreakdownStructureId);
        productBreakDownStructure.setChangeRequestID(changeRequestId);
        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, org.mockserver.model.MediaType.APPLICATION_JSON.toString())
                .withPath("/product-breakdown-structures")
                .withQueryStringParameter("id", productBreakdownStructureId);
        String mockResponse = new ObjectMapper().writeValueAsString(productBreakDownStructure);

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                .withBody(mockResponse);

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockCerberusSuccessfulupdateProductBreakdownStructure(Long changeRequestId, String productBreakdownStructureId) throws JsonProcessingException {

        ProductBreakdownStructure productBreakDownStructureRequest = new ProductBreakdownStructure();
        productBreakDownStructureRequest.setChangeRequestID(changeRequestId);
        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setStatus("SUCCESS");
        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.PUT.name())
                .withHeader(HttpHeaders.ACCEPT, org.mockserver.model.MediaType.APPLICATION_JSON.toString())
                .withPath("/product-breakdown-structures/"+productBreakdownStructureId).withBody(new ObjectMapper().writeValueAsString(productBreakDownStructureRequest));
        String mockResponse = new ObjectMapper().writeValueAsString(successResponse);

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                .withBody(mockResponse);

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockCerberusSuccessfulDeleteOfProductBreakdownStructure(Long changeRequestId,String productBreakdownStructureId) throws JsonProcessingException {

        ProductBreakdownStructure productBreakDownStructure = new ProductBreakdownStructure();
        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setStatus("SUCCESS");
        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.DELETE.name())
                .withHeader(HttpHeaders.ACCEPT, org.mockserver.model.MediaType.APPLICATION_JSON.toString())
                .withPath("/product-breakdown-structures/"+productBreakdownStructureId+"/change-requests/" + changeRequestId);
        String mockResponse = new ObjectMapper().writeValueAsString(successResponse);

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
