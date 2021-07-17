package com.example.mirai.projectname.reviewservice.review.scheduler;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.event.annotation.PublishResponse;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.review.model.aggregate.ReviewAggregate;
import com.example.mirai.projectname.reviewservice.review.service.ReviewService;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class ReviewReconciliationScheduler {

    @Resource
    private ReviewReconciliationScheduler self;
    private final ReviewService reviewService;
    private final ReviewReconciliationSchedulerConfiguration reviewReconciliationSchedulerConfiguration;

    public ReviewReconciliationScheduler(ReviewService reviewService, ReviewReconciliationSchedulerConfiguration reviewReconciliationSchedulerConfiguration) {
        this.reviewReconciliationSchedulerConfiguration = reviewReconciliationSchedulerConfiguration;
        this.reviewService = reviewService;
    }

    @Scheduled(cron = "${mirai.projectname.reviewservice.review.scheduler.reconciliation.cron}")
    @SchedulerLock(name = "Review.reconciliation", lockAtMostFor = "${mirai.projectname.reviewservice.scheduler.lock.default-at-most-for}",
            lockAtLeastFor = "${mirai.projectname.reviewservice.scheduler.lock.default-at-least-for}")
    public void publishReviewsForReconciliation() {
        List<BaseEntityInterface> reviews = reviewService.getUpdatedReviewsInDuration(reviewReconciliationSchedulerConfiguration.getModifiedInPastDays());
        reviews.forEach(review -> self.publishUpdatedReview((Review) review));
    }

    @PublishResponse(eventType = "RECONCILIATION", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.review")
    public Review publishUpdatedReview(Review review) {
        return review;
    }
}
