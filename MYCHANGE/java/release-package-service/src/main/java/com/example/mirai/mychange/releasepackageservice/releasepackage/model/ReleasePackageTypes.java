package com.example.mirai.projectname.releasepackageservice.releasepackage.model;

import lombok.Getter;

@Getter
public enum ReleasePackageTypes {
    HW("HW"),PR("OP-PR"),WI("WI");

    String type;
    ReleasePackageTypes(String type) {
        this.type = type;
    }
}
