package com.example.mirai.libraries.backgroundable.scheduler;

import com.example.mirai.libraries.backgroundable.service.JobService;
import lombok.AllArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class JobCleanup {
	private final JobService jobService;

	@Scheduled(cron = "${mirai.libraries.backgroundable.scheduler.job-cleanup.cron}")
	public void cleanUpJobs() {
		jobService.cleanUpJobs();
	}
}
