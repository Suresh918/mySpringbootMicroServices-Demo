package com.example.mirai.projectname.reviewservice.reviewentry.service;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.model.BaseEvaluationContext;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.review.model.ReviewStatus;
import com.example.mirai.projectname.reviewservice.review.service.ReviewService;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntry;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntryStatus;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTaskStatus;
import com.example.mirai.projectname.reviewservice.reviewtask.service.ReviewTaskService;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ReviewEntryEvaluationContext extends BaseEvaluationContext<ReviewEntry> {

    public Integer getStatus() {
        return context.getStatus();
    }

    public boolean isReviewEntryOpen(){
        return context.getStatus().equals(ReviewEntryStatus.OPENED.getStatusCode());
    }

    public boolean isReviewEntryAccepted(){
        return context.getStatus().equals(ReviewEntryStatus.ACCEPTED.getStatusCode());
    }

    public boolean isReviewEntryMarkedDuplicate(){
        return context.getStatus().equals(ReviewEntryStatus.MARKEDDUPLICATE.getStatusCode());
    }

    public boolean isReviewEntryRejected(){
        return context.getStatus().equals(ReviewEntryStatus.REJECTED.getStatusCode());
    }

    public boolean isReviewEntryCompleted(){
        return context.getStatus().equals(ReviewEntryStatus.COMPLETED.getStatusCode());
    }

    // used in case action acl
    public Review fetchReview() {
        ReviewService reviewService = (ReviewService) ApplicationContextHolder.getService(ReviewService.class);
        return (Review) reviewService.getEntityById(context.getReview().getId());

    }

    public boolean isReviewOpen(){
        return fetchReview().getStatus().equals(ReviewStatus.OPENED.getStatusCode());
    }

    public boolean isReviewCompleted(){
        return fetchReview().getStatus().equals(ReviewStatus.COMPLETED.getStatusCode());
    }

    // used in case action acl
    public ReviewTask fetchReviewTask() {
        ReviewTaskService reviewTaskService = (ReviewTaskService) ApplicationContextHolder.getService(ReviewTaskService.class);
        return (ReviewTask) reviewTaskService.getEntityById(context.getReviewTask().getId());
    }

    public boolean isReviewTaskOpen(){
        return fetchReviewTask().getStatus().equals(ReviewTaskStatus.OPENED.getStatusCode());
    }

    public boolean isReviewTaskAccepted(){
        return fetchReviewTask().getStatus().equals(ReviewTaskStatus.ACCEPTED.getStatusCode());
    }

    public boolean isAuditorReviewTaskAssignee() {
        String auditorUserId = auditor.getUserId();
        return auditorUserId.equals(this.context.getReviewTask().getAssignee().getUserId());
    }
}
