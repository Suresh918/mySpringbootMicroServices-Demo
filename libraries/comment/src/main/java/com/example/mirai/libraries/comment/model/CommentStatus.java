package com.example.mirai.libraries.comment.model;

import java.util.Arrays;

import com.example.mirai.libraries.core.model.StatusInterface;
import lombok.Getter;

@Getter
public enum CommentStatus implements StatusInterface {
	DRAFTED(1, "Draft"), PUBLISHED(2, "Published"), REMOVED(3, "Removed");

	private final Integer statusCode;

	private final String statusLabel;

	CommentStatus(Integer statusCode, String statusLabel) {
		this.statusCode = statusCode;
		this.statusLabel = statusLabel;
	}

	public static String getLabelByCode(Integer statusCode) {
		return Arrays.stream(CommentStatus.values()).filter(status -> status.getStatusCode() == statusCode).findFirst().get().getStatusLabel();
	}
}
