package com.example.mirai.projectname.changerequestservice.migration.model;

import com.example.mirai.projectname.changerequestservice.comment.model.ChangeRequestComment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeRequestCommentMigrate extends ChangeRequestComment {
    private String commentOldId;

    public ChangeRequestCommentMigrate(ChangeRequestComment changeRequestComment, String commentOldId) {
        this.setId(changeRequestComment.getId());
        this.commentOldId = commentOldId;
        this.setChangeRequest(changeRequestComment.getChangeRequest());
        this.setCommentText(changeRequestComment.getCommentText());
        if (Objects.nonNull(changeRequestComment.getCreatedOn()))
            this.setCreatedOn(changeRequestComment.getCreatedOn());
        if (Objects.nonNull(changeRequestComment.getCreator()))
            this.setCreator(changeRequestComment.getCreator());
        this.setStatus(changeRequestComment.getStatus());
    }
}
