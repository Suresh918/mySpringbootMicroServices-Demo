package com.example.mirai.projectname.changerequestservice.tests;


import com.example.mirai.projectname.changerequestservice.json.ExceptionResponse;
import com.example.mirai.projectname.changerequestservice.shared.exception.ChangeRequestErrorStatusCodes;
import com.example.mirai.libraries.core.exception.ErrorStatusCodes;
import org.springframework.http.HttpStatus;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import org.exparity.hamcrest.date.DateMatchers;

public class ExceptionValidator {

    public static void exceptionResponseIsMandatoryFieldViolationException(ExceptionResponse exceptionResponse, String path, String entity) {
        isMandatoryMissingException(exceptionResponse, path, entity);
    }
    public static void exceptionResponseIsUnauthorizedException(ExceptionResponse exceptionResponse, String path) {
        isForbiddenException(exceptionResponse, path);
    }
    public static void isForbiddenException(ExceptionResponse exceptionResponse, String path) {
        assertThat("wrong http status code", exceptionResponse.getError(), is(equalTo(HttpStatus.FORBIDDEN.getReasonPhrase())));
        assertThat("wrong application status code", exceptionResponse.getApplicationStatusCode(), is(equalTo(ErrorStatusCodes.FORBIDDEN.getCode())));
        assertThat("wrong message", exceptionResponse.getMessage(), is(equalTo(ErrorStatusCodes.FORBIDDEN.getMessage())));
        //assertThat("wrong path", exceptionResponse.getPath(), is(equalTo(path)));
        assertThat("wrong severity", exceptionResponse.getSeverity(), is(equalTo(com.example.mirai.libraries.core.exception.ExceptionResponse.Severities.LOW.getCode())));
        //assertThat("wrong timestamp", exceptionResponse.getTimestamp(), DateMatchers.within(10, ChronoUnit.SECONDS, new Date()));
    }
    public static void isMandatoryMissingException(ExceptionResponse exceptionResponse, String path, String entityName) {
        assertThat("wrong http status code" , exceptionResponse.getError(), is(equalTo(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())));
        assertThat("wrong application status code", exceptionResponse.getApplicationStatusCode(), is(equalTo(ErrorStatusCodes.MANDATORY_MISSING.getCode())));
        assertThat("wrong message", exceptionResponse.getMessage(), is(equalTo(ErrorStatusCodes.MANDATORY_MISSING.getMessage().replace("${entity}", entityName))));
        //assertThat("wrong path", exceptionResponse.getPath(), is(equalTo(path)));
        assertThat("wrong severity", exceptionResponse.getSeverity(), is(equalTo(com.example.mirai.libraries.core.exception.ExceptionResponse.Severities.LOW.getCode())));
        //assertThat("wrong timestamp", exceptionResponse.getTimestamp(), DateMatchers. within(10, ChronoUnit.SECONDS, new Date()));
    }

}
