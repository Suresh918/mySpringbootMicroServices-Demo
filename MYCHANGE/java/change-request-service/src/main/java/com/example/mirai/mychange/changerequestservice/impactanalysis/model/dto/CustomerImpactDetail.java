package com.example.mirai.projectname.changerequestservice.impactanalysis.model.dto;

import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.customerimpact.model.CustomerImpact;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
import com.example.mirai.projectname.changerequestservice.preinstallimpact.model.PreinstallImpact;
import com.example.mirai.projectname.changerequestservice.scope.model.Scope;
import com.example.mirai.projectname.changerequestservice.shared.util.Constants;
import com.example.mirai.projectname.changerequestservice.shared.util.IssueTypes;
import com.example.mirai.projectname.changerequestservice.shared.util.ScopeValues;
import com.example.mirai.projectname.changerequestservice.solutiondefinition.model.SolutionDefinition;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;


@NoArgsConstructor
@Getter
@Setter
public class CustomerImpactDetail {
    private String partsToolingInScope;
    private String partsManufacturedBefore;
    private String impactOnPreinstall;
    private String systemLevelPerformanceImpact;
    @JsonIgnore
    private String systemLevelPerformanceImpactValue;
    private String negativeImpactOnAvailability;
    @JsonIgnore
    private String negativeImpactOnAvailabilityValue;
    private String changeToSoftware;
    private String functionalSoftwareDependencies;
    @JsonIgnore
    private String functionalSoftwareDependenciesValue;
    private String impactOnUserInterfaces;
    @JsonIgnore
    private String impactOnUserInterfacesValue;
    private String impactOnWaferProcessEnvironment;
    @JsonIgnore
    private String impactOnWaferProcessEnvironmentValue;
    private String changeToCustomerImpactCriticalPart;
    @JsonIgnore
    private String changeToCustomerImpactCriticalPartValue;
    private String changeToProcessImpactsCustomer;
    @JsonIgnore
    private String changeToProcessImpactsCustomerValue;
    private String fcoUpgradeOptionCsrImplementationChange;
    @JsonIgnore
    private String fcoUpgradeOptionCsrImplementationChangeValue;
    private ChangeRequest changeRequest;
    private String customerImpactResult;

    public CustomerImpactDetail(ChangeRequest changeRequest, Scope scope, ImpactAnalysis impactAnalysis, PreinstallImpact preinstallImpact, SolutionDefinition solutionDefinition, CustomerImpact customerImpact) {
        this.evaluatePartsToolingInScope(scope);
        this.evaluatePartsManufacturedBefore(impactAnalysis);
        this.evaluateImpactOnPreinstall(preinstallImpact);
        this.evaluateSystemLevelPerformanceImpact(impactAnalysis);
        this.evaluateNegativeImpactOnAvailability(impactAnalysis);
        this.evaluateChangeToSoftware(changeRequest);
        this.evaluateFunctionalSoftwareDependencies(solutionDefinition);
        this.evaluateImpactOnUserInterfaces(customerImpact);
        this.evaluateImpactOnWaferProcessingEnvironment(customerImpact);
        this.evaluateChangeToCustomerImpactCriticalPart(customerImpact);
        this.evaluateChangeToProcessImpactsCustomer(customerImpact);
        this.evaluateFcoUpgradeOptionCsrImplementationChange(customerImpact);
        this.changeRequest = changeRequest;
    }

    //Q12 Is a New FCO, Upgrade, Option or CSR Created to Implement the Change
    private void evaluateFcoUpgradeOptionCsrImplementationChange(CustomerImpact customerImpact) {
        if (Objects.nonNull(customerImpact.getFcoUpgradeOptionCsrImplementationChange())) {
            if (customerImpact.getFcoUpgradeOptionCsrImplementationChange().toUpperCase().equals(Constants.NAME_NO)) {
                this.fcoUpgradeOptionCsrImplementationChangeValue = Constants.LABEL_NO;
            } else if (customerImpact.getFcoUpgradeOptionCsrImplementationChange().toUpperCase().equals(Constants.NAME_YES)) {
                this.fcoUpgradeOptionCsrImplementationChangeValue = Constants.LABEL_YES;
            }
            if (Objects.nonNull(customerImpact.getFcoUpgradeOptionCsrImplementationChangeDetails())) {
                this.fcoUpgradeOptionCsrImplementationChange = fcoUpgradeOptionCsrImplementationChangeValue + ", " + customerImpact.getFcoUpgradeOptionCsrImplementationChangeDetails();
            } else {
                this.fcoUpgradeOptionCsrImplementationChange = fcoUpgradeOptionCsrImplementationChangeValue;
            }
        }
    }

