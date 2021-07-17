package com.example.mirai.projectname.reviewservice.review.service;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.projectname.reviewservice.review.model.aggregate.ReviewAggregate;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask;
import com.example.mirai.projectname.reviewservice.reviewtask.model.aggregate.ReviewTaskAggregate;
import com.example.mirai.projectname.reviewservice.reviewtask.service.ReviewTaskService;

public class AggregateEventBuilder extends com.example.mirai.libraries.event.AggregateEventBuilder {
    @Override
    public Object translateResponse(Object obj) {
        ReviewTaskAggregate reviewTaskAggregate = ((ReviewAggregate) obj).getReviewTasks().iterator().next();
        ReviewTask reviewTask = (ReviewTask) ApplicationContextHolder.getService(ReviewTaskService.class).getEntityById(reviewTaskAggregate.getReviewTask().getId());
        return super.translateResponse(reviewTask.getReview());
    }
}
