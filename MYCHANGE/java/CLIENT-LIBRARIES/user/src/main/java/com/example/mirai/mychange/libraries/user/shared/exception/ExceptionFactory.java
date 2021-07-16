package com.example.mirai.projectname.libraries.user.shared.exception;

public class ExceptionFactory {
	public static UserException getExceptionInstance(Exception exception) {
		return new UserDefaultException(exception.getMessage());
	}
}

