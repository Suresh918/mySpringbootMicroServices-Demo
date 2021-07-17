package com.example.mirai.projectname.reviewservice.review.scheduler;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "mirai.projectname.reviewservice.review.scheduler.reconciliation")
public class ReviewReconciliationSchedulerConfiguration {
    /**
     * Republish Reviews updated in specified number of past days.
     */
    private Integer modifiedInPastDays = 1;

    /**
     * Cron expression to specify the frequency with which the records are checked for update.
     */
    private String cron = "0 30 1 * * ?";
}
