package com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate;

import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestContext;
import com.example.mirai.projectname.changerequestservice.changerequest.model.RuleSet;
import com.example.mirai.projectname.changerequestservice.completebusinesscase.model.CompleteBusinessCase;
import com.example.mirai.projectname.changerequestservice.customerimpact.model.CustomerImpact;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.dto.CustomerImpactDetail;
import com.example.mirai.projectname.changerequestservice.myteam.model.aggregate.ChangeRequestMyTeamAggregate;
import com.example.mirai.projectname.changerequestservice.myteam.model.aggregate.ChangeRequestMyTeamDetailsAggregate;
import com.example.mirai.projectname.changerequestservice.myteam.model.aggregate.ChangeRequestMyTeamMemberAggregate;
import com.example.mirai.projectname.changerequestservice.preinstallimpact.model.PreinstallImpact;
import com.example.mirai.projectname.changerequestservice.scope.model.Scope;
import com.example.mirai.projectname.changerequestservice.scope.model.dto.ScopeFieldVisibilityFactor;
import com.example.mirai.projectname.changerequestservice.shared.util.Constants;
import com.example.mirai.projectname.changerequestservice.solutiondefinition.model.SolutionDefinition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@Getter
public class ChangeRequestDetail {
    private Long id;
    //private String changeRequestNumber;
    private List<ChangeRequestContext> contexts;
    private String title;
    private Integer status;
    private Boolean isSecure;
    private User changeSpecialist1;
    private User changeSpecialist2;
    private User creator;
    private Date createdOn;
    private List<String> changeControlBoards;
    private List<String> changeBoards;
    private List<String> issueTypes;
    private String changeRequestType;
    private Integer analysisPriority;
    private String projectId;
    private String productId;
    private String functionalClusterId;
    private List<String> reasonsForChange;
    private String problemDescription;
    private String proposedSolution;
    private String rootCause;
    private String benefitsOfChange;
    private Integer implementationPriority;
    private String requirementsForImplementationPlan;
    private Float excessAndObsolescenceSavings;
    private List<String> dependentChangeRequestIds;
    private RuleSet changeBoardRuleSet;
    private User changeOwner;
    private String changeOwnerType;
    private Scope scope;
    private ScopeFieldVisibilityFactor scopeFieldsVisibilityFactor;
    private SolutionDefinition solutionDefinition;
    private ImpactAnalysisDetail impactAnalysis;
    private CompleteBusinessCase completeBusinessCase;
    private MyTeamDetail myTeam;

    public ChangeRequestDetail(ChangeRequestAggregate changeRequestAggregate, CustomerImpactDetail customerImpactDetail) {
        this(changeRequestAggregate);
        this.scopeFieldsVisibilityFactor = new ScopeFieldVisibilityFactor();
        scopeFieldsVisibilityFactor.setShowExistingPartQuestion(
                Objects.nonNull(customerImpactDetail.getPartsToolingInScope()) && customerImpactDetail.getPartsToolingInScope().equals(Constants.NAME_YES));
        scopeFieldsVisibilityFactor.setShowOtherQuestions(
                Objects.nonNull(customerImpactDetail.getPartsToolingInScope()) && customerImpactDetail.getPartsToolingInScope().equals(Constants.NAME_YES)
                        && Objects.nonNull(customerImpactDetail.getPartsManufacturedBefore()) && customerImpactDetail.getPartsManufacturedBefore().equals(Constants.NAME_YES));
    }

