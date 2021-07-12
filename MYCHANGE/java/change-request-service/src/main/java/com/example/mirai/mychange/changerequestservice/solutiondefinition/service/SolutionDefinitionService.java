package com.example.mirai.projectname.changerequestservice.solutiondefinition.service;

import com.example.mirai.libraries.audit.service.AuditServiceDefaultInterface;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.annotation.SecureCaseAction;
import com.example.mirai.libraries.core.annotation.SecurePropertyMerge;
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
import com.example.mirai.projectname.changerequestservice.customerimpact.service.CustomerImpactService;
import com.example.mirai.projectname.changerequestservice.shared.service.AggregateEventBuilder;
import com.example.mirai.projectname.changerequestservice.solutiondefinition.model.SolutionDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Service
@EntityClass(SolutionDefinition.class)
public class SolutionDefinitionService implements EntityServiceDefaultInterface, SecurityServiceDefaultInterface, AuditServiceDefaultInterface {
    @Resource
    private SolutionDefinitionService self;
    private ChangeRequestStateMachine stateMachine;
    private AbacProcessor abacProcessor;
    private RbacProcessor rbacProcessor;
    private EntityACL acl;
    private PropertyACL pacl;
    private CaseActionList caseActionList;
    @Autowired
    private CustomerImpactService customerImpactService;

    public SolutionDefinitionService(ChangeRequestStateMachine stateMachine, AbacProcessor abacProcessor, RbacProcessor rbacProcessor,
                                     CaseActionList caseActionList,
                                     EntityACL acl, PropertyACL pacl) {
        this.stateMachine = stateMachine;
        this.abacProcessor = abacProcessor;
        this.rbacProcessor = rbacProcessor;
        this.caseActionList = caseActionList;
        this.acl = acl;
        this.pacl = pacl;
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
    public SolutionDefinition merge(BaseEntityInterface newInst, BaseEntityInterface oldInst, List<String> oldInsChangedAttributeNames, List<String> newInsChangedAttributeNames) {
        SolutionDefinition solutionDefinition = (SolutionDefinition) EntityServiceDefaultInterface.super.merge(newInst, oldInst, oldInsChangedAttributeNames, newInsChangedAttributeNames);
        customerImpactService.evaluateCustomerImpactDetail(solutionDefinition);
        return solutionDefinition;
    }

    public SolutionDefinition getSolutionDefinitionByChangeRequestId(Long changeRequestId) {
        Slice<Id> idSlice = this.filterIds("changeRequest.id:" + changeRequestId , PageRequest.of(0,1));
        if (Objects.nonNull(idSlice) && idSlice.getNumberOfElements() > 0) {
            Long solutionDefinitionId = idSlice.getContent().get(0).getValue();
            return (SolutionDefinition) self.getEntityById(solutionDefinitionId);
        }
        return null;
    }
}
