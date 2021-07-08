package com.example.mirai.libraries.core.exception;

import java.sql.Timestamp;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ExceptionResponse {
	String applicationStatusCode;

	Object status;

	String error;

	Integer severity;

	String message;

	String path;

	Timestamp timestamp;

	public ExceptionResponse(HttpStatus httpStatus, String applicationStatusCode, String message, Integer severity, String path) {
		this.status = httpStatus.value();
		this.error = httpStatus.getReasonPhrase();
		this.applicationStatusCode = applicationStatusCode;
		this.message = message;
		this.severity = severity;
		this.path = path;
		this.timestamp = new Timestamp(new Date().getTime());
	}


	public ExceptionResponse(HttpStatus httpStatus, ErrorStatusCodes status, Integer severity, String path) {
		this.status = httpStatus.value();
		this.error = httpStatus.getReasonPhrase();
		this.message = status.getMessage();
		this.applicationStatusCode = status.getCode();
		this.severity = severity;
		this.path = path;
		this.timestamp = new Timestamp(new Date().getTime());
	}

	public ExceptionResponse(HttpStatus httpStatus, ErrorStatusCodes status, Integer severity, String path, String message) {
		this.status = httpStatus.value();
		this.error = httpStatus.getReasonPhrase();
		this.message = "<div> " + message + "<div>" + status.getMessage();
		this.applicationStatusCode = status.getCode();
		this.severity = severity;
		this.path = path;
		this.timestamp = new Timestamp(new Date().getTime());
	}

	@Getter
	public enum Severities {
		HIGH(1), MEDIUM(2), LOW(3), MODERATE(4);

		private final Integer code;

		Severities(int code) {
			this.code = code;
		}
	}
}

