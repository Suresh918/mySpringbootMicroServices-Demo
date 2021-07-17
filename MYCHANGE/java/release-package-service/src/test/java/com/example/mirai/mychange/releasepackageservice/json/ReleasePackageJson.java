package com.example.mirai.projectname.releasepackageservice.json;

import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageContext;
import net.minidev.json.JSONArray;

import java.util.Date;

public class ReleasePackageJson extends Content {

    public ReleasePackageJson(String content) {
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

    public Date getPlannedReleaseDate() {
        return convertStringToDate(documentContext.read("planned_release_date"));
    }

    public Date getPlannedEffectiveDate() {
        return convertStringToDate(documentContext.read("planned_effective_date"));
    }

    public Date getCreatedOn() {
        return convertStringToDate(documentContext.read("created_on"));
    }

    public boolean getSapChangeControl() {
        return documentContext.read("sap_change_control");
    }


    public String getPrerequisitesApplicable() {
        return documentContext.read("prerequisites_applicable");
    }

    public String getPrerequisitesDetail() {
        return documentContext.read("prerequisites_detail");
    }

    public String getProductId() {
        return documentContext.read("product_id");
    }

    public String getProjectId() {
        return documentContext.read("project_id");
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

    public User getChangeSpecialist3() {
        User changeSpecialist3 = new User();
        changeSpecialist3.setUserId(getChangeSpecialist3UserId());
        changeSpecialist3.setFullName(getChangeSpecialist3FullName());
        changeSpecialist3.setEmail(getChangeSpecialist3Email());
        changeSpecialist3.setDepartmentName(getChangeSpecialist3DepartmentName());
        changeSpecialist3.setAbbreviation(getChangeSpecialist3Abbreviation());
        return changeSpecialist3;
    }

    public String getChangeSpecialist3UserId() {
        return documentContext.read("$.change_specialist_3.user_id");
    }

    public String getChangeSpecialist3FullName() {
        return documentContext.read("$.change_specialist_3.full_name");
    }

    public String getChangeSpecialist3Email() {
        return documentContext.read("$.change_specialist_3.email");
    }

    public String getChangeSpecialist3DepartmentName() {
        return documentContext.read("$.change_specialist_3.department_name");
    }

    public String getChangeSpecialist3Abbreviation() {
        return documentContext.read("$.change_specialist_3.abbreviation");
    }


//    public ReviewContext getReleasePackageReviewContext() {
//        ReviewContext reviewContext = new ReviewContext();
//        reviewContext.setType("RELEASEPACKAGE");
//        reviewContext.setContextId(getReleasePackageContextId());
//        reviewContext.setName(getReleasePackageName());
//        reviewContext.setStatus(getReleasePackageStatus());
//        return reviewContext;
//    }

    public ReleasePackageContext getECNReleasePackageContext() {
        ReleasePackageContext releasePackageContext = new ReleasePackageContext();
        releasePackageContext.setType("ECN");
        releasePackageContext.setContextId(getECNContextId());
        releasePackageContext.setName(getECNName());
        releasePackageContext.setStatus(getECNStatus());
        return releasePackageContext;
    }

    public ReleasePackageContext getTeamcenterReleasePackageContext() {
        ReleasePackageContext releasePackageContext = new ReleasePackageContext();
        releasePackageContext.setType("TEAMCENTER");
        releasePackageContext.setContextId(getTeamcenterContextId());
        releasePackageContext.setName(getTeamcenterName());
        releasePackageContext.setStatus(getTeamcenterStatus());
        return releasePackageContext;
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