    //Q11 Change to a Procedure that Impacts the Customer
    private void evaluateChangeToProcessImpactsCustomer(CustomerImpact customerImpact) {
        if (Objects.nonNull(customerImpact.getChangeToProcessImpactingCustomer())) {
            if (customerImpact.getChangeToProcessImpactingCustomer().toUpperCase().equals(Constants.NAME_NO)) {
                this.changeToProcessImpactsCustomerValue = Constants.LABEL_NO;
            } else if (customerImpact.getChangeToProcessImpactingCustomer().toUpperCase().equals(Constants.NAME_YES)) {
                this.changeToProcessImpactsCustomerValue = Constants.LABEL_YES;
            }
            if (Objects.nonNull(customerImpact.getChangeToProcessImpactingCustomerDetails())) {
                this.changeToProcessImpactsCustomer = changeToProcessImpactsCustomerValue + ", " + customerImpact.getChangeToProcessImpactingCustomerDetails();
            } else {
                this.changeToProcessImpactsCustomer = changeToProcessImpactsCustomerValue;
            }
        }
    }

    //Q10 New 11NC or LD Change to a Customer Impact Critical Part
    private void evaluateChangeToCustomerImpactCriticalPart(CustomerImpact customerImpact) {
        if (Objects.nonNull(customerImpact.getChangeToCustomerImpactCriticalPart())) {
            if (customerImpact.getChangeToCustomerImpactCriticalPart().toUpperCase().equals(Constants.NAME_NO)) {
                this.changeToCustomerImpactCriticalPartValue = Constants.LABEL_NO;
            } else if (customerImpact.getChangeToCustomerImpactCriticalPart().toUpperCase().equals(Constants.NAME_YES)) {
                this.changeToCustomerImpactCriticalPartValue = Constants.LABEL_YES;
            }
            if (Objects.nonNull(customerImpact.getChangeToCustomerImpactCriticalPartDetails())) {
                this.changeToCustomerImpactCriticalPart = changeToCustomerImpactCriticalPartValue + ", " + customerImpact.getChangeToCustomerImpactCriticalPartDetails();
            } else {
                this.changeToCustomerImpactCriticalPart = changeToCustomerImpactCriticalPartValue;
            }
        }
    }

    //Q9 Impact on Wafer Processing Environment
    private void evaluateImpactOnWaferProcessingEnvironment(CustomerImpact customerImpact) {
        if (Objects.nonNull(customerImpact.getImpactOnWaferProcessEnvironment())) {
            if (customerImpact.getImpactOnWaferProcessEnvironment().toUpperCase().equals(Constants.NAME_NO)) {
                this.impactOnWaferProcessEnvironmentValue = Constants.LABEL_NO;
            } else if (customerImpact.getImpactOnWaferProcessEnvironment().toUpperCase().equals(Constants.NAME_YES)) {
                this.impactOnWaferProcessEnvironmentValue = Constants.LABEL_YES;
            }
            if (Objects.nonNull(customerImpact.getImpactOnWaferProcessEnvironmentDetails())) {
                this.impactOnWaferProcessEnvironment = impactOnWaferProcessEnvironmentValue + ", " + customerImpact.getImpactOnWaferProcessEnvironmentDetails();
            } else {
                this.impactOnWaferProcessEnvironment = impactOnWaferProcessEnvironmentValue;
            }
        }
    }

