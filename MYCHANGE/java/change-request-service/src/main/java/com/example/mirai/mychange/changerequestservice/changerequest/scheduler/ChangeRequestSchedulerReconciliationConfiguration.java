package com.example.mirai.projectname.changerequestservice.changerequest.scheduler;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "mirai.projectname.changerequestservice.changerequest.scheduler.reconciliation")
public class ChangeRequestSchedulerReconciliationConfiguration {
    /**
     * Republish Change Requests updated in specified number of past days.
     */
    private Integer modifiedInPastDays = 1;
}
