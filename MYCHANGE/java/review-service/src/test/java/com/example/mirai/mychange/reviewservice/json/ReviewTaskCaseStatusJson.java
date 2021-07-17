package com.example.mirai.projectname.reviewservice.json;

public class ReviewTaskCaseStatusJson extends Content {
    public ReviewTaskCaseStatusJson(String content) {
        super(content);
    }

    public Long getId() {
        return Long.valueOf("" + documentContext.read("id"));
    }

    public Integer getStatus() {
        return Integer.valueOf("" + documentContext.read("status"));
    }
}
