package com.example.mirai.projectname.reviewservice.review.model.dto.ecn;


import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Getter
public enum SolutionItemChangeType {
    ADD("I", "New Part"), UPDATE("U", "Update"),
    DELETE("D", "Delete"), NOCHANGE("N", "No Change");

    private String statusCode;
    private String statusLabel;

    SolutionItemChangeType(String statusCode, String statusLabel) {
        this.statusCode = statusCode;
        this.statusLabel = statusLabel;
    }

    public static String getLabelByCode(String statusCode) {
        if(Objects.isNull(statusCode) || statusCode.equals("")){
            return "-";
        }
        Optional<SolutionItemChangeType> solutionItemChangeType = Arrays.stream(SolutionItemChangeType.values()).filter(status -> status.getStatusCode().equals(statusCode)).findFirst();
        if(solutionItemChangeType.isPresent()){
            return solutionItemChangeType.get().getStatusLabel();
        }
        return statusCode;
    }
}
