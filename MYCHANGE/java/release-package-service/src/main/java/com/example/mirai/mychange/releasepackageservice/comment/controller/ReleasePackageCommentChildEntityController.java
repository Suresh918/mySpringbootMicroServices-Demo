package com.example.mirai.projectname.releasepackageservice.comment.controller;

import com.example.mirai.libraries.comment.controller.CommentChildEntityController;
import com.example.mirai.projectname.releasepackageservice.comment.service.ReleasePackageCommentService;
import com.example.mirai.projectname.releasepackageservice.core.component.EntityResolver;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"{parentType:release-packages}/{parentId}/{entityType:comments}", "{parentType:comments}/{parentId}/{entityType:comments}"})
public class ReleasePackageCommentChildEntityController extends CommentChildEntityController {

    public ReleasePackageCommentChildEntityController(ObjectMapper objectMapper, ReleasePackageCommentService releasePackageCommentService,
                                                      EntityResolver entityResolver) {
        super(objectMapper, releasePackageCommentService, entityResolver);
    }
}
