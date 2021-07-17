package com.example.mirai.projectname.releasepackageservice.document.controller;

import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.document.controller.DocumentSecurityController;
import com.example.mirai.projectname.releasepackageservice.document.model.ReleasePackageDocument;
import com.example.mirai.projectname.releasepackageservice.document.service.ReleasePackageDocumentService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("{parentType:release-packages}/{entityType:documents}")
public class ReleasePackageDocumentSecurityController extends DocumentSecurityController {

    public ReleasePackageDocumentSecurityController(ReleasePackageDocumentService releasePackageDocumentService) {
        super(releasePackageDocumentService);
    }

    @Override
    public Class<AggregateInterface> getCaseStatusAggregateClass() {
        return null;
    }

    @Override
    public Class<ReleasePackageDocument> getEntityClass() {
        return ReleasePackageDocument.class;
    }
}
