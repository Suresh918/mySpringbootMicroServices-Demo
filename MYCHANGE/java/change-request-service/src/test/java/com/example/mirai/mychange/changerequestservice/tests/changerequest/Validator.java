package com.example.mirai.projectname.changerequestservice.tests.changerequest;


import com.example.mirai.libraries.comment.model.Comment;
import com.example.mirai.libraries.document.model.Document;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestStatus;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestAggregate;
import com.example.mirai.projectname.changerequestservice.comment.model.ChangeRequestComment;
import com.example.mirai.projectname.changerequestservice.completebusinesscase.model.CompleteBusinessCase;
import com.example.mirai.projectname.changerequestservice.customerimpact.model.CustomerImpact;
import com.example.mirai.projectname.changerequestservice.document.model.ChangeRequestCommentDocument;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
import com.example.mirai.projectname.changerequestservice.json.ChangeRequestCommentDocumentJson;
import com.example.mirai.projectname.changerequestservice.json.ChangeRequestCommentJson;
import com.example.mirai.projectname.changerequestservice.json.ChangeRequestMyTeamJson;
import com.example.mirai.projectname.changerequestservice.json.ExceptionResponse;
import com.example.mirai.projectname.changerequestservice.myteam.model.ChangeRequestMyTeam;
import com.example.mirai.projectname.changerequestservice.preinstallimpact.model.PreinstallImpact;
import com.example.mirai.projectname.changerequestservice.scope.model.Scope;
import com.example.mirai.projectname.changerequestservice.shared.util.Defaults;
import com.example.mirai.projectname.changerequestservice.solutiondefinition.model.SolutionDefinition;
import com.example.mirai.projectname.changerequestservice.tests.ExceptionValidator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class Validator {

    static void createChangeRequestIsSuccessful(ChangeRequestAggregate requestChangeRequest, ChangeRequest savedChangeRequest, ChangeRequest responseChangeRequest) {
        assertThat("ids are not same in response and saved change request", responseChangeRequest.getId(), equalTo(savedChangeRequest.getId()));
        assertThat("titles are not same in response and saved change request", responseChangeRequest.getTitle(), equalTo(savedChangeRequest.getTitle()));

        assertThat("Default title is New CR", responseChangeRequest.getTitle(), equalTo(Defaults.TITLE));
        assertThat("Default title is New CR", savedChangeRequest.getTitle(), equalTo(Defaults.TITLE));

        assertThat("Default secure is false", responseChangeRequest.getIsSecure(), equalTo(Defaults.SECURE));
        assertThat("Default secure is false", savedChangeRequest.getIsSecure(), equalTo(Defaults.SECURE));

        assertThat("Status is not draft", savedChangeRequest.getStatus(), equalTo(ChangeRequestStatus.DRAFTED.getStatusCode()));
    }

    public static void changeRequestJsonsAreSameWithoutComparingAuditAndStatus(ChangeRequest changeRequestBeforeCaseAction, ChangeRequest changeRequestAfterCaseAction) {
    }

    public static void unauthorizedExceptionAndChangeRequestDidNotChange(ChangeRequest changeRequestBeforeCaseAction, ChangeRequest changeRequestAfterCaseAction,
                                                                         com.example.mirai.projectname.changerequestservice.json.ExceptionResponse exceptionResponse,
                                                                         String path, Integer originalStatus) {
        ExceptionValidator.exceptionResponseIsUnauthorizedException(exceptionResponse, path);
        changeRequestAreSame(changeRequestBeforeCaseAction, changeRequestAfterCaseAction);
        assertThat("unauthorized case action has changed status", changeRequestAfterCaseAction.getStatus(), equalTo(originalStatus));
    }
    public static void unauthorizedExceptionAndSolutionDefinitionDidNotChange(SolutionDefinition solutionDefinitionBeforeUpdate, SolutionDefinition solutionDefinitionAfterUpdate,
                                                                              com.example.mirai.projectname.changerequestservice.json.ExceptionResponse exceptionResponse,
                                                                              String path, Integer originalStatus) {
        ExceptionValidator.exceptionResponseIsUnauthorizedException(exceptionResponse, path);
        solutionDefinitionAreSame(solutionDefinitionBeforeUpdate, solutionDefinitionAfterUpdate);
        assertThat("unauthorized case action has changed status", solutionDefinitionAfterUpdate.getChangeRequest().getStatus(), equalTo(originalStatus));
    }
    public static void unauthorizedExceptionAndPreinstallImpactDidNotChange(PreinstallImpact preinstallImpactBeforeUpdate, PreinstallImpact preinstallImpactAfterUpdate,
                                                                            com.example.mirai.projectname.changerequestservice.json.ExceptionResponse exceptionResponse,
                                                                            String path, Integer originalStatus) {
        ExceptionValidator.exceptionResponseIsUnauthorizedException(exceptionResponse, path);
        preinstallImpactAreSame(preinstallImpactBeforeUpdate, preinstallImpactAfterUpdate);
        assertThat("unauthorized case action has changed status", preinstallImpactAfterUpdate.getImpactAnalysis().getChangeRequest().getStatus(), equalTo(originalStatus));
    }
    public static void unauthorizedExceptionAndimpactAnalysisDidNotChange(ImpactAnalysis impactAnalysisBeforeUpdate, ImpactAnalysis impactAnalysisAfterUpdate,
                                                                          com.example.mirai.projectname.changerequestservice.json.ExceptionResponse exceptionResponse,
                                                                          String path, Integer originalStatus) {
        ExceptionValidator.exceptionResponseIsUnauthorizedException(exceptionResponse, path);
        impactAnalysisAreSame(impactAnalysisBeforeUpdate, impactAnalysisAfterUpdate);
        assertThat("unauthorized case action has changed status", impactAnalysisAfterUpdate.getChangeRequest().getStatus(), equalTo(originalStatus));
    }
    public static void unauthorizedExceptionAndcompleteBusinessCaseisDidNotChange(CompleteBusinessCase completeBusinessCaseBeforeUpdate, CompleteBusinessCase completeBusinessCaseAfterUpdate,
                                                                                  com.example.mirai.projectname.changerequestservice.json.ExceptionResponse exceptionResponse,
                                                                                  String path, Integer originalStatus) {
        ExceptionValidator.exceptionResponseIsUnauthorizedException(exceptionResponse, path);
        completeBusinessCaseAreSame(completeBusinessCaseBeforeUpdate, completeBusinessCaseAfterUpdate);
        assertThat("unauthorized case action has changed status", completeBusinessCaseAfterUpdate.getImpactAnalysis().getChangeRequest().getStatus(), equalTo(originalStatus));
    }
    public static void unauthorizedExceptionAndScopeisDidNotChange(Scope scopeBeforeUpdate, Scope scopeAfterUpdate,
                                                                   com.example.mirai.projectname.changerequestservice.json.ExceptionResponse exceptionResponse,
                                                                   String path, Integer originalStatus) {
        ExceptionValidator.exceptionResponseIsUnauthorizedException(exceptionResponse, path);
        scopeAreSame(scopeBeforeUpdate, scopeAfterUpdate);
        assertThat("unauthorized case action has changed status", scopeAfterUpdate.getChangeRequest().getStatus(), equalTo(originalStatus));
    }



    public static void unauthorizedExceptionAndCustomerImpactDidNotChange(CustomerImpact customerImpactBeforeUpdate, CustomerImpact customerImpactAfterUpdate,
                                                                          com.example.mirai.projectname.changerequestservice.json.ExceptionResponse exceptionResponse,
                                                                          String path, Integer originalStatus) {
        ExceptionValidator.exceptionResponseIsUnauthorizedException(exceptionResponse, path);
        customerImpactAreSame(customerImpactBeforeUpdate, customerImpactAfterUpdate);
        assertThat("unauthorized case action has changed status", customerImpactAfterUpdate.getImpactAnalysis().getChangeRequest().getStatus(), equalTo(originalStatus));
    }

    public static void solutionDefinitionAreSame(SolutionDefinition solutionDefinitionBeforeUpdate, SolutionDefinition solutionDefinitionAfterUpdate) {
        assertThat("statuses are  same", solutionDefinitionBeforeUpdate.getStatus(), equalTo(solutionDefinitionAfterUpdate.getStatus()));
        assertThat("TestAndReleaseStrategyDetails are same", solutionDefinitionBeforeUpdate.getTestAndReleaseStrategyDetails(), equalTo(solutionDefinitionAfterUpdate.getTestAndReleaseStrategyDetails()));
        assertThat("TestAndReleaseStrategyDetails are same", solutionDefinitionBeforeUpdate.getTestAndReleaseStrategyDetails(), equalTo(solutionDefinitionAfterUpdate.getTestAndReleaseStrategyDetails()));
    }
    public static void preinstallImpactAreSame(PreinstallImpact preinstallImpactBeforeUpdate, PreinstallImpact preinstallImpactAfterUpdate) {
        assertThat("statuses are  same", preinstallImpactBeforeUpdate.getStatus(), equalTo(preinstallImpactAfterUpdate.getStatus()));
        assertThat("getChangeIntroducesNew11NcDetails are same", preinstallImpactBeforeUpdate.getChangeIntroducesNew11NcDetails(), equalTo(preinstallImpactAfterUpdate.getChangeIntroducesNew11NcDetails()));
        assertThat("ImpactOnFacilityFlowsDetails are same", preinstallImpactBeforeUpdate.getImpactOnFacilityFlowsDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnFacilityFlowsDetails()));
        assertThat("ImpactOnCustomerFactoryLayoutDetails are same", preinstallImpactBeforeUpdate.getImpactOnCustomerFactoryLayoutDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnCustomerFactoryLayoutDetails()));
        assertThat("ImpactOnPreinstallInterConnectCablesDetails are same", preinstallImpactBeforeUpdate.getImpactOnPreinstallInterConnectCablesDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnPreinstallInterConnectCablesDetails()));
        assertThat("ImpactOnPreinstallInterConnectCablesDetails are same", preinstallImpactBeforeUpdate.getPreinstallImpactResult(), equalTo(preinstallImpactAfterUpdate.getPreinstallImpactResult()));
        assertThat("getChangeReplacesMentionedPartsDetails are same", preinstallImpactBeforeUpdate.getChangeReplacesMentionedPartsDetails(), equalTo(preinstallImpactAfterUpdate.getChangeReplacesMentionedPartsDetails()));
    }
    public static void impactAnalysisAreSame(ImpactAnalysis impactAnalysisBeforeUpdate, ImpactAnalysis impactAnalysisAfterUpdate) {
        assertThat("statuses are  same", impactAnalysisBeforeUpdate.getStatus(), equalTo(impactAnalysisAfterUpdate.getStatus()));
        assertThat("getImplementationRanges are same", impactAnalysisBeforeUpdate.getImplementationRanges(), equalTo(impactAnalysisBeforeUpdate.getImplementationRanges()));
        assertThat("getCbpStrategies are same", impactAnalysisBeforeUpdate.getCbpStrategies(), equalTo(impactAnalysisAfterUpdate.getCbpStrategies()));
        assertThat("getImpactOnAvailability are same", impactAnalysisBeforeUpdate.getImpactOnAvailability(), equalTo(impactAnalysisAfterUpdate.getImpactOnAvailability()));
        assertThat("getImpactOnAvailability are same", impactAnalysisBeforeUpdate.getImpactOnSystemLevelPerformance(), equalTo(impactAnalysisAfterUpdate.getImpactOnSystemLevelPerformance()));
        assertThat("getImpactOnExistingParts are same", impactAnalysisBeforeUpdate.getImpactOnExistingParts(), equalTo(impactAnalysisAfterUpdate.getImpactOnExistingParts()));
        assertThat("impactAnalysisAfterUpdate are same", impactAnalysisBeforeUpdate.getImpactOnSequenceDetails(), equalTo(impactAnalysisAfterUpdate.getImpactOnSequenceDetails()));
    }
    public static void completeBusinessCaseAreSame(CompleteBusinessCase completeBusinessCaseBeforeUpdate, CompleteBusinessCase completeBusinessCaseAfterUpdate) {
        assertThat("statuses are  same", completeBusinessCaseBeforeUpdate.getStatus(), equalTo(completeBusinessCaseAfterUpdate.getStatus()));
        assertThat("getexampleSavings are same", completeBusinessCaseBeforeUpdate.getexampleSavings(), equalTo(completeBusinessCaseAfterUpdate.getexampleSavings()));
        assertThat("getCustomerOpexSavings are same", completeBusinessCaseBeforeUpdate.getCustomerOpexSavings(), equalTo(completeBusinessCaseAfterUpdate.getCustomerOpexSavings()));
        assertThat("getImpactOnSystemLevelPerformance are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getCycleTimeRecurringCosts are same", completeBusinessCaseBeforeUpdate.getCycleTimeRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getCycleTimeRecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getFarmOutDevelopmentNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFarmOutDevelopmentNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFarmOutDevelopmentNonrecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getCustomerUptimeImprovementBenefits are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getFieldChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFieldChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFieldChangeOrderNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("getMaterialRecurringCosts are same", completeBusinessCaseBeforeUpdate.getMaterialRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getMaterialRecurringCosts()));
        assertThat("getPrototypeMaterialsNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getPrototypeMaterialsNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getPrototypeMaterialsNonrecurringCosts()));
        assertThat("getUpdateUpgradeProductDocumentationNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("getHardwareCommitment are same", completeBusinessCaseBeforeUpdate.getHardwareCommitment(), equalTo(completeBusinessCaseAfterUpdate.getHardwareCommitment()));

    }

    public static void scopeAreSame(Scope scopeBeforeUpdate, Scope scopeAfterUpdate) {
        assertThat("statuses are  same", scopeBeforeUpdate.getStatus(), equalTo(scopeAfterUpdate.getStatus()));
        assertThat("Parts are same", scopeBeforeUpdate.getParts(), equalTo(scopeAfterUpdate.getParts()));
        assertThat("Packaging are same", scopeBeforeUpdate.getPackaging(), equalTo(scopeAfterUpdate.getPackaging()));
        assertThat("Tooling are same", scopeBeforeUpdate.getTooling(), equalTo(scopeAfterUpdate.getTooling()));
        assertThat("Bop are same", scopeBeforeUpdate.getBop(), equalTo(scopeAfterUpdate.getBop()));
        assertThat("ScopeDetails are same", scopeBeforeUpdate.getScopeDetails(), equalTo(scopeAfterUpdate.getScopeDetails()));
        assertThat("PartDetail are same", scopeBeforeUpdate.getPartDetail().getServicePart(), equalTo(scopeAfterUpdate.getPartDetail().getServicePart()));
        assertThat("PackagingDetail are same", scopeBeforeUpdate.getPackagingDetail().getShippingPackaging(), equalTo(scopeAfterUpdate.getPackagingDetail().getShippingPackaging()));
        assertThat("ToolingDetail are same", scopeBeforeUpdate.getToolingDetail().getSupplierTooling(), equalTo(scopeAfterUpdate.getToolingDetail().getSupplierTooling()));

    }

    public static void customerImpactAreSame(CustomerImpact customerImpactBeforeUpdate, CustomerImpact customerImpactAfterUpdate) {
        assertThat("statuses are  same", customerImpactBeforeUpdate.getStatus(), equalTo(customerImpactAfterUpdate.getStatus()));
        assertThat("getImpactOnUserInterfacesDetails are same", customerImpactBeforeUpdate.getImpactOnUserInterfacesDetails(), equalTo(customerImpactAfterUpdate.getImpactOnUserInterfacesDetails()));
        assertThat("getChangeToCustomerImpactCriticalPartDetails are same", customerImpactBeforeUpdate.getChangeToCustomerImpactCriticalPartDetails(), equalTo(customerImpactAfterUpdate.getChangeToCustomerImpactCriticalPartDetails()));
        assertThat("getFcoUpgradeOptionCsrImplementationChange are same", customerImpactBeforeUpdate.getFcoUpgradeOptionCsrImplementationChange(), equalTo(customerImpactAfterUpdate.getFcoUpgradeOptionCsrImplementationChange()));
        assertThat("getFcoUpgradeOptionCsrImplementationChange are same", customerImpactBeforeUpdate.getFcoUpgradeOptionCsrImplementationChange(), equalTo(customerImpactAfterUpdate.getImpactOnWaferProcessEnvironment()));
        assertThat("getCustomerCommunicationDetails are same", customerImpactBeforeUpdate.getCustomerCommunicationDetails(), equalTo(customerImpactAfterUpdate.getCustomerCommunicationDetails()));
        assertThat("ChangeToProcessImpactingCustomerDetails are same", customerImpactBeforeUpdate.getChangeToProcessImpactingCustomerDetails(), equalTo(customerImpactAfterUpdate.getChangeToProcessImpactingCustomerDetails()));
        assertThat("ChangeToCustomerImpactCriticalPartDetails are same", customerImpactBeforeUpdate.getChangeToCustomerImpactCriticalPartDetails(), equalTo(customerImpactAfterUpdate.getChangeToCustomerImpactCriticalPartDetails()));
        assertThat("CustomerApprovalDetails are same", customerImpactBeforeUpdate.getCustomerApprovalDetails(), equalTo(customerImpactAfterUpdate.getCustomerApprovalDetails()));
    }

    public static void changeRequestAreSameWithoutComparingTitle(ChangeRequest changeRequest1, ChangeRequest changeRequest2) {
        //assertThat("title", changeRequest1.getTitle(), equalTo(changeRequest2.getTitle()));
        assertThat("ChangeSpecialist1 are same", changeRequest1.getChangeSpecialist1(), equalTo(changeRequest2.getChangeSpecialist1()));
        assertThat("ChangeSpecialist2 are  same", changeRequest1.getChangeSpecialist2(), equalTo(changeRequest2.getChangeSpecialist2()));
        assertThat("IsSecure are  same", changeRequest1.getIsSecure(), equalTo(changeRequest2.getIsSecure()));
        assertThat("ChangeControlBoards are  same", changeRequest1.getChangeControlBoards(), equalTo(changeRequest2.getChangeControlBoards()));
        assertThat("ChangeBoards are  same", changeRequest1.getChangeBoards(), equalTo(changeRequest2.getChangeBoards()));
        assertThat("IssueTypes are  same", changeRequest1.getIssueTypes(), equalTo(changeRequest2.getIssueTypes()));
        assertThat("ChangeRequestType  are  same", changeRequest1.getChangeRequestType(), equalTo(changeRequest2.getChangeRequestType()));
        assertThat("AnalysisPriority  are  same", changeRequest1.getAnalysisPriority(), equalTo(changeRequest2.getAnalysisPriority()));
        assertThat("ProjectId are  same", changeRequest1.getProjectId(), equalTo(changeRequest2.getProjectId()));
        assertThat("ProductId  are  same", changeRequest1.getProductId(), equalTo(changeRequest2.getProductId()));
        assertThat("FunctionalClusterId are  same", changeRequest1.getFunctionalClusterId(), equalTo(changeRequest2.getFunctionalClusterId()));
        assertThat("ReasonsForChange are  same", changeRequest1.getReasonsForChange(), equalTo(changeRequest2.getReasonsForChange()));
        assertThat("ProblemDescription are  same", changeRequest1.getProblemDescription(), equalTo(changeRequest2.getProblemDescription()));
        assertThat("ProposedSolution are  same", changeRequest1.getProposedSolution(), equalTo(changeRequest2.getProposedSolution()));
        assertThat("RootCause are  same", changeRequest1.getRootCause(), equalTo(changeRequest2.getRootCause()));
        assertThat("BenefitsOfChange are  same", changeRequest1.getBenefitsOfChange(), equalTo(changeRequest2.getBenefitsOfChange()));
        assertThat("ImplementationPriority are  same", changeRequest1.getImplementationPriority(), equalTo(changeRequest2.getImplementationPriority()));
        assertThat("RequirementsForImplementationPlan are  same", changeRequest1.getRequirementsForImplementationPlan(), equalTo(changeRequest2.getRequirementsForImplementationPlan()));
        assertThat("ChangeOwnerType  are  same", changeRequest1.getChangeOwnerType(), equalTo(changeRequest2.getChangeOwnerType()));
        assertThat("DependentChangeRequestIds are  same", changeRequest1.getDependentChangeRequestIds(), equalTo(changeRequest2.getDependentChangeRequestIds()));
    }

    public static void changeRequestAreSameWithoutComparingProblemDescription(ChangeRequest changeRequest1, ChangeRequest changeRequest2) {
        assertThat("title", changeRequest1.getTitle(), equalTo(changeRequest2.getTitle()));
        assertThat("ChangeSpecialist1 are same", changeRequest1.getChangeSpecialist1(), equalTo(changeRequest2.getChangeSpecialist1()));
        assertThat("ChangeSpecialist2 are  same", changeRequest1.getChangeSpecialist2(), equalTo(changeRequest2.getChangeSpecialist2()));
        assertThat("IsSecure are  same", changeRequest1.getIsSecure(), equalTo(changeRequest2.getIsSecure()));
        assertThat("ChangeControlBoards are  same", changeRequest1.getChangeControlBoards(), equalTo(changeRequest2.getChangeControlBoards()));
        assertThat("ChangeBoards are  same", changeRequest1.getChangeBoards(), equalTo(changeRequest2.getChangeBoards()));
        assertThat("IssueTypes are  same", changeRequest1.getIssueTypes(), equalTo(changeRequest2.getIssueTypes()));
        assertThat("ChangeRequestType  are  same", changeRequest1.getChangeRequestType(), equalTo(changeRequest2.getChangeRequestType()));
        assertThat("AnalysisPriority  are  same", changeRequest1.getAnalysisPriority(), equalTo(changeRequest2.getAnalysisPriority()));
        assertThat("ProjectId are  same", changeRequest1.getProjectId(), equalTo(changeRequest2.getProjectId()));
        assertThat("ProductId  are  same", changeRequest1.getProductId(), equalTo(changeRequest2.getProductId()));
        assertThat("FunctionalClusterId are  same", changeRequest1.getFunctionalClusterId(), equalTo(changeRequest2.getFunctionalClusterId()));
        assertThat("ReasonsForChange are  same", changeRequest1.getReasonsForChange(), equalTo(changeRequest2.getReasonsForChange()));
        //assertThat("ProblemDescription are  same", changeRequest1.getProblemDescription(), equalTo(changeRequest2.getProblemDescription()));
        assertThat("ProposedSolution are  same", changeRequest1.getProposedSolution(), equalTo(changeRequest2.getProposedSolution()));
        assertThat("RootCause are  same", changeRequest1.getRootCause(), equalTo(changeRequest2.getRootCause()));
        assertThat("BenefitsOfChange are  same", changeRequest1.getBenefitsOfChange(), equalTo(changeRequest2.getBenefitsOfChange()));
        assertThat("ImplementationPriority are  same", changeRequest1.getImplementationPriority(), equalTo(changeRequest2.getImplementationPriority()));
        assertThat("RequirementsForImplementationPlan are  same", changeRequest1.getRequirementsForImplementationPlan(), equalTo(changeRequest2.getRequirementsForImplementationPlan()));
        assertThat("ChangeOwnerType  are  same", changeRequest1.getChangeOwnerType(), equalTo(changeRequest2.getChangeOwnerType()));
        assertThat("DependentChangeRequestIds are  same", changeRequest1.getDependentChangeRequestIds(), equalTo(changeRequest2.getDependentChangeRequestIds()));
    }

    public static void changeRequestAreSameWithoutComparingChangeSpecialist1(ChangeRequest changeRequest1, ChangeRequest changeRequest2) {
        assertThat("title", changeRequest1.getTitle(), equalTo(changeRequest2.getTitle()));
        //assertThat("ChangeSpecialist1 are same", changeRequest1.getChangeSpecialist1(), equalTo(changeRequest2.getChangeSpecialist1()));
        assertThat("ChangeSpecialist2 are  same", changeRequest1.getChangeSpecialist2(), equalTo(changeRequest2.getChangeSpecialist2()));
        assertThat("IsSecure are  same", changeRequest1.getIsSecure(), equalTo(changeRequest2.getIsSecure()));
        assertThat("ChangeControlBoards are  same", changeRequest1.getChangeControlBoards(), equalTo(changeRequest2.getChangeControlBoards()));
        assertThat("ChangeBoards are  same", changeRequest1.getChangeBoards(), equalTo(changeRequest2.getChangeBoards()));
        assertThat("IssueTypes are  same", changeRequest1.getIssueTypes(), equalTo(changeRequest2.getIssueTypes()));
        assertThat("ChangeRequestType  are  same", changeRequest1.getChangeRequestType(), equalTo(changeRequest2.getChangeRequestType()));
        assertThat("AnalysisPriority  are  same", changeRequest1.getAnalysisPriority(), equalTo(changeRequest2.getAnalysisPriority()));
        assertThat("ProjectId are  same", changeRequest1.getProjectId(), equalTo(changeRequest2.getProjectId()));
        assertThat("ProductId  are  same", changeRequest1.getProductId(), equalTo(changeRequest2.getProductId()));
        assertThat("FunctionalClusterId are  same", changeRequest1.getFunctionalClusterId(), equalTo(changeRequest2.getFunctionalClusterId()));
        assertThat("ReasonsForChange are  same", changeRequest1.getReasonsForChange(), equalTo(changeRequest2.getReasonsForChange()));
        assertThat("ProblemDescription are  same", changeRequest1.getProblemDescription(), equalTo(changeRequest2.getProblemDescription()));
        assertThat("ProposedSolution are  same", changeRequest1.getProposedSolution(), equalTo(changeRequest2.getProposedSolution()));
        assertThat("RootCause are  same", changeRequest1.getRootCause(), equalTo(changeRequest2.getRootCause()));
        assertThat("BenefitsOfChange are  same", changeRequest1.getBenefitsOfChange(), equalTo(changeRequest2.getBenefitsOfChange()));
        assertThat("ImplementationPriority are  same", changeRequest1.getImplementationPriority(), equalTo(changeRequest2.getImplementationPriority()));
        assertThat("RequirementsForImplementationPlan are  same", changeRequest1.getRequirementsForImplementationPlan(), equalTo(changeRequest2.getRequirementsForImplementationPlan()));
        assertThat("ChangeOwnerType  are  same", changeRequest1.getChangeOwnerType(), equalTo(changeRequest2.getChangeOwnerType()));
        assertThat("DependentChangeRequestIds are  same", changeRequest1.getDependentChangeRequestIds(), equalTo(changeRequest2.getDependentChangeRequestIds()));
    }

    public static void changeRequestAreSameWithoutComparingReasonsForChange(ChangeRequest changeRequest1, ChangeRequest changeRequest2) {
        assertThat("title", changeRequest1.getTitle(), equalTo(changeRequest2.getTitle()));
        assertThat("ChangeSpecialist1 are same", changeRequest1.getChangeSpecialist1(), equalTo(changeRequest2.getChangeSpecialist1()));
        assertThat("ChangeSpecialist2 are  same", changeRequest1.getChangeSpecialist2(), equalTo(changeRequest2.getChangeSpecialist2()));
        assertThat("IsSecure are  same", changeRequest1.getIsSecure(), equalTo(changeRequest2.getIsSecure()));
        assertThat("ChangeControlBoards are  same", changeRequest1.getChangeControlBoards(), equalTo(changeRequest2.getChangeControlBoards()));
        assertThat("ChangeBoards are  same", changeRequest1.getChangeBoards(), equalTo(changeRequest2.getChangeBoards()));
        assertThat("IssueTypes are  same", changeRequest1.getIssueTypes(), equalTo(changeRequest2.getIssueTypes()));
        assertThat("ChangeRequestType  are  same", changeRequest1.getChangeRequestType(), equalTo(changeRequest2.getChangeRequestType()));
        assertThat("AnalysisPriority  are  same", changeRequest1.getAnalysisPriority(), equalTo(changeRequest2.getAnalysisPriority()));
        assertThat("ProjectId are  same", changeRequest1.getProjectId(), equalTo(changeRequest2.getProjectId()));
        assertThat("ProductId  are  same", changeRequest1.getProductId(), equalTo(changeRequest2.getProductId()));
        assertThat("FunctionalClusterId are  same", changeRequest1.getFunctionalClusterId(), equalTo(changeRequest2.getFunctionalClusterId()));
        //assertThat("ReasonsForChange are  same", changeRequest1.getReasonsForChange(), equalTo(changeRequest2.getReasonsForChange()));
        assertThat("ProblemDescription are  same", changeRequest1.getProblemDescription(), equalTo(changeRequest2.getProblemDescription()));
        assertThat("ProposedSolution are  same", changeRequest1.getProposedSolution(), equalTo(changeRequest2.getProposedSolution()));
        assertThat("RootCause are  same", changeRequest1.getRootCause(), equalTo(changeRequest2.getRootCause()));
        assertThat("BenefitsOfChange are  same", changeRequest1.getBenefitsOfChange(), equalTo(changeRequest2.getBenefitsOfChange()));
        assertThat("ImplementationPriority are  same", changeRequest1.getImplementationPriority(), equalTo(changeRequest2.getImplementationPriority()));
        assertThat("RequirementsForImplementationPlan are  same", changeRequest1.getRequirementsForImplementationPlan(), equalTo(changeRequest2.getRequirementsForImplementationPlan()));
        assertThat("ChangeOwnerType  are  same", changeRequest1.getChangeOwnerType(), equalTo(changeRequest2.getChangeOwnerType()));
        assertThat("DependentChangeRequestIds are  same", changeRequest1.getDependentChangeRequestIds(), equalTo(changeRequest2.getDependentChangeRequestIds()));
    }

    public static void changeRequestAreSameWithoutComparingProposedSolution(ChangeRequest changeRequest1, ChangeRequest changeRequest2) {
        assertThat("title", changeRequest1.getTitle(), equalTo(changeRequest2.getTitle()));
        assertThat("ChangeSpecialist1 are same", changeRequest1.getChangeSpecialist1(), equalTo(changeRequest2.getChangeSpecialist1()));
        assertThat("ChangeSpecialist2 are  same", changeRequest1.getChangeSpecialist2(), equalTo(changeRequest2.getChangeSpecialist2()));
        assertThat("IsSecure are  same", changeRequest1.getIsSecure(), equalTo(changeRequest2.getIsSecure()));
        assertThat("ChangeControlBoards are  same", changeRequest1.getChangeControlBoards(), equalTo(changeRequest2.getChangeControlBoards()));
        assertThat("ChangeBoards are  same", changeRequest1.getChangeBoards(), equalTo(changeRequest2.getChangeBoards()));
        assertThat("IssueTypes are  same", changeRequest1.getIssueTypes(), equalTo(changeRequest2.getIssueTypes()));
        assertThat("ChangeRequestType  are  same", changeRequest1.getChangeRequestType(), equalTo(changeRequest2.getChangeRequestType()));
        assertThat("AnalysisPriority  are  same", changeRequest1.getAnalysisPriority(), equalTo(changeRequest2.getAnalysisPriority()));
        assertThat("ProjectId are  same", changeRequest1.getProjectId(), equalTo(changeRequest2.getProjectId()));
        assertThat("ProductId  are  same", changeRequest1.getProductId(), equalTo(changeRequest2.getProductId()));
        assertThat("FunctionalClusterId are  same", changeRequest1.getFunctionalClusterId(), equalTo(changeRequest2.getFunctionalClusterId()));
        assertThat("ReasonsForChange are  same", changeRequest1.getReasonsForChange(), equalTo(changeRequest2.getReasonsForChange()));
        assertThat("ProblemDescription are  same", changeRequest1.getProblemDescription(), equalTo(changeRequest2.getProblemDescription()));
        //assertThat("ProposedSolution are  same", changeRequest1.getProposedSolution(), equalTo(changeRequest2.getProposedSolution()));
        assertThat("RootCause are  same", changeRequest1.getRootCause(), equalTo(changeRequest2.getRootCause()));
        assertThat("BenefitsOfChange are  same", changeRequest1.getBenefitsOfChange(), equalTo(changeRequest2.getBenefitsOfChange()));
        assertThat("ImplementationPriority are  same", changeRequest1.getImplementationPriority(), equalTo(changeRequest2.getImplementationPriority()));
        assertThat("RequirementsForImplementationPlan are  same", changeRequest1.getRequirementsForImplementationPlan(), equalTo(changeRequest2.getRequirementsForImplementationPlan()));
        assertThat("ChangeOwnerType  are  same", changeRequest1.getChangeOwnerType(), equalTo(changeRequest2.getChangeOwnerType()));
        assertThat("DependentChangeRequestIds are  same", changeRequest1.getDependentChangeRequestIds(), equalTo(changeRequest2.getDependentChangeRequestIds()));
    }

    public static void changeRequestAreSameWithoutComparingisSecure(ChangeRequest changeRequest1, ChangeRequest changeRequest2) {
        assertThat("title", changeRequest1.getTitle(), equalTo(changeRequest2.getTitle()));
        assertThat("ChangeSpecialist1 are same", changeRequest1.getChangeSpecialist1(), equalTo(changeRequest2.getChangeSpecialist1()));
        assertThat("ChangeSpecialist2 are  same", changeRequest1.getChangeSpecialist2(), equalTo(changeRequest2.getChangeSpecialist2()));
        //assertThat("IsSecure are  same", changeRequest1.getIsSecure(), equalTo(changeRequest2.getIsSecure()));
        assertThat("ChangeControlBoards are  same", changeRequest1.getChangeControlBoards(), equalTo(changeRequest2.getChangeControlBoards()));
        assertThat("ChangeBoards are  same", changeRequest1.getChangeBoards(), equalTo(changeRequest2.getChangeBoards()));
        assertThat("IssueTypes are  same", changeRequest1.getIssueTypes(), equalTo(changeRequest2.getIssueTypes()));
        assertThat("ChangeRequestType  are  same", changeRequest1.getChangeRequestType(), equalTo(changeRequest2.getChangeRequestType()));
        assertThat("AnalysisPriority  are  same", changeRequest1.getAnalysisPriority(), equalTo(changeRequest2.getAnalysisPriority()));
        assertThat("ProjectId are  same", changeRequest1.getProjectId(), equalTo(changeRequest2.getProjectId()));
        assertThat("ProductId  are  same", changeRequest1.getProductId(), equalTo(changeRequest2.getProductId()));
        assertThat("FunctionalClusterId are  same", changeRequest1.getFunctionalClusterId(), equalTo(changeRequest2.getFunctionalClusterId()));
        assertThat("ReasonsForChange are  same", changeRequest1.getReasonsForChange(), equalTo(changeRequest2.getReasonsForChange()));
        assertThat("ProblemDescription are  same", changeRequest1.getProblemDescription(), equalTo(changeRequest2.getProblemDescription()));
        assertThat("ProposedSolution are  same", changeRequest1.getProposedSolution(), equalTo(changeRequest2.getProposedSolution()));
        assertThat("RootCause are  same", changeRequest1.getRootCause(), equalTo(changeRequest2.getRootCause()));
        assertThat("BenefitsOfChange are  same", changeRequest1.getBenefitsOfChange(), equalTo(changeRequest2.getBenefitsOfChange()));
        assertThat("ImplementationPriority are  same", changeRequest1.getImplementationPriority(), equalTo(changeRequest2.getImplementationPriority()));
        assertThat("RequirementsForImplementationPlan are  same", changeRequest1.getRequirementsForImplementationPlan(), equalTo(changeRequest2.getRequirementsForImplementationPlan()));
        assertThat("ChangeOwnerType  are  same", changeRequest1.getChangeOwnerType(), equalTo(changeRequest2.getChangeOwnerType()));
        assertThat("DependentChangeRequestIds are  same", changeRequest1.getDependentChangeRequestIds(), equalTo(changeRequest2.getDependentChangeRequestIds()));
    }

    public static void changeRequestAreSameWithoutComparingChangeSpecialist2(ChangeRequest changeRequest1, ChangeRequest changeRequest2) {
        assertThat("title", changeRequest1.getTitle(), equalTo(changeRequest2.getTitle()));
        assertThat("ChangeSpecialist1 are same", changeRequest1.getChangeSpecialist1(), equalTo(changeRequest2.getChangeSpecialist1()));
        // assertThat("ChangeSpecialist2 are  same", changeRequest1.getChangeSpecialist2(), equalTo(changeRequest2.getChangeSpecialist2()));
        assertThat("IsSecure are  same", changeRequest1.getIsSecure(), equalTo(changeRequest2.getIsSecure()));
        assertThat("ChangeControlBoards are  same", changeRequest1.getChangeControlBoards(), equalTo(changeRequest2.getChangeControlBoards()));
        assertThat("ChangeBoards are  same", changeRequest1.getChangeBoards(), equalTo(changeRequest2.getChangeBoards()));
        assertThat("IssueTypes are  same", changeRequest1.getIssueTypes(), equalTo(changeRequest2.getIssueTypes()));
        assertThat("ChangeRequestType  are  same", changeRequest1.getChangeRequestType(), equalTo(changeRequest2.getChangeRequestType()));
        assertThat("AnalysisPriority  are  same", changeRequest1.getAnalysisPriority(), equalTo(changeRequest2.getAnalysisPriority()));
        assertThat("ProjectId are  same", changeRequest1.getProjectId(), equalTo(changeRequest2.getProjectId()));
        assertThat("ProductId  are  same", changeRequest1.getProductId(), equalTo(changeRequest2.getProductId()));
        assertThat("FunctionalClusterId are  same", changeRequest1.getFunctionalClusterId(), equalTo(changeRequest2.getFunctionalClusterId()));
        assertThat("ReasonsForChange are  same", changeRequest1.getReasonsForChange(), equalTo(changeRequest2.getReasonsForChange()));
        assertThat("ProblemDescription are  same", changeRequest1.getProblemDescription(), equalTo(changeRequest2.getProblemDescription()));
        assertThat("ProposedSolution are  same", changeRequest1.getProposedSolution(), equalTo(changeRequest2.getProposedSolution()));
        assertThat("RootCause are  same", changeRequest1.getRootCause(), equalTo(changeRequest2.getRootCause()));
        assertThat("BenefitsOfChange are  same", changeRequest1.getBenefitsOfChange(), equalTo(changeRequest2.getBenefitsOfChange()));
        assertThat("ImplementationPriority are  same", changeRequest1.getImplementationPriority(), equalTo(changeRequest2.getImplementationPriority()));
        assertThat("RequirementsForImplementationPlan are  same", changeRequest1.getRequirementsForImplementationPlan(), equalTo(changeRequest2.getRequirementsForImplementationPlan()));
        assertThat("ChangeOwnerType  are  same", changeRequest1.getChangeOwnerType(), equalTo(changeRequest2.getChangeOwnerType()));
        assertThat("DependentChangeRequestIds are  same", changeRequest1.getDependentChangeRequestIds(), equalTo(changeRequest2.getDependentChangeRequestIds()));
    }

    public static void changeRequestAreSameWithoutComparingchangeControlBoards(ChangeRequest changeRequest1, ChangeRequest changeRequest2) {
        assertThat("title", changeRequest1.getTitle(), equalTo(changeRequest2.getTitle()));
        assertThat("ChangeSpecialist1 are same", changeRequest1.getChangeSpecialist1(), equalTo(changeRequest2.getChangeSpecialist1()));
        assertThat("ChangeSpecialist2 are  same", changeRequest1.getChangeSpecialist2(), equalTo(changeRequest2.getChangeSpecialist2()));
        assertThat("IsSecure are  same", changeRequest1.getIsSecure(), equalTo(changeRequest2.getIsSecure()));
        //assertThat("ChangeControlBoards are  same", changeRequest1.getChangeControlBoards(), equalTo(changeRequest2.getChangeControlBoards()));
        assertThat("ChangeBoards are  same", changeRequest1.getChangeBoards(), equalTo(changeRequest2.getChangeBoards()));
        assertThat("IssueTypes are  same", changeRequest1.getIssueTypes(), equalTo(changeRequest2.getIssueTypes()));
        assertThat("ChangeRequestType  are  same", changeRequest1.getChangeRequestType(), equalTo(changeRequest2.getChangeRequestType()));
        assertThat("AnalysisPriority  are  same", changeRequest1.getAnalysisPriority(), equalTo(changeRequest2.getAnalysisPriority()));
        assertThat("ProjectId are  same", changeRequest1.getProjectId(), equalTo(changeRequest2.getProjectId()));
        assertThat("ProductId  are  same", changeRequest1.getProductId(), equalTo(changeRequest2.getProductId()));
        assertThat("FunctionalClusterId are  same", changeRequest1.getFunctionalClusterId(), equalTo(changeRequest2.getFunctionalClusterId()));
        assertThat("ReasonsForChange are  same", changeRequest1.getReasonsForChange(), equalTo(changeRequest2.getReasonsForChange()));
        assertThat("ProblemDescription are  same", changeRequest1.getProblemDescription(), equalTo(changeRequest2.getProblemDescription()));
        assertThat("ProposedSolution are  same", changeRequest1.getProposedSolution(), equalTo(changeRequest2.getProposedSolution()));
        assertThat("RootCause are  same", changeRequest1.getRootCause(), equalTo(changeRequest2.getRootCause()));
        assertThat("BenefitsOfChange are  same", changeRequest1.getBenefitsOfChange(), equalTo(changeRequest2.getBenefitsOfChange()));
        assertThat("ImplementationPriority are  same", changeRequest1.getImplementationPriority(), equalTo(changeRequest2.getImplementationPriority()));
        assertThat("RequirementsForImplementationPlan are  same", changeRequest1.getRequirementsForImplementationPlan(), equalTo(changeRequest2.getRequirementsForImplementationPlan()));
        assertThat("ChangeOwnerType  are  same", changeRequest1.getChangeOwnerType(), equalTo(changeRequest2.getChangeOwnerType()));
        assertThat("DependentChangeRequestIds are  same", changeRequest1.getDependentChangeRequestIds(), equalTo(changeRequest2.getDependentChangeRequestIds()));
    }

    public static void changeRequestAreSameWithoutComparingChangeBoards(ChangeRequest changeRequest1, ChangeRequest changeRequest2) {
        assertThat("title", changeRequest1.getTitle(), equalTo(changeRequest2.getTitle()));
        assertThat("ChangeSpecialist1 are same", changeRequest1.getChangeSpecialist1(), equalTo(changeRequest2.getChangeSpecialist1()));
        assertThat("ChangeSpecialist2 are  same", changeRequest1.getChangeSpecialist2(), equalTo(changeRequest2.getChangeSpecialist2()));
        assertThat("IsSecure are  same", changeRequest1.getIsSecure(), equalTo(changeRequest2.getIsSecure()));
        assertThat("ChangeControlBoards are  same", changeRequest1.getChangeControlBoards(), equalTo(changeRequest2.getChangeControlBoards()));
        //assertThat("ChangeBoards are  same", changeRequest1.getChangeBoards(), equalTo(changeRequest2.getChangeBoards()));
        assertThat("IssueTypes are  same", changeRequest1.getIssueTypes(), equalTo(changeRequest2.getIssueTypes()));
        assertThat("ChangeRequestType  are  same", changeRequest1.getChangeRequestType(), equalTo(changeRequest2.getChangeRequestType()));
        assertThat("AnalysisPriority  are  same", changeRequest1.getAnalysisPriority(), equalTo(changeRequest2.getAnalysisPriority()));
        assertThat("ProjectId are  same", changeRequest1.getProjectId(), equalTo(changeRequest2.getProjectId()));
        assertThat("ProductId  are  same", changeRequest1.getProductId(), equalTo(changeRequest2.getProductId()));
        assertThat("FunctionalClusterId are  same", changeRequest1.getFunctionalClusterId(), equalTo(changeRequest2.getFunctionalClusterId()));
        assertThat("ReasonsForChange are  same", changeRequest1.getReasonsForChange(), equalTo(changeRequest2.getReasonsForChange()));
        assertThat("ProblemDescription are  same", changeRequest1.getProblemDescription(), equalTo(changeRequest2.getProblemDescription()));
        assertThat("ProposedSolution are  same", changeRequest1.getProposedSolution(), equalTo(changeRequest2.getProposedSolution()));
        assertThat("RootCause are  same", changeRequest1.getRootCause(), equalTo(changeRequest2.getRootCause()));
        assertThat("BenefitsOfChange are  same", changeRequest1.getBenefitsOfChange(), equalTo(changeRequest2.getBenefitsOfChange()));
        assertThat("ImplementationPriority are  same", changeRequest1.getImplementationPriority(), equalTo(changeRequest2.getImplementationPriority()));
        assertThat("RequirementsForImplementationPlan are  same", changeRequest1.getRequirementsForImplementationPlan(), equalTo(changeRequest2.getRequirementsForImplementationPlan()));
        assertThat("ChangeOwnerType  are  same", changeRequest1.getChangeOwnerType(), equalTo(changeRequest2.getChangeOwnerType()));
        assertThat("DependentChangeRequestIds are  same", changeRequest1.getDependentChangeRequestIds(), equalTo(changeRequest2.getDependentChangeRequestIds()));
    }

    public static void changeRequestAreSameWithoutComparingIssueTypes(ChangeRequest changeRequest1, ChangeRequest changeRequest2) {
        assertThat("title", changeRequest1.getTitle(), equalTo(changeRequest2.getTitle()));
        assertThat("ChangeSpecialist1 are same", changeRequest1.getChangeSpecialist1(), equalTo(changeRequest2.getChangeSpecialist1()));
        assertThat("ChangeSpecialist2 are  same", changeRequest1.getChangeSpecialist2(), equalTo(changeRequest2.getChangeSpecialist2()));
        assertThat("IsSecure are  same", changeRequest1.getIsSecure(), equalTo(changeRequest2.getIsSecure()));
        assertThat("ChangeControlBoards are  same", changeRequest1.getChangeControlBoards(), equalTo(changeRequest2.getChangeControlBoards()));
        assertThat("ChangeBoards are  same", changeRequest1.getChangeBoards(), equalTo(changeRequest2.getChangeBoards()));
        //assertThat("IssueTypes are  same", changeRequest1.getIssueTypes(), equalTo(changeRequest2.getIssueTypes()));
        assertThat("ChangeRequestType  are  same", changeRequest1.getChangeRequestType(), equalTo(changeRequest2.getChangeRequestType()));
        assertThat("AnalysisPriority  are  same", changeRequest1.getAnalysisPriority(), equalTo(changeRequest2.getAnalysisPriority()));
        assertThat("ProjectId are  same", changeRequest1.getProjectId(), equalTo(changeRequest2.getProjectId()));
        assertThat("ProductId  are  same", changeRequest1.getProductId(), equalTo(changeRequest2.getProductId()));
        assertThat("FunctionalClusterId are  same", changeRequest1.getFunctionalClusterId(), equalTo(changeRequest2.getFunctionalClusterId()));
        assertThat("ReasonsForChange are  same", changeRequest1.getReasonsForChange(), equalTo(changeRequest2.getReasonsForChange()));
        assertThat("ProblemDescription are  same", changeRequest1.getProblemDescription(), equalTo(changeRequest2.getProblemDescription()));
        assertThat("ProposedSolution are  same", changeRequest1.getProposedSolution(), equalTo(changeRequest2.getProposedSolution()));
        assertThat("RootCause are  same", changeRequest1.getRootCause(), equalTo(changeRequest2.getRootCause()));
        assertThat("BenefitsOfChange are  same", changeRequest1.getBenefitsOfChange(), equalTo(changeRequest2.getBenefitsOfChange()));
        assertThat("ImplementationPriority are  same", changeRequest1.getImplementationPriority(), equalTo(changeRequest2.getImplementationPriority()));
        assertThat("RequirementsForImplementationPlan are  same", changeRequest1.getRequirementsForImplementationPlan(), equalTo(changeRequest2.getRequirementsForImplementationPlan()));
        assertThat("ChangeOwnerType  are  same", changeRequest1.getChangeOwnerType(), equalTo(changeRequest2.getChangeOwnerType()));
        assertThat("DependentChangeRequestIds are  same", changeRequest1.getDependentChangeRequestIds(), equalTo(changeRequest2.getDependentChangeRequestIds()));
    }

    public static void changeRequestAreSameWithoutComparingChangeRequestType(ChangeRequest changeRequest1, ChangeRequest changeRequest2) {
        assertThat("title", changeRequest1.getTitle(), equalTo(changeRequest2.getTitle()));
        assertThat("ChangeSpecialist1 are same", changeRequest1.getChangeSpecialist1(), equalTo(changeRequest2.getChangeSpecialist1()));
        assertThat("ChangeSpecialist2 are  same", changeRequest1.getChangeSpecialist2(), equalTo(changeRequest2.getChangeSpecialist2()));
        assertThat("IsSecure are  same", changeRequest1.getIsSecure(), equalTo(changeRequest2.getIsSecure()));
        assertThat("ChangeControlBoards are  same", changeRequest1.getChangeControlBoards(), equalTo(changeRequest2.getChangeControlBoards()));
        assertThat("ChangeBoards are  same", changeRequest1.getChangeBoards(), equalTo(changeRequest2.getChangeBoards()));
        assertThat("IssueTypes are  same", changeRequest1.getIssueTypes(), equalTo(changeRequest2.getIssueTypes()));
        //assertThat("ChangeRequestType  are  same", changeRequest1.getChangeRequestType(), equalTo(changeRequest2.getChangeRequestType()));
        assertThat("AnalysisPriority  are  same", changeRequest1.getAnalysisPriority(), equalTo(changeRequest2.getAnalysisPriority()));
        assertThat("ProjectId are  same", changeRequest1.getProjectId(), equalTo(changeRequest2.getProjectId()));
        assertThat("ProductId  are  same", changeRequest1.getProductId(), equalTo(changeRequest2.getProductId()));
        assertThat("FunctionalClusterId are  same", changeRequest1.getFunctionalClusterId(), equalTo(changeRequest2.getFunctionalClusterId()));
        assertThat("ReasonsForChange are  same", changeRequest1.getReasonsForChange(), equalTo(changeRequest2.getReasonsForChange()));
        assertThat("ProblemDescription are  same", changeRequest1.getProblemDescription(), equalTo(changeRequest2.getProblemDescription()));
        assertThat("ProposedSolution are  same", changeRequest1.getProposedSolution(), equalTo(changeRequest2.getProposedSolution()));
        assertThat("RootCause are  same", changeRequest1.getRootCause(), equalTo(changeRequest2.getRootCause()));
        assertThat("BenefitsOfChange are  same", changeRequest1.getBenefitsOfChange(), equalTo(changeRequest2.getBenefitsOfChange()));
        assertThat("ImplementationPriority are  same", changeRequest1.getImplementationPriority(), equalTo(changeRequest2.getImplementationPriority()));
        assertThat("RequirementsForImplementationPlan are  same", changeRequest1.getRequirementsForImplementationPlan(), equalTo(changeRequest2.getRequirementsForImplementationPlan()));
        assertThat("ChangeOwnerType  are  same", changeRequest1.getChangeOwnerType(), equalTo(changeRequest2.getChangeOwnerType()));
        assertThat("DependentChangeRequestIds are  same", changeRequest1.getDependentChangeRequestIds(), equalTo(changeRequest2.getDependentChangeRequestIds()));
    }

    public static void changeRequestAreSameWithoutComparingAnalysisPriority(ChangeRequest changeRequest1, ChangeRequest changeRequest2) {
        assertThat("title", changeRequest1.getTitle(), equalTo(changeRequest2.getTitle()));
        assertThat("ChangeSpecialist1 are same", changeRequest1.getChangeSpecialist1(), equalTo(changeRequest2.getChangeSpecialist1()));
        assertThat("ChangeSpecialist2 are  same", changeRequest1.getChangeSpecialist2(), equalTo(changeRequest2.getChangeSpecialist2()));
        assertThat("IsSecure are  same", changeRequest1.getIsSecure(), equalTo(changeRequest2.getIsSecure()));
        assertThat("ChangeControlBoards are  same", changeRequest1.getChangeControlBoards(), equalTo(changeRequest2.getChangeControlBoards()));
        assertThat("ChangeBoards are  same", changeRequest1.getChangeBoards(), equalTo(changeRequest2.getChangeBoards()));
        assertThat("IssueTypes are  same", changeRequest1.getIssueTypes(), equalTo(changeRequest2.getIssueTypes()));
        assertThat("ChangeRequestType  are  same", changeRequest1.getChangeRequestType(), equalTo(changeRequest2.getChangeRequestType()));
        //assertThat("AnalysisPriority  are  same", changeRequest1.getAnalysisPriority(), equalTo(changeRequest2.getAnalysisPriority()));
        assertThat("ProjectId are  same", changeRequest1.getProjectId(), equalTo(changeRequest2.getProjectId()));
        assertThat("ProductId  are  same", changeRequest1.getProductId(), equalTo(changeRequest2.getProductId()));
        assertThat("FunctionalClusterId are  same", changeRequest1.getFunctionalClusterId(), equalTo(changeRequest2.getFunctionalClusterId()));
        assertThat("ReasonsForChange are  same", changeRequest1.getReasonsForChange(), equalTo(changeRequest2.getReasonsForChange()));
        assertThat("ProblemDescription are  same", changeRequest1.getProblemDescription(), equalTo(changeRequest2.getProblemDescription()));
        assertThat("ProposedSolution are  same", changeRequest1.getProposedSolution(), equalTo(changeRequest2.getProposedSolution()));
        assertThat("RootCause are  same", changeRequest1.getRootCause(), equalTo(changeRequest2.getRootCause()));
        assertThat("BenefitsOfChange are  same", changeRequest1.getBenefitsOfChange(), equalTo(changeRequest2.getBenefitsOfChange()));
        assertThat("ImplementationPriority are  same", changeRequest1.getImplementationPriority(), equalTo(changeRequest2.getImplementationPriority()));
        assertThat("RequirementsForImplementationPlan are  same", changeRequest1.getRequirementsForImplementationPlan(), equalTo(changeRequest2.getRequirementsForImplementationPlan()));
        assertThat("ChangeOwnerType  are  same", changeRequest1.getChangeOwnerType(), equalTo(changeRequest2.getChangeOwnerType()));
        assertThat("DependentChangeRequestIds are  same", changeRequest1.getDependentChangeRequestIds(), equalTo(changeRequest2.getDependentChangeRequestIds()));
    }

    public static void changeRequestAreSameWithoutComparingProjectId(ChangeRequest changeRequest1, ChangeRequest changeRequest2) {
        assertThat("title", changeRequest1.getTitle(), equalTo(changeRequest2.getTitle()));
        assertThat("ChangeSpecialist1 are same", changeRequest1.getChangeSpecialist1(), equalTo(changeRequest2.getChangeSpecialist1()));
        assertThat("ChangeSpecialist2 are  same", changeRequest1.getChangeSpecialist2(), equalTo(changeRequest2.getChangeSpecialist2()));
        assertThat("IsSecure are  same", changeRequest1.getIsSecure(), equalTo(changeRequest2.getIsSecure()));
        assertThat("ChangeControlBoards are  same", changeRequest1.getChangeControlBoards(), equalTo(changeRequest2.getChangeControlBoards()));
        assertThat("ChangeBoards are  same", changeRequest1.getChangeBoards(), equalTo(changeRequest2.getChangeBoards()));
        assertThat("IssueTypes are  same", changeRequest1.getIssueTypes(), equalTo(changeRequest2.getIssueTypes()));
        assertThat("ChangeRequestType  are  same", changeRequest1.getChangeRequestType(), equalTo(changeRequest2.getChangeRequestType()));
        assertThat("AnalysisPriority  are  same", changeRequest1.getAnalysisPriority(), equalTo(changeRequest2.getAnalysisPriority()));
        //assertThat("ProjectId are  same", changeRequest1.getProjectId(), equalTo(changeRequest2.getProjectId()));
        assertThat("ProductId  are  same", changeRequest1.getProductId(), equalTo(changeRequest2.getProductId()));
        assertThat("FunctionalClusterId are  same", changeRequest1.getFunctionalClusterId(), equalTo(changeRequest2.getFunctionalClusterId()));
        assertThat("ReasonsForChange are  same", changeRequest1.getReasonsForChange(), equalTo(changeRequest2.getReasonsForChange()));
        assertThat("ProblemDescription are  same", changeRequest1.getProblemDescription(), equalTo(changeRequest2.getProblemDescription()));
        assertThat("ProposedSolution are  same", changeRequest1.getProposedSolution(), equalTo(changeRequest2.getProposedSolution()));
        assertThat("RootCause are  same", changeRequest1.getRootCause(), equalTo(changeRequest2.getRootCause()));
        assertThat("BenefitsOfChange are  same", changeRequest1.getBenefitsOfChange(), equalTo(changeRequest2.getBenefitsOfChange()));
        assertThat("ImplementationPriority are  same", changeRequest1.getImplementationPriority(), equalTo(changeRequest2.getImplementationPriority()));
        assertThat("RequirementsForImplementationPlan are  same", changeRequest1.getRequirementsForImplementationPlan(), equalTo(changeRequest2.getRequirementsForImplementationPlan()));
        assertThat("ChangeOwnerType  are  same", changeRequest1.getChangeOwnerType(), equalTo(changeRequest2.getChangeOwnerType()));
        assertThat("DependentChangeRequestIds are  same", changeRequest1.getDependentChangeRequestIds(), equalTo(changeRequest2.getDependentChangeRequestIds()));
    }

    public static void changeRequestAreSameWithoutComparingProductId(ChangeRequest changeRequest1, ChangeRequest changeRequest2) {
        assertThat("title", changeRequest1.getTitle(), equalTo(changeRequest2.getTitle()));
        assertThat("ChangeSpecialist1 are same", changeRequest1.getChangeSpecialist1(), equalTo(changeRequest2.getChangeSpecialist1()));
        assertThat("ChangeSpecialist2 are  same", changeRequest1.getChangeSpecialist2(), equalTo(changeRequest2.getChangeSpecialist2()));
        assertThat("IsSecure are  same", changeRequest1.getIsSecure(), equalTo(changeRequest2.getIsSecure()));
        assertThat("ChangeControlBoards are  same", changeRequest1.getChangeControlBoards(), equalTo(changeRequest2.getChangeControlBoards()));
        assertThat("ChangeBoards are  same", changeRequest1.getChangeBoards(), equalTo(changeRequest2.getChangeBoards()));
        assertThat("IssueTypes are  same", changeRequest1.getIssueTypes(), equalTo(changeRequest2.getIssueTypes()));
        assertThat("ChangeRequestType  are  same", changeRequest1.getChangeRequestType(), equalTo(changeRequest2.getChangeRequestType()));
        assertThat("AnalysisPriority  are  same", changeRequest1.getAnalysisPriority(), equalTo(changeRequest2.getAnalysisPriority()));
        assertThat("ProjectId are  same", changeRequest1.getProjectId(), equalTo(changeRequest2.getProjectId()));
        //assertThat("ProductId  are  same", changeRequest1.getProductId(), equalTo(changeRequest2.getProductId()));
        assertThat("FunctionalClusterId are  same", changeRequest1.getFunctionalClusterId(), equalTo(changeRequest2.getFunctionalClusterId()));
        assertThat("ReasonsForChange are  same", changeRequest1.getReasonsForChange(), equalTo(changeRequest2.getReasonsForChange()));
        assertThat("ProblemDescription are  same", changeRequest1.getProblemDescription(), equalTo(changeRequest2.getProblemDescription()));
        assertThat("ProposedSolution are  same", changeRequest1.getProposedSolution(), equalTo(changeRequest2.getProposedSolution()));
        assertThat("RootCause are  same", changeRequest1.getRootCause(), equalTo(changeRequest2.getRootCause()));
        assertThat("BenefitsOfChange are  same", changeRequest1.getBenefitsOfChange(), equalTo(changeRequest2.getBenefitsOfChange()));
        assertThat("ImplementationPriority are  same", changeRequest1.getImplementationPriority(), equalTo(changeRequest2.getImplementationPriority()));
        assertThat("RequirementsForImplementationPlan are  same", changeRequest1.getRequirementsForImplementationPlan(), equalTo(changeRequest2.getRequirementsForImplementationPlan()));
        assertThat("ChangeOwnerType  are  same", changeRequest1.getChangeOwnerType(), equalTo(changeRequest2.getChangeOwnerType()));
        assertThat("DependentChangeRequestIds are  same", changeRequest1.getDependentChangeRequestIds(), equalTo(changeRequest2.getDependentChangeRequestIds()));
    }
    public static void changeRequestAreSameWithoutComparingFunctionalClusterId(ChangeRequest changeRequest1, ChangeRequest changeRequest2) {
        assertThat("title", changeRequest1.getTitle(), equalTo(changeRequest2.getTitle()));
        assertThat("ChangeSpecialist1 are same", changeRequest1.getChangeSpecialist1(), equalTo(changeRequest2.getChangeSpecialist1()));
        assertThat("ChangeSpecialist2 are  same", changeRequest1.getChangeSpecialist2(), equalTo(changeRequest2.getChangeSpecialist2()));
        assertThat("IsSecure are  same", changeRequest1.getIsSecure(), equalTo(changeRequest2.getIsSecure()));
        assertThat("ChangeControlBoards are  same", changeRequest1.getChangeControlBoards(), equalTo(changeRequest2.getChangeControlBoards()));
        assertThat("ChangeBoards are  same", changeRequest1.getChangeBoards(), equalTo(changeRequest2.getChangeBoards()));
        assertThat("IssueTypes are  same", changeRequest1.getIssueTypes(), equalTo(changeRequest2.getIssueTypes()));
        assertThat("ChangeRequestType  are  same", changeRequest1.getChangeRequestType(), equalTo(changeRequest2.getChangeRequestType()));
        assertThat("AnalysisPriority  are  same", changeRequest1.getAnalysisPriority(), equalTo(changeRequest2.getAnalysisPriority()));
        assertThat("ProjectId are  same", changeRequest1.getProjectId(), equalTo(changeRequest2.getProjectId()));
        assertThat("ProductId  are  same", changeRequest1.getProductId(), equalTo(changeRequest2.getProductId()));
        //assertThat("FunctionalClusterId are  same", changeRequest1.getFunctionalClusterId(), equalTo(changeRequest2.getFunctionalClusterId()));
        assertThat("ReasonsForChange are  same", changeRequest1.getReasonsForChange(), equalTo(changeRequest2.getReasonsForChange()));
        assertThat("ProblemDescription are  same", changeRequest1.getProblemDescription(), equalTo(changeRequest2.getProblemDescription()));
        assertThat("ProposedSolution are  same", changeRequest1.getProposedSolution(), equalTo(changeRequest2.getProposedSolution()));
        assertThat("RootCause are  same", changeRequest1.getRootCause(), equalTo(changeRequest2.getRootCause()));
        assertThat("BenefitsOfChange are  same", changeRequest1.getBenefitsOfChange(), equalTo(changeRequest2.getBenefitsOfChange()));
        assertThat("ImplementationPriority are  same", changeRequest1.getImplementationPriority(), equalTo(changeRequest2.getImplementationPriority()));
        assertThat("RequirementsForImplementationPlan are  same", changeRequest1.getRequirementsForImplementationPlan(), equalTo(changeRequest2.getRequirementsForImplementationPlan()));
        assertThat("ChangeOwnerType  are  same", changeRequest1.getChangeOwnerType(), equalTo(changeRequest2.getChangeOwnerType()));
        assertThat("DependentChangeRequestIds are  same", changeRequest1.getDependentChangeRequestIds(), equalTo(changeRequest2.getDependentChangeRequestIds()));
    }

    public static void changeRequestAreSameWithoutComparingRootCause(ChangeRequest changeRequest1, ChangeRequest changeRequest2) {
        assertThat("title", changeRequest1.getTitle(), equalTo(changeRequest2.getTitle()));
        assertThat("ChangeSpecialist1 are same", changeRequest1.getChangeSpecialist1(), equalTo(changeRequest2.getChangeSpecialist1()));
        assertThat("ChangeSpecialist2 are  same", changeRequest1.getChangeSpecialist2(), equalTo(changeRequest2.getChangeSpecialist2()));
        assertThat("IsSecure are  same", changeRequest1.getIsSecure(), equalTo(changeRequest2.getIsSecure()));
        assertThat("ChangeControlBoards are  same", changeRequest1.getChangeControlBoards(), equalTo(changeRequest2.getChangeControlBoards()));
        assertThat("ChangeBoards are  same", changeRequest1.getChangeBoards(), equalTo(changeRequest2.getChangeBoards()));
        assertThat("IssueTypes are  same", changeRequest1.getIssueTypes(), equalTo(changeRequest2.getIssueTypes()));
        assertThat("ChangeRequestType  are  same", changeRequest1.getChangeRequestType(), equalTo(changeRequest2.getChangeRequestType()));
        assertThat("AnalysisPriority  are  same", changeRequest1.getAnalysisPriority(), equalTo(changeRequest2.getAnalysisPriority()));
        assertThat("ProjectId are  same", changeRequest1.getProjectId(), equalTo(changeRequest2.getProjectId()));
        assertThat("ProductId  are  same", changeRequest1.getProductId(), equalTo(changeRequest2.getProductId()));
        assertThat("FunctionalClusterId are  same", changeRequest1.getFunctionalClusterId(), equalTo(changeRequest2.getFunctionalClusterId()));
        assertThat("ReasonsForChange are  same", changeRequest1.getReasonsForChange(), equalTo(changeRequest2.getReasonsForChange()));
        assertThat("ProblemDescription are  same", changeRequest1.getProblemDescription(), equalTo(changeRequest2.getProblemDescription()));
        assertThat("ProposedSolution are  same", changeRequest1.getProposedSolution(), equalTo(changeRequest2.getProposedSolution()));
        //assertThat("RootCause are  same", changeRequest1.getRootCause(), equalTo(changeRequest2.getRootCause()));
        assertThat("BenefitsOfChange are  same", changeRequest1.getBenefitsOfChange(), equalTo(changeRequest2.getBenefitsOfChange()));
        assertThat("ImplementationPriority are  same", changeRequest1.getImplementationPriority(), equalTo(changeRequest2.getImplementationPriority()));
        assertThat("RequirementsForImplementationPlan are  same", changeRequest1.getRequirementsForImplementationPlan(), equalTo(changeRequest2.getRequirementsForImplementationPlan()));
        assertThat("ChangeOwnerType  are  same", changeRequest1.getChangeOwnerType(), equalTo(changeRequest2.getChangeOwnerType()));
        assertThat("DependentChangeRequestIds are  same", changeRequest1.getDependentChangeRequestIds(), equalTo(changeRequest2.getDependentChangeRequestIds()));
    }

    public static void changeRequestAreSameWithoutComparingBenefitsOfChange(ChangeRequest changeRequest1, ChangeRequest changeRequest2) {
        assertThat("title", changeRequest1.getTitle(), equalTo(changeRequest2.getTitle()));
        assertThat("ChangeSpecialist1 are same", changeRequest1.getChangeSpecialist1(), equalTo(changeRequest2.getChangeSpecialist1()));
        assertThat("ChangeSpecialist2 are  same", changeRequest1.getChangeSpecialist2(), equalTo(changeRequest2.getChangeSpecialist2()));
        assertThat("IsSecure are  same", changeRequest1.getIsSecure(), equalTo(changeRequest2.getIsSecure()));
        assertThat("ChangeControlBoards are  same", changeRequest1.getChangeControlBoards(), equalTo(changeRequest2.getChangeControlBoards()));
        assertThat("ChangeBoards are  same", changeRequest1.getChangeBoards(), equalTo(changeRequest2.getChangeBoards()));
        assertThat("IssueTypes are  same", changeRequest1.getIssueTypes(), equalTo(changeRequest2.getIssueTypes()));
        assertThat("ChangeRequestType  are  same", changeRequest1.getChangeRequestType(), equalTo(changeRequest2.getChangeRequestType()));
        assertThat("AnalysisPriority  are  same", changeRequest1.getAnalysisPriority(), equalTo(changeRequest2.getAnalysisPriority()));
        assertThat("ProjectId are  same", changeRequest1.getProjectId(), equalTo(changeRequest2.getProjectId()));
        assertThat("ProductId  are  same", changeRequest1.getProductId(), equalTo(changeRequest2.getProductId()));
        assertThat("FunctionalClusterId are  same", changeRequest1.getFunctionalClusterId(), equalTo(changeRequest2.getFunctionalClusterId()));
        assertThat("ReasonsForChange are  same", changeRequest1.getReasonsForChange(), equalTo(changeRequest2.getReasonsForChange()));
        assertThat("ProblemDescription are  same", changeRequest1.getProblemDescription(), equalTo(changeRequest2.getProblemDescription()));
        assertThat("ProposedSolution are  same", changeRequest1.getProposedSolution(), equalTo(changeRequest2.getProposedSolution()));
        assertThat("RootCause are  same", changeRequest1.getRootCause(), equalTo(changeRequest2.getRootCause()));
        //assertThat("BenefitsOfChange are  same", changeRequest1.getBenefitsOfChange(), equalTo(changeRequest2.getBenefitsOfChange()));
        assertThat("ImplementationPriority are  same", changeRequest1.getImplementationPriority(), equalTo(changeRequest2.getImplementationPriority()));
        assertThat("RequirementsForImplementationPlan are  same", changeRequest1.getRequirementsForImplementationPlan(), equalTo(changeRequest2.getRequirementsForImplementationPlan()));
        assertThat("ChangeOwnerType  are  same", changeRequest1.getChangeOwnerType(), equalTo(changeRequest2.getChangeOwnerType()));
        assertThat("DependentChangeRequestIds are  same", changeRequest1.getDependentChangeRequestIds(), equalTo(changeRequest2.getDependentChangeRequestIds()));
    }

    public static void changeRequestAreSameWithoutComparingDependentChangeRequests(ChangeRequest changeRequest1, ChangeRequest changeRequest2) {
        assertThat("title", changeRequest1.getTitle(), equalTo(changeRequest2.getTitle()));
        assertThat("ChangeSpecialist1 are same", changeRequest1.getChangeSpecialist1(), equalTo(changeRequest2.getChangeSpecialist1()));
        assertThat("ChangeSpecialist2 are  same", changeRequest1.getChangeSpecialist2(), equalTo(changeRequest2.getChangeSpecialist2()));
        assertThat("IsSecure are  same", changeRequest1.getIsSecure(), equalTo(changeRequest2.getIsSecure()));
        assertThat("ChangeControlBoards are  same", changeRequest1.getChangeControlBoards(), equalTo(changeRequest2.getChangeControlBoards()));
        assertThat("ChangeBoards are  same", changeRequest1.getChangeBoards(), equalTo(changeRequest2.getChangeBoards()));
        assertThat("IssueTypes are  same", changeRequest1.getIssueTypes(), equalTo(changeRequest2.getIssueTypes()));
        assertThat("ChangeRequestType  are  same", changeRequest1.getChangeRequestType(), equalTo(changeRequest2.getChangeRequestType()));
        assertThat("AnalysisPriority  are  same", changeRequest1.getAnalysisPriority(), equalTo(changeRequest2.getAnalysisPriority()));
        assertThat("ProjectId are  same", changeRequest1.getProjectId(), equalTo(changeRequest2.getProjectId()));
        assertThat("ProductId  are  same", changeRequest1.getProductId(), equalTo(changeRequest2.getProductId()));
        assertThat("FunctionalClusterId are  same", changeRequest1.getFunctionalClusterId(), equalTo(changeRequest2.getFunctionalClusterId()));
        assertThat("ReasonsForChange are  same", changeRequest1.getReasonsForChange(), equalTo(changeRequest2.getReasonsForChange()));
        assertThat("ProblemDescription are  same", changeRequest1.getProblemDescription(), equalTo(changeRequest2.getProblemDescription()));
        assertThat("ProposedSolution are  same", changeRequest1.getProposedSolution(), equalTo(changeRequest2.getProposedSolution()));
        assertThat("RootCause are  same", changeRequest1.getRootCause(), equalTo(changeRequest2.getRootCause()));
        assertThat("BenefitsOfChange are  same", changeRequest1.getBenefitsOfChange(), equalTo(changeRequest2.getBenefitsOfChange()));
        assertThat("ImplementationPriority are  same", changeRequest1.getImplementationPriority(), equalTo(changeRequest2.getImplementationPriority()));
        assertThat("RequirementsForImplementationPlan are  same", changeRequest1.getRequirementsForImplementationPlan(), equalTo(changeRequest2.getRequirementsForImplementationPlan()));
        assertThat("ChangeOwnerType  are  same", changeRequest1.getChangeOwnerType(), equalTo(changeRequest2.getChangeOwnerType()));
        //assertThat("DependentChangeRequestIds are  same", changeRequest1.getDependentChangeRequestIds(), equalTo(changeRequest2.getDependentChangeRequestIds()));
    }

    public static void changeRequestAreSameWithoutComparingImplementationPriority(ChangeRequest changeRequest1, ChangeRequest changeRequest2) {
        assertThat("title", changeRequest1.getTitle(), equalTo(changeRequest2.getTitle()));
        assertThat("ChangeSpecialist1 are same", changeRequest1.getChangeSpecialist1(), equalTo(changeRequest2.getChangeSpecialist1()));
        assertThat("ChangeSpecialist2 are  same", changeRequest1.getChangeSpecialist2(), equalTo(changeRequest2.getChangeSpecialist2()));
        assertThat("IsSecure are  same", changeRequest1.getIsSecure(), equalTo(changeRequest2.getIsSecure()));
        assertThat("ChangeControlBoards are  same", changeRequest1.getChangeControlBoards(), equalTo(changeRequest2.getChangeControlBoards()));
        assertThat("ChangeBoards are  same", changeRequest1.getChangeBoards(), equalTo(changeRequest2.getChangeBoards()));
        assertThat("IssueTypes are  same", changeRequest1.getIssueTypes(), equalTo(changeRequest2.getIssueTypes()));
        assertThat("ChangeRequestType  are  same", changeRequest1.getChangeRequestType(), equalTo(changeRequest2.getChangeRequestType()));
        assertThat("AnalysisPriority  are  same", changeRequest1.getAnalysisPriority(), equalTo(changeRequest2.getAnalysisPriority()));
        assertThat("ProjectId are  same", changeRequest1.getProjectId(), equalTo(changeRequest2.getProjectId()));
        assertThat("ProductId  are  same", changeRequest1.getProductId(), equalTo(changeRequest2.getProductId()));
        assertThat("FunctionalClusterId are  same", changeRequest1.getFunctionalClusterId(), equalTo(changeRequest2.getFunctionalClusterId()));
        assertThat("ReasonsForChange are  same", changeRequest1.getReasonsForChange(), equalTo(changeRequest2.getReasonsForChange()));
        assertThat("ProblemDescription are  same", changeRequest1.getProblemDescription(), equalTo(changeRequest2.getProblemDescription()));
        assertThat("ProposedSolution are  same", changeRequest1.getProposedSolution(), equalTo(changeRequest2.getProposedSolution()));
        assertThat("RootCause are  same", changeRequest1.getRootCause(), equalTo(changeRequest2.getRootCause()));
        assertThat("BenefitsOfChange are  same", changeRequest1.getBenefitsOfChange(), equalTo(changeRequest2.getBenefitsOfChange()));
        //assertThat("ImplementationPriority are  same", changeRequest1.getImplementationPriority(), equalTo(changeRequest2.getImplementationPriority()));
        assertThat("RequirementsForImplementationPlan are  same", changeRequest1.getRequirementsForImplementationPlan(), equalTo(changeRequest2.getRequirementsForImplementationPlan()));
        assertThat("ChangeOwnerType  are  same", changeRequest1.getChangeOwnerType(), equalTo(changeRequest2.getChangeOwnerType()));
        assertThat("DependentChangeRequestIds are  same", changeRequest1.getDependentChangeRequestIds(), equalTo(changeRequest2.getDependentChangeRequestIds()));
    }

    public static void changeRequestAreSameWithoutComparingRequirementsForImplementationPlan(ChangeRequest changeRequest1, ChangeRequest changeRequest2) {
        assertThat("title", changeRequest1.getTitle(), equalTo(changeRequest2.getTitle()));
        assertThat("ChangeSpecialist1 are same", changeRequest1.getChangeSpecialist1(), equalTo(changeRequest2.getChangeSpecialist1()));
        assertThat("ChangeSpecialist2 are  same", changeRequest1.getChangeSpecialist2(), equalTo(changeRequest2.getChangeSpecialist2()));
        assertThat("IsSecure are  same", changeRequest1.getIsSecure(), equalTo(changeRequest2.getIsSecure()));
        assertThat("ChangeControlBoards are  same", changeRequest1.getChangeControlBoards(), equalTo(changeRequest2.getChangeControlBoards()));
        assertThat("ChangeBoards are  same", changeRequest1.getChangeBoards(), equalTo(changeRequest2.getChangeBoards()));
        assertThat("IssueTypes are  same", changeRequest1.getIssueTypes(), equalTo(changeRequest2.getIssueTypes()));
        assertThat("ChangeRequestType  are  same", changeRequest1.getChangeRequestType(), equalTo(changeRequest2.getChangeRequestType()));
        assertThat("AnalysisPriority  are  same", changeRequest1.getAnalysisPriority(), equalTo(changeRequest2.getAnalysisPriority()));
        assertThat("ProjectId are  same", changeRequest1.getProjectId(), equalTo(changeRequest2.getProjectId()));
        assertThat("ProductId  are  same", changeRequest1.getProductId(), equalTo(changeRequest2.getProductId()));
        assertThat("FunctionalClusterId are  same", changeRequest1.getFunctionalClusterId(), equalTo(changeRequest2.getFunctionalClusterId()));
        assertThat("ReasonsForChange are  same", changeRequest1.getReasonsForChange(), equalTo(changeRequest2.getReasonsForChange()));
        assertThat("ProblemDescription are  same", changeRequest1.getProblemDescription(), equalTo(changeRequest2.getProblemDescription()));
        assertThat("ProposedSolution are  same", changeRequest1.getProposedSolution(), equalTo(changeRequest2.getProposedSolution()));
        assertThat("RootCause are  same", changeRequest1.getRootCause(), equalTo(changeRequest2.getRootCause()));
        assertThat("BenefitsOfChange are  same", changeRequest1.getBenefitsOfChange(), equalTo(changeRequest2.getBenefitsOfChange()));
        assertThat("ImplementationPriority are  same", changeRequest1.getImplementationPriority(), equalTo(changeRequest2.getImplementationPriority()));
        //assertThat("RequirementsForImplementationPlan are  same", changeRequest1.getRequirementsForImplementationPlan(), equalTo(changeRequest2.getRequirementsForImplementationPlan()));
        assertThat("ChangeOwnerType  are  same", changeRequest1.getChangeOwnerType(), equalTo(changeRequest2.getChangeOwnerType()));
        assertThat("DependentChangeRequestIds are  same", changeRequest1.getDependentChangeRequestIds(), equalTo(changeRequest2.getDependentChangeRequestIds()));
    }

    public static void changeRequestAreSameWithoutComparingChangeOwnerType(ChangeRequest changeRequest1, ChangeRequest changeRequest2) {
        assertThat("title", changeRequest1.getTitle(), equalTo(changeRequest2.getTitle()));
        assertThat("ChangeSpecialist1 are same", changeRequest1.getChangeSpecialist1(), equalTo(changeRequest2.getChangeSpecialist1()));
        assertThat("ChangeSpecialist2 are  same", changeRequest1.getChangeSpecialist2(), equalTo(changeRequest2.getChangeSpecialist2()));
        assertThat("IsSecure are  same", changeRequest1.getIsSecure(), equalTo(changeRequest2.getIsSecure()));
        assertThat("ChangeControlBoards are  same", changeRequest1.getChangeControlBoards(), equalTo(changeRequest2.getChangeControlBoards()));
        assertThat("ChangeBoards are  same", changeRequest1.getChangeBoards(), equalTo(changeRequest2.getChangeBoards()));
        assertThat("IssueTypes are  same", changeRequest1.getIssueTypes(), equalTo(changeRequest2.getIssueTypes()));
        assertThat("ChangeRequestType  are  same", changeRequest1.getChangeRequestType(), equalTo(changeRequest2.getChangeRequestType()));
        assertThat("AnalysisPriority  are  same", changeRequest1.getAnalysisPriority(), equalTo(changeRequest2.getAnalysisPriority()));
        assertThat("ProjectId are  same", changeRequest1.getProjectId(), equalTo(changeRequest2.getProjectId()));
        assertThat("ProductId  are  same", changeRequest1.getProductId(), equalTo(changeRequest2.getProductId()));
        assertThat("FunctionalClusterId are  same", changeRequest1.getFunctionalClusterId(), equalTo(changeRequest2.getFunctionalClusterId()));
        assertThat("ReasonsForChange are  same", changeRequest1.getReasonsForChange(), equalTo(changeRequest2.getReasonsForChange()));
        assertThat("ProblemDescription are  same", changeRequest1.getProblemDescription(), equalTo(changeRequest2.getProblemDescription()));
        assertThat("ProposedSolution are  same", changeRequest1.getProposedSolution(), equalTo(changeRequest2.getProposedSolution()));
        assertThat("RootCause are  same", changeRequest1.getRootCause(), equalTo(changeRequest2.getRootCause()));
        assertThat("BenefitsOfChange are  same", changeRequest1.getBenefitsOfChange(), equalTo(changeRequest2.getBenefitsOfChange()));
        assertThat("ImplementationPriority are  same", changeRequest1.getImplementationPriority(), equalTo(changeRequest2.getImplementationPriority()));
        assertThat("RequirementsForImplementationPlan are  same", changeRequest1.getRequirementsForImplementationPlan(), equalTo(changeRequest2.getRequirementsForImplementationPlan()));
        //assertThat("ChangeOwnerType  are  same", changeRequest1.getChangeOwnerType(), equalTo(changeRequest2.getChangeOwnerType()));
        assertThat("DependentChangeRequestIds are  same", changeRequest1.getDependentChangeRequestIds(), equalTo(changeRequest2.getDependentChangeRequestIds()));
    }


    public static void changeRequestAreSame(ChangeRequest changeRequestBeforeCaseAction, ChangeRequest changeRequestAfterCaseAction) {

        assertThat("creators are  same", changeRequestBeforeCaseAction.getCreator(), equalTo(changeRequestAfterCaseAction.getCreator()));
        assertThat("statuses are  same", changeRequestBeforeCaseAction.getStatus(), equalTo(changeRequestAfterCaseAction.getStatus()));
        assertThat("titles are  same", changeRequestBeforeCaseAction.getTitle(), equalTo(changeRequestAfterCaseAction.getTitle()));
        assertThat("ChangeSpecialist1 are  same", changeRequestBeforeCaseAction.getChangeSpecialist1(), equalTo(changeRequestAfterCaseAction.getChangeSpecialist1()));
        assertThat("ProblemDescription are  same", changeRequestBeforeCaseAction.getProblemDescription(), equalTo(changeRequestAfterCaseAction.getProblemDescription()));
        assertThat("FunctionalClusterId are same", changeRequestBeforeCaseAction.getFunctionalClusterId(), equalTo(changeRequestAfterCaseAction.getFunctionalClusterId()));
        assertThat("IssueTypes are same", changeRequestBeforeCaseAction.getIssueTypes(), equalTo(changeRequestAfterCaseAction.getIssueTypes()));
        assertThat("ReasonsForChange are  same", changeRequestBeforeCaseAction.getReasonsForChange(), equalTo(changeRequestAfterCaseAction.getReasonsForChange()));
        assertThat("RootCause are same", changeRequestBeforeCaseAction.getRootCause(), equalTo(changeRequestAfterCaseAction.getRootCause()));
        assertThat("BenefitsOfChange are same", changeRequestBeforeCaseAction.getBenefitsOfChange(), equalTo(changeRequestAfterCaseAction.getBenefitsOfChange()));
        assertThat("RequirementsForImplementationPlan are same", changeRequestBeforeCaseAction.getRequirementsForImplementationPlan(), equalTo(changeRequestAfterCaseAction.getRequirementsForImplementationPlan()));
        assertThat("DependentChangeRequestIds are same", changeRequestBeforeCaseAction.getDependentChangeRequestIds(), equalTo(changeRequestAfterCaseAction.getDependentChangeRequestIds()));
    }



    public static void solutionDefinitionAreSameWithoutComparingTestAndReleaseStrategyDetails(SolutionDefinition solutionDefinitionBeforeUpdate, SolutionDefinition solutionDefinitionAfterUpdate){
        assertThat("FunctionalSoftwareDependenciesDetails are  same", solutionDefinitionBeforeUpdate.getFunctionalSoftwareDependenciesDetails(), equalTo(solutionDefinitionAfterUpdate.getFunctionalSoftwareDependenciesDetails()));
        assertThat("statuses are  same", solutionDefinitionBeforeUpdate.getStatus(), equalTo(solutionDefinitionAfterUpdate.getStatus()));
        assertThat("AlignedWithFODetails are  same", solutionDefinitionBeforeUpdate.getAlignedWithFoDetails(), equalTo(solutionDefinitionAfterUpdate.getAlignedWithFoDetails()));
        assertThat("ProductsAffected are  same", solutionDefinitionBeforeUpdate.getProductsAffected(), equalTo(solutionDefinitionAfterUpdate.getProductsAffected()));
        assertThat("TechnicalRecommendation are  same", solutionDefinitionBeforeUpdate.getTechnicalRecommendation(), equalTo(solutionDefinitionAfterUpdate.getTechnicalRecommendation()));
        assertThat("ProductsModuleAffected are  same", solutionDefinitionBeforeUpdate.getProductsModuleAffected(), equalTo(solutionDefinitionAfterUpdate.getProductsModuleAffected()));
    }

    public static void solutionDefinitionAreSameWithoutComparingProductsAffected(SolutionDefinition solutionDefinitionBeforeUpdate, SolutionDefinition solutionDefinitionAfterUpdate){

        assertThat("FunctionalSoftwareDependenciesDetails are  same", solutionDefinitionBeforeUpdate.getFunctionalSoftwareDependenciesDetails(), equalTo(solutionDefinitionAfterUpdate.getFunctionalSoftwareDependenciesDetails()));
        assertThat("statuses are  same", solutionDefinitionBeforeUpdate.getStatus(), equalTo(solutionDefinitionAfterUpdate.getStatus()));
        assertThat("AlignedWithFODetails are  same", solutionDefinitionBeforeUpdate.getAlignedWithFoDetails(), equalTo(solutionDefinitionAfterUpdate.getAlignedWithFoDetails()));
        assertThat("TechnicalRecommendation are  same", solutionDefinitionBeforeUpdate.getTechnicalRecommendation(), equalTo(solutionDefinitionAfterUpdate.getTechnicalRecommendation()));
        assertThat("TechnicalRecommendation are  same", solutionDefinitionBeforeUpdate.getTechnicalRecommendation(), equalTo(solutionDefinitionAfterUpdate.getTechnicalRecommendation()));
        assertThat("ProductsModuleAffected are  same", solutionDefinitionBeforeUpdate.getProductsModuleAffected(), equalTo(solutionDefinitionAfterUpdate.getProductsModuleAffected()));
        assertThat("HardwareSoftwareDependenciesAlignedDetails are  same", solutionDefinitionBeforeUpdate.getHardwareSoftwareDependenciesAlignedDetails(), equalTo(solutionDefinitionAfterUpdate.getHardwareSoftwareDependenciesAlignedDetails()));
    }

    public static void solutionDefinitionAreSameWithoutComparingAlignedWithFODetails(SolutionDefinition solutionDefinitionBeforeUpdate, SolutionDefinition solutionDefinitionAfterUpdate){

        assertThat("FunctionalSoftwareDependenciesDetails are  same", solutionDefinitionBeforeUpdate.getFunctionalSoftwareDependenciesDetails(), equalTo(solutionDefinitionAfterUpdate.getFunctionalSoftwareDependenciesDetails()));
        assertThat("statuses are  same", solutionDefinitionBeforeUpdate.getStatus(), equalTo(solutionDefinitionAfterUpdate.getStatus()));
        assertThat("ProductsAffected are  same", solutionDefinitionBeforeUpdate.getProductsAffected(), equalTo(solutionDefinitionAfterUpdate.getProductsAffected()));
        assertThat("ProductsAffected are  same", solutionDefinitionBeforeUpdate.getProductsAffected(), equalTo(solutionDefinitionAfterUpdate.getProductsAffected()));
        assertThat("TechnicalRecommendation are  same", solutionDefinitionBeforeUpdate.getTechnicalRecommendation(), equalTo(solutionDefinitionAfterUpdate.getTechnicalRecommendation()));
        assertThat("ProductsModuleAffected are  same", solutionDefinitionBeforeUpdate.getProductsModuleAffected(), equalTo(solutionDefinitionAfterUpdate.getProductsModuleAffected()));
        assertThat("HardwareSoftwareDependenciesAlignedDetails are  same", solutionDefinitionBeforeUpdate.getHardwareSoftwareDependenciesAlignedDetails(), equalTo(solutionDefinitionAfterUpdate.getHardwareSoftwareDependenciesAlignedDetails()));
    }

    public static void solutionDefinitionAreSameWithoutComparingTechnicalRecommendation(SolutionDefinition solutionDefinitionBeforeUpdate, SolutionDefinition solutionDefinitionAfterUpdate){
        assertThat("FunctionalSoftwareDependenciesDetails are  same", solutionDefinitionBeforeUpdate.getFunctionalSoftwareDependenciesDetails(), equalTo(solutionDefinitionAfterUpdate.getFunctionalSoftwareDependenciesDetails()));
        assertThat("statuses are  same", solutionDefinitionBeforeUpdate.getStatus(), equalTo(solutionDefinitionAfterUpdate.getStatus()));
        assertThat("ProductsAffected are  same", solutionDefinitionBeforeUpdate.getProductsAffected(), equalTo(solutionDefinitionAfterUpdate.getProductsAffected()));
        assertThat("ProductsAffected are  same", solutionDefinitionBeforeUpdate.getProductsAffected(), equalTo(solutionDefinitionAfterUpdate.getProductsAffected()));
        assertThat("ProductsModuleAffected are  same", solutionDefinitionBeforeUpdate.getProductsModuleAffected(), equalTo(solutionDefinitionAfterUpdate.getProductsModuleAffected()));
        assertThat("HardwareSoftwareDependenciesAlignedDetails are  same", solutionDefinitionBeforeUpdate.getHardwareSoftwareDependenciesAlignedDetails(), equalTo(solutionDefinitionAfterUpdate.getHardwareSoftwareDependenciesAlignedDetails()));
    }

    public static void solutionDefinitionAreSameWithoutComparingProductsModuleAffected(SolutionDefinition solutionDefinitionBeforeUpdate, SolutionDefinition solutionDefinitionAfterUpdate){
        assertThat("FunctionalSoftwareDependenciesDetails are  same", solutionDefinitionBeforeUpdate.getFunctionalSoftwareDependenciesDetails(), equalTo(solutionDefinitionAfterUpdate.getFunctionalSoftwareDependenciesDetails()));
        assertThat("statuses are  same", solutionDefinitionBeforeUpdate.getStatus(), equalTo(solutionDefinitionAfterUpdate.getStatus()));
        assertThat("ProductsAffected are  same", solutionDefinitionBeforeUpdate.getProductsAffected(), equalTo(solutionDefinitionAfterUpdate.getProductsAffected()));
        assertThat("ProductsAffected are  same", solutionDefinitionBeforeUpdate.getProductsAffected(), equalTo(solutionDefinitionAfterUpdate.getProductsAffected()));
        assertThat("TechnicalRecommendation are  same", solutionDefinitionBeforeUpdate.getTechnicalRecommendation(), equalTo(solutionDefinitionAfterUpdate.getTechnicalRecommendation()));
        assertThat("HardwareSoftwareDependenciesAlignedDetails are  same", solutionDefinitionBeforeUpdate.getHardwareSoftwareDependenciesAlignedDetails(), equalTo(solutionDefinitionAfterUpdate.getHardwareSoftwareDependenciesAlignedDetails()));
    }

    public static void solutionDefinitionAreSameWithoutComparingFunctionalSoftwareDependenciesDetails(SolutionDefinition solutionDefinitionBeforeUpdate, SolutionDefinition solutionDefinitionAfterUpdate){
        assertThat("statuses are  same", solutionDefinitionBeforeUpdate.getStatus(), equalTo(solutionDefinitionAfterUpdate.getStatus()));
        assertThat("ProductsAffected are  same", solutionDefinitionBeforeUpdate.getProductsAffected(), equalTo(solutionDefinitionAfterUpdate.getProductsAffected()));
        assertThat("ProductsAffected are  same", solutionDefinitionBeforeUpdate.getProductsAffected(), equalTo(solutionDefinitionAfterUpdate.getProductsAffected()));
        assertThat("TechnicalRecommendation are  same", solutionDefinitionBeforeUpdate.getTechnicalRecommendation(), equalTo(solutionDefinitionAfterUpdate.getTechnicalRecommendation()));
        assertThat("ProductsModuleAffected are  same", solutionDefinitionBeforeUpdate.getProductsModuleAffected(), equalTo(solutionDefinitionAfterUpdate.getProductsModuleAffected()));
        assertThat("HardwareSoftwareDependenciesAlignedDetails are  same", solutionDefinitionBeforeUpdate.getHardwareSoftwareDependenciesAlignedDetails(), equalTo(solutionDefinitionAfterUpdate.getHardwareSoftwareDependenciesAlignedDetails()));
    }

    public static void solutionDefinitionAreSameWithoutComparingFunctionalHardwareDependenciesDetails(SolutionDefinition solutionDefinitionBeforeUpdate, SolutionDefinition solutionDefinitionAfterUpdate){
        assertThat("FunctionalHardwareDependenciesDetails are  same", solutionDefinitionBeforeUpdate.getFunctionalHardwareDependenciesDetails(), equalTo(solutionDefinitionAfterUpdate.getFunctionalHardwareDependenciesDetails()));
        assertThat("statuses are  same", solutionDefinitionBeforeUpdate.getStatus(), equalTo(solutionDefinitionAfterUpdate.getStatus()));
        assertThat("ProductsAffected are  same", solutionDefinitionBeforeUpdate.getProductsAffected(), equalTo(solutionDefinitionAfterUpdate.getProductsAffected()));
        assertThat("ProductsAffected are  same", solutionDefinitionBeforeUpdate.getProductsAffected(), equalTo(solutionDefinitionAfterUpdate.getProductsAffected()));
        assertThat("TechnicalRecommendation are  same", solutionDefinitionBeforeUpdate.getTechnicalRecommendation(), equalTo(solutionDefinitionAfterUpdate.getTechnicalRecommendation()));
        assertThat("ProductsModuleAffected are  same", solutionDefinitionBeforeUpdate.getProductsModuleAffected(), equalTo(solutionDefinitionAfterUpdate.getProductsModuleAffected()));
        assertThat("HardwareSoftwareDependenciesAlignedDetails are  same", solutionDefinitionBeforeUpdate.getHardwareSoftwareDependenciesAlignedDetails(), equalTo(solutionDefinitionAfterUpdate.getHardwareSoftwareDependenciesAlignedDetails()));
    }

    public static void solutionDefinitionAreSameWithoutComparingHardwareSoftwareDependenciesAlignedDetails(SolutionDefinition solutionDefinitionBeforeUpdate, SolutionDefinition solutionDefinitionAfterUpdate){
        assertThat("FunctionalHardwareDependenciesDetails are  same", solutionDefinitionBeforeUpdate.getFunctionalHardwareDependenciesDetails(), equalTo(solutionDefinitionAfterUpdate.getFunctionalHardwareDependenciesDetails()));
        assertThat("statuses are  same", solutionDefinitionBeforeUpdate.getStatus(), equalTo(solutionDefinitionAfterUpdate.getStatus()));
        assertThat("ProductsAffected are  same", solutionDefinitionBeforeUpdate.getProductsAffected(), equalTo(solutionDefinitionAfterUpdate.getProductsAffected()));
        assertThat("ProductsAffected are  same", solutionDefinitionBeforeUpdate.getProductsAffected(), equalTo(solutionDefinitionAfterUpdate.getProductsAffected()));
        assertThat("TechnicalRecommendation are  same", solutionDefinitionBeforeUpdate.getTechnicalRecommendation(), equalTo(solutionDefinitionAfterUpdate.getTechnicalRecommendation()));
        assertThat("ProductsModuleAffected are  same", solutionDefinitionBeforeUpdate.getProductsModuleAffected(), equalTo(solutionDefinitionAfterUpdate.getProductsModuleAffected()));
    }

    public static void customerImpactAreSameWithoutComparingCustomerApprovalDetails(CustomerImpact customerImpactBeforeUpdate, CustomerImpact customerImpactAfterUpdate){
        assertThat("getCustomerCommunicationDetails are  same", customerImpactBeforeUpdate.getCustomerCommunicationDetails(), equalTo(customerImpactBeforeUpdate.getCustomerCommunicationDetails()));
        assertThat("statuses are  same", customerImpactBeforeUpdate.getStatus(), equalTo(customerImpactAfterUpdate.getStatus()));
        assertThat("getChangeToCustomerImpactCriticalPartDetails are  same", customerImpactBeforeUpdate.getChangeToCustomerImpactCriticalPartDetails(), equalTo(customerImpactAfterUpdate.getChangeToCustomerImpactCriticalPartDetails()));
        assertThat("getCustomerApproval are  same", customerImpactBeforeUpdate.getCustomerApproval(), equalTo(customerImpactAfterUpdate.getCustomerApproval()));
        assertThat("getChangeToCustomerImpactCriticalPartDetails are  same", customerImpactBeforeUpdate.getChangeToCustomerImpactCriticalPartDetails(), equalTo(customerImpactAfterUpdate.getChangeToCustomerImpactCriticalPartDetails()));
        assertThat("getFcoUpgradeOptionCsrImplementationChangeDetails are  same", customerImpactBeforeUpdate.getFcoUpgradeOptionCsrImplementationChangeDetails(), equalTo(customerImpactAfterUpdate.getFcoUpgradeOptionCsrImplementationChangeDetails()));
        assertThat("ChangeToProcessImpactingCustomerDetails are same", customerImpactBeforeUpdate.getChangeToProcessImpactingCustomerDetails(), equalTo(customerImpactAfterUpdate.getChangeToProcessImpactingCustomerDetails()));
        assertThat("ChangeToCustomerImpactCriticalPartDetails are same", customerImpactBeforeUpdate.getChangeToCustomerImpactCriticalPartDetails(), equalTo(customerImpactAfterUpdate.getChangeToCustomerImpactCriticalPartDetails()));

    }

    public static void customerImpactAreSameWithoutComparingCustomerCommunicationDetails(CustomerImpact customerImpactBeforeUpdate, CustomerImpact customerImpactAfterUpdate){
        assertThat("statuses are  same", customerImpactBeforeUpdate.getStatus(), equalTo(customerImpactAfterUpdate.getStatus()));
        assertThat("getChangeToCustomerImpactCriticalPartDetails are  same", customerImpactBeforeUpdate.getChangeToCustomerImpactCriticalPartDetails(), equalTo(customerImpactAfterUpdate.getChangeToCustomerImpactCriticalPartDetails()));

        assertThat("getChangeToCustomerImpactCriticalPartDetails are  same", customerImpactBeforeUpdate.getChangeToCustomerImpactCriticalPartDetails(), equalTo(customerImpactAfterUpdate.getChangeToCustomerImpactCriticalPartDetails()));
        assertThat("getFcoUpgradeOptionCsrImplementationChangeDetails are  same", customerImpactBeforeUpdate.getFcoUpgradeOptionCsrImplementationChangeDetails(), equalTo(customerImpactAfterUpdate.getFcoUpgradeOptionCsrImplementationChangeDetails()));
        assertThat("ChangeToProcessImpactingCustomerDetails are same", customerImpactBeforeUpdate.getChangeToProcessImpactingCustomerDetails(), equalTo(customerImpactAfterUpdate.getChangeToProcessImpactingCustomerDetails()));
        assertThat("ChangeToCustomerImpactCriticalPartDetails are same", customerImpactBeforeUpdate.getChangeToCustomerImpactCriticalPartDetails(), equalTo(customerImpactAfterUpdate.getChangeToCustomerImpactCriticalPartDetails()));
        assertThat("CustomerApprovalDetails are same", customerImpactBeforeUpdate.getCustomerApprovalDetails(), equalTo(customerImpactAfterUpdate.getCustomerApprovalDetails()));
    }

    public static void customerImpactAreSameWithoutComparingImpactOnUserInterfacesDetails(CustomerImpact customerImpactBeforeUpdate, CustomerImpact customerImpactAfterUpdate){
        assertThat("getCustomerCommunicationDetails are  same", customerImpactBeforeUpdate.getCustomerCommunicationDetails(), equalTo(customerImpactBeforeUpdate.getCustomerCommunicationDetails()));
        assertThat("statuses are  same", customerImpactBeforeUpdate.getStatus(), equalTo(customerImpactAfterUpdate.getStatus()));
        assertThat("getChangeToCustomerImpactCriticalPartDetails are  same", customerImpactBeforeUpdate.getChangeToCustomerImpactCriticalPartDetails(), equalTo(customerImpactAfterUpdate.getChangeToCustomerImpactCriticalPartDetails()));

        assertThat("getChangeToCustomerImpactCriticalPartDetails are  same", customerImpactBeforeUpdate.getChangeToCustomerImpactCriticalPartDetails(), equalTo(customerImpactAfterUpdate.getChangeToCustomerImpactCriticalPartDetails()));
        assertThat("getFcoUpgradeOptionCsrImplementationChangeDetails are  same", customerImpactBeforeUpdate.getFcoUpgradeOptionCsrImplementationChangeDetails(), equalTo(customerImpactAfterUpdate.getFcoUpgradeOptionCsrImplementationChangeDetails()));
        assertThat("ChangeToProcessImpactingCustomerDetails are same", customerImpactBeforeUpdate.getChangeToProcessImpactingCustomerDetails(), equalTo(customerImpactAfterUpdate.getChangeToProcessImpactingCustomerDetails()));
        assertThat("ChangeToCustomerImpactCriticalPartDetails are same", customerImpactBeforeUpdate.getChangeToCustomerImpactCriticalPartDetails(), equalTo(customerImpactAfterUpdate.getChangeToCustomerImpactCriticalPartDetails()));
        assertThat("CustomerApprovalDetails are same", customerImpactBeforeUpdate.getCustomerApprovalDetails(), equalTo(customerImpactAfterUpdate.getCustomerApprovalDetails()));

    }
    public static void customerImpactAreSameWithoutComparingImpactOnWaferProcessEnvironmentDetails(CustomerImpact customerImpactBeforeUpdate, CustomerImpact customerImpactAfterUpdate){
        assertThat("getCustomerCommunicationDetails are  same", customerImpactBeforeUpdate.getCustomerCommunicationDetails(), equalTo(customerImpactBeforeUpdate.getCustomerCommunicationDetails()));
        assertThat("statuses are  same", customerImpactBeforeUpdate.getStatus(), equalTo(customerImpactAfterUpdate.getStatus()));
        assertThat("getChangeToCustomerImpactCriticalPartDetails are  same", customerImpactBeforeUpdate.getChangeToCustomerImpactCriticalPartDetails(), equalTo(customerImpactAfterUpdate.getChangeToCustomerImpactCriticalPartDetails()));
        assertThat("getChangeToCustomerImpactCriticalPartDetails are  same", customerImpactBeforeUpdate.getChangeToCustomerImpactCriticalPartDetails(), equalTo(customerImpactAfterUpdate.getChangeToCustomerImpactCriticalPartDetails()));
        assertThat("getFcoUpgradeOptionCsrImplementationChangeDetails are  same", customerImpactBeforeUpdate.getFcoUpgradeOptionCsrImplementationChangeDetails(), equalTo(customerImpactAfterUpdate.getFcoUpgradeOptionCsrImplementationChangeDetails()));
        assertThat("ChangeToProcessImpactingCustomerDetails are same", customerImpactBeforeUpdate.getChangeToProcessImpactingCustomerDetails(), equalTo(customerImpactAfterUpdate.getChangeToProcessImpactingCustomerDetails()));
        assertThat("ChangeToCustomerImpactCriticalPartDetails are same", customerImpactBeforeUpdate.getChangeToCustomerImpactCriticalPartDetails(), equalTo(customerImpactAfterUpdate.getChangeToCustomerImpactCriticalPartDetails()));
        assertThat("CustomerApprovalDetails are same", customerImpactBeforeUpdate.getCustomerApprovalDetails(), equalTo(customerImpactAfterUpdate.getCustomerApprovalDetails()));

    }

    public static void customerImpactAreSameWithoutComparingChangeToCustomerImpactCriticalPartDetails(CustomerImpact customerImpactBeforeUpdate, CustomerImpact customerImpactAfterUpdate){
        assertThat("getCustomerCommunicationDetails are  same", customerImpactBeforeUpdate.getCustomerCommunicationDetails(), equalTo(customerImpactBeforeUpdate.getCustomerCommunicationDetails()));
        assertThat("statuses are  same", customerImpactBeforeUpdate.getStatus(), equalTo(customerImpactAfterUpdate.getStatus()));
        assertThat("getFcoUpgradeOptionCsrImplementationChangeDetails are  same", customerImpactBeforeUpdate.getFcoUpgradeOptionCsrImplementationChangeDetails(), equalTo(customerImpactAfterUpdate.getFcoUpgradeOptionCsrImplementationChangeDetails()));
        assertThat("ChangeToProcessImpactingCustomerDetails are same", customerImpactBeforeUpdate.getChangeToProcessImpactingCustomerDetails(), equalTo(customerImpactAfterUpdate.getChangeToProcessImpactingCustomerDetails()));
        assertThat("CustomerApprovalDetails are same", customerImpactBeforeUpdate.getCustomerApprovalDetails(), equalTo(customerImpactAfterUpdate.getCustomerApprovalDetails()));
        assertThat("ChangeToProcessImpactingCustomerDetails are same", customerImpactBeforeUpdate.getChangeToProcessImpactingCustomerDetails(), equalTo(customerImpactAfterUpdate.getChangeToProcessImpactingCustomerDetails()));
        assertThat("CustomerApprovalDetails are same", customerImpactBeforeUpdate.getCustomerApprovalDetails(), equalTo(customerImpactAfterUpdate.getCustomerApprovalDetails()));
    }
    public static void customerImpactAreSameWithoutComparingChangeToProcessImpactingCustomerDetails(CustomerImpact customerImpactBeforeUpdate, CustomerImpact customerImpactAfterUpdate){
        assertThat("getCustomerCommunicationDetails are  same", customerImpactBeforeUpdate.getCustomerCommunicationDetails(), equalTo(customerImpactBeforeUpdate.getCustomerCommunicationDetails()));
        assertThat("statuses are  same", customerImpactBeforeUpdate.getStatus(), equalTo(customerImpactAfterUpdate.getStatus()));
        assertThat("getChangeToCustomerImpactCriticalPartDetails are  same", customerImpactBeforeUpdate.getChangeToCustomerImpactCriticalPartDetails(), equalTo(customerImpactAfterUpdate.getChangeToCustomerImpactCriticalPartDetails()));
        assertThat("getCustomerApproval are  same", customerImpactBeforeUpdate.getCustomerApproval(), equalTo(customerImpactAfterUpdate.getCustomerApproval()));
        assertThat("getChangeToCustomerImpactCriticalPartDetails are  same", customerImpactBeforeUpdate.getChangeToCustomerImpactCriticalPartDetails(), equalTo(customerImpactAfterUpdate.getChangeToCustomerImpactCriticalPartDetails()));
        assertThat("getFcoUpgradeOptionCsrImplementationChangeDetails are  same", customerImpactBeforeUpdate.getFcoUpgradeOptionCsrImplementationChangeDetails(), equalTo(customerImpactAfterUpdate.getFcoUpgradeOptionCsrImplementationChangeDetails()));
        assertThat("ChangeToCustomerImpactCriticalPartDetails are same", customerImpactBeforeUpdate.getChangeToCustomerImpactCriticalPartDetails(), equalTo(customerImpactAfterUpdate.getChangeToCustomerImpactCriticalPartDetails()));
        assertThat("CustomerApprovalDetails are same", customerImpactBeforeUpdate.getCustomerApprovalDetails(), equalTo(customerImpactAfterUpdate.getCustomerApprovalDetails()));

    }
    public static void customerImpactAreSameWithoutComparingFcoUpgradeOptionCsrImplementationChangeDetails(CustomerImpact customerImpactBeforeUpdate, CustomerImpact customerImpactAfterUpdate){
        assertThat("getCustomerCommunicationDetails are  same", customerImpactBeforeUpdate.getCustomerCommunicationDetails(), equalTo(customerImpactBeforeUpdate.getCustomerCommunicationDetails()));
        assertThat("statuses are  same", customerImpactBeforeUpdate.getStatus(), equalTo(customerImpactAfterUpdate.getStatus()));
        assertThat("getChangeToCustomerImpactCriticalPartDetails are  same", customerImpactBeforeUpdate.getChangeToCustomerImpactCriticalPartDetails(), equalTo(customerImpactAfterUpdate.getChangeToCustomerImpactCriticalPartDetails()));
        assertThat("getCustomerApproval are  same", customerImpactBeforeUpdate.getCustomerApproval(), equalTo(customerImpactAfterUpdate.getCustomerApproval()));
        assertThat("getChangeToCustomerImpactCriticalPartDetails are  same", customerImpactBeforeUpdate.getChangeToCustomerImpactCriticalPartDetails(), equalTo(customerImpactAfterUpdate.getChangeToCustomerImpactCriticalPartDetails()));
        assertThat("ChangeToProcessImpactingCustomerDetails are same", customerImpactBeforeUpdate.getChangeToProcessImpactingCustomerDetails(), equalTo(customerImpactAfterUpdate.getChangeToProcessImpactingCustomerDetails()));
        assertThat("CustomerApprovalDetails are same", customerImpactBeforeUpdate.getCustomerApprovalDetails(), equalTo(customerImpactAfterUpdate.getCustomerApprovalDetails()));
    }

    public static void customerImpactAreSameWithoutComparinggetPreinstallImpactResult(PreinstallImpact preinstallImpactBeforeUpdate, PreinstallImpact preinstallImpactAfterUpdate){
        assertThat("getChangeReplacesMentionedPartsDetails are  same", preinstallImpactBeforeUpdate.getChangeReplacesMentionedPartsDetails(), equalTo(preinstallImpactAfterUpdate.getChangeReplacesMentionedPartsDetails()));
        assertThat("statuses are  same", preinstallImpactBeforeUpdate.getStatus(), equalTo(preinstallImpactAfterUpdate.getStatus()));
        assertThat("getChangeIntroducesNew11NcDetails are  same", preinstallImpactBeforeUpdate.getChangeIntroducesNew11NcDetails(), equalTo(preinstallImpactAfterUpdate.getChangeIntroducesNew11NcDetails()));
        assertThat("getImpactOnCustomerFactoryLayoutDetails are  same", preinstallImpactBeforeUpdate.getImpactOnCustomerFactoryLayoutDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnCustomerFactoryLayoutDetails()));
        assertThat("getImpactOnFacilityFlowsDetails are  same", preinstallImpactBeforeUpdate.getImpactOnFacilityFlowsDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnFacilityFlowsDetails()));
        assertThat("getChangeIntroducesNew11NcDetails are same", preinstallImpactBeforeUpdate.getChangeIntroducesNew11NcDetails(), equalTo(preinstallImpactAfterUpdate.getChangeIntroducesNew11NcDetails()));
        assertThat("ImpactOnFacilityFlowsDetails are same", preinstallImpactBeforeUpdate.getImpactOnFacilityFlowsDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnFacilityFlowsDetails()));
        assertThat("ImpactOnCustomerFactoryLayoutDetails are same", preinstallImpactBeforeUpdate.getImpactOnCustomerFactoryLayoutDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnCustomerFactoryLayoutDetails()));
        assertThat("ImpactOnPreinstallInterConnectCablesDetails are same", preinstallImpactBeforeUpdate.getImpactOnPreinstallInterConnectCablesDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnPreinstallInterConnectCablesDetails()));
        assertThat("getChangeReplacesMentionedPartsDetails are same", preinstallImpactBeforeUpdate.getChangeReplacesMentionedPartsDetails(), equalTo(preinstallImpactAfterUpdate.getChangeReplacesMentionedPartsDetails()));
    }
    public static void customerImpactAreSameWithoutComparingChangeIntroducesNew11NCDetails(PreinstallImpact preinstallImpactBeforeUpdate, PreinstallImpact preinstallImpactAfterUpdate){
        assertThat("getChangeReplacesMentionedPartsDetails are  same", preinstallImpactBeforeUpdate.getChangeReplacesMentionedPartsDetails(), equalTo(preinstallImpactAfterUpdate.getChangeReplacesMentionedPartsDetails()));
        assertThat("statuses are  same", preinstallImpactBeforeUpdate.getStatus(), equalTo(preinstallImpactAfterUpdate.getStatus()));
        assertThat("getImpactOnCustomerFactoryLayoutDetails are  same", preinstallImpactBeforeUpdate.getImpactOnCustomerFactoryLayoutDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnCustomerFactoryLayoutDetails()));
        assertThat("getImpactOnFacilityFlowsDetails are  same", preinstallImpactBeforeUpdate.getImpactOnFacilityFlowsDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnFacilityFlowsDetails()));
        assertThat("ImpactOnFacilityFlowsDetails are same", preinstallImpactBeforeUpdate.getImpactOnFacilityFlowsDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnFacilityFlowsDetails()));
        assertThat("ImpactOnCustomerFactoryLayoutDetails are same", preinstallImpactBeforeUpdate.getImpactOnCustomerFactoryLayoutDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnCustomerFactoryLayoutDetails()));
        assertThat("ImpactOnPreinstallInterConnectCablesDetails are same", preinstallImpactBeforeUpdate.getImpactOnPreinstallInterConnectCablesDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnPreinstallInterConnectCablesDetails()));
        assertThat("ImpactOnPreinstallInterConnectCablesDetails are same", preinstallImpactBeforeUpdate.getPreinstallImpactResult(), equalTo(preinstallImpactAfterUpdate.getPreinstallImpactResult()));
        assertThat("getChangeReplacesMentionedPartsDetails are same", preinstallImpactBeforeUpdate.getChangeReplacesMentionedPartsDetails(), equalTo(preinstallImpactAfterUpdate.getChangeReplacesMentionedPartsDetails()));
    }
    public static void customerImpactAreSameWithoutComparingImpactOnFacilityFlowsDetails(PreinstallImpact preinstallImpactBeforeUpdate, PreinstallImpact preinstallImpactAfterUpdate){
        assertThat("getChangeReplacesMentionedPartsDetails are  same", preinstallImpactBeforeUpdate.getChangeReplacesMentionedPartsDetails(), equalTo(preinstallImpactAfterUpdate.getChangeReplacesMentionedPartsDetails()));
        assertThat("statuses are  same", preinstallImpactBeforeUpdate.getStatus(), equalTo(preinstallImpactAfterUpdate.getStatus()));
        assertThat("getImpactOnCustomerFactoryLayoutDetails are  same", preinstallImpactBeforeUpdate.getImpactOnCustomerFactoryLayoutDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnCustomerFactoryLayoutDetails()));
        assertThat("getChangeIntroducesNew11NcDetails are  same", preinstallImpactBeforeUpdate.getChangeIntroducesNew11NcDetails(), equalTo(preinstallImpactAfterUpdate.getChangeIntroducesNew11NcDetails()));
        assertThat("getChangeIntroducesNew11NcDetails are same", preinstallImpactBeforeUpdate.getChangeIntroducesNew11NcDetails(), equalTo(preinstallImpactAfterUpdate.getChangeIntroducesNew11NcDetails()));
        assertThat("ImpactOnCustomerFactoryLayoutDetails are same", preinstallImpactBeforeUpdate.getImpactOnCustomerFactoryLayoutDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnCustomerFactoryLayoutDetails()));
        assertThat("ImpactOnPreinstallInterConnectCablesDetails are same", preinstallImpactBeforeUpdate.getImpactOnPreinstallInterConnectCablesDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnPreinstallInterConnectCablesDetails()));
        assertThat("ImpactOnPreinstallInterConnectCablesDetails are same", preinstallImpactBeforeUpdate.getPreinstallImpactResult(), equalTo(preinstallImpactAfterUpdate.getPreinstallImpactResult()));
        assertThat("getChangeReplacesMentionedPartsDetails are same", preinstallImpactBeforeUpdate.getChangeReplacesMentionedPartsDetails(), equalTo(preinstallImpactAfterUpdate.getChangeReplacesMentionedPartsDetails()));
    }
    public static void customerImpactAreSameWithoutComparingimpactOnPreinstallInterConnectCablesDetails(PreinstallImpact preinstallImpactBeforeUpdate, PreinstallImpact preinstallImpactAfterUpdate){
        assertThat("getChangeReplacesMentionedPartsDetails are  same", preinstallImpactBeforeUpdate.getChangeReplacesMentionedPartsDetails(), equalTo(preinstallImpactAfterUpdate.getChangeReplacesMentionedPartsDetails()));
        assertThat("statuses are  same", preinstallImpactBeforeUpdate.getStatus(), equalTo(preinstallImpactAfterUpdate.getStatus()));
        assertThat("getChangeIntroducesNew11NcDetails are  same", preinstallImpactBeforeUpdate.getChangeIntroducesNew11NcDetails(), equalTo(preinstallImpactAfterUpdate.getChangeIntroducesNew11NcDetails()));
        assertThat("getImpactOnCustomerFactoryLayoutDetails are  same", preinstallImpactBeforeUpdate.getImpactOnCustomerFactoryLayoutDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnCustomerFactoryLayoutDetails()));
        assertThat("getImpactOnFacilityFlowsDetails are  same", preinstallImpactBeforeUpdate.getImpactOnFacilityFlowsDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnFacilityFlowsDetails()));
        assertThat("getChangeIntroducesNew11NcDetails are same", preinstallImpactBeforeUpdate.getChangeIntroducesNew11NcDetails(), equalTo(preinstallImpactAfterUpdate.getChangeIntroducesNew11NcDetails()));
        assertThat("ImpactOnFacilityFlowsDetails are same", preinstallImpactBeforeUpdate.getImpactOnFacilityFlowsDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnFacilityFlowsDetails()));
        assertThat("ImpactOnCustomerFactoryLayoutDetails are same", preinstallImpactBeforeUpdate.getImpactOnCustomerFactoryLayoutDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnCustomerFactoryLayoutDetails()));
        assertThat("PreInstallImpactResult are same", preinstallImpactBeforeUpdate.getPreinstallImpactResult(), equalTo(preinstallImpactAfterUpdate.getPreinstallImpactResult()));
        assertThat("getChangeReplacesMentionedPartsDetails are same", preinstallImpactBeforeUpdate.getChangeReplacesMentionedPartsDetails(), equalTo(preinstallImpactAfterUpdate.getChangeReplacesMentionedPartsDetails()));
    }

    public static void customerImpactAreSameWithoutComparingChangeReplacesMentionedPartsDetails(PreinstallImpact preinstallImpactBeforeUpdate, PreinstallImpact preinstallImpactAfterUpdate){
        assertThat("statuses are  same", preinstallImpactBeforeUpdate.getStatus(), equalTo(preinstallImpactAfterUpdate.getStatus()));
        assertThat("getChangeIntroducesNew11NcDetails are  same", preinstallImpactBeforeUpdate.getChangeIntroducesNew11NcDetails(), equalTo(preinstallImpactAfterUpdate.getChangeIntroducesNew11NcDetails()));
        assertThat("getImpactOnCustomerFactoryLayoutDetails are  same", preinstallImpactBeforeUpdate.getImpactOnCustomerFactoryLayoutDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnCustomerFactoryLayoutDetails()));
        assertThat("getImpactOnFacilityFlowsDetails are  same", preinstallImpactBeforeUpdate.getImpactOnFacilityFlowsDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnFacilityFlowsDetails()));
        assertThat("getChangeIntroducesNew11NcDetails are same", preinstallImpactBeforeUpdate.getChangeIntroducesNew11NcDetails(), equalTo(preinstallImpactAfterUpdate.getChangeIntroducesNew11NcDetails()));
        assertThat("ImpactOnFacilityFlowsDetails are same", preinstallImpactBeforeUpdate.getImpactOnFacilityFlowsDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnFacilityFlowsDetails()));
        assertThat("ImpactOnCustomerFactoryLayoutDetails are same", preinstallImpactBeforeUpdate.getImpactOnCustomerFactoryLayoutDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnCustomerFactoryLayoutDetails()));
        assertThat("ImpactOnPreinstallInterConnectCablesDetails are same", preinstallImpactBeforeUpdate.getImpactOnPreinstallInterConnectCablesDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnPreinstallInterConnectCablesDetails()));
        assertThat("ImpactOnPreinstallInterConnectCablesDetails are same", preinstallImpactBeforeUpdate.getPreinstallImpactResult(), equalTo(preinstallImpactAfterUpdate.getPreinstallImpactResult()));

    }
    public static void customerImpactAreSameWithoutComparinImpactOnCustomerFactoryLayoutDetails(PreinstallImpact preinstallImpactBeforeUpdate, PreinstallImpact preinstallImpactAfterUpdate){
        assertThat("getChangeReplacesMentionedPartsDetails are  same", preinstallImpactBeforeUpdate.getChangeReplacesMentionedPartsDetails(), equalTo(preinstallImpactAfterUpdate.getChangeReplacesMentionedPartsDetails()));
        assertThat("statuses are  same", preinstallImpactBeforeUpdate.getStatus(), equalTo(preinstallImpactAfterUpdate.getStatus()));
        assertThat("getChangeIntroducesNew11NcDetails are  same", preinstallImpactBeforeUpdate.getChangeIntroducesNew11NcDetails(), equalTo(preinstallImpactAfterUpdate.getChangeIntroducesNew11NcDetails()));
        assertThat("getImpactOnFacilityFlowsDetails are  same", preinstallImpactBeforeUpdate.getImpactOnFacilityFlowsDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnFacilityFlowsDetails()));
        assertThat("getChangeIntroducesNew11NcDetails are same", preinstallImpactBeforeUpdate.getChangeIntroducesNew11NcDetails(), equalTo(preinstallImpactAfterUpdate.getChangeIntroducesNew11NcDetails()));
        assertThat("ImpactOnFacilityFlowsDetails are same", preinstallImpactBeforeUpdate.getImpactOnFacilityFlowsDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnFacilityFlowsDetails()));
        assertThat("ImpactOnPreinstallInterConnectCablesDetails are same", preinstallImpactBeforeUpdate.getImpactOnPreinstallInterConnectCablesDetails(), equalTo(preinstallImpactAfterUpdate.getImpactOnPreinstallInterConnectCablesDetails()));
        assertThat("ImpactOnPreinstallInterConnectCablesDetails are same", preinstallImpactBeforeUpdate.getPreinstallImpactResult(), equalTo(preinstallImpactAfterUpdate.getPreinstallImpactResult()));
        assertThat("getChangeReplacesMentionedPartsDetails are same", preinstallImpactBeforeUpdate.getChangeReplacesMentionedPartsDetails(), equalTo(preinstallImpactAfterUpdate.getChangeReplacesMentionedPartsDetails()));
    }
    public static void impactAnalysisAreSameWithoutComparingCbpStrategiesDetails(ImpactAnalysis impactAnalysisBeforeUpdate, ImpactAnalysis impactAnalysisAfterUpdate){
        assertThat("statuses are  same", impactAnalysisBeforeUpdate.getStatus(), equalTo(impactAnalysisAfterUpdate.getStatus()));
        assertThat("getImplementationRanges are same", impactAnalysisBeforeUpdate.getImplementationRanges(), equalTo(impactAnalysisBeforeUpdate.getImplementationRanges()));
        assertThat("getCbpStrategies are same", impactAnalysisBeforeUpdate.getCbpStrategies(), equalTo(impactAnalysisAfterUpdate.getCbpStrategies()));
        assertThat("getImpactOnAvailability are same", impactAnalysisBeforeUpdate.getImpactOnAvailability(), equalTo(impactAnalysisAfterUpdate.getImpactOnAvailability()));
        assertThat("getImpactOnAvailability are same", impactAnalysisBeforeUpdate.getImpactOnSystemLevelPerformance(), equalTo(impactAnalysisAfterUpdate.getImpactOnSystemLevelPerformance()));
        assertThat("getImpactOnExistingParts are same", impactAnalysisBeforeUpdate.getImpactOnExistingParts(), equalTo(impactAnalysisAfterUpdate.getImpactOnExistingParts()));
        assertThat("impactAnalysisAfterUpdate are same", impactAnalysisBeforeUpdate.getImpactOnSequenceDetails(), equalTo(impactAnalysisAfterUpdate.getImpactOnSequenceDetails()));
    }
    public static void impactAnalysisAreSameWithoutComparingImpactOnSequenceDetails(ImpactAnalysis impactAnalysisBeforeUpdate, ImpactAnalysis impactAnalysisAfterUpdate){
        assertThat("statuses are  same", impactAnalysisBeforeUpdate.getStatus(), equalTo(impactAnalysisAfterUpdate.getStatus()));
        assertThat("getImplementationRanges are same", impactAnalysisBeforeUpdate.getImplementationRanges(), equalTo(impactAnalysisBeforeUpdate.getImplementationRanges()));
        assertThat("getCbpStrategies are same", impactAnalysisBeforeUpdate.getCbpStrategies(), equalTo(impactAnalysisAfterUpdate.getCbpStrategies()));
        assertThat("getImpactOnAvailability are same", impactAnalysisBeforeUpdate.getImpactOnAvailability(), equalTo(impactAnalysisAfterUpdate.getImpactOnAvailability()));
        assertThat("getImpactOnSystemLevelPerformance are same", impactAnalysisBeforeUpdate.getImpactOnSystemLevelPerformance(), equalTo(impactAnalysisAfterUpdate.getImpactOnSystemLevelPerformance()));
        assertThat("getImpactOnExistingParts are same", impactAnalysisBeforeUpdate.getImpactOnExistingParts(), equalTo(impactAnalysisAfterUpdate.getImpactOnExistingParts()));
    }

    public static void impactAnalysisAreSameWithoutComparingImpactOnAvailabilityDetails(ImpactAnalysis impactAnalysisBeforeUpdate, ImpactAnalysis impactAnalysisAfterUpdate){
        assertThat("statuses are  same", impactAnalysisBeforeUpdate.getStatus(), equalTo(impactAnalysisAfterUpdate.getStatus()));
        assertThat("getImplementationRanges are same", impactAnalysisBeforeUpdate.getImplementationRanges(), equalTo(impactAnalysisBeforeUpdate.getImplementationRanges()));
        assertThat("getCbpStrategies are same", impactAnalysisBeforeUpdate.getCbpStrategies(), equalTo(impactAnalysisAfterUpdate.getCbpStrategies()));
        assertThat("getImpactOnSystemLevelPerformance are same", impactAnalysisBeforeUpdate.getImpactOnSystemLevelPerformance(), equalTo(impactAnalysisAfterUpdate.getImpactOnSystemLevelPerformance()));
        assertThat("getImpactOnExistingParts are same", impactAnalysisBeforeUpdate.getImpactOnExistingParts(), equalTo(impactAnalysisAfterUpdate.getImpactOnExistingParts()));
        assertThat("ImpactOnSystemLevelPerformanceDetails are same", impactAnalysisBeforeUpdate.getImpactOnSystemLevelPerformanceDetails(), equalTo(impactAnalysisAfterUpdate.getImpactOnSystemLevelPerformanceDetails()));
        assertThat("getImpactOnExistingParts are same", impactAnalysisBeforeUpdate.getImpactOnSystemLevelPerformanceDetails(), equalTo(impactAnalysisAfterUpdate.getImpactOnSystemLevelPerformanceDetails()));
        assertThat("getImpactOnExistingParts are same", impactAnalysisBeforeUpdate.getImpactOnSystemLevelPerformanceDetails(), equalTo(impactAnalysisAfterUpdate.getImpactOnSystemLevelPerformanceDetails()));


    }
    public static void impactAnalysisAreSameWithoutComparingPhaseOutSparesToolsDetails(ImpactAnalysis impactAnalysisBeforeUpdate, ImpactAnalysis impactAnalysisAfterUpdate){
        assertThat("statuses are  same", impactAnalysisBeforeUpdate.getStatus(), equalTo(impactAnalysisAfterUpdate.getStatus()));
        assertThat("getImplementationRanges are same", impactAnalysisBeforeUpdate.getImplementationRanges(), equalTo(impactAnalysisBeforeUpdate.getImplementationRanges()));
        assertThat("getCbpStrategies are same", impactAnalysisBeforeUpdate.getCbpStrategies(), equalTo(impactAnalysisAfterUpdate.getCbpStrategies()));
        assertThat("getImpactOnSystemLevelPerformance are same", impactAnalysisBeforeUpdate.getImpactOnSystemLevelPerformance(), equalTo(impactAnalysisAfterUpdate.getImpactOnSystemLevelPerformance()));
        assertThat("getImpactOnExistingParts are same", impactAnalysisBeforeUpdate.getImpactOnExistingParts(), equalTo(impactAnalysisAfterUpdate.getImpactOnExistingParts()));
        assertThat("getImpactOnCycleTimeDetails are same", impactAnalysisBeforeUpdate.getImpactOnCycleTimeDetails(), equalTo(impactAnalysisAfterUpdate.getImpactOnCycleTimeDetails()));
        assertThat("getImpactOnSystemLevelPerformanceDetails are same", impactAnalysisBeforeUpdate.getImpactOnSystemLevelPerformanceDetails(), equalTo(impactAnalysisAfterUpdate.getImpactOnSystemLevelPerformanceDetails()));
        assertThat("getImplementationRangesDetails are same", impactAnalysisBeforeUpdate.getImplementationRangesDetails(), equalTo(impactAnalysisAfterUpdate.getImplementationRangesDetails()));

    }
    public static void impactAnalysisAreSameWithoutComparingTechRiskAssessmentSraDetails(ImpactAnalysis impactAnalysisBeforeUpdate, ImpactAnalysis impactAnalysisAfterUpdate){
        assertThat("statuses are  same", impactAnalysisBeforeUpdate.getStatus(), equalTo(impactAnalysisAfterUpdate.getStatus()));
        assertThat("getImplementationRanges are same", impactAnalysisBeforeUpdate.getImplementationRanges(), equalTo(impactAnalysisBeforeUpdate.getImplementationRanges()));
        assertThat("getCbpStrategies are same", impactAnalysisBeforeUpdate.getCbpStrategies(), equalTo(impactAnalysisAfterUpdate.getCbpStrategies()));
        assertThat("getImpactOnSystemLevelPerformance are same", impactAnalysisBeforeUpdate.getImpactOnSystemLevelPerformance(), equalTo(impactAnalysisAfterUpdate.getImpactOnSystemLevelPerformance()));
        assertThat("getImpactOnExistingParts are same", impactAnalysisBeforeUpdate.getImpactOnExistingParts(), equalTo(impactAnalysisAfterUpdate.getImpactOnExistingParts()));
        assertThat("getImpactOnCycleTimeDetails are same", impactAnalysisBeforeUpdate.getImpactOnCycleTimeDetails(), equalTo(impactAnalysisAfterUpdate.getImpactOnCycleTimeDetails()));
        assertThat("getImpactOnSystemLevelPerformanceDetails are same", impactAnalysisBeforeUpdate.getImpactOnSystemLevelPerformanceDetails(), equalTo(impactAnalysisAfterUpdate.getImpactOnSystemLevelPerformanceDetails()));
        assertThat("getImplementationRangesDetails are same", impactAnalysisBeforeUpdate.getImplementationRangesDetails(), equalTo(impactAnalysisAfterUpdate.getImplementationRangesDetails()));

    }
    public static void impactAnalysisAreSameWithoutComparingTechRiskAssessmentFmeaDetails(ImpactAnalysis impactAnalysisBeforeUpdate, ImpactAnalysis impactAnalysisAfterUpdate){
        assertThat("statuses are  same", impactAnalysisBeforeUpdate.getStatus(), equalTo(impactAnalysisAfterUpdate.getStatus()));
        assertThat("getImplementationRanges are same", impactAnalysisBeforeUpdate.getImplementationRanges(), equalTo(impactAnalysisBeforeUpdate.getImplementationRanges()));
        assertThat("getCbpStrategies are same", impactAnalysisBeforeUpdate.getCbpStrategies(), equalTo(impactAnalysisAfterUpdate.getCbpStrategies()));
        assertThat("getImpactOnSystemLevelPerformance are same", impactAnalysisBeforeUpdate.getImpactOnSystemLevelPerformance(), equalTo(impactAnalysisAfterUpdate.getImpactOnSystemLevelPerformance()));
        assertThat("getImpactOnExistingParts are same", impactAnalysisBeforeUpdate.getImpactOnExistingParts(), equalTo(impactAnalysisAfterUpdate.getImpactOnExistingParts()));
        assertThat("getImpactOnCycleTimeDetails are same", impactAnalysisBeforeUpdate.getImpactOnCycleTimeDetails(), equalTo(impactAnalysisAfterUpdate.getImpactOnCycleTimeDetails()));
        assertThat("getImpactOnSystemLevelPerformanceDetails are same", impactAnalysisBeforeUpdate.getImpactOnSystemLevelPerformanceDetails(), equalTo(impactAnalysisAfterUpdate.getImpactOnSystemLevelPerformanceDetails()));
        assertThat("getImplementationRangesDetails are same", impactAnalysisBeforeUpdate.getImplementationRangesDetails(), equalTo(impactAnalysisAfterUpdate.getImplementationRangesDetails()));

    }
    public static void impactAnalysisAreSameWithoutComparingTotalInstancesAffected(ImpactAnalysis impactAnalysisBeforeUpdate, ImpactAnalysis impactAnalysisAfterUpdate) {
        assertThat("statuses are  same", impactAnalysisBeforeUpdate.getStatus(), equalTo(impactAnalysisAfterUpdate.getStatus()));
        assertThat("getImplementationRanges are same", impactAnalysisBeforeUpdate.getImplementationRanges(), equalTo(impactAnalysisBeforeUpdate.getImplementationRanges()));
        assertThat("getCbpStrategies are same", impactAnalysisBeforeUpdate.getCbpStrategies(), equalTo(impactAnalysisAfterUpdate.getCbpStrategies()));
        assertThat("getImpactOnSystemLevelPerformance are same", impactAnalysisBeforeUpdate.getImpactOnSystemLevelPerformance(), equalTo(impactAnalysisAfterUpdate.getImpactOnSystemLevelPerformance()));
        assertThat("getImpactOnExistingParts are same", impactAnalysisBeforeUpdate.getImpactOnExistingParts(), equalTo(impactAnalysisAfterUpdate.getImpactOnExistingParts()));
        assertThat("getImpactOnCycleTimeDetails are same", impactAnalysisBeforeUpdate.getImpactOnCycleTimeDetails(), equalTo(impactAnalysisAfterUpdate.getImpactOnCycleTimeDetails()));
        assertThat("getImpactOnSystemLevelPerformanceDetails are same", impactAnalysisBeforeUpdate.getImpactOnSystemLevelPerformanceDetails(), equalTo(impactAnalysisAfterUpdate.getImpactOnSystemLevelPerformanceDetails()));
        assertThat("getImplementationRangesDetails are same", impactAnalysisBeforeUpdate.getImplementationRangesDetails(), equalTo(impactAnalysisAfterUpdate.getImplementationRangesDetails()));
    }

    public static void impactAnalysisAreSameWithoutComparingImpactOnSystemLevelPerformanceDetails(ImpactAnalysis impactAnalysisBeforeUpdate, ImpactAnalysis impactAnalysisAfterUpdate) {
        assertThat("statuses are  same", impactAnalysisBeforeUpdate.getStatus(), equalTo(impactAnalysisAfterUpdate.getStatus()));
        assertThat("getImplementationRanges are same", impactAnalysisBeforeUpdate.getImplementationRanges(), equalTo(impactAnalysisBeforeUpdate.getImplementationRanges()));
        assertThat("getCbpStrategies are same", impactAnalysisBeforeUpdate.getCbpStrategies(), equalTo(impactAnalysisAfterUpdate.getCbpStrategies()));
        assertThat("getImpactOnSystemLevelPerformance are same", impactAnalysisBeforeUpdate.getImpactOnSystemLevelPerformance(), equalTo(impactAnalysisAfterUpdate.getImpactOnSystemLevelPerformance()));
        assertThat("getImpactOnExistingParts are same", impactAnalysisBeforeUpdate.getImpactOnExistingParts(), equalTo(impactAnalysisAfterUpdate.getImpactOnExistingParts()));
        assertThat("getImpactOnCycleTimeDetails are same", impactAnalysisBeforeUpdate.getImpactOnCycleTimeDetails(), equalTo(impactAnalysisAfterUpdate.getImpactOnCycleTimeDetails()));
        assertThat("getImplementationRangesDetails are same", impactAnalysisBeforeUpdate.getImplementationRangesDetails(), equalTo(impactAnalysisAfterUpdate.getImplementationRangesDetails()));
    }

    public static void impactAnalysisAreSameWithoutComparingImpactOnCycleTimeDetails(ImpactAnalysis impactAnalysisBeforeUpdate, ImpactAnalysis impactAnalysisAfterUpdate) {
        assertThat("statuses are  same", impactAnalysisBeforeUpdate.getStatus(), equalTo(impactAnalysisAfterUpdate.getStatus()));
        assertThat("getImplementationRanges are same", impactAnalysisBeforeUpdate.getImplementationRanges(), equalTo(impactAnalysisBeforeUpdate.getImplementationRanges()));
        assertThat("getCbpStrategies are same", impactAnalysisBeforeUpdate.getCbpStrategies(), equalTo(impactAnalysisAfterUpdate.getCbpStrategies()));
        assertThat("getImpactOnSystemLevelPerformance are same", impactAnalysisBeforeUpdate.getImpactOnSystemLevelPerformance(), equalTo(impactAnalysisAfterUpdate.getImpactOnSystemLevelPerformance()));
        assertThat("getImpactOnExistingParts are same", impactAnalysisBeforeUpdate.getImpactOnExistingParts(), equalTo(impactAnalysisAfterUpdate.getImpactOnExistingParts()));
        assertThat("getImpactOnSystemLevelPerformanceDetails are same", impactAnalysisBeforeUpdate.getImpactOnSystemLevelPerformanceDetails(), equalTo(impactAnalysisAfterUpdate.getImpactOnSystemLevelPerformanceDetails()));
        assertThat("getImplementationRangesDetails are same", impactAnalysisBeforeUpdate.getImplementationRangesDetails(), equalTo(impactAnalysisAfterUpdate.getImplementationRangesDetails()));
    }

    public static void impactAnalysisAreSameWithoutComparingImplementationRangesDetails(ImpactAnalysis impactAnalysisBeforeUpdate, ImpactAnalysis impactAnalysisAfterUpdate) {
        assertThat("statuses are  same", impactAnalysisBeforeUpdate.getStatus(), equalTo(impactAnalysisAfterUpdate.getStatus()));
        assertThat("getImplementationRanges are same", impactAnalysisBeforeUpdate.getImplementationRanges(), equalTo(impactAnalysisBeforeUpdate.getImplementationRanges()));
        assertThat("getCbpStrategies are same", impactAnalysisBeforeUpdate.getCbpStrategies(), equalTo(impactAnalysisAfterUpdate.getCbpStrategies()));
        assertThat("getImpactOnSystemLevelPerformance are same", impactAnalysisBeforeUpdate.getImpactOnSystemLevelPerformance(), equalTo(impactAnalysisAfterUpdate.getImpactOnSystemLevelPerformance()));
        assertThat("getImpactOnExistingParts are same", impactAnalysisBeforeUpdate.getImpactOnExistingParts(), equalTo(impactAnalysisAfterUpdate.getImpactOnExistingParts()));
        assertThat("getImpactOnSystemLevelPerformanceDetails are same", impactAnalysisBeforeUpdate.getImpactOnSystemLevelPerformanceDetails(), equalTo(impactAnalysisAfterUpdate.getImpactOnSystemLevelPerformanceDetails()));
        assertThat("getImpactOnCycleTimeDetails are same", impactAnalysisBeforeUpdate.getImpactOnCycleTimeDetails(), equalTo(impactAnalysisAfterUpdate.getImpactOnCycleTimeDetails()));

    }
    public static void impactAnalysisAreSameWithoutComparingFcoTypes(ImpactAnalysis impactAnalysisBeforeUpdate, ImpactAnalysis impactAnalysisAfterUpdate) {
        assertThat("statuses are  same", impactAnalysisBeforeUpdate.getStatus(), equalTo(impactAnalysisAfterUpdate.getStatus()));
        assertThat("getImplementationRanges are same", impactAnalysisBeforeUpdate.getImplementationRanges(), equalTo(impactAnalysisBeforeUpdate.getImplementationRanges()));
        assertThat("getCbpStrategies are same", impactAnalysisBeforeUpdate.getCbpStrategies(), equalTo(impactAnalysisAfterUpdate.getCbpStrategies()));
        assertThat("getImpactOnSystemLevelPerformance are same", impactAnalysisBeforeUpdate.getImpactOnSystemLevelPerformance(), equalTo(impactAnalysisAfterUpdate.getImpactOnSystemLevelPerformance()));
        assertThat("getImpactOnExistingParts are same", impactAnalysisBeforeUpdate.getImpactOnExistingParts(), equalTo(impactAnalysisAfterUpdate.getImpactOnExistingParts()));
        assertThat("getImpactOnSystemLevelPerformanceDetails are same", impactAnalysisBeforeUpdate.getImpactOnSystemLevelPerformanceDetails(), equalTo(impactAnalysisAfterUpdate.getImpactOnSystemLevelPerformanceDetails()));
        assertThat("getImpactOnCycleTimeDetails are same", impactAnalysisBeforeUpdate.getImpactOnCycleTimeDetails(), equalTo(impactAnalysisAfterUpdate.getImpactOnCycleTimeDetails()));

    }

    public static void completeBusinessCaseAreSameWithoutComparingSystemStartsImpacted(CompleteBusinessCase completeBusinessCaseBeforeUpdate, CompleteBusinessCase completeBusinessCaseAfterUpdate) {
        assertThat("statuses are  same", completeBusinessCaseBeforeUpdate.getStatus(), equalTo(completeBusinessCaseAfterUpdate.getStatus()));
        assertThat("getexampleSavings are same", completeBusinessCaseBeforeUpdate.getexampleSavings(), equalTo(completeBusinessCaseAfterUpdate.getexampleSavings()));
        assertThat("getCustomerOpexSavings are same", completeBusinessCaseBeforeUpdate.getCustomerOpexSavings(), equalTo(completeBusinessCaseAfterUpdate.getCustomerOpexSavings()));
        assertThat("getImpactOnSystemLevelPerformance are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getCycleTimeRecurringCosts are same", completeBusinessCaseBeforeUpdate.getCycleTimeRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getCycleTimeRecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getFarmOutDevelopmentNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFarmOutDevelopmentNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFarmOutDevelopmentNonrecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getCustomerUptimeImprovementBenefits are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getFieldChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFieldChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFieldChangeOrderNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("getMaterialRecurringCosts are same", completeBusinessCaseBeforeUpdate.getMaterialRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getMaterialRecurringCosts()));
        assertThat("getPrototypeMaterialsNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getPrototypeMaterialsNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getPrototypeMaterialsNonrecurringCosts()));
        assertThat("getUpdateUpgradeProductDocumentationNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("getHardwareCommitment are same", completeBusinessCaseBeforeUpdate.getHardwareCommitment(), equalTo(completeBusinessCaseAfterUpdate.getHardwareCommitment()));


    }
    public static void completeBusinessCaseAreSameWithoutComparingRisk(CompleteBusinessCase completeBusinessCaseBeforeUpdate, CompleteBusinessCase completeBusinessCaseAfterUpdate) {
        assertThat("statuses are  same", completeBusinessCaseBeforeUpdate.getStatus(), equalTo(completeBusinessCaseAfterUpdate.getStatus()));
        assertThat("getexampleSavings are same", completeBusinessCaseBeforeUpdate.getexampleSavings(), equalTo(completeBusinessCaseAfterUpdate.getexampleSavings()));
        assertThat("getCustomerOpexSavings are same", completeBusinessCaseBeforeUpdate.getCustomerOpexSavings(), equalTo(completeBusinessCaseAfterUpdate.getCustomerOpexSavings()));
        assertThat("getImpactOnSystemLevelPerformance are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getCycleTimeRecurringCosts are same", completeBusinessCaseBeforeUpdate.getCycleTimeRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getCycleTimeRecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getFarmOutDevelopmentNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFarmOutDevelopmentNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFarmOutDevelopmentNonrecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getCustomerUptimeImprovementBenefits are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getFieldChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFieldChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFieldChangeOrderNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("getMaterialRecurringCosts are same", completeBusinessCaseBeforeUpdate.getMaterialRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getMaterialRecurringCosts()));
        assertThat("getPrototypeMaterialsNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getPrototypeMaterialsNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getPrototypeMaterialsNonrecurringCosts()));
        assertThat("getUpdateUpgradeProductDocumentationNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("getHardwareCommitment are same", completeBusinessCaseBeforeUpdate.getHardwareCommitment(), equalTo(completeBusinessCaseAfterUpdate.getHardwareCommitment()));


    }
    public static void completeBusinessCaseAreSameWithoutComparingRiskInLaborHours(CompleteBusinessCase completeBusinessCaseBeforeUpdate, CompleteBusinessCase completeBusinessCaseAfterUpdate) {
        assertThat("statuses are  same", completeBusinessCaseBeforeUpdate.getStatus(), equalTo(completeBusinessCaseAfterUpdate.getStatus()));
        assertThat("getexampleSavings are same", completeBusinessCaseBeforeUpdate.getexampleSavings(), equalTo(completeBusinessCaseAfterUpdate.getexampleSavings()));
        assertThat("getCustomerOpexSavings are same", completeBusinessCaseBeforeUpdate.getCustomerOpexSavings(), equalTo(completeBusinessCaseAfterUpdate.getCustomerOpexSavings()));
        assertThat("getImpactOnSystemLevelPerformance are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getCycleTimeRecurringCosts are same", completeBusinessCaseBeforeUpdate.getCycleTimeRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getCycleTimeRecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getFarmOutDevelopmentNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFarmOutDevelopmentNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFarmOutDevelopmentNonrecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getCustomerUptimeImprovementBenefits are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getFieldChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFieldChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFieldChangeOrderNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("getMaterialRecurringCosts are same", completeBusinessCaseBeforeUpdate.getMaterialRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getMaterialRecurringCosts()));
        assertThat("getPrototypeMaterialsNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getPrototypeMaterialsNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getPrototypeMaterialsNonrecurringCosts()));
        assertThat("getUpdateUpgradeProductDocumentationNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("getHardwareCommitment are same", completeBusinessCaseBeforeUpdate.getHardwareCommitment(), equalTo(completeBusinessCaseAfterUpdate.getHardwareCommitment()));


    }
    public static void completeBusinessCaseAreSameWithoutComparingHardwareCommitment(CompleteBusinessCase completeBusinessCaseBeforeUpdate, CompleteBusinessCase completeBusinessCaseAfterUpdate) {
        assertThat("statuses are  same", completeBusinessCaseBeforeUpdate.getStatus(), equalTo(completeBusinessCaseAfterUpdate.getStatus()));
        assertThat("getexampleSavings are same", completeBusinessCaseBeforeUpdate.getexampleSavings(), equalTo(completeBusinessCaseAfterUpdate.getexampleSavings()));
        assertThat("getCustomerOpexSavings are same", completeBusinessCaseBeforeUpdate.getCustomerOpexSavings(), equalTo(completeBusinessCaseAfterUpdate.getCustomerOpexSavings()));
        assertThat("getImpactOnSystemLevelPerformance are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getCycleTimeRecurringCosts are same", completeBusinessCaseBeforeUpdate.getCycleTimeRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getCycleTimeRecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getFarmOutDevelopmentNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFarmOutDevelopmentNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFarmOutDevelopmentNonrecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getCustomerUptimeImprovementBenefits are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getFieldChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFieldChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFieldChangeOrderNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("getMaterialRecurringCosts are same", completeBusinessCaseBeforeUpdate.getMaterialRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getMaterialRecurringCosts()));
        assertThat("getPrototypeMaterialsNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getPrototypeMaterialsNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getPrototypeMaterialsNonrecurringCosts()));
        assertThat("getUpdateUpgradeProductDocumentationNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("RiskInLaborHours are same", completeBusinessCaseBeforeUpdate.getRiskInLaborHours(), equalTo(completeBusinessCaseAfterUpdate.getRiskInLaborHours()));


    }
    public static void completeBusinessCaseAreSameWithoutComparingSystemsInWipAndFieldImpacted(CompleteBusinessCase completeBusinessCaseBeforeUpdate, CompleteBusinessCase completeBusinessCaseAfterUpdate) {
        assertThat("statuses are  same", completeBusinessCaseBeforeUpdate.getStatus(), equalTo(completeBusinessCaseAfterUpdate.getStatus()));
        assertThat("getexampleSavings are same", completeBusinessCaseBeforeUpdate.getexampleSavings(), equalTo(completeBusinessCaseAfterUpdate.getexampleSavings()));
        assertThat("getCustomerOpexSavings are same", completeBusinessCaseBeforeUpdate.getCustomerOpexSavings(), equalTo(completeBusinessCaseAfterUpdate.getCustomerOpexSavings()));
        assertThat("getImpactOnSystemLevelPerformance are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getCycleTimeRecurringCosts are same", completeBusinessCaseBeforeUpdate.getCycleTimeRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getCycleTimeRecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getFarmOutDevelopmentNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFarmOutDevelopmentNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFarmOutDevelopmentNonrecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getCustomerUptimeImprovementBenefits are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getFieldChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFieldChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFieldChangeOrderNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("getMaterialRecurringCosts are same", completeBusinessCaseBeforeUpdate.getMaterialRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getMaterialRecurringCosts()));
        assertThat("getPrototypeMaterialsNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getPrototypeMaterialsNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getPrototypeMaterialsNonrecurringCosts()));
        assertThat("getUpdateUpgradeProductDocumentationNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("RiskInLaborHours are same", completeBusinessCaseBeforeUpdate.getRiskInLaborHours(), equalTo(completeBusinessCaseAfterUpdate.getRiskInLaborHours()));


    }

    public static void completeBusinessCaseAreSameWithoutComparingFactoryInvestments(CompleteBusinessCase completeBusinessCaseBeforeUpdate, CompleteBusinessCase completeBusinessCaseAfterUpdate) {
        assertThat("statuses are  same", completeBusinessCaseBeforeUpdate.getStatus(), equalTo(completeBusinessCaseAfterUpdate.getStatus()));
        assertThat("getexampleSavings are same", completeBusinessCaseBeforeUpdate.getexampleSavings(), equalTo(completeBusinessCaseAfterUpdate.getexampleSavings()));
        assertThat("getCustomerOpexSavings are same", completeBusinessCaseBeforeUpdate.getCustomerOpexSavings(), equalTo(completeBusinessCaseAfterUpdate.getCustomerOpexSavings()));
        assertThat("getImpactOnSystemLevelPerformance are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getCycleTimeRecurringCosts are same", completeBusinessCaseBeforeUpdate.getCycleTimeRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getCycleTimeRecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getFarmOutDevelopmentNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFarmOutDevelopmentNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFarmOutDevelopmentNonrecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getCustomerUptimeImprovementBenefits are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getFieldChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFieldChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFieldChangeOrderNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("getMaterialRecurringCosts are same", completeBusinessCaseBeforeUpdate.getMaterialRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getMaterialRecurringCosts()));
        assertThat("getPrototypeMaterialsNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getPrototypeMaterialsNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getPrototypeMaterialsNonrecurringCosts()));
        assertThat("getUpdateUpgradeProductDocumentationNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("RiskInLaborHours are same", completeBusinessCaseBeforeUpdate.getRiskInLaborHours(), equalTo(completeBusinessCaseAfterUpdate.getRiskInLaborHours()));


    }
    public static void completeBusinessCaseAreSameWithoutComparingFsToolingInvestments(CompleteBusinessCase completeBusinessCaseBeforeUpdate, CompleteBusinessCase completeBusinessCaseAfterUpdate) {
        assertThat("statuses are  same", completeBusinessCaseBeforeUpdate.getStatus(), equalTo(completeBusinessCaseAfterUpdate.getStatus()));
        assertThat("getexampleSavings are same", completeBusinessCaseBeforeUpdate.getexampleSavings(), equalTo(completeBusinessCaseAfterUpdate.getexampleSavings()));
        assertThat("getCustomerOpexSavings are same", completeBusinessCaseBeforeUpdate.getCustomerOpexSavings(), equalTo(completeBusinessCaseAfterUpdate.getCustomerOpexSavings()));
        assertThat("getImpactOnSystemLevelPerformance are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getCycleTimeRecurringCosts are same", completeBusinessCaseBeforeUpdate.getCycleTimeRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getCycleTimeRecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getFarmOutDevelopmentNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFarmOutDevelopmentNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFarmOutDevelopmentNonrecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getCustomerUptimeImprovementBenefits are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getFieldChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFieldChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFieldChangeOrderNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("getMaterialRecurringCosts are same", completeBusinessCaseBeforeUpdate.getMaterialRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getMaterialRecurringCosts()));
        assertThat("getPrototypeMaterialsNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getPrototypeMaterialsNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getPrototypeMaterialsNonrecurringCosts()));
        assertThat("getUpdateUpgradeProductDocumentationNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("RiskInLaborHours are same", completeBusinessCaseBeforeUpdate.getRiskInLaborHours(), equalTo(completeBusinessCaseAfterUpdate.getRiskInLaborHours()));


    }
    public static void completeBusinessCaseAreSameWithoutComparingSupplyChainManagementInvestments(CompleteBusinessCase completeBusinessCaseBeforeUpdate, CompleteBusinessCase completeBusinessCaseAfterUpdate) {
        assertThat("statuses are  same", completeBusinessCaseBeforeUpdate.getStatus(), equalTo(completeBusinessCaseAfterUpdate.getStatus()));
        assertThat("getexampleSavings are same", completeBusinessCaseBeforeUpdate.getexampleSavings(), equalTo(completeBusinessCaseAfterUpdate.getexampleSavings()));
        assertThat("getCustomerOpexSavings are same", completeBusinessCaseBeforeUpdate.getCustomerOpexSavings(), equalTo(completeBusinessCaseAfterUpdate.getCustomerOpexSavings()));
        assertThat("getImpactOnSystemLevelPerformance are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getCycleTimeRecurringCosts are same", completeBusinessCaseBeforeUpdate.getCycleTimeRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getCycleTimeRecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getFarmOutDevelopmentNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFarmOutDevelopmentNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFarmOutDevelopmentNonrecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getCustomerUptimeImprovementBenefits are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getFieldChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFieldChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFieldChangeOrderNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("getMaterialRecurringCosts are same", completeBusinessCaseBeforeUpdate.getMaterialRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getMaterialRecurringCosts()));
        assertThat("getPrototypeMaterialsNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getPrototypeMaterialsNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getPrototypeMaterialsNonrecurringCosts()));
        assertThat("getUpdateUpgradeProductDocumentationNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("RiskInLaborHours are same", completeBusinessCaseBeforeUpdate.getRiskInLaborHours(), equalTo(completeBusinessCaseAfterUpdate.getRiskInLaborHours()));


    }
    public static void completeBusinessCaseAreSameWithoutComparingSupplierInvestments(CompleteBusinessCase completeBusinessCaseBeforeUpdate, CompleteBusinessCase completeBusinessCaseAfterUpdate) {
        assertThat("statuses are  same", completeBusinessCaseBeforeUpdate.getStatus(), equalTo(completeBusinessCaseAfterUpdate.getStatus()));
        assertThat("getexampleSavings are same", completeBusinessCaseBeforeUpdate.getexampleSavings(), equalTo(completeBusinessCaseAfterUpdate.getexampleSavings()));
        assertThat("getCustomerOpexSavings are same", completeBusinessCaseBeforeUpdate.getCustomerOpexSavings(), equalTo(completeBusinessCaseAfterUpdate.getCustomerOpexSavings()));
        assertThat("getImpactOnSystemLevelPerformance are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getCycleTimeRecurringCosts are same", completeBusinessCaseBeforeUpdate.getCycleTimeRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getCycleTimeRecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getFarmOutDevelopmentNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFarmOutDevelopmentNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFarmOutDevelopmentNonrecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getCustomerUptimeImprovementBenefits are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getFieldChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFieldChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFieldChangeOrderNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("getMaterialRecurringCosts are same", completeBusinessCaseBeforeUpdate.getMaterialRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getMaterialRecurringCosts()));
        assertThat("getPrototypeMaterialsNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getPrototypeMaterialsNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getPrototypeMaterialsNonrecurringCosts()));
        assertThat("getUpdateUpgradeProductDocumentationNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("RiskInLaborHours are same", completeBusinessCaseBeforeUpdate.getRiskInLaborHours(), equalTo(completeBusinessCaseAfterUpdate.getRiskInLaborHours()));


    }
    public static void completeBusinessCaseAreSameWithoutComparingDeInvestments(CompleteBusinessCase completeBusinessCaseBeforeUpdate, CompleteBusinessCase completeBusinessCaseAfterUpdate) {
        assertThat("statuses are  same", completeBusinessCaseBeforeUpdate.getStatus(), equalTo(completeBusinessCaseAfterUpdate.getStatus()));
        assertThat("getexampleSavings are same", completeBusinessCaseBeforeUpdate.getexampleSavings(), equalTo(completeBusinessCaseAfterUpdate.getexampleSavings()));
        assertThat("getCustomerOpexSavings are same", completeBusinessCaseBeforeUpdate.getCustomerOpexSavings(), equalTo(completeBusinessCaseAfterUpdate.getCustomerOpexSavings()));
        assertThat("getImpactOnSystemLevelPerformance are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getCycleTimeRecurringCosts are same", completeBusinessCaseBeforeUpdate.getCycleTimeRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getCycleTimeRecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getFarmOutDevelopmentNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFarmOutDevelopmentNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFarmOutDevelopmentNonrecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getCustomerUptimeImprovementBenefits are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getFieldChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFieldChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFieldChangeOrderNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("getMaterialRecurringCosts are same", completeBusinessCaseBeforeUpdate.getMaterialRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getMaterialRecurringCosts()));
        assertThat("getPrototypeMaterialsNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getPrototypeMaterialsNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getPrototypeMaterialsNonrecurringCosts()));
        assertThat("getUpdateUpgradeProductDocumentationNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("RiskInLaborHours are same", completeBusinessCaseBeforeUpdate.getRiskInLaborHours(), equalTo(completeBusinessCaseAfterUpdate.getRiskInLaborHours()));


    }

    public static void completeBusinessCaseAreSameWithoutComparingMaterialRecurringCosts(CompleteBusinessCase completeBusinessCaseBeforeUpdate, CompleteBusinessCase completeBusinessCaseAfterUpdate) {
        assertThat("statuses are  same", completeBusinessCaseBeforeUpdate.getStatus(), equalTo(completeBusinessCaseAfterUpdate.getStatus()));
        assertThat("getexampleSavings are same", completeBusinessCaseBeforeUpdate.getexampleSavings(), equalTo(completeBusinessCaseAfterUpdate.getexampleSavings()));
        assertThat("getCustomerOpexSavings are same", completeBusinessCaseBeforeUpdate.getCustomerOpexSavings(), equalTo(completeBusinessCaseAfterUpdate.getCustomerOpexSavings()));
        assertThat("getImpactOnSystemLevelPerformance are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getCycleTimeRecurringCosts are same", completeBusinessCaseBeforeUpdate.getCycleTimeRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getCycleTimeRecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getFarmOutDevelopmentNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFarmOutDevelopmentNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFarmOutDevelopmentNonrecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getCustomerUptimeImprovementBenefits are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getFieldChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFieldChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFieldChangeOrderNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("getPrototypeMaterialsNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getPrototypeMaterialsNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getPrototypeMaterialsNonrecurringCosts()));
        assertThat("getUpdateUpgradeProductDocumentationNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("RiskInLaborHours are same", completeBusinessCaseBeforeUpdate.getRiskInLaborHours(), equalTo(completeBusinessCaseAfterUpdate.getRiskInLaborHours()));


    }
    public static void completeBusinessCaseAreSameWithoutComparingCycleTimeRecurringCosts(CompleteBusinessCase completeBusinessCaseBeforeUpdate, CompleteBusinessCase completeBusinessCaseAfterUpdate) {
        assertThat("statuses are  same", completeBusinessCaseBeforeUpdate.getStatus(), equalTo(completeBusinessCaseAfterUpdate.getStatus()));
        assertThat("getexampleSavings are same", completeBusinessCaseBeforeUpdate.getexampleSavings(), equalTo(completeBusinessCaseAfterUpdate.getexampleSavings()));
        assertThat("getCustomerOpexSavings are same", completeBusinessCaseBeforeUpdate.getCustomerOpexSavings(), equalTo(completeBusinessCaseAfterUpdate.getCustomerOpexSavings()));
        assertThat("getImpactOnSystemLevelPerformance are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getFarmOutDevelopmentNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFarmOutDevelopmentNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFarmOutDevelopmentNonrecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getCustomerUptimeImprovementBenefits are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getFieldChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFieldChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFieldChangeOrderNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("getPrototypeMaterialsNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getPrototypeMaterialsNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getPrototypeMaterialsNonrecurringCosts()));
        assertThat("getUpdateUpgradeProductDocumentationNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("RiskInLaborHours are same", completeBusinessCaseBeforeUpdate.getRiskInLaborHours(), equalTo(completeBusinessCaseAfterUpdate.getRiskInLaborHours()));


    }
    public static void completeBusinessCaseAreSameWithoutComparingLaborRecurringCosts(CompleteBusinessCase completeBusinessCaseBeforeUpdate, CompleteBusinessCase completeBusinessCaseAfterUpdate) {
        assertThat("statuses are  same", completeBusinessCaseBeforeUpdate.getStatus(), equalTo(completeBusinessCaseAfterUpdate.getStatus()));
        assertThat("getexampleSavings are same", completeBusinessCaseBeforeUpdate.getexampleSavings(), equalTo(completeBusinessCaseAfterUpdate.getexampleSavings()));
        assertThat("getCustomerOpexSavings are same", completeBusinessCaseBeforeUpdate.getCustomerOpexSavings(), equalTo(completeBusinessCaseAfterUpdate.getCustomerOpexSavings()));
        assertThat("getImpactOnSystemLevelPerformance are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getCycleTimeRecurringCosts are same", completeBusinessCaseBeforeUpdate.getCycleTimeRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getCycleTimeRecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getFarmOutDevelopmentNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFarmOutDevelopmentNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFarmOutDevelopmentNonrecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getCustomerUptimeImprovementBenefits are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getFieldChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFieldChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFieldChangeOrderNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("getPrototypeMaterialsNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getPrototypeMaterialsNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getPrototypeMaterialsNonrecurringCosts()));
        assertThat("getUpdateUpgradeProductDocumentationNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("RiskInLaborHours are same", completeBusinessCaseBeforeUpdate.getRiskInLaborHours(), equalTo(completeBusinessCaseAfterUpdate.getRiskInLaborHours()));


    }

    public static void completeBusinessCaseAreSameWithoutComparingInventoryReplaceNonrecurringCosts(CompleteBusinessCase completeBusinessCaseBeforeUpdate, CompleteBusinessCase completeBusinessCaseAfterUpdate) {
        assertThat("statuses are  same", completeBusinessCaseBeforeUpdate.getStatus(), equalTo(completeBusinessCaseAfterUpdate.getStatus()));
        assertThat("getexampleSavings are same", completeBusinessCaseBeforeUpdate.getexampleSavings(), equalTo(completeBusinessCaseAfterUpdate.getexampleSavings()));
        assertThat("getCustomerOpexSavings are same", completeBusinessCaseBeforeUpdate.getCustomerOpexSavings(), equalTo(completeBusinessCaseAfterUpdate.getCustomerOpexSavings()));
        assertThat("getImpactOnSystemLevelPerformance are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getCycleTimeRecurringCosts are same", completeBusinessCaseBeforeUpdate.getCycleTimeRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getCycleTimeRecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getFarmOutDevelopmentNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFarmOutDevelopmentNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFarmOutDevelopmentNonrecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getCustomerUptimeImprovementBenefits are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getFieldChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFieldChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFieldChangeOrderNonrecurringCosts()));
        assertThat("getPrototypeMaterialsNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getPrototypeMaterialsNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getPrototypeMaterialsNonrecurringCosts()));
        assertThat("getUpdateUpgradeProductDocumentationNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts()));
        assertThat("RiskInLaborHours are same", completeBusinessCaseBeforeUpdate.getRiskInLaborHours(), equalTo(completeBusinessCaseAfterUpdate.getRiskInLaborHours()));
    }
    public static void completeBusinessCaseAreSameWithoutComparingInventoryScrapNonrecurringCosts(CompleteBusinessCase completeBusinessCaseBeforeUpdate, CompleteBusinessCase completeBusinessCaseAfterUpdate) {
        assertThat("statuses are  same", completeBusinessCaseBeforeUpdate.getStatus(), equalTo(completeBusinessCaseAfterUpdate.getStatus()));
        assertThat("getexampleSavings are same", completeBusinessCaseBeforeUpdate.getexampleSavings(), equalTo(completeBusinessCaseAfterUpdate.getexampleSavings()));
        assertThat("getCustomerOpexSavings are same", completeBusinessCaseBeforeUpdate.getCustomerOpexSavings(), equalTo(completeBusinessCaseAfterUpdate.getCustomerOpexSavings()));
        assertThat("getImpactOnSystemLevelPerformance are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getCycleTimeRecurringCosts are same", completeBusinessCaseBeforeUpdate.getCycleTimeRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getCycleTimeRecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getFarmOutDevelopmentNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFarmOutDevelopmentNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFarmOutDevelopmentNonrecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getCustomerUptimeImprovementBenefits are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getFieldChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFieldChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFieldChangeOrderNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("getPrototypeMaterialsNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getPrototypeMaterialsNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getPrototypeMaterialsNonrecurringCosts()));
        assertThat("getUpdateUpgradeProductDocumentationNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts()));
        assertThat("RiskInLaborHours are same", completeBusinessCaseBeforeUpdate.getRiskInLaborHours(), equalTo(completeBusinessCaseAfterUpdate.getRiskInLaborHours()));
    }
    public static void completeBusinessCaseAreSameWithoutComparingSupplyChainAdjustmentsNonrecurringCosts(CompleteBusinessCase completeBusinessCaseBeforeUpdate, CompleteBusinessCase completeBusinessCaseAfterUpdate) {
        assertThat("statuses are  same", completeBusinessCaseBeforeUpdate.getStatus(), equalTo(completeBusinessCaseAfterUpdate.getStatus()));
        assertThat("getexampleSavings are same", completeBusinessCaseBeforeUpdate.getexampleSavings(), equalTo(completeBusinessCaseAfterUpdate.getexampleSavings()));
        assertThat("getCustomerOpexSavings are same", completeBusinessCaseBeforeUpdate.getCustomerOpexSavings(), equalTo(completeBusinessCaseAfterUpdate.getCustomerOpexSavings()));
        assertThat("getImpactOnSystemLevelPerformance are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getCycleTimeRecurringCosts are same", completeBusinessCaseBeforeUpdate.getCycleTimeRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getCycleTimeRecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getFarmOutDevelopmentNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFarmOutDevelopmentNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFarmOutDevelopmentNonrecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getCustomerUptimeImprovementBenefits are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getFieldChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFieldChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFieldChangeOrderNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("getPrototypeMaterialsNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getPrototypeMaterialsNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getPrototypeMaterialsNonrecurringCosts()));
        assertThat("getUpdateUpgradeProductDocumentationNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts()));
        assertThat("RiskInLaborHours are same", completeBusinessCaseBeforeUpdate.getRiskInLaborHours(), equalTo(completeBusinessCaseAfterUpdate.getRiskInLaborHours()));
    }
    public static void completeBusinessCaseAreSameWithoutComparingFactoryChangeOrderNonrecurringCosts(CompleteBusinessCase completeBusinessCaseBeforeUpdate, CompleteBusinessCase completeBusinessCaseAfterUpdate) {
        assertThat("statuses are  same", completeBusinessCaseBeforeUpdate.getStatus(), equalTo(completeBusinessCaseAfterUpdate.getStatus()));
        assertThat("getexampleSavings are same", completeBusinessCaseBeforeUpdate.getexampleSavings(), equalTo(completeBusinessCaseAfterUpdate.getexampleSavings()));
        assertThat("getCustomerOpexSavings are same", completeBusinessCaseBeforeUpdate.getCustomerOpexSavings(), equalTo(completeBusinessCaseAfterUpdate.getCustomerOpexSavings()));
        assertThat("getImpactOnSystemLevelPerformance are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getCycleTimeRecurringCosts are same", completeBusinessCaseBeforeUpdate.getCycleTimeRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getCycleTimeRecurringCosts()));
        assertThat("getFarmOutDevelopmentNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFarmOutDevelopmentNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFarmOutDevelopmentNonrecurringCosts()));
        assertThat("getCustomerUptimeImprovementBenefits are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getFieldChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFieldChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFieldChangeOrderNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("getPrototypeMaterialsNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getPrototypeMaterialsNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getPrototypeMaterialsNonrecurringCosts()));
        assertThat("getUpdateUpgradeProductDocumentationNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getUpdateUpgradeProductDocumentationNonrecurringCosts()));
        assertThat("RiskInLaborHours are same", completeBusinessCaseBeforeUpdate.getRiskInLaborHours(), equalTo(completeBusinessCaseAfterUpdate.getRiskInLaborHours()));
    }
    public static void completeBusinessCaseAreSameWithoutComparingUpdateUpgradeProductDocumentationNonrecurringCosts(CompleteBusinessCase completeBusinessCaseBeforeUpdate, CompleteBusinessCase completeBusinessCaseAfterUpdate) {
        assertThat("statuses are  same", completeBusinessCaseBeforeUpdate.getStatus(), equalTo(completeBusinessCaseAfterUpdate.getStatus()));
        assertThat("getexampleSavings are same", completeBusinessCaseBeforeUpdate.getexampleSavings(), equalTo(completeBusinessCaseAfterUpdate.getexampleSavings()));
        assertThat("getCustomerOpexSavings are same", completeBusinessCaseBeforeUpdate.getCustomerOpexSavings(), equalTo(completeBusinessCaseAfterUpdate.getCustomerOpexSavings()));
        assertThat("getImpactOnSystemLevelPerformance are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getCycleTimeRecurringCosts are same", completeBusinessCaseBeforeUpdate.getCycleTimeRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getCycleTimeRecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getFarmOutDevelopmentNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFarmOutDevelopmentNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFarmOutDevelopmentNonrecurringCosts()));
        assertThat("getCustomerUptimeImprovementBenefits are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getFieldChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFieldChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFieldChangeOrderNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("getPrototypeMaterialsNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getPrototypeMaterialsNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getPrototypeMaterialsNonrecurringCosts()));
        assertThat("RiskInLaborHours are same", completeBusinessCaseBeforeUpdate.getRiskInLaborHours(), equalTo(completeBusinessCaseAfterUpdate.getRiskInLaborHours()));
    }
    public static void completeBusinessCaseAreSameWithoutComparingFarmOutDevelopmentNonrecurringCosts(CompleteBusinessCase completeBusinessCaseBeforeUpdate, CompleteBusinessCase completeBusinessCaseAfterUpdate) {
        assertThat("statuses are  same", completeBusinessCaseBeforeUpdate.getStatus(), equalTo(completeBusinessCaseAfterUpdate.getStatus()));
        assertThat("getexampleSavings are same", completeBusinessCaseBeforeUpdate.getexampleSavings(), equalTo(completeBusinessCaseAfterUpdate.getexampleSavings()));
        assertThat("getCustomerOpexSavings are same", completeBusinessCaseBeforeUpdate.getCustomerOpexSavings(), equalTo(completeBusinessCaseAfterUpdate.getCustomerOpexSavings()));
        assertThat("getImpactOnSystemLevelPerformance are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getCycleTimeRecurringCosts are same", completeBusinessCaseBeforeUpdate.getCycleTimeRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getCycleTimeRecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getCustomerUptimeImprovementBenefits are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getFieldChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFieldChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFieldChangeOrderNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("getPrototypeMaterialsNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getPrototypeMaterialsNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getPrototypeMaterialsNonrecurringCosts()));
        assertThat("RiskInLaborHours are same", completeBusinessCaseBeforeUpdate.getRiskInLaborHours(), equalTo(completeBusinessCaseAfterUpdate.getRiskInLaborHours()));
    }

    public static void completeBusinessCaseAreSameWithoutComparingPrototypeMaterialsNonrecurringCosts(CompleteBusinessCase completeBusinessCaseBeforeUpdate, CompleteBusinessCase completeBusinessCaseAfterUpdate) {
        assertThat("statuses are  same", completeBusinessCaseBeforeUpdate.getStatus(), equalTo(completeBusinessCaseAfterUpdate.getStatus()));
        assertThat("getexampleSavings are same", completeBusinessCaseBeforeUpdate.getexampleSavings(), equalTo(completeBusinessCaseAfterUpdate.getexampleSavings()));
        assertThat("getCustomerOpexSavings are same", completeBusinessCaseBeforeUpdate.getCustomerOpexSavings(), equalTo(completeBusinessCaseAfterUpdate.getCustomerOpexSavings()));
        assertThat("getImpactOnSystemLevelPerformance are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getCycleTimeRecurringCosts are same", completeBusinessCaseBeforeUpdate.getCycleTimeRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getCycleTimeRecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getFarmOutDevelopmentNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFarmOutDevelopmentNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFarmOutDevelopmentNonrecurringCosts()));
        assertThat("getCustomerUptimeImprovementBenefits are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getFieldChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFieldChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFieldChangeOrderNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("RiskInLaborHours are same", completeBusinessCaseBeforeUpdate.getRiskInLaborHours(), equalTo(completeBusinessCaseAfterUpdate.getRiskInLaborHours()));
    }
    public static void completeBusinessCaseAreSameWithoutComparingRevenuesBenefits(CompleteBusinessCase completeBusinessCaseBeforeUpdate, CompleteBusinessCase completeBusinessCaseAfterUpdate) {
        assertThat("statuses are  same", completeBusinessCaseBeforeUpdate.getStatus(), equalTo(completeBusinessCaseAfterUpdate.getStatus()));
        assertThat("getexampleSavings are same", completeBusinessCaseBeforeUpdate.getexampleSavings(), equalTo(completeBusinessCaseAfterUpdate.getexampleSavings()));
        assertThat("getCustomerOpexSavings are same", completeBusinessCaseBeforeUpdate.getCustomerOpexSavings(), equalTo(completeBusinessCaseAfterUpdate.getCustomerOpexSavings()));
        assertThat("getImpactOnSystemLevelPerformance are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getCycleTimeRecurringCosts are same", completeBusinessCaseBeforeUpdate.getCycleTimeRecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getCycleTimeRecurringCosts()));
        assertThat("getFactoryChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFactoryChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFactoryChangeOrderNonrecurringCosts()));
        assertThat("getFarmOutDevelopmentNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFarmOutDevelopmentNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFarmOutDevelopmentNonrecurringCosts()));
        assertThat("getCustomerUptimeImprovementBenefits are same", completeBusinessCaseBeforeUpdate.getCustomerUptimeImprovementBenefits(), equalTo(completeBusinessCaseAfterUpdate.getCustomerUptimeImprovementBenefits()));
        assertThat("getFieldChangeOrderNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getFieldChangeOrderNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getFieldChangeOrderNonrecurringCosts()));
        assertThat("getInventoryReplaceNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getInventoryReplaceNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getInventoryReplaceNonrecurringCosts()));
        assertThat("getPrototypeMaterialsNonrecurringCosts are same", completeBusinessCaseBeforeUpdate.getPrototypeMaterialsNonrecurringCosts(), equalTo(completeBusinessCaseAfterUpdate.getPrototypeMaterialsNonrecurringCosts()));
        assertThat("RiskInLaborHours are same", completeBusinessCaseBeforeUpdate.getRiskInLaborHours(), equalTo(completeBusinessCaseAfterUpdate.getRiskInLaborHours()));
    }
    public static void scopeAreSameWithoutComparingParts(Scope scopeBeforeUpdate, Scope scopeAfterUpdate) {
        assertThat("statuses are  same", scopeBeforeUpdate.getStatus(), equalTo(scopeAfterUpdate.getStatus()));
        assertThat("Packaging are same", scopeBeforeUpdate.getPackaging(), equalTo(scopeAfterUpdate.getPackaging()));
        assertThat("Tooling are same", scopeBeforeUpdate.getTooling(), equalTo(scopeAfterUpdate.getTooling()));
        assertThat("Bop are same", scopeBeforeUpdate.getBop(), equalTo(scopeAfterUpdate.getBop()));
        assertThat("ScopeDetails are same", scopeBeforeUpdate.getScopeDetails(), equalTo(scopeAfterUpdate.getScopeDetails()));
        assertThat("PartDetail are same", scopeBeforeUpdate.getPartDetail().getDevBagPart(), equalTo(scopeAfterUpdate.getPartDetail().getDevBagPart()));
        assertThat("PackagingDetail are same", scopeBeforeUpdate.getPackagingDetail().getShippingPackaging(), equalTo(scopeAfterUpdate.getPackagingDetail().getShippingPackaging()));
        assertThat("ToolingDetail are same", scopeBeforeUpdate.getToolingDetail().getSupplierTooling(), equalTo(scopeAfterUpdate.getToolingDetail().getSupplierTooling()));
    }
    public static void scopeAreSameWithoutComparingTooling(Scope scopeBeforeUpdate, Scope scopeAfterUpdate) {
        assertThat("statuses are  same", scopeBeforeUpdate.getStatus(), equalTo(scopeAfterUpdate.getStatus()));
        assertThat("Packaging are same", scopeBeforeUpdate.getPackaging(), equalTo(scopeAfterUpdate.getPackaging()));
        assertThat("Bop are same", scopeBeforeUpdate.getBop(), equalTo(scopeAfterUpdate.getBop()));
        assertThat("ScopeDetails are same", scopeBeforeUpdate.getScopeDetails(), equalTo(scopeAfterUpdate.getScopeDetails()));
        assertThat("PartDetail are same", scopeBeforeUpdate.getPartDetail().getServicePart(), equalTo(scopeAfterUpdate.getPartDetail().getServicePart()));
        assertThat("PackagingDetail are same", scopeBeforeUpdate.getPackagingDetail().getReusablePackaging(), equalTo(scopeAfterUpdate.getPackagingDetail().getReusablePackaging()));
    }
    public static void scopeAreSameWithoutComparingPackaging(Scope scopeBeforeUpdate, Scope scopeAfterUpdate) {
        assertThat("statuses are  same", scopeBeforeUpdate.getStatus(), equalTo(scopeAfterUpdate.getStatus()));
        assertThat("Tooling are same", scopeBeforeUpdate.getTooling(), equalTo(scopeAfterUpdate.getTooling()));
        assertThat("Bop are same", scopeBeforeUpdate.getBop(), equalTo(scopeAfterUpdate.getBop()));
        assertThat("ScopeDetails are same", scopeBeforeUpdate.getScopeDetails(), equalTo(scopeAfterUpdate.getScopeDetails()));
        assertThat("PartDetail are same", scopeBeforeUpdate.getPartDetail().getPreinstallPart(), equalTo(scopeAfterUpdate.getPartDetail().getPreinstallPart()));
        assertThat("ToolingDetail are same", scopeBeforeUpdate.getToolingDetail().getManufacturingDeTooling(), equalTo(scopeAfterUpdate.getToolingDetail().getManufacturingDeTooling()));
    }
    public static void scopeAreSameWithoutComparingBop(Scope scopeBeforeUpdate, Scope scopeAfterUpdate) {
        assertThat("statuses are  same", scopeBeforeUpdate.getStatus(), equalTo(scopeAfterUpdate.getStatus()));
        assertThat("Packaging are same", scopeBeforeUpdate.getPackaging(), equalTo(scopeAfterUpdate.getPackaging()));
        assertThat("Tooling are same", scopeBeforeUpdate.getTooling(), equalTo(scopeAfterUpdate.getTooling()));
        assertThat("ScopeDetails are same", scopeBeforeUpdate.getScopeDetails(), equalTo(scopeAfterUpdate.getScopeDetails()));
        assertThat("PartDetail are same", scopeBeforeUpdate.getPartDetail().getMachineBomPart(), equalTo(scopeAfterUpdate.getPartDetail().getMachineBomPart()));
        assertThat("PackagingDetail are same", scopeBeforeUpdate.getPackagingDetail().getStoragePackaging(), equalTo(scopeAfterUpdate.getPackagingDetail().getStoragePackaging()));
        assertThat("ToolingDetail are same", scopeBeforeUpdate.getToolingDetail().getServiceTooling(), equalTo(scopeAfterUpdate.getToolingDetail().getServiceTooling()));
    }
    public static void scopeAreSameWithoutComparingScopeDetails(Scope scopeBeforeUpdate, Scope scopeAfterUpdate) {
        assertThat("statuses are  same", scopeBeforeUpdate.getStatus(), equalTo(scopeAfterUpdate.getStatus()));
        assertThat("Packaging are same", scopeBeforeUpdate.getPackaging(), equalTo(scopeAfterUpdate.getPackaging()));
        assertThat("Tooling are same", scopeBeforeUpdate.getTooling(), equalTo(scopeAfterUpdate.getTooling()));
        assertThat("Bop are same", scopeBeforeUpdate.getBop(), equalTo(scopeAfterUpdate.getBop()));
        assertThat("PackagingDetail are same", scopeBeforeUpdate.getPackagingDetail().getSupplierPackaging(), equalTo(scopeAfterUpdate.getPackagingDetail().getSupplierPackaging()));
        assertThat("ToolingDetail are same", scopeBeforeUpdate.getToolingDetail().getSupplierTooling(), equalTo(scopeAfterUpdate.getToolingDetail().getSupplierTooling()));
    }
    public static void scopeAreSameWithoutComparingDevBagPart(Scope scopeBeforeUpdate, Scope scopeAfterUpdate) {
        assertThat("statuses are  same", scopeBeforeUpdate.getStatus(), equalTo(scopeAfterUpdate.getStatus()));
        assertThat("Packaging are same", scopeBeforeUpdate.getPackaging(), equalTo(scopeAfterUpdate.getPackaging()));
        assertThat("Tooling are same", scopeBeforeUpdate.getTooling(), equalTo(scopeAfterUpdate.getTooling()));
        assertThat("Bop are same", scopeBeforeUpdate.getBop(), equalTo(scopeAfterUpdate.getBop()));
        assertThat("ScopeDetails are same", scopeBeforeUpdate.getScopeDetails(), equalTo(scopeAfterUpdate.getScopeDetails()));
        assertThat("ToolingDetail are same", scopeBeforeUpdate.getToolingDetail().getServiceTooling(), equalTo(scopeAfterUpdate.getToolingDetail().getServiceTooling()));
    }
    public static void scopeAreSameWithoutComparinggetMachineBomPart(Scope scopeBeforeUpdate, Scope scopeAfterUpdate) {
        assertThat("statuses are  same", scopeBeforeUpdate.getStatus(), equalTo(scopeAfterUpdate.getStatus()));
        assertThat("Packaging are same", scopeBeforeUpdate.getPackaging(), equalTo(scopeAfterUpdate.getPackaging()));
        assertThat("Tooling are same", scopeBeforeUpdate.getTooling(), equalTo(scopeAfterUpdate.getTooling()));
        assertThat("Bop are same", scopeBeforeUpdate.getBop(), equalTo(scopeAfterUpdate.getBop()));
        assertThat("ScopeDetails are same", scopeBeforeUpdate.getScopeDetails(), equalTo(scopeAfterUpdate.getScopeDetails()));
        assertThat("ToolingDetail are same", scopeBeforeUpdate.getToolingDetail().getSupplierTooling(), equalTo(scopeAfterUpdate.getToolingDetail().getSupplierTooling()));
    }

    public static void changeRequestCommentsAreSameWithoutComparingAuditAndStatus(Comment comment1, Comment comment2) {
        assertThat("commentText are not same", comment1.getCommentText(), equalTo(comment2.getCommentText()));

    }

    public static void changeRequestCommentsAreSameWithoutComparingCommentText(Comment comment1, Comment comment2) {
        assertThat("status are not same", comment1.getStatus(), equalTo(comment2.getStatus()));
    }

    public static void createCommentIsSuccessful(Comment comment, ChangeRequestComment changeRequestComment, ChangeRequestCommentJson changeRequestCommentJson) {

        assertThat("created_on is null in response", changeRequestComment.getCreatedOn(), is(notNullValue()));
        assertThat("status is not draft for a reply", changeRequestComment.getStatus(), equalTo(1));
    }

    public static void createCommentDocumentIsSuccessful(Document document, ChangeRequestCommentDocument changeRequestCommentDocument, ChangeRequestCommentDocumentJson changeRequestCommentJson) {

        assertThat("document tags are null for a comment document", changeRequestCommentDocument.getTags(), is(notNullValue()));
        assertThat("document name is null for a comment document", changeRequestCommentDocument.getName(), is(notNullValue()));
        assertThat("status is not draft for a comment document", changeRequestCommentDocument.getStatus(), equalTo(1));
    }

    public static void createMyTeamMemberIsSuccessful(MyTeamMember myTeamMember1, MyTeamMember myTeamMember2, ChangeRequestMyTeamJson changeRequestMyTeamJson) {
        assertThat("change request are not same", ((ChangeRequestMyTeam) myTeamMember1.getMyteam()).getChangeRequest().getId(), samePropertyValuesAs(((ChangeRequestMyTeam) myTeamMember2.getMyteam()).getChangeRequest().getId()));
        assertThat("my team id are not same", myTeamMember1.getMyteam().getId(), equalTo(myTeamMember2.getMyteam().getId()));
        assertThat("my team are not same", myTeamMember1.getStatus(), equalTo(myTeamMember2.getStatus()));
        assertThat("roles are not same", myTeamMember1.getRoles(), equalTo(myTeamMember2.getRoles()));
        assertThat("my team member id is not null", myTeamMember2.getId(), notNullValue());
    }

    public static void unauthorizedExceptionAndMyTeamMemberDidNotChange(MyTeamMember myTeamMemberBeforeDelete, MyTeamMember myTeamMemberAfterDelete, ExceptionResponse exceptionResponse,
                                                                        String path) {
        ExceptionValidator.exceptionResponseIsUnauthorizedException(exceptionResponse, path);
        myTeamMemberIsEqualWithoutRoles(myTeamMemberBeforeDelete, myTeamMemberAfterDelete);
        assertThat("roles are not same", myTeamMemberBeforeDelete.getRoles(), equalTo(myTeamMemberAfterDelete.getRoles()));
    }

    public static void myTeamMemberIsEqualWithoutRoles(MyTeamMember myTeamMember1, MyTeamMember myTeamMember2) {
        assertThat("change request are  same", ((ChangeRequestMyTeam) myTeamMember1.getMyteam()).getChangeRequest().getId(), samePropertyValuesAs(((ChangeRequestMyTeam) myTeamMember2.getMyteam()).getChangeRequest().getId()));
        assertThat("my team member id are  same", myTeamMember1.getId(), equalTo(myTeamMember2.getId()));
        assertThat("my team id are  same", myTeamMember1.getMyteam().getId(), equalTo(myTeamMember2.getMyteam().getId()));
        assertThat("my team status are  same", myTeamMember1.getMyteam().getStatus(), equalTo(myTeamMember2.getMyteam().getStatus()));
        assertThat("my team are  same", myTeamMember1.getStatus(), equalTo(myTeamMember2.getStatus()));
    }

    public static void updateTeamMemberRoleIsUnSuccessful(MyTeamMember myTeamMember1, MyTeamMember myTeamMember2) {
        myTeamMemberIsEqualWithoutRoles(myTeamMember1, myTeamMember2);
        assertThat("roles are not same", myTeamMember1.getRoles(), equalTo(myTeamMember2.getRoles()));
    }

    public static void updateTeamMemberRoleIsSuccessful(MyTeamMember myTeamMember1, MyTeamMember myTeamMember2) {
        myTeamMemberIsEqualWithoutRoles(myTeamMember1, myTeamMember2);
        assertThat("roles are same", myTeamMember1.getRoles(), not(myTeamMember2.getRoles()));
    }

}
