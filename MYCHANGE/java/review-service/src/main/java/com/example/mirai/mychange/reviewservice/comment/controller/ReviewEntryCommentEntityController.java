package com.example.mirai.projectname.reviewservice.comment.controller;

import com.example.mirai.libraries.comment.controller.CommentEntityController;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.projectname.reviewservice.comment.model.ReviewEntryComment;
import com.example.mirai.projectname.reviewservice.comment.service.ReviewEntryCommentService;
import com.example.mirai.projectname.reviewservice.core.component.EntityResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("{parentType:review-entries}/{entityType:comments}")
public class ReviewEntryCommentEntityController extends CommentEntityController {

    public ReviewEntryCommentEntityController(ObjectMapper objectMapper, ReviewEntryCommentService reviewEntryCommentService,
                                              EntityResolver entityResolver) {
        super(objectMapper, reviewEntryCommentService, entityResolver);
    }

    ReviewEntryCommentService getService() {
        return ((ReviewEntryCommentService) (super.entityServiceDefaultInterface));
    }

    @Override
    public Class<? extends BaseEntityInterface> getEntityClass() {
        return ReviewEntryComment.class;
    }
}
