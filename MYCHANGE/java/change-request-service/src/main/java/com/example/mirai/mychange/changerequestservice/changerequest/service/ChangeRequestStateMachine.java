package com.example.mirai.projectname.changerequestservice.changerequest.service;

import com.example.mirai.libraries.core.exception.MandatoryFieldViolationException;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.CaseAction;
import com.example.mirai.libraries.core.model.EntityUpdate;
import com.example.mirai.libraries.entity.service.EntityStateMachineDefaultInterface;
import com.example.mirai.libraries.security.core.component.CaseActionList;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestCaseActions;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestContext;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestStatus;
import com.example.mirai.projectname.changerequestservice.shared.util.Constants;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChangeRequestStateMachine implements EntityStateMachineDefaultInterface {

    private CaseActionList caseActionList;
    @Value("${skip-scia-obsolescence-on-obsolete:true}")
    private boolean skipSciObsolescenceOnObsolete;

    public ChangeRequestStateMachine(CaseActionList caseActionList) {
        this.caseActionList = caseActionList;
    }

    public void checkForMandatoryFieldsAndSetStatusForCreate(BaseEntityInterface entity) {
        CaseAction caseAction = caseActionList.getCaseAction(entity.getClass(), ChangeRequestCaseActions.CREATE.name());
        if (!EntityStateMachineDefaultInterface.super.isCaseActionPerformable(entity, caseAction)) {
            throw new MandatoryFieldViolationException(ChangeRequest.class.getSimpleName());
        }
        entity.setStatus(ChangeRequestStatus.DRAFTED.getStatusCode());
    }

    public EntityUpdate submit(BaseEntityInterface entity) throws MandatoryFieldViolationException {
        return checkForMandatoryFieldsAndGetEntityUpdate(entity, ChangeRequestCaseActions.SUBMIT.name(), ChangeRequestStatus.NEW);
    }

    public EntityUpdate redraft(BaseEntityInterface entity) throws MandatoryFieldViolationException {
        return checkForMandatoryFieldsAndGetEntityUpdate(entity, ChangeRequestCaseActions.REDRAFT.name(), ChangeRequestStatus.DRAFTED);
    }

    public EntityUpdate defineSolution(BaseEntityInterface entity) throws MandatoryFieldViolationException {
        return checkForMandatoryFieldsAndGetEntityUpdate(entity, ChangeRequestCaseActions.DEFINE_SOLUTION.name(), ChangeRequestStatus.SOLUTION_DEFINED);
    }

    public EntityUpdate analyzeImpact(BaseEntityInterface entity) throws MandatoryFieldViolationException {
        return checkForMandatoryFieldsAndGetEntityUpdate(entity, ChangeRequestCaseActions.ANALYZE_IMPACT.name(), ChangeRequestStatus.IMPACT_ANALYZED);
    }

    public EntityUpdate approve(BaseEntityInterface entity) throws MandatoryFieldViolationException {
        return checkForMandatoryFieldsAndGetEntityUpdate(entity, ChangeRequestCaseActions.APPROVE.name(), ChangeRequestStatus.APPROVED);
    }

    public EntityUpdate close(BaseEntityInterface entity) throws MandatoryFieldViolationException {
        return checkForMandatoryFieldsAndGetEntityUpdate(entity, ChangeRequestCaseActions.CLOSE.name(), ChangeRequestStatus.CLOSED);
    }

    public EntityUpdate reject(BaseEntityInterface entity) throws MandatoryFieldViolationException {
        return checkForMandatoryFieldsAndGetEntityUpdate(entity, ChangeRequestCaseActions.REJECT.name(), ChangeRequestStatus.REJECTED);
    }

    public EntityUpdate obsolete(BaseEntityInterface entity) throws MandatoryFieldViolationException {
        EntityUpdate entityUpdate = checkForMandatoryFieldsAndGetEntityUpdate(entity, ChangeRequestCaseActions.OBSOLETE.name(), ChangeRequestStatus.OBSOLETED);
        if(!skipSciObsolescenceOnObsolete) {
            ChangeRequest changeRequest = (ChangeRequest) entityUpdate.getEntity();
            List<ChangeRequestContext> updatedContext = new ArrayList();
            changeRequest.getContexts().stream()
                    .forEach(context -> {
                        ChangeRequestContext sciaContext = new ChangeRequestContext();
                        sciaContext.setContextId(context.getContextId());
                        sciaContext.setName(context.getName());
                        if (Objects.equals(context.getType(), "SCIA"))
                            sciaContext.setStatus(Constants.SCIA_STATUS_OBSOLETE);
                        else
                            sciaContext.setStatus(context.getStatus());
                        sciaContext.setType(context.getType());
                        updatedContext.add(sciaContext);
                    });
            changeRequest.setContexts(updatedContext);
            entityUpdate.addToChangedAttrs("contexts", updatedContext);
        }
        return entityUpdate;
    }

    public EntityUpdate checkForMandatoryFieldsAndGetEntityUpdate(BaseEntityInterface entity, String caseActionName, ChangeRequestStatus newStatus) {
        Map<String, Object> changedAttrs = new HashMap<>();
        List<CaseAction> caseActions = caseActionList.getCaseActions(entity.getClass(), caseActionName);
        if (!EntityStateMachineDefaultInterface.super.isCaseActionPerformable(entity, caseActions)) {
            throw new MandatoryFieldViolationException(ChangeRequest.class.getSimpleName());
        }
        ChangeRequest changeRequest = (ChangeRequest) entity;
        changeRequest.setStatus(newStatus.getStatusCode());
        changedAttrs.put("status", newStatus.getStatusCode());
        return new EntityUpdate(changeRequest, changedAttrs);
    }
}
