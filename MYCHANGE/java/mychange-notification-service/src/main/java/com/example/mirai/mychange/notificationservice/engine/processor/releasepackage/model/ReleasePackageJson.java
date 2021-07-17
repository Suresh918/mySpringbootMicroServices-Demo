package com.example.mirai.projectname.notificationservice.engine.processor.releasepackage.model;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.notificationservice.engine.processor.review.Statuses;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;

public class ReleasePackageJson {

    String releasePackageJson;

    public ReleasePackageJson(String releasePackageJson) {
        this.releasePackageJson = releasePackageJson;
    }

    public Integer getReleasePackageId() {
        return (JsonPath.parse(releasePackageJson).read("$.id"));
    }

    public User getReleasePackageExecutor() {
        return (getObjectMapper().convertValue(JsonPath.parse(releasePackageJson).read("$.executor"), User.class));
    }

    public User getReleasePackageMyTeamMember() {
        return (getObjectMapper().convertValue(JsonPath.parse(releasePackageJson).read("$.executor"), User.class));
    }

    public User getReleasePackageChangeSpecialist3() {
        return (getObjectMapper().convertValue(JsonPath.parse(releasePackageJson).read("$.change_specialist3"), User.class));
    }

    public String getReleasePackageNumber() {
        return (JsonPath.parse(releasePackageJson).read("$.release_package_number"));
    }

    public Integer getReleasePackageStatus() {
        return (JsonPath.parse(releasePackageJson).read("$.status"));
    }

    public String getReleasePackageECN() {
        return (String) ((JSONArray) (JsonPath.parse(releasePackageJson).read("$.contexts[?(@.type=='ECN')].context_id"))).get(0);
    }

    public String getTeamCenterId() {
        return (JsonPath.parse(releasePackageJson).read("$.contexts[?(@.type=='TEAMCENTER')].context_id")).toString();
    }

    public String getReleasePackageTitle() {
        return (JsonPath.parse(releasePackageJson).read("$.title"));
    }


    public String getReleasePackageCreatorPhotoUrl() {
        return (JsonPath.parse(releasePackageJson).read(("$.creator.user_id")));
    }

    public String getReleasePackageCreator() {
        return (JsonPath.parse(releasePackageJson).read(("$.creator.user_id")));

    }

    public String getReleasePackageCreatedOn() {
        return (JsonPath.parse(releasePackageJson).read(("$.created_on")));
    }

    public String getReviewStatus() {
        String status = (String) ((JSONArray) (JsonPath.parse(releasePackageJson).read("$.contexts[?(@.type=='REVIEW')].status"))).get(0);
        return Statuses.ReviewStatus.getLabelByCode(Integer.parseInt(status));
    }

    public String getReviewTitle() {
        return (String) ((JSONArray) (JsonPath.parse(releasePackageJson).read("$.contexts[?(@.type=='REVIEW')].name"))).get(0);
    }

    public String getReviewId() {
        return (String) ((JSONArray) (JsonPath.parse(releasePackageJson).read("$.contexts[?(@.type=='REVIEW')].context_id"))).get(0);
    }

    public User getReviewExecutor() {
        return (getObjectMapper().convertValue(JsonPath.parse(releasePackageJson).read("$.executor"), User.class));

    }

    public String getReviewExecutorPhotoUrl() {
        return (JsonPath.parse(releasePackageJson).read(("$.executor.user_id")));
    }

    public String getChangeNoticeId() {
        return (String) ((JSONArray) (JsonPath.parse(releasePackageJson).read("$.contexts[?(@.type=='CHANGENOTICE')].context_id"))).get(0);
    }

    ObjectMapper getObjectMapper() { return ApplicationContextHolder.getApplicationContext().getBean(ObjectMapper.class);}

}
