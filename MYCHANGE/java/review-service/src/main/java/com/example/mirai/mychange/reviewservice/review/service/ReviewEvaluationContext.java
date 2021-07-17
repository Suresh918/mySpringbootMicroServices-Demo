package com.example.mirai.projectname.reviewservice.review.service;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.model.BaseEvaluationContext;
import com.example.mirai.projectname.reviewservice.review.model.ReleasePackageStatus;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.review.model.ReviewContext;
import com.example.mirai.projectname.reviewservice.review.model.ReviewStatus;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTaskStatus;
import com.example.mirai.projectname.reviewservice.reviewtask.service.ReviewTaskService;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
public class ReviewEvaluationContext extends BaseEvaluationContext<Review> {

    private Boolean hasAdditionalSolutionItem;

    public Integer getStatus() {
        return context.getStatus();
    }

    public boolean isReviewOpen(){
     return context.getStatus().equals(ReviewStatus.OPENED.getStatusCode());
    }

    public boolean isReviewLocked(){
     return context.getStatus().equals(ReviewStatus.LOCKED.getStatusCode());
    }

    public boolean isReviewValidationStarted(){
     return context.getStatus().equals(ReviewStatus.VALIDATIONSTARTED.getStatusCode());
    }

    public boolean isReviewCompleted(){
     return context.getStatus().equals(ReviewStatus.COMPLETED.getStatusCode());
    }

    public Integer getReviewTaskStatus() {
        ReviewTaskService reviewTaskService = (ReviewTaskService) ApplicationContextHolder.getService(ReviewTaskService.class);
        return reviewTaskService.findReviewTasksByReviewIdAndAssigneeUserId(context.getId(), this.auditor.getUserId()).get(0).getStatus();
    }

    public boolean isReviewTaskOpen() {
        ReviewTaskService reviewTaskService = (ReviewTaskService) ApplicationContextHolder.getService(ReviewTaskService.class);
        return reviewTaskService.findReviewTasksByReviewIdAndAssigneeUserId(context.getId(), this.auditor.getUserId()).get(0).getStatus().equals(ReviewTaskStatus.OPENED.getStatusCode());
    }

    public boolean isReviewTaskAccepted() {
        ReviewTaskService reviewTaskService = (ReviewTaskService) ApplicationContextHolder.getService(ReviewTaskService.class);
        return reviewTaskService.findReviewTasksByReviewIdAndAssigneeUserId(context.getId(), this.auditor.getUserId()).get(0).getStatus().equals(ReviewTaskStatus.ACCEPTED.getStatusCode());

    }



    public boolean isRPInValidStatus() {
        ReviewService reviewService = (ReviewService) ApplicationContextHolder.getService(ReviewService.class);
        Optional<ReviewContext> reviewRPContext = context.getContexts().stream().filter(item -> item.getType().equals("RELEASEPACKAGE")).findFirst();
        if (!reviewRPContext.isEmpty()) {
            String rpStatus = reviewRPContext.get().getStatus();
            List<Review> reviews = reviewService.findReviewsByContextTypeAndId("RELEASEPACKAGE", reviewRPContext.get().getContextId());
            List<Long> reviewIds = reviews.stream().map(review -> review.getId()).collect(Collectors.toList());
            if (rpStatus.equals(ReleasePackageStatus.CLOSED.getStatusName())) {
                return false;
            } else if (rpStatus.equals(ReleasePackageStatus.READY_FOR_RELEASE.getStatusName())
                    || rpStatus.equals(ReleasePackageStatus.RELEASED.getStatusName())) {
                //action is performable on the latest review
                return reviews.size() == 2 ? context.getId().equals(Collections.max(reviewIds)) : false;
            }
            return true;
        }
        return false;
    }




}
