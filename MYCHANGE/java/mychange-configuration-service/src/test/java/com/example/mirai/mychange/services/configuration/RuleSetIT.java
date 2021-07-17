package com.example.mirai.projectname.services.configuration;

import com.example.mirai.projectname.services.configuration.fixtures.JwtFactory;
import com.example.mirai.projectname.services.configuration.ruleset.RuleSetRepository;
import com.example.mirai.projectname.services.configuration.ruleset.models.Rule;
import com.example.mirai.projectname.services.configuration.ruleset.models.RuleSet;
import com.example.mirai.projectname.services.configuration.util.Util;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@ActiveProfiles("it")
@AutoConfigureMockMvc
class RuleSetIT {
    @Container
    private static final PostgreSQLContainer postgreSqlContainer = new PostgreSQLContainer("postgres:11");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RuleSetRepository ruleSetRepository;

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
        RuleSet generatedRuleSet = generateRuleSet(false);

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                configurationServiceConfigurationProperties.getRuleSetAdminRoles());

        // Call create service
        mockMvc.perform(
                post("/rule-sets")
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedRuleSet)))
                // Verify returned HTTP code to be 201
                .andExpect(status().isCreated())
                // Verify that object saved in database and object returned by service are exact match
                .andExpect(content().string(objectMapper.writeValueAsString(populateRuleSetName(generatedRuleSet))));
    }

    @Test
    void adminUserCanUpdate() throws Exception {
        // Generate object populated with dummy data
        RuleSet generatedRuleSet = generateRuleSet(true);

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                configurationServiceConfigurationProperties.getRuleSetAdminRoles());

        // Insert generated object directly into database
        ruleSetRepository.save(generatedRuleSet);

        // Update object before calling service to generate explicit difference
        generatedRuleSet.setLabel("changed label");

        // Call update service
        mockMvc.perform(
                put("/rule-sets/" + generatedRuleSet.getName())
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedRuleSet)))
                // Verify returned HTTP code to be 200
                .andExpect(status().isOk())
                // Verify that object saved in database and object returned by service are exact match
                .andExpect(content().string(objectMapper.writeValueAsString(generatedRuleSet)));
    }

    @Test
    void normalUserUnauthorizedToCreate() throws Exception {
        // Generate object populated with dummy data
        RuleSet generatedRuleSet = generateRuleSet(false);

        // Generate Bearer Token with user group that is not authorized to call operation
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                new String[]{"cug-regular-random-group"});

        // Call create service
        mockMvc.perform(
                post("/rule-sets")
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedRuleSet)))
                // Verify returned HTTP code to be 403
                .andExpect(status().isForbidden());
    }

    @Test
    void normalUserUnauthorizedToUpdate() throws Exception {
        // Generate object populated with dummy data
        RuleSet generatedRuleSet = generateRuleSet(true);

        // Generate Bearer Token with user group that is not authorized to call operation
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                new String[]{"cug-regular-random-group"});

        // Call create operation
        mockMvc.perform(
                put("/rule-sets")
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedRuleSet)))
                // Verify returned HTTP code to be 403
                .andExpect(status().isForbidden());
    }

    @Test
    void adminUserCanDelete() throws Exception {
        // Generate object populated with dummy data
        RuleSet generatedRuleSet = generateRuleSet(true);

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                configurationServiceConfigurationProperties.getRuleSetAdminRoles());

        // Insert generated settings directly into database
        ruleSetRepository.save(generatedRuleSet);

        // Call delete operation
        mockMvc.perform(
                delete("/rule-sets/" + generatedRuleSet.getName())
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedRuleSet)))
                .andExpect(status().isNoContent());

        // Verify that deleted object is not present in database
        Assertions.assertThat(ruleSetRepository.findById(generatedRuleSet.getName())).isEmpty();
    }

    @Test
    void normalUserUnauthorizedToDelete() throws Exception {
        // Generate object populated with dummy data
        RuleSet generatedRuleSet = generateRuleSet(true);

        // Generate Bearer Token with user group that is not authorized to call operation
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                new String[]{"cug-regular-random-group"});

        // Insert generated settings directly into database
        ruleSetRepository.save(generatedRuleSet);

        // Call delete operation
        mockMvc.perform(
                delete("/rule-sets/" + generatedRuleSet.getName())
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedRuleSet)))
                .andExpect(status().isForbidden());

        // Verify that deleted object is not present in database
        Assertions.assertThat(ruleSetRepository.findById(generatedRuleSet.getName())).isNotEmpty();
    }

    @Test
    void getRuleSetWorks() throws Exception {
        // Generate object populated with dummy data
        RuleSet generatedRuleSet = generateRuleSet(true);

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user", new String[]{"cug-regular-random-group"});

        // Insert generated object directly into database
        ruleSetRepository.save(generatedRuleSet);

        // Call get service
        mockMvc.perform(get("/rule-sets/" + generatedRuleSet.getName())
                .header("Authorization", bearerToken))
                // Verify returned HTTP code to be 200
                .andExpect(status().isOk())
                // Verify that object saved in database and object returned by service are exact match
                .andExpect(content().string(objectMapper.writeValueAsString(generatedRuleSet)));
    }

    @Test
    void getProductCategoriesWorks() throws Exception {
        // Generate object populated with dummy data
        RuleSet firstGeneratedRuleSet = generateRuleSet(true);
        RuleSet secondGeneratedRuleSet = generateRuleSet(true);
        RuleSet thirdGeneratedRuleSet = generateRuleSet(true);

        // Insert generated object directly into database
        ruleSetRepository.save(firstGeneratedRuleSet);
        ruleSetRepository.save(secondGeneratedRuleSet);
        ruleSetRepository.save(thirdGeneratedRuleSet);

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user", new String[]{"cug-regular-random-group"});

        // Call get service
        mockMvc.perform(get("/rule-sets")
                .header("Authorization", bearerToken))
                // Verify returned HTTP code to be 200
                .andExpect(status().isOk())
                // Verify that all generated objects are present in the list
                .andExpect(jsonPath(String.format("$.[?(@.name in [%s,%s,%s])]",
                        firstGeneratedRuleSet.getName(),
                        secondGeneratedRuleSet.getName(),
                        thirdGeneratedRuleSet.getName()),
                        Matchers.hasSize(3)));
    }

    private RuleSet generateRuleSet(Boolean populateRuleSetName) {
        RuleSet ruleSet = new RuleSet();
        ruleSet.setLabel("APP YS Wilton CB Rules" + "-" + TestUtil.generateRandomAlphanumericString(8).toLowerCase());
        if (populateRuleSetName)
            populateRuleSetName(ruleSet);
        ruleSet.setRules(new Rule[]{
                new Rule("a. Non-recurring cost >= €25K","help1"),
                new Rule("b. Impact on Configuration Calendar","help1"),
                new Rule("c. Cost of Goods increase > €0 (wrt Current or COG SDS)", "help1"),
                new Rule("d. Customer Impact Analysis = Major","help1"),
                new Rule("e. WIP/FAT upgrade","help1"),
                new Rule("f. Cycle Time increase > 0 min","help1"),
                new Rule("g. Impact on Facilities – Factory","help1"),
                new Rule("h. Tool investment MF > €25K","help1"),
                new Rule("i. Tool investment CS > €25K","help1"),
                new Rule("j. Tool investment SRC > €25K","help1"),
                new Rule("k. Test/Release Strat. = Release-Qualify (VHV/field)","help1")
        });
        return ruleSet;
    }

    private RuleSet populateRuleSetName(RuleSet ruleSet) {
        ruleSet.setName(Util.generateIdFromString(ruleSet.getLabel()));
        return ruleSet;
    }
}
