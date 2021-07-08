package com.example.mirai.libraries.myteam.exception;

import javax.servlet.http.HttpServletRequest;

import com.example.mirai.libraries.core.exception.ExceptionHandlerAdvice;
import com.example.mirai.libraries.core.exception.ExceptionResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class MyTeamControllerExceptionHandler extends ExceptionHandlerAdvice {
	@ResponseBody
	@ExceptionHandler({ MyTeamMemberExistsException.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ExceptionResponse handleMyTeamMemberExistsException(HttpServletRequest req) {
		return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, MyTeamErrorStatusCodes.MY_TEAM_MEMBER_EXISTS.getCode(),
				MyTeamErrorStatusCodes.MY_TEAM_MEMBER_EXISTS.getMessage(),
				ExceptionResponse.Severities.LOW.getCode(),
				req.getRequestURI());
	}

	@ResponseBody
	@ExceptionHandler({ MyTeamMemberRoleEmptyException.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ExceptionResponse handleMyTeamMemberRoleEmptyException(HttpServletRequest req) {
		return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, MyTeamErrorStatusCodes.MY_TEAM_MEMBER_ROLE_EMPTY.getCode(),
				MyTeamErrorStatusCodes.MY_TEAM_MEMBER_ROLE_EMPTY.getMessage(),
				ExceptionResponse.Severities.LOW.getCode(),
				req.getRequestURI());
	}

}
