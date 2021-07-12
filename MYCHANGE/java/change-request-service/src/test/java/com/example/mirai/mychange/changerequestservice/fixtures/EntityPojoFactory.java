package com.example.mirai.projectname.changerequestservice.fixtures;

import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestContext;
import com.example.mirai.projectname.changerequestservice.changerequest.model.RuleSet;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestAggregate;
import com.example.mirai.projectname.changerequestservice.comment.model.ChangeRequestComment;
import com.example.mirai.projectname.changerequestservice.completebusinesscase.model.CompleteBusinessCase;
import com.example.mirai.projectname.changerequestservice.customerimpact.model.CustomerImpact;
import com.example.mirai.projectname.changerequestservice.document.model.ChangeRequestCommentDocument;
import com.example.mirai.projectname.changerequestservice.document.model.ChangeRequestDocument;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.aggregate.ImpactAnalysisAggregate;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.aggregate.ImpactAnalysisDetailsAggregate;
import com.example.mirai.projectname.changerequestservice.myteam.model.ChangeRequestMyTeam;
import com.example.mirai.projectname.changerequestservice.myteam.model.aggregate.ChangeRequestMyTeamDetailsAggregate;
import com.example.mirai.projectname.changerequestservice.preinstallimpact.model.PreinstallImpact;
import com.example.mirai.projectname.changerequestservice.scope.model.Scope;
import com.example.mirai.projectname.changerequestservice.scope.model.dto.PackagingDetail;
import com.example.mirai.projectname.changerequestservice.scope.model.dto.PartDetail;
import com.example.mirai.projectname.changerequestservice.scope.model.dto.ToolingDetail;
import com.example.mirai.projectname.changerequestservice.shared.util.ScopeValues;
import com.example.mirai.projectname.changerequestservice.solutiondefinition.model.SolutionDefinition;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.*;

public class EntityPojoFactory {

        public static ChangeRequestAggregate createChangeRequest() {
        ChangeRequestAggregate changeRequestAggregate = new ChangeRequestAggregate();
        changeRequestAggregate.setDescription(new ChangeRequest());
        return changeRequestAggregate;
    }

