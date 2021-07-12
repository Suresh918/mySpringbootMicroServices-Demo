package com.example.mirai.projectname.changerequestservice.changerequest.service;

import com.example.mirai.libraries.audit.service.AuditServiceDefaultInterface;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.CaseStatus;
import com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface;
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
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestDetail;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestDetailWithComments;
import com.example.mirai.projectname.changerequestservice.comment.model.ChangeRequestComment;
import com.example.mirai.projectname.changerequestservice.comment.service.ChangeRequestCommentService;
import com.example.mirai.projectname.changerequestservice.completebusinesscase.model.CompleteBusinessCase;
import com.example.mirai.projectname.changerequestservice.customerimpact.model.CustomerImpact;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.aggregate.ImpactAnalysisAggregate;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.aggregate.ImpactAnalysisDetailsAggregate;
import com.example.mirai.projectname.changerequestservice.migration.model.ChangeRequestCommentMigrate;
import com.example.mirai.projectname.changerequestservice.myteam.model.ChangeRequestMyTeam;
import com.example.mirai.projectname.changerequestservice.myteam.model.aggregate.ChangeRequestMyTeamDetailsAggregate;
import com.example.mirai.projectname.changerequestservice.preinstallimpact.model.PreinstallImpact;
import com.example.mirai.projectname.changerequestservice.scope.model.Scope;
import com.example.mirai.projectname.changerequestservice.solutiondefinition.model.SolutionDefinition;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
@EntityClass(ChangeRequest.class)
@Slf4j
@AllArgsConstructor
public class ChangeRequestMigrateServiceOld implements EntityServiceDefaultInterface, SecurityServiceDefaultInterface, AuditServiceDefaultInterface {

    private ChangeRequestStateMachine stateMachine;
    private AbacProcessor abacProcessor;
    private RbacProcessor rbacProcessor;
    private EntityACL acl;
    private PropertyACL pacl;
    private ChangeRequestService changeRequestService;
    private ChangeRequestCommentService changeRequestCommentService;

    @Transactional
    public ChangeRequestDetailWithComments createChangeRequestMigrateAggregate(ChangeRequestDetailWithComments request) {
        ChangeRequestDetailWithComments changeRequestDetailWithComments = new ChangeRequestDetailWithComments();
        ChangeRequestDetail changeRequestDetail = request.getChangeRequestDetail();
        List<ChangeRequestCommentMigrate> commentList = request.getComments();
        //process change request aggregate
        ChangeRequestAggregate changeRequestAggregate = new ChangeRequestAggregate();
        changeRequestAggregate.setDescription(getDescription(changeRequestDetail));
        changeRequestAggregate.setScope(Objects.isNull(changeRequestDetail.getScope()) ? new Scope() : changeRequestDetail.getScope());
        changeRequestAggregate.setSolutionDefinition(Objects.isNull(changeRequestDetail.getSolutionDefinition()) ? new SolutionDefinition() : changeRequestDetail.getSolutionDefinition());
        ImpactAnalysisAggregate impactAnalysisAggregate = new ImpactAnalysisAggregate();
        ImpactAnalysisDetailsAggregate impactAnalysisDetailsAggregate = new ImpactAnalysisDetailsAggregate();
        impactAnalysisAggregate.setGeneral(getImpactAnalysis(changeRequestDetail.getImpactAnalysis()));
        if (Objects.isNull(changeRequestDetail.getImpactAnalysis().getCustomerImpact())) {
            impactAnalysisDetailsAggregate.setCustomerImpact(new CustomerImpact());
        } else {
            impactAnalysisDetailsAggregate.setCustomerImpact(changeRequestDetail.getImpactAnalysis().getCustomerImpact());
        }
        if (Objects.isNull(changeRequestDetail.getImpactAnalysis().getPreinstallImpact())) {
            impactAnalysisDetailsAggregate.setPreinstallImpact(new PreinstallImpact());
        } else {
            impactAnalysisDetailsAggregate.setPreinstallImpact(changeRequestDetail.getImpactAnalysis().getPreinstallImpact());
        }
        if (Objects.isNull(changeRequestDetail.getCompleteBusinessCase())) {
            impactAnalysisDetailsAggregate.setCompleteBusinessCase(new CompleteBusinessCase());
        } else {
            impactAnalysisDetailsAggregate.setCompleteBusinessCase(changeRequestDetail.getCompleteBusinessCase());
        }
        impactAnalysisAggregate.setDetails(impactAnalysisDetailsAggregate);
        changeRequestAggregate.setImpactAnalysis(impactAnalysisAggregate);
        //process myteam aggregate
        ChangeRequestMyTeamDetailsAggregate changeRequestMyTeamAggregate = new ChangeRequestMyTeamDetailsAggregate();
        ChangeRequestMyTeam myTeam = new ChangeRequestMyTeam();
        myTeam.setChangeRequest(changeRequestAggregate.getDescription());
        changeRequestMyTeamAggregate.setMyTeam(myTeam);
        if (Objects.nonNull(request.getChangeRequestDetail().getMyTeam())) {
            changeRequestMyTeamAggregate.setMembers(request.getChangeRequestDetail().getMyTeam().getMembers());
        }
        changeRequestAggregate.setMyTeamDetails(changeRequestMyTeamAggregate);
        //create change request aggregate
        changeRequestAggregate  = (ChangeRequestAggregate) EntityServiceDefaultInterface.super.createRootAggregate(changeRequestAggregate);
        //update change request creator info
        ChangeRequest changeRequest = changeRequestAggregate.getDescription();
        if (Objects.nonNull(request.getChangeRequestDetail().getCreatedOn()))
            changeRequest.setCreatedOn(request.getChangeRequestDetail().getCreatedOn());
        if (Objects.nonNull(request.getChangeRequestDetail().getCreator()))
            changeRequest.setCreator(request.getChangeRequestDetail().getCreator());
        EntityServiceDefaultInterface.super.update(changeRequest);
        //create change request detail
        changeRequestDetailWithComments.setChangeRequestDetail(new ChangeRequestDetail(changeRequestAggregate));
        //process comments
        final Long changeRequestId = changeRequestAggregate.getDescription().getId();
        List<ChangeRequestCommentMigrate> createdComments = new ArrayList<>();
        if (Objects.nonNull(commentList)) {
            commentList.forEach(comment -> {
                try {
                    String commentOldId = comment.getCommentOldId();
                    ChangeRequestComment changeRequestComment1 = new ChangeRequestComment();
                    changeRequestComment1.setCommentText(comment.getCommentText());
                    changeRequestComment1.setStatus(comment.getStatus());
                    ChangeRequestComment changeRequestComment = (ChangeRequestComment) changeRequestCommentService.createCommentMigrate(changeRequestComment1, changeRequestId, ChangeRequest.class, comment.getCreatedOn(), comment.getCreator());
                    createdComments.add(new ChangeRequestCommentMigrate(changeRequestComment, commentOldId));
                } catch (ParseException exception) {
                    log.error("Parsing exception occurred", exception);
                }
            });
        }
        changeRequestDetailWithComments.setComments(createdComments);
        return changeRequestDetailWithComments;
    }

