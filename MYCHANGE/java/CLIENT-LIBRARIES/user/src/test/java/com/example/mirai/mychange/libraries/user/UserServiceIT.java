package com.example.mirai.projectname.libraries.user;

import com.example.mirai.projectname.libraries.user.config.UserConfigurationProperties;
import com.example.mirai.projectname.libraries.user.model.PreferredRole;
import com.example.mirai.projectname.libraries.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.mockserver.model.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserService.class, UserConfigurationProperties.class},
        initializers = {ConfigFileApplicationContextInitializer.class})
public class UserServiceIT {

    private static ClientAndServer mockClientAndServer;

    @Autowired
    UserService userService;

    @Autowired
    UserConfigurationProperties userConfigurationProperties;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeAll
    public static void startMockServer() {
        mockClientAndServer = ClientAndServer.startClientAndServer(9999);
    }

    @AfterAll
    public static void stopMockServer() {
        mockClientAndServer.stop();
    }

    @BeforeEach
    public void resetMockServer() {
        mockClientAndServer.reset();
    }

    @Test
    void getPreferredRolesByUserIds() throws JsonProcessingException {
        List<String> userIds = Arrays.asList(new String[]{"user1", "user2"});

        // Generate Mock Server response value
        List<PreferredRole> mockResponse = generatePreferredRoles(userIds);

        // Configure Mock Server http request configuration
        Map parameterMap = new HashMap<>();
        parameterMap.put("user-ids", userIds);
        HttpRequest httpRequest = new HttpRequest()
                .withMethod(HttpMethod.GET.name())
                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .withQueryStringParameters(parameterMap);

        // Configure Mock Server http response configuration
        HttpResponse httpResponse = new HttpResponse()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(new ObjectMapper().writeValueAsString(mockResponse));

        // Configure Mock Server for the test
        mockClientAndServer.when(httpRequest).respond(httpResponse);

        // Call the service
        List<PreferredRole> preferredRoles = userService.getPreferredRolesByUserIds(userIds);

        // Verify that server received request
        mockClientAndServer.verify(httpRequest);

        // Assert that result object is properly parsed
        assertEquals(preferredRoles.size(), userIds.size());
        assertEquals(preferredRoles.get(0).getUserId(), userIds.get(0));
        assertEquals(preferredRoles.get(1).getUserId(), userIds.get(1));
        assertArrayEquals(preferredRoles.get(0).getPreferredRoles(), mockResponse.get(0).getPreferredRoles());
        assertArrayEquals(preferredRoles.get(1).getPreferredRoles(), mockResponse.get(1).getPreferredRoles());
    }

    private List<PreferredRole> generatePreferredRoles(List<String> userIds) {
        List<PreferredRole> preferredRoleList = new ArrayList<>();
        userIds.forEach(userId -> {
            PreferredRole preferredRole = new PreferredRole();
            String[] preferredRoles = new String[]{"role1", "role2"};
            preferredRole.setPreferredRoles(preferredRoles);
            preferredRole.setUserId(userId);
            preferredRoleList.add(preferredRole);
        });
        return preferredRoleList;
    }
}
