package com.example.mirai.projectname.libraries.user.shared.exception;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserException extends RuntimeException {
	private final String applicationStatusCode;

	private final Integer status;

	private final String error;

	private final Integer severity;

	private final String message;

	private final Timestamp timestamp;

	public UserException(String applicationStatusCode, Integer status, String error, Integer severity, String message, Timestamp timestamp) {
		this.applicationStatusCode = applicationStatusCode;
		this.status = status;
		this.error = error;
		this.severity = severity;
		this.message = message;
		this.timestamp = timestamp;
	}
}
