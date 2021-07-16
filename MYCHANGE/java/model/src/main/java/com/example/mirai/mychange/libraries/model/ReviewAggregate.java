package com.example.mirai.projectname.libraries.model;


public class ReviewAggregate extends MyChangeEvent {
    String reviewJson;

    public ReviewAggregate(String reviewJson) {
        super(reviewJson, "review", "RELEASEPACKAGE");
        this.reviewJson = reviewJson;
    }

    @Override
    public String getType() {
        return "REVIEW";
    }

}
