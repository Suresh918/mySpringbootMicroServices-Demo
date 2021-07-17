package com.example.mirai.projectname.reviewservice.reviewtask.controller;

import com.example.mirai.libraries.security.core.controller.SecurityController;
import com.example.mirai.projectname.reviewservice.review.model.aggregate.ReviewCaseStatusAggregate;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask;
import com.example.mirai.projectname.reviewservice.reviewtask.service.ReviewTaskService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("reviews/{entityType:review-tasks}")
public class ReviewTaskSecurityController extends SecurityController {

    public ReviewTaskSecurityController(ReviewTaskService reviewTaskService) {
        super(reviewTaskService);
    }

    @Override
    public Class<ReviewCaseStatusAggregate> getCaseStatusAggregateClass() {
        return ReviewCaseStatusAggregate.class;
    }

    @Override
    public Class<ReviewTask> getEntityClass() {
        return ReviewTask.class;
    }
}
