package com.example.mirai.projectname.changerequestservice.mockservers;

import com.example.mirai.libraries.core.model.Group;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.gds.config.GdsConfigurationProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GdsMockServer extends BaseMockServer {

    private final GdsConfigurationProperties gdsConfigurationProperties;
    private final ObjectMapper objectMapper;

    public GdsMockServer(GdsConfigurationProperties gdsConfigurationProperties, ObjectMapper objectMapper) {
        this.gdsConfigurationProperties = gdsConfigurationProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    protected int getMockServerPort() {
        return getMockServerPort(this.gdsConfigurationProperties.getBaseUrl());
    }

    public void mockGdsToGetUserDetails() throws com.fasterxml.jackson.core.JsonProcessingException {

        String userId="user";
        // Configure Mock Server http request configuration
        User user = new User();
        user.setUserId("user");
        user.setAbbreviation("user abbreviation");
        user.setEmail("user email");
        user.setDepartmentName("user department");
        user.setFullName("user full name");
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .withPath("/users/" + userId);
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(objectMapper.writeValueAsString(user));

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public void mockGdsToGetGroupDetails(List<String> groupIds) throws com.fasterxml.jackson.core.JsonProcessingException {

        // Configure Mock Server http request configuration
        Group group = new Group();
        group.setGroupId(groupIds.get(0));
        List<User> userList = new ArrayList<>();
        User user = new User();
        user.setUserId("user");
        user.setAbbreviation("user abbreviation");
        user.setEmail("user email");
        user.setDepartmentName("user department");
        user.setFullName("user full name");
        userList.add(user);
        group.setMembers(userList);
        List<Group> responseGroupList = new ArrayList<>();
        responseGroupList.add(group);
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .withPath("/groups")
                .withQueryStringParameter("group_id", groupIds.get(0));
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(objectMapper.writeValueAsString(responseGroupList));

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

}
