package com.example.mirai.services.userservice.exception;

import java.sql.Timestamp;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.http.HttpStatus;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
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
		this.timestamp = new Timestamp((new Date()).getTime());
	}

	public ExceptionResponse(HttpStatus httpStatus, ApplicationErrorStatusCodes status, Integer severity, String path) {
		this.status = httpStatus.value();
		this.error = httpStatus.getReasonPhrase();
		this.message = status.getMessage();
		this.applicationStatusCode = status.getCode();
		this.severity = severity;
		this.path = path;
		this.timestamp = new Timestamp((new Date()).getTime());
	}

	public ExceptionResponse(HttpStatus httpStatus, ApplicationErrorStatusCodes status, Integer severity, String path, String message) {
		this.status = httpStatus.value();
		this.error = httpStatus.getReasonPhrase();
		this.message = "<div> " + message + "<div>" + status.getMessage();
		this.applicationStatusCode = status.getCode();
		this.severity = severity;
		this.path = path;
		this.timestamp = new Timestamp((new Date()).getTime());
	}

	public enum Severities {
		HIGH(1),
		MEDIUM(2),
		LOW(3),
		MODERATE(4);

		private final Integer code;

		Severities(int code) {
			this.code = code;
		}

		public Integer getCode() {
			return this.code;
		}
	}

}
