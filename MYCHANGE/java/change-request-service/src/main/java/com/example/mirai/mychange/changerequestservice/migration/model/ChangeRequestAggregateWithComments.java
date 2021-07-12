package com.example.mirai.projectname.changerequestservice.migration.model;

import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestAggregate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeRequestAggregateWithComments {
    private ChangeRequestAggregate changeRequestAggregate;
    private List<ChangeRequestCommentMigrate> comments;
    private Date modifiedOn;
    private User modifiedBy;
}
