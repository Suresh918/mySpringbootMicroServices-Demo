package com.example.mirai.projectname.changerequestservice.document.controller;

import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.document.controller.DocumentSecurityController;
import com.example.mirai.projectname.changerequestservice.document.model.ChangeRequestCommentDocument;
import com.example.mirai.projectname.changerequestservice.document.service.ChangeRequestCommentDocumentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("{parentType:comments}/{entityType:documents}")
public class ChangeRequestCommentDocumentSecurityController extends DocumentSecurityController {

    public ChangeRequestCommentDocumentSecurityController(ChangeRequestCommentDocumentService changeRequestCommentDocumentService) {
        super(changeRequestCommentDocumentService);
    }

    @Override
    public Class<AggregateInterface> getCaseStatusAggregateClass() {
        return null;
    }

    @Override
    public Class<ChangeRequestCommentDocument> getEntityClass() {
        return ChangeRequestCommentDocument.class;
    }
}
