package com.example.mirai.projectname.reviewservice.reviewtask.service;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.model.BaseEvaluationContext;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.review.model.ReviewStatus;
import com.example.mirai.projectname.reviewservice.review.service.ReviewService;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntry;
import com.example.mirai.projectname.reviewservice.reviewentry.service.ReviewEntryService;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTaskStatus;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor
public class ReviewTaskEvaluationContext extends BaseEvaluationContext<ReviewTask> {

    public Integer getStatus() {
        return context.getStatus();
    }

    public boolean isReviewTaskOpen(){
        return context.getStatus().equals(ReviewTaskStatus.OPENED.getStatusCode());
    }

    public boolean isReviewTaskAccepted(){
        return context.getStatus().equals(ReviewTaskStatus.ACCEPTED.getStatusCode());
    }

    public boolean isReviewTaskNotFinalized(){
        return context.getStatus().equals(ReviewTaskStatus.NOTFINALIZED.getStatusCode());
    }

    public boolean isReviewTaskFinalized(){
        return context.getStatus().equals(ReviewTaskStatus.FINALIZED.getStatusCode());
    }

    public boolean isReviewTaskCompleted(){
        return context.getStatus().equals(ReviewTaskStatus.COMPLETED.getStatusCode());
    }

    public boolean isReviewTaskRejected(){
        return context.getStatus().equals(ReviewTaskStatus.REJECTED.getStatusCode());
    }

    public Review getReview() {
        // fetch the updated review always as the review in review task might not have the updated data
        ReviewService reviewService = (ReviewService) ApplicationContextHolder.getService(ReviewService.class);
        return (Review) reviewService.getEntityById(context.getReview().getId());
    }

    public boolean isReviewOpen(){
        return getReview().getStatus().equals(ReviewStatus.OPENED.getStatusCode());
    }

    public boolean isReviewCompleted(){
        return getReview().getStatus().equals(ReviewStatus.COMPLETED.getStatusCode());
    }

    // used in case action acl
    public Boolean hasReviewEntry() {
        if (context != null) {
            ReviewEntryService reviewEntryService = (ReviewEntryService) ApplicationContextHolder.getService(ReviewEntryService.class);
            List<ReviewEntry> reviewEntries = reviewEntryService.findReviewEntriesByReviewTask(context, PageRequest.of(0, 1));
            return (reviewEntries.size() > 0);
        }
        return null;
    }

}
