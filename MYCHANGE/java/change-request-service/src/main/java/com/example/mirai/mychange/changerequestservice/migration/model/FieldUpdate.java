package com.example.mirai.projectname.changerequestservice.migration.model;

import com.example.mirai.libraries.core.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class FieldUpdate {
    private Object oldIns;
    private Object newIns;
    private Date modifiedOn;
    private User modifiedBy;
}