    public static ChangeRequestAggregate createChangeRequest(String dataIdentifier, String missingProperty,String changeNoticeStatus) {
        ChangeRequestAggregate changeRequestAggregate = new ChangeRequestAggregate();
        changeRequestAggregate.setDescription(new ChangeRequest());
        changeRequestAggregate.setSolutionDefinition(new SolutionDefinition());
        changeRequestAggregate.setScope(new Scope());
        changeRequestAggregate.getScope().setPartDetail(new PartDetail());
        changeRequestAggregate.getScope().setToolingDetail(new ToolingDetail());
        changeRequestAggregate.getScope().setPackagingDetail(new PackagingDetail());
        ImpactAnalysisAggregate impactAnalysisAggregate = new ImpactAnalysisAggregate();
        impactAnalysisAggregate.setGeneral(new ImpactAnalysis());
        ImpactAnalysisDetailsAggregate impactAnalysisDetailsAggregate = new ImpactAnalysisDetailsAggregate();
        impactAnalysisDetailsAggregate.setCustomerImpact(new CustomerImpact());
        impactAnalysisDetailsAggregate.setPreinstallImpact(new PreinstallImpact());
        impactAnalysisDetailsAggregate.setCompleteBusinessCase(new CompleteBusinessCase());
        impactAnalysisAggregate.setDetails(impactAnalysisDetailsAggregate);
        changeRequestAggregate.setImpactAnalysis(impactAnalysisAggregate);
        ChangeRequestMyTeamDetailsAggregate myTeamDetailsAggregate = new ChangeRequestMyTeamDetailsAggregate();
        myTeamDetailsAggregate.setMyTeam(new ChangeRequestMyTeam());
        changeRequestAggregate.setMyTeamDetails(myTeamDetailsAggregate);
        //change request
        addChangeRequestCreator(changeRequestAggregate.getDescription(), dataIdentifier);
        if(Objects.isNull(missingProperty)) {
            missingProperty = "";
        }
        if (!missingProperty.equals("change_specialist1")) {
            addChangeSpecialist1(changeRequestAggregate.getDescription(), dataIdentifier);
        }
        if (!missingProperty.equals("change_specialist2")) {
            addChangeSpecialist2(changeRequestAggregate.getDescription(), dataIdentifier);
        }
        if (!missingProperty.equals("title")) {
            addChangeRequestTitle(changeRequestAggregate.getDescription(), dataIdentifier);
        }
        if (!missingProperty.equals("issue_types")) {
            addChangeRequestIssueTypes(changeRequestAggregate.getDescription(), dataIdentifier);
        }
        if(Objects.nonNull(changeNoticeStatus)){
            addContext(changeRequestAggregate.getDescription(), dataIdentifier, changeNoticeStatus);
        }
        addTeamCenterContext(changeRequestAggregate.getDescription(), dataIdentifier);
        if(missingProperty.equals("agenda_item")) {
            addAgendaItemContext(changeRequestAggregate.getDescription(), dataIdentifier);
        }
        if(missingProperty.equals("air_context")) {
            addAirContext(changeRequestAggregate.getDescription(), dataIdentifier);
        }
        if(missingProperty.equals("pbs_context")) {
            addPbsContext(changeRequestAggregate.getDescription(), dataIdentifier);
        }
        addFunctionalClusterId(changeRequestAggregate.getDescription(), dataIdentifier);
        addChangeRequestChangeBoards(changeRequestAggregate.getDescription(), dataIdentifier);
        if (!missingProperty.equals("problem_description")) {
            addChangeRequestProblemDescription(changeRequestAggregate.getDescription(), dataIdentifier);
        }
        if (!missingProperty.equals("is_secure")) {
            addChangeRequestIsSecure(changeRequestAggregate.getDescription(), dataIdentifier);
        }
        if (!missingProperty.equals("project_id")) {
            addChangeRequestProjectId(changeRequestAggregate.getDescription(), dataIdentifier);
        }
        if (!missingProperty.equals("product_id")) {
            addChangeRequestProductId(changeRequestAggregate.getDescription(), dataIdentifier);
        }

        //addChangeRequestIsSecure(changeRequestAggregate.getDescription(), dataIdentifier);
        if (!missingProperty.equals("proposed_solution")) {
            addChangeRequestProposedSolution(changeRequestAggregate.getDescription(), dataIdentifier);
        }
        addChangeRequestImplementationPriority(changeRequestAggregate.getDescription(), dataIdentifier);
        if (!missingProperty.equals("root_cause")) {
            addChangeRequestRootCause(changeRequestAggregate.getDescription(), dataIdentifier);
        }
        //addChangeRequestIsSecure(changeRequestAggregate.getDescription(), dataIdentifier);
        if (!missingProperty.equals("benefits_of_change")) {
            addChangeRequestBenefitsOfChange(changeRequestAggregate.getDescription(), dataIdentifier);
        }
        addChangeRequestreasonForChange(changeRequestAggregate.getDescription(), dataIdentifier);
        addChangeRequestChangeControleBoards(changeRequestAggregate.getDescription(), dataIdentifier);
        //addChangeRequestChangeBoardRuleSet(changeRequestAggregate.getDescription(), dataIdentifier);
        //addChangeRequestIsSecure(changeRequestAggregate.getDescription(), dataIdentifier);
        if (!missingProperty.equals("requirements_for_implementation_plan")) {
            addChangeRequestRequirementsForImplementationPlan(changeRequestAggregate.getDescription(), dataIdentifier);
        }
        addChangeRequestChangeOwnerType(changeRequestAggregate.getDescription(), dataIdentifier);


        //solution-definition

        if (!missingProperty.equals("test_and_release_strategy_details")) {
            addTestAndReleaseStrategyDetails(changeRequestAggregate.getSolutionDefinition(), dataIdentifier);
        }
        if (!missingProperty.equals("products_affected")) {
            addProductsAffected(changeRequestAggregate.getSolutionDefinition(), dataIdentifier);
        }
        if (!missingProperty.equals("functional_software_dependencies")) {
            addFunctionalSoftwareDependencies(changeRequestAggregate.getSolutionDefinition(), dataIdentifier);
        }
        if (!missingProperty.equals("aligned_with_fo_details")) {
            addAlignedWithFODetails(changeRequestAggregate.getSolutionDefinition(), dataIdentifier);
        }
        if (!missingProperty.equals("technical_recommendation")) {
            addTechnicalRecommendation(changeRequestAggregate.getSolutionDefinition(), dataIdentifier);
        }

        //customer impact

        if (!missingProperty.equals("customer_approval_details")) {
            addCustomerApprovalDetails(changeRequestAggregate.getImpactAnalysis().getDetails().getCustomerImpact(), dataIdentifier);
        }
        if (!missingProperty.equals("customer_impact_result")) {
            addCustomerImpactResult(changeRequestAggregate.getImpactAnalysis().getDetails().getCustomerImpact(), dataIdentifier);
        }

        if (!missingProperty.equals("customer_communication_details")) {
            addCustomerCommunicationDetails(changeRequestAggregate.getImpactAnalysis().getDetails().getCustomerImpact(), dataIdentifier);
        }

        if (!missingProperty.equals("impact_on_user_interfaces_details")) {
            addImpactOnUserInterfacesDetails(changeRequestAggregate.getImpactAnalysis().getDetails().getCustomerImpact(), dataIdentifier);
        }

        if (!missingProperty.equals("impact_on_wafer_process_environment_details")) {
            addImpactOnWaferProcessEnvironmentDetails(changeRequestAggregate.getImpactAnalysis().getDetails().getCustomerImpact(), dataIdentifier);
        }

        if (!missingProperty.equals("change_to_customer_impact_critical_part_details")) {
            addChangeToCustomerImpactCriticalPartDetails(changeRequestAggregate.getImpactAnalysis().getDetails().getCustomerImpact(), dataIdentifier);
        }

        if (!missingProperty.equals("change_to_process_impacting_customer_details")) {
            addChangeToProcessImpactingCustomerDetails(changeRequestAggregate.getImpactAnalysis().getDetails().getCustomerImpact(), dataIdentifier);
        }
        if (!missingProperty.equals("fco_upgrade_option_csr_implementation_change_details")) {
            addFcoUpgradeOptionCsrImplementationChangedetails(changeRequestAggregate.getImpactAnalysis().getDetails().getCustomerImpact(), dataIdentifier);
        }

        //preinstall impact
        if (!missingProperty.equals("preinstall_impact_result")) {
            addPreinstallImpactResult(changeRequestAggregate.getImpactAnalysis().getDetails().getPreinstallImpact(), dataIdentifier);
        }
        if (!missingProperty.equals("change_introduces_new11_nc_details")) {
            addChangeIntroducesNew11NCDetails(changeRequestAggregate.getImpactAnalysis().getDetails().getPreinstallImpact(), dataIdentifier);
        }
        if (!missingProperty.equals("impact_on_customer_factory_layout_details")) {
            addImpactOnCustomerFactoryLayoutDetails(changeRequestAggregate.getImpactAnalysis().getDetails().getPreinstallImpact(), dataIdentifier);
        }
        if (!missingProperty.equals("impact_on_facility_flows_details")) {
            addImpactOnFacilityFlowsDetails(changeRequestAggregate.getImpactAnalysis().getDetails().getPreinstallImpact(), dataIdentifier);
        }
        if (!missingProperty.equals("impact_on_preinstall_inter_connect_cables_details")) {
            addImpactOnPreinstallInterConnectCablesDetails(changeRequestAggregate.getImpactAnalysis().getDetails().getPreinstallImpact(), dataIdentifier);
        }
        if (!missingProperty.equals("change_replaces_mentioned_parts_details")) {
            addChangeReplacesMentionedPartsDetails(changeRequestAggregate.getImpactAnalysis().getDetails().getPreinstallImpact(), dataIdentifier);
        }
        //Scope
        if (!missingProperty.equals("tooling")) {
            addTooling(changeRequestAggregate.getScope(), dataIdentifier);
        }
        if (!missingProperty.equals("packaging")) {
            addPackaging(changeRequestAggregate.getScope(), dataIdentifier);
        }
        if (!missingProperty.equals("parts")) {
            addParts(changeRequestAggregate.getScope(), dataIdentifier);
        }
        if (!missingProperty.equals("packaging_detail")) {
            addPackagingDetail(changeRequestAggregate.getScope(), dataIdentifier);
        }
        if (!missingProperty.equals("part_detail")) {
            addPartDetail(changeRequestAggregate.getScope(), dataIdentifier);
        }
        if (!missingProperty.equals("tooling_detail")) {
            addToolingDetail(changeRequestAggregate.getScope(), dataIdentifier);
        }
        if (!missingProperty.equals("scope_details")) {
            addScopeDetails(changeRequestAggregate.getScope(), dataIdentifier);
        }
        if (!missingProperty.equals("bop")) {
            addBop(changeRequestAggregate.getScope(), dataIdentifier);
        }

        //impact analysis

        if (!missingProperty.equals("implementation_ranges")) {
            addImplementationRanges(changeRequestAggregate.getImpactAnalysis(), dataIdentifier);
        }
        if (!missingProperty.equals("impact_on_sequence")) {
            addImpactOnSequence(changeRequestAggregate.getImpactAnalysis(), dataIdentifier);
        }
        if (!missingProperty.equals("impact_on_sequence_details")) {
            addImpactOnSequenceDetails(changeRequestAggregate.getImpactAnalysis(), dataIdentifier);
        }
        if (!missingProperty.equals("impact_on_availability")) {
            addImpactOnAvailability(changeRequestAggregate.getImpactAnalysis(), dataIdentifier);
        }
        if (!missingProperty.equals("impact_on_availability_details")) {
            addImpactOnAvailabilityDetails(changeRequestAggregate.getImpactAnalysis(), dataIdentifier);
        }

        if (!missingProperty.equals("phase_out_spares_tools_details")) {
            addPhaseOutSparesToolsDetails(changeRequestAggregate.getImpactAnalysis(), dataIdentifier);
        }

        if (!missingProperty.equals("tech_risk_assessment_sra_details")) {
            addTechRiskAssessmentSraDetails(changeRequestAggregate.getImpactAnalysis(), dataIdentifier);
        }

        if (!missingProperty.equals("tech_risk_assessment_fmea_details")) {
            addTechRiskAssessmentFmeaDetails(changeRequestAggregate.getImpactAnalysis(), dataIdentifier);
        }
        if (!missingProperty.equals("total_instances_affected")) {
            addTotalInstancesAffected(changeRequestAggregate.getImpactAnalysis(), dataIdentifier);
        }
        if (!missingProperty.equals("impact_on_system_level_performance")) {
            addImpactOnSystemLevelPerformance(changeRequestAggregate.getImpactAnalysis(), dataIdentifier);
        }
        if (!missingProperty.equals("impact_on_system_level_performance_details")) {
            addImpactOnSystemLevelPerformanceDetails(changeRequestAggregate.getImpactAnalysis(), dataIdentifier);
        }

        if (!missingProperty.equals("impact_on_cycle_time_details")) {
            addImpactOnCycleTimeDetails(changeRequestAggregate.getImpactAnalysis(), dataIdentifier);
        }

        if (!missingProperty.equals("liability_risks")) {
            addLiabilityRisks(changeRequestAggregate.getImpactAnalysis(), dataIdentifier);
        }
        if (!missingProperty.equals("implementation_ranges_details")) {
            addImplementationRangesDetails(changeRequestAggregate.getImpactAnalysis(), dataIdentifier);
        }
        if (!missingProperty.equals("cbp_strategies")) {
            addCbpStrategies(changeRequestAggregate.getImpactAnalysis(), dataIdentifier);
        }
        if (!missingProperty.equals("fco_types")) {
            addFcoTypes(changeRequestAggregate.getImpactAnalysis(), dataIdentifier);
        }
        if (!missingProperty.equals("cbp_strategies_details")) {
            addCbpStrategiesDetails(changeRequestAggregate.getImpactAnalysis(), dataIdentifier);
        }

        return changeRequestAggregate;
    }

