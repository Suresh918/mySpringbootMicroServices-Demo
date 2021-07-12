package com.example.mirai.projectname.changerequestservice.comment.service;

import com.example.mirai.libraries.comment.service.CommentEvaluationContext;
import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestStatus;
import com.example.mirai.projectname.changerequestservice.comment.model.ChangeRequestComment;

import java.util.Objects;

public class ChangeRequestCommentEvaluationContext extends CommentEvaluationContext<ChangeRequestComment> {

    public boolean hasReplies() {
        ChangeRequestCommentService changeRequestCommentService = (ChangeRequestCommentService) ApplicationContextHolder.getService(ChangeRequestCommentService.class);
        ChangeRequestComment comment = changeRequestCommentService.findFirstUnremovedCommentByReplyToId(context.getId());
        return comment != null;
    }
    public ChangeRequest getChangeRequest() {
        return context.getChangeRequest();
    }

    public ChangeRequest getLinkedChangeRequest() {
        ChangeRequestCommentService changeRequestCommentService = (ChangeRequestCommentService) ApplicationContextHolder.getService(ChangeRequestCommentService.class);
        if (Objects.nonNull(context.getReplyTo())) {
            ChangeRequestComment comment = (ChangeRequestComment) changeRequestCommentService.getEntityById(context.getReplyTo().getId());
            return comment.getChangeRequest();
        }
        return null;
    }

    public Integer getChangeRequestStatus() {
        ChangeRequest changeRequest = getChangeRequest();
        if (Objects.isNull(changeRequest)) {
            changeRequest = getLinkedChangeRequest();
        }
        return changeRequest.getStatus();
    }

    public Boolean isChangeRequestNotObsoleted() {
        ChangeRequest changeRequest = getChangeRequest();
        if (Objects.isNull(changeRequest)) {
            changeRequest = getLinkedChangeRequest();
        }
        return !changeRequest.getStatus().equals(ChangeRequestStatus.OBSOLETED.getStatusCode());
    }

    public Boolean isChangeRequestSecure() {
        ChangeRequest changeRequest = getChangeRequest();
        if (Objects.nonNull(changeRequest.getIsSecure()))
            return changeRequest.getIsSecure().equals(true);
        return false;
    }
}
