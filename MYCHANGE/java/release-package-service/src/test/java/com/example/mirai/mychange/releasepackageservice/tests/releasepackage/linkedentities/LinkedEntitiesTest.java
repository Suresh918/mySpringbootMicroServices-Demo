package com.example.mirai.projectname.releasepackageservice.tests.releasepackage.linkedentities;

import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.projectname.releasepackageservice.BaseTest;
import com.example.mirai.projectname.releasepackageservice.fixtures.JwtFactory;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageStatus;
import com.example.mirai.projectname.releasepackageservice.tests.myteams.MyTeamsTests;
import com.example.mirai.projectname.releasepackageservice.utils.PathGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MvcResult;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

public class LinkedEntitiesTest extends BaseTest {


    private static String linkedEntitiesExpectedContent;

    static {
        InputStream inputStream = MyTeamsTests.class.getResourceAsStream("/expectations/releasepackage/linkedentities/GetLinkedEntities.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        linkedEntitiesExpectedContent = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    }


    @ParameterizedTest(name = "{0} user is to perform linkedentities in release package status {1}")
    @CsvFileSource(resources = "/parameters/releasepackage/linkedentities/LinkedEntities.csv", numLinesToSkip = 1)
    void getLinkedEntitiesTest(String user, ReleasePackageStatus originalReleasePackageStatus) throws Exception {

        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();

        Long id = entityInstanceManager.createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES_LINKED_ENTITIES", originalReleasePackageStatus);

        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(id);
        assertThat(optionalReleasePackage.isPresent(), equalTo(true));

        String path = PathGenerator.getLinkedEntitiesPath(id);

        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, user);
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        MvcResult mvcResult = getMockMvc().perform(get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();

        String expectedContent = linkedEntitiesExpectedContent.replace("<DATA_IDENTIFIER>", dataIdentifier);
        JSONAssert.assertEquals("linked entities is not as expected", expectedContent, content, JSONCompareMode.NON_EXTENSIBLE);
    }
}
