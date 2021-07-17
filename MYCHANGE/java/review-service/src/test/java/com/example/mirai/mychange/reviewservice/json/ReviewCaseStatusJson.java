package com.example.mirai.projectname.reviewservice.json;

public class ReviewCaseStatusJson extends Content {


    public ReviewCaseStatusJson(String content) {
        super(content);
    }

    public Long getId() {
        return Long.valueOf("" + documentContext.read("id"));
    }

    public Integer getStatus() {
        return Integer.valueOf("" + documentContext.read("$.review_case_status.status"));
    }

}
