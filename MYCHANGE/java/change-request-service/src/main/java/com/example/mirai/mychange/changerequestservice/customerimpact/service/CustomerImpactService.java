package com.example.mirai.projectname.changerequestservice.customerimpact.service;

import com.example.mirai.libraries.audit.service.AuditServiceDefaultInterface;
import com.example.mirai.libraries.core.annotation.EntityClass;
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
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestAggregate;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestService;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestStateMachine;
import com.example.mirai.projectname.changerequestservice.customerimpact.model.CustomerImpact;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.dto.CustomerImpactDetail;
import com.example.mirai.projectname.changerequestservice.impactanalysis.service.ImpactAnalysisService;
import com.example.mirai.projectname.changerequestservice.preinstallimpact.model.PreinstallImpact;
import com.example.mirai.projectname.changerequestservice.preinstallimpact.service.PreinstallImpactService;
import com.example.mirai.projectname.changerequestservice.scope.model.Scope;
import com.example.mirai.projectname.changerequestservice.scope.service.ScopeService;
import com.example.mirai.projectname.changerequestservice.shared.service.AggregateEventBuilder;
import com.example.mirai.projectname.changerequestservice.shared.util.Constants;
import com.example.mirai.projectname.changerequestservice.shared.util.CustomerImpactValues;
import com.example.mirai.projectname.changerequestservice.solutiondefinition.model.SolutionDefinition;
import com.example.mirai.projectname.changerequestservice.solutiondefinition.service.SolutionDefinitionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@EntityClass(CustomerImpact.class)
public class CustomerImpactService implements EntityServiceDefaultInterface, SecurityServiceDefaultInterface, AuditServiceDefaultInterface {
    @Resource
    private CustomerImpactService self;
    private final ChangeRequestStateMachine stateMachine;
    private final AbacProcessor abacProcessor;
    private final RbacProcessor rbacProcessor;
    private final EntityACL acl;
    private final PropertyACL pacl;
    private final CaseActionList caseActionList;
    @Autowired
    private ImpactAnalysisService impactAnalysisService;
    @Autowired
    private PreinstallImpactService preinstallImpactService;
    @Autowired
    private ScopeService scopeService;
    @Autowired
    public ChangeRequestService changeRequestService;
    @Autowired
    private SolutionDefinitionService solutionDefinitionService;

