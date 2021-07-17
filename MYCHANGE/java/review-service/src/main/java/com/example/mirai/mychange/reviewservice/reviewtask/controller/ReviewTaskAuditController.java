package com.example.mirai.projectname.reviewservice.reviewtask.controller;

import com.example.mirai.libraries.audit.controller.AuditController;
import com.example.mirai.projectname.reviewservice.review.model.aggregate.ReviewChangeLogAggregate;
import com.example.mirai.projectname.reviewservice.reviewtask.service.ReviewTaskService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("reviews/{entityType:review-tasks}")
public class ReviewTaskAuditController extends AuditController {

    public ReviewTaskAuditController(ReviewTaskService reviewTaskService) {
        super(reviewTaskService);
    }

    @Override
    public Class<ReviewChangeLogAggregate> getChangeLogAggregateClass() {
        return ReviewChangeLogAggregate.class;
    }
}
