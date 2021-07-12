package com.example.mirai.projectname.changerequestservice.document.controller;

import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.document.controller.DocumentSecurityController;
import com.example.mirai.projectname.changerequestservice.document.model.ChangeRequestDocument;
import com.example.mirai.projectname.changerequestservice.document.service.ChangeRequestDocumentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("{parentType:change-requests}/{entityType:documents}")
public class ChangeRequestDocumentSecurityController extends DocumentSecurityController {

    public ChangeRequestDocumentSecurityController(ChangeRequestDocumentService changeRequestDocumentService) {
        super(changeRequestDocumentService);
    }

    @Override
    public Class<AggregateInterface> getCaseStatusAggregateClass() {
        return null;
    }

    @Override
    public Class<ChangeRequestDocument> getEntityClass() {
        return ChangeRequestDocument.class;
    }
}
