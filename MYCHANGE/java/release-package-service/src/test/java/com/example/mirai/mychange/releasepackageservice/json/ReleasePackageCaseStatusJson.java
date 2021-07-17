package com.example.mirai.projectname.releasepackageservice.json;

public class ReleasePackageCaseStatusJson extends Content {


    public ReleasePackageCaseStatusJson(String content) {
        super(content);
    }

    public Long getId() {
        return Long.valueOf("" + documentContext.read("id"));
    }

    public Integer getStatus() {
        return Integer.valueOf("" + documentContext.read("$.release_package_case_status.status"));
    }

}
