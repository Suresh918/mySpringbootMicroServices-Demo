package com.example.mirai.projectname.changerequestservice.mockservers;
/*
import com.example.mirai.projectname.libraries.user.config.UserConfigurationProperties;
import com.example.mirai.projectname.libraries.user.model.PreferredRole;*/
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.PropertyNamingStrategy;

import java.util.*;

@Component
public class UserServiceMockServer  {

   /* private final UserConfigurationProperties userConfigurationProperties;

    public UserServiceMockServer(UserConfigurationProperties userConfigurationProperties) {
        this.userConfigurationProperties = userConfigurationProperties;
    }

    @Override
    protected int getMockServerPort() {
        return getMockServerPort(this.userConfigurationProperties.getBaseUrl());
    }

    public void mockUserServicePreferredRoles(String userId) throws JsonProcessingException {
        UUID uuid = UUID.randomUUID();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        List<String> userIds = Arrays.asList(new String[]{userId});
        // Configure Mock Server http request configuration
        Map parameterMap = new HashMap<>();
        parameterMap.put("user-ids", userIds);
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withPath("/preferred-roles").withQueryStringParameters(parameterMap);

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(objectMapper.writeValueAsString(getPreferredRoles(userId)));

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public List<PreferredRole> getPreferredRoles(String userId) {
        List<PreferredRole> preferredRoleList = new ArrayList<>();
        PreferredRole preferredRole = new PreferredRole();
        preferredRole.setUserId(userId);
        preferredRole.setPreferredRoles(new String[]{"role1", "role2"});
        preferredRoleList.add(preferredRole);
        return preferredRoleList;
    }

    public void mockGetUserPreferredRoles(List<String> userIds) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        // Configure Mock Server http request configuration
        Map parameterMap = new HashMap<>();
        parameterMap.put("user-ids", userIds);
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withPath("/preferred-roles").withQueryStringParameters(parameterMap);

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(objectMapper.writeValueAsString(getPreferredRoles()));

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);
    }

    public List<PreferredRole> getPreferredRoles() {
        List<PreferredRole> preferredRoleList = new ArrayList<>();
        String[] preferredRoles = new String[2];
        preferredRoles[0]="role1";
        preferredRoles[1]="role2";
        PreferredRole preferredRole = new PreferredRole();
        preferredRole.setPreferredRoles(preferredRoles);
        preferredRole.setUserId("user");
        preferredRoleList.add(preferredRole);
        return preferredRoleList;
    }*/

}
