package com.example.mirai.projectname.reviewservice.json;

import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.reviewservice.review.model.ReviewContext;
import net.minidev.json.JSONArray;

import java.util.Date;

public class ReviewJson extends Content {

    public ReviewJson(String content) {
        super(content);
    }

    public Long getId() {
        return Long.valueOf("" + documentContext.read("id"));
    }

    public String getTitle() {
        return documentContext.read("title");
    }

    public Integer getStatus() {
        return documentContext.read("status");
    }

    public Date getCompletionDate() {
        return convertStringToDate(documentContext.read("completion_date"));
    }

    public Date getCreatedOn() {
        return convertStringToDate(documentContext.read("created_on"));
    }

    public User getExecutor() {
        User executor = new User();
        executor.setUserId(getExecutorUserId());
        executor.setFullName(getExecutorFullName());
        executor.setEmail(getExecutorEmail());
        executor.setDepartmentName(getExecutorDepartmentName());
        executor.setAbbreviation(getExecutorAbbreviation());
        return executor;
    }

    public String getExecutorUserId() {
        return documentContext.read("$.executor.user_id");
    }

    public String getExecutorFullName() {
        return documentContext.read("$.executor.full_name");
    }

    public String getExecutorEmail() {
        return documentContext.read("$.executor.email");
    }

    public String getExecutorDepartmentName() {
        return documentContext.read("$.executor.department_name");
    }

    public String getExecutorAbbreviation() {
        return documentContext.read("$.executor.abbreviation");
    }

    public ReviewContext getReleasePackageReviewContext() {
        ReviewContext reviewContext = new ReviewContext();
        reviewContext.setType("RELEASEPACKAGE");
        reviewContext.setContextId(getReleasePackageContextId());
        reviewContext.setName(getReleasePackageName());
        reviewContext.setStatus(getReleasePackageStatus());
        return reviewContext;
    }

    public ReviewContext getECNReviewContext() {
        ReviewContext reviewContext = new ReviewContext();
        reviewContext.setType("ECN");
        reviewContext.setContextId(getECNContextId());
        reviewContext.setName(getECNName());
        reviewContext.setStatus(getECNStatus());
        return reviewContext;
    }

    public ReviewContext getTeamcenterReviewContext() {
        ReviewContext reviewContext = new ReviewContext();
        reviewContext.setType("TEAMCENTER");
        reviewContext.setContextId(getTeamcenterContextId());
        reviewContext.setName(getTeamcenterName());
        reviewContext.setStatus(getTeamcenterStatus());
        return reviewContext;
    }

    public String getReleasePackageContextId() {
        JSONArray jsonArray = documentContext.read("$.contexts[?(@.type=='RELEASEPACKAGE')].context_id");
        return jsonArray.get(0).toString();
    }

    public String getReleasePackageName() {
        JSONArray jsonArray = documentContext.read("$.contexts[?(@.type=='RELEASEPACKAGE')].name");
        return jsonArray.get(0).toString();
    }

    public String getReleasePackageStatus() {
        JSONArray jsonArray = documentContext.read("$.contexts[?(@.type=='RELEASEPACKAGE')].status");
        return jsonArray.get(0).toString();
    }

    public String getECNContextId() {
        JSONArray jsonArray = documentContext.read("$.contexts[?(@.type=='ECN')].context_id");
        return jsonArray.get(0).toString();
    }

    public String getECNName() {
        JSONArray jsonArray = documentContext.read("$.contexts[?(@.type=='ECN')].name");
        return jsonArray.get(0).toString();
    }

    public String getECNStatus() {
        JSONArray jsonArray = documentContext.read("$.contexts[?(@.type=='ECN')].status");
        return jsonArray.get(0).toString();
    }

    public String getTeamcenterContextId() {
        JSONArray jsonArray = documentContext.read("$.contexts[?(@.type=='TEAMCENTER')].context_id");
        return jsonArray.get(0).toString();
    }

    public String getTeamcenterName() {
        JSONArray jsonArray = documentContext.read("$.contexts[?(@.type=='TEAMCENTER')].name");
        return jsonArray.get(0).toString();
    }

    public String getTeamcenterStatus() {
        JSONArray jsonArray = documentContext.read("$.contexts[?(@.type=='TEAMCENTER')].status");
        return jsonArray.get(0).toString();
    }

}
