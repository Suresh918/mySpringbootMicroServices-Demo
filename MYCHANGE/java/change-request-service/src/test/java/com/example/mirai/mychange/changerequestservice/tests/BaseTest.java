package com.example.mirai.projectname.changerequestservice.tests;

import com.example.mirai.libraries.myteam.service.MyTeamMemberService;
import com.example.mirai.projectname.changerequestservice.changerequest.repository.ChangeRequestRepository;
import com.example.mirai.projectname.changerequestservice.comment.repository.ChangeRequestCommentRepository;
import com.example.mirai.projectname.changerequestservice.completebusinesscase.repository.CompleteBusinessCaseRepository;
import com.example.mirai.projectname.changerequestservice.customerimpact.repository.CustomerImpactRepository;
import com.example.mirai.projectname.changerequestservice.document.repository.ChangeRequestCommentDocumentRepository;
import com.example.mirai.projectname.changerequestservice.document.repository.ChangeRequestDocumentRepository;
import com.example.mirai.projectname.changerequestservice.fixtures.EntityInstanceManager;
import com.example.mirai.projectname.changerequestservice.impactanalysis.repository.ImpactAnalysisRepository;
import com.example.mirai.projectname.changerequestservice.mockservers.*;
import com.example.mirai.projectname.changerequestservice.myteam.repository.ChangeRequestMyTeamRepository;
import com.example.mirai.projectname.changerequestservice.preinstallimpact.repository.PreinstallImpactRepository;
import com.example.mirai.projectname.changerequestservice.scope.repository.ScopeRepository;
import com.example.mirai.projectname.changerequestservice.solutiondefinition.repository.SolutionDefinitionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
/*import liquibase.pro.packaged.A;*/
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("it")
@SpringBootTest
public abstract class BaseTest {
    private static boolean viewsExecuted = false;
    /**
     * Prevent call to `issuer-uri`.
     */
    @MockBean
    protected JwtDecoder jwtDecoder;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ChangeRequestRepository changeRequestRepository;

    @Autowired
    protected ScopeRepository scopeRepository;

    @Autowired
    protected SolutionDefinitionRepository solutionDefinitionRepository;

    @Autowired
    protected ImpactAnalysisRepository impactAnalysisRepository;

    @Autowired
    protected CustomerImpactRepository customerImpactRepository;

    @Autowired
    protected CompleteBusinessCaseRepository completeBusinessCaseRepository;

    @Autowired
    protected PreinstallImpactRepository preinstallImpactRepository;


    @Autowired
    protected ChangeRequestMyTeamRepository changeRequestMyTeamRepository;

    @Autowired
    protected MyTeamMemberService myTeamMemberService;

    @Autowired
    protected EntityInstanceManager entityInstanceManager;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected UserServiceMockServer userServiceMockServer;

    @Autowired
    protected CerberusMockServer cerberusMockServer;

    @Autowired
    protected AirMockServer airMockServer;

    @Autowired
    protected HanaMockServer hanaMockServer;

    @Autowired
    protected GdsMockServer gdsMockServer;

    @Autowired
    protected SciaMockServer sciaMockServer;

    @Autowired
    protected ChangeRequestCommentRepository changeRequestCommentRepository;

    @Autowired
    protected ChangeRequestDocumentRepository changeRequestDocumentRepository;

    @Autowired
    protected ChangeRequestCommentDocumentRepository changeRequestCommentDocumentRepository;

    private MockMvc mvc;

    @BeforeAll
    static void setup(@Autowired UserServiceMockServer userServiceMockServer, @Autowired CerberusMockServer cerberusMockServer,@Autowired HanaMockServer hanaMockServer,
                      @Autowired GdsMockServer gdsMockServer,@Autowired AirMockServer airMockServer, @Autowired SciaMockServer sciaMockServer) {
       // userServiceMockServer.startMockServer();
        cerberusMockServer.startMockServer();
        hanaMockServer.startMockServer();
        gdsMockServer.startMockServer();
        airMockServer.startMockServer();
        // sciaMockServer.startMockServer();
    }

    @AfterAll
    static void tearDown(@Autowired UserServiceMockServer userServiceMockServer, @Autowired CerberusMockServer cerberusMockServer,
                         @Autowired HanaMockServer hanaMockServer, @Autowired GdsMockServer gdsMockServer,@Autowired AirMockServer airMockServer, @Autowired SciaMockServer sciaMockServer) {
       // userServiceMockServer.stopMockServer();
        cerberusMockServer.stopMockServer();
        hanaMockServer.stopMockServer();
        gdsMockServer.stopMockServer();
        airMockServer.stopMockServer();
       // sciaMockServer.stopMockServer();
    }

    @BeforeEach
    void reset(@Autowired UserServiceMockServer userServiceMockServer) {
       // userServiceMockServer.resetMockServer();
    }

    @BeforeEach
    void reset(@Autowired CerberusMockServer cerberusMockServer) {
        cerberusMockServer.resetMockServer();
    }

    @BeforeEach
    void reset(@Autowired AirMockServer airMockServer) {
        airMockServer.resetMockServer();
    }

    @BeforeEach
    void reset(@Autowired HanaMockServer hanaMockServer) {
        hanaMockServer.resetMockServer();
    }

    @BeforeEach
    void reset(@Autowired GdsMockServer gdsMockServer) {
        gdsMockServer.resetMockServer();
    }

    /*@BeforeEach
    void reset(@Autowired SciaMockServer sciaMockServer) {
        sciaMockServer.resetMockServer();
    }*/


    protected MockMvc getMockMvc() {
        if(mvc == null) {
            mvc = MockMvcBuilders
                    .webAppContextSetup(webApplicationContext)
                    .apply(SecurityMockMvcConfigurers.springSecurity())
                    .build();
        }
        return mvc;
    }


    /*Test Container Start*/
    /*private static final PostgreSQLContainer postgreSqlContainer;

    @DynamicPropertySource
    static void dataSourceProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSqlContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSqlContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSqlContainer::getPassword);
    }

    static {
        postgreSqlContainer = new PostgreSQLContainer("postgres:11");
        postgreSqlContainer.start();

    }*/
    /*Test Container End*/

}
