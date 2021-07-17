package com.example.mirai.projectname.releasepackageservice.releasepackage.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrerequisiteReleasePackage implements Serializable {
    private Long releasePackageId;
    private String releasePackageNumber;
    private Integer sequence;
}