    private static void addChangeRequestTitle(ChangeRequest changeRequest, String dataIdentifier) {
        changeRequest.setTitle(dataIdentifier + "_change_request-title");
    }

    private static void addChangeRequestProblemDescription(ChangeRequest changeRequest, String dataIdentifier) {
        changeRequest.setProblemDescription(dataIdentifier + "_change_request-problem_description");
    }

    private static void addChangeRequestIsSecure(ChangeRequest changeRequest, String dataIdentifier) {
        changeRequest.setIsSecure(false);
    }

    private static void addChangeRequestChangeOwnerType(ChangeRequest changeRequest, String dataIdentifier) {
        changeRequest.setChangeOwnerType(dataIdentifier + "_change_owner_type");
    }

    private static void addChangeRequestImplementationPriority(ChangeRequest changeRequest, String dataIdentifier) {
        changeRequest.setImplementationPriority(1);
    }

    private static void addChangeRequestProjectId(ChangeRequest changeRequest, String dataIdentifier) {
        changeRequest.setProjectId("2237-0035");
    }
    private static void addChangeRequestProductId(ChangeRequest changeRequest, String dataIdentifier) {
        changeRequest.setProductId("1113");
    }

    private static void addChangeRequestIssueTypes(ChangeRequest changeRequest, String dataIdentifier) {
        List<String> issueTypes = new ArrayList<>();
        issueTypes.add(dataIdentifier + "_change_request-issue_type");
        changeRequest.setIssueTypes(issueTypes);
    }

