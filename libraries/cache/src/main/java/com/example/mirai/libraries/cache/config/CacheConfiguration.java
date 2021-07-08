package com.example.mirai.libraries.cache.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CacheConfiguration {

	/**
	 * Enables or Disables caching of entity; disabled by default"
	 */
	protected Boolean enabled = true;

	/**
	 * Configures cron expression for frequency of cache refresh"
	 */
	protected String refreshCron = "0 0 23 * * *";

	/**
	 * Enables or Disables cache refresh on start; disabled by default"
	 */
	protected Boolean enableRefreshOnStart = true;

	/**
	 * Enable this to perform caching in separate thread.
	 */
	protected Boolean asynchronous = false;
}
