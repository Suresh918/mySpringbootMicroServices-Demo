package com.example.mirai.projectname.changerequestservice.migration.model;

import com.example.mirai.libraries.core.model.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Getter
@Setter
public class ChangeRequestMigrate {
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;
    private User createdBy;
    private String title;
}
