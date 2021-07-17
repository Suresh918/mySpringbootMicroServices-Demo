package com.example.mirai.projectname.reviewservice.reviewentry.service;

import com.example.mirai.libraries.core.exception.MandatoryFieldViolationException;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.CaseAction;
import com.example.mirai.libraries.core.model.EntityUpdate;
import com.example.mirai.libraries.entity.service.EntityStateMachineDefaultInterface;
import com.example.mirai.libraries.security.core.component.CaseActionList;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntry;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntryCaseActions;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntryStatus;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Data
public class ReviewEntryStateMachine implements EntityStateMachineDefaultInterface {

    private final CaseActionList caseActionList;

    public void checkForMandatoryFieldsAndSetStatusForCreate(BaseEntityInterface entity) {
        CaseAction caseAction = caseActionList.getCaseAction(entity.getClass(), "CREATE");
        if (!EntityStateMachineDefaultInterface.super.isCaseActionPerformable(entity, caseAction)) {
            throw new MandatoryFieldViolationException(ReviewEntry.class.getSimpleName());
        }
        entity.setStatus(ReviewEntryStatus.valueOf("OPENED").getStatusCode());
    }

    public EntityUpdate accept(BaseEntityInterface entity) throws MandatoryFieldViolationException {
        return checkPermissionAndGetEntityUpdate(entity, ReviewEntryCaseActions.ACCEPT.name(), ReviewEntryStatus.ACCEPTED.getStatusCode());
    }

    public EntityUpdate reject(BaseEntityInterface entity) throws MandatoryFieldViolationException {
        return checkPermissionAndGetEntityUpdate(entity, ReviewEntryCaseActions.REJECT.name(), ReviewEntryStatus.REJECTED.getStatusCode());
    }

    public EntityUpdate markDuplicate(BaseEntityInterface entity) throws MandatoryFieldViolationException {
        return checkPermissionAndGetEntityUpdate(entity, ReviewEntryCaseActions.MARKDUPLICATE.name(), ReviewEntryStatus.MARKEDDUPLICATE.getStatusCode());
    }

    public EntityUpdate reopen(BaseEntityInterface entity) throws MandatoryFieldViolationException {
        return checkPermissionAndGetEntityUpdate(entity, ReviewEntryCaseActions.REOPEN.name(), ReviewEntryStatus.OPENED.getStatusCode());
    }


    public EntityUpdate complete(BaseEntityInterface entity) throws MandatoryFieldViolationException {
        return checkPermissionAndGetEntityUpdate(entity, ReviewEntryCaseActions.COMPLETE.name(), ReviewEntryStatus.COMPLETED.getStatusCode());
    }

    private EntityUpdate checkPermissionAndGetEntityUpdate(BaseEntityInterface entity, String caseActionName, Integer statusCode) {
        CaseAction caseAction = caseActionList.getCaseAction(entity.getClass(), caseActionName);
        Map<String, Object> changedAttrs = new HashMap<>();
        if (!EntityStateMachineDefaultInterface.super.isCaseActionPerformable(entity, caseAction)) {
            throw new MandatoryFieldViolationException(ReviewEntry.class.getSimpleName());
        }
        ReviewEntry reviewEntry = (ReviewEntry) entity;
        reviewEntry.setStatus(statusCode);
        changedAttrs.put("status", statusCode);
        return new EntityUpdate(reviewEntry, changedAttrs);
    }
}
