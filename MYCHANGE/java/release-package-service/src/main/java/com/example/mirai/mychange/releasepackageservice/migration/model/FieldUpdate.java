package com.example.mirai.projectname.releasepackageservice.migration.model;

import java.util.Date;

import com.example.mirai.libraries.core.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldUpdate {
    private Object oldIns;
    private Object newIns;
    private Date modifiedOn;
    private User modifiedBy;
}