    private static void addChangeRequestChangeBoards(ChangeRequest changeRequest, String dataIdentifier) {
        List<String> changeBoards = new ArrayList<>();
        changeBoards.add(dataIdentifier + "_change_request-change-boards");
        changeRequest.setChangeBoards(changeBoards);
    }
    private static void addFunctionalClusterId(ChangeRequest changeRequest, String dataIdentifier){
        changeRequest.setFunctionalClusterId(dataIdentifier+"_functional_clusterId");
    }
    private static void addChangeRequestProposedSolution(ChangeRequest changeRequest, String dataIdentifier) {
        changeRequest.setProposedSolution(dataIdentifier + "_change_request-proposed_solution");
    }
    private static void addChangeRequestRootCause(ChangeRequest changeRequest, String dataIdentifier) {
        changeRequest.setRootCause(dataIdentifier + "_change_request-root_cause");
    }
    private static void addChangeRequestBenefitsOfChange(ChangeRequest changeRequest, String dataIdentifier) {
        changeRequest.setBenefitsOfChange(dataIdentifier + "_change_request-benefits_of_change");
    }
    private static void addChangeRequestreasonForChange(ChangeRequest changeRequest, String dataIdentifier) {
        List<String> reasonForChange = new ArrayList<>();
        reasonForChange.add(dataIdentifier + "_change_request-reason_for_change");
        changeRequest.setReasonsForChange(reasonForChange);
    }
    private static void addChangeRequestChangeControleBoards(ChangeRequest changeRequest, String dataIdentifier) {
        List<String> changeControleBoards = new ArrayList<>();
        changeControleBoards.add(dataIdentifier + "_change_request-change_controle_boards");
        changeRequest.setChangeControlBoards(changeControleBoards);
    }
   /* private static void addChangeRequestChangeBoardRuleSet(ChangeRequest changeRequest, String dataIdentifier) {
        RuleSet ruleSet = new RuleSet();
        ruleSet.setRuleSetName("ruleset_"+dataIdentifier);
        List<String> rules = new ArrayList<>();
        rules.add("rule1");
        rules.add("rule2");
        ruleSet.setRules(rules);
        changeRequest.setChangeBoardRuleSet(ruleSet);
    }*/
    private static void addChangeRequestRequirementsForImplementationPlan(ChangeRequest changeRequest, String dataIdentifier) {
        changeRequest.setRequirementsForImplementationPlan(dataIdentifier + "_change_request-requirements_for_implementation_plan");
    }
    

    private static void addTestAndReleaseStrategyDetails(SolutionDefinition solutionDefinition, String dataIdentifier) {
        solutionDefinition.setTestAndReleaseStrategyDetails(dataIdentifier + "_change_request-test_and_release_strategy_details");
    }
    private static void addProductsAffected(SolutionDefinition solutionDefinition, String dataIdentifier) {
        List<String> productsAffected = new ArrayList<>();
        productsAffected.add(dataIdentifier + "_change_request-products_affected");
        solutionDefinition.setProductsAffected(productsAffected);
    }
    private static  void addFunctionalSoftwareDependencies(SolutionDefinition solutionDefinition, String dataIdentifier){
        solutionDefinition.setFunctionalSoftwareDependencies(dataIdentifier + "_change_request-functional_software_dependencies");
    }

    private static void addAlignedWithFODetails(SolutionDefinition solutionDefinition, String dataIdentifier) {
        solutionDefinition.setAlignedWithFoDetails(dataIdentifier + "_change_request-aligned_with_fo_details");
    }
    private static void addTechnicalRecommendation(SolutionDefinition solutionDefinition, String dataIdentifier) {
        solutionDefinition.setTechnicalRecommendation(dataIdentifier + "_change_request-technical_recommendation");
    }

    private static void addCustomerApprovalDetails(CustomerImpact customerImpact, String dataIdentifier) {
        customerImpact.setCustomerApprovalDetails(dataIdentifier + "_change_request-customer_approval_details");
    }
    private static void addCustomerImpactResult(CustomerImpact customerImpact, String dataIdentifier) {
        customerImpact.setCustomerImpactResult(dataIdentifier + "_change_request-customer_impact_result");
    }

    private static void addCustomerCommunicationDetails(CustomerImpact customerImpact, String dataIdentifier) {
        customerImpact.setCustomerCommunicationDetails(dataIdentifier + "_change_request-customer_communication_details");
    }

    private static void addImpactOnUserInterfacesDetails(CustomerImpact customerImpact, String dataIdentifier) {
        customerImpact.setImpactOnUserInterfacesDetails(dataIdentifier + "_change_request-impact_on_user_interfaces_details");
    }

    private static void addImpactOnWaferProcessEnvironmentDetails(CustomerImpact customerImpact, String dataIdentifier) {
        customerImpact.setImpactOnWaferProcessEnvironmentDetails(dataIdentifier + "_change_request-impact_on_wafer_process_environment_details");
    }

    private static void addChangeToCustomerImpactCriticalPartDetails(CustomerImpact customerImpact, String dataIdentifier) {
        customerImpact.setChangeToCustomerImpactCriticalPartDetails(dataIdentifier + "_change_request-change_to_customer_impact_critical_part_details");
    }

    private static void addChangeToProcessImpactingCustomerDetails(CustomerImpact customerImpact, String dataIdentifier) {
        customerImpact.setChangeToProcessImpactingCustomerDetails(dataIdentifier + "_change_request-change_to_process_impacting_customer_details");
    }
    private static void addFcoUpgradeOptionCsrImplementationChangedetails(CustomerImpact customerImpact, String dataIdentifier) {
        customerImpact.setFcoUpgradeOptionCsrImplementationChangeDetails(dataIdentifier + "_change_request-fco_upgrade_option_csr_implementation_change_details");
    }

