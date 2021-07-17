package com.example.mirai.projectname.releasepackageservice.shared.exception;

import java.util.List;

import lombok.Getter;

@Getter
public class SapMdgAdditionalMaterialException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String message;
    private List<String> items;
    public SapMdgAdditionalMaterialException(List<String> items){
        this.items = items;
        String message = ReleasePackageErrorStatusCodes.ADDITIONAL_MATERIAL_IN_SAP_MDG.getMessage();
        this.message = message.replace("<ITEMS>" , String.join(" <br> ", items));
    }
}