    //Q8 Impact on User Interfaces
    private void evaluateImpactOnUserInterfaces(CustomerImpact customerImpact) {
        if (Objects.nonNull(customerImpact.getImpactOnUserInterfaces())) {
            if (customerImpact.getImpactOnUserInterfaces().toUpperCase().equals(Constants.NAME_NO)) {
                this.impactOnUserInterfacesValue = Constants.LABEL_NO;
            } else if (customerImpact.getImpactOnUserInterfaces().toUpperCase().equals(Constants.NAME_YES)) {
                this.impactOnUserInterfacesValue = Constants.LABEL_YES;
            }
            if (Objects.nonNull(customerImpact.getImpactOnUserInterfacesDetails())) {
                this.impactOnUserInterfaces = impactOnUserInterfacesValue + ", " + customerImpact.getImpactOnUserInterfacesDetails();
            } else {
                this.impactOnUserInterfaces = impactOnUserInterfacesValue;
            }
        }
    }

    //Q7 Functional Software Dependencies
    private void evaluateFunctionalSoftwareDependencies(SolutionDefinition solutionDefinition) {
        if (Objects.nonNull(solutionDefinition.getFunctionalSoftwareDependencies())) {
            if(solutionDefinition.getFunctionalSoftwareDependencies().toUpperCase().equals(Constants.NAME_NONE)) {
                this.functionalSoftwareDependenciesValue = Constants.LABEL_NO;
            } else if (solutionDefinition.getFunctionalSoftwareDependencies().toUpperCase().equals(Constants.NAME_YES)) {
                this.functionalSoftwareDependenciesValue = Constants.LABEL_YES;
            }
            if (Objects.nonNull(solutionDefinition.getFunctionalSoftwareDependenciesDetails())) {
                this.functionalSoftwareDependencies = functionalSoftwareDependenciesValue + ", " +  solutionDefinition.getFunctionalSoftwareDependenciesDetails();
            } else {
                this.functionalSoftwareDependencies = functionalSoftwareDependenciesValue;
            }
        }
    }

    // Q6 ChangeToSoftware
    private void evaluateChangeToSoftware(ChangeRequest changeRequest) {
        if (Objects.nonNull(changeRequest.getIssueTypes()) && changeRequest.getIssueTypes().size() > 0) {
            if (changeRequest.getIssueTypes().contains(IssueTypes.SW)) {
                this.changeToSoftware = Constants.LABEL_YES;
            } else {
                this.changeToSoftware = Constants.LABEL_NO;
            }
        } else {
            this.changeToSoftware = Constants.LABEL_NO;
        }
    }

    // Q5 :Negative Impact on Availability
    private void evaluateNegativeImpactOnAvailability(ImpactAnalysis impactAnalysis) {
        if (Objects.nonNull(impactAnalysis.getImpactOnAvailability())) {
            if (impactAnalysis.getImpactOnAvailability().equals(Constants.NAME_INCREASE)
                    || impactAnalysis.getImpactOnAvailability().equals(Constants.NAME_NONE)) {
                this.negativeImpactOnAvailabilityValue = Constants.LABEL_NO;
            }
            if (impactAnalysis.getImpactOnAvailability().equals(Constants.NAME_DECREASE)) {
                this.negativeImpactOnAvailabilityValue = Constants.LABEL_YES;
            }
            if (Objects.nonNull(impactAnalysis.getImpactOnAvailabilityDetails())) {
                this.negativeImpactOnAvailability = negativeImpactOnAvailabilityValue + ", " + impactAnalysis.getImpactOnAvailabilityDetails();
            } else {
                this.negativeImpactOnAvailability = negativeImpactOnAvailabilityValue;
            }
        }

    }


    // Q4 System Level Performance Impact
    private void evaluateSystemLevelPerformanceImpact(ImpactAnalysis impactAnalysis) {
        if (Objects.nonNull(impactAnalysis.getImpactOnSystemLevelPerformance())) {
            if (impactAnalysis.getImpactOnSystemLevelPerformance().toUpperCase().equals(Constants.NAME_NONE)) {
                this.systemLevelPerformanceImpactValue = Constants.LABEL_NO;
            } else if (impactAnalysis.getImpactOnSystemLevelPerformance().toUpperCase().equals(Constants.NAME_YES)) {
                this.systemLevelPerformanceImpactValue = Constants.LABEL_YES;
            }
            if (Objects.nonNull(impactAnalysis.getImpactOnSystemLevelPerformanceDetails())) {
                this.systemLevelPerformanceImpact = systemLevelPerformanceImpactValue + ", " + impactAnalysis.getImpactOnSystemLevelPerformanceDetails();
            } else {
                this.systemLevelPerformanceImpact = systemLevelPerformanceImpactValue;
            }
        }
    }

