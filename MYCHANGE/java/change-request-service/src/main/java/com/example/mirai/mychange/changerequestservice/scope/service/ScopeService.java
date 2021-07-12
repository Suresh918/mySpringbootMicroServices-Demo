package com.example.mirai.projectname.changerequestservice.scope.service;

import com.example.mirai.libraries.audit.service.AuditServiceDefaultInterface;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.annotation.SecureCaseAction;
import com.example.mirai.libraries.core.annotation.SecurePropertyMerge;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.CaseStatus;
import com.example.mirai.libraries.entity.model.Id;
import com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
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
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestService;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestStateMachine;
import com.example.mirai.projectname.changerequestservice.customerimpact.service.CustomerImpactService;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.dto.CustomerImpactDetail;
import com.example.mirai.projectname.changerequestservice.impactanalysis.service.ImpactAnalysisService;
import com.example.mirai.projectname.changerequestservice.myteam.service.ChangeRequestMyTeamService;
import com.example.mirai.projectname.changerequestservice.scope.model.Scope;
import com.example.mirai.projectname.changerequestservice.scope.model.dto.PackagingDetail;
import com.example.mirai.projectname.changerequestservice.scope.model.dto.PartDetail;
import com.example.mirai.projectname.changerequestservice.scope.model.dto.ToolingDetail;
import com.example.mirai.projectname.changerequestservice.scope.repository.ScopeRepository;
import com.example.mirai.projectname.changerequestservice.shared.service.AggregateEventBuilder;
import com.example.mirai.projectname.changerequestservice.shared.util.ScopeValues;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@EntityClass(Scope.class)
public class ScopeService implements EntityServiceDefaultInterface, SecurityServiceDefaultInterface, AuditServiceDefaultInterface {
    @Resource
    private ScopeService self;
    private final ChangeRequestStateMachine stateMachine;
    private final AbacProcessor abacProcessor;
    private final RbacProcessor rbacProcessor;
    private final EntityACL acl;
    private final PropertyACL pacl;
    private final CaseActionList caseActionList;
    private final ObjectMapper objectMapper;
    ScopeRepository scopeRepository;
    ImpactAnalysisService impactAnalysisService;
    CustomerImpactService customerImpactService;
    ChangeRequestMyTeamService changeRequestMyTeamService;
    ChangeRequestService changeRequestService;

