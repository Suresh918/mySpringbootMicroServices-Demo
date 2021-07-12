
package com.example.mirai.projectname.changerequestservice.fixtures;

import com.example.mirai.libraries.comment.model.CommentStatus;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.document.model.DocumentStatus;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestContext;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestStatus;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestAggregate;
import com.example.mirai.projectname.changerequestservice.comment.model.ChangeRequestComment;
import com.example.mirai.projectname.changerequestservice.completebusinesscase.model.CompleteBusinessCase;
import com.example.mirai.projectname.changerequestservice.customerimpact.model.CustomerImpact;
import com.example.mirai.projectname.changerequestservice.document.model.ChangeRequestCommentDocument;
import com.example.mirai.projectname.changerequestservice.document.model.ChangeRequestDocument;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
import com.example.mirai.projectname.changerequestservice.myteam.model.aggregate.ChangeRequestMyTeamDetailsAggregate;
import com.example.mirai.projectname.changerequestservice.preinstallimpact.model.PreinstallImpact;
import com.example.mirai.projectname.changerequestservice.scope.model.Scope;
import com.example.mirai.projectname.changerequestservice.solutiondefinition.model.SolutionDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
public class EntityInstanceManager {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Long findChangeRequestIdByCreatorUserId(String title) {
        String sql = "SELECT ID FROM CHANGE_REQUEST WHERE CREATOR_USER_ID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{title}, (rs, rowNum) ->
                rs.getLong("id"));

    }

    public Long createChangeRequestAndSetStatus(String dataIdentifier, String missingProperty, ChangeRequestStatus changeRequestStatus, String changeNoticeStatus) {
        Long id = createChangeRequest(dataIdentifier, missingProperty,changeNoticeStatus);
        assert id != null;
        String stmt = "update change_request set status = ? where id = ?";
        int updatedRecords = jdbcTemplate.update(stmt, changeRequestStatus.getStatusCode(), id);
        assert updatedRecords == 1;

        return id;
    }

    public Long createChangeRequest(String dataIdentifier, String missingProperty,String changeNoticeStatus) {
        ChangeRequestAggregate requestChangeRequestAggregate = null;
        //prepare data
        requestChangeRequestAggregate = EntityPojoFactory.createChangeRequest(dataIdentifier, missingProperty,changeNoticeStatus);
        ChangeRequest changeRequest = requestChangeRequestAggregate.getDescription();
        SolutionDefinition solutionDefinition = requestChangeRequestAggregate.getSolutionDefinition();
        CustomerImpact customerImpact = requestChangeRequestAggregate.getImpactAnalysis().getDetails().getCustomerImpact();
        PreinstallImpact preinstallImpact = requestChangeRequestAggregate.getImpactAnalysis().getDetails().getPreinstallImpact();
        ImpactAnalysis impactAnalysis = requestChangeRequestAggregate.getImpactAnalysis().getGeneral();
        CompleteBusinessCase completeBusinessCase = requestChangeRequestAggregate.getImpactAnalysis().getDetails().getCompleteBusinessCase();
        Scope scope = requestChangeRequestAggregate.getScope();
        //insert data in db
        insertChangeRequestData(requestChangeRequestAggregate.getDescription());
        Long changeRequestId = findChangeRequestIdByCreatorUserId(requestChangeRequestAggregate.getDescription().getCreator().getUserId());
        Long solutionDefinitionId =  insertSolutionDefinitionData(changeRequestId, requestChangeRequestAggregate.getSolutionDefinition());
        Long impactAnalysisId = insertImpactAnalysisData(changeRequestId, requestChangeRequestAggregate.getImpactAnalysis().getGeneral());
        Long scopeId = insertScopeData(changeRequestId, requestChangeRequestAggregate.getScope());
        Long completeBusinessCaseId = insertCompleteBusinessCaseData(impactAnalysisId, requestChangeRequestAggregate.getImpactAnalysis().getDetails().getCompleteBusinessCase());
        Long customerImpactId = insertCustomerImpactData(impactAnalysisId, requestChangeRequestAggregate.getImpactAnalysis().getDetails().getCustomerImpact());
        Long preinstallImpactId = insertPreinstallImpactData(impactAnalysisId, requestChangeRequestAggregate.getImpactAnalysis().getDetails().getPreinstallImpact());
        prepareMyTeam(changeRequestId, requestChangeRequestAggregate.getMyTeamDetails());
        // Long changeRequestId = findChangeRequestIdByCreatorUserId(requestChangeRequestAggregate.getDescription().getCreator().getUserId());
        Long nextVal = getNextHibernateSequence();
        String stmt = "insert into aud_updater (abbreviation, department_name, email, full_name, " +
                "timestamp, user_id, id) values (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(stmt,
                requestChangeRequestAggregate.getDescription().getCreator().getAbbreviation(), requestChangeRequestAggregate.getDescription().getCreator().getDepartmentName(), requestChangeRequestAggregate.getDescription().getCreator().getEmail(), requestChangeRequestAggregate.getDescription().getCreator().getFullName(),
                (new Date()).getTime(), requestChangeRequestAggregate.getDescription().getCreator().getUserId(), nextVal);


        stmt = "insert into aud_change_request (revtype, revend, revend_tstmp, " +
                "analysis_priority, analysis_priority_mod, created_on, created_on_mod, " +
                "benefits_of_change, benefits_of_change_mod, creator_abbreviation, creator_department_name, creator_email," +
                " creator_full_name, creator_user_id, creator_mod, rule_set_name, change_board_rule_set_mod, change_request_type, " +
                "change_request_type_mod, change_specialist1_abbreviation, change_specialist1_user_id,change_specialist1_department_name," +
                " change_specialist1_email, change_specialist1_full_name, change_specialist1_mod, change_specialist2_abbreviation," +
                " change_specialist2_user_id, change_specialist2_department_name, change_specialist2_email, change_specialist2_full_name," +
                " change_specialist2_mod,excess_and_obsolescence_savings, excess_and_obsolescence_savings_mod,functional_cluster_id," +
                " functional_cluster_id_mod,implementation_priority, implementation_priority_mod, is_secure, is_secure_mod," +
                " problem_description, problem_description_mod, product_id, product_id_mod, project_id, project_id_mod, proposed_solution," +
                " proposed_solution_mod, requirements_for_implementation_plan, requirements_for_implementation_plan_mod, root_cause," +
                " root_cause_mod, status, status_mod, title, title_mod, change_boards_mod, change_control_boards_mod, contexts_mod," +
                " dependent_change_request_ids_mod, issue_types_mod, reasons_for_change_mod, id, rev) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(stmt, 1, null, null,
                changeRequest.getAnalysisPriority(), false, changeRequest.getCreatedOn(), true,
                changeRequest.getBenefitsOfChange(), false, changeRequest.getCreator().getAbbreviation(),
                changeRequest.getCreator().getDepartmentName(), changeRequest.getCreator().getEmail(),
                changeRequest.getCreator().getFullName(), changeRequest.getCreator().getUserId(), true,
                changeRequest.getChangeBoardRuleSet() != null ? changeRequest.getChangeBoardRuleSet().getRuleSetName() : null, false,
                changeRequest.getChangeRequestType(), false,
                changeRequest.getChangeSpecialist1() != null ? changeRequest.getChangeSpecialist1().getAbbreviation() : null,
                changeRequest.getChangeSpecialist1() != null ? changeRequest.getChangeSpecialist1().getUserId() : null,
                changeRequest.getChangeSpecialist1() != null ? changeRequest.getChangeSpecialist1().getDepartmentName() : null,
                changeRequest.getChangeSpecialist1() != null ? changeRequest.getChangeSpecialist1().getEmail() : null,
                changeRequest.getChangeSpecialist1() != null ? changeRequest.getChangeSpecialist1().getFullName() : null, false,
                changeRequest.getChangeSpecialist2() != null ? changeRequest.getChangeSpecialist2().getAbbreviation() : null,
                changeRequest.getChangeSpecialist2() != null ? changeRequest.getChangeSpecialist2().getUserId() : null,
                changeRequest.getChangeSpecialist2() != null ? changeRequest.getChangeSpecialist2().getDepartmentName() : null,
                changeRequest.getChangeSpecialist2() != null ? changeRequest.getChangeSpecialist2().getEmail() : null,
                changeRequest.getChangeSpecialist2() != null ? changeRequest.getChangeSpecialist2().getFullName() : null, false,
                changeRequest.getExcessAndObsolescenceSavings(), false,
                changeRequest.getFunctionalClusterId(), false, changeRequest.getImplementationPriority(), false, changeRequest.getIsSecure(), false,
                changeRequest.getProblemDescription(), false, changeRequest.getProductId(), false, changeRequest.getProjectId(), false,
                changeRequest.getProposedSolution(), false, changeRequest.getRequirementsForImplementationPlan(), false,
                changeRequest.getRootCause(), false, changeRequest.getStatus(), false, changeRequest.getTitle(), false,
                false, false, false, false, false, false, changeRequestId, nextVal);
        //TODO : Update aud of solution definition, impact analysis, customer impact, preinstall impact, complete business case
        insertaudSolutionDefinitionTable(solutionDefinition, changeRequestId, solutionDefinitionId,nextVal,stmt);
        insertaudCustomerImpactTable( customerImpact,  impactAnalysisId,  customerImpactId, nextVal, stmt);
        insertaudImpactAnalysisTable( impactAnalysis,  changeRequestId,  impactAnalysisId, nextVal, stmt);
        insertaudCompleteBusisnessCaseTable( completeBusinessCase,  impactAnalysisId,  completeBusinessCaseId, nextVal, stmt);
        insertaudPreInstallImpactTable( preinstallImpact,  impactAnalysisId,  preinstallImpactId, nextVal, stmt);
        insertaudScopeTable(scope, changeRequestId, scopeId, nextVal, stmt);

        return changeRequestId;
    }

    private void prepareMyTeam(Long changeRequestId, ChangeRequestMyTeamDetailsAggregate myTeamDetails) {
        Long id = getNextHibernateSequence();
        String stmt = "INSERT INTO public.my_team(" +
                "dtype, id, change_request_id) VALUES (?,?,?)";
        jdbcTemplate.update(stmt, "ChangeRequest", id, changeRequestId);
    }

    private Long insertPreinstallImpactData(Long impactAnalysisId, PreinstallImpact preinstallImpact) {
        Long id = getNextHibernateSequence();
        String stmt = "INSERT INTO public.preinstall_impact(" +
                "id, change_introduces_new11nc, change_introduces_new11nc_details, change_replaces_mentioned_parts," +
                "change_replaces_mentioned_parts_details, impact_on_customer_factory_layout, impact_on_customer_factory_layout_details," +
                "impact_on_facility_flows, impact_on_facility_flows_details, impact_on_preinstall_inter_connect_cables, " +
                "impact_on_preinstall_inter_connect_cables_details, preinstall_impact_result, impact_analysis_id) " +
                "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(stmt, id, preinstallImpact.getChangeIntroducesNew11Nc(), preinstallImpact.getChangeIntroducesNew11NcDetails(),
                preinstallImpact.getChangeReplacesMentionedParts(), preinstallImpact.getChangeReplacesMentionedPartsDetails(),
                preinstallImpact.getImpactOnCustomerFactoryLayout(), preinstallImpact.getImpactOnCustomerFactoryLayoutDetails(),
                preinstallImpact.getImpactOnFacilityFlows(), preinstallImpact.getImpactOnFacilityFlowsDetails(), preinstallImpact.getImpactOnPreinstallInterConnectCables(),
                preinstallImpact.getImpactOnPreinstallInterConnectCablesDetails(), preinstallImpact.getPreinstallImpactResult(),
                impactAnalysisId);
        return id;
    }

    private Long insertCustomerImpactData(Long impactAnalysisId, CustomerImpact customerImpact) {
        Long id = getNextHibernateSequence();
        String stmt = "INSERT INTO public.customer_impact(" +
                "id, change_to_customer_impact_critical_part, change_to_customer_impact_critical_part_details, change_to_process_impacting_customer," +
                "change_to_process_impacting_customer_details, customer_approval, customer_approval_details, customer_communication," +
                "customer_communication_details, customer_impact_result, fco_implementation, fco_implementation_availability," +
                "fco_upgrade_option_csr_implementation_change, fco_upgrade_option_csr_implementation_change_details, impact_on_user_interfaces, " +
                "impact_on_user_interfaces_details, impact_on_wafer_process_environment, impact_on_wafer_process_environment_details," +
                "uptime_improvement, uptime_improvement_availability, uptime_payback, uptime_payback_availability," +
                "impact_analysis_id) " +
                "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(stmt, id, customerImpact.getChangeToCustomerImpactCriticalPart(), customerImpact.getChangeToCustomerImpactCriticalPartDetails(),
                customerImpact.getChangeToProcessImpactingCustomer(), customerImpact.getChangeToProcessImpactingCustomerDetails(),
                customerImpact.getCustomerApproval(), customerImpact.getCustomerApprovalDetails(), customerImpact.getCustomerCommunication(),
                customerImpact.getCustomerCommunicationDetails(), customerImpact.getCustomerImpactResult(),customerImpact.getFcoImplementation(),
                customerImpact.getFcoImplementationAvailability(), customerImpact.getFcoUpgradeOptionCsrImplementationChange(),
                customerImpact.getFcoUpgradeOptionCsrImplementationChangeDetails(), customerImpact.getImpactOnUserInterfaces(),
                customerImpact.getImpactOnUserInterfacesDetails(), customerImpact.getImpactOnWaferProcessEnvironment(),
                customerImpact.getImpactOnWaferProcessEnvironmentDetails(), customerImpact.getUptimeImprovement(), customerImpact.getUptimeImprovementAvailability(),
                customerImpact.getUptimePayback(), customerImpact.getUptimePaybackAvailability(), impactAnalysisId);

        return id;
    }

    private Long insertCompleteBusinessCaseData(Long impactAnalysisId, CompleteBusinessCase completeBusinessCase) {
        Long id = getNextHibernateSequence();
        String stmt = "INSERT INTO public.complete_business_case(" +
                "id, example_savings, customer_opex_savings, customer_uptime_improvement_benefits, cycle_time_recurring_costs, de_investments," +
                "factory_change_order_nonrecurring_costs, factory_investments, farm_out_development_nonrecurring_costs, field_change_order_nonrecurring_costs," +
                "fs_tooling_investments, hardware_commitment, internal_rate_of_return, inventory_replace_nonrecurring_costs, " +
                "inventory_scrap_nonrecurring_costs, labor_recurring_costs, material_recurring_costs,opex_reduction_field_labor_benefits," +
                "opex_reduction_spare_parts_benefits, other_opex_savings_benefits, payback_period, prototype_materials_nonrecurring_costs," +
                "revenues_benefits, risk, risk_in_labor_hours, risk_on_excess_and_obsolescence, risk_on_excess_and_obsolescence_reduction_proposal, " +
                "risk_on_excess_and_obsolescence_reduction_proposal_costs, supplier_investments, supply_chain_adjustments_nonrecurring_costs," +
                "supply_chain_management_investments, system_starts_impacted, systems_in_wip_and_field_impacted," +
                "update_upgrade_product_documentation_nonrecurring_costs, impact_analysis_id) " +
                "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(stmt, id, completeBusinessCase.getexampleSavings(), completeBusinessCase.getCustomerOpexSavings(), completeBusinessCase.getCustomerUptimeImprovementBenefits(),
                completeBusinessCase.getCycleTimeRecurringCosts(), completeBusinessCase.getDeInvestments(), completeBusinessCase.getFactoryChangeOrderNonrecurringCosts(),
                completeBusinessCase.getFactoryInvestments(), completeBusinessCase.getFarmOutDevelopmentNonrecurringCosts(),
                completeBusinessCase.getFieldChangeOrderNonrecurringCosts(), completeBusinessCase.getFsToolingInvestments(),
                completeBusinessCase.getHardwareCommitment(), completeBusinessCase.getInternalRateOfReturn(), completeBusinessCase.getInventoryReplaceNonrecurringCosts(),
                completeBusinessCase.getInventoryScrapNonrecurringCosts(), completeBusinessCase.getLaborRecurringCosts(),
                completeBusinessCase.getMaterialRecurringCosts(), completeBusinessCase.getOpexReductionFieldLaborBenefits(),
                completeBusinessCase.getOpexReductionSparePartsBenefits(), completeBusinessCase.getOtherOpexSavingsBenefits(),
                completeBusinessCase.getPaybackPeriod(), completeBusinessCase.getPrototypeMaterialsNonrecurringCosts(),
                completeBusinessCase.getRevenuesBenefits(), completeBusinessCase.getRisk(), completeBusinessCase.getRiskInLaborHours(),
                completeBusinessCase.getRiskOnExcessAndObsolescence(), completeBusinessCase.getRiskOnExcessAndObsolescenceReductionProposal(),
                completeBusinessCase.getRiskOnExcessAndObsolescenceReductionProposalCosts(), completeBusinessCase.getSupplierInvestments(),
                completeBusinessCase.getSupplyChainAdjustmentsNonrecurringCosts(), completeBusinessCase.getSupplyChainManagementInvestments(),
                completeBusinessCase.getSystemStartsImpacted(), completeBusinessCase.getSystemsInWipAndFieldImpacted(),
                completeBusinessCase.getUpdateUpgradeProductDocumentationNonrecurringCosts(), impactAnalysisId);
        return id;
    }

    private Long insertScopeData(Long changeRequestId, Scope scope) {
        Long id = getNextHibernateSequence();
        String stmt = "INSERT INTO public.scope(" +
                "id, packaging, reusable_packaging, shipping_packaging, storage_packaging, supplier_packaging," +
                "dev_bag_part, fco_upgrade_option_csr, machine_bom_part, preinstall_part, service_part, test_rig_part, " +
                "parts, scope_details, tooling, manufacturing_de_tooling, service_tooling, supplier_tooling, " +
                "change_request_id) " +
                "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(stmt, id, scope.getPackaging(), scope.getPackagingDetail().getReusablePackaging(),
                scope.getPackagingDetail().getShippingPackaging(), scope.getPackagingDetail().getStoragePackaging(),
                scope.getPackagingDetail().getSupplierPackaging(), scope.getPartDetail().getDevBagPart(),
                scope.getPartDetail().getFcoUpgradeOptionCsr(), scope.getPartDetail().getMachineBomPart(),scope.getPartDetail().getPreinstallPart(),
                scope.getPartDetail().getServicePart(), scope.getPartDetail().getTestRigPart(), scope.getParts(), scope.getScopeDetails(),
                scope.getTooling(), scope.getToolingDetail().getManufacturingDeTooling(), scope.getToolingDetail().getServiceTooling(),
                scope.getToolingDetail().getSupplierTooling(), changeRequestId);
        return id;
    }

    private Long insertImpactAnalysisData(Long changeRequestId, ImpactAnalysis impactAnalysis) {
        Long impactAnalysisId = getNextHibernateSequence();
        String stmt = "INSERT INTO public.impact_analysis(" +
                "id, calendar_dependency, cbp_strategies_details, development_labor_hours, impact_on_availability, impact_on_availability_details," +
                "impact_on_cycle_time, impact_on_cycle_time_details, impact_on_existing_parts, impact_on_sequence, " +
                "impact_on_sequence_details, impact_on_system_level_performance, impact_on_system_level_performance_details, implementation_ranges_details, " +
                "investigation_labor_hours, multi_plant_impact, phase_out_spares_tools, phase_out_spares_tools_details, pre_post_conditions," +
                "recovery_time, targeted_valid_configurations, tech_risk_assessment_fmea, tech_risk_assessment_fmea_details, tech_risk_assessment_sra, " +
                "tech_risk_assessment_sra_details, total_instances_affected, upgrade_packages, upgrade_time, change_request_id) " +
                "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(stmt, impactAnalysisId, impactAnalysis.getCalendarDependency(), impactAnalysis.getCbpStrategiesDetails(),
                impactAnalysis.getDevelopmentLaborHours(), impactAnalysis.getImpactOnAvailability(),
                impactAnalysis.getImpactOnAvailabilityDetails(), impactAnalysis.getImpactOnCycleTime(),
                impactAnalysis.getImpactOnCycleTimeDetails(), impactAnalysis.getImpactOnExistingParts(),
                impactAnalysis.getImpactOnSequence(), impactAnalysis.getImpactOnSequenceDetails(), impactAnalysis.getImpactOnSystemLevelPerformance(),
                impactAnalysis.getImpactOnSystemLevelPerformanceDetails(), impactAnalysis.getImplementationRangesDetails(), impactAnalysis.getInvestigationLaborHours(),
                impactAnalysis.getMultiPlantImpact(), impactAnalysis.getPhaseOutSparesTools(), impactAnalysis.getPhaseOutSparesToolsDetails(), impactAnalysis.getPrePostConditions(),
                impactAnalysis.getRecoveryTime(), impactAnalysis.getTargetedValidConfigurations(), impactAnalysis.getTechRiskAssessmentFmea(),
                impactAnalysis.getTechRiskAssessmentFmeaDetails(), impactAnalysis.getTechRiskAssessmentSra(), impactAnalysis.getTechRiskAssessmentSraDetails(),
                impactAnalysis.getTotalInstancesAffected(), impactAnalysis.getUpgradePackages(), impactAnalysis.getUpgradeTime(), changeRequestId);
        int order = 0;
        if (Objects.nonNull(impactAnalysis.getImplementationRanges())) {
            for (String implementationRange : impactAnalysis.getImplementationRanges()) {
                stmt = "INSERT INTO implementation_ranges(" +
                        "id, implementation_ranges, implementation_ranges_order) " +
                        "VALUES (?, ?, ?)";
                jdbcTemplate.update(stmt,
                        impactAnalysisId, implementationRange, order++);
            }
        }
        order = 0;
        if (Objects.nonNull(impactAnalysis.getCbpStrategies())) {
            for (String strategy : impactAnalysis.getCbpStrategies()) {
                stmt = "INSERT INTO cbp_strategies(" +
                        "id, cbp_strategies, cbp_strategies_order) " +
                        "VALUES (?, ?, ?)";
                jdbcTemplate.update(stmt,
                        impactAnalysisId, strategy, order++);
            }
        }

        order = 0;
        if (Objects.nonNull(impactAnalysis.getLiabilityRisks())) {
            for (String liabilityrisk : impactAnalysis.getLiabilityRisks()) {
                stmt = "INSERT INTO liability_risks(" +
                        "id, liability_risks, liability_risks_order) " +
                        "VALUES (?, ?, ?)";
                jdbcTemplate.update(stmt,
                        impactAnalysisId, liabilityrisk, order++);
            }
        }

        order = 0;
        if (Objects.nonNull(impactAnalysis.getFcoTypes())) {
            for (String fcoType : impactAnalysis.getFcoTypes()) {
                stmt = "INSERT INTO fco_types(" +
                        "id, fco_types, fco_types_order) " +
                        "VALUES (?, ?, ?)";
                jdbcTemplate.update(stmt,
                        impactAnalysisId, fcoType, order++);
            }
        }

        return impactAnalysisId;
    }

    private Long insertSolutionDefinitionData(Long changeRequestId, SolutionDefinition solutionDefinition) {
        Long id = getNextHibernateSequence();
        String stmt = "INSERT INTO public.solution_definition(" +
                "id, aligned_with_fo, aligned_with_fo_details, functional_hardware_dependencies, functional_hardware_dependencies_details, functional_software_dependencies," +
                "functional_software_dependencies_details, hardware_software_dependencies_aligned, hardware_software_dependencies_aligned_details, " +
                "products_module_affected, technical_recommendation, test_and_release_strategy, test_and_release_strategy_details, " +
                "change_request_id) " +
                "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(stmt, id, solutionDefinition.getAlignedWithFo(), solutionDefinition.getAlignedWithFoDetails(),
                solutionDefinition.getFunctionalHardwareDependencies(), solutionDefinition.getFunctionalHardwareDependenciesDetails(),
                solutionDefinition.getFunctionalSoftwareDependencies(), solutionDefinition.getFunctionalSoftwareDependenciesDetails(),
                solutionDefinition.getHardwareSoftwareDependenciesAligned(), solutionDefinition.getHardwareSoftwareDependenciesAlignedDetails(),
                solutionDefinition.getProductsModuleAffected(), solutionDefinition.getTechnicalRecommendation(), solutionDefinition.getTestAndReleaseStrategy(),
                solutionDefinition.getTestAndReleaseStrategyDetails(), changeRequestId);

        int productsAffectdOrder = 0;
        if (Objects.nonNull(solutionDefinition.getProductsAffected())) {
            for (String productsAffected : solutionDefinition.getProductsAffected()) {
                stmt = "INSERT INTO products_affected(" +
                        "id, products_affected, products_affected_order) " +
                        "VALUES (?, ?, ?)";
                jdbcTemplate.update(stmt,
                        id, productsAffected, productsAffectdOrder++);
            }
        }

        return id;
    }

    private void insertChangeRequestData(ChangeRequest changeRequest) {
        Long id = getNextHibernateSequence();
        String stmt = "INSERT INTO public.change_request(" +
                "id,created_on, status, title, is_secure, problem_description," +
                "change_specialist1_abbreviation, change_specialist1_department_name, change_specialist1_email, change_specialist1_full_name, change_specialist1_user_id," +
                "change_specialist2_abbreviation, change_specialist2_department_name, change_specialist2_email, change_specialist2_full_name, change_specialist2_user_id, project_id," +
                "product_id, creator_abbreviation, creator_department_name, creator_email, creator_full_name, creator_user_id,change_owner_type,functional_cluster_id) " +
                "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";
        jdbcTemplate.update(stmt, id, changeRequest.getCreatedOn(), changeRequest.getStatus(), changeRequest.getTitle(),
                changeRequest.getIsSecure(), changeRequest.getProblemDescription(),
                changeRequest.getChangeSpecialist1() != null ? changeRequest.getChangeSpecialist1().getAbbreviation() : null,
                changeRequest.getChangeSpecialist1() != null ? changeRequest.getChangeSpecialist1().getUserId() : null,
                changeRequest.getChangeSpecialist1() != null ? changeRequest.getChangeSpecialist1().getDepartmentName() : null,
                changeRequest.getChangeSpecialist1() != null ? changeRequest.getChangeSpecialist1().getEmail() : null,
                changeRequest.getChangeSpecialist1() != null ? changeRequest.getChangeSpecialist1().getFullName() : null,
                changeRequest.getChangeSpecialist2() != null ? changeRequest.getChangeSpecialist2().getAbbreviation() : null,
                changeRequest.getChangeSpecialist2() != null ? changeRequest.getChangeSpecialist2().getUserId() : null,
                changeRequest.getChangeSpecialist2() != null ? changeRequest.getChangeSpecialist2().getDepartmentName() : null,
                changeRequest.getChangeSpecialist2() != null ? changeRequest.getChangeSpecialist2().getEmail() : null,
                changeRequest.getChangeSpecialist2() != null ? changeRequest.getChangeSpecialist2().getFullName() : null,
                changeRequest.getProjectId(), changeRequest.getProductId(), changeRequest.getCreator().getAbbreviation(),
                changeRequest.getCreator().getDepartmentName(),changeRequest.getCreator().getEmail(), changeRequest.getCreator().getFullName(),
                changeRequest.getCreator().getUserId(), changeRequest.getChangeOwnerType(),changeRequest.getFunctionalClusterId()
        );

        int order = 0;
        if (Objects.nonNull(changeRequest.getIssueTypes())) {
            for (String issueType : changeRequest.getIssueTypes()) {
                stmt = "INSERT INTO issue_types(" +
                        "id, issue_types, issue_types_order) " +
                        "VALUES (?, ?, ?)";
                jdbcTemplate.update(stmt,
                        id, issueType, order++);
            }
        }
        int changeBoardsOrder = 0;
        if (Objects.nonNull(changeRequest.getChangeBoards())) {
            for (String changeBoards : changeRequest.getChangeBoards()) {
                stmt = "INSERT INTO change_boards(" +
                        "id, change_boards, change_boards_order) " +
                        "VALUES (?, ?, ?)";
                jdbcTemplate.update(stmt,
                        id, changeBoards, changeBoardsOrder++);
            }
        }

        int reasonsForChangeOrder = 0;
        if (Objects.nonNull(changeRequest.getReasonsForChange())) {
            for (String reasonsForChange : changeRequest.getReasonsForChange()) {
                stmt = "INSERT INTO reasons_for_change(" +
                        "id, reasons_for_change, reasons_for_change_order) " +
                        "VALUES (?, ?, ?)";
                jdbcTemplate.update(stmt,
                        id, reasonsForChange, reasonsForChangeOrder++);
            }
        }

        int ChangeControlBoardsOrder = 0;
        if (Objects.nonNull(changeRequest.getChangeControlBoards())) {
            for (String changeControlBoard : changeRequest.getChangeControlBoards()) {
                stmt = "INSERT INTO change_control_boards(" +
                        "id, change_control_boards, change_control_boards_order) " +
                        "VALUES (?, ?, ?)";
                jdbcTemplate.update(stmt,
                        id, changeControlBoard, ChangeControlBoardsOrder++);
            }
        }

        int dependentChangeRequestIdsOrder = 0;
        if (Objects.nonNull(changeRequest.getDependentChangeRequestIds())) {
            for (String dependentChangeRequestId : changeRequest.getDependentChangeRequestIds()) {
                stmt = "INSERT INTO dependent_change_requests(" +
                        "id, dependent_change_request_ids, dependent_change_request_ids_order) " +
                        "VALUES (?, ?, ?)";
                jdbcTemplate.update(stmt,
                        id, dependentChangeRequestId, dependentChangeRequestIdsOrder++);
            }
        }

        int contextsOrder = 0;
        if (Objects.nonNull(changeRequest.getContexts())) {
            for (ChangeRequestContext changeRequestContext : changeRequest.getContexts()) {
                stmt = "INSERT INTO change_request_contexts(" +
                        "id, context_id, name, status, type,contexts_order) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
                jdbcTemplate.update(stmt,
                        id, changeRequestContext.getContextId(),changeRequestContext.getName(),changeRequestContext.getStatus(), changeRequestContext.getType(), contextsOrder++);
            }
        }
    }

    public Long getNextHibernateSequence() {
        String sql = "select nextval ('hibernate_sequence') as nextval";
        return jdbcTemplate.queryForObject(sql, new Object[]{}, (rs, rowNum) ->
                rs.getLong("nextval"));

    }

    private void insertaudSolutionDefinitionTable(SolutionDefinition solutionDefinition, Long changeRequestId, Long solutionDefinitionId,Long nextVal,String stmt){
        stmt= "insert into aud_solution_definition ( revtype, revend, revend_tstmp, " +
                "aligned_with_fo, aligned_with_fo_mod, aligned_with_fo_details, aligned_with_fo_details_mod, " +
                "functional_hardware_dependencies, functional_hardware_dependencies_mod, functional_hardware_dependencies_details, functional_hardware_dependencies_details_mod, functional_software_dependencies," +
                " functional_software_dependencies_mod, functional_software_dependencies_details, functional_software_dependencies_details_mod, hardware_software_dependencies_aligned, hardware_software_dependencies_aligned_mod, hardware_software_dependencies_aligned_details, " +
                "hardware_software_dependencies_aligned_details_mod, products_module_affected, products_module_affected_mod,technical_recommendation," +
                " technical_recommendation_mod, test_and_release_strategy, test_and_release_strategy_mod, test_and_release_strategy_details," +
                " test_and_release_strategy_details_mod, change_request_id, change_request_mod, products_affected_mod, rev,id) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?,?)";

        jdbcTemplate.update(stmt, 1, null, null,
                solutionDefinition.getAlignedWithFo(), false, solutionDefinition.getAlignedWithFoDetails(), false,
                solutionDefinition.getFunctionalHardwareDependencies(), false, solutionDefinition.getFunctionalHardwareDependenciesDetails(),
                false, solutionDefinition.getFunctionalSoftwareDependencies(),
                false, solutionDefinition.getFunctionalSoftwareDependenciesDetails(), false,
                solutionDefinition.getHardwareSoftwareDependenciesAligned(), false,
                solutionDefinition.getHardwareSoftwareDependenciesAlignedDetails(), false, solutionDefinition.getProductsModuleAffected(), false, solutionDefinition.getTechnicalRecommendation(), false,
                solutionDefinition.getTestAndReleaseStrategy(), false, solutionDefinition.getTestAndReleaseStrategyDetails(), false, changeRequestId, true,
                false, nextVal, solutionDefinitionId);
    }

    private void insertaudImpactAnalysisTable(ImpactAnalysis impactAnalysis, Long changeRequestId, Long impactAnalysisId,Long nextVal,String stmt){
        stmt= "insert into aud_impact_analysis ( revtype, revend, revend_tstmp, " +
                "calendar_dependency, calendar_dependency_mod, cbp_strategies_details, cbp_strategies_details_mod, " +
                "development_labor_hours, development_labor_hours_mod, impact_on_availability, impact_on_availability_mod," +
                "impact_on_availability_details, impact_on_availability_details_mod, impact_on_cycle_time, impact_on_cycle_time_mod, impact_on_cycle_time_details, " +
                " impact_on_cycle_time_details_mod, impact_on_existing_parts, impact_on_existing_parts_mod, impact_on_labor_hours, impact_on_labor_hours_mod, " +
                " impact_on_labor_hours_details, impact_on_labor_hours_details_mod," +
                " impact_on_sequence, impact_on_sequence_mod," +
                " impact_on_sequence_details, impact_on_sequence_details_mod," +
                " impact_on_system_level_performance, impact_on_system_level_performance_mod," +
                " impact_on_system_level_performance_details, impact_on_system_level_performance_details_mod," +
                " implementation_ranges_details, implementation_ranges_details_mod," +
                " investigation_labor_hours, investigation_labor_hours_mod," +
                " multi_plant_impact, multi_plant_impact_mod," +
                " phase_out_spares_tools, phase_out_spares_tools_mod," +
                " phase_out_spares_tools_details, phase_out_spares_tools_details_mod," +
                " pre_post_conditions, pre_post_conditions_mod," +
                " recovery_time, recovery_time_mod," +
                " targeted_valid_configurations, targeted_valid_configurations_mod," +
                " tech_risk_assessment_fmea, tech_risk_assessment_fmea_mod," +
                " tech_risk_assessment_fmea_details, tech_risk_assessment_fmea_details_mod," +
                " tech_risk_assessment_sra, tech_risk_assessment_sra_mod," +
                " tech_risk_assessment_sra_details, tech_risk_assessment_sra_details_mod," +
                " total_instances_affected, total_instances_affected_mod," +
                " upgrade_packages, upgrade_packages_mod," +
                " upgrade_time, upgrade_time_mod," +
                " cbp_strategies_mod, fco_types_mod, implementation_ranges_mod, liability_risks_mod," +
                " change_request_id, change_request_mod, rev, id) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(stmt, 1, null, null,
                impactAnalysis.getCalendarDependency(), false, impactAnalysis.getCbpStrategiesDetails(), false,
                impactAnalysis.getDevelopmentLaborHours(), false, impactAnalysis.getImpactOnAvailability(), false,
                impactAnalysis.getImpactOnAvailabilityDetails(), false, impactAnalysis.getImpactOnCycleTime(),false,
                impactAnalysis.getImpactOnCycleTimeDetails(), false,
                impactAnalysis.getImpactOnExistingParts(), false,
                impactAnalysis.getImpactOnLaborHours(), false,impactAnalysis.getImpactOnLaborHoursDetails(), false,
                impactAnalysis.getImpactOnSequence(), false, impactAnalysis.getImpactOnSequenceDetails(),false,
                impactAnalysis.getImpactOnSystemLevelPerformance(),false,impactAnalysis.getImpactOnSystemLevelPerformanceDetails(),false,
                impactAnalysis.getImplementationRangesDetails(),false,
                impactAnalysis.getInvestigationLaborHours(),false, impactAnalysis.getMultiPlantImpact(),false,
                impactAnalysis.getPhaseOutSparesTools(),false,impactAnalysis.getPhaseOutSparesToolsDetails(),false,
                impactAnalysis.getPrePostConditions(),false,impactAnalysis.getRecoveryTime(),false,
                impactAnalysis.getTargetedValidConfigurations(),false,impactAnalysis.getTechRiskAssessmentFmea(),false,
                impactAnalysis.getTechRiskAssessmentFmeaDetails(),false,
                impactAnalysis.getTechRiskAssessmentSra(),false,impactAnalysis.getTechRiskAssessmentSraDetails(),false,
                impactAnalysis.getTotalInstancesAffected(),false,impactAnalysis.getUpgradePackages(),false,
                impactAnalysis.getUpgradeTime(),false, false, false, false, false,
                changeRequestId, false, nextVal, impactAnalysisId);

    }

    private void insertaudCustomerImpactTable(CustomerImpact customerImpact, Long impactAnalysisId, Long customerImpactId,Long nextVal,String stmt){
        stmt =   "insert into aud_customer_impact ( revtype, revend, revend_tstmp, " +
                "change_to_customer_impact_critical_part, change_to_customer_impact_critical_part_mod, change_to_customer_impact_critical_part_details, change_to_customer_impact_critical_part_details_mod, " +
                "change_to_process_impacting_customer, change_to_process_impacting_customer_mod, change_to_process_impacting_customer_details, change_to_process_impacting_customer_details_mod, customer_approval," +
                " customer_approval_mod, customer_approval_details, customer_approval_details_mod, customer_communication, customer_communication_mod, customer_communication_details, " +
                "customer_communication_details_mod, customer_impact_result, customer_impact_result_mod,fco_implementation," +
                " fco_implementation_mod, fco_implementation_availability, fco_implementation_availability_mod, fco_upgrade_option_csr_implementation_change," +
                " fco_upgrade_option_csr_implementation_change_mod, fco_upgrade_option_csr_implementation_change_details, fco_upgrade_option_csr_implementation_change_details_mod, impact_on_user_interfaces, impact_on_user_interfaces_mod, " +
                " impact_on_user_interfaces_details, impact_on_user_interfaces_details_mod, impact_on_wafer_process_environment, impact_on_wafer_process_environment_mod," +
                " impact_on_wafer_process_environment_details, impact_on_wafer_process_environment_details_mod, uptime_improvement, uptime_improvement_mod," +
                " uptime_improvement_availability, uptime_improvement_availability_mod, uptime_payback," +
                " uptime_payback_mod, uptime_payback_availability, uptime_payback_availability_mod, impact_analysis_id, impact_analysis_mod, rev, id) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

        jdbcTemplate.update(stmt, 1, null, null,
                customerImpact.getChangeToCustomerImpactCriticalPart(), false, customerImpact.getChangeToCustomerImpactCriticalPartDetails(), false,
                customerImpact.getChangeToProcessImpactingCustomer(), false, customerImpact.getChangeToProcessImpactingCustomerDetails(),
                false, customerImpact.getCustomerApproval(), false, customerImpact.getCustomerApprovalDetails(), false,
                customerImpact.getCustomerCommunication(), false,
                customerImpact.getCustomerCommunicationDetails(), false, customerImpact.getCustomerImpactResult(), false, customerImpact.getFcoImplementation(), false,
                customerImpact.getFcoImplementationAvailability(), false, customerImpact.getFcoUpgradeOptionCsrImplementationChange(), false, customerImpact.getFcoUpgradeOptionCsrImplementationChangeDetails(), false,
                customerImpact.getImpactOnUserInterfaces(), false, customerImpact.getImpactOnUserInterfacesDetails(), false, customerImpact.getImpactOnWaferProcessEnvironment(), false,
                customerImpact.getImpactOnWaferProcessEnvironmentDetails(), false, customerImpact.getUptimeImprovement(), false, customerImpact.getUptimeImprovementAvailability(), false,
                customerImpact.getUptimePayback(), false, customerImpact.getUptimePaybackAvailability(), false, impactAnalysisId, false, nextVal, customerImpactId);

    }

    private void insertaudCompleteBusisnessCaseTable(CompleteBusinessCase completeBusinessCase, Long impactAnalysisId, Long completeBusinessCaseId,Long nextVal,String stmt){
        stmt= "insert into aud_complete_business_case ( revtype, revend, revend_tstmp, " +
                "example_savings, example_savings_mod, customer_opex_savings, customer_opex_savings_mod, " +
                "customer_uptime_improvement_benefits, customer_uptime_improvement_benefits_mod, cycle_time_recurring_costs, cycle_time_recurring_costs_mod," +
                "de_investments, de_investments_mod, factory_change_order_nonrecurring_costs, factory_change_order_nonrecurring_costs_mod, factory_investments, " +
                " factory_investments_mod, farm_out_development_nonrecurring_costs, farm_out_development_nonrecurring_costs_mod, field_change_order_nonrecurring_costs, field_change_order_nonrecurring_costs_mod, " +
                " fs_tooling_investments, fs_tooling_investments_mod," +
                " hardware_commitment, hardware_commitment_mod," +
                " internal_rate_of_return, internal_rate_of_return_mod," +
                " inventory_replace_nonrecurring_costs, inventory_replace_nonrecurring_costs_mod," +
                " inventory_scrap_nonrecurring_costs, inventory_scrap_nonrecurring_costs_mod," +
                " labor_recurring_costs, labor_recurring_costs_mod," +
                " material_recurring_costs, material_recurring_costs_mod," +
                " opex_reduction_field_labor_benefits, opex_reduction_field_labor_benefits_mod," +
                " opex_reduction_spare_parts_benefits, opex_reduction_spare_parts_benefits_mod," +
                " other_opex_savings_benefits, other_opex_savings_benefits_mod," +
                " payback_period, payback_period_mod," +
                " prototype_materials_nonrecurring_costs, prototype_materials_nonrecurring_costs_mod," +
                " revenues_benefits, revenues_benefits_mod," +
                " risk, risk_mod," +
                " risk_in_labor_hours, risk_in_labor_hours_mod," +
                " risk_on_excess_and_obsolescence, risk_on_excess_and_obsolescence_mod," +
                " risk_on_excess_and_obsolescence_reduction_proposal, risk_on_excess_and_obsolescence_reduction_proposal_mod," +
                " risk_on_excess_and_obsolescence_reduction_proposal_costs, risk_on_excess_and_obsolescence_reduction_proposal_costs_mod," +
                " supplier_investments, supplier_investments_mod," +
                " supply_chain_adjustments_nonrecurring_costs, supply_chain_adjustments_nonrecurring_costs_mod, supply_chain_management_investments, supply_chain_management_investments_mod," +
                " system_starts_impacted, system_starts_impacted_mod," +
                " systems_in_wip_and_field_impacted, systems_in_wip_and_field_impacted_mod," +
                " update_upgrade_product_documentation_nonrecurring_costs, update_upgrade_product_documentation_nonrecurring_costs_mod," +
                " impact_analysis_id, impact_analysis_mod, rev, id) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?)";

        jdbcTemplate.update(stmt, 1, null, null,
                completeBusinessCase.getexampleSavings(),false,completeBusinessCase.getCustomerOpexSavings(),false,
                completeBusinessCase.getCustomerUptimeImprovementBenefits(),false, completeBusinessCase.getCycleTimeRecurringCosts(),false,
                completeBusinessCase.getDeInvestments(),false,completeBusinessCase.getFactoryChangeOrderNonrecurringCosts(),false,
                completeBusinessCase.getFactoryInvestments(),false,completeBusinessCase.getFarmOutDevelopmentNonrecurringCosts(),false,
                completeBusinessCase.getFieldChangeOrderNonrecurringCosts(),false,completeBusinessCase.getFsToolingInvestments(),false,
                completeBusinessCase.getHardwareCommitment(),false,completeBusinessCase.getInternalRateOfReturn(),false,
                completeBusinessCase.getInventoryReplaceNonrecurringCosts(),false,completeBusinessCase.getInventoryScrapNonrecurringCosts(),false,
                completeBusinessCase.getLaborRecurringCosts(),false,completeBusinessCase.getMaterialRecurringCosts(),false,
                completeBusinessCase.getOpexReductionFieldLaborBenefits(),false,completeBusinessCase.getOpexReductionSparePartsBenefits(),false,
                completeBusinessCase.getOtherOpexSavingsBenefits(),false,completeBusinessCase.getPaybackPeriod(),false,
                completeBusinessCase.getPrototypeMaterialsNonrecurringCosts(),false,
                completeBusinessCase.getRevenuesBenefits(),false,completeBusinessCase.getRisk(),false,completeBusinessCase.getRiskInLaborHours(),false,
                completeBusinessCase.getRiskOnExcessAndObsolescence(),false,completeBusinessCase.getRiskOnExcessAndObsolescenceReductionProposal(),false,
                completeBusinessCase.getRiskOnExcessAndObsolescenceReductionProposalCosts(),false,completeBusinessCase.getSupplierInvestments(),false,
                completeBusinessCase.getSupplyChainAdjustmentsNonrecurringCosts(),false,completeBusinessCase.getSupplyChainManagementInvestments(),false,
                completeBusinessCase.getSystemStartsImpacted(),false,completeBusinessCase.getSystemsInWipAndFieldImpacted(),false,
                completeBusinessCase.getUpdateUpgradeProductDocumentationNonrecurringCosts(),false,
                impactAnalysisId, false, nextVal, completeBusinessCaseId);
    }

    private void insertaudScopeTable(Scope scope, Long changeRequestId, Long scopeId,Long nextVal,String stmt){
        stmt= "insert into aud_scope ( revtype, revend, revend_tstmp, " +
                "bop, bop_mod, packaging, packaging_mod, " +
                "reusable_packaging, shipping_packaging," +
                "storage_packaging, supplier_packaging, packaging_detail_mod," +
                "dev_bag_part, fco_upgrade_option_csr," +
                " machine_bom_part, preinstall_part," +
                "service_part, test_rig_part," +
                "part_detail_mod, parts," +
                "parts_mod, scope_details," +
                "scope_details_mod, tooling," +
                "tooling_mod, manufacturing_de_tooling," +
                "service_tooling, supplier_tooling," +
                "tooling_detail_mod, change_request_id, change_request_mod," +
                "rev, id) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(stmt, 1, null, null,
                scope.getBop(),false,scope.getPackaging(),false,scope.getPackagingDetail().getReusablePackaging(),scope.getPackagingDetail().getShippingPackaging(),
                scope.getPackagingDetail().getStoragePackaging(),scope.getPackagingDetail().getSupplierPackaging(),false,
                scope.getPartDetail().getDevBagPart(),scope.getPartDetail().getFcoUpgradeOptionCsr(),scope.getPartDetail().getMachineBomPart(),
                scope.getPartDetail().getPreinstallPart(),scope.getPartDetail().getServicePart(),scope.getPartDetail().getTestRigPart(),
                false,scope.getParts(),false,scope.getScopeDetails(),false,scope.getTooling(),false,scope.getToolingDetail().getManufacturingDeTooling(),
                scope.getToolingDetail().getServiceTooling(),scope.getToolingDetail().getSupplierTooling(),false,
                changeRequestId, false, nextVal, scopeId);
    }

    private void insertaudPreInstallImpactTable(PreinstallImpact preinstallImpact, Long impactAnalysisId, Long preinstallImpactId,Long nextVal,String stmt){
        stmt= "insert into aud_preinstall_impact ( revtype, revend, revend_tstmp, " +
                "change_introduces_new11nc, change_introduces_new11nc_mod, change_introduces_new11nc_details, change_introduces_new11nc_details_mod, " +
                "change_replaces_mentioned_parts, change_replaces_mentioned_parts_mod, change_replaces_mentioned_parts_details, change_replaces_mentioned_parts_details_mod, impact_on_customer_factory_layout," +
                " impact_on_customer_factory_layout_mod, impact_on_customer_factory_layout_details, impact_on_customer_factory_layout_details_mod, impact_on_facility_flows, impact_on_facility_flows_mod, impact_on_facility_flows_details, " +
                " impact_on_facility_flows_details_mod, impact_on_preinstall_inter_connect_cables, impact_on_preinstall_inter_connect_cables_mod, impact_on_preinstall_inter_connect_cables_details, impact_on_preinstall_inter_connect_cables_details_mod, " +
                " preinstall_impact_result, preinstall_impact_result_mod," +
                " impact_analysis_id, impact_analysis_mod, rev, id) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?)";

        jdbcTemplate.update(stmt, 1, null, null,
                preinstallImpact.getChangeIntroducesNew11Nc(), false, preinstallImpact.getChangeIntroducesNew11NcDetails(), false,
                preinstallImpact.getChangeReplacesMentionedParts(), false, preinstallImpact.getChangeReplacesMentionedPartsDetails(),
                false, preinstallImpact.getImpactOnCustomerFactoryLayout(), false, preinstallImpact.getImpactOnCustomerFactoryLayoutDetails(),false, preinstallImpact.getImpactOnFacilityFlows(), false,
                preinstallImpact.getImpactOnFacilityFlowsDetails(), false,
                preinstallImpact.getImpactOnPreinstallInterConnectCables(), false, preinstallImpact.getImpactOnPreinstallInterConnectCablesDetails(), false, preinstallImpact.getPreinstallImpactResult(), false,
                impactAnalysisId, false, nextVal, preinstallImpactId);
    }

    public Long createChangeRequestCommentAndSetStatus(String dataIdentifier, String properties, CommentStatus status, Long changeRequestId) {
        Long id = createChangeRequestComment(dataIdentifier, properties, changeRequestId);
        assert id != null;
        String stmt = "update comment set status = ? where id = ?";
        int updatedRecords = jdbcTemplate.update(stmt, status.getStatusCode(), id);
        assert updatedRecords == 1;

        return id;
    }

    public Long createChangeRequestComment(String dataIdentifier, String properties, Long changeRequestId) {
        ChangeRequestComment chageRequestComment = null;
        chageRequestComment = EntityPojoFactory.createChangeRequestComment(dataIdentifier, properties);

        String stmt = "INSERT INTO public.comment(" +
                "dtype,created_on, creator_abbreviation, creator_department_name, creator_email, creator_full_name, " +
                "creator_user_id,comment_text,replyto_id,change_request_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(stmt, "ChangeRequest",
                chageRequestComment.getCreatedOn(), chageRequestComment.getCreator().getAbbreviation(), chageRequestComment.getCreator().getDepartmentName(),
                chageRequestComment.getCreator().getEmail(), chageRequestComment.getCreator().getFullName(), chageRequestComment.getCreator().getUserId(),
                chageRequestComment.getCommentText(), null, changeRequestId);
        long changeRequestCommentId = findChangeRequestCommentIdByCommentText(chageRequestComment.getCommentText());
        return changeRequestCommentId;
    }

    public Long findChangeRequestCommentIdByCommentText(String commentText) {
        String sql = "SELECT ID FROM COMMENT WHERE COMMENT_TEXT = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{commentText}, (rs, rowNum) ->
                rs.getLong("id"));

    }

    public Long createChangeRequestDocumentAndSetStatus(String dataIdentifier, String properties,
                                                        Long changeRequestId, DocumentStatus documentStatus) {
        Long id = createChangeRequestDocument(dataIdentifier, properties, changeRequestId);
        assert id != null;
        String stmt = "update document set status = ? where id = ?";
        int updatedRecords = jdbcTemplate.update(stmt, documentStatus.getStatusCode(), id);
        assert updatedRecords == 1;

        return id;
    }

    public Long createChangeRequestDocument(String dataIdentifier, String properties, Long changeRequestId) {
        ChangeRequestDocument changeRequestDocument = null;
        changeRequestDocument = EntityPojoFactory.createChangeRequestDocument(dataIdentifier, properties);

        String stmt = "INSERT INTO public.document(" +
                "dtype,created_on, creator_abbreviation, creator_department_name, creator_email, creator_full_name, " +
                "creator_user_id,description,name,change_request_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(stmt, "ChangeRequestDocument",
                changeRequestDocument.getCreatedOn(), changeRequestDocument.getCreator().getAbbreviation(), changeRequestDocument.getCreator().getDepartmentName(),
                changeRequestDocument.getCreator().getEmail(), changeRequestDocument.getCreator().getFullName(), changeRequestDocument.getCreator().getUserId(),
                changeRequestDocument.getDescription(), changeRequestDocument.getName(), changeRequestId);
        long changeRequestDocumentId = findChangeRequestIdDocumentIdByChangeRequestId(changeRequestId);
        return changeRequestDocumentId;
    }

    public Long findChangeRequestIdDocumentIdByChangeRequestId(Long changeRequestId) {
        String sql = "SELECT ID FROM DOCUMENT WHERE change_request_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{changeRequestId}, (rs, rowNum) ->
                rs.getLong("id"));

    }

    public Long createChangeRequestCommentDocumentAndSetStatus(String dataIdentifier, String properties, CommentStatus status,
                                                                Long changeRequestCommentId, DocumentStatus documentStatus) {
        Long id = createChangeRequestCommentDocument(dataIdentifier, properties, changeRequestCommentId);
        assert id != null;
        String stmt = "update document set status = ? where id = ?";
        int updatedRecords = jdbcTemplate.update(stmt, documentStatus.getStatusCode(), id);
        assert updatedRecords == 1;

        return id;
    }

    public Long createChangeRequestCommentDocument(String dataIdentifier, String properties, Long changeRequestCommentId) {
        ChangeRequestCommentDocument changeRequestCommentDocument = null;
        changeRequestCommentDocument = EntityPojoFactory.createChangeRequestCommentDocument(dataIdentifier, properties);

        String stmt = "INSERT INTO public.document(" +
                "dtype,created_on, creator_abbreviation, creator_department_name, creator_email, creator_full_name, " +
                "creator_user_id,description,name,change_request_comment_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(stmt, "ChangeRequestCommentDocument",
                changeRequestCommentDocument.getCreatedOn(), changeRequestCommentDocument.getCreator().getAbbreviation(), changeRequestCommentDocument.getCreator().getDepartmentName(),
                changeRequestCommentDocument.getCreator().getEmail(), changeRequestCommentDocument.getCreator().getFullName(), changeRequestCommentDocument.getCreator().getUserId(),
                changeRequestCommentDocument.getDescription(), changeRequestCommentDocument.getName(), changeRequestCommentId);
        long changeRequestCommentDocumentId = findChangeRequestCommentDocumentIdByCommentId(changeRequestCommentId);
        return changeRequestCommentDocumentId;
    }

    public Long findChangeRequestCommentDocumentIdByCommentId(Long changeRequestCommentId) {
        String sql = "SELECT ID FROM DOCUMENT WHERE change_request_comment_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{changeRequestCommentId}, (rs, rowNum) ->
                rs.getLong("id"));

    }

    public Long getSolutionDefinitionIdByChangeRequestId(Long changeRequestId) {
        String sql = "SELECT ID FROM solution_definition WHERE change_request_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{changeRequestId}, (rs, rowNum) ->
                rs.getLong("id"));
    }

    public Long getScopeIdByChangeRequestId(Long changeRequestId) {
        String sql = "SELECT ID FROM scope WHERE change_request_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{changeRequestId}, (rs, rowNum) ->
                rs.getLong("id"));
    }

    public Long getImpactAnalysisIdByChangeRequestId(Long changeRequestId) {
        String sql = "SELECT ID FROM impact_analysis WHERE change_request_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{changeRequestId}, (rs, rowNum) ->
                rs.getLong("id"));
    }

    public Long getCustomerImpactIdByChangeRequestId(Long changeRequestId) {
        Long impactAnalysisId = getImpactAnalysisIdByChangeRequestId(changeRequestId);
        String sql = "SELECT ID FROM customer_impact WHERE impact_analysis_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{impactAnalysisId}, (rs, rowNum) ->
                rs.getLong("id"));
    }


    public Long getCompleteBusinessCaseIdByChangeRequestId(Long changeRequestId) {
        Long impactAnalysisId = getImpactAnalysisIdByChangeRequestId(changeRequestId);
        String sql = "SELECT ID FROM complete_business_case WHERE impact_analysis_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{impactAnalysisId}, (rs, rowNum) ->
                rs.getLong("id"));
    }

    public Long getPreinstallImpactIdByChangeRequestId(Long changeRequestId) {
        Long impactAnalysisId = getImpactAnalysisIdByChangeRequestId(changeRequestId);
        String sql = "SELECT ID FROM preinstall_impact WHERE impact_analysis_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{impactAnalysisId}, (rs, rowNum) ->
                rs.getLong("id"));
    }

    public Long findMyTeamIdByChangeRequestId(Long changeRequestId) {
        String sql = "SELECT ID FROM MY_TEAM WHERE change_request_id=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{changeRequestId}, (rs, rowNum) ->
                rs.getLong("id"));
    }

    public Long createMyTeamMember(String dataIdentifier, Long myTeamId, List<String> roles) {
        User myTeamMemberDetails = EntityPojoFactory.createMyTeamMember(dataIdentifier);
        long nextVal = getNextHibernateSequence();

        String stmt = "INSERT INTO public.my_team_member(id, abbreviation, department_name, email, full_name, user_id, myteam_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(stmt, nextVal, myTeamMemberDetails.getAbbreviation(), myTeamMemberDetails.getDepartmentName(),
                myTeamMemberDetails.getEmail(), myTeamMemberDetails.getFullName(), myTeamMemberDetails.getUserId(), myTeamId);
        Long myTeamMemberId = findMyTeamMemberIdByTeamId(myTeamId);
        stmt = "insert into aud_updater (abbreviation, department_name, email, full_name, " +
                "timestamp, user_id, id) values (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(stmt,
                null, null, null, null,
                (new Date()).getTime(), null, nextVal);

        stmt = "INSERT INTO public.aud_my_team_member(id,rev, revtype, revend, revend_tstmp, abbreviation, department_name, email, full_name, user_id, myteam_id)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(stmt, myTeamMemberId, nextVal, 1, null, null, myTeamMemberDetails.getAbbreviation(), myTeamMemberDetails.getDepartmentName(),
                myTeamMemberDetails.getEmail(), myTeamMemberDetails.getFullName(), myTeamMemberDetails.getUserId(), myTeamId);

        int roleOrder = 0;
        if (Objects.nonNull(roles)) {
            for (String role : roles) {
                stmt = "INSERT INTO public.member_role(id, roles, roles_order) VALUES (?, ?, ?)";
                jdbcTemplate.update(stmt, myTeamMemberId, role,roleOrder);
                roleOrder++;
            }
        }

        return myTeamMemberId;

    }

    /*public Long addMemberRole(String role, Long id, Integer order) {
        String stmt = "INSERT INTO public.member_role(id, roles, roles_order) VALUES (?, ?, ?)";
        jdbcTemplate.update(stmt, id, role, order);
        Long memberRoleId = findMemberRoleByTeamId(id);
        return memberRoleId;
    }*/

    public void updateScopeforCia(Long changerequestId,String parts,String tooling,String machineBomPart,
                                  String fcoUpgradeOptionCsr,String servicePart,String preinstallPart,String serviceTooling) {
        Long scopeId = getScopeIdByChangeRequestId(changerequestId);
        String stmt = "UPDATE scope " +
                      "SET parts = " + "'"+parts + "'" +
                           ", tooling = "+ "'"+tooling + "'" +
                           ", machine_bom_part = "+ "'"+ machineBomPart + "'" +
                           ", fco_upgrade_option_csr = "+ "'"+ fcoUpgradeOptionCsr + "'" +
                           ", service_part = "+ "'"+ servicePart + "'" +
                           ", preinstall_part ="+ "'"+ preinstallPart + "'" +
                           ", service_tooling ="+ "'"+ serviceTooling + "'" +
                           " WHERE id = "+scopeId;
        jdbcTemplate.update(stmt);
    }
    public void updateImpactAnalysisforCia(Long changerequestId,String impactOnExistingParts,String impactOnSystemLevelPerformance,String impactOnAvailability) {
        Long impactAnalysisId = getImpactAnalysisIdByChangeRequestId(changerequestId);
        String stmt = "UPDATE impact_analysis " +
                "SET impact_on_existing_parts = " + "'"+ impactOnExistingParts +"'" +
                ", impact_on_system_level_performance = "+ "'"+ impactOnSystemLevelPerformance +"'" +
                ", impact_on_availability = "+ "'"+ impactOnAvailability +"'" +
                " WHERE id = "+impactAnalysisId;
        jdbcTemplate.update(stmt);
    }
    public void updateSolutionDefinitionforCia(Long changerequestId,String functionalSoftwareDependencies) {
        Long solutionDefinitionId = getSolutionDefinitionIdByChangeRequestId(changerequestId);
        String stmt = "UPDATE solution_definition " +
                "SET functional_software_dependencies = " + "'"+ functionalSoftwareDependencies +"'" +
                " WHERE id = "+solutionDefinitionId;
        jdbcTemplate.update(stmt);
    }
    public void updatePreinstallImpactforCia(Long changerequestId,String preinstallImpactResult) {
        Long preinstallImpactId = getPreinstallImpactIdByChangeRequestId(changerequestId);
        String stmt = "UPDATE preinstall_impact " +
                "SET preinstall_impact_result = " + "'"+ preinstallImpactResult + "'" +
                " WHERE id = "+preinstallImpactId;
        jdbcTemplate.update(stmt);
    }
    public void updateIssueTypesforCia(Long changerequestId,String issueTypes) {
              String stmt = "UPDATE issue_types " +
                "SET issue_types = " + "'"+ issueTypes + "'" +
                " WHERE id = "+changerequestId;
        jdbcTemplate.update(stmt);
    }

    public void updateCustomerImpactforCia(Long changerequestId,String impactOnUserInterfaces,String impactOnWaferProcessEnvironment,String changeToCustomerImpactCriticalPart,
                                           String changeToProcessImpactingCustomer,String fcoUpgradeOptionCsrImplementationChange) {
        Long customerImpactId = getCustomerImpactIdByChangeRequestId(changerequestId);
        String stmt = "UPDATE customer_impact " +
                "SET impact_on_user_interfaces = " + "'"+impactOnUserInterfaces + "'"+
                ", impact_on_wafer_process_environment = "+ "'"+impactOnWaferProcessEnvironment +"'"+
                ", change_to_customer_impact_critical_part = "+ "'"+changeToCustomerImpactCriticalPart +"'"+
                ", change_to_process_impacting_customer = "+ "'"+changeToProcessImpactingCustomer +"'"+
                ", fco_upgrade_option_csr_implementation_change = "+ "'"+fcoUpgradeOptionCsrImplementationChange +"'"+
                " WHERE id = "+customerImpactId;
        jdbcTemplate.update(stmt);
    }

    public Long findMyTeamMemberIdByTeamId(Long myTeamid) {
        String sql = "SELECT ID FROM MY_TEAM_MEMBER WHERE MYTEAM_ID=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{myTeamid}, (rs, rowNum) ->
                rs.getLong("id"));
    }

    public Long findMemberRoleByTeamId(Long myTeamid) {
        String sql = "SELECT ID FROM MEMBER_ROLE WHERE ID=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{myTeamid}, (rs, rowNum) ->
                rs.getLong("id"));
    }

}
