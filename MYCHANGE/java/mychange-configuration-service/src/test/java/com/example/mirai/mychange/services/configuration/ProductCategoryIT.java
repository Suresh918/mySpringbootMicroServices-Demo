package com.example.mirai.projectname.services.configuration;

import com.example.mirai.projectname.services.configuration.productcategory.ProductCategoryRepository;
import com.example.mirai.projectname.services.configuration.productcategory.models.Product;
import com.example.mirai.projectname.services.configuration.productcategory.models.ProductCategory;
import com.example.mirai.projectname.services.configuration.util.Util;
import com.example.mirai.projectname.services.configuration.fixtures.JwtFactory;
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
class ProductCategoryIT {
    @Container
    private static final PostgreSQLContainer postgreSqlContainer = new PostgreSQLContainer("postgres:11");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

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
        ProductCategory generatedProductCategory = generateProductCategory(false);

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                configurationServiceConfigurationProperties.getProductCategoryAdminRoles());

        // Call create service
        mockMvc.perform(
                post("/product-categories")
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedProductCategory)))
                // Verify returned HTTP code to be 201
                .andExpect(status().isCreated())
                // Verify that object saved in database and object returned by service are exact match
                .andExpect(content().string(objectMapper.writeValueAsString(populateProductCategoryName(generatedProductCategory))));
    }

    @Test
    void adminUserCanUpdate() throws Exception {
        // Generate object populated with dummy data
        ProductCategory generatedProductCategory = generateProductCategory(true);

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                configurationServiceConfigurationProperties.getProductCategoryAdminRoles());

        // Insert generated object directly into database
        productCategoryRepository.save(generatedProductCategory);

        // Update object before calling service to generate explicit difference
        generatedProductCategory.setLabel("changed label");

        // Call update service
        mockMvc.perform(
                put("/product-categories/" + generatedProductCategory.getName())
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedProductCategory)))
                // Verify returned HTTP code to be 200
                .andExpect(status().isOk())
                // Verify that object saved in database and object returned by service are exact match
                .andExpect(content().string(objectMapper.writeValueAsString(generatedProductCategory)));
    }

    @Test
    void normalUserUnauthorizedToCreate() throws Exception {
        // Generate object populated with dummy data
        ProductCategory generatedProductCategory = generateProductCategory(false);

        // Generate Bearer Token with user group that is not authorized to call operation
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                new String[]{"cug-regular-random-group"});

        // Call create service
        mockMvc.perform(
                post("/product-categories")
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedProductCategory)))
                // Verify returned HTTP code to be 403
                .andExpect(status().isForbidden());
    }

    @Test
    void normalUserUnauthorizedToUpdate() throws Exception {
        // Generate object populated with dummy data
        ProductCategory generatedProductCategory = generateProductCategory(true);

        // Generate Bearer Token with user group that is not authorized to call operation
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                new String[]{"cug-regular-random-group"});

        // Call create operation
        mockMvc.perform(
                put("/product-categories")
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedProductCategory)))
                // Verify returned HTTP code to be 403
                .andExpect(status().isForbidden());
    }

    @Test
    void adminUserCanDelete() throws Exception {
        // Generate object populated with dummy data
        ProductCategory generatedProductCategory = generateProductCategory(true);

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                configurationServiceConfigurationProperties.getProductCategoryAdminRoles());

        // Insert generated settings directly into database
        productCategoryRepository.save(generatedProductCategory);

        // Call delete operation
        mockMvc.perform(
                delete("/product-categories/" + generatedProductCategory.getName())
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedProductCategory)))
                .andExpect(status().isNoContent());

        // Verify that deleted object is not present in database
        Assertions.assertThat(productCategoryRepository.findById(generatedProductCategory.getName())).isEmpty();
    }

    @Test
    void normalUserUnauthorizedToDelete() throws Exception {
        // Generate object populated with dummy data
        ProductCategory generatedProductCategory = generateProductCategory(true);

        // Generate Bearer Token with user group that is not authorized to call operation
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user",
                new String[]{"cug-regular-random-group"});

        // Insert generated settings directly into database
        productCategoryRepository.save(generatedProductCategory);

        // Call delete operation
        mockMvc.perform(
                delete("/product-categories/" + generatedProductCategory.getName())
                        .header("Authorization", bearerToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(generatedProductCategory)))
                .andExpect(status().isForbidden());

        // Verify that deleted object is not present in database
        Assertions.assertThat(productCategoryRepository.findById(generatedProductCategory.getName())).isNotEmpty();
    }


    @Test
    void getProductCategoryWorks() throws Exception {
        // Generate object populated with dummy data
        ProductCategory generatedProductCategory = generateProductCategory(true);

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user", new String[]{"cug-regular-random-group"});

        // Insert generated object directly into database
        productCategoryRepository.save(generatedProductCategory);

        // Call get service
        mockMvc.perform(get("/product-categories/" + generatedProductCategory.getName())
                .header("Authorization", bearerToken))
                // Verify returned HTTP code to be 200
                .andExpect(status().isOk())
                // Verify that object saved in database and object returned by service are exact match
                .andExpect(content().string(objectMapper.writeValueAsString(generatedProductCategory)));
    }

    @Test
    void getProductCategoriesWorks() throws Exception {
        // Generate object populated with dummy data
        ProductCategory firstGeneratedProductCategory = generateProductCategory(true);
        ProductCategory secondGeneratedProductCategory = generateProductCategory(true);
        ProductCategory thirdGeneratedProductCategory = generateProductCategory(true);

        // Insert generated object directly into database
        productCategoryRepository.save(firstGeneratedProductCategory);
        productCategoryRepository.save(secondGeneratedProductCategory);
        productCategoryRepository.save(thirdGeneratedProductCategory);

        // Generate Bearer Token
        String bearerToken = "Bearer " + JwtFactory.generateJwtToken("configuration_user", new String[]{"cug-regular-random-group"});

        // Call get service
        mockMvc.perform(get("/product-categories")
                .header("Authorization", bearerToken))
                // Verify returned HTTP code to be 200
                .andExpect(status().isOk())
                // Verify that all generated objects are present in the list
                .andExpect(jsonPath(String.format("$.[?(@.name in [%s,%s,%s])]",
                        firstGeneratedProductCategory.getName(),
                        secondGeneratedProductCategory.getName(),
                        thirdGeneratedProductCategory.getName()),
                        Matchers.hasSize(3)));
    }

    private ProductCategory generateProductCategory(Boolean populateProductCategoryName) {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setLabel("3D NAND" + "-" + TestUtil.generateRandomAlphanumericString(8).toLowerCase());
        if (populateProductCategoryName)
            populateProductCategoryName(productCategory);
        Product[] products = new Product[]{
                new Product("3D NAND"),
                new Product("3D NAND AWACS"),
                new Product("3D NAND PEP-Align 1"),
                new Product("3D NAND TOP-Align")
        };
        productCategory.setProducts(products);
        return productCategory;
    }

    private ProductCategory populateProductCategoryName(ProductCategory productCategory) {
        productCategory.setName(Util.generateIdFromString(productCategory.getLabel()));
        return productCategory;
    }
}
