package com.example.mirai.projectname.changerequestservice.impactanalysis.service;

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
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestStateMachine;
import com.example.mirai.projectname.changerequestservice.customerimpact.service.CustomerImpactService;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
import com.example.mirai.projectname.changerequestservice.scope.model.Scope;
import com.example.mirai.projectname.changerequestservice.shared.service.AggregateEventBuilder;
import com.example.mirai.projectname.changerequestservice.shared.util.Constants;
import com.example.mirai.projectname.changerequestservice.shared.util.Defaults;
import com.example.mirai.projectname.changerequestservice.shared.util.ImplementationRangesValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@EntityClass(ImpactAnalysis.class)
public class ImpactAnalysisService implements EntityServiceDefaultInterface, SecurityServiceDefaultInterface, AuditServiceDefaultInterface {
    @Resource
    private ImpactAnalysisService self;
    private final ChangeRequestStateMachine stateMachine;
    private final AbacProcessor abacProcessor;
    private final RbacProcessor rbacProcessor;
    private final EntityACL acl;
    private final PropertyACL pacl;
    private final CaseActionList caseActionList;
    private final ObjectMapper objectMapper;
    @Autowired
    private CustomerImpactService customerImpactService;

    public ImpactAnalysisService(ChangeRequestStateMachine stateMachine, AbacProcessor abacProcessor, RbacProcessor rbacProcessor,
                                 EntityACL acl, PropertyACL pacl, CaseActionList caseActionList, ObjectMapper objectMapper) {
        this.stateMachine = stateMachine;
        this.abacProcessor = abacProcessor;
        this.rbacProcessor = rbacProcessor;
        this.acl = acl;
        this.pacl = pacl;
        this.caseActionList = caseActionList;
        this.objectMapper = objectMapper;
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


    @SneakyThrows
    @SecureCaseAction("UPDATE")
    @SecurePropertyMerge
    @PublishResponse(eventType = "MERGE", eventBuilder = AggregateEventBuilder.class,
            responseClass = ChangeRequestAggregate.class, destination = "com.example.mirai.projectname.changerequestservice.changerequest")
    @Transactional
    @Override
    public BaseEntityInterface merge(BaseEntityInterface newInst, BaseEntityInterface oldInst, List<String> oldInsChangedAttributeNames, List<String> newInsChangedAttributeNames) {
        List<String> changedProperties = ObjectMapperUtil.getChangedProperties(oldInst, newInst);
        if (changedProperties.contains("implementationRanges")) {
            handleImplementationRangesChange((ImpactAnalysis) oldInst, (ImpactAnalysis) newInst, oldInsChangedAttributeNames, newInsChangedAttributeNames);
        }
        if (changedProperties.contains("cbpStrategies")) {
            handleCBPStrategiesChange((ImpactAnalysis) oldInst, (ImpactAnalysis) newInst, oldInsChangedAttributeNames, newInsChangedAttributeNames);
        }
        newInsChangedAttributeNames = newInsChangedAttributeNames.stream().distinct().collect(Collectors.toList());
        ImpactAnalysis updatedImpactAnalysis = (ImpactAnalysis) EntityServiceDefaultInterface.super.merge(newInst, oldInst, oldInsChangedAttributeNames, newInsChangedAttributeNames);
        if (changedProperties.contains("impactOnAvailability") || changedProperties.contains("impactOnSystemLevelPerformance") || changedProperties.contains("impactOnExistingParts")) {
            customerImpactService.evaluateCustomerImpactDetail(updatedImpactAnalysis);
        }
        return updatedImpactAnalysis;
    }

    private void handleCBPStrategiesChange(ImpactAnalysis oldInst, ImpactAnalysis newInst, List<String> oldInsChangedAttributeNames, List<String> newInsChangedAttributeNames) {
        if ((newInst.getCbpStrategies().size() == 1 && newInst.getCbpStrategies().get(0).equals(Constants.CBP_STRATEGIES_NA))
                || newInst.getCbpStrategies().size() == 0 || Objects.isNull(newInst.getCbpStrategies())) {
            ImpactAnalysis impactAnalysis = (ImpactAnalysis) self.getEntityById(oldInst.getId());
            if (Objects.nonNull(impactAnalysis.getCalendarDependency())) {
                newInst.setCalendarDependency(null);
                newInsChangedAttributeNames.add("calendar_dependency");
                oldInst.setCalendarDependency(impactAnalysis.getCalendarDependency());
                oldInsChangedAttributeNames.add("calendar_dependency");
            }
            if (Objects.nonNull(impactAnalysis.getTargetedValidConfigurations())) {
                newInst.setTargetedValidConfigurations(null);
                newInsChangedAttributeNames.add("targeted_valid_configurations");
                oldInst.setTargetedValidConfigurations(impactAnalysis.getTargetedValidConfigurations());
                oldInsChangedAttributeNames.add("targeted_valid_configurations");
            }
        }
    }

    private void handleImplementationRangesChange(ImpactAnalysis oldInst, ImpactAnalysis newInst, List<String> oldInsChangedAttributeNames, List<String> changedAttributeNames) {
        ImpactAnalysis impactAnalysis = (ImpactAnalysis) self.getEntityById(newInst.getId());
        if (newInst.getImplementationRanges().contains(ImplementationRangesValues.IMPLEMENTATION_RANGES_NA) ||
                newInst.getImplementationRanges().contains(ImplementationRangesValues.IMPLEMENTATION_RANGES_SUPPLY_CHAIN) ||
                newInst.getImplementationRanges().contains(ImplementationRangesValues.IMPLEMENTATION_RANGES_CUSTOMER_STOCKS)) {
            if (Objects.isNull(impactAnalysis.getLiabilityRisks()) || impactAnalysis.getLiabilityRisks().isEmpty()) {
                List<String> liabilityRisks = newInst.getLiabilityRisks();
                if (Objects.isNull(liabilityRisks))
                    liabilityRisks = new ArrayList<>();
                liabilityRisks.add(Defaults.LIABILITY_RISK);
                newInst.setLiabilityRisks(liabilityRisks);
                oldInst.setLiabilityRisks(Objects.isNull(impactAnalysis.getLiabilityRisks()) ? new ArrayList<>() : impactAnalysis.getLiabilityRisks());
                oldInsChangedAttributeNames.add("liability_risks");
                changedAttributeNames.add("liability_risks");
            }
            if (Objects.isNull(impactAnalysis.getTotalInstancesAffected())) {
                newInst.setTotalInstancesAffected(Defaults.TOTAL_INSTANCES_AFFECTED);
                changedAttributeNames.add("total_instances_affected");
            }
            if (Objects.isNull(impactAnalysis.getUpgradeTime())) {
                newInst.setUpgradeTime(Defaults.UPGRADE_TIME);
                changedAttributeNames.add("upgrade_time");
            }
            if (Objects.isNull(impactAnalysis.getUpgradePackages())) {
                newInst.setUpgradePackages(Defaults.UPGRADE_PACKAGES);
                changedAttributeNames.add("upgrade_packages");
            }
            if (Objects.isNull(impactAnalysis.getPrePostConditions())) {
                newInst.setPrePostConditions(Defaults.PRE_POST_CONDITIONS);
                changedAttributeNames.add("pre_post_conditions");
            }
            if (Objects.isNull(impactAnalysis.getRecoveryTime())) {
                newInst.setRecoveryTime(Defaults.RECOVERY_TIME);
                changedAttributeNames.add("recovery_time");
            }
        }
    }

    public ImpactAnalysis getImpactAnalysisByChangeRequestId(Long changeRequestId) {
        Slice<Id> idSlice = this.filterIds("changeRequest.id:" + changeRequestId , PageRequest.of(0,1));
        if (Objects.nonNull(idSlice) && Objects.nonNull(idSlice.getContent()) && idSlice.getContent().size() > 0) {
            Long impactAnalysisId = idSlice.getContent().get(0).getValue();
            return (ImpactAnalysis) self.getEntityById(impactAnalysisId);
        }
        return null;
    }


    public void resetDataForOutScope(Scope scope) {
        ImpactAnalysis impactAnalysis = this.getImpactAnalysisByChangeRequestId(scope.getChangeRequest().getId());
        if (Objects.nonNull(impactAnalysis)) {
            impactAnalysis.setImpactOnExistingParts(null);
            Map<String, Object> changedAttrs =  new HashMap<>();
            changedAttrs.put("impact_on_existing_parts", null);
            customerImpactService.resetDataForOutScope(impactAnalysis);
            self.update(impactAnalysis, changedAttrs);
        }
    }
}
