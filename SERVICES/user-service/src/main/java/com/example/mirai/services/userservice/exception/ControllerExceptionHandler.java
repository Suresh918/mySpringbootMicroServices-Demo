package com.example.mirai.services.userservice.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerExceptionHandler {

	@ResponseBody
	@ExceptionHandler({ ProfileCreationFailedException.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ExceptionResponse handleProfileCreationFailed(HttpServletRequest req) {
		return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ApplicationErrorStatusCodes.PROFILE_CREATION_FAILED.getCode(),
				ApplicationErrorStatusCodes.PROFILE_CREATION_FAILED.getMessage(),
				ExceptionResponse.Severities.HIGH.getCode(),
				req.getRequestURI());
	}

	@ResponseBody
	@ExceptionHandler({ ProfilePublishFailedException.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ExceptionResponse handleProfilePublishFailedException(HttpServletRequest req) {
		return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ApplicationErrorStatusCodes.PROFILE_PUBLISH_FAILED.getCode(),
				ApplicationErrorStatusCodes.PROFILE_PUBLISH_FAILED.getMessage(),
				ExceptionResponse.Severities.HIGH.getCode(),
				req.getRequestURI());
	}

	@ResponseBody
	@ExceptionHandler({ PreferredRoleUpdatePublishFailedException.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ExceptionResponse handlePreferredRoleUpdatePublishFailedException(HttpServletRequest req) {
		return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ApplicationErrorStatusCodes.PREFERRED_ROLES_UPDATE_PUBLISH_FAILED.getCode(),
				ApplicationErrorStatusCodes.PREFERRED_ROLES_UPDATE_PUBLISH_FAILED.getMessage(),
				ExceptionResponse.Severities.MEDIUM.getCode(),
				req.getRequestURI());
	}

}
