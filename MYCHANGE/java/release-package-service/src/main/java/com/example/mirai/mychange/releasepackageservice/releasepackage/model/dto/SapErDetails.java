package com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto;

import java.io.Serializable;

public class SapErDetails implements Serializable {
    private String erNumber;
    private Integer status;
    private String statusLabel;

    public SapErDetails(String erNumber, Integer status) {
        this.erNumber = erNumber;
        this.status = status;
        this.statusLabel = SapErStatus.getStatusLabel(status);
    }
}
