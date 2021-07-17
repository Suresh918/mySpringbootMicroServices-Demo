package com.example.mirai.projectname.services.configuration;

import com.example.mirai.projectname.services.configuration.fixtures.JwtFactory;
import com.example.mirai.projectname.services.configuration.link.LinkRepository;
import com.example.mirai.projectname.services.configuration.link.models.Link;
import com.example.mirai.projectname.services.configuration.fixtures.TestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@ActiveProfiles("it")
@AutoConfigureMockMvc
class LinkIT {
    @Container
    private static final PostgreSQLContainer postgreSqlContainer = new PostgreSQLContainer("postgres:11");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private ConfigurationServiceConfigurationProperties configurationServiceConfigurationProperties;

    @DynamicPropertySource
    static void dataSourceProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSqlContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSqlContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSqlContainer::getPassword);
    }

    @Test
    void adminUserCanCreate() throws Exception {
        // Generate object populated with dummy data
        Link generatedLink = generateLink();

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                configurationServiceConfigurationProperties.getLinkAdminRoles());

        // Call create service
        mockMvc.perform(
                post("/links")
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedLink)))
                // Verify returned HTTP code to be 201
                .andExpect(status().isCreated())
                // Verify that object saved in database and object returned by service are exact match
                .andExpect(content().string(objectMapper.writeValueAsString(generatedLink)));
    }

    @Test
    void adminUserCanUpdate() throws Exception {
        // Generate object populated with dummy data
        Link generatedLink = generateLink();

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                configurationServiceConfigurationProperties.getLinkAdminRoles());

        // Insert generated object directly into database
        linkRepository.save(generatedLink);

        // Update object before calling service to generate explicit difference
        generatedLink.setLabel("changed label");

        // Call update service
        mockMvc.perform(
                put("/links/" + generatedLink.getName())
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedLink)))
                // Verify returned HTTP code to be 200
                .andExpect(status().isOk())
                // Verify that object saved in database and object returned by service are exact match
                .andExpect(content().string(objectMapper.writeValueAsString(generatedLink)));
    }

    @Test
    void normalUserUnauthorizedToCreate() throws Exception {
        // Generate object populated with dummy data
        Link generatedLink = generateLink();

        // Generate Bearer Token with user group that is not authorized to call operation
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                new String[]{"cug-regular-random-group"});

        // Call create service
        mockMvc.perform(
                post("/links")
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedLink)))
                // Verify returned HTTP code to be 403
                .andExpect(status().isForbidden());
    }

    @Test
    void normalUserUnauthorizedToUpdate() throws Exception {
        // Generate object populated with dummy data
        Link generatedLink = generateLink();

        // Generate Bearer Token with user group that is not authorized to call operation
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                new String[]{"cug-regular-random-group"});

        // Call create operation
        mockMvc.perform(
                put("/links")
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedLink)))
                // Verify returned HTTP code to be 403
                .andExpect(status().isForbidden());
    }

    @Test
    void adminUserCanDelete() throws Exception {
        // Generate object populated with dummy data
        Link generatedLink = generateLink();

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                configurationServiceConfigurationProperties.getLinkAdminRoles());

        // Insert generated settings directly into database
        linkRepository.save(generatedLink);

        // Call delete operation
        mockMvc.perform(
                delete("/links/" + generatedLink.getName())
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedLink)))
                .andExpect(status().isNoContent());

        // Verify that deleted object is not present in database
        Assertions.assertThat(linkRepository.findById(generatedLink.getName())).isEmpty();
    }

    @Test
    void normalUserUnauthorizedToDelete() throws Exception {
        // Generate object populated with dummy data
        Link generatedLink = generateLink();

        // Generate Bearer Token with user group that is not authorized to call operation
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                new String[]{"cug-regular-random-group"});

        // Insert generated settings directly into database
        linkRepository.save(generatedLink);

        // Call delete operation
        mockMvc.perform(
                delete("/links/" + generatedLink.getName())
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedLink)))
                .andExpect(status().isForbidden());

        // Verify that deleted object is not present in database
        Assertions.assertThat(linkRepository.findById(generatedLink.getName())).isNotEmpty();
    }


    @Test
    void getLinkWorks() throws Exception {
        // Generate object populated with dummy data
        Link generatedLink = generateLink();

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user", new String[]{"cug-regular-random-group"});

        // Insert generated object directly into database
        linkRepository.save(generatedLink);

        // Call get service
        mockMvc.perform(get("/links/" + generatedLink.getName())
                .header("Authorization", bearerToken))
                // Verify returned HTTP code to be 200
                .andExpect(status().isOk())
                // Verify that object saved in database and object returned by service are exact match
                .andExpect(content().string(objectMapper.writeValueAsString(generatedLink)));
    }

    @Test
    void getLinksWorks() throws Exception {
        // Generate object populated with dummy data
        Link firstGeneratedLink = generateLink();
        Link secondGeneratedLink = generateLink();
        Link thirdGeneratedLink = generateLink();

        // Insert generated object directly into database
        linkRepository.save(firstGeneratedLink);
        linkRepository.save(secondGeneratedLink);
        linkRepository.save(thirdGeneratedLink);

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user", new String[]{"cug-regular-random-group"});

        // Call get service
        mockMvc.perform(get("/links")
                .header("Authorization", bearerToken))
                // Verify returned HTTP code to be 200
                .andExpect(status().isOk())
                // Verify that all generated objects are present in the list
                .andExpect(jsonPath(String.format("$.[?(@.name in [%s,%s,%s])]",
                        firstGeneratedLink.getName(),
                        secondGeneratedLink.getName(),
                        thirdGeneratedLink.getName()),
                        Matchers.hasSize(3)));
    }

    private Link generateLink() throws MalformedURLException {
        Link link = new Link();
        link.setName("delta-1-" + TestUtil.generateRandomAlphanumericString(8).toLowerCase());
        link.setUrl(new URL("https://example.com/tc/webclient?argument={DELTA-REPORT-ID}&TC_file=redirs/viewdataset&open_reference={EXCEL-REFERENCE-ID}"));
        link.setLabel("Delta 1");
        link.setHint("Link to download Delta 1 report");
        return link;
    }
}
