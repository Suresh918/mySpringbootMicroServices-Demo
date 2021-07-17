package com.example.mirai.services.userservice.profile;

import java.util.List;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mirai.services.user-service.profile")
@Data
public class ProfileConfigurationProperties {
	/**
	 * Topic on which to publish user profile.
	 */
	private String destination = "com.example.mirai.services.userservice.profile";
	/**
	 * whether to publish the profile created profile or not.
	 */
	private Boolean publishEnabled = true;

	/**
	 * List of roles.
	 */
	private String[] roles;

	private final LastLoggedInReport lastLoggedInReport = new LastLoggedInReport();

	@Data
	public static class LastLoggedInReport {

		/**
		 * whether to publish the last logged in report or not.
		 */
		private Boolean publishEnabled = true;
		/**
		 * JMS destination to publish the last logged in report data onto
		 */
		private String destination = "com.example.mirai.services.userservice.profile.lastloggedinreport";

		/**
		 * Collect last logged in users for the given amount of days in the past.
		 */
		private Integer pastDays = 30;

		/**
		 * Cron schedule to send out last last logged in email
		 */
		private String cron = "0 0 0 1 * *";

		/**
		 * Recipient email addresses to receive last logged in email
		 */
		private List<String> recipients;
	}
}
