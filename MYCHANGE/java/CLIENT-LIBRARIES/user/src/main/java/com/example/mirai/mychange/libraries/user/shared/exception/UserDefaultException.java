package com.example.mirai.projectname.libraries.user.shared.exception;

import java.sql.Timestamp;
import java.util.Date;

public class UserDefaultException extends UserException {
	public UserDefaultException(String exceptionMessage) {
		super("USER-000", 500, "error", 3, ApplicationStatusCode.USER_DEFAULT + " : " + exceptionMessage, new Timestamp(new Date().getTime()));
	}
}
