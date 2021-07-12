package com.example.mirai.projectname.changerequestservice.changerequest.scheduler;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.event.AggregateEventBuilder;
import com.example.mirai.libraries.event.annotation.PublishResponse;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestAggregate;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestService;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class ChangeRequestSchedulerReconciliation {
    @Resource
    ChangeRequestSchedulerReconciliation self;

    private final ChangeRequestService changeRequestService;
    private final ChangeRequestSchedulerReconciliationConfiguration changeRequestSchedulerReconciliationConfiguration;

    public ChangeRequestSchedulerReconciliation(ChangeRequestService changeRequestService, ChangeRequestSchedulerReconciliationConfiguration changeRequestSchedulerReconciliationConfiguration) {
        this.changeRequestService = changeRequestService;
        this.changeRequestSchedulerReconciliationConfiguration = changeRequestSchedulerReconciliationConfiguration;
    }


    @Scheduled(cron = "${mirai.projectname.changerequestservice.changerequest.scheduler.reconciliation.cron}")
    @SchedulerLock(name = "publishModifiedChangeRequests")
    public void publishModifiedChangeRequests() {
        List<BaseEntityInterface> changeRequests = changeRequestService.getChangeRequestModifiedInLastDays(changeRequestSchedulerReconciliationConfiguration.getModifiedInPastDays());
        if (changeRequests != null)
            changeRequests.forEach(changeRequest -> self.publishModified((ChangeRequest) changeRequest));
    }

    @PublishResponse(eventType = "RECONCILIATION", eventBuilder = AggregateEventBuilder.class,
            responseClass = ChangeRequestAggregate.class, destination="com.example.mirai.projectname.changerequestservice.changerequest")
    public ChangeRequest publishModified(ChangeRequest changeRequest) {
        return changeRequest;
    }


}
