package com.example.mirai.projectname.releasepackageservice.utils;


import com.example.mirai.libraries.core.exception.ErrorStatusCodes;
import com.example.mirai.projectname.releasepackageservice.json.ExceptionResponse;
import org.exparity.hamcrest.date.DateMatchers;
import org.springframework.http.HttpStatus;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ExceptionValidator {
    public static void exceptionResponseIsUnauthorizedException(ExceptionResponse exceptionResponse, String path) {
        isForbiddenException(exceptionResponse, path);
    }


    public static void exceptionResponseIsMandatoryFieldViolationException(ExceptionResponse exceptionResponse, String path, String entity) {
        isMandatoryMissingException(exceptionResponse, path, entity);
    }

    public static void exceptionResponseIsApplicationException(ExceptionResponse exceptionResponse, String path, String entity) {
        isApplicationException(exceptionResponse, path, entity);
    }

    public static void isForbiddenException(ExceptionResponse exceptionResponse, String path) {
        assertThat("wrong http status code", exceptionResponse.getError(), is(equalTo(HttpStatus.FORBIDDEN.getReasonPhrase())));
        assertThat("wrong application status code", exceptionResponse.getApplicationStatusCode(), is(equalTo(ErrorStatusCodes.FORBIDDEN.getCode())));
        assertThat("wrong message", exceptionResponse.getMessage(), is(equalTo(ErrorStatusCodes.FORBIDDEN.getMessage())));
        assertThat("wrong path", exceptionResponse.getPath(), is(equalTo(path)));
        assertThat("wrong severity", exceptionResponse.getSeverity(), is(equalTo(com.example.mirai.libraries.core.exception.ExceptionResponse.Severities.LOW.getCode())));
        assertThat("wrong timestamp", exceptionResponse.getTimestamp(), DateMatchers.within(10, ChronoUnit.SECONDS, new Date()));
    }

    public static void isMandatoryMissingException(ExceptionResponse exceptionResponse, String path, String entityName) {
        assertThat("wrong http status code", exceptionResponse.getError(), is(equalTo(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())));
        assertThat("wrong application status code", exceptionResponse.getApplicationStatusCode(), is(equalTo(ErrorStatusCodes.MANDATORY_MISSING.getCode())));
        assertThat("wrong message", exceptionResponse.getMessage(), is(equalTo(ErrorStatusCodes.MANDATORY_MISSING.getMessage().replace("${entity}", entityName))));
        assertThat("wrong path", exceptionResponse.getPath(), is(equalTo(path)));
        assertThat("wrong severity", exceptionResponse.getSeverity(), is(equalTo(com.example.mirai.libraries.core.exception.ExceptionResponse.Severities.LOW.getCode())));
        assertThat("wrong timestamp", exceptionResponse.getTimestamp(), DateMatchers.within(10, ChronoUnit.SECONDS, new Date()));
    }

    public static void isApplicationException(ExceptionResponse exceptionResponse, String path, String entityName) {
        assertThat("wrong http status code", exceptionResponse.getError(), is(equalTo(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())));
    }
}
