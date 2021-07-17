package com.example.mirai.projectname.releasepackageservice.document.controller;

import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.document.controller.DocumentSecurityController;
import com.example.mirai.projectname.releasepackageservice.document.model.ReleasePackageCommentDocument;
import com.example.mirai.projectname.releasepackageservice.document.service.ReleasePackageCommentDocumentService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("{parentType:comments}/{entityType:documents}")
public class ReleasePackageCommentDocumentSecurityController extends DocumentSecurityController {

    public ReleasePackageCommentDocumentSecurityController(ReleasePackageCommentDocumentService releasePackageCommentDocumentService) {
        super(releasePackageCommentDocumentService);
    }

    @Override
    public Class<AggregateInterface> getCaseStatusAggregateClass() {
        return null;
    }

    @Override
    public Class<ReleasePackageCommentDocument> getEntityClass() {
        return ReleasePackageCommentDocument.class;
    }
}
