package com.example.mirai.projectname.reviewservice.tests;

import com.example.mirai.projectname.reviewservice.fixtures.EntityInstanceManager;
import com.example.mirai.projectname.reviewservice.mockservers.SapMdgMockServer;
import com.example.mirai.projectname.reviewservice.mockservers.TeamcenterMockServer;
import com.example.mirai.projectname.reviewservice.review.repository.ReviewRepository;
import com.example.mirai.projectname.reviewservice.reviewentry.repository.ReviewEntryRepository;
import com.example.mirai.projectname.reviewservice.reviewtask.repository.ReviewTaskRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("integration-test")
@SpringBootTest
public abstract class BaseTest {
    /**
     * Prevent call to `issuer-uri`.
     */
    @MockBean
    protected JwtDecoder jwtDecoder;
    @Autowired
    protected ReviewRepository reviewRepository;
    @Autowired
    protected ReviewTaskRepository reviewTaskRepository;
    @Autowired
    protected ReviewEntryRepository reviewEntryRepository;
    @Autowired
    protected EntityInstanceManager entityInstanceManager;
    @Autowired
    protected WebApplicationContext webApplicationContext;
    @Autowired
    protected TeamcenterMockServer teamcenterMockServer;
    @Autowired
    protected SapMdgMockServer sapMDGMockServer;

    private MockMvc mvc;

    @BeforeAll
    static void setup(@Autowired TeamcenterMockServer teamcenterMockServer, @Autowired SapMdgMockServer sapMDGMockServer) {
        teamcenterMockServer.startMockServer();
        sapMDGMockServer.startMockServer();
    }

    @AfterAll
    static void tearDown(@Autowired TeamcenterMockServer teamcenterMockServer, @Autowired SapMdgMockServer sapMDGMockServer) {
        teamcenterMockServer.stopMockServer();
        sapMDGMockServer.stopMockServer();
    }

    @BeforeEach
    void reset(@Autowired TeamcenterMockServer teamcenterMockServer, @Autowired SapMdgMockServer sapMDGMockServer) {
        teamcenterMockServer.resetMockServer();
        sapMDGMockServer.resetMockServer();
    }

    protected MockMvc getMockMvc() {
        if (mvc == null) {
            mvc = MockMvcBuilders
                    .webAppContextSetup(webApplicationContext)
                    //.apply(SecurityMockMvcConfigurers.springSecurity())
                    .build();
        }
        return mvc;
    }
}
