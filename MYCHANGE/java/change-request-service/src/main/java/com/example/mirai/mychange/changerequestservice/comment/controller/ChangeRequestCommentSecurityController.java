package com.example.mirai.projectname.changerequestservice.comment.controller;

import com.example.mirai.libraries.comment.controller.CommentSecurityController;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.projectname.changerequestservice.comment.model.ChangeRequestComment;
import com.example.mirai.projectname.changerequestservice.comment.service.ChangeRequestCommentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("change-requests/{entityType:comments}")
public class ChangeRequestCommentSecurityController extends CommentSecurityController {

    public ChangeRequestCommentSecurityController(ChangeRequestCommentService changeRequestCommentService) {
        super(changeRequestCommentService);
    }

    @Override
    public Class<AggregateInterface> getCaseStatusAggregateClass() {
        return null;
    }

    @Override
    public Class<ChangeRequestComment> getEntityClass() {
        return ChangeRequestComment.class;
    }
}
