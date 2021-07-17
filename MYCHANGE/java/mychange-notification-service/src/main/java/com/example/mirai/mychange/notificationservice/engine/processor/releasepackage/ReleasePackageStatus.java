package com.example.mirai.projectname.notificationservice.engine.processor.releasepackage;

import com.example.mirai.libraries.core.model.StatusInterface;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ReleasePackageStatus implements StatusInterface {
    SUBMITTED(1, "New","NEW"), CREATED(2, "Created","CREATED"),
    READY_FOR_RELEASE(3, "Ready For Release","READY-FOR-RELEASE"),
    RELEASED(4, "Released","RELEASED"),
    CLOSED(5, "Closed","CLOSED"), OBSOLETED(6, "Obsoleted","OBSOLETED");

    private Integer statusCode;
    private String statusLabel;
    private String teamcenterStatusLabel;

    ReleasePackageStatus(Integer statusCode, String statusLabel, String teamcenterStatusLabel) {
        this.statusCode = statusCode;
        this.statusLabel = statusLabel;
        this.teamcenterStatusLabel = teamcenterStatusLabel;
    }

    public static String getLabelByCode(Integer statusCode) {
        return Arrays.stream(ReleasePackageStatus.values()).filter(status -> status.getStatusCode().equals(statusCode)).findFirst().get().getStatusLabel();
    }

    public static String getTeamcenterLabelByCode(Integer statusCode) {
        return Arrays.stream(ReleasePackageStatus.values()).filter(status -> status.getStatusCode().equals(statusCode)).findFirst().get().getTeamcenterStatusLabel();
    }
}
