package com.example.mirai.projectname.changerequestservice.shared.controller;

import com.example.mirai.libraries.air.problem.shared.exception.*;
import com.example.mirai.libraries.backgroundable.model.LongerThanExpectedException;
import com.example.mirai.libraries.cerberus.shared.exception.*;
import com.example.mirai.libraries.core.exception.ExceptionResponse;
import com.example.mirai.libraries.gds.exception.GdsException;
import com.example.mirai.libraries.gds.exception.GdsUserNotFoundException;
import com.example.mirai.libraries.hana.shared.exception.HanaEntityNotFoundException;
import com.example.mirai.libraries.hana.shared.exception.HanaException;
import com.example.mirai.projectname.changerequestservice.shared.exception.*;
import com.example.mirai.projectname.libraries.impacteditem.shared.exception.ImpactedItemException;
/*import com.example.mirai.projectname.libraries.user.shared.exception.UserException;*/
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControllerExceptionHandler extends com.example.mirai.libraries.core.exception.ExceptionHandlerAdvice {

    @ResponseBody
    @ExceptionHandler({MandatoryFieldViolationForChangeRequestException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleMandatoryFieldViolationForChangeRequest(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ChangeRequestErrorStatusCodes.MANDATORY_VIOLATION_FOR_CHANGEREQUEST.getCode(),
                ChangeRequestErrorStatusCodes.MANDATORY_VIOLATION_FOR_CHANGEREQUEST.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({UnlinkPbsFailedException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handlePbsImportCerberusUpdateFailedException(HttpServletRequest req, UnlinkPbsFailedException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ChangeRequestErrorStatusCodes.UNLINK_PBS_FAILED.getCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({UnlinkAirFailedException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handlePbsImportChangeRequestUpdateFailedException(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ChangeRequestErrorStatusCodes.UNLINK_AIR_FAILED.getCode(),
                ChangeRequestErrorStatusCodes.UNLINK_AIR_FAILED.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({AirDefaultException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleAirDefaultException(HttpServletRequest req, AirDefaultException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({AirAuthenticationFailed.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleAirAuthenticationFailed(HttpServletRequest req, AirAuthenticationFailed e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({AirAuthorizationFailed.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleAirAuthorizationFailed(HttpServletRequest req, AirAuthorizationFailed e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({AirCommunicationError.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleAirCommunicationError(HttpServletRequest req, AirCommunicationError e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({AirNotReachable.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleAirNotReachable(HttpServletRequest req, AirNotReachable e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({AirNotWorking.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleAirNotWorking(HttpServletRequest req, AirNotWorking e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({AirTimeout.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleAirTimeout(HttpServletRequest req, AirTimeout e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }
    @ResponseBody
    @ExceptionHandler({CerberusAuthenticationFailed.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleCerberusAuthenticationFailed(HttpServletRequest req, CerberusAuthenticationFailed e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({CerberusAuthorizationFailed.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleCerberusAuthorizationFailed(HttpServletRequest req, CerberusAuthorizationFailed e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({CerberusChangeRequestAlreadyUnlinked.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleCerberusChangeRequestAlreadyUnlinked(HttpServletRequest req, CerberusChangeRequestAlreadyUnlinked e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({CerberusCommunicationError.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleCerberusCommunicationError(HttpServletRequest req, CerberusCommunicationError e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({CerberusDefaultException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleCerberusDefaultException(HttpServletRequest req, CerberusDefaultException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({CerberusNotReachable.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleCerberusNotReachable(HttpServletRequest req, CerberusNotReachable e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({CerberusNotWorking.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleCerberusNotWorking(HttpServletRequest req, CerberusNotWorking e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({CerberusTimeout.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleCerberusTimeout(HttpServletRequest req, CerberusTimeout e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({IncorrectDocumentTagsSizeException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleIncorrectDocumentTagsSizeException(HttpServletRequest req, IncorrectDocumentTagsSizeException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ChangeRequestErrorStatusCodes.INCORRECT_DOCUMENT_TAG_SIZE.getCode(),
                ChangeRequestErrorStatusCodes.INCORRECT_DOCUMENT_TAG_SIZE.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({NotAllowedToUpdateChangeOwnerException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleNotAllowedToUpdateChangeOwnerException(HttpServletRequest req, NotAllowedToUpdateChangeOwnerException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ChangeRequestErrorStatusCodes.CHANGE_OWNER_UPDATE_NOT_ALLOWED.getCode(),
                ChangeRequestErrorStatusCodes.CHANGE_OWNER_UPDATE_NOT_ALLOWED.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({NotAllowedToAddSelfAsDependentException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleNotAllowedToAddSelfAsDependentException(HttpServletRequest req, NotAllowedToAddSelfAsDependentException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ChangeRequestErrorStatusCodes.SELF_DEPENDENT_NOT_ALLOWED.getCode(),
                ChangeRequestErrorStatusCodes.SELF_DEPENDENT_NOT_ALLOWED.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({ImpactedItemException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleImpactedItemException(HttpServletRequest req, ImpactedItemException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({LongerThanExpectedException.class})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> handleLongerThanExpectedException(HttpServletRequest req, LongerThanExpectedException e) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ResponseBody
    @ExceptionHandler({HanaEntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ExceptionResponse handleEntityNotFound(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.NO_CONTENT, ChangeRequestErrorStatusCodes.HANA_ENTITY_NOT_FOUND.getCode(),
                ChangeRequestErrorStatusCodes.HANA_ENTITY_NOT_FOUND.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({HanaException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleHanaException(HttpServletRequest req, HanaException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

  /*  @ResponseBody
    @ExceptionHandler({UserException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleUserException(HttpServletRequest req, UserException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }
*/
    @ResponseBody
    @ExceptionHandler({GdsException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleGdsException(HttpServletRequest req, GdsException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }


    @ResponseBody
    @ExceptionHandler({GdsUserNotFoundException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleGdsUserNotFoundException(HttpServletRequest req, GdsException e) {
        return new ExceptionResponse(HttpStatus.NOT_FOUND, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }
}
