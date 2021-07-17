package com.example.mirai.projectname.releasepackageservice.comment.controller;

import com.example.mirai.libraries.comment.controller.CommentSecurityController;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.projectname.releasepackageservice.comment.model.ReleasePackageComment;
import com.example.mirai.projectname.releasepackageservice.comment.service.ReleasePackageCommentService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("release-packages/comments")
public class ReleasePackageCommentSecurityController extends CommentSecurityController {

    public ReleasePackageCommentSecurityController(ReleasePackageCommentService releasePackageCommentService) {
        super(releasePackageCommentService);
    }

    @Override
    public Class<AggregateInterface> getCaseStatusAggregateClass() {
        return null;
    }

    @Override
    public Class<ReleasePackageComment> getEntityClass() {
        return ReleasePackageComment.class;
    }
}