    private static void addPreinstallImpactResult(PreinstallImpact preinstallImpact, String dataIdentifier) {
        preinstallImpact.setPreinstallImpactResult(dataIdentifier + "_change_request-preinstall_impact_result");
    }
    private static void addChangeIntroducesNew11NCDetails(PreinstallImpact preinstallImpact, String dataIdentifier) {
        preinstallImpact.setChangeIntroducesNew11NcDetails(dataIdentifier + "_change_request-change_introduces_new11NC_details");
    }
    private static void addImpactOnCustomerFactoryLayoutDetails(PreinstallImpact preinstallImpact, String dataIdentifier) {
        preinstallImpact.setImpactOnCustomerFactoryLayoutDetails(dataIdentifier + "_change_request-impact_on_customer_factory_layout_details");
    }
    private static void addImpactOnFacilityFlowsDetails(PreinstallImpact preinstallImpact, String dataIdentifier) {
        preinstallImpact.setImpactOnFacilityFlowsDetails(dataIdentifier + "_change_request-impact_on_facility_flows_details");
    }
    private static void addImpactOnPreinstallInterConnectCablesDetails(PreinstallImpact preinstallImpact, String dataIdentifier) {
        preinstallImpact.setImpactOnPreinstallInterConnectCablesDetails(dataIdentifier + "_change_request-impact_on_preinstall_inter_connect_cables_details");
    }
    private static void addChangeReplacesMentionedPartsDetails(PreinstallImpact preinstallImpact, String dataIdentifier) {
        preinstallImpact.setChangeReplacesMentionedPartsDetails(dataIdentifier + "_change_request-change_replaces_mentioned_parts_details");
    }
    private static void addImpactOnSequence(ImpactAnalysisAggregate impactAnalysis, String dataIdentifier) {
        impactAnalysis.getGeneral().setImpactOnSequence(dataIdentifier + "_change_request-impact_on_sequence");
    }

    private static void addImpactOnSequenceDetails(ImpactAnalysisAggregate impactAnalysis, String dataIdentifier) {
        impactAnalysis.getGeneral().setImpactOnSequenceDetails(dataIdentifier + "_change_request-impact_on_sequence_details");
    }
    private static void addImpactOnAvailability(ImpactAnalysisAggregate impactAnalysis, String dataIdentifier) {
        impactAnalysis.getGeneral().setImpactOnAvailability(dataIdentifier + "_change_request-impact_on_availability");
    }
    private static void addImpactOnAvailabilityDetails(ImpactAnalysisAggregate impactAnalysis, String dataIdentifier) {
        impactAnalysis.getGeneral().setImpactOnAvailabilityDetails(dataIdentifier + "_change_request-impact_on_availability_details");
    }

    private static void addPhaseOutSparesToolsDetails(ImpactAnalysisAggregate impactAnalysis, String dataIdentifier) {
        impactAnalysis.getGeneral().setPhaseOutSparesToolsDetails(dataIdentifier + "_change_request-phase_out_spares_tools_details");
    }

    private static void addTechRiskAssessmentSraDetails(ImpactAnalysisAggregate impactAnalysis, String dataIdentifier) {
        impactAnalysis.getGeneral().setTechRiskAssessmentSraDetails(dataIdentifier + "_change_request-tech_risk_assessment_sra_details");
    }

    private static void addTechRiskAssessmentFmeaDetails(ImpactAnalysisAggregate impactAnalysis, String dataIdentifier) {
        impactAnalysis.getGeneral().setTechRiskAssessmentFmeaDetails(dataIdentifier + "_change_request-tech_risk_assessment_fmea_details");
    }
    private static void addTotalInstancesAffected(ImpactAnalysisAggregate impactAnalysis, String dataIdentifier) {
        impactAnalysis.getGeneral().setTotalInstancesAffected(dataIdentifier + "_change_request-total_instances_affected");
    }
    private static void addImpactOnSystemLevelPerformance(ImpactAnalysisAggregate impactAnalysis, String dataIdentifier) {
        impactAnalysis.getGeneral().setImpactOnSystemLevelPerformance(dataIdentifier + "_change_request-impact_on_system_level_performance");
    }
    private static void addImpactOnSystemLevelPerformanceDetails(ImpactAnalysisAggregate impactAnalysis, String dataIdentifier) {
        impactAnalysis.getGeneral().setImpactOnSystemLevelPerformanceDetails(dataIdentifier + "_change_request-impact_on_system_level_performance_details");
    }

    private static void addImpactOnCycleTimeDetails(ImpactAnalysisAggregate impactAnalysis, String dataIdentifier) {
        impactAnalysis.getGeneral().setImpactOnCycleTimeDetails(dataIdentifier + "_change_request-impact_on_cycle_time_details");
    }

    private static void addLiabilityRisks(ImpactAnalysisAggregate impactAnalysis, String dataIdentifier) {
        List<String> liabilityRisks = new ArrayList<>();
        liabilityRisks.add(dataIdentifier + "_change_request-liability_risks");
        impactAnalysis.getGeneral().setLiabilityRisks(liabilityRisks);
    }
    private static void addImplementationRangesDetails(ImpactAnalysisAggregate impactAnalysis, String dataIdentifier) {
        impactAnalysis.getGeneral().setImplementationRangesDetails(dataIdentifier + "_change_request-implementation_ranges_details");
    }
    private static void addCbpStrategies(ImpactAnalysisAggregate impactAnalysis, String dataIdentifier) {
        List<String> cbpStrategies = new ArrayList<>();
        cbpStrategies.add(dataIdentifier + "_change_request-cbp_strategies");
        impactAnalysis.getGeneral().setCbpStrategies(cbpStrategies);
    }
    private static void addFcoTypes(ImpactAnalysisAggregate impactAnalysis, String dataIdentifier) {
        List<String> fcoTypes = new ArrayList<>();
        fcoTypes.add(dataIdentifier + "_change_request-fco_types");
        impactAnalysis.getGeneral().setFcoTypes(fcoTypes);
    }
    private static void addCbpStrategiesDetails(ImpactAnalysisAggregate impactAnalysis, String dataIdentifier) {
        impactAnalysis.getGeneral().setCbpStrategiesDetails(dataIdentifier + "_change_request-cbp_strategies_details");
    }

    private static void addImplementationRanges(ImpactAnalysisAggregate impactAnalysis, String dataIdentifier) {
        List<String> implementationRanges = new ArrayList<>();
        implementationRanges.add(dataIdentifier + "_change_request-implementation_ranges");
        impactAnalysis.getGeneral().setImplementationRanges(implementationRanges);
    }

