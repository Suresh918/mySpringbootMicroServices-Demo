package com.example.mirai.libraries.threadpool;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(
		prefix = "mirai.libraries.thread-pool"
)
@Data
public class ThreadPoolConfiguration {

	/**
	 * Specify whether to allow core threads to time out.
	 */
	private Boolean setAllowCoreThreadTimeOut = false;

	/**
	 * Set the ThreadPoolExecutor's core pool size.
	 */
	private Integer corePoolSize = 10;

	/**
	 * Set the ThreadPoolExecutor's keep-alive seconds.
	 */
	private Integer setKeepAliveSeconds = 60;

	/**
	 * Set the ThreadPoolExecutor's maximum pool size.
	 */
	private Integer maxPoolSize = 100;

	/**
	 * Set the capacity for the ThreadPoolExecutor's BlockingQueue.
	 */
	private Integer queueCapacity = 50;

	/**
	 * Base URL to be used to access service endpoint.
	 */
	private String threadNamePrefix = "mirai-tp-";
}
