package com.example.mirai.projectname.releasepackageservice.comment.service;

import com.example.mirai.libraries.comment.service.CommentEvaluationContext;
import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.projectname.releasepackageservice.comment.model.ReleasePackageComment;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageStatus;

public class ReleasePackageCommentEvaluationContext extends CommentEvaluationContext<ReleasePackageComment> {

    public boolean hasReplies() {
        ReleasePackageCommentService releasePackageCommentService = (ReleasePackageCommentService) ApplicationContextHolder.getService(ReleasePackageCommentService.class);
        ReleasePackageComment comment = releasePackageCommentService.findFirstUnremovedCommentByReplyToId(context.getId());
        return comment != null;
    }

    public ReleasePackage getReleasePackage() {
        return context.getReleasePackage()==null?((ReleasePackageComment)context.getReplyTo()).getReleasePackage():context.getReleasePackage();
    }

    public ReleasePackage getLinkedReleasePackage() {
        ReleasePackageCommentService releasePackageCommentService = (ReleasePackageCommentService) ApplicationContextHolder.getService(ReleasePackageCommentService.class);
        if (context.getReplyTo() != null) {
            ReleasePackageComment comment = (ReleasePackageComment) releasePackageCommentService.getEntityById(context.getReplyTo().getId());
            return comment.getReleasePackage();
        }
        return null;
    }

    public Integer getReleasePackageStatus() {
        ReleasePackage releasePackage = getReleasePackage();
        if (releasePackage == null) {
            releasePackage = getLinkedReleasePackage();
        }
        return releasePackage.getStatus();
    }

    public Boolean isReleasePackageNotObsoleted() {
        ReleasePackage releasePackage = getReleasePackage();
        return releasePackage.getStatus() != ReleasePackageStatus.OBSOLETED.getStatusCode();
    }

    public Boolean isReleasePackageSecure() {
        ReleasePackage releasePackage = getReleasePackage();
        return releasePackage.getIsSecure() == true;
    }

    public Boolean isReleasePackageNotClosed() {
        ReleasePackage releasePackage = getReleasePackage();
        return releasePackage.getStatus() != ReleasePackageStatus.CLOSED.getStatusCode();
    }
}
