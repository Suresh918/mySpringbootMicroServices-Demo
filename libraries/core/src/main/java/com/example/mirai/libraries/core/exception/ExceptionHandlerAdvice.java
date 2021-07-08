package com.example.mirai.libraries.core.exception;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

/**
 * <p>By default, the methods in an {@code @ControllerAdvice} apply globally to all controllers.
 *
 * <p>For handling exceptions, an {@code @ExceptionHandler} will be picked on the first advice with a matching exception handler method
 *
 * @author ptummala
 * @see ControllerAdvice
 * @see RestControllerAdvice
 * @since 1.0.0
 */
@ControllerAdvice
public class ExceptionHandlerAdvice {
    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(value = EntityIdNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse entityIdNotFoundExceptionHandler(HttpServletRequest req, EntityIdNotFoundException e) {
        String message = ErrorStatusCodes.ID_NOT_FOUND.getMessage().replace("${entity}", e.getEntity() == null ? "Entity" : e.getEntity());
        return new ExceptionResponse(HttpStatus.NOT_FOUND, ErrorStatusCodes.ID_NOT_FOUND.getCode(), message,
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(value = ParallelUpdateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse parallelUpdateExceptionHandler(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.CONFLICT, ErrorStatusCodes.CONFLICT,
                ExceptionResponse.Severities.MODERATE.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(value = UnauthorizedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionResponse unauthorizedExceptionHandler(HttpServletRequest req, UnauthorizedException e) {
        return new ExceptionResponse(HttpStatus.FORBIDDEN, ErrorStatusCodes.FORBIDDEN,
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(value = InternalAssertionException.class)
    @ResponseStatus(HttpStatus.NOT_EXTENDED)
    public ExceptionResponse internalAssertionExceptionHandler(HttpServletRequest req, Exception e) {
        return new ExceptionResponse(HttpStatus.NOT_EXTENDED, ErrorStatusCodes.NOT_EXTENDED,
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI(), e.getMessage());
    }

    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler({CaseActionNotFoundException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleCaseActionNotFound(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorStatusCodes.INVALID_CASE_ACTION.getCode(),
                ErrorStatusCodes.INVALID_CASE_ACTION.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler({EntityLinkMismatchException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleEntityLinkMismatch(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorStatusCodes.ENTITY_LINK_MISMATCH.getCode(),
                ErrorStatusCodes.ENTITY_LINK_MISMATCH.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(value = DefaultException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse defaultExceptionHandler(HttpServletRequest req, Exception e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorStatusCodes.DEFAULT.getCode(), e.getMessage() != null ? e.getMessage() : ErrorStatusCodes.DEFAULT.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler({MandatoryFieldViolationException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleMandatoryFieldViolation(HttpServletRequest req, MandatoryFieldViolationException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorStatusCodes.MANDATORY_MISSING.getCode(),
                ErrorStatusCodes.MANDATORY_MISSING.getMessage().replace("${entity}", e.getEntity() == null ? "Entity" : e.getEntity()),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    // exceptions thrown from spring library
    @org.springframework.web.bind.annotation.ExceptionHandler({ConversionNotSupportedException.class, HttpMessageNotWritableException.class, MissingPathVariableException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    protected ExceptionResponse handleServerErrorException(
            Exception e, HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorStatusCodes.NOT_EXTENDED.getCode(),
                ErrorStatusCodes.NOT_EXTENDED.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({MissingServletRequestParameterException.class,
            ServletRequestBindingException.class, TypeMismatchException.class, HttpMessageNotReadableException.class,
            MethodArgumentNotValidException.class, MissingServletRequestPartException.class, BindException.class,
            ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    protected ExceptionResponse handleBadRequestException(
            Exception e, HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.BAD_REQUEST, ErrorStatusCodes.BAD_REQUEST.getCode(),
                ErrorStatusCodes.BAD_REQUEST.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    protected ExceptionResponse handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e, HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.METHOD_NOT_ALLOWED, ErrorStatusCodes.METHOD_NOT_ALLOWED.getCode(),
                ErrorStatusCodes.METHOD_NOT_ALLOWED.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ResponseBody
    protected ExceptionResponse handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException e, HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ErrorStatusCodes.UNSUPPORTED_MEDIA_TYPE.getCode(),
                ErrorStatusCodes.UNSUPPORTED_MEDIA_TYPE.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ResponseBody
    protected ExceptionResponse handleHttpMediaTypeNotAcceptableException(
            HttpMediaTypeNotAcceptableException e, HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.NOT_ACCEPTABLE, ErrorStatusCodes.NOT_ACCEPTABLE.getCode(),
                ErrorStatusCodes.NOT_ACCEPTABLE.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({NoHandlerFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    protected ExceptionResponse handleNoHandlerFoundException(
            NoHandlerFoundException e, HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.NOT_FOUND, ErrorStatusCodes.NO_HANDLER_FOUND.getCode(),
                ErrorStatusCodes.NO_HANDLER_FOUND.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({AsyncRequestTimeoutException.class})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ResponseBody
    protected ExceptionResponse handleAsyncRequestTimeoutException(
            RuntimeException e, HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.SERVICE_UNAVAILABLE, ErrorStatusCodes.SERVICE_UNAVAILABLE.getCode(),
                ErrorStatusCodes.SERVICE_UNAVAILABLE.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }
}
