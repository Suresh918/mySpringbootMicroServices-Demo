package com.example.mirai.projectname.reviewservice.reviewtask.scheduler;


import com.example.mirai.libraries.event.annotation.PublishResponse;
import com.example.mirai.projectname.reviewservice.review.model.aggregate.ReviewAggregate;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask;
import com.example.mirai.projectname.reviewservice.reviewtask.service.ReviewTaskService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class ReviewTaskDueDateScheduler {
    private final ReviewTaskService reviewTaskService;
    @Resource
    ReviewTaskDueDateScheduler self;
    private final ReviewTaskDueDateSchedulerConfiguration reviewTaskDueDateSchedulerConfiguration;

    public ReviewTaskDueDateScheduler(ReviewTaskService reviewTaskService, ReviewTaskDueDateSchedulerConfiguration reviewTaskDueDateSchedulerConfiguration) {
        this.reviewTaskService = reviewTaskService;
        this.reviewTaskDueDateSchedulerConfiguration = reviewTaskDueDateSchedulerConfiguration;
    }


    @Scheduled(cron = "${mirai.projectname.reviewservice.reviewtask.scheduler.due-date.due-soon-cron}")
    @SchedulerLock(name = "ReviewTaskDueDate.publishDueSoonReviewTasks", lockAtMostFor = "${mirai.projectname.reviewservice.scheduler.lock.default-at-most-for}",
            lockAtLeastFor = "${mirai.projectname.reviewservice.scheduler.lock.default-at-least-for}")
    public void publishDueSoonReviewTasks() {
        LockAssert.assertLocked();
        log.info("scheduler - reviewtask.scheduler.due-date-soon -- started");
        List<ReviewTask> reviewTasks = reviewTaskService.getIncompleteReviewTasksWithDueDateSoon(reviewTaskDueDateSchedulerConfiguration.getDueSoonDays());
        reviewTasks.forEach(reviewTask -> self.publishDueSoon(reviewTask));
    }

    @PublishResponse(eventType = "DUE-DATE-SOON", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.reviewtask")
    public ReviewTask publishDueSoon(ReviewTask reviewTask) {
        return reviewTask;
    }

    @Scheduled(cron = "${mirai.projectname.reviewservice.reviewtask.scheduler.due-date.expired-cron}")
    @SchedulerLock(name = "ReviewTaskDueDate.publishExpiredReviewTasks", lockAtMostFor = "${mirai.projectname.reviewservice.scheduler.lock.default-at-most-for}",
            lockAtLeastFor = "${mirai.projectname.reviewservice.scheduler.lock.default-at-least-for}")
    public void publishExpiredReviewTasks() {
        LockAssert.assertLocked();
        log.info("scheduler - reviewtask.scheduler.due-date-expired -- started");
        List<ReviewTask> reviewTasks = reviewTaskService.getIncompleteReviewTasksWithDueDateExpired();
        log.info("number of expired review tasks" + reviewTasks.size());
        reviewTasks.forEach(reviewTask -> self.publishExpired(reviewTask));
    }

    @PublishResponse(eventType = "DUE-DATE-EXPIRED", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.reviewtask")
    public ReviewTask publishExpired(ReviewTask reviewTask) {
        return reviewTask;
    }
}
