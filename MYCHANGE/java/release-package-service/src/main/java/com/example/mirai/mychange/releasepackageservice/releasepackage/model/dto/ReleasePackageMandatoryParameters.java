package com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReleasePackageMandatoryParameters implements Serializable {
    private String releasePackageNumber;
    private Long id;
    private List<String> missingDetails;
}
