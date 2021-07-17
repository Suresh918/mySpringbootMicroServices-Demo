package com.example.mirai.projectname.reviewservice.comment.controller;

import com.example.mirai.libraries.comment.controller.CommentChildEntityController;
import com.example.mirai.libraries.entity.controller.ChildEntityController;
import com.example.mirai.projectname.reviewservice.comment.service.ReviewEntryCommentService;
import com.example.mirai.projectname.reviewservice.core.component.EntityResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"{parentType:review-entries}/{parentId}/{entityType:comments}",
        "{parentType:comments}/{parentId}/{entityType:comments}"})
public class ReviewEntryCommentChildEntityController extends CommentChildEntityController {

    public ReviewEntryCommentChildEntityController(ObjectMapper objectMapper, ReviewEntryCommentService reviewEntryCommentService,
                                                   EntityResolver entityResolver) {
        super(objectMapper, reviewEntryCommentService, entityResolver);
    }
}
