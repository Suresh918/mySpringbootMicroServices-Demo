package com.example.mirai.projectname.reviewservice.json;

import java.util.List;

public class ReviewTaskSummaryJson<T> extends Content {
    public ReviewTaskSummaryJson(String content) {
        super(content);
    }

    /*public Long getTotalElements() {
        return Long.valueOf("" + documentContext.read("total_elements"));
    }
    public Long getTotalPages() {
        return Long.valueOf("" + documentContext.read("total_pages"));
    }
    public Boolean getHasNext() { return Boolean.valueOf("" + documentContext.read("has_next")); }*/
    public List<T> getReviewTaskSummaries() {
        return documentContext.read("review_task_summaries");
    }
}
