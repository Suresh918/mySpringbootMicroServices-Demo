package com.example.mirai.projectname.reviewservice.reviewtask.service;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.exception.MandatoryFieldViolationException;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.CaseAction;
import com.example.mirai.libraries.core.model.EntityUpdate;
import com.example.mirai.libraries.entity.service.EntityStateMachineDefaultInterface;
import com.example.mirai.libraries.security.core.component.CaseActionList;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntry;
import com.example.mirai.projectname.reviewservice.reviewentry.service.ReviewEntryService;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTaskCaseActions;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTaskStatus;
import com.example.mirai.projectname.reviewservice.shared.utils.Constants;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Data
public class ReviewTaskStateMachine implements EntityStateMachineDefaultInterface {

    private final CaseActionList caseActionList;


    public void checkForMandatoryFieldsAndSetStatus(BaseEntityInterface entity) {
        CaseAction caseAction = caseActionList.getCaseAction(entity.getClass(), "CREATE");
        if (!EntityStateMachineDefaultInterface.super.isCaseActionPerformable(entity, caseAction)) {
            throw new MandatoryFieldViolationException(ReviewTask.class.getSimpleName());
        }
        entity.setStatus(ReviewTaskStatus.valueOf("OPENED").getStatusCode());
    }

    public EntityUpdate accept(BaseEntityInterface entity) {
        return checkPermissionAndGetEntityUpdate(entity, ReviewTaskCaseActions.ACCEPT.name(), ReviewTaskStatus.ACCEPTED.getStatusCode());
    }

    public EntityUpdate reject(BaseEntityInterface entity) {
        return checkPermissionAndGetEntityUpdate(entity, ReviewTaskCaseActions.REJECT.name(), ReviewTaskStatus.REJECTED.getStatusCode());
    }

    public EntityUpdate complete(BaseEntityInterface entity) {
        return checkPermissionAndGetEntityUpdate(entity, ReviewTaskCaseActions.COMPLETE.name(), ReviewTaskStatus.COMPLETED.getStatusCode());
    }

    public EntityUpdate lock(BaseEntityInterface entity) {
        Map<String, Object> changedAttrs = new HashMap<>();
        CaseAction caseAction = caseActionList.getCaseAction(entity.getClass(), "LOCK");
        if (!EntityStateMachineDefaultInterface.super.isCaseActionPerformable(entity, caseAction)) {
            throw new MandatoryFieldViolationException(ReviewTask.class.getSimpleName());
        }
        ReviewTask reviewTask = (ReviewTask) entity;
        if (reviewTask.getStatus().equals(ReviewTaskStatus.OPENED.getStatusCode()) || reviewTask.getStatus().equals(ReviewTaskStatus.ACCEPTED.getStatusCode())) {
            reviewTask.setStatus(ReviewTaskStatus.NOTFINALIZED.getStatusCode());
            changedAttrs.put(Constants.STATUS, ReviewTaskStatus.NOTFINALIZED.getStatusCode());
        } else if (reviewTask.getStatus().equals(ReviewTaskStatus.REJECTED.getStatusCode()) || reviewTask.getStatus().equals(ReviewTaskStatus.COMPLETED.getStatusCode())) {
            reviewTask.setStatus(ReviewTaskStatus.FINALIZED.getStatusCode());
            changedAttrs.put(Constants.STATUS, ReviewTaskStatus.FINALIZED.getStatusCode());
        }
        return new EntityUpdate(reviewTask, changedAttrs);
    }

    public EntityUpdate unlock(BaseEntityInterface entity) {
        Map<String, Object> changedAttrs = new HashMap<>();
        CaseAction caseAction = caseActionList.getCaseAction(entity.getClass(), "UNLOCK");
        if (!EntityStateMachineDefaultInterface.super.isCaseActionPerformable(entity, caseAction)) {
            throw new MandatoryFieldViolationException(ReviewTask.class.getSimpleName());
        }
        ReviewTask reviewTask = (ReviewTask) entity;
        ReviewEntryService reviewEntryService = (ReviewEntryService) ApplicationContextHolder.getService(ReviewEntryService.class);
        List<ReviewEntry> reviewEntries = reviewEntryService.findReviewEntriesByReviewTask(reviewTask, PageRequest.of(0, 1));
        if (reviewEntries.size() > 0) {
            reviewTask.setStatus(ReviewTaskStatus.ACCEPTED.getStatusCode());
            changedAttrs.put(Constants.STATUS, ReviewTaskStatus.ACCEPTED.getStatusCode());
        } else {
            reviewTask.setStatus(ReviewTaskStatus.OPENED.getStatusCode());
            changedAttrs.put(Constants.STATUS, ReviewTaskStatus.OPENED.getStatusCode());
        }
        return new EntityUpdate(reviewTask, changedAttrs);
    }

    public EntityUpdate reopen(BaseEntityInterface entity) {
        Map<String, Object> changedAttrs = new HashMap<>();
        CaseAction caseAction = caseActionList.getCaseAction(entity.getClass(), "REOPEN");
        if (!EntityStateMachineDefaultInterface.super.isCaseActionPerformable(entity, caseAction)) {
            throw new MandatoryFieldViolationException(ReviewTask.class.getSimpleName());
        }
        ReviewTask reviewTask = (ReviewTask) entity;
        ReviewEntryService reviewEntryService = (ReviewEntryService) ApplicationContextHolder.getService(ReviewEntryService.class);
        List<ReviewEntry> reviewEntries = reviewEntryService.findReviewEntriesByReviewTask(reviewTask, PageRequest.of(0, 1));
        if (reviewEntries.size() > 0) {
            reviewTask.setStatus(ReviewTaskStatus.ACCEPTED.getStatusCode());
            changedAttrs.put(Constants.STATUS, ReviewTaskStatus.ACCEPTED.getStatusCode());
        } else {
            reviewTask.setStatus(ReviewTaskStatus.OPENED.getStatusCode());
            changedAttrs.put(Constants.STATUS, ReviewTaskStatus.OPENED.getStatusCode());
        }
        return new EntityUpdate(reviewTask, changedAttrs);
    }

    public EntityUpdate delete(BaseEntityInterface entity) {
        Map<String, Object> changedAttrs = new HashMap<>();
        CaseAction caseAction = caseActionList.getCaseAction(entity.getClass(), "DELETE");
        if (!EntityStateMachineDefaultInterface.super.isCaseActionPerformable(entity, caseAction)) {
            throw new MandatoryFieldViolationException(ReviewTask.class.getSimpleName());
        }
        ReviewTask reviewTask = (ReviewTask) entity;
        //TODO: Update status based on the requirement(if needed)

        return new EntityUpdate(reviewTask, changedAttrs);
    }

    private EntityUpdate checkPermissionAndGetEntityUpdate(BaseEntityInterface entity, String caseActionName, Integer statusCode) {
        Map<String, Object> changedAttrs = new HashMap<String, Object>();
        CaseAction caseAction = caseActionList.getCaseAction(entity.getClass(), caseActionName);
        if (!EntityStateMachineDefaultInterface.super.isCaseActionPerformable(entity, caseAction)) {
            throw new MandatoryFieldViolationException(ReviewTask.class.getSimpleName());
        }
        ReviewTask reviewTask = (ReviewTask) entity;
        reviewTask.setStatus(statusCode);
        changedAttrs.put(Constants.STATUS, statusCode);
        return new EntityUpdate(reviewTask, changedAttrs);
    }
}
