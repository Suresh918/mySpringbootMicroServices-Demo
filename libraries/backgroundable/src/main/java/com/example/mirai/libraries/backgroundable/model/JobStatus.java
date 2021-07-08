package com.example.mirai.libraries.backgroundable.model;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum JobStatus {
	PROCESSING(1, "Processing"), COMPLETED(2, "Completed"),
	FAILED(3, "Failed");

	private final Integer statusCode;

	private final String statusLabel;

	JobStatus(Integer statusCode, String statusLabel) {
		this.statusCode = statusCode;
		this.statusLabel = statusLabel;
	}

	public static String getLabelByCode(Integer statusCode) {
		return Arrays.stream(JobStatus.values()).filter(status -> status.getStatusCode().equals(statusCode)).findFirst().get().getStatusLabel();
	}
}
