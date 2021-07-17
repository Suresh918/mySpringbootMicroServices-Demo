package com.example.mirai.projectname.reviewservice.review.model;

import lombok.Getter;

@Getter
public enum ReleasePackageStatus {
    NEW("NEW"), CREATED("CREATED"), READY_FOR_RELEASE("READY-FOR-RELEASE"), RELEASED("RELEASED"), CLOSED("CLOSED"), OBSOLETED("OBSOLETED");

    private String statusName;

    ReleasePackageStatus(String status) {
        this.statusName = status;
    }
}
