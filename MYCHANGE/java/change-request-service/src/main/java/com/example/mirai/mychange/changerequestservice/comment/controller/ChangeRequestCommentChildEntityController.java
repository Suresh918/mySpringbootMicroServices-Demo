package com.example.mirai.projectname.changerequestservice.comment.controller;

import com.example.mirai.libraries.comment.controller.CommentChildEntityController;
import com.example.mirai.projectname.changerequestservice.comment.service.ChangeRequestCommentService;
import com.example.mirai.projectname.changerequestservice.core.component.EntityResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"{parentType:change-requests}/{parentId}/{entityType:comments}", "{parentType:comments}/{parentId}/{entityType:comments}"})
public class ChangeRequestCommentChildEntityController extends CommentChildEntityController {

    public ChangeRequestCommentChildEntityController(ObjectMapper objectMapper, ChangeRequestCommentService ChangeRequestCommentService,
                                                     EntityResolver entityResolver) {
        super(objectMapper, ChangeRequestCommentService, entityResolver);
    }
}
