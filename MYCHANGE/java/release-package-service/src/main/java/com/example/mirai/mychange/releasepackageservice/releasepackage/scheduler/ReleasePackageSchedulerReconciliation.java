package com.example.mirai.projectname.releasepackageservice.releasepackage.scheduler;

import java.util.List;

import javax.annotation.Resource;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.event.annotation.PublishResponse;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate.ReleasePackageAggregate;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.ReleasePackageService;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReleasePackageSchedulerReconciliation {
    private final ReleasePackageService releasePackageService;
    private final ReleasePackageSchedulerReconciliationConfiguration releasePackageSchedulerReconciliationConfiguration;

    @Resource
    private ReleasePackageSchedulerReconciliation self;

    public ReleasePackageSchedulerReconciliation(ReleasePackageService releasePackageService, ReleasePackageSchedulerReconciliationConfiguration releasePackageSchedulerReconciliationConfiguration) {
        this.releasePackageService = releasePackageService;
        this.releasePackageSchedulerReconciliationConfiguration = releasePackageSchedulerReconciliationConfiguration;
    }

    @Scheduled(cron = "${mirai.projectname.releasepackageservice.releasepackage.scheduler.reconciliation.cron}")
    @SchedulerLock(name = "publishReleasePackagesForReconciliation")
    public void publishReleasePackagesForReconciliation() {
        List<BaseEntityInterface> releasePackages = releasePackageService.getUpdatedReleasePackagesInDuration(releasePackageSchedulerReconciliationConfiguration.getModifiedInPastDays());
        releasePackages.forEach(releasePackage -> self.publishUpdatedReleasePackage((ReleasePackage) releasePackage));
    }

    @PublishResponse(eventType = "RECONCILIATION", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReleasePackageAggregate.class, destination = "com.example.mirai.projectname.releasepackageservice.releasepackage")
    public ReleasePackage publishUpdatedReleasePackage(ReleasePackage releasePackage) {
        return releasePackage;
    }

}
