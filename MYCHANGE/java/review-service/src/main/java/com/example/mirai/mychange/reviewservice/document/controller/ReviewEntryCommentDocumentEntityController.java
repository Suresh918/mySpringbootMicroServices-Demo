package com.example.mirai.projectname.reviewservice.document.controller;


import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.document.controller.DocumentEntityController;
import com.example.mirai.projectname.reviewservice.core.component.EntityResolver;
import com.example.mirai.projectname.reviewservice.document.model.ReviewEntryCommentDocument;
import com.example.mirai.projectname.reviewservice.document.service.ReviewEntryCommentDocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"{parentType:comments}/{entityType:documents}", "{parentType:comments}/{parentId}/{entityType:documents}"})
public class ReviewEntryCommentDocumentEntityController extends DocumentEntityController {

    public ReviewEntryCommentDocumentEntityController(ObjectMapper objectMapper,
                                                      ReviewEntryCommentDocumentService reviewEntryCommentDocumentService,
                                                      EntityResolver entityResolver) {
        super(objectMapper, reviewEntryCommentDocumentService, entityResolver);
    }

    @Override
    public Class<? extends BaseEntityInterface> getEntityClass() {
        return ReviewEntryCommentDocument.class;
    }

}