    // Q3 Impact on Pre-Install (PIIA)
    private void evaluateImpactOnPreinstall(PreinstallImpact preinstallImpact) {
        if (Objects.nonNull(preinstallImpact.getPreinstallImpactResult())) {
            if (preinstallImpact.getPreinstallImpactResult().toUpperCase().equals(Constants.NAME_NONE)) {
                this.impactOnPreinstall = Constants.LABEL_NONE;
            } else if (preinstallImpact.getPreinstallImpactResult().toUpperCase().equals(Constants.NAME_YES)){
                this.impactOnPreinstall = Constants.LABEL_YES;
            }
        }
    }

    //Q2 Parts that Have Been Manufactured Before
    public void evaluatePartsManufacturedBefore(ImpactAnalysis impactAnalysis) {
        if (Objects.nonNull(this.partsToolingInScope) && this.partsToolingInScope.toUpperCase().equals(Constants.LABEL_NO)) {
            this.partsManufacturedBefore = null;
        } else if (Objects.nonNull(impactAnalysis.getImpactOnExistingParts())) {
            if (impactAnalysis.getImpactOnExistingParts().equals(Constants.NAME_YES)) {
                this.partsManufacturedBefore = Constants.LABEL_YES;
            } if (impactAnalysis.getImpactOnExistingParts().equals(Constants.NAME_NO)) {
                this.partsManufacturedBefore = Constants.LABEL_NO;
            }
        } else {
            this.partsManufacturedBefore = null;
        }
    }

    //Q1 - Machine Parts, Pre-install Parts, Service Tooling or Service Parts in Scope
    public void evaluatePartsToolingInScope(Scope scope) {
        if (Objects.isNull(scope.getParts()) && Objects.isNull(scope.getTooling())) {
            this.partsToolingInScope = null;
        }
        if (Objects.nonNull(scope.getParts()) && scope.getParts().toUpperCase().equals(ScopeValues.OUT_SCOPE)
                && Objects.nonNull(scope.getTooling()) && scope.getTooling().toUpperCase().equals(ScopeValues.OUT_SCOPE)) {
            this.partsToolingInScope = Constants.LABEL_NO;
        }
        if ((Objects.nonNull(scope.getParts())&& scope.getParts().toUpperCase().equals(ScopeValues.IN_SCOPE))
                || (Objects.nonNull(scope.getTooling()) && scope.getTooling().toUpperCase().equals(ScopeValues.IN_SCOPE))) {
            if ((Objects.nonNull(scope.getPartDetail().getMachineBomPart()) && scope.getPartDetail().getMachineBomPart().toUpperCase().equals(ScopeValues.IN_SCOPE))
                    || (Objects.nonNull(scope.getPartDetail().getFcoUpgradeOptionCsr()) && scope.getPartDetail().getFcoUpgradeOptionCsr().toUpperCase().equals(ScopeValues.IN_SCOPE))
                    || (Objects.nonNull(scope.getPartDetail().getServicePart()) && scope.getPartDetail().getServicePart().toUpperCase().equals(ScopeValues.IN_SCOPE))
                    || (Objects.nonNull(scope.getPartDetail().getPreinstallPart()) && scope.getPartDetail().getPreinstallPart().toUpperCase().equals(ScopeValues.IN_SCOPE))
                    || (Objects.nonNull(scope.getToolingDetail().getServiceTooling()) && scope.getToolingDetail().getServiceTooling().toUpperCase().equals(ScopeValues.IN_SCOPE))) {
                this.partsToolingInScope = Constants.LABEL_YES;
            } else {
                this.partsToolingInScope = Constants.LABEL_NO;
            }
        }
    }

}
