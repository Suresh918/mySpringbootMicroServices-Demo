package com.example.mirai.libraries.jdbcdatasource.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JdbcSourceException extends RuntimeException {

	private final String message;

	public JdbcSourceException(String message) {
		this.message = message;
	}
}
