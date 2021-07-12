package com.example.mirai.projectname.changerequestservice.json;

public class ImpactAnalysisJson extends Content {
    public ImpactAnalysisJson(String content) {
        super(content);
    }

    public Long getId() {
        return Long.valueOf("" + documentContext.read("id"));
    }
}
