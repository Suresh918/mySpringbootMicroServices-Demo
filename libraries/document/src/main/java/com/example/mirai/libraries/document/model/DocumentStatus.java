package com.example.mirai.libraries.document.model;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum DocumentStatus {
	PUBLISHED(1, "Published"), REMOVED(2, "Removed");

	private final Integer statusCode;

	private final String statusLabel;

	DocumentStatus(Integer statusCode, String statusLabel) {
		this.statusCode = statusCode;
		this.statusLabel = statusLabel;
	}

	public static String getLabelByCode(Integer statusCode) {
		return Arrays.stream(DocumentStatus.values()).filter(status -> status.getStatusCode() == statusCode).findFirst().get().getStatusLabel();
	}
}
