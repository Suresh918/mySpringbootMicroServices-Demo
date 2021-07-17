package com.example.mirai.projectname.reviewservice.review.service;

import com.example.mirai.libraries.core.exception.MandatoryFieldViolationException;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.CaseAction;
import com.example.mirai.libraries.core.model.EntityUpdate;
import com.example.mirai.libraries.entity.service.EntityStateMachineDefaultInterface;
import com.example.mirai.libraries.security.core.component.CaseActionList;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.review.model.ReviewCaseActions;
import com.example.mirai.projectname.reviewservice.review.model.ReviewStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class ReviewStateMachine implements EntityStateMachineDefaultInterface {

    private CaseActionList caseActionList;


    public void checkForMandatoryFieldsAndSetStatusForCreate(BaseEntityInterface entity) {
        CaseAction caseAction = caseActionList.getCaseAction(entity.getClass(), "CREATE");
        if (!EntityStateMachineDefaultInterface.super.isCaseActionPerformable(entity, caseAction)) {
            throw new MandatoryFieldViolationException(Review.class.getSimpleName());
        }
        entity.setStatus(ReviewStatus.valueOf("OPENED").getStatusCode());
    }

    public EntityUpdate lock(BaseEntityInterface entity) throws MandatoryFieldViolationException {
        return checkPermissionAndGetEntityUpdate(entity, ReviewCaseActions.LOCK.name(), ReviewStatus.LOCKED.getStatusCode());
    }

    public EntityUpdate startValidation(BaseEntityInterface entity) throws MandatoryFieldViolationException {
        return checkPermissionAndGetEntityUpdate(entity, ReviewCaseActions.STARTVALIDATION.name(), ReviewStatus.VALIDATIONSTARTED.getStatusCode());
    }

    public EntityUpdate reopen(BaseEntityInterface entity) throws MandatoryFieldViolationException {
        return checkPermissionAndGetEntityUpdate(entity, ReviewCaseActions.REOPEN.name(), ReviewStatus.OPENED.getStatusCode());
    }

    public EntityUpdate complete(BaseEntityInterface entity) throws MandatoryFieldViolationException {
        return checkPermissionAndGetEntityUpdate(entity, ReviewCaseActions.COMPLETE.name(), ReviewStatus.COMPLETED.getStatusCode());
    }


    private EntityUpdate checkPermissionAndGetEntityUpdate(BaseEntityInterface entity, String caseActionName, Integer statusCode) {
        Map<String, Object> changedAttrs = new HashMap<>();
        CaseAction caseAction = caseActionList.getCaseAction(entity.getClass(), caseActionName);
        if (!EntityStateMachineDefaultInterface.super.isCaseActionPerformable(entity, caseAction)) {
            throw new MandatoryFieldViolationException(Review.class.getSimpleName());
        }
        Review review = (Review) entity;
        review.setStatus(statusCode);
        changedAttrs.put("status", statusCode);
        return new EntityUpdate(review, changedAttrs);
    }
}
