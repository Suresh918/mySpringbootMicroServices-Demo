package com.example.mirai.projectname.services.configuration;

import com.example.mirai.projectname.services.configuration.fixtures.JwtFactory;
import com.example.mirai.projectname.services.configuration.form.FormRepository;
import com.example.mirai.projectname.services.configuration.form.models.Action;
import com.example.mirai.projectname.services.configuration.form.models.Field;
import com.example.mirai.projectname.services.configuration.form.models.Form;
import com.example.mirai.projectname.services.configuration.form.models.Help;
import com.example.mirai.projectname.services.configuration.form.models.Option;
import com.example.mirai.projectname.services.configuration.form.models.Properties;
import com.example.mirai.projectname.services.configuration.form.models.Validators;
import com.example.mirai.projectname.services.configuration.fixtures.TestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@ActiveProfiles("it")
@AutoConfigureMockMvc
class FormIT {
    @Container
    private static final PostgreSQLContainer postgreSqlContainer = new PostgreSQLContainer("postgres:11");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private ConfigurationServiceConfigurationProperties configurationServiceConfigurationProperties;

    @DynamicPropertySource
    static void dataSourceProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSqlContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSqlContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSqlContainer::getPassword);
    }

    private static String groupViewExpectedContent;
    static {
        InputStream inputStream = FormIT.class.getResourceAsStream("/expectations/form/GroupView.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        groupViewExpectedContent = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    @Test
    void adminUserCanCreate() throws Exception {
        // Generate object populated with dummy data
        Form generatedForm = generateForm();

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                configurationServiceConfigurationProperties.getFormAdminRoles());

        // Call create service
        mockMvc.perform(
                post("/forms")
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedForm)))
                // Verify returned HTTP code to be 201
                .andExpect(status().isCreated())
                // Verify that object saved in database and object returned by service are exact match
                .andExpect(content().string(objectMapper.writeValueAsString(generatedForm)));
    }

    @Test
    void adminUserCanUpdate() throws Exception {
        // Generate object populated with dummy data
        Form generatedForm = generateForm();

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                configurationServiceConfigurationProperties.getFormAdminRoles());

        // Insert generated object directly into database
        formRepository.save(generatedForm);

        // Update object before calling service to generate explicit difference
        Arrays.stream(generatedForm.getFields()).findFirst().get().setName("testFieldName");

        // Call update service
        mockMvc.perform(
                put("/forms/" + generatedForm.getName())
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedForm)))
                // Verify returned HTTP code to be 200
                .andExpect(status().isOk())
                // Verify that object saved in database and object returned by service are exact match
                .andExpect(content().string(objectMapper.writeValueAsString(generatedForm)));
    }

    @Test
    void normalUserUnauthorizedToCreate() throws Exception {
        // Generate object populated with dummy data
        Form generatedForm = generateForm();

        // Generate Bearer Token with user group that is not authorized to call operation
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                new String[]{"cug-regular-random-group"});

        // Call create service
        mockMvc.perform(
                post("/forms")
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedForm)))
                // Verify returned HTTP code to be 403
                .andExpect(status().isForbidden());
    }

    @Test
    void normalUserUnauthorizedToUpdate() throws Exception {
        // Generate object populated with dummy data
        Form generatedForm = generateForm();

        // Generate Bearer Token with user group that is not authorized to call operation
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                new String[]{"cug-regular-random-group"});

        // Call create operation
        mockMvc.perform(
                put("/forms")
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedForm)))
                // Verify returned HTTP code to be 403
                .andExpect(status().isForbidden());
    }

    @Test
    void adminUserCanDelete() throws Exception {
        // Generate object populated with dummy data
        Form generatedForm = generateForm();

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                configurationServiceConfigurationProperties.getFormAdminRoles());

        // Insert generated settings directly into database
        formRepository.save(generatedForm);

        // Call delete operation
        mockMvc.perform(
                delete("/forms/" + generatedForm.getName())
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedForm)))
                .andExpect(status().isNoContent());

        // Verify that deleted object is not present in database
        Assertions.assertThat(formRepository.findById(generatedForm.getName())).isEmpty();
    }

    @Test
    void normalUserUnauthorizedToDelete() throws Exception {
        // Generate object populated with dummy data
        Form generatedForm = generateForm();

        // Generate Bearer Token with user group that is not authorized to call operation
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                new String[]{"cug-regular-random-group"});

        // Insert generated settings directly into database
        formRepository.save(generatedForm);

        // Call delete operation
        mockMvc.perform(
                delete("/forms/" + generatedForm.getName())
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedForm)))
                .andExpect(status().isForbidden());

        // Verify that deleted object is not present in database
        Assertions.assertThat(formRepository.findById(generatedForm.getName())).isNotEmpty();
    }


    @Test
    void getFormWorks() throws Exception {
        // Generate object populated with dummy data
        Form generatedForm = generateForm();

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user", new String[]{"cug-regular-random-group"});

        // Insert generated object directly into database
        formRepository.save(generatedForm);

        // Call get service
        mockMvc.perform(get("/forms/" + generatedForm.getName())
                .header("Authorization", bearerToken))
                // Verify returned HTTP code to be 200
                .andExpect(status().isOk())
                // Verify that object saved in database and object returned by service are exact match
                .andExpect(content().string(objectMapper.writeValueAsString(generatedForm)));
    }

    @Test
    void getGroupViewOfFormWorks() throws Exception {
        // Generate object populated with dummy data
        Form generatedForm = generateForm();

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user", new String[]{"cug-regular-random-group"});

        // Insert generated object directly into database
        formRepository.save(generatedForm);

        // Call get service
        MvcResult result = mockMvc.perform(get("/forms/" + generatedForm.getName() + "/group-view")
                .header("Authorization", bearerToken))
                // Verify returned HTTP code to be 200
                .andExpect(status().isOk()).andReturn();

                // Verify that object saved in database and object returned by service are exact match
                //.andExpect(content().string(objectMapper.writeValueAsString(generatedForm)));
        //Get Response
        String responseContent = result.getResponse().getContentAsString();

        JSONAssert.assertEquals("form is not as expected", groupViewExpectedContent,responseContent, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void getFormsWorks() throws Exception {
        // Generate object populated with dummy data
        Form firstGeneratedForm = generateForm();
        Form secondGeneratedForm = generateForm();
        Form thirdGeneratedForm = generateForm();

        // Insert generated object directly into database
        formRepository.save(firstGeneratedForm);
        formRepository.save(secondGeneratedForm);
        formRepository.save(thirdGeneratedForm);

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user", new String[]{"cug-regular-random-group"});

        // Call get service
        mockMvc.perform(get("/forms")
                .header("Authorization", bearerToken))
                // Verify returned HTTP code to be 200
                .andExpect(status().isOk())
                // Verify that all generated objects are present in the list
                .andExpect(jsonPath(String.format("$.[?(@.name in [%s,%s,%s])]",
                        firstGeneratedForm.getName(),
                        secondGeneratedForm.getName(),
                        thirdGeneratedForm.getName()),
                        Matchers.hasSize(3)));
    }

    Form generateForm() {
        Validators validators = new Validators();
        validators.setMinLength(1024);
        validators.setMaxLength(1024);
        validators.setPattern(Pattern.compile("^\\\\d+$"));

        Properties properties = new Properties();
        properties.setLabel("Priority of Analysis");
        properties.setHint("");
        properties.setPlaceholder("Priority of Analysis");
        properties.setGroup("Group Of Field");
        properties.setHelp(new Help(null,null,null,"<p> Select <ol><li> (critical)</li><li>(high)</li><li>(medium)</li><li>(low)</li></ol> For AIR issues, this should be clear from the priority set in AIR.</p>"));
        properties.setValidators(validators);

        Option[] options = new Option[]{
                new Option("1", "1 - Critical", 1),
                new Option("2", "2 - High", 2),
                new Option("3", "3 - Medium", 3),
                new Option("4", "4 - Low", 4)
        };

        Field field = new Field();
        field.setName("analysisPriority");
        field.setDataType("Number");
        field.setProperties(properties);
        field.setOptions(options);

        Action action = new Action();
        action.setName("ANALYZE-IMPACT");
        action.setLabel("Impact Analyzed");
        action.setTooltip("Change Status to 'Impact Analyzed'");
        action.setNotApplicableHandle("HIDE");
        action.setConfirmationMessage("");

        Form form = new Form();
        form.setName("change-request-" + TestUtil.generateRandomAlphanumericString(8).toLowerCase());
        form.setFields(new Field[]{field});
        form.setActions(new Action[]{action});

        return form;
    }
}
