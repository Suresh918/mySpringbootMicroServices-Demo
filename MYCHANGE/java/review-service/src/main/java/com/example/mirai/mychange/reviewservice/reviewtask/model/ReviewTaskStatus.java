package com.example.mirai.projectname.reviewservice.reviewtask.model;


import com.example.mirai.libraries.core.model.StatusInterface;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ReviewTaskStatus implements StatusInterface {

    OPENED(1, "Open"), ACCEPTED(2, "Accepted"),
    NOTFINALIZED(3, "Not Completed"), FINALIZED(4, "Completed"),
    COMPLETED(5, "Completed"), REJECTED(6, "Rejected");

    private Integer statusCode;
    private String statusLabel;

    ReviewTaskStatus(Integer statusCode, String statusLabel) {
        this.statusCode = statusCode;
        this.statusLabel = statusLabel;
    }

    public static String getLabelByCode(Integer statusCode) {
        return Arrays.stream(ReviewTaskStatus.values()).filter(status -> status.getStatusCode().equals(statusCode)).findFirst().get().getStatusLabel();
    }
}
