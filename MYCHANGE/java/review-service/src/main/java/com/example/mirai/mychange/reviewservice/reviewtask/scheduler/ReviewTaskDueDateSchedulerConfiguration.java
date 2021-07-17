package com.example.mirai.projectname.reviewservice.reviewtask.scheduler;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "mirai.projectname.reviewservice.reviewtask.scheduler.due-date")
public class ReviewTaskDueDateSchedulerConfiguration {
    /**
     * Publish Review Tasks with due date within specified number of due soon days.
     */
    private Integer dueSoonDays = 2;

    private String dueSoonCron = "";

    private String expiredCron = "";

    private String lockAtMosFor = "";

    private String lockAtLeastFor = "";

}
