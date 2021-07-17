package com.example.mirai.projectname.notificationservice.engine.processor.review;

import com.example.mirai.libraries.core.model.StatusInterface;
import lombok.Getter;

import java.util.Arrays;

public class Statuses {
	@Getter
	public enum ReviewStatus implements StatusInterface {
		OPENED(1, "Open"), LOCKED(2, "Defects Locked"),
		VALIDATIONSTARTED(3, "Validate"), COMPLETED(4, "Completed");

		private final Integer statusCode;

		private final String statusLabel;

		ReviewStatus(Integer statusCode, String statusLabel) {
			this.statusCode = statusCode;
			this.statusLabel = statusLabel;
		}

		public static String getLabelByCode(Integer statusCode) {
			return Arrays.stream(ReviewStatus.values()).filter(status -> status.getStatusCode() == statusCode).findFirst().get().getStatusLabel();
		}
	}

	@Getter
	public enum ReviewTaskStatus implements StatusInterface {

		OPENED(1, "Open"), ACCEPTED(2, "Accepted"),
		NOTFINALIZED(3, "Not Completed"), FINALIZED(4, "Completed"),
		COMPLETED(5, "Completed"), REJECTED(6, "Rejected");

		private final Integer statusCode;

		private final String statusLabel;

		ReviewTaskStatus(Integer statusCode, String statusLabel) {
			this.statusCode = statusCode;
			this.statusLabel = statusLabel;
		}

		public static String getLabelByCode(Integer statusCode) {
			return Arrays.stream(ReviewTaskStatus.values()).filter(status -> status.getStatusCode() == statusCode).findFirst().get().getStatusLabel();
		}
	}

	@Getter
	public enum ReviewEntryStatus implements StatusInterface {
		OPENED(1, "Open"), ACCEPTED(2, "Accepted"),
		MARKEDDUPLICATE(3, "Duplicate"),
		REJECTED(4, "Rejected"), COMPLETED(5, "Done");

		private final Integer statusCode;

		private final String statusLabel;

		ReviewEntryStatus(Integer statusCode, String statusLabel) {
			this.statusCode = statusCode;
			this.statusLabel = statusLabel;
		}

		public static String getLabelByCode(Integer statusCode) {
			return Arrays.stream(ReviewEntryStatus.values()).filter(status -> status.getStatusCode() == statusCode).findFirst().get().getStatusLabel();
		}
	}
}
