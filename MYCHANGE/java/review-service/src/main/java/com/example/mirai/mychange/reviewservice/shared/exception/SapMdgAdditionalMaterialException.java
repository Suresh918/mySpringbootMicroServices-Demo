package com.example.mirai.projectname.reviewservice.shared.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class SapMdgAdditionalMaterialException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String message;
    private List<String> items;
    public SapMdgAdditionalMaterialException(List<String> items){
        this.items = items;
        String message = ReviewErrorStatusCodes.ADDITIONAL_MATERIAL_IN_SAP_MDG.getMessage();
        this.message = message.replace("<ITEMS>" , String.join(" <br> ", items));
    }
}
