package com.example.mirai.projectname.reviewservice.reviewentry.model;


import com.example.mirai.libraries.core.model.StatusInterface;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ReviewEntryStatus implements StatusInterface {
    OPENED(1, "Open"), ACCEPTED(2, "Accepted"),
    MARKEDDUPLICATE(3, "Duplicate"),
    REJECTED(4, "Rejected"), COMPLETED(5, "Done");

    private Integer statusCode;
    private String statusLabel;

    ReviewEntryStatus(Integer statusCode, String statusLabel) {
        this.statusCode = statusCode;
        this.statusLabel = statusLabel;
    }

    public static String getLabelByCode(Integer statusCode) {
        return Arrays.stream(ReviewEntryStatus.values()).filter(status -> status.getStatusCode().equals(statusCode)).findFirst().get().getStatusLabel();
    }
}
