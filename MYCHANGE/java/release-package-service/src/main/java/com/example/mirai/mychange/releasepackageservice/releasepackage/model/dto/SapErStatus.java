package com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import com.example.mirai.libraries.core.model.StatusInterface;
import lombok.Getter;

@Getter
public enum SapErStatus implements StatusInterface {
    INITIAL(10,"Initial"),
    PLANT_SELECTION(20,"Plant Selection"),
    ENRICHMENT(30,"Enrichment"),
    VALIDATION(40,"Validation"),
    EFFECTUTED(50,"Effectuted"),
    CLOSED(60,"Closed");

    SapErStatus(Integer statusCode, String statusLabel) {
        this.statusCode = statusCode;
        this.statusLabel = statusLabel;
    }

    private Integer statusCode;
    private String statusLabel;

    @Override
    public Integer getStatusCode() {
        return statusCode;
    }
    public static String getStatusLabel(Integer statusCode) {
        if (Objects.isNull(statusCode))
            return "";
        Optional<SapErStatus> erStatus =  Arrays.stream(SapErStatus.values()).filter(status -> status.getStatusCode().equals(statusCode)).findFirst();
        if (erStatus.isPresent())
            return erStatus.get().getStatusLabel();
        else {
            return "";
        }
    }
}