    private static ChangeRequest getDescription(ChangeRequestDetail changeRequestDetail) {
        ChangeRequest changeRequest = new ChangeRequest();
        changeRequest.setContexts(changeRequestDetail.getContexts());
        changeRequest.setTitle(changeRequestDetail.getTitle());
        changeRequest.setStatus(changeRequestDetail.getStatus());
        changeRequest.setIsSecure(changeRequestDetail.getIsSecure());
        changeRequest.setChangeSpecialist1(changeRequestDetail.getChangeSpecialist1());
        changeRequest.setChangeSpecialist2(changeRequestDetail.getChangeSpecialist2());
        changeRequest.setCreator(changeRequestDetail.getCreator());
        changeRequest.setCreatedOn(changeRequestDetail.getCreatedOn());
        changeRequest.setChangeBoards(changeRequestDetail.getChangeBoards());
        changeRequest.setChangeControlBoards(changeRequestDetail.getChangeControlBoards());
        changeRequest.setIssueTypes(changeRequestDetail.getIssueTypes());
        changeRequest.setChangeRequestType(changeRequestDetail.getChangeRequestType());
        changeRequest.setAnalysisPriority(changeRequestDetail.getAnalysisPriority());
        changeRequest.setProjectId(changeRequestDetail.getProjectId());
        changeRequest.setProductId(changeRequestDetail.getProductId());
        changeRequest.setFunctionalClusterId(changeRequestDetail.getFunctionalClusterId());
        changeRequest.setReasonsForChange(changeRequestDetail.getReasonsForChange());
        changeRequest.setProposedSolution(changeRequestDetail.getProposedSolution());
        changeRequest.setProblemDescription(changeRequestDetail.getProblemDescription());
        changeRequest.setRootCause(changeRequestDetail.getRootCause());
        changeRequest.setBenefitsOfChange(changeRequestDetail.getBenefitsOfChange());
        changeRequest.setImplementationPriority(changeRequestDetail.getImplementationPriority());
        changeRequest.setChangeBoardRuleSet(changeRequestDetail.getChangeBoardRuleSet());
        changeRequest.setRequirementsForImplementationPlan(changeRequestDetail.getRequirementsForImplementationPlan());
        changeRequest.setDependentChangeRequestIds(changeRequestDetail.getDependentChangeRequestIds());
        changeRequest.setExcessAndObsolescenceSavings(changeRequestDetail.getExcessAndObsolescenceSavings());
        return changeRequest;
    }

