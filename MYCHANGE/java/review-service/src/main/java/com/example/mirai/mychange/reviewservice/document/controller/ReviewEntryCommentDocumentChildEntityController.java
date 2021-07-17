package com.example.mirai.projectname.reviewservice.document.controller;


import com.example.mirai.libraries.document.controller.DocumentChildEntityController;
import com.example.mirai.projectname.reviewservice.core.component.EntityResolver;
import com.example.mirai.projectname.reviewservice.document.service.ReviewEntryCommentDocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("{parentType:comments}/{parentId}/{entityType:documents}")
public class ReviewEntryCommentDocumentChildEntityController extends DocumentChildEntityController {

    public ReviewEntryCommentDocumentChildEntityController(ObjectMapper objectMapper,
                                                           ReviewEntryCommentDocumentService reviewEntryCommentDocumentService,
                                                           EntityResolver entityResolver) {
        super(objectMapper, reviewEntryCommentDocumentService, entityResolver);
    }
}
