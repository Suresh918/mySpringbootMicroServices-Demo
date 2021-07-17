package com.example.mirai.projectname.releasepackageservice.json;


public class ReleasePackageMyTeamJson extends Content {
    public ReleasePackageMyTeamJson(String content) {
        super(content);
    }

    public Long getId() {
        return Long.valueOf("" + documentContext.read("id"));
    }
}
