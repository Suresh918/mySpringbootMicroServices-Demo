package com.example.mirai.projectname.reviewservice.tests.review;

import com.example.mirai.libraries.websecurity.WebSecurityConfigurer;
import com.example.mirai.projectname.reviewservice.fixtures.JwtFactory;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.review.model.ReviewStatus;
import com.example.mirai.projectname.reviewservice.tests.BaseTest;
import org.junit.jupiter.api.Test;
import org.mockserver.model.HttpResponse;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EcnReviewTest extends BaseTest {


    private static String getReviewBomStructure;
    private static String getReviewTpd;
    private static String getSolutionItemList;
    private static String getMaterialList;
    private static String getMaterialDeltaList;
    private static String getSolutionItemSummaryList;

    static {
        InputStream getReviewBomStructureInputStream = ReviewTests.class.getResourceAsStream("/expectations/ecnreview/GetReviewBomStructure.txt");
        BufferedReader getReviewBomStructureBufferedReader = new BufferedReader(new InputStreamReader(getReviewBomStructureInputStream, StandardCharsets.UTF_8));
        getReviewBomStructure = getReviewBomStructureBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getReviewTpdInputStream = ReviewTests.class.getResourceAsStream("/expectations/ecnreview/GetReviewTpd.txt");
        BufferedReader getReviewTpdBufferedReader = new BufferedReader(new InputStreamReader(getReviewTpdInputStream, StandardCharsets.UTF_8));
        getReviewTpd = getReviewTpdBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getSolutionItemInputStream = ReviewTests.class.getResourceAsStream("/expectations/ecnreview/GetSolutionItem.txt");
        BufferedReader getSolutionItemBufferedReader = new BufferedReader(new InputStreamReader(getSolutionItemInputStream, StandardCharsets.UTF_8));
        getSolutionItemList = getSolutionItemBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getMaterialInputStream = ReviewTests.class.getResourceAsStream("/expectations/ecnreview/GetMaterial.txt");
        BufferedReader getMaterialInputStreamBufferedReader = new BufferedReader(new InputStreamReader(getMaterialInputStream, StandardCharsets.UTF_8));
        getMaterialList = getMaterialInputStreamBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getMaterialDeltaInputStream = ReviewTests.class.getResourceAsStream("/expectations/ecnreview/GetMaterialDelta.txt");
        BufferedReader getMaterialDeltaBufferedReader = new BufferedReader(new InputStreamReader(getMaterialDeltaInputStream, StandardCharsets.UTF_8));
        getMaterialDeltaList = getMaterialDeltaBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        InputStream getSolutionItemSummaryInputStream = ReviewTests.class.getResourceAsStream("/expectations/ecnreview/GetSolutionItemSummary.txt");
        BufferedReader getSolutionItemSummaryBufferedReader = new BufferedReader(new InputStreamReader(getSolutionItemSummaryInputStream, StandardCharsets.UTF_8));
        getSolutionItemSummaryList = getSolutionItemSummaryBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

    }

    @Test
    void getReviewBomStructure() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-authorized-user");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        Long id = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewStatus.OPENED);
        Optional<Review> optionalReview = reviewRepository.findById(id);
        String teamcenterId = getContextIdByType(optionalReview.get(), "TEAMCENTER");
        teamcenterMockServer.mockTeamcenterSuccessfulFetchforBomStructure(teamcenterId);

        String path = "/reviews/" + id + "/bom-structure";
        MvcResult result = getMockMvc().perform(get(path))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JSONAssert.assertEquals("Review Bom Structure as expected", getReviewBomStructure, responseContent, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void getReviewBomStructureNotFound() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-authorized-user");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        String path = "/reviews/" + 12345 + "/bom-structure";
        getMockMvc().perform(get(path))
                .andExpect(status().isNotFound());

    }

    @Test
    void getSolutionItemList() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-authorized-user");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        Long id = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewStatus.OPENED);
        Optional<Review> optionalReview = reviewRepository.findById(id);
        String teamcenterId = getContextIdByType(optionalReview.get(), "TEAMCENTER");
        teamcenterMockServer.mockTeamcenterSuccessfulFetchForSolutionItemMaterials(teamcenterId);

        String path = "/reviews/" + id + "/solution-items";
        MvcResult result = getMockMvc().perform(get(path))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JSONAssert.assertEquals("Soultion Item List as expected", getSolutionItemList, responseContent, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void getSolutionItemListNotFound() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-authorized-user");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        String path = "/reviews/" + 12345 + "/solution-items";
        getMockMvc().perform(get(path))
                .andExpect(status().isNotFound());
    }

    @Test
    void getReviewTpd() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-authorized-user");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        Long id = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewStatus.OPENED);
        Optional<Review> optionalReview = reviewRepository.findById(id);
        String teamcenterId = getContextIdByType(optionalReview.get(), "TEAMCENTER");
        teamcenterMockServer.mockTeamcenterSuccessfulFetchForTpds(teamcenterId);

        String path = "/reviews/" + id + "/tpds";
        MvcResult result = getMockMvc().perform(get(path))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JSONAssert.assertEquals("tpd as expected", getReviewTpd, responseContent, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void getReviewTpdsNotFound() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-authorized-user");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        String path = "/reviews/" + 12345 + "/tpds";
        getMockMvc().perform(get(path))
                .andExpect(status().isNotFound());
    }

    //@Test
    void getMaterialList() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-authorized-user");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        Long id = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewStatus.OPENED);
        Optional<Review> optionalReview = reviewRepository.findById(id);
        String sapId = getContextIdByType(optionalReview.get(), "MDG-CR");
        sapMDGMockServer.mockSAPMDGMaterialSuccessFulFetchForMaterialList(sapId);
        String path = "/reviews/" + id + "/materials";
        MvcResult result = getMockMvc().perform(get(path))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JSONAssert.assertEquals("material list as expected", getMaterialList, responseContent, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void getMaterialListNotFound() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-authorized-user");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        String path = "/reviews/" + 12345 + "/materials";
        getMockMvc().perform(get(path))
                .andExpect(status().isNotFound());
    }

    //@Test
    void getMaterialDeltaList() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-authorized-user");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        Long id = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewStatus.OPENED);
        Optional<Review> optionalReview = reviewRepository.findById(id);
        String teamcenterId = getContextIdByType(optionalReview.get(), "TEAMCENTER");
        String sapId = getContextIdByType(optionalReview.get(), "MDG-CR");
        HttpResponse httpResponse = teamcenterMockServer.mockTeamcenterSuccessfulFetchForMaterialDeltaList(teamcenterId, sapId, id);
        String responseContent = httpResponse.getBody().getValue().toString();
        JSONAssert.assertEquals("Material Delta List as expected", getMaterialDeltaList, responseContent, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void getSolutionItemSummaryList() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-authorized-user");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        Long id = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewStatus.OPENED);
        Optional<Review> optionalReview = reviewRepository.findById(id);
        String teamcenterId = getContextIdByType(optionalReview.get(), "TEAMCENTER");
        String sapId = getContextIdByType(optionalReview.get(), "MDG-CR");
        HttpResponse httpResponse = teamcenterMockServer.mockTeamcenterSuccessfulFetchForSolutionItemSummaryList(teamcenterId, sapId, id);
        String responseContent = httpResponse.getBody().getValue().toString();
        JSONAssert.assertEquals("Solution Item Summary List as expected", getSolutionItemSummaryList, responseContent, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void deleteMaterialFromChangeRequestByMdgCr() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-authorized-user");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));

        Long id = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewStatus.OPENED);
        Optional<Review> optionalReview = reviewRepository.findById(id);
        String mdgCrId = getContextIdByType(optionalReview.get(), "MDG-CR");
        sapMDGMockServer.mockSapMdgSuccessFullDeleteForMaterialFromChangeRequest(mdgCrId);

        String path = "/reviews/change-requests/" + optionalReview.get().getId() + "/materials?material-number=" + 122;
        MvcResult result = getMockMvc().perform(delete(path))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        String expectedStr = "{\"status\":\"SUCCESS\"}";
        String responseContent = result.getResponse().getContentAsString();
        JSONAssert.assertEquals("response as expected", expectedStr, responseContent, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void deleteMaterialFromChangeRequestForMdgCrContextNotExistException() throws Exception {
        UUID uuid = UUID.randomUUID();
        String dataIdentifier = uuid.toString();
        Jwt jwt = JwtFactory.getJwtToken(dataIdentifier, "cug-projectname-authorized-user");
        SecurityContextHolder.getContext().setAuthentication(WebSecurityConfigurer.authenticationConverter().convert(jwt));
        Long id = entityInstanceManager.createReviewAndSetStatus(dataIdentifier, "ALL_PROPERTIES", ReviewStatus.OPENED);
        Optional<Review> optionalReview = reviewRepository.findById(id);
        String mdgCrId = getContextIdByType(optionalReview.get(), "MDG-CR");
        sapMDGMockServer.mockSapMdgSuccessFullDeleteForMaterialFromChangeRequest(mdgCrId);
        optionalReview.get().getContexts().get(3).setType("WITHOUTMDG-CR");
        reviewRepository.save(optionalReview.get());
        String path = "/reviews/change-requests/" + optionalReview.get().getId() + "/materials?material-number=" + 122;
        getMockMvc().perform(delete(path))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }


    private String getContextIdByType(Review review, String type) {
        return review.getContexts().stream().filter(context -> context.getType().equals(type)).findFirst().get().getContextId();
    }
}
