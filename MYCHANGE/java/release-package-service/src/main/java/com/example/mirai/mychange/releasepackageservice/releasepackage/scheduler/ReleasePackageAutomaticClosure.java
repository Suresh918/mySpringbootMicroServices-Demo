package com.example.mirai.projectname.releasepackageservice.releasepackage.scheduler;

import com.example.mirai.projectname.releasepackageservice.releasepackage.service.ReleasePackageAutomaticClosureService;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.ReleasePackageService;
import lombok.AllArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ReleasePackageAutomaticClosure {
    private final ReleasePackageAutomaticClosureService releasePackageAutomaticClosureService;

    @Scheduled(cron = "${mirai.projectname.releasepackageservice.releasepackage.scheduler.automatic-closure.cron}")
	@SchedulerLock(name = "publishReleasePackagesForAutomaticClosure")
    public void publishReleasePackagesForAutomaticClosure() {
        try {
            ReleasePackageService.AutomaticClosureHolder.isAutomaticClosure().set(true);
            releasePackageAutomaticClosureService.closeReleasePackages();
        } finally {
            ReleasePackageService.AutomaticClosureHolder.isAutomaticClosure().remove();
        }
    }

}
