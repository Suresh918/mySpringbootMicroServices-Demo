package com.example.mirai.projectname.changerequestservice.completebusinesscase.service;

import com.example.mirai.libraries.audit.service.AuditServiceDefaultInterface;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.annotation.SecureCaseAction;
import com.example.mirai.libraries.core.annotation.SecurePropertyMerge;
import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.CaseStatus;
import com.example.mirai.libraries.entity.model.Id;
import com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface;
import com.example.mirai.libraries.event.annotation.PublishResponse;
import com.example.mirai.libraries.security.abac.AbacAwareInterface;
import com.example.mirai.libraries.security.abac.component.AbacProcessor;
import com.example.mirai.libraries.security.core.component.CaseActionList;
import com.example.mirai.libraries.security.core.component.EntityACL;
import com.example.mirai.libraries.security.core.component.PropertyACL;
import com.example.mirai.libraries.security.core.service.SecurityServiceDefaultInterface;
import com.example.mirai.libraries.security.rbac.RbacAwareInterface;
import com.example.mirai.libraries.security.rbac.component.RbacProcessor;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestAggregate;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestStateMachine;
import com.example.mirai.projectname.changerequestservice.completebusinesscase.model.CompleteBusinessCase;
import com.example.mirai.projectname.changerequestservice.impactanalysis.service.ImpactAnalysisService;
import com.example.mirai.projectname.changerequestservice.shared.service.AggregateEventBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Service
@EntityClass(CompleteBusinessCase.class)
public class CompleteBusinessCaseService implements EntityServiceDefaultInterface, SecurityServiceDefaultInterface, AuditServiceDefaultInterface {
    private final ChangeRequestStateMachine stateMachine;
    private final AbacProcessor abacProcessor;
    private final RbacProcessor rbacProcessor;
    private final EntityACL acl;
    private final PropertyACL pacl;
    private final CaseActionList caseActionList;
    private final ImpactAnalysisService impactAnalysisService;
    @Resource
    CompleteBusinessCaseService self;

    public CompleteBusinessCaseService(ChangeRequestStateMachine stateMachine, AbacProcessor abacProcessor,
                                       RbacProcessor rbacProcessor, EntityACL acl, PropertyACL pacl, CaseActionList caseActionList,
                                       ImpactAnalysisService impactAnalysisService) {
        this.stateMachine = stateMachine;
        this.abacProcessor = abacProcessor;
        this.rbacProcessor = rbacProcessor;
        this.acl = acl;
        this.pacl = pacl;
        this.caseActionList = caseActionList;
        this.impactAnalysisService = impactAnalysisService;
    }

    @Override
    public EntityACL getEntityACL() {
        return acl;
    }

    @Override
    public PropertyACL getPropertyACL() {
        return pacl;
    }

    @Override
    public CaseActionList getCaseActionList() {
        return caseActionList;
    }

    @Override
    public AbacAwareInterface getABACAware() {
        return abacProcessor;
    }

    @Override
    public RbacAwareInterface getRBACAware() {
        return rbacProcessor;
    }

    @Override
    public BaseEntityInterface performCaseAction(Long aLong, String s) {
        return null;
    }

    @Override
    public CaseStatus performCaseActionAndGetCaseStatus(Long aLong, String s) {
        return null;
    }

    @Override
    public AggregateInterface performCaseActionAndGetCaseStatusAggregate(Long aLong, String s, Class<AggregateInterface> aClass) {
        return null;
    }

    @Override
    public CaseStatus getCaseStatus(BaseEntityInterface baseEntityInterface) {
        return null;
    }


    @SecureCaseAction("UPDATE")
    @SecurePropertyMerge
    @PublishResponse(eventType = "MERGE", eventBuilder = AggregateEventBuilder.class,
            responseClass = ChangeRequestAggregate.class, destination = "com.example.mirai.projectname.changerequestservice.changerequest")
    @Transactional
    @Override
    public CompleteBusinessCase merge(BaseEntityInterface newInst, BaseEntityInterface oldInst, List<String> oldInsChangedAttributeNames, List<String> newInsChangedAttributeNames) {
        return (CompleteBusinessCase) EntityServiceDefaultInterface.super.merge(newInst, oldInst, oldInsChangedAttributeNames, newInsChangedAttributeNames);
    }

    public CompleteBusinessCase getCompleteBusinessCaseByChangeRequestId(Long changeRequestId) {
        Long cbcId = getCompleteBusinessCaseIdByChangeRequestId(changeRequestId);
        return (CompleteBusinessCase) self.getEntityById(cbcId);
    }
    public Long getCompleteBusinessCaseIdByChangeRequestId(Long changeRequestId) {
        Long impactAnalysisId = getImpactAnalysisIdByChangeRequestId(changeRequestId);
        Slice<Id> idSlice = filterIds("impactAnalysis.id: " + impactAnalysisId, PageRequest.of(0, 1));
        if (Objects.isNull(idSlice) || idSlice.getNumberOfElements() != 1)
            throw  new InternalAssertionException("No Complete Business Case / multiple Complete Business Case found with changeRequest Id " + changeRequestId);
        return idSlice.getContent().get(0).getValue();
    }

    public Long getImpactAnalysisIdByChangeRequestId(Long changeRequestId) {

        Slice<Id> idSlice = impactAnalysisService.filterIds("changeRequest.id: " + changeRequestId, PageRequest.of(0, 1));
        if (Objects.isNull(idSlice) || idSlice.getNumberOfElements() != 1)
            throw  new InternalAssertionException("No Impact Analysis / multiple Impact Analysis found with changeRequest Id " + changeRequestId);
        return idSlice.getContent().get(0).getValue();
    }
}
