package com.example.mirai.projectname.releasepackageservice.migration.model;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.example.mirai.libraries.core.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReleasePackageMigrate {
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;
    private User createdBy;
    private String releasePackageNumber;
    private String title;
}
