package com.example.mirai.projectname.releasepackageservice.document.controller;


import com.example.mirai.libraries.document.controller.DocumentChildEntityController;
import com.example.mirai.projectname.releasepackageservice.core.component.EntityResolver;
import com.example.mirai.projectname.releasepackageservice.document.service.ReleasePackageCommentDocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("{parentType:comments}/{parentId}/{entityType:documents}")
public class ReleasePackageCommentDocumentChildEntityController extends DocumentChildEntityController {

    public ReleasePackageCommentDocumentChildEntityController(ObjectMapper objectMapper,
                                                              ReleasePackageCommentDocumentService releasePackageCommentDocumentService,
                                                              EntityResolver entityResolver) {
        super(objectMapper, releasePackageCommentDocumentService, entityResolver);
    }
}
