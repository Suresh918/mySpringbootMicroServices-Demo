package com.example.mirai.projectname.reviewservice.review.controller;

import com.example.mirai.libraries.audit.controller.AuditController;
import com.example.mirai.projectname.reviewservice.review.model.aggregate.ReviewChangeLogAggregate;
import com.example.mirai.projectname.reviewservice.review.service.ReviewService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("reviews")
public class ReviewAuditController extends AuditController {

    public ReviewAuditController(ReviewService reviewService) {
        super(reviewService);
    }

    @Override
    public Class<ReviewChangeLogAggregate> getChangeLogAggregateClass() {
        return ReviewChangeLogAggregate.class;
    }
}