    public ScopeService(ChangeRequestStateMachine stateMachine, AbacProcessor abacProcessor, RbacProcessor rbacProcessor,
                        EntityACL acl, PropertyACL pacl, CaseActionList caseActionList, ObjectMapper objectMapper, ScopeRepository scopeRepository, ImpactAnalysisService impactAnalysisService,
                        CustomerImpactService customerImpactService, ChangeRequestMyTeamService changeRequestMyTeamService, ChangeRequestService changeRequestService) {
        this.stateMachine = stateMachine;
        this.abacProcessor = abacProcessor;
        this.rbacProcessor = rbacProcessor;
        this.acl = acl;
        this.pacl = pacl;
        this.caseActionList = caseActionList;
        this.objectMapper = objectMapper;
        this.scopeRepository = scopeRepository;
        this.impactAnalysisService = impactAnalysisService;
        this.customerImpactService = customerImpactService;
        this.changeRequestMyTeamService = changeRequestMyTeamService;
        this.changeRequestService = changeRequestService;
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
    public Scope merge(BaseEntityInterface newInst, BaseEntityInterface oldInst, List<String> oldInsChangedAttributeNames, List<String> newInsChangedAttributeNames) {
        return (Scope) EntityServiceDefaultInterface.super.merge(newInst, oldInst, oldInsChangedAttributeNames, newInsChangedAttributeNames);
    }

    @Transactional
    @PublishResponse(eventType = "UPDATE", eventBuilder = AggregateEventBuilder.class,
            responseClass = ChangeRequestAggregate.class, destination = "com.example.mirai.projectname.changerequestservice.changerequest")
    @SecureCaseAction("UPDATE")
    public Scope updateScope(Scope scope, Map<String, Object> newChangedAttrs) {
        //if parent is set to outscope, all children should be set to outscope
        if (Objects.nonNull(scope.getParts()) && scope.getParts().equals(ScopeValues.OUT_SCOPE)) {
           setPartDetailScope(scope);
           newChangedAttrs.put("part_detail", scope.getPartDetail());
        }
        if (Objects.nonNull(scope.getPackaging()) && scope.getPackaging().equals(ScopeValues.OUT_SCOPE)) {
            setPackagingDetailScope(scope);
            newChangedAttrs.put("packaging_detail", scope.getPackagingDetail());
        }
        if (Objects.nonNull(scope.getTooling()) && scope.getTooling().equals(ScopeValues.OUT_SCOPE)) {
            setToolingDetailScope(scope);
            newChangedAttrs.put("tooling_detail", scope.getToolingDetail());
        }
        Scope updatedScope = (Scope) self.update(scope, newChangedAttrs);
        if (Objects.nonNull(updatedScope) && Objects.nonNull(updatedScope.getPartDetail()) &&  Objects.nonNull(updatedScope.getPartDetail().getMachineBomPart())
                && updatedScope.getPartDetail().getMachineBomPart().toUpperCase().equals(ScopeValues.OUT_SCOPE)
                && Objects.nonNull(updatedScope.getPartDetail().getFcoUpgradeOptionCsr()) && updatedScope.getPartDetail().getFcoUpgradeOptionCsr().toUpperCase().equals(ScopeValues.OUT_SCOPE)
                && Objects.nonNull(updatedScope.getPartDetail().getServicePart()) && updatedScope.getPartDetail().getServicePart().toUpperCase().equals(ScopeValues.OUT_SCOPE)
                && Objects.nonNull(updatedScope.getPartDetail().getPreinstallPart()) && updatedScope.getPartDetail().getPreinstallPart().toUpperCase().equals(ScopeValues.OUT_SCOPE)
                && Objects.nonNull(updatedScope.getToolingDetail().getServiceTooling()) && updatedScope.getToolingDetail().getServiceTooling().toUpperCase().equals(ScopeValues.OUT_SCOPE)) {
            impactAnalysisService.resetDataForOutScope(updatedScope);
        }
        return updatedScope;
    }

    private void setToolingDetailScope(Scope scope) {
        if (Objects.isNull(scope.getToolingDetail()))
            scope.setToolingDetail(new ToolingDetail());
        scope.getToolingDetail().setSupplierTooling(ScopeValues.OUT_SCOPE);
        scope.getToolingDetail().setServiceTooling(ScopeValues.OUT_SCOPE);
        scope.getToolingDetail().setManufacturingDeTooling(ScopeValues.OUT_SCOPE);
    }

    private void setPackagingDetailScope(Scope scope) {
        if (Objects.isNull(scope.getPackagingDetail()))
            scope.setPackagingDetail(new PackagingDetail());
        scope.getPackagingDetail().setSupplierPackaging(ScopeValues.OUT_SCOPE);
        scope.getPackagingDetail().setStoragePackaging(ScopeValues.OUT_SCOPE);
        scope.getPackagingDetail().setShippingPackaging(ScopeValues.OUT_SCOPE);
        scope.getPackagingDetail().setReusablePackaging(ScopeValues.OUT_SCOPE);
    }

    private void setPartDetailScope(Scope scope) {
        if (Objects.isNull(scope.getPartDetail()))
            scope.setPartDetail(new PartDetail());
        scope.getPartDetail().setTestRigPart(ScopeValues.OUT_SCOPE);
        scope.getPartDetail().setServicePart(ScopeValues.OUT_SCOPE);
        scope.getPartDetail().setPreinstallPart(ScopeValues.OUT_SCOPE);
        scope.getPartDetail().setMachineBomPart(ScopeValues.OUT_SCOPE);
        scope.getPartDetail().setFcoUpgradeOptionCsr(ScopeValues.OUT_SCOPE);
        scope.getPartDetail().setDevBagPart(ScopeValues.OUT_SCOPE);
    }

    public Scope getScopeByChangeRequestId(Long changeRequestId) {
        Slice<Id> idSlice = this.filterIds("changeRequest.id:" + changeRequestId , PageRequest.of(0,1));
        if (Objects.nonNull(idSlice) && Objects.nonNull(idSlice.getContent()) && idSlice.getContent().size() > 0) {
            Long scopeId = idSlice.getContent().get(0).getValue();
            return (Scope) self.getEntityById(scopeId);
        }
        return null;
    }

    public CustomerImpactDetail evaluateCustomerImpact(Scope scope) {
        return customerImpactService.evaluateCustomerImpactDetail(scope);
    }
    public ChangeRequestAggregate getChangeRequestAggregate(Scope scope) {
        return changeRequestService.getAggregate(scope.getChangeRequest().getId());
    }

    public void resetScope(Long changeRequestId) {
        Scope scope = getScopeByChangeRequestId(changeRequestId);
        scope.setPackaging(null);
        scope.setParts(null);
        scope.setTooling(null);
        scope.setPackagingDetail(null);
        scope.setToolingDetail(null);
        scope.setPartDetail(null);
        scope.setScopeDetails(null);
        scope.setBop(null);
        updateScope(scope, ObjectMapperUtil.getChangedAttributes(objectMapper.convertValue(scope, JsonNode.class)));
        evaluateCustomerImpact(scope);
    }
}
