package com.example.mirai.projectname.services.configuration;

import com.example.mirai.projectname.services.configuration.util.Util;
import com.example.mirai.projectname.services.configuration.fixtures.JwtFactory;
import com.example.mirai.projectname.services.configuration.fixtures.TestUtil;
import com.example.mirai.projectname.services.configuration.tag.TagRepository;
import com.example.mirai.projectname.services.configuration.tag.models.Tag;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@ActiveProfiles("it")
@AutoConfigureMockMvc
class TagIT {
    @Container
    private static final PostgreSQLContainer postgreSqlContainer = new PostgreSQLContainer("postgres:11");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TagRepository ruleSetRepository;

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
        Tag generatedTag = generateTag(false);

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                configurationServiceConfigurationProperties.getTagAdminRoles());

        // Call create service
        mockMvc.perform(
                post("/tags")
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedTag)))
                // Verify returned HTTP code to be 201
                .andExpect(status().isCreated())
                // Verify that object saved in database and object returned by service are exact match
                .andExpect(content().string(objectMapper.writeValueAsString(populateTagName(generatedTag))));
    }

    @Test
    void adminUserCanUpdate() throws Exception {
        // Generate object populated with dummy data
        Tag generatedTag = generateTag(true);

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                configurationServiceConfigurationProperties.getTagAdminRoles());

        // Insert generated object directly into database
        ruleSetRepository.save(generatedTag);

        // Update object before calling service to generate explicit difference
        generatedTag.setLabel("changed label");

        // Call update service
        mockMvc.perform(
                put("/tags/" + generatedTag.getName())
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedTag)))
                // Verify returned HTTP code to be 200
                .andExpect(status().isOk())
                // Verify that object saved in database and object returned by service are exact match
                .andExpect(content().string(objectMapper.writeValueAsString(generatedTag)));
    }

    @Test
    void normalUserUnauthorizedToCreate() throws Exception {
        // Generate object populated with dummy data
        Tag generatedTag = generateTag(false);

        // Generate Bearer Token with user group that is not authorized to call operation
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                new String[]{"cug-regular-random-group"});

        // Call create service
        mockMvc.perform(
                post("/tags")
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedTag)))
                // Verify returned HTTP code to be 403
                .andExpect(status().isForbidden());
    }

    @Test
    void normalUserUnauthorizedToUpdate() throws Exception {
        // Generate object populated with dummy data
        Tag generatedTag = generateTag(true);

        // Generate Bearer Token with user group that is not authorized to call operation
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                new String[]{"cug-regular-random-group"});

        // Call create operation
        mockMvc.perform(
                put("/tags")
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedTag)))
                // Verify returned HTTP code to be 403
                .andExpect(status().isForbidden());
    }

    @Test
    void adminUserCanDelete() throws Exception {
        // Generate object populated with dummy data
        Tag generatedTag = generateTag(true);

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                configurationServiceConfigurationProperties.getTagAdminRoles());

        // Insert generated settings directly into database
        ruleSetRepository.save(generatedTag);

        // Call delete operation
        mockMvc.perform(
                delete("/tags/" + generatedTag.getName())
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedTag)))
                .andExpect(status().isNoContent());

        // Verify that deleted object is not present in database
        Assertions.assertThat(ruleSetRepository.findById(generatedTag.getName())).isEmpty();
    }

    @Test
    void normalUserUnauthorizedToDelete() throws Exception {
        // Generate object populated with dummy data
        Tag generatedTag = generateTag(true);

        // Generate Bearer Token with user group that is not authorized to call operation
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                new String[]{"cug-regular-random-group"});

        // Insert generated settings directly into database
        ruleSetRepository.save(generatedTag);

        // Call delete operation
        mockMvc.perform(
                delete("/tags/" + generatedTag.getName())
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedTag)))
                .andExpect(status().isForbidden());

        // Verify that deleted object is not present in database
        Assertions.assertThat(ruleSetRepository.findById(generatedTag.getName())).isNotEmpty();
    }

    @Test
    void getTagWorks() throws Exception {
        // Generate object populated with dummy data
        Tag generatedTag = generateTag(true);

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user", new String[]{"cug-regular-random-group"});

        // Insert generated object directly into database
        ruleSetRepository.save(generatedTag);

        // Call get service
        mockMvc.perform(get("/tags/" + generatedTag.getName())
                .header("Authorization", bearerToken))
                // Verify returned HTTP code to be 200
                .andExpect(status().isOk())
                // Verify that object saved in database and object returned by service are exact match
                .andExpect(content().string(objectMapper.writeValueAsString(generatedTag)));
    }

    @Test
    void getTagsWorks() throws Exception {
        // Generate object populated with dummy data
        Tag firstGeneratedTag = generateTag(true);
        Tag secondGeneratedTag = generateTag(true);
        Tag thirdGeneratedTag = generateTag(true);

        // Insert generated object directly into database
        ruleSetRepository.save(firstGeneratedTag);
        ruleSetRepository.save(secondGeneratedTag);
        ruleSetRepository.save(thirdGeneratedTag);

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user", new String[]{"cug-regular-random-group"});

        // Call get service
        mockMvc.perform(get("/tags")
                .header("Authorization", bearerToken))
                // Verify returned HTTP code to be 200
                .andExpect(status().isOk())
                // Verify that all generated objects are present in the list
                .andExpect(jsonPath(String.format("$.[?(@.name in [%s,%s,%s])]",
                        firstGeneratedTag.getName(),
                        secondGeneratedTag.getName(),
                        thirdGeneratedTag.getName()),
                        Matchers.hasSize(3)));
    }

    private Tag generateTag(Boolean populateTagName) {
        Tag tag = new Tag();
        tag.setLabel("Corrective Release Package" + "-" + TestUtil.generateRandomAlphanumericString(8).toLowerCase());
        if (populateTagName)
            tag = populateTagName(tag);
        return tag;
    }

    private Tag populateTagName(Tag tag) {
        tag.setName(Util.generateIdFromString(tag.getLabel()));
        return tag;
    }
}
