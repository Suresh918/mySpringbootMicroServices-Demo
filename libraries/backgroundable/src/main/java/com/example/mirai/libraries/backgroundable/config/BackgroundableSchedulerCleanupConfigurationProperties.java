package com.example.mirai.libraries.backgroundable.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "mirai.libraries.backgroundable.scheduler.job-cleanup")
public class BackgroundableSchedulerCleanupConfigurationProperties {
    private Completed completed;

    @Data
    public static class Completed {
        /**
         * Delete the jobs older than the specified number of days.
         * Default value is 7 days
         */
        private Integer olderThanDays = 7;
    }

    private Failed failed;

    @Data
    public static class Failed {
        /**
         * Delete the jobs older than the specified number of days.
         * Default value is 7 days
         */
        private Integer olderThanDays = 7;
    }

    /**
     * Cron configuration for frequency at which the cleanup job is run.
     * Default value is '0 0 23 * * *', implying 23:00 hours each day.
     */
    private String cron = "0 0 23 * * *";
}
