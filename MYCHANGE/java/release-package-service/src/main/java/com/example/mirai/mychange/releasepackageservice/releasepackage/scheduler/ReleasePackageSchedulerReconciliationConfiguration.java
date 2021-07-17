package com.example.mirai.projectname.releasepackageservice.releasepackage.scheduler;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "mirai.projectname.releasepackageservice.releasepackage.scheduler.reconciliation")
public class ReleasePackageSchedulerReconciliationConfiguration {
    /**
     * Republish Release Packages updated in specified number of past days.
     */
    private Integer modifiedInPastDays = 1;
}
