package com.example.mirai.projectname.notificationservice.engine.processor.changerequest;

import com.example.mirai.libraries.core.model.StatusInterface;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ChangeRequestStatus implements StatusInterface {
    DRAFTED(1, "Draft"), NEW(2, "New"),
    SOLUTION_DEFINED(3, "Solution-Defined"), IMPACT_ANALYZED(4, "Impact-Analyzed"),
    APPROVED(5, "Approved"), CLOSED(6, "Closed"),
    REJECTED(7, "Rejected"), OBSOLETED(8, "Obsoleted");

    private Integer statusCode;
    private String statusLabel;

    ChangeRequestStatus(Integer statusCode, String statusLabel) {
        this.statusCode = statusCode;
        this.statusLabel = statusLabel;
    }
    public static String getLabelByCode(Integer statusCode) {
        return Arrays.stream(ChangeRequestStatus.values()).filter(status -> status.getStatusCode().equals(statusCode)).findFirst().get().getStatusLabel();
    }
}
