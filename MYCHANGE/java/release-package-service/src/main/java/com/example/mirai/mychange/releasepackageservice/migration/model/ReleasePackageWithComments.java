package com.example.mirai.projectname.releasepackageservice.migration.model;

import java.util.Date;
import java.util.List;

import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate.ReleasePackageAggregate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReleasePackageWithComments {
    private ReleasePackageAggregate releasePackageAggregate;
    private List<ReleasePackageCommentMigrate> comments;
    private Date modifiedOn;
    private User modifiedBy;
}