    public CustomerImpactService(ChangeRequestStateMachine stateMachine, AbacProcessor abacProcessor, RbacProcessor rbacProcessor,
                                 EntityACL acl, PropertyACL pacl, CaseActionList caseActionList) {
        this.stateMachine = stateMachine;
        this.abacProcessor = abacProcessor;
        this.rbacProcessor = rbacProcessor;
        this.acl = acl;
        this.pacl = pacl;
        this.caseActionList = caseActionList;
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

    @SecurePropertyMerge
    @PublishResponse(eventType = "MERGE", eventBuilder = AggregateEventBuilder.class,
            responseClass = ChangeRequestAggregate.class, destination = "com.example.mirai.projectname.changerequestservice.changerequest")
    @Transactional
    @Override
    public CustomerImpact merge(BaseEntityInterface newInst, BaseEntityInterface oldInst, List<String> oldInsChangedAttributeNames, List<String> newInsChangedAttributeNames) {
        CustomerImpact updatedCustomerImpact = (CustomerImpact) EntityServiceDefaultInterface.super.merge(newInst, oldInst, oldInsChangedAttributeNames, newInsChangedAttributeNames);
        if (newInsChangedAttributeNames.contains("impact_on_user_interfaces") || newInsChangedAttributeNames.contains("impact_on_wafer_process_environment") ||
                newInsChangedAttributeNames.contains("change_to_customer_impact_critical_part") || newInsChangedAttributeNames.contains("change_to_process_impacting_customer") ||
                newInsChangedAttributeNames.contains("fco_upgrade_option_csr_implementation_change")) {
            CustomerImpactDetail customerImpactDetail = evaluateCustomerImpactDetail(updatedCustomerImpact);
            updatedCustomerImpact.setCustomerImpactResult(customerImpactDetail.getCustomerImpactResult());
        }
        return updatedCustomerImpact;
    }

    public CustomerImpact getCustomerImpactByImpactAnalysisId(Long impactAnalysisId) {
        Slice<Id> idSlice = this.filterIds("impactAnalysis.id:" + impactAnalysisId, PageRequest.of(0, 1));
        if (Objects.nonNull(idSlice) && Objects.nonNull(idSlice.getContent()) && idSlice.getContent().size() > 0) {
            Long customerImpactId = idSlice.getContent().get(0).getValue();
            return (CustomerImpact) self.getEntityById(customerImpactId);
        }
        return null;
    }

    public CustomerImpactDetail evaluateCustomerImpactDetail(Scope scope) {
        ImpactAnalysis impactAnalysis = this.impactAnalysisService.getImpactAnalysisByChangeRequestId(scope.getChangeRequest().getId());
        SolutionDefinition solutionDefinition = this.solutionDefinitionService.getSolutionDefinitionByChangeRequestId(scope.getChangeRequest().getId());
        ChangeRequest changeRequest = (ChangeRequest) changeRequestService.getEntityById(scope.getChangeRequest().getId());
        return evaluateCustomerImpactDetail(scope, impactAnalysis, solutionDefinition, changeRequest);
    }

    public CustomerImpactDetail evaluateCustomerImpactDetail(ImpactAnalysis impactAnalysis) {
        Scope scope = this.scopeService.getScopeByChangeRequestId(impactAnalysis.getChangeRequest().getId());
        SolutionDefinition solutionDefinition = this.solutionDefinitionService.getSolutionDefinitionByChangeRequestId(impactAnalysis.getChangeRequest().getId());
        ChangeRequest changeRequest = (ChangeRequest) changeRequestService.getEntityById(impactAnalysis.getChangeRequest().getId());
        return evaluateCustomerImpactDetail(scope, impactAnalysis, solutionDefinition, changeRequest);
    }

    public CustomerImpactDetail evaluateCustomerImpactDetail(SolutionDefinition solutionDefinition) {
        ImpactAnalysis impactAnalysis = this.impactAnalysisService.getImpactAnalysisByChangeRequestId(solutionDefinition.getChangeRequest().getId());
        Scope scope = this.scopeService.getScopeByChangeRequestId(solutionDefinition.getChangeRequest().getId());
        ChangeRequest changeRequest = (ChangeRequest) changeRequestService.getEntityById(solutionDefinition.getChangeRequest().getId());
        return evaluateCustomerImpactDetail(scope, impactAnalysis, solutionDefinition, changeRequest);
    }

    public CustomerImpactDetail evaluateCustomerImpactDetail(ChangeRequest changeRequest) {
        ImpactAnalysis impactAnalysis = this.impactAnalysisService.getImpactAnalysisByChangeRequestId(changeRequest.getId());
        Scope scope = this.scopeService.getScopeByChangeRequestId(changeRequest.getId());
        SolutionDefinition solutionDefinition = this.solutionDefinitionService.getSolutionDefinitionByChangeRequestId(changeRequest.getId());
        return evaluateCustomerImpactDetail(scope, impactAnalysis, solutionDefinition, changeRequest);
    }

    private CustomerImpactDetail evaluateCustomerImpactDetail(Scope scope, ImpactAnalysis impactAnalysis, SolutionDefinition solutionDefinition, ChangeRequest changeRequest) {
        PreinstallImpact preinstallImpact = this.preinstallImpactService.getPreinstallImpactByImpactAnalysisId(impactAnalysis.getId());
        CustomerImpact customerImpact = self.getCustomerImpactByImpactAnalysisId(impactAnalysis.getId());
        CustomerImpactDetail customerImpactDetail = new CustomerImpactDetail(changeRequest, scope, impactAnalysis, preinstallImpact, solutionDefinition, customerImpact);
        CustomerImpact evaluatedCustomerImpact = determineCustomerImpact(customerImpactDetail, impactAnalysis);
        customerImpactDetail.setCustomerImpactResult(evaluatedCustomerImpact.getCustomerImpactResult());
        return customerImpactDetail;
    }

    public CustomerImpactDetail evaluateCustomerImpactDetail(CustomerImpact customerImpact) {
        ImpactAnalysis impactAnalysis = (ImpactAnalysis) this.impactAnalysisService.getEntityById(customerImpact.getImpactAnalysis().getId());
        SolutionDefinition solutionDefinition = this.solutionDefinitionService.getSolutionDefinitionByChangeRequestId(impactAnalysis.getChangeRequest().getId());
        PreinstallImpact preinstallImpact = this.preinstallImpactService.getPreinstallImpactByImpactAnalysisId(impactAnalysis.getId());
        Scope scope = scopeService.getScopeByChangeRequestId(impactAnalysis.getChangeRequest().getId());
        ChangeRequest changeRequest = (ChangeRequest) changeRequestService.getEntityById(impactAnalysis.getChangeRequest().getId());
        CustomerImpactDetail customerImpactDetail = new CustomerImpactDetail(changeRequest, scope, impactAnalysis, preinstallImpact, solutionDefinition, customerImpact);
        CustomerImpact evaluatedCustomerImpact = determineCustomerImpact(customerImpactDetail, impactAnalysis);
        customerImpactDetail.setCustomerImpactResult(evaluatedCustomerImpact.getCustomerImpactResult());
        return customerImpactDetail;
    }

    private CustomerImpact determineCustomerImpact(CustomerImpactDetail customerImpactDetail, ImpactAnalysis impactAnalysis) {
        CustomerImpact customerImpact = self.getCustomerImpactByImpactAnalysisId(impactAnalysis.getId());
        if (Objects.nonNull(customerImpactDetail.getPartsToolingInScope()) && customerImpactDetail.getPartsToolingInScope().equals(Constants.LABEL_YES)) {
            if (Objects.nonNull(customerImpactDetail.getPartsManufacturedBefore()) && customerImpactDetail.getPartsManufacturedBefore().equals(Constants.LABEL_YES)) {
                if (this.isImpactMajor(customerImpactDetail)) {
                    //Qn 3-12 atleast one yes
                    customerImpact.setCustomerImpactResult(CustomerImpactValues.CUSTOMER_IMPACT_MAJOR);
                } else if (this.isImpactMinor(customerImpactDetail)) {
                    //Qn 3-12 all no
                    customerImpact.setCustomerImpactResult(CustomerImpactValues.CUSTOMER_IMPACT_MINOR);
                    customerImpact.setCustomerCommunication(Constants.CUSTOMER_COMMUNICATION_NOT_REQUIRED);
                    customerImpact.setCustomerApproval(Constants.CUSTOMER_APPROVAL_NOT_REQUIRED);
                } else {
                    customerImpact.setCustomerImpactResult(null);
                }
            } else if (Objects.nonNull(customerImpactDetail.getPartsManufacturedBefore()) && customerImpactDetail.getPartsManufacturedBefore().equals(Constants.LABEL_NO)) {
                if (Objects.nonNull(customerImpactDetail.getImpactOnPreinstall()) && customerImpactDetail.getImpactOnPreinstall().equals(Constants.LABEL_YES)) {
                    customerImpact.setCustomerImpactResult(CustomerImpactValues.CUSTOMER_IMPACT_MAJOR);
                } else if (Objects.nonNull(customerImpactDetail.getImpactOnPreinstall()) && customerImpactDetail.getImpactOnPreinstall().equals(Constants.LABEL_NONE)) {
                    customerImpact.setCustomerImpactResult(CustomerImpactValues.CUSTOMER_IMPACT_NA);
                    customerImpact.setCustomerCommunication(Constants.CUSTOMER_COMMUNICATION_NOT_REQUIRED);
                    customerImpact.setCustomerApproval(Constants.CUSTOMER_APPROVAL_NOT_REQUIRED);
                } else {
                    customerImpact.setCustomerImpactResult(null);
                }
            } else {
                customerImpact.setCustomerImpactResult(null);
            }
        } else if (Objects.nonNull(customerImpactDetail.getPartsToolingInScope()) && customerImpactDetail.getPartsToolingInScope().equals(Constants.LABEL_NO)) {
            customerImpact.setCustomerImpactResult(CustomerImpactValues.CUSTOMER_IMPACT_NA);
            customerImpact.setCustomerCommunication(Constants.CUSTOMER_COMMUNICATION_NOT_REQUIRED);
            customerImpact.setCustomerApproval(Constants.CUSTOMER_APPROVAL_NOT_REQUIRED);
        } else {
            customerImpact.setCustomerImpactResult(null);
        }
        return (CustomerImpact) self.update(customerImpact);
    }


    private boolean isImpactMajor(CustomerImpactDetail customerImpactDetail) {
        return ((Objects.nonNull(customerImpactDetail.getImpactOnPreinstall()) && customerImpactDetail.getImpactOnPreinstall().equals(Constants.LABEL_YES))
                || (Objects.nonNull(customerImpactDetail.getSystemLevelPerformanceImpactValue()) && customerImpactDetail.getSystemLevelPerformanceImpactValue().equals(Constants.LABEL_YES))
                || (Objects.nonNull(customerImpactDetail.getNegativeImpactOnAvailabilityValue()) && customerImpactDetail.getNegativeImpactOnAvailabilityValue().equals(Constants.LABEL_YES))
                || (Objects.nonNull(customerImpactDetail.getChangeToSoftware()) && customerImpactDetail.getChangeToSoftware().equals(Constants.LABEL_YES))
                || (Objects.nonNull(customerImpactDetail.getFunctionalSoftwareDependenciesValue()) && customerImpactDetail.getFunctionalSoftwareDependenciesValue().equals(Constants.LABEL_YES))
                || (Objects.nonNull(customerImpactDetail.getImpactOnUserInterfacesValue()) && customerImpactDetail.getImpactOnUserInterfacesValue().equals(Constants.LABEL_YES))
                || (Objects.nonNull(customerImpactDetail.getImpactOnWaferProcessEnvironmentValue()) && customerImpactDetail.getImpactOnWaferProcessEnvironmentValue().equals(Constants.LABEL_YES))
                || (Objects.nonNull(customerImpactDetail.getChangeToCustomerImpactCriticalPartValue()) && customerImpactDetail.getChangeToCustomerImpactCriticalPartValue().equals(Constants.LABEL_YES))
                || (Objects.nonNull(customerImpactDetail.getChangeToProcessImpactsCustomerValue()) && customerImpactDetail.getChangeToProcessImpactsCustomerValue().equals(Constants.LABEL_YES))
                || (Objects.nonNull(customerImpactDetail.getFcoUpgradeOptionCsrImplementationChangeValue()) && customerImpactDetail.getFcoUpgradeOptionCsrImplementationChangeValue().equals(Constants.LABEL_YES)));

    }

    private boolean isImpactMinor(CustomerImpactDetail customerImpactDetail) {
        return ((Objects.nonNull(customerImpactDetail.getImpactOnPreinstall()) && customerImpactDetail.getImpactOnPreinstall().equals(Constants.LABEL_NONE))
                && (Objects.nonNull(customerImpactDetail.getSystemLevelPerformanceImpactValue()) && customerImpactDetail.getSystemLevelPerformanceImpactValue().equals(Constants.LABEL_NO))
                && (Objects.nonNull(customerImpactDetail.getNegativeImpactOnAvailabilityValue()) && customerImpactDetail.getNegativeImpactOnAvailabilityValue().equals(Constants.LABEL_NO))
                && (Objects.nonNull(customerImpactDetail.getChangeToSoftware()) && customerImpactDetail.getChangeToSoftware().equals(Constants.LABEL_NO))
                && (Objects.nonNull(customerImpactDetail.getFunctionalSoftwareDependenciesValue()) && customerImpactDetail.getFunctionalSoftwareDependenciesValue().equals(Constants.LABEL_NO))
                && (Objects.nonNull(customerImpactDetail.getImpactOnUserInterfacesValue()) && customerImpactDetail.getImpactOnUserInterfacesValue().equals(Constants.LABEL_NO))
                && (Objects.nonNull(customerImpactDetail.getImpactOnWaferProcessEnvironmentValue()) && customerImpactDetail.getImpactOnWaferProcessEnvironmentValue().equals(Constants.LABEL_NO))
                && (Objects.nonNull(customerImpactDetail.getChangeToCustomerImpactCriticalPartValue()) && customerImpactDetail.getChangeToCustomerImpactCriticalPartValue().equals(Constants.LABEL_NO))
                && (Objects.nonNull(customerImpactDetail.getChangeToProcessImpactsCustomerValue()) && customerImpactDetail.getChangeToProcessImpactsCustomerValue().equals(Constants.LABEL_NO))
                && (Objects.nonNull(customerImpactDetail.getFcoUpgradeOptionCsrImplementationChangeValue()) && customerImpactDetail.getFcoUpgradeOptionCsrImplementationChangeValue().equals(Constants.LABEL_NO)));

    }

    public void resetDataForOutScope(ImpactAnalysis impactAnalysis) {
        CustomerImpact customerImpact = this.getCustomerImpactByImpactAnalysisId(impactAnalysis.getId());
        if (Objects.nonNull(customerImpact)) {
            customerImpact.setImpactOnUserInterfaces(null);
            customerImpact.setImpactOnUserInterfacesDetails(null);
            customerImpact.setImpactOnWaferProcessEnvironment(null);
            customerImpact.setImpactOnWaferProcessEnvironmentDetails(null);
            customerImpact.setChangeToCustomerImpactCriticalPart(null);
            customerImpact.setChangeToCustomerImpactCriticalPartDetails(null);
            customerImpact.setChangeToProcessImpactingCustomer(null);
            customerImpact.setChangeToProcessImpactingCustomerDetails(null);
            customerImpact.setFcoUpgradeOptionCsrImplementationChange(null);
            customerImpact.setFcoUpgradeOptionCsrImplementationChangeDetails(null);
            self.update(customerImpact);
        }

    }

    public void resetCustomerCommunicationAndApproval(Long changeRequestId) {
        ImpactAnalysis impactAnalysis = impactAnalysisService.getImpactAnalysisByChangeRequestId(changeRequestId);
        CustomerImpact customerImpact = self.getCustomerImpactByImpactAnalysisId(impactAnalysis.getId());
        customerImpact.setCustomerCommunication(Constants.CUSTOMER_COMMUNICATION_NOT_REQUIRED);
        customerImpact.setCustomerApproval(Constants.CUSTOMER_APPROVAL_NOT_REQUIRED);
        self.update(customerImpact);
    }

    @Transactional
    public CustomerImpact mergeEntityBySystemUser(BaseEntityInterface newInst, BaseEntityInterface oldInst, List<String> oldInsChangedAttributeNames, List<String> newInsChangedAttributeNames) {
        log.info("newInst " + newInsChangedAttributeNames + " oldInst " + oldInsChangedAttributeNames);
        return (CustomerImpact) self.merge(newInst, oldInst, oldInsChangedAttributeNames, newInsChangedAttributeNames);
    }

    @SecurePropertyRead
    public CustomerImpact getCustomerImpactById(Long customerImpactId) {
        return (CustomerImpact) self.getEntityById(customerImpactId);
    }
}
