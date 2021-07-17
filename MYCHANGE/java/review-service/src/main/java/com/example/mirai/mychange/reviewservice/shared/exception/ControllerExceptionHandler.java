package com.example.mirai.projectname.reviewservice.shared.exception;

import com.example.mirai.libraries.core.exception.ExceptionResponse;
import com.example.mirai.libraries.sapmdg.shared.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControllerExceptionHandler extends com.example.mirai.libraries.core.exception.ExceptionHandlerAdvice {

    @ResponseBody
    @ExceptionHandler({ReviewTaskExistException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleReviewTasksExist(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReviewErrorStatusCodes.REVIEW_TASK_EXIST.getCode(),
                ReviewErrorStatusCodes.REVIEW_TASK_EXIST.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({ReviewTaskNotExistException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleReviewTaskNotExist(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReviewErrorStatusCodes.NO_LINKED_REVIEW_TASK.getCode(),
                ReviewErrorStatusCodes.NO_LINKED_REVIEW_TASK.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({MandatoryFieldViolationForReviewTasksException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleMandatoryFieldViolationForReviewTasks(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReviewErrorStatusCodes.MANDATORY_VIOLATION_FOR_REVIEW_TASK.getCode(),
                ReviewErrorStatusCodes.MANDATORY_VIOLATION_FOR_REVIEW_TASK.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({ReviewCompletionException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleReviewCompletionException(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReviewErrorStatusCodes.DEFECTS_NOT_PROCESSED_TO_COMPLETE_REVIEW.getCode(),
                ReviewErrorStatusCodes.DEFECTS_NOT_PROCESSED_TO_COMPLETE_REVIEW.getMessage(),
                -1,
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({ReviewEntryDeleteNotPossibleException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleReviewEntryDeleteNotPossibleException(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReviewErrorStatusCodes.COMMENTS_EXIST_TO_DELETE_REVIEW_ENTRY.getCode(),
                ReviewErrorStatusCodes.COMMENTS_EXIST_TO_DELETE_REVIEW_ENTRY.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({IncompleteReviewExistForReleasePackage.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleIncompleteReviewExistForReleasePackage(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReviewErrorStatusCodes.INCOMPLETE_REVIEW_EXIST_FOR_RELEASE_PACKAGE.getCode(),
                ReviewErrorStatusCodes.INCOMPLETE_REVIEW_EXIST_FOR_RELEASE_PACKAGE.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({MdgCrContextNotExistException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleReviewContexSapMdgCrDoesNotExist(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReviewErrorStatusCodes.MDG_CR_NOT_EXISTS.getCode(),
                ReviewErrorStatusCodes.MDG_CR_NOT_EXISTS.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({ZecnReviewException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleZECNReview(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReviewErrorStatusCodes.ZCN_REVIEW_FOUND.getCode(),
                ReviewErrorStatusCodes.ZCN_REVIEW_FOUND.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({DeleteMaterialFailedException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleDeleteMaterialFailed(HttpServletRequest req) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReviewErrorStatusCodes.DELETE_MATERIAL_FAILED.getCode(),
                ReviewErrorStatusCodes.DELETE_MATERIAL_FAILED.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({SapMdgRPNotLinkedToMdgCrException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleSapMdgRPNotLinkedToMdgCrException(HttpServletRequest req, SapMdgRPNotLinkedToMdgCrException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({SapMdgMaterialsNotExistsException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleSapMdgMaterialsNotExistsException(HttpServletRequest req, SapMdgMaterialsNotExistsException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({SapMdgInvalidReleasePackageException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleSapMdgInvalidReleasePackageException(HttpServletRequest req, SapMdgInvalidReleasePackageException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({SapMdgMaterialListEmptyException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleSapMdgMaterialListEmptyException(HttpServletRequest req, SapMdgMaterialListEmptyException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({SapMdgDeleteMaterialException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleSapMdgDeleteMaterialException(HttpServletRequest req, SapMdgDeleteMaterialException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getApplicationStatusCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({TeamcenterAdditionalSolutionItemException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleSapMdgMaterialMissing(HttpServletRequest req, TeamcenterAdditionalSolutionItemException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReviewErrorStatusCodes.ADDITIONAL_SOLUTION_ITEM_IN_TEAMCENTER.getCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({SapMdgAdditionalMaterialException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleTeamCenterSolutionItemMissing(HttpServletRequest req, SapMdgAdditionalMaterialException e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ReviewErrorStatusCodes.ADDITIONAL_MATERIAL_IN_SAP_MDG.getCode(),
                e.getMessage(),
                ExceptionResponse.Severities.LOW.getCode(),
                req.getRequestURI());
    }
}
