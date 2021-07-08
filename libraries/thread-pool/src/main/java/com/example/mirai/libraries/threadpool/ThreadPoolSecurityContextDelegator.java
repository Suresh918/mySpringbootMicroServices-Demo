package com.example.mirai.libraries.threadpool;

import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

@Configuration
@AllArgsConstructor
public class ThreadPoolSecurityContextDelegator {

	ThreadPoolConfiguration threadPoolConfiguration;

	@Bean
	public ThreadPoolTaskExecutor miraiThreadPoolTaskExecutor() {
		final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(threadPoolConfiguration.getCorePoolSize());
		executor.setMaxPoolSize(threadPoolConfiguration.getMaxPoolSize());
		executor.setQueueCapacity(threadPoolConfiguration.getQueueCapacity());
		executor.setThreadNamePrefix(threadPoolConfiguration.getThreadNamePrefix());
		return executor;
	}

	@Bean
	public DelegatingSecurityContextAsyncTaskExecutor miraiDelegatingSecurityContextAsyncTaskExecutor(ThreadPoolTaskExecutor miraiThreadPoolTaskExecutor) {
		return new DelegatingSecurityContextAsyncTaskExecutor(miraiThreadPoolTaskExecutor);
	}
}
