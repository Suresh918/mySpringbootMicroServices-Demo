package com.example.mirai.projectname.releasepackageservice.document.controller;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.document.controller.DocumentEntityController;
import com.example.mirai.projectname.releasepackageservice.core.component.EntityResolver;
import com.example.mirai.projectname.releasepackageservice.document.model.ReleasePackageDocument;
import com.example.mirai.projectname.releasepackageservice.document.service.ReleasePackageDocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"{parentType:release-packages}/{entityType:documents}", "{parentType:release-packages}/{parentId}/{entityType:documents}"})
public class ReleasePackageDocumentEntityController extends DocumentEntityController {

    public ReleasePackageDocumentEntityController(ObjectMapper objectMapper, ReleasePackageDocumentService releasePackageDocumentService,
                                                  EntityResolver entityResolver) {
        super(objectMapper, releasePackageDocumentService, entityResolver);
    }

    @Override
    public Class<? extends BaseEntityInterface> getEntityClass() {
        return ReleasePackageDocument.class;
    }
}
