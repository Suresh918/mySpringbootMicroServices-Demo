package com.example.mirai.projectname.reviewservice.review.controller;

import com.example.mirai.libraries.security.core.controller.SecurityController;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.review.model.aggregate.ReviewCaseStatusAggregate;
import com.example.mirai.projectname.reviewservice.review.service.ReviewService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("reviews")
public class ReviewSecurityController extends SecurityController {

    public ReviewSecurityController(ReviewService reviewService) {
        super(reviewService);
    }

    @Override
    public Class<ReviewCaseStatusAggregate> getCaseStatusAggregateClass() {
        return ReviewCaseStatusAggregate.class;
    }

    @Override
    public Class<Review> getEntityClass() {
        return Review.class;
    }
}