    private static void addChangeSpecialist1(ChangeRequest changeRequest, String dataIdentifier) {
        User changeSpecialist1 = new User();
        changeSpecialist1.setAbbreviation(dataIdentifier + "_change_request-change_specialist1_abbreviation");
        changeSpecialist1.setDepartmentName(dataIdentifier + "_change_request-change_specialist1_department_name");
        changeSpecialist1.setEmail(dataIdentifier + "_change_request-change_specialist1_email");
        changeSpecialist1.setFullName(dataIdentifier + "_change_request-change_specialist1_full_name");
        changeSpecialist1.setUserId(dataIdentifier + "_change_request-change_specialist1_user_id");
        changeRequest.setChangeSpecialist1(changeSpecialist1);
    }

    private static void addChangeSpecialist2(ChangeRequest changeRequest, String dataIdentifier) {
        User changeSpecialist2 = new User();
        changeSpecialist2.setAbbreviation(dataIdentifier + "_change_request-change_specialist2_abbreviation");
        changeSpecialist2.setDepartmentName(dataIdentifier + "_change_request-change_specialist2_department_name");
        changeSpecialist2.setEmail(dataIdentifier + "_change_request-change_specialist2_email");
        changeSpecialist2.setFullName(dataIdentifier + "_change_request-change_specialist2_full_name");
        changeSpecialist2.setUserId(dataIdentifier + "_change_request-change_specialist2_user_id");
        changeRequest.setChangeSpecialist2(changeSpecialist2);
    }

    private static void addTooling(Scope scope,String dataIdentifier){
        scope.setTooling(ScopeValues.IN_SCOPE);
    }
    private static void addPackaging(Scope scope,String dataIdentifier){
        scope.setPackaging(ScopeValues.IN_SCOPE);
    }
    private static void addParts(Scope scope,String dataIdentifier){
        scope.setParts(ScopeValues.IN_SCOPE);
    }
    private static void addScopeDetails(Scope scope,String dataIdentifier){
        scope.setScopeDetails(dataIdentifier +"_scopedetails");
    }
    private static void addBop(Scope scope,String dataIdentifier){
        scope.setBop(dataIdentifier +"_bop");
    }

    private static void addPackagingDetail(Scope scope,String dataIdentifier){
        scope.getPackagingDetail().setSupplierPackaging(dataIdentifier+"_SupplierPackaging");
        scope.getPackagingDetail().setReusablePackaging(dataIdentifier+"_ReusablePackaging");
        scope.getPackagingDetail().setShippingPackaging(dataIdentifier+"_ShippingPackaging");
        scope.getPackagingDetail().setStoragePackaging(dataIdentifier+"_StoragePackaging");

    }
    private static void addPartDetail(Scope scope,String dataIdentifier){
        scope.getPartDetail().setDevBagPart(dataIdentifier+"_DevBagPart");
        scope.getPartDetail().setPreinstallPart(dataIdentifier+"_PreinstallPart");
        scope.getPartDetail().setServicePart(dataIdentifier+"_ServicePart");
        scope.getPartDetail().setTestRigPart(dataIdentifier+"_TestRigPart");
        scope.getPartDetail().setMachineBomPart(dataIdentifier+"_MachineBomPart");
        scope.getPartDetail().setFcoUpgradeOptionCsr(dataIdentifier+"_FcoUpgradeOptionCsr");

    }
    private static void addToolingDetail(Scope scope,String dataIdentifier){
        scope.getToolingDetail().setServiceTooling(dataIdentifier+"_ServiceTooling");
        scope.getToolingDetail().setSupplierTooling(dataIdentifier+"_SupplierTooling");
        scope.getToolingDetail().setManufacturingDeTooling(dataIdentifier+"_ManufacturingDeTooling");

    }
    private static void addDevBagPart(Scope scope,String dataIdentifier){
        scope.getPartDetail().setDevBagPart(dataIdentifier +"_DevBagPart");
    }
    private static void addFcoUpgradeOptionCsr(Scope scope,String dataIdentifier){
        scope.getPartDetail().setFcoUpgradeOptionCsr(dataIdentifier +"_FcoUpgradeOptionCsr");
    }
    private static void addMachineBomPart(Scope scope,String dataIdentifier){
        scope.getPartDetail().setMachineBomPart(dataIdentifier +"_MachineBomPart");
    }
    private static void addPreinstallPart(Scope scope,String dataIdentifier){
        scope.getPartDetail().setPreinstallPart(dataIdentifier +"_PreinstallPart");
    }
    private static void addServicePart(Scope scope,String dataIdentifier){
        scope.getPartDetail().setServicePart(dataIdentifier +"_ServicePart");
    }
    private static void addTestRigPart(Scope scope,String dataIdentifier){
        scope.getPartDetail().setTestRigPart(dataIdentifier +"_TestRigPart");
    }

    private static void addContext(ChangeRequest changeRequest,String dataIdentifier,String changeNoticeStatus){
        ChangeRequestContext changeRequestContext = new ChangeRequestContext();
        changeRequestContext.setContextId(dataIdentifier);
        changeRequestContext.setName("New CR");
        changeRequestContext.setType("CHANGENOTICE");
        changeRequestContext.setStatus(changeNoticeStatus);
        List<ChangeRequestContext> contexts = new ArrayList<>();
        contexts.add(changeRequestContext);
        changeRequest.setContexts(contexts);

    }
    private static void addTeamCenterContext(ChangeRequest changeRequest,String dataIdentifier){
       // long contextId = (long) (Math.random() * 2 * Long.MAX_VALUE - Long.MAX_VALUE);
        long contextId = System.currentTimeMillis();
        ChangeRequestContext changeRequestContext = new ChangeRequestContext();
        changeRequestContext.setContextId(new Long(contextId).toString());
        changeRequestContext.setName("New CR");
        changeRequestContext.setType("TEAMCENTER");
        changeRequestContext.setStatus("NEW");
        List<ChangeRequestContext> contexts = new ArrayList<>();
        contexts.add(changeRequestContext);
        changeRequest.setContexts(contexts);

    }

