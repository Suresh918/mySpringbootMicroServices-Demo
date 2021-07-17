package com.example.mirai.projectname.releasepackageservice.shared.controller;

import javax.servlet.http.HttpServletRequest;

import com.example.mirai.libraries.backgroundable.model.LongerThanExpectedException;
import com.example.mirai.libraries.cerberus.shared.exception.CerberusAuthenticationFailed;
import com.example.mirai.libraries.cerberus.shared.exception.CerberusAuthorizationFailed;
import com.example.mirai.libraries.cerberus.shared.exception.CerberusCommunicationError;
import com.example.mirai.libraries.cerberus.shared.exception.CerberusDefaultException;
import com.example.mirai.libraries.cerberus.shared.exception.CerberusNotReachable;
import com.example.mirai.libraries.cerberus.shared.exception.CerberusNotWorking;
import com.example.mirai.libraries.cerberus.shared.exception.CerberusTimeout;
import com.example.mirai.libraries.core.exception.ExceptionResponse;
import com.example.mirai.libraries.gds.exception.GdsException;
import com.example.mirai.libraries.gds.exception.GdsUserNotFoundException;
import com.example.mirai.libraries.hana.shared.exception.HanaEntityNotFoundException;
import com.example.mirai.libraries.hana.shared.exception.HanaException;
import com.example.mirai.libraries.sapmdg.shared.exception.SapMdgCommunicationErrorException;
import com.example.mirai.libraries.sapmdg.shared.exception.SapMdgCrAlreadyExistsException;
import com.example.mirai.libraries.sapmdg.shared.exception.SapMdgDefaultException;
import com.example.mirai.libraries.sapmdg.shared.exception.SapMdgInvalidReleasePackageNumberException;
import com.example.mirai.libraries.sapmdg.shared.exception.SapMdgUnableToObsoleteException;
import com.example.mirai.libraries.sapmdg.shared.exception.SapMdgUnableToReleaseForActivationException;
import com.example.mirai.libraries.teamcenter.shared.exception.TeamcenterAuthenticationFailed;
import com.example.mirai.libraries.teamcenter.shared.exception.TeamcenterAuthorLicense;
import com.example.mirai.libraries.teamcenter.shared.exception.TeamcenterDefaultException;
import com.example.mirai.libraries.teamcenter.shared.exception.TeamcenterDeltaReportNotFound;
import com.example.mirai.libraries.teamcenter.shared.exception.TeamcenterDeltaReportReferenceNotFound;
import com.example.mirai.libraries.teamcenter.shared.exception.TeamcenterECNAlreadyExists;
import com.example.mirai.libraries.teamcenter.shared.exception.TeamcenterECNNotFound;
import com.example.mirai.libraries.teamcenter.shared.exception.TeamcenterLoginException;
import com.example.mirai.libraries.teamcenter.shared.exception.TeamcenterNotReachable;
import com.example.mirai.libraries.teamcenter.shared.exception.TeamcenterNotUpdated;
import com.example.mirai.libraries.teamcenter.shared.exception.TeamcenterNotWorking;
import com.example.mirai.libraries.teamcenter.shared.exception.TeamcenterSessionInvalidated;
import com.example.mirai.libraries.teamcenter.shared.exception.TeamcenterSolutionItemsNotFoundException;
import com.example.mirai.libraries.teamcenter.shared.exception.TeamcenterTimeout;
import com.example.mirai.projectname.libraries.impacteditem.shared.exception.ImpactedItemException;
import com.example.mirai.projectname.libraries.impacteditem.shared.exception.SdlNotAuthenticatedException;
import com.example.mirai.projectname.libraries.impacteditem.shared.exception.SdlNotWorkingException;
import com.example.mirai.projectname.libraries.impacteditem.shared.exception.SdlTimeoutException;
import com.example.mirai.projectname.libraries.impacteditem.shared.exception.SdlUnreachableException;
import com.example.mirai.projectname.libraries.user.shared.exception.UserException;
import com.example.mirai.projectname.releasepackageservice.shared.exception.ChangeNoticeStatusInvalidForReadyException;
import com.example.mirai.projectname.releasepackageservice.shared.exception.ChangeObjectPublicationPendingException;
import com.example.mirai.projectname.releasepackageservice.shared.exception.DeleteMaterialFailedException;
import com.example.mirai.projectname.releasepackageservice.shared.exception.MandatoryFieldViolationForReleasePackageException;
import com.example.mirai.projectname.releasepackageservice.shared.exception.ReleasePackageErrorStatusCodes;
import com.example.mirai.projectname.releasepackageservice.shared.exception.SapMdgAdditionalMaterialException;
import com.example.mirai.projectname.releasepackageservice.shared.exception.SdlPublishCaseActionFailedException;
import com.example.mirai.projectname.releasepackageservice.shared.exception.SdlReleaseCaseActionFailedException;
import com.example.mirai.projectname.releasepackageservice.shared.exception.SdlStartCaseActionFailedException;
import com.example.mirai.projectname.releasepackageservice.shared.exception.TeamcenterAdditionalSolutionItemException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerExceptionHandler extends com.example.mirai.libraries.core.exception.ExceptionHandlerAdvice {

    @ResponseBody
    @ExceptionHandler({MandatoryFieldViolationForReleasePackageException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleMandatoryFieldViolationForReleasePackage(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReleasePackageErrorStatusCodes.MANDATORY_VIOLATION_FOR_RELEASE_PACKAGE.getCode(),
                ReleasePackageErrorStatusCodes.MANDATORY_VIOLATION_FOR_RELEASE_PACKAGE.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({CerberusAuthenticationFailed.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionResponse handleCerberusAuthenticationFailed(HttpServletRequest req, CerberusAuthenticationFailed e) {
        return new ExceptionResponse(HttpStatus.FORBIDDEN, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({CerberusAuthorizationFailed.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionResponse handleCerberusAuthorizationFailed(HttpServletRequest req, CerberusAuthorizationFailed e) {
        return new ExceptionResponse(HttpStatus.FORBIDDEN, e.getApplicationStatusCode(),
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
    @ExceptionHandler({TeamcenterAuthenticationFailed.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleTeamcenterAuthenticationFailedException(HttpServletRequest req, TeamcenterAuthenticationFailed e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({TeamcenterNotReachable.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleTeamcenterNotReachable(HttpServletRequest req, TeamcenterNotReachable e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({TeamcenterTimeout.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleTeamcenterTimeoutException(HttpServletRequest req, TeamcenterTimeout e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({TeamcenterSolutionItemsNotFoundException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleTeamcenterSolutionItemsNotFoundException(HttpServletRequest req, TeamcenterSolutionItemsNotFoundException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({TeamcenterLoginException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleTeamcenterLoginException(HttpServletRequest req, TeamcenterLoginException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({TeamcenterSessionInvalidated.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleTeamcenterSessionInvalidatedException(HttpServletRequest req, TeamcenterSessionInvalidated e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({TeamcenterDeltaReportNotFound.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleTeamcenterDeltaReportNotFoundException(HttpServletRequest req, TeamcenterDeltaReportNotFound e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({TeamcenterDeltaReportReferenceNotFound.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleTeamcenterDeltaReportReferenceNotFoundException(HttpServletRequest req, TeamcenterDeltaReportReferenceNotFound e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({TeamcenterECNAlreadyExists.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleTeamcenterECNAlreadyExistsException(HttpServletRequest req, TeamcenterECNAlreadyExists e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({TeamcenterNotUpdated.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleTeamcenterNotUpdatedException(HttpServletRequest req, TeamcenterNotUpdated e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({TeamcenterAuthorLicense.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleTeamcenterAuthorLicenseException(HttpServletRequest req, TeamcenterAuthorLicense e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({TeamcenterECNNotFound.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleTeamcenterECNNotFound(HttpServletRequest req, TeamcenterECNNotFound e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({TeamcenterNotWorking.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleTeamcenterNotWorking(HttpServletRequest req, TeamcenterNotWorking e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({TeamcenterDefaultException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleTeamcenterDefaultException(HttpServletRequest req, TeamcenterDefaultException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }


    @ResponseBody
    @ExceptionHandler({SapMdgDefaultException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleSapMdgDefaultException(HttpServletRequest req, SapMdgDefaultException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({SapMdgCrAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleSapMdgCrAlreadyExistsException(HttpServletRequest req, SapMdgCrAlreadyExistsException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({TeamcenterAdditionalSolutionItemException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleSapMdgMaterialMissing(HttpServletRequest req, TeamcenterAdditionalSolutionItemException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReleasePackageErrorStatusCodes.ADDITIONAL_SOLUTION_ITEM_IN_TEAMCENTER.getCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({SapMdgAdditionalMaterialException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleTeamCenterSolutionItemMissing(HttpServletRequest req, SapMdgAdditionalMaterialException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReleasePackageErrorStatusCodes.ADDITIONAL_MATERIAL_IN_SAP_MDG.getCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({DeleteMaterialFailedException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleDeleteMaterialFailed(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReleasePackageErrorStatusCodes.MANDATORY_VIOLATION_FOR_RELEASE_PACKAGE.getCode(),
                ReleasePackageErrorStatusCodes.MANDATORY_VIOLATION_FOR_RELEASE_PACKAGE.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({SdlStartCaseActionFailedException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleSdlCaseActionFailed(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReleasePackageErrorStatusCodes.SDL_START_CASE_ACTION_FAILED.getCode(),
                ReleasePackageErrorStatusCodes.SDL_START_CASE_ACTION_FAILED.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({SdlReleaseCaseActionFailedException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleSdlReleaseCaseActionFailedException(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReleasePackageErrorStatusCodes.SDL_RELEASE_CASE_ACTION_FAILED.getCode(),
                ReleasePackageErrorStatusCodes.SDL_RELEASE_CASE_ACTION_FAILED.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({SdlPublishCaseActionFailedException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleSdlPublishCaseActionFailedException(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReleasePackageErrorStatusCodes.SDL_PUBLISH_CASE_ACTION_FAILED.getCode(),
                ReleasePackageErrorStatusCodes.SDL_PUBLISH_CASE_ACTION_FAILED.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({SapMdgUnableToObsoleteException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleSapMdgUnableToObsoleteReleasePackage(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReleasePackageErrorStatusCodes.UNABLE_TO_OBSOLETE_RP.getCode(),
                ReleasePackageErrorStatusCodes.UNABLE_TO_OBSOLETE_RP.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({SapMdgUnableToReleaseForActivationException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleSapMdgUnableToReleaseForActivation(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReleasePackageErrorStatusCodes.UNABLE_TO_RELEASE_FOR_ACTIVATION_RP.getCode(),
                ReleasePackageErrorStatusCodes.UNABLE_TO_RELEASE_FOR_ACTIVATION_RP.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({SapMdgInvalidReleasePackageNumberException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleSapMdgInvalidReleasePackageNumber(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReleasePackageErrorStatusCodes.INVALID_RELEASE_PACKAGE_NUMBER.getCode(),
                ReleasePackageErrorStatusCodes.INVALID_RELEASE_PACKAGE_NUMBER.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({SapMdgCommunicationErrorException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleSapMdgCommunicationError(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReleasePackageErrorStatusCodes.COMMUNICATION_ERROR.getCode(),
                ReleasePackageErrorStatusCodes.COMMUNICATION_ERROR.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({ChangeObjectPublicationPendingException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleChangeObjectPublicationPendingException(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReleasePackageErrorStatusCodes.CHANGE_OBJECT_PUBLICATION_PENDING.getCode(),
                ReleasePackageErrorStatusCodes.IMPACTED_ITEM_ERROR.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    //TODO: Handle All impacted item exceptions
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
    @ExceptionHandler({SdlNotAuthenticatedException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleSdlNotAuthenticatedException(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReleasePackageErrorStatusCodes.SDL_NOT_AUTHENTICATED.getCode(),
                ReleasePackageErrorStatusCodes.SDL_NOT_AUTHENTICATED.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({SdlNotWorkingException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleSdlNotWorkingException(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReleasePackageErrorStatusCodes.SDL_NOT_WORKING.getCode(),
                ReleasePackageErrorStatusCodes.SDL_NOT_WORKING.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({SdlUnreachableException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleSdlUnreachableException(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReleasePackageErrorStatusCodes.SDL_UNREACHABLE.getCode(),
                ReleasePackageErrorStatusCodes.SDL_UNREACHABLE.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({SdlTimeoutException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleSdlTimeoutException(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReleasePackageErrorStatusCodes.SDL_TIMEOUT.getCode(),
                ReleasePackageErrorStatusCodes.SDL_TIMEOUT.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({HanaEntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ExceptionResponse handleEntityNotFound(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.NO_CONTENT, ReleasePackageErrorStatusCodes.HANA_ENTITY_NOT_FOUND.getCode(),
                ReleasePackageErrorStatusCodes.HANA_ENTITY_NOT_FOUND.getMessage(),
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

    @ResponseBody
    @ExceptionHandler({LongerThanExpectedException.class})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> handleLongerThanExpectedException(HttpServletRequest req, LongerThanExpectedException e) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @ResponseBody
    @ExceptionHandler({UserException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleUserException(HttpServletRequest req, UserException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

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

    @ResponseBody
    @ExceptionHandler({ChangeNoticeStatusInvalidForReadyException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleChangeNoticeStatusInvalidForReadyException(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReleasePackageErrorStatusCodes.CHANGE_NOTICE_STATUS_INVALID_FOR_READY.getCode(),
                ReleasePackageErrorStatusCodes.CHANGE_NOTICE_STATUS_INVALID_FOR_READY.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }
}
