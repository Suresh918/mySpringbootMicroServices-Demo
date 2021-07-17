package com.example.mirai.projectname.releasepackageservice.releasepackage.model;


import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import com.example.mirai.libraries.core.model.StatusInterface;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public enum ReleasePackageStatus implements StatusInterface {
    DRAFTED(1, "New", "NEW"), CREATED(2, "Created", "CREATED"),
    READY_FOR_RELEASE(3, "Ready For Release", "READY-FOR-RELEASE"),
    RELEASED(4, "Released", "RELEASED"),
    CLOSED(5, "Closed", "CLOSED"), OBSOLETED(6, "Obsoleted", "OBSOLETED");

    private Integer statusCode;
    private String statusLabel;
    private String statusName;

    ReleasePackageStatus(Integer statusCode, String statusLabel, String statusName) {
        this.statusCode = statusCode;
        this.statusLabel = statusLabel;
        this.statusName = statusName;
    }

    public static String getLabelByCode(Integer statusCode) {
        if (Objects.isNull(statusCode))
            return "";
        Optional<ReleasePackageStatus> releasePackageStatus =  Arrays.stream(ReleasePackageStatus.values()).filter(status -> status.getStatusCode().equals(statusCode)).findFirst();
        if (releasePackageStatus.isPresent())
            return releasePackageStatus.get().getStatusLabel();
        else {
            log.info("status not present " + statusCode);
            return "";
        }
    }
    public static String getStatusNameByCode(Integer statusCode) {
        return Arrays.stream(ReleasePackageStatus.values()).filter(status -> status.getStatusCode().equals(statusCode)).findFirst().get().getStatusName();
    }
}
