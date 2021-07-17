package com.example.mirai.projectname.releasepackageservice.comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReleasePackageCommentMigrate extends ReleasePackageComment {
    private String commentOldId;

    public ReleasePackageCommentMigrate(ReleasePackageComment releasePackageComment, String commentOldId) {
        this.setId(releasePackageComment.getId());
        this.commentOldId = commentOldId;
        this.setReleasePackage(releasePackageComment.getReleasePackage());
        this.setCommentText(releasePackageComment.getCommentText());
        this.setCreatedOn(releasePackageComment.getCreatedOn());
        this.setCreator(releasePackageComment.getCreator());
        this.setStatus(releasePackageComment.getStatus());
    }
}