    private static void addAgendaItemContext(ChangeRequest changeRequest,String dataIdentifier){
        long agendacontextId = System.currentTimeMillis()+100;
        ChangeRequestContext changeRequestContext = new ChangeRequestContext();
        changeRequestContext.setContextId(new Long(agendacontextId).toString());
        changeRequestContext.setName("New CR");
        changeRequestContext.setType("AGENDAITEM");
        changeRequestContext.setStatus("NEW");
        List<ChangeRequestContext> contexts = new ArrayList<>();
        contexts.add(changeRequestContext);

        long aircontextId = System.currentTimeMillis()+300;
        ChangeRequestContext changeRequestContext1 = new ChangeRequestContext();
        changeRequestContext1.setContextId(new Long(aircontextId).toString());
        changeRequestContext1.setName("New CR");
        changeRequestContext1.setType("AIR");
        changeRequestContext1.setStatus("NEW");
        contexts.add(changeRequestContext1);
        changeRequest.setContexts(contexts);

        long contextId = System.currentTimeMillis()+510;
        ChangeRequestContext changeRequestContext2 = new ChangeRequestContext();
        changeRequestContext2.setContextId(new Long(contextId).toString());
        changeRequestContext2.setName("New CR");
        changeRequestContext2.setType("PBS");
        changeRequestContext2.setStatus("NEW");
        contexts.add(changeRequestContext2);

        changeRequest.setContexts(contexts);
    }

    private static void addAirContext(ChangeRequest changeRequest,String dataIdentifier){
        long contextId = System.currentTimeMillis()+210;
        ChangeRequestContext changeRequestContext = new ChangeRequestContext();
        changeRequestContext.setContextId(new Long(contextId).toString());
        changeRequestContext.setName("New CR");
        changeRequestContext.setType("AIR");
        changeRequestContext.setStatus("NEW");
        List<ChangeRequestContext> contexts = new ArrayList<>();
        contexts.add(changeRequestContext);
        changeRequest.setContexts(contexts);
    }

    private static void addPbsContext(ChangeRequest changeRequest,String dataIdentifier){
        long contextId = System.currentTimeMillis()+510;
        ChangeRequestContext changeRequestContext = new ChangeRequestContext();
        changeRequestContext.setContextId(new Long(contextId).toString());
        changeRequestContext.setName("New CR");
        changeRequestContext.setType("PBS");
        changeRequestContext.setStatus("NEW");
        List<ChangeRequestContext> contexts = new ArrayList<>();
        contexts.add(changeRequestContext);
        changeRequest.setContexts(contexts);
    }

    private static ChangeRequest addChangeRequestCreator(ChangeRequest changeRequest, String dataIdentifier) {
        User creator = new User();
        creator.setAbbreviation(dataIdentifier + "_change_request-creator_abbreviation");
        creator.setDepartmentName(dataIdentifier + "_change_request-creator_department_name");
        creator.setEmail(dataIdentifier + "_change_request-creator_email");
        creator.setFullName(dataIdentifier + "_change_request-creator_full_name");
        creator.setUserId(dataIdentifier + "_change_request-creator_user_id");
        changeRequest.setCreator(creator);

        return changeRequest;
    }
    /*private static ChangeRequest createChangeRequest(String dataIdentifier) {

    }*/

    public static ChangeRequestComment createChangeRequestComment(String dataIdentifier, String properties) {

        ChangeRequestComment changeRequestComment = new ChangeRequestComment();
        switch (properties) {
            case "ALL_PROPERTIES":
                addChangeRequestCommentText(changeRequestComment, dataIdentifier);
                addChangeRequestCommentCreator(changeRequestComment, dataIdentifier);
                addChangeRequestCommentCreatedOn(changeRequestComment, dataIdentifier);
                return changeRequestComment;
            default:
                return changeRequestComment;
        }
    }

    private static ChangeRequestComment addChangeRequestCommentCreator(ChangeRequestComment changeRequestComment, String dataIdentifier) {
        User creator = new User();
        creator.setAbbreviation(dataIdentifier + "_Comment.creator_abbreviation");
        creator.setDepartmentName(dataIdentifier + "_Comment.creator_department_name");
        creator.setEmail(dataIdentifier + "_Comment.creator_email");
        creator.setFullName(dataIdentifier + "_Comment.creator_full_name");
        creator.setUserId(dataIdentifier + "_Comment.creator_user_id");
        changeRequestComment.setCreator(creator);
        return changeRequestComment;
    }

    private static ChangeRequestComment addChangeRequestCommentText(ChangeRequestComment changeRequestComment, String dataIdentifier) {
        changeRequestComment.setCommentText(dataIdentifier + "_text");
        return changeRequestComment;
    }

    private static ChangeRequestComment addChangeRequestCommentCreatedOn(ChangeRequestComment changeRequestComment, String dataIdentifier) {
        changeRequestComment.setCreatedOn(getDate(dataIdentifier));
        return changeRequestComment;
    }

    private static Date getDate(String dataIdentifier) {
        try {
            Long timestamp = Long.parseLong(dataIdentifier);
            return new Date(timestamp);
        } catch (NumberFormatException nfe) {
            return new Date();
        }
    }
    public static ChangeRequestDocument createChangeRequestDocument(String dataIdentifier, String properties) {

        ChangeRequestDocument changeRequestDocument = new ChangeRequestDocument();
        switch (properties) {
            case "ALL_PROPERTIES":
                addChangeRequestDocumentTags(changeRequestDocument, dataIdentifier);
                addChangeRequestDocumentCreator(changeRequestDocument, dataIdentifier);
                addChangeRequestDocumentCreatedOn(changeRequestDocument, dataIdentifier);
                addChangeRequestDocumentDescription(changeRequestDocument, dataIdentifier);
                return changeRequestDocument;
            default:
                return changeRequestDocument;
        }
    }

    private static ChangeRequestDocument addChangeRequestDocumentTags(ChangeRequestDocument changeRequestDocument, String dataIdentifier) {
        List<String> documentTags = new ArrayList<>();
        documentTags.add("Notes");
        changeRequestDocument.setTags(Collections.singletonList(dataIdentifier + documentTags));
        return changeRequestDocument;
    }

