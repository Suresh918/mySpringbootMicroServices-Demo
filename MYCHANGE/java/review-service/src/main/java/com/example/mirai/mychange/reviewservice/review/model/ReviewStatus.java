package com.example.mirai.projectname.reviewservice.review.model;

import com.example.mirai.libraries.core.model.StatusInterface;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ReviewStatus implements StatusInterface {
    OPENED(1, "Open"), LOCKED(2, "Defects Locked"),
    VALIDATIONSTARTED(3, "Validate"), COMPLETED(4, "Completed");

    private Integer statusCode;
    private String statusLabel;

    ReviewStatus(Integer statusCode, String statusLabel) {
        this.statusCode = statusCode;
        this.statusLabel = statusLabel;
    }

    public static String getLabelByCode(Integer statusCode) {
        return Arrays.stream(ReviewStatus.values()).filter(status -> status.getStatusCode().equals(statusCode)).findFirst().get().getStatusLabel();
    }
}
