package com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate;

import java.io.Serializable;
import java.util.List;

import com.example.mirai.projectname.releasepackageservice.comment.model.ReleasePackageCommentMigrate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReleasePackageDetailWithComments implements Serializable {
    private ReleasePackageDetail releasePackageDetail;
    private List<ReleasePackageCommentMigrate> comments;
}