    private static ChangeRequestDocument addChangeRequestDocumentDescription(ChangeRequestDocument changeRequestDocument, String dataIdentifier) {
        changeRequestDocument.setDescription(dataIdentifier + "_description");
        return changeRequestDocument;
    }

    private static ChangeRequestDocument addChangeRequestDocumentCreator(ChangeRequestDocument changeRequestDocument, String dataIdentifier) {
        User creator = new User();
        creator.setAbbreviation(dataIdentifier + "_cug-projectname-change-specialist-2_abbreviation");
        creator.setDepartmentName(dataIdentifier + "_cug-projectname-change-specialist-2_department_name");
        creator.setEmail(dataIdentifier + "_cug-projectname-change-specialist-2_email");
        creator.setFullName(dataIdentifier + "_cug-projectname-change-specialist-2_full_name");
        creator.setUserId(dataIdentifier + "_cug-projectname-change-specialist-2_user_id");
        changeRequestDocument.setCreator(creator);
        return changeRequestDocument;
    }

    private static ChangeRequestDocument addChangeRequestDocumentCreatedOn(ChangeRequestDocument changeRequestDocument, String dataIdentifier) {
        changeRequestDocument.setCreatedOn(getDate(dataIdentifier));
        return changeRequestDocument;
    }

    public static ChangeRequestCommentDocument createChangeRequestCommentDocument(String dataIdentifier, String properties) {

        ChangeRequestCommentDocument changeRequestCommentDocument = new ChangeRequestCommentDocument();
        switch (properties) {
            case "ALL_PROPERTIES":
                addChangeRequestCommentDocumentTags(changeRequestCommentDocument, dataIdentifier);
                addChangeRequestCommentDocumentCreator(changeRequestCommentDocument, dataIdentifier);
                addChangeRequestCommentDocumentCreatedOn(changeRequestCommentDocument, dataIdentifier);
                addChangeRequestCommentDocumentDescription(changeRequestCommentDocument, dataIdentifier);
                return changeRequestCommentDocument;
            default:
                return changeRequestCommentDocument;
        }
    }

    private static ChangeRequestCommentDocument addChangeRequestCommentDocumentTags(ChangeRequestCommentDocument changeRequestCommentDocument, String dataIdentifier) {
        List<String> documentTags = new ArrayList<>();
        documentTags.add("Notes");
        changeRequestCommentDocument.setTags(Collections.singletonList(dataIdentifier + documentTags));
        return changeRequestCommentDocument;
    }

    private static ChangeRequestCommentDocument addChangeRequestCommentDocumentDescription(ChangeRequestCommentDocument changeRequestCommentDocument, String dataIdentifier) {
        changeRequestCommentDocument.setDescription(dataIdentifier + "_description");
        return changeRequestCommentDocument;
    }


    private static ChangeRequestCommentDocument addChangeRequestCommentDocumentCreator(ChangeRequestCommentDocument changeRequestCommentDocument, String dataIdentifier) {
        User creator = new User();
        creator.setAbbreviation(dataIdentifier + "_ChangeRequestComment.creator_abbreviation");
        creator.setDepartmentName(dataIdentifier + "_ChangeRequestComment.creator_department_name");
        creator.setEmail(dataIdentifier + "_ChangeRequestComment.creator_email");
        creator.setFullName(dataIdentifier + "_ChangeRequestComment.creator_full_name");
        creator.setUserId(dataIdentifier + "_ChangeRequestComment.creator_user_id");
        changeRequestCommentDocument.setCreator(creator);
        return changeRequestCommentDocument;
    }

    private static ChangeRequestCommentDocument addChangeRequestCommentDocumentCreatedOn(ChangeRequestCommentDocument changeRequestCommentDocument, String dataIdentifier) {
        changeRequestCommentDocument.setCreatedOn(getDate(dataIdentifier));
        return changeRequestCommentDocument;
    }

    public static MyTeamMember createMyTeamMemberRequest(String dataIdentifier, ChangeRequestMyTeam savedChangedRequestMyTeam) throws JsonProcessingException {
        MyTeamMember myTeamMember = new MyTeamMember();
        Random random = new Random();
        myTeamMember.setId(random.nextLong());
        myTeamMember.setMyteam(savedChangedRequestMyTeam);
        User user = new User();
        user.setAbbreviation(dataIdentifier + "_my_team_member-abbreviation");
        user.setDepartmentName(dataIdentifier + "_my_team_member_department_name");
        user.setEmail(dataIdentifier + "_my_team_member_email");
        user.setFullName(dataIdentifier + "_my_team_member_full_name");
        user.setUserId(dataIdentifier + "_my_team_member_user_id");
        myTeamMember.setUser(user);
        List<String> roles = new ArrayList<>();
        roles.add("submitterRequestor");
        roles.add("changeSpecialist2");
        myTeamMember.setRoles(roles);
        return myTeamMember;
    }

    public static User createMyTeamMember(String dataIdentifier) {
        User user = new User();
        user.setAbbreviation(dataIdentifier + "_my_team_member-abbreviation");
        user.setDepartmentName(dataIdentifier + "_my_team_member_department_name");
        user.setEmail(dataIdentifier + "_my_team_member_email");
        user.setFullName(dataIdentifier + "_my_team_member_full_name");
        user.setUserId(dataIdentifier + "_my_team_member_user_id");
        return user;
    }

    @Data
    private static class TeamMemberDetail {
        User user;
        String[] roles;
    }

    @Data
    private static class TeamMemberRole {
        List<String> roles = new ArrayList<>();
    }

    @Data
    private static class UpdateTeamMemberRequest {
        TeamMemberRole oldIns = new TeamMemberRole();
        TeamMemberRole newIns = new TeamMemberRole();
    }

    public static String getUpdateRequest(String oldIns, String newIns) throws JsonProcessingException {
        UpdateTeamMemberRequest request = new UpdateTeamMemberRequest();
        request.oldIns.roles.add(oldIns);

        request.newIns.roles.add(newIns);
        return new ObjectMapper().writeValueAsString(request);
    }

}
