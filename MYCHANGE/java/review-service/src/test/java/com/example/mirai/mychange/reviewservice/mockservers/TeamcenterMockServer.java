package com.example.mirai.projectname.reviewservice.mockservers;

/*import com.example.mirai.libraries.teamcenter.ecn.model.SolutionItem;
import com.example.mirai.libraries.teamcenter.ecn.model.Tpd;*/
import com.example.mirai.libraries.deltareport.model.dto.SolutionItem;
import com.example.mirai.libraries.deltareport.model.dto.Tpd;
import com.example.mirai.projectname.reviewservice.fixtures.EntityPojoFactory;
import com.example.mirai.projectname.reviewservice.review.model.dto.ecn.*;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;


@Component
public class TeamcenterMockServer {
    @Autowired
    protected SapMdgMockServer sapMDGMockServer;
    private ClientAndServer mockClientAndServer;
   /* @Value("projectname.libraries.teamcenter.mock-server-port")
    String port*/;

    public void startMockServer() {
        mockClientAndServer = ClientAndServer.startClientAndServer(9999);
    }

    public void stopMockServer() {
        mockClientAndServer.stop();
    }

    public void resetMockServer() {
        mockClientAndServer.reset();
    }

    public void mockTeamcenterSuccessfulFetchforBomStructure(String teamcenterId) throws JsonProcessingException {

        List<SolutionItem> mocksolutionItemsList = EntityPojoFactory.createSolutionItemList();

        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .withPath("/engineering-change-notices/" + teamcenterId + "/bom-structure");

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(new ObjectMapper().writeValueAsString(mocksolutionItemsList));

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockTeamcenterSuccessfulFetchForSolutionItemMaterials(String teamcenterId) throws JsonProcessingException {

        List<SolutionItem> mockSolutionItemList = EntityPojoFactory.createSolutionItemList();

        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .withPath("/engineering-change-notices/" + teamcenterId + "/solution-items");

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(new ObjectMapper().writeValueAsString(mockSolutionItemList));

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockTeamcenterSuccessfulFetchForTpds(String teamcenterId) throws JsonProcessingException {

        List<Tpd> mockTpdList = EntityPojoFactory.createTpd();

        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .withPath("/engineering-change-notices/" + teamcenterId + "/tpds");

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(new ObjectMapper().writeValueAsString(mockTpdList));

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockTeamcenterSuccessfulFetchForSolutionItemDeltaList(String teamcenterId) throws JsonProcessingException {

        List<ReviewSolutionItemDelta> mockReviewSolutionItemDeltaList = EntityPojoFactory.createSolutionItemDeltaList();

        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .withPath("/engineering-change-notices/" + teamcenterId + "/solution-item-delta-list");

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(new ObjectMapper().writeValueAsString(mockReviewSolutionItemDeltaList));

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }


    public HttpResponse mockTeamcenterSuccessfulFetchForMaterialDeltaList(String teamcenterId, String sapId, Long id) throws JsonProcessingException {
        List<ReviewSolutionItemDelta> mockSolutionItemDeltaList = EntityPojoFactory.createSolutionItemDeltaList();
        List<ReviewMaterialDelta> mockMaterialList = EntityPojoFactory.createMaterialList();
        List<MaterialDelta> materialDeltaList = EntityPojoFactory.createMaterialDeltaList(mockSolutionItemDeltaList, mockMaterialList);
        mockTeamcenterSuccessfulFetchForSolutionItemDeltaList(teamcenterId);
        sapMDGMockServer.mockSAPMDGMaterialSuccessFulFetchForMaterialList("216810");

        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .withPath("/change-requests/" + id + "/material-delta");

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(new ObjectMapper().writeValueAsString(materialDeltaList));

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
        return httpResponse;
    }

    public HttpResponse mockTeamcenterSuccessfulFetchForSolutionItemSummaryList(String teamcenterId, String sapId, Long id) throws JsonProcessingException {
        List<SolutionItemSummary> solutionItemSummaryList = EntityPojoFactory.createSolutionItemSummaryList();
        mockTeamcenterSuccessfulFetchForSolutionItemMaterials(teamcenterId);
        sapMDGMockServer.mockSAPMDGMaterialSuccessFulFetchForMaterialList("216810");

        // Configure Mock Server http request configurationd
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .withPath("/change-requests/" + id + "/solution-item-summary");

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(new ObjectMapper().writeValueAsString(solutionItemSummaryList));

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
        return httpResponse;
    }


}
