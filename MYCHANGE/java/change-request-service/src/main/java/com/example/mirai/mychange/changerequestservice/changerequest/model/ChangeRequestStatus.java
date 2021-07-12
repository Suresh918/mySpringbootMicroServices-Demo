package com.example.mirai.projectname.changerequestservice.changerequest.model;

import com.example.mirai.libraries.core.model.StatusInterface;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ChangeRequestStatus implements StatusInterface {
    DRAFTED(1, "Draft", "DRAFT"), NEW(2, "New", "NEW"),
    SOLUTION_DEFINED(3, "Solution-Defined", "SOLUTION-DEFINED"), IMPACT_ANALYZED(4, "Impact-Analyzed", "IMPACT-ANALYZED"),
    APPROVED(5, "Approved", "APPROVED"), CLOSED(6, "Closed", "CLOSED"),
    REJECTED(7, "Rejected", "REJECTED"), OBSOLETED(8, "Obsoleted", "OBSOLETED");

    private Integer statusCode;
    private String statusLabel;
    private String statusName;

    ChangeRequestStatus(Integer statusCode, String statusLabel, String statusName) {
        this.statusCode = statusCode;
        this.statusLabel = statusLabel;
        this.statusName = statusName;
    }

    public static String getLabelByCode(Integer statusCode) {
        return Arrays.stream(ChangeRequestStatus.values()).filter(status -> status.getStatusCode().equals(statusCode)).findFirst().get().getStatusLabel();
    }

    public static String getNameByCode(Integer statusCode) {
        return Arrays.stream(ChangeRequestStatus.values()).filter(status -> status.getStatusCode().equals(statusCode)).findFirst().get().getStatusName();
    }
}
