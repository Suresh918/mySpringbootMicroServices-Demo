package com.example.mirai.projectname.changerequestservice.document.controller;


import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.document.controller.DocumentEntityController;
import com.example.mirai.projectname.changerequestservice.document.model.ChangeRequestCommentDocument;
import com.example.mirai.projectname.changerequestservice.document.service.ChangeRequestCommentDocumentService;
import com.example.mirai.projectname.changerequestservice.core.component.EntityResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"{parentType:comments}/{entityType:documents}", "{parentType:comments}/{parentId}/{entityType:documents}"})
public class ChangeRequestCommentDocumentEntityController extends DocumentEntityController {

    public ChangeRequestCommentDocumentEntityController(ObjectMapper objectMapper,
                                                        ChangeRequestCommentDocumentService changeRequestCommentDocumentService,
                                                        EntityResolver entityResolver) {
        super(objectMapper, changeRequestCommentDocumentService, entityResolver);
    }

    @Override
    public Class<? extends BaseEntityInterface> getEntityClass() {
        return ChangeRequestCommentDocument.class;
    }

}
