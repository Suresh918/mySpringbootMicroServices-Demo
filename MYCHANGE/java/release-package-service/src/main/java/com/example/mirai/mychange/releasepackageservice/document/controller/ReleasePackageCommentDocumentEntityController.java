package com.example.mirai.projectname.releasepackageservice.document.controller;


import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.document.controller.DocumentEntityController;
import com.example.mirai.projectname.releasepackageservice.core.component.EntityResolver;
import com.example.mirai.projectname.releasepackageservice.document.model.ReleasePackageCommentDocument;
import com.example.mirai.projectname.releasepackageservice.document.service.ReleasePackageCommentDocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"{parentType:comments}/{entityType:documents}", "{parentType:comments}/{parentId}/{entityType:documents}"})
public class ReleasePackageCommentDocumentEntityController extends DocumentEntityController {

    public ReleasePackageCommentDocumentEntityController(ObjectMapper objectMapper,
                                                         ReleasePackageCommentDocumentService releasePackageCommentDocumentService,
                                                         EntityResolver entityResolver) {
        super(objectMapper, releasePackageCommentDocumentService, entityResolver);
    }

    @Override
    public Class<? extends BaseEntityInterface> getEntityClass() {
        return ReleasePackageCommentDocument.class;
    }

}