    private static ImpactAnalysis getImpactAnalysis(ChangeRequestDetail.ImpactAnalysisDetail impactAnalysisDetail) {
        ImpactAnalysis impactAnalysis = new ImpactAnalysis();
        impactAnalysis.setImpactOnExistingParts(impactAnalysisDetail.getImpactOnExistingParts());
        impactAnalysis.setCalendarDependency(impactAnalysisDetail.getCalendarDependency());
        impactAnalysis.setRecoveryTime(impactAnalysisDetail.getRecoveryTime());
        impactAnalysis.setUpgradePackages(impactAnalysisDetail.getUpgradePackages());
        impactAnalysis.setPrePostConditions(impactAnalysisDetail.getPrePostConditions());
        impactAnalysis.setUpgradeTime(impactAnalysisDetail.getUpgradeTime());
        impactAnalysis.setTotalInstancesAffected(impactAnalysisDetail.getTotalInstancesAffected());
        impactAnalysis.setCbpStrategies(impactAnalysisDetail.getCbpStrategies());
        impactAnalysis.setCbpStrategiesDetails(impactAnalysisDetail.getCbpStrategiesDetails());
        impactAnalysis.setDevelopmentLaborHours(impactAnalysisDetail.getDevelopmentLaborHours());
        impactAnalysis.setFcoTypes(impactAnalysisDetail.getFcoTypes());
        impactAnalysis.setImpactOnAvailability(impactAnalysisDetail.getImpactOnAvailability());
        impactAnalysis.setImpactOnAvailabilityDetails(impactAnalysisDetail.getImpactOnAvailabilityDetails());
        impactAnalysis.setTechRiskAssessmentSraDetails(impactAnalysisDetail.getTechRiskAssessmentSraDetails());
        impactAnalysis.setTechRiskAssessmentSra(impactAnalysisDetail.getTechRiskAssessmentSra());
        impactAnalysis.setImpactOnCycleTime(impactAnalysisDetail.getImpactOnCycleTime());
        impactAnalysis.setImpactOnCycleTimeDetails(impactAnalysisDetail.getImpactOnCycleTimeDetails());
        impactAnalysis.setImpactOnLaborHours(impactAnalysisDetail.getImpactOnLaborHours());
        impactAnalysis.setImpactOnLaborHoursDetails(impactAnalysisDetail.getImpactOnLaborHoursDetails());
        impactAnalysis.setImpactOnSequence(impactAnalysisDetail.getImpactOnSequence());
        impactAnalysis.setImpactOnSequenceDetails(impactAnalysisDetail.getImpactOnSequenceDetails());
        impactAnalysis.setImpactOnSystemLevelPerformance(impactAnalysisDetail.getImpactOnSystemLevelPerformance());
        impactAnalysis.setImpactOnSystemLevelPerformanceDetails(impactAnalysisDetail.getImpactOnSystemLevelPerformanceDetails());
        impactAnalysis.setImplementationRanges(impactAnalysisDetail.getImplementationRanges());
        impactAnalysis.setImplementationRangesDetails(impactAnalysisDetail.getImplementationRangesDetails());
        impactAnalysis.setInvestigationLaborHours(impactAnalysisDetail.getInvestigationLaborHours());
        impactAnalysis.setLiabilityRisks(impactAnalysisDetail.getLiabilityRisks());
        impactAnalysis.setMultiPlantImpact(impactAnalysisDetail.getMultiPlantImpact());
        impactAnalysis.setTargetedValidConfigurations(impactAnalysisDetail.getTargetedValidConfigurations());
        impactAnalysis.setTechRiskAssessmentFmea(impactAnalysisDetail.getTechRiskAssessmentFmea());
        impactAnalysis.setTechRiskAssessmentFmeaDetails(impactAnalysisDetail.getTechRiskAssessmentFmeaDetails());
        impactAnalysis.setPhaseOutSparesTools(impactAnalysisDetail.getPhaseOutSparesTools());
        impactAnalysis.setPhaseOutSparesToolsDetails(impactAnalysisDetail.getPhaseOutSparesToolsDetails());
        return impactAnalysis;
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

    @Override
    public AbacAwareInterface getABACAware() {
        return null;
    }

    @Override
    public RbacAwareInterface getRBACAware() {
        return null;
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
        return null;
    }
}
