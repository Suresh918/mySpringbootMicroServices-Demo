package com.example.mirai.projectname.reviewservice.comment.service;

import com.example.mirai.libraries.comment.service.CommentEvaluationContext;
import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.projectname.reviewservice.comment.model.ReviewEntryComment;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.review.model.ReviewStatus;
import com.example.mirai.projectname.reviewservice.review.service.ReviewService;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntry;
import com.example.mirai.projectname.reviewservice.reviewentry.service.ReviewEntryService;

import java.util.Objects;

public class ReviewEntryCommentEvaluationContext extends CommentEvaluationContext<ReviewEntryComment> {

    public boolean hasReplies() {
        ReviewEntryCommentService reviewEntryCommentService = (ReviewEntryCommentService) ApplicationContextHolder.getService(ReviewEntryCommentService.class);
        ReviewEntryComment comment = reviewEntryCommentService.findFirstUnremovedCommentByReplyToId(context.getId());
        return comment != null;
    }

    public ReviewEntry getReviewEntry() {
        ReviewEntryService reviewEntryService = (ReviewEntryService) ApplicationContextHolder.getService(ReviewEntryService.class);
        if (Objects.nonNull(context.getReviewEntry()))
            return (ReviewEntry) reviewEntryService.getEntityById(context.getReviewEntry().getId());
        return null;
    }

    public ReviewEntry getLinkedReviewEntry() {
        ReviewEntryCommentService reviewEntryCommentService = (ReviewEntryCommentService) ApplicationContextHolder.getService(ReviewEntryCommentService.class);
        ReviewEntryService reviewEntryService = (ReviewEntryService) ApplicationContextHolder.getService(ReviewEntryService.class);
        if (context.getReplyTo() != null) {
            ReviewEntryComment comment = (ReviewEntryComment) reviewEntryCommentService.getEntityById(context.getReplyTo().getId());
            return (ReviewEntry) reviewEntryService.getEntityById(comment.getReviewEntry().getId());
        }
        return null;
    }

    public Review getReview() {
        ReviewService reviewService = (ReviewService) ApplicationContextHolder.getService(ReviewService.class);
        ReviewEntry reviewEntry = getReviewEntry();
        if (reviewEntry == null) {
            reviewEntry = getLinkedReviewEntry();
        }
        return (Review) reviewService.getEntityById(reviewEntry.getReview().getId());
    }

    public boolean isReviewOpen(){
        Review review = getReview();
        return review.getStatus().equals(ReviewStatus.OPENED.getStatusCode());
    }

    public boolean isReviewCompleted(){
        Review review = getReview();
        return review.getStatus().equals(ReviewStatus.COMPLETED.getStatusCode());
    }
}
