package com.example.mirai.projectname.releasepackageservice.releasepackage.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.mirai.libraries.core.exception.MandatoryFieldViolationException;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.CaseAction;
import com.example.mirai.libraries.core.model.EntityUpdate;
import com.example.mirai.libraries.entity.service.EntityStateMachineDefaultInterface;
import com.example.mirai.libraries.security.core.component.CaseActionList;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageStatus;
import lombok.Data;

import org.springframework.stereotype.Service;

@Service
@Data
public class ReleasePackageStateMachine implements EntityStateMachineDefaultInterface {
    private final CaseActionList caseActionList;

    public EntityUpdate create(BaseEntityInterface entity) throws MandatoryFieldViolationException {
        return checkForMandatoryFieldsAndGetEntityUpdate(entity, "CREATE", ReleasePackageStatus.valueOf("CREATED").getStatusCode());
    }

    public EntityUpdate recreate(BaseEntityInterface entity) throws MandatoryFieldViolationException {
        return checkForMandatoryFieldsAndGetEntityUpdate(entity, "RECREATE", ReleasePackageStatus.valueOf("CREATED").getStatusCode());
    }

    public EntityUpdate ready(BaseEntityInterface entity) throws MandatoryFieldViolationException {
        return checkForMandatoryFieldsAndGetEntityUpdate(entity, "READY", ReleasePackageStatus.valueOf("READY_FOR_RELEASE").getStatusCode());
    }

    public EntityUpdate reready(BaseEntityInterface entity) throws MandatoryFieldViolationException {
        return checkForMandatoryFieldsAndGetEntityUpdate(entity, "REREADY", ReleasePackageStatus.valueOf("READY_FOR_RELEASE").getStatusCode());
    }

    public EntityUpdate release(BaseEntityInterface entity) throws MandatoryFieldViolationException {
        return checkForMandatoryFieldsAndGetEntityUpdate(entity, "RELEASE", ReleasePackageStatus.valueOf("RELEASED").getStatusCode());
    }

    public EntityUpdate close(BaseEntityInterface entity) throws MandatoryFieldViolationException {
        return checkForMandatoryFieldsAndGetEntityUpdate(entity, "CLOSE", ReleasePackageStatus.valueOf("CLOSED").getStatusCode());
    }

    public EntityUpdate obsolete(BaseEntityInterface entity) throws MandatoryFieldViolationException {
        return checkForMandatoryFieldsAndGetEntityUpdate(entity, "OBSOLETE", ReleasePackageStatus.valueOf("OBSOLETED").getStatusCode());
    }

    public EntityUpdate checkForMandatoryFieldsAndGetEntityUpdate(BaseEntityInterface entity, String caseActionName, Integer statusCode) {

        Map<String, Object> changedAttrs = new HashMap<>();
        ReleasePackage releasePackage = (ReleasePackage) entity;
        releasePackage.setStatus(statusCode);
        changedAttrs.put("status", statusCode);
        return new EntityUpdate(releasePackage, changedAttrs);
    }

    public void checkForMandatoryFields(BaseEntityInterface entity, String caseActionName) {
        List<CaseAction> caseActions = caseActionList.getCaseActions(entity.getClass(), caseActionName);
        if (!EntityStateMachineDefaultInterface.super.isCaseActionPerformable(entity, caseActions)) {
            throw new MandatoryFieldViolationException(ReleasePackage.class.getSimpleName());
        }
    }
}