    public ChangeRequestDetail(ChangeRequestAggregate changeRequestAggregate) {
        this.myTeam = new MyTeamDetail(changeRequestAggregate.getMyTeamDetails());
        this.scope = changeRequestAggregate.getScope();
        this.solutionDefinition = changeRequestAggregate.getSolutionDefinition();
        if (Objects.nonNull(changeRequestAggregate.getImpactAnalysis())) {
            this.impactAnalysis = new ImpactAnalysisDetail(changeRequestAggregate.getImpactAnalysis().getGeneral());
            if (Objects.nonNull(changeRequestAggregate.getImpactAnalysis().getDetails())) {
                this.impactAnalysis.setCustomerImpact(changeRequestAggregate.getImpactAnalysis().getDetails().getCustomerImpact());
                this.completeBusinessCase = changeRequestAggregate.getImpactAnalysis().getDetails().getCompleteBusinessCase();
                this.impactAnalysis.setPreinstallImpact(changeRequestAggregate.getImpactAnalysis().getDetails().getPreinstallImpact());
            }
        }
        if (Objects.nonNull(changeRequestAggregate.getDescription())) {
            ChangeRequest changeRequest = changeRequestAggregate.getDescription();
            this.id = changeRequest.getId();
            //this.changeRequestNumber = changeRequest.getId().toString();
            this.analysisPriority = changeRequest.getAnalysisPriority();
            this.benefitsOfChange = changeRequest.getBenefitsOfChange();
            this.changeBoardRuleSet = changeRequest.getChangeBoardRuleSet();
            this.contexts = changeRequest.getContexts();
            this.title = changeRequest.getTitle();
            this.status = changeRequest.getStatus();
            this.isSecure = changeRequest.getIsSecure();
            this.changeSpecialist1 = changeRequest.getChangeSpecialist1();
            this.changeSpecialist2 = changeRequest.getChangeSpecialist2();
            this.createdOn = changeRequest.getCreatedOn();
            this.creator = changeRequest.getCreator();
            this.changeControlBoards = changeRequest.getChangeControlBoards();
            this.changeBoards = changeRequest.getChangeBoards();
            this.issueTypes = changeRequest.getIssueTypes();
            this.changeRequestType = changeRequest.getChangeRequestType();
            this.projectId = changeRequest.getProjectId();
            this.productId = changeRequest.getProductId();
            this.functionalClusterId = changeRequest.getFunctionalClusterId();
            this.reasonsForChange = changeRequest.getReasonsForChange();
            this.problemDescription = changeRequest.getProblemDescription();
            this.proposedSolution = changeRequest.getProposedSolution();
            this.rootCause = changeRequest.getRootCause();
            this.implementationPriority = changeRequest.getImplementationPriority();
            this.requirementsForImplementationPlan = (changeRequest.getRequirementsForImplementationPlan());
            this.excessAndObsolescenceSavings = changeRequest.getExcessAndObsolescenceSavings();
            this.dependentChangeRequestIds = changeRequest.getDependentChangeRequestIds();
            this.changeOwner = changeRequest.getChangeOwner();
            this.changeOwnerType = changeRequest.getChangeOwnerType();
        }
    }
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ImpactAnalysisDetail {
        private Long id;
        private String upgradePackages;
        private Duration upgradeTime;
        private Duration recoveryTime;
        private String prePostConditions;
        private String impactOnSequence;
        private String impactOnSequenceDetails;
        private String impactOnAvailability;
        private String impactOnAvailabilityDetails;
        private String multiPlantImpact;
        private String phaseOutSparesTools;
        private String phaseOutSparesToolsDetails;
        private String techRiskAssessmentSra;
        private String techRiskAssessmentSraDetails;
        private String techRiskAssessmentFmea;
        private String techRiskAssessmentFmeaDetails;
        private String totalInstancesAffected;
        private String impactOnSystemLevelPerformance;
        private String impactOnSystemLevelPerformanceDetails;
        private String impactOnCycleTime;
        private String impactOnCycleTimeDetails;
        private String impactOnLaborHours;
        private String impactOnLaborHoursDetails;
        private String impactOnExistingParts;
        private List<String> liabilityRisks;
        private List<String> implementationRanges;
        private String implementationRangesDetails;
        private List<String> cbpStrategies;
        private String cbpStrategiesDetails;
        private Duration developmentLaborHours;
        private Duration investigationLaborHours;
        private List<String> fcoTypes;
        private String calendarDependency; //leading-NonLeading
        private String targetedValidConfigurations;
        private CustomerImpact customerImpact;
        private PreinstallImpact preinstallImpact;
        public ImpactAnalysisDetail(ImpactAnalysis impactAnalysis) {
            this.id = impactAnalysis.getId();
            this.upgradePackages = impactAnalysis.getUpgradePackages();
            this.upgradeTime = impactAnalysis.getUpgradeTime();
            this.recoveryTime = impactAnalysis.getRecoveryTime();
            this.prePostConditions = impactAnalysis.getPrePostConditions();
            this.impactOnSequence = impactAnalysis.getImpactOnSequence();
            this.impactOnSequenceDetails = impactAnalysis.getImpactOnSequenceDetails();
            this.impactOnAvailability = impactAnalysis.getImpactOnAvailability();
            this.impactOnAvailabilityDetails = impactAnalysis.getImpactOnAvailabilityDetails();
            this.multiPlantImpact = impactAnalysis.getMultiPlantImpact();
            this.phaseOutSparesTools = impactAnalysis.getPhaseOutSparesTools();
            this.phaseOutSparesToolsDetails = impactAnalysis.getPhaseOutSparesToolsDetails();
            this.techRiskAssessmentSra = impactAnalysis.getTechRiskAssessmentSra();
            this.techRiskAssessmentSraDetails = impactAnalysis.getTechRiskAssessmentSraDetails();
            this.techRiskAssessmentFmea = impactAnalysis.getTechRiskAssessmentFmea();
            this.techRiskAssessmentFmeaDetails = impactAnalysis.getTechRiskAssessmentFmeaDetails();
            this.totalInstancesAffected = impactAnalysis.getTotalInstancesAffected();
            this.impactOnSystemLevelPerformance = impactAnalysis.getImpactOnSystemLevelPerformance();
            this.impactOnSystemLevelPerformanceDetails = impactAnalysis.getImpactOnSystemLevelPerformanceDetails();
            this.impactOnCycleTime = impactAnalysis.getImpactOnCycleTime();
            this.impactOnLaborHours = impactAnalysis.getImpactOnLaborHours();
            this.impactOnLaborHoursDetails = impactAnalysis.getImpactOnLaborHoursDetails();
            this.impactOnCycleTimeDetails = impactAnalysis.getImpactOnCycleTimeDetails();
            this.impactOnExistingParts = impactAnalysis.getImpactOnExistingParts();
            this.liabilityRisks = impactAnalysis.getLiabilityRisks();
            this.implementationRanges = impactAnalysis.getImplementationRanges();
            this.implementationRangesDetails = impactAnalysis.getImplementationRangesDetails();
            this.cbpStrategies = impactAnalysis.getCbpStrategies();
            this.cbpStrategiesDetails = impactAnalysis.getCbpStrategiesDetails();
            this.developmentLaborHours = impactAnalysis.getDevelopmentLaborHours();
            this.investigationLaborHours = impactAnalysis.getInvestigationLaborHours();
            this.fcoTypes = impactAnalysis.getFcoTypes();
            this.calendarDependency = impactAnalysis.getCalendarDependency(); //leading-NonLeading
            this.targetedValidConfigurations = impactAnalysis.getTargetedValidConfigurations();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyTeamDetail {
        private Long id;
        private Set<ChangeRequestMyTeamMemberAggregate> members;
        public MyTeamDetail(ChangeRequestMyTeamDetailsAggregate changeRequestMyTeamAggregate) {
            this.id = changeRequestMyTeamAggregate.getMyTeam().getId();
            this.members = changeRequestMyTeamAggregate.getMembers();
        }

        public MyTeamDetail(ChangeRequestMyTeamAggregate changeRequestMyTeamAggregate) {
            this.id = changeRequestMyTeamAggregate.getMyTeam().getId();
            this.members = changeRequestMyTeamAggregate.getMembers();
        }
    }
}
