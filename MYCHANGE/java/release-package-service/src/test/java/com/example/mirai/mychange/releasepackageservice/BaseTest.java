package com.example.mirai.projectname.releasepackageservice;

import com.example.mirai.projectname.releasepackageservice.comment.repository.ReleasePackageCommentRepository;
import com.example.mirai.projectname.releasepackageservice.document.repository.ReleasePackageCommentDocumentRepository;
import com.example.mirai.projectname.releasepackageservice.document.repository.ReleasePackageDocumentRepository;
import com.example.mirai.projectname.releasepackageservice.fixtures.EntityInstanceManager;
import com.example.mirai.projectname.releasepackageservice.mockservers.CerberusMockServer;
import com.example.mirai.projectname.releasepackageservice.mockservers.ImpactedItemMockServer;
import com.example.mirai.projectname.releasepackageservice.mockservers.SapMdgMockServer;
import com.example.mirai.projectname.releasepackageservice.mockservers.TeamcenterMockServer;
import com.example.mirai.projectname.releasepackageservice.myteam.repository.ReleasePackageMyTeamRepository;
import com.example.mirai.projectname.releasepackageservice.releasepackage.repository.ReleasePackageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@ActiveProfiles("it")
@SpringBootTest
public abstract class BaseTest {
    /**
     * Prevent call to `issuer-uri`.
     */
    @MockBean
    protected JwtDecoder jwtDecoder;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected ReleasePackageRepository releasePackageRepository;
    @Autowired
    protected ReleasePackageMyTeamRepository releasePackageMyTeamRepository;
    @Autowired
    protected EntityInstanceManager entityInstanceManager;
    @Autowired
    protected ReleasePackageCommentRepository releasePackageCommentRepository;
    @Autowired
    protected ReleasePackageCommentDocumentRepository releasePackageCommentDocumentRepository;
    @Autowired
    protected ReleasePackageDocumentRepository releasePackageDocumentRepository;
    @Autowired
    protected WebApplicationContext webApplicationContext;
    @Autowired
    protected TeamcenterMockServer teamcenterMockServer;
    @Autowired
    protected SapMdgMockServer sapMdgMockServer;
    @Autowired
    protected CerberusMockServer cerberusMockServer;
    private MockMvc mvc;
    @Autowired
    protected ImpactedItemMockServer impactedItemMockServer;

    @BeforeAll
    static void setup(@Autowired DataSource dataSource, @Autowired TeamcenterMockServer teamcenterMockServer,
                      @Autowired SapMdgMockServer sapMdgMockServer,
                      @Autowired CerberusMockServer cerberusMockServer,@Autowired ImpactedItemMockServer impactedItemMockServer) throws SQLException {
        teamcenterMockServer.startMockServer();
        cerberusMockServer.startMockServer();
        sapMdgMockServer.startMockServer();
        impactedItemMockServer.startMockServer();
    }

    @AfterAll
    static void tearDown(@Autowired TeamcenterMockServer teamcenterMockServer, @Autowired CerberusMockServer cerberusMockServer,
                         @Autowired SapMdgMockServer sapMdgMockServer,@Autowired ImpactedItemMockServer impactedItemMockServer) {
        teamcenterMockServer.stopMockServer();
        cerberusMockServer.stopMockServer();
        sapMdgMockServer.stopMockServer();
        impactedItemMockServer.stopMockServer();
    }

    @BeforeEach
    void reset(@Autowired TeamcenterMockServer teamcenterMockServer, @Autowired CerberusMockServer cerberusMockServer,
               @Autowired SapMdgMockServer sapMdgMockServer,@Autowired ImpactedItemMockServer impactedItemMockServer) {
        teamcenterMockServer.resetMockServer();
        cerberusMockServer.resetMockServer();
        sapMdgMockServer.resetMockServer();
        impactedItemMockServer.resetMockServer();
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
