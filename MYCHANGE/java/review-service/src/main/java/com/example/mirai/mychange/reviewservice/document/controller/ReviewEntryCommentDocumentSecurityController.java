package com.example.mirai.projectname.reviewservice.document.controller;

import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.document.controller.DocumentSecurityController;
import com.example.mirai.projectname.reviewservice.document.model.ReviewEntryCommentDocument;
import com.example.mirai.projectname.reviewservice.document.service.ReviewEntryCommentDocumentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("{parentType:comments}/{entityType:documents}")
public class ReviewEntryCommentDocumentSecurityController extends DocumentSecurityController {

    public ReviewEntryCommentDocumentSecurityController(ReviewEntryCommentDocumentService reviewEntryCommentDocumentService) {
        super(reviewEntryCommentDocumentService);
    }

    @Override
    public Class<AggregateInterface> getCaseStatusAggregateClass() {
        return null;
    }

    @Override
    public Class<ReviewEntryCommentDocument> getEntityClass() {
        return ReviewEntryCommentDocument.class;
    }
}
