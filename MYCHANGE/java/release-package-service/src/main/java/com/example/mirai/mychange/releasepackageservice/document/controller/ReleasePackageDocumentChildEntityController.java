package com.example.mirai.projectname.releasepackageservice.document.controller;

import com.example.mirai.libraries.document.controller.DocumentChildEntityController;
import com.example.mirai.projectname.releasepackageservice.core.component.EntityResolver;
import com.example.mirai.projectname.releasepackageservice.document.service.ReleasePackageDocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("{parentType:release-packages}/{parentId:[0-9]+}/{entityType:documents}")
public class ReleasePackageDocumentChildEntityController extends DocumentChildEntityController {

    public ReleasePackageDocumentChildEntityController(ObjectMapper objectMapper,
                                                       ReleasePackageDocumentService releasePackageDocumentService, EntityResolver entityResolver) {
        super(objectMapper, releasePackageDocumentService, entityResolver);
    }
}
