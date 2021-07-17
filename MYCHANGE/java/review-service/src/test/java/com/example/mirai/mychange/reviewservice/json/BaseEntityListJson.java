package com.example.mirai.projectname.reviewservice.json;

import java.util.List;

public class BaseEntityListJson<T> extends Content {

    public BaseEntityListJson(String content) {
        super(content);
    }

    public Long getTotalElements() {
        return Long.valueOf("" + documentContext.read("total_elements"));
    }

    public Long getTotalPages() {
        return Long.valueOf("" + documentContext.read("total_pages"));
    }

    public Boolean getHasNext() {
        return Boolean.valueOf("" + documentContext.read("has_next"));
    }

    public List<T> getResults() {
        return documentContext.read("results");
    }
}
