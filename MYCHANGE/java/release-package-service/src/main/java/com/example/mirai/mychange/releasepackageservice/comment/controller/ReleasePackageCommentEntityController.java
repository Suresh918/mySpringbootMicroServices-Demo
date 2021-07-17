package com.example.mirai.projectname.releasepackageservice.comment.controller;

import com.example.mirai.libraries.comment.controller.CommentEntityController;
import com.example.mirai.projectname.releasepackageservice.comment.model.ReleasePackageComment;
import com.example.mirai.projectname.releasepackageservice.comment.service.ReleasePackageCommentService;
import com.example.mirai.projectname.releasepackageservice.core.component.EntityResolver;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("release-packages/comments")
public class ReleasePackageCommentEntityController extends CommentEntityController {

    public ReleasePackageCommentEntityController(ObjectMapper objectMapper,
                                                 ReleasePackageCommentService releasePackageCommentService,
                                                 EntityResolver entityResolver) {
        super(objectMapper, releasePackageCommentService, entityResolver);
    }

    @Override
    public Class<ReleasePackageComment> getEntityClass() {
        return ReleasePackageComment.class;
    }

}
