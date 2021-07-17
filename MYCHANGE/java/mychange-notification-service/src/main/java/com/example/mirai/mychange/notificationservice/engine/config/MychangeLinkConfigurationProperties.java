package com.example.mirai.projectname.notificationservice.engine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration("projectname.notification-service.link.LinkConfigurationProperties")
@ConfigurationProperties(prefix = "projectname.notification-service.link")
@Data
public class MychangeLinkConfigurationProperties {

	private projectname projectname;
	/**
	 * URL to be used in notifications to construct deep link into Teamcenter ECN page.
	 */
	private String ecn = "team center url";

	private String dia = "dia url";
	/**
	 * DIA URL to be used in notifications to construct deep link into Cerberus DIA page.
	 */
	private String diaByChangeNoticeId = "dia url";

	private String delta1 ="team center url";
	private String delta2 ="team center url1";

	/**
	 * DIA URL to be used in notifications to construct deep link into Cerberus DIA page.
	 */
	private String diaByChangeRequestId = "dia url";

	@Data
	public static class projectname {
		/**
		 * Environment specific projectname base URL to be used in notifications to construct deep link into projectname.
		 */
		private String baseUrl = "https://projectname.example.moc";

		/**
		 * Partial review url to be used in notifications to construct deep link into projectname review details page.
		 */
		private String review = "/reviews/{ID}";

		/**
		 * Partial release package URL to be used in notifications to construct deep link into projectname release package details page.
		 */
		private String releasePackage = "/release-packages/{ID}";

		/**
		 * Partial URL to be used in notifications to construct deep link into projectname notifications settings page.
		 */
		private String notifications = "/settings/notifications";

		/**
		 * Partial release package URL to be used in notifications to construct deep link into projectname change request details page.
		 */
		private String changeRequest = "/change-requests/{ID}";

		/**
		 * Partial release package URL to be used in notifications to construct deep link into projectname Implementation Strategy page.
		 */
		private String ims = "/change-requests/cr-implementation-strategy/{ID}";
	}
}
