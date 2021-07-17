package com.example.mirai.projectname.reviewservice.comment.controller;

import com.example.mirai.libraries.comment.controller.CommentSecurityController;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.security.core.controller.SecurityController;
import com.example.mirai.projectname.reviewservice.comment.model.ReviewEntryComment;
import com.example.mirai.projectname.reviewservice.comment.service.ReviewEntryCommentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("{parentType:review-entries}/{entityType:comments}")
public class ReviewEntryCommentSecurityController extends CommentSecurityController {

    public ReviewEntryCommentSecurityController(ReviewEntryCommentService reviewEntryCommentService) {
        super(reviewEntryCommentService);
    }

    @Override
    public Class<AggregateInterface> getCaseStatusAggregateClass() {
        return null;
    }

    @Override
    public Class<ReviewEntryComment> getEntityClass() {
        return ReviewEntryComment.class;
    }
}
