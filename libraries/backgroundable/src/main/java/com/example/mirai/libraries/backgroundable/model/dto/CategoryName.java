package com.example.mirai.libraries.backgroundable.model.dto;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum CategoryName {
	SCHEDULED(0, "Scheduled"), PROCESSING(1, "Processing"),
	COMPLETED(2, "Completed"), FAILED(3, "Failed");

	private final int categoryCode;

	private final String categoryLabel;

	CategoryName(int categoryCode, String categoryLabel) {
		this.categoryCode = categoryCode;
		this.categoryLabel = categoryLabel;
	}

	public static CategoryName getLabelByCode(int statusCode) {
		return Arrays.stream(CategoryName.values()).filter(status -> status.categoryCode == statusCode).findFirst().get();
	}
}
