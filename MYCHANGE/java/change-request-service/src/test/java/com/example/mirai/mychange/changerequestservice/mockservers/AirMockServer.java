package com.example.mirai.projectname.changerequestservice.mockservers;

import com.example.mirai.libraries.air.problem.config.ProblemConfigurationProperties;
import com.example.mirai.libraries.air.problem.model.Problem;
import com.example.mirai.libraries.air.problem.model.ProblemUpdate;
import com.example.mirai.libraries.air.problem.model.ProblemUpdateResponse;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@Component
public class AirMockServer extends BaseMockServer {

    private final ProblemConfigurationProperties problemConfigurationProperties;

    public AirMockServer(ProblemConfigurationProperties problemConfigurationProperties) {
        this.problemConfigurationProperties = problemConfigurationProperties;
    }

    @Override
    protected int getMockServerPort() {
        return getMockServerPort(this.problemConfigurationProperties.getBaseUrl());
    }

    public void mockAirSuccessfulgetProblemByNumber(String number) throws JsonProcessingException {

        Problem problem = new Problem();
        problem.setNumber(number);
        ;
        problem.setDescription("Description");
        problem.setShortDescription("ShortDescription");
        problem.setSolutionDescription("SolutionDescription");

        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, org.mockserver.model.MediaType.APPLICATION_JSON.toString())
                .withPath("/problems/" + number);
        String mockResponse = new ObjectMapper().writeValueAsString(problem);

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                .withBody(mockResponse);

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockAirSuccessfulgetProblemByNumbers(String number) throws JsonProcessingException {

        List<Problem> problemsList = new ArrayList<>();
        Problem problem1 = new Problem();
        problem1.setNumber(number);
        problem1.setDescription("Description");
        problem1.setShortDescription("ShortDescription");
        problem1.setSolutionDescription("SolutionDescription");
        problemsList.add(problem1);
        List<String> numbers = Arrays.asList(new String[]{number});
        Map parameterMap = new HashMap<>();
        parameterMap.put("numbers", numbers);
        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, org.mockserver.model.MediaType.APPLICATION_JSON.toString())
                .withPath("/problems")
                .withQueryStringParameters(parameterMap);
        String mockResponse = new ObjectMapper().writeValueAsString(problemsList);

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                .withBody(mockResponse);

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockAirSuccessfulUpdateProblem(Long changeRequestId, String changeRequestStatus, String changeRequestTitle, List<String> airIds) throws JsonProcessingException {

        ProblemUpdate problemUpdate = new ProblemUpdate(changeRequestId, changeRequestStatus, changeRequestTitle, airIds);
        ProblemUpdateResponse problemUpdateResponse = new ProblemUpdateResponse();

        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.PUT.name())
                .withHeader(HttpHeaders.ACCEPT, org.mockserver.model.MediaType.APPLICATION_JSON.toString())
                .withPath("/problems").withBody(new ObjectMapper().writeValueAsString(problemUpdate));
        String mockResponse = new ObjectMapper().writeValueAsString(problemUpdateResponse);

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                .withBody(mockResponse);

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockAirSuccessfulfindProblemsByPartialNumber(String partialNumber) throws JsonProcessingException {

        List<Problem> problemsList = new ArrayList<>();
        Problem problem = new Problem();
        problem.setNumber(partialNumber);
        problem.setDescription("Description");
        problem.setShortDescription("ShortDescription");
        problem.setSolutionDescription("SolutionDescription");
        problemsList.add(problem);
        // Configure Mock Server http request configuration
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, org.mockserver.model.MediaType.APPLICATION_JSON.toString())
                .withPath("/problems/")
                .withQueryStringParameter("filter","number[equa]P1110*");
        String mockResponse = new ObjectMapper().writeValueAsString(problemsList);

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                .withBody(mockResponse);

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

}
