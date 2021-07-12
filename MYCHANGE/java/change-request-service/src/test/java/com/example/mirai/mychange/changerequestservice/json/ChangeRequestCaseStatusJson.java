package com.example.mirai.projectname.changerequestservice.json;

public class ChangeRequestCaseStatusJson extends Content {

    public ChangeRequestCaseStatusJson(String content) {
        super(content);
    }

    public Integer getStatus() {
        return documentContext.read("$.status");
    }
    public String getStatusLabel() {
        return documentContext.read("$.status_label");
    }
}
