package com.example.mirai.projectname.changerequestservice.json;


public class ChangeRequestMyTeamJson extends Content {
    public ChangeRequestMyTeamJson(String content) {
        super(content);
    }

    public Long getId() {
        return Long.valueOf("" + documentContext.read("id"));
    }
}
