package com.example.mirai.projectname.changerequestservice.preinstallimpact.service;

import com.example.mirai.libraries.audit.service.AuditServiceDefaultInterface;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.annotation.SecureCaseAction;
import com.example.mirai.libraries.core.annotation.SecurePropertyMerge;
import com.example.mirai.libraries.core.annotation.SecurePropertyRead;
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
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestDetail;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestService;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestStateMachine;
import com.example.mirai.projectname.changerequestservice.customerimpact.service.CustomerImpactService;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.dto.CustomerImpactDetail;
import com.example.mirai.projectname.changerequestservice.impactanalysis.service.ImpactAnalysisService;
import com.example.mirai.projectname.changerequestservice.myteam.service.ChangeRequestMyTeamService;
import com.example.mirai.projectname.changerequestservice.preinstallimpact.model.PreinstallImpact;
import com.example.mirai.projectname.changerequestservice.preinstallimpact.repository.PreinstallImpactRepository;
import com.example.mirai.projectname.changerequestservice.scope.model.Scope;
import com.example.mirai.projectname.changerequestservice.shared.service.AggregateEventBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@EntityClass(PreinstallImpact.class)
public class PreinstallImpactService implements EntityServiceDefaultInterface, SecurityServiceDefaultInterface, AuditServiceDefaultInterface {
    @Resource
    private PreinstallImpactService self;
    private final ChangeRequestStateMachine stateMachine;
    private final AbacProcessor abacProcessor;
    private final RbacProcessor rbacProcessor;
    private final EntityACL acl;
    private final PropertyACL pacl;
    private final CaseActionList caseActionList;
    private final PreinstallImpactRepository preinstallimpactRepository;
    private final CustomerImpactService customerImpactService;
    private final ChangeRequestMyTeamService changeRequestMyTeamService;
    private final ChangeRequestService changeRequestService;
    private final ImpactAnalysisService impactAnalysisService;

    public PreinstallImpactService(ChangeRequestStateMachine stateMachine, AbacProcessor abacProcessor, RbacProcessor rbacProcessor,
                                   EntityACL acl, PropertyACL pacl, CaseActionList caseActionList, PreinstallImpactRepository preinstallimpactRepository,
                                   CustomerImpactService customerImpactService,
                                   ChangeRequestService changeRequestService, ChangeRequestMyTeamService changeRequestMyTeamService,
                                   ImpactAnalysisService impactAnalysisService) {
        this.stateMachine = stateMachine;
        this.abacProcessor = abacProcessor;
        this.rbacProcessor = rbacProcessor;
        this.acl = acl;
        this.pacl = pacl;
        this.caseActionList = caseActionList;
        this.customerImpactService = customerImpactService;
        this.preinstallimpactRepository = preinstallimpactRepository;
        this.changeRequestService = changeRequestService;
        this.changeRequestMyTeamService = changeRequestMyTeamService;
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
    public PreinstallImpact merge(BaseEntityInterface newInst, BaseEntityInterface oldInst, List<String> oldInsChangedAttributeNames, List<String> newInsChangedAttributeNames) {
        return (PreinstallImpact) EntityServiceDefaultInterface.super.merge(newInst, oldInst, oldInsChangedAttributeNames, newInsChangedAttributeNames);
    }

    public PreinstallImpact getPreinstallImpactByImpactAnalysisId(Long impactAnalysisId) {
        Slice<Id> idSlice = this.filterIds("impactAnalysis.id:" + impactAnalysisId , PageRequest.of(0,1));
        if (Objects.nonNull(idSlice) && Objects.nonNull(idSlice.getContent()) && idSlice.getContent().size() > 0) {
            Long preinstallImpactId = idSlice.getContent().get(0).getValue();
            return (PreinstallImpact) self.getEntityById(preinstallImpactId);
        }
        return null;
    }

    @Transactional
    @PublishResponse(eventType = "UPDATE", eventBuilder = AggregateEventBuilder.class,
            responseClass = ChangeRequestAggregate.class, destination = "com.example.mirai.projectname.changerequestservice.changerequest")
    @SecureCaseAction("UPDATE")
    @SecurePropertyRead
    public ChangeRequestAggregate updatePreinstallImpact(BaseEntityInterface entity, Map<String, Object> newChangedAttrs) {
        PreinstallImpact preinstallImpact = (PreinstallImpact) self.update(entity, newChangedAttrs);
        Long impactAnalysisId = preinstallImpact.getImpactAnalysis().getId();
        ImpactAnalysis impactAnalysis = (ImpactAnalysis) impactAnalysisService.getEntityById(impactAnalysisId);
        return changeRequestService.getAggregate(impactAnalysis.getChangeRequest().getId());
    }

    public ChangeRequestDetail getChangeRequestDetail(ChangeRequestAggregate changeRequestAggregate) {
        Scope scope = changeRequestAggregate.getScope();
        CustomerImpactDetail customerImpactDetail = customerImpactService.evaluateCustomerImpactDetail(scope);
        return new ChangeRequestDetail(changeRequestAggregate, customerImpactDetail);
    }
}
