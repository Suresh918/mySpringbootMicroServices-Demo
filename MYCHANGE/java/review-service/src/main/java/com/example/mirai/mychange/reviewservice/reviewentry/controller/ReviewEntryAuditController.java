package com.example.mirai.projectname.reviewservice.reviewentry.controller;

import com.example.mirai.libraries.audit.controller.AuditController;
import com.example.mirai.projectname.reviewservice.review.model.aggregate.ReviewChangeLogAggregate;
import com.example.mirai.projectname.reviewservice.reviewentry.service.ReviewEntryService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("reviews/{entityType:review-entries}")
public class ReviewEntryAuditController extends AuditController {

    public ReviewEntryAuditController(ReviewEntryService reviewEntryService) {
        super(reviewEntryService);
    }

    @Override
    public Class<ReviewChangeLogAggregate> getChangeLogAggregateClass() {
        return ReviewChangeLogAggregate.class;
    }
}
