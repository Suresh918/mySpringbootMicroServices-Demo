package com.example.mirai.projectname.releasepackageservice.tests.prerequisites;

import com.example.mirai.projectname.releasepackageservice.releasepackage.model.PrerequisiteReleasePackage;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONArray;
import org.json.JSONException;


import java.util.LinkedHashMap;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class Validator {

    public static void addPrerequisitesIsSuccessful(String releasePackagePrerequisites, List<PrerequisiteReleasePackage> prerequisitesResponse) throws JSONException {
          JSONArray jsonArray = JsonPath.parse(releasePackagePrerequisites).read("results");
        LinkedHashMap prerequisitemap = new LinkedHashMap();
        prerequisitemap = (LinkedHashMap) jsonArray.get(0);
        assertThat("sequence is not same", prerequisitemap.get("sequence_number"), equalTo(prerequisitesResponse.get(0).getSequence()));
        assertThat("release package number is not same", prerequisitemap.get("release_package_number"), equalTo(prerequisitesResponse.get(0).getReleasePackageNumber()));
    }

    public static void reorderPrerequisitesIsSuccessful(String releasePackagePrerequisites, List<PrerequisiteReleasePackage> prerequisitesResponse) throws JSONException {

        JSONArray jsonArray = JsonPath.parse(releasePackagePrerequisites).read("release_package_prerequisites.results");
        LinkedHashMap prerequisitemap = new LinkedHashMap();
        prerequisitemap = (LinkedHashMap) jsonArray.get(0);
        assertThat("sequence is not same", prerequisitemap.get("sequence_number"), equalTo(prerequisitesResponse.get(0).getSequence()));
        assertThat("release package number is not same", prerequisitemap.get("release_package_number"), equalTo(prerequisitesResponse.get(0).getReleasePackageNumber()));
    }

    public static void deletePrerequisitesIsSuccessful(String releasePackagePrerequisites, List<PrerequisiteReleasePackage> prerequisitesResponse) throws JSONException {

        JSONArray jsonArray = JsonPath.parse(releasePackagePrerequisites).read("release_package_prerequisites.results");
        LinkedHashMap prerequisitemap = new LinkedHashMap();
        prerequisitemap = (LinkedHashMap) jsonArray.get(0);
        assertThat("sequence is not same", prerequisitemap.get("sequence_number"), equalTo(prerequisitesResponse.get(0).getSequence()));
        assertThat("release package number is not same", prerequisitemap.get("release_package_number"), equalTo(prerequisitesResponse.get(0).getReleasePackageNumber()));
    }


}
