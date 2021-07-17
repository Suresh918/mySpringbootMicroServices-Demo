package com.example.mirai.projectname.reviewservice.reviewentry.controller;

import com.example.mirai.libraries.security.core.controller.SecurityController;
import com.example.mirai.projectname.reviewservice.review.model.aggregate.ReviewCaseStatusAggregate;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntry;
import com.example.mirai.projectname.reviewservice.reviewentry.service.ReviewEntryService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("reviews/{entityType:review-entries}")
public class ReviewEntrySecurityController extends SecurityController {

    public ReviewEntrySecurityController(ReviewEntryService reviewEntryService) {
        super(reviewEntryService);
    }

    @Override
    public Class<ReviewCaseStatusAggregate> getCaseStatusAggregateClass() {
        return ReviewCaseStatusAggregate.class;
    }

    @Override
    public Class<ReviewEntry> getEntityClass() {
        return ReviewEntry.class;
    }
}
