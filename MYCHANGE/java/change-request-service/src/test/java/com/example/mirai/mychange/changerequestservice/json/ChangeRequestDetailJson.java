package com.example.mirai.projectname.changerequestservice.json;

import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.RuleSet;
import com.example.mirai.projectname.changerequestservice.scope.model.Scope;
import com.example.mirai.projectname.changerequestservice.scope.model.dto.PackagingDetail;
import com.example.mirai.projectname.changerequestservice.scope.model.dto.PartDetail;
import com.example.mirai.projectname.changerequestservice.scope.model.dto.ToolingDetail;
import com.example.mirai.projectname.changerequestservice.solutiondefinition.model.SolutionDefinition;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ChangeRequestDetailJson extends Content {
    public ChangeRequestDetailJson(String content) {
        super(content);
    }

    public Long getId() {
        return Long.valueOf("" + documentContext.read("id"));
    }

    public String getTitle() {
        return documentContext.read("title");
    }

    public Integer getStatus() {
        return documentContext.read("status");
    }

    public Boolean getIsSecure() {
        return documentContext.read("is_secure");
    }
    public Object getChangeSpecialist2Node() {
        return documentContext.read("change_specialist2");
    }
    public User getChangeSpecialist2() {
        User changeSpecialist2 = new User();
        if (Objects.nonNull(getChangeSpecialist2Node())) {
            changeSpecialist2.setUserId(getChangeSpecialist2UserId());
            changeSpecialist2.setFullName(getChangeSpecialist2FullName());
            changeSpecialist2.setEmail(getChangeSpecialist2Email());
            changeSpecialist2.setDepartmentName(getChangeSpecialist2DepartmentName());
            changeSpecialist2.setAbbreviation(getChangeSpecialist2Abbreviation());
            return changeSpecialist2;
        }
        return null;
    }

    public Date getCreatedOn() {
        return convertStringToDate(documentContext.read("created_on"));
    }
    public Object getChangeSpecialist1Node() {
        return documentContext.read("change_specialist1");
    }
    public User getChangeSpecialist1() {
        User executor = new User();
        if (Objects.nonNull(getChangeSpecialist1Node())) {
            executor.setUserId(getChangeSpecialist1UserId());
            executor.setFullName(getChangeSpecialist1FullName());
            executor.setEmail(getChangeSpecialist1Email());
            executor.setDepartmentName(getChangeSpecialist1DepartmentName());
            executor.setAbbreviation(getChangeSpecialist1Abbreviation());
            return executor;
        }
        return null;
    }

    public ChangeRequest getChangeRequest() {
        ChangeRequest changeRequest = new ChangeRequest();
        changeRequest.setId(getId());
        changeRequest.setTitle(getTitle());
        changeRequest.setIsSecure(getIsSecure());
        changeRequest.setImplementationPriority(getImplementationPriority());
        changeRequest.setIssueTypes(getIssueTypes());
        changeRequest.setDependentChangeRequestIds(getDependentChangeRequestIds());
        changeRequest.setCreatedOn(getCreatedOn());
        User changeSpecialist1 = new User();
        if (Objects.nonNull(getChangeSpecialist1())) {
            changeSpecialist1.setUserId(getChangeSpecialist1UserId());
            changeSpecialist1.setFullName(getChangeSpecialist1FullName());
            changeSpecialist1.setEmail(getChangeSpecialist1Email());
            changeSpecialist1.setDepartmentName(getChangeSpecialist1DepartmentName());
            changeSpecialist1.setAbbreviation(getChangeSpecialist1Abbreviation());
        }
        changeRequest.setChangeSpecialist1(changeSpecialist1);
        User changeSpecialist2 = new User();
        if (Objects.nonNull(getChangeSpecialist2())) {
            changeSpecialist2.setUserId(getChangeSpecialist2UserId());
            changeSpecialist2.setFullName(getChangeSpecialist2FullName());
            changeSpecialist2.setEmail(getChangeSpecialist2Email());
            changeSpecialist2.setDepartmentName(getChangeSpecialist2DepartmentName());
            changeSpecialist2.setAbbreviation(getChangeSpecialist2Abbreviation());
        }
        changeRequest.setChangeSpecialist2(changeSpecialist2);
        changeRequest.setExcessAndObsolescenceSavings(getExcessAndObsolescenceSavings());
        changeRequest.setReasonsForChange(getReasonsForChange());
        changeRequest.setRequirementsForImplementationPlan(getRequirementsForImplementationPlan());
        changeRequest.setChangeRequestType(getChangeRequestType());
        changeRequest.setChangeControlBoards(getChangeControlBoards());
        changeRequest.setChangeBoards(getChangeBoards());
        changeRequest.setChangeBoardRuleSet(getChangeBoardRuleSet());
        changeRequest.setProposedSolution(getProposedSolution());
        changeRequest.setRootCause(getRootCause());
        changeRequest.setProblemDescription(getProblemDescription());
        changeRequest.setFunctionalClusterId(getFunctionalClusterId());
        changeRequest.setProductId(getProductId());
        changeRequest.setProjectId(getProjectId());
        changeRequest.setAnalysisPriority(getAnalysisPriority());
        return changeRequest;
    }

    private List<String> getReasonsForChange() {
        return documentContext.read("reasons_for_change");
    }

    private String getRequirementsForImplementationPlan() {
        return documentContext.read("requirements_for_implementation_plan");
    }

    private String getChangeRequestType() {
        return documentContext.read("change_request_type");
    }

    private List<String> getChangeControlBoards() {
        return documentContext.read("change_control_boards");
    }

    private List<String> getChangeBoards() {
        return documentContext.read("change_boards");
    }

    private RuleSet getChangeBoardRuleSet() {
        return documentContext.read("change_board_rule_set");
    }

    private String getProposedSolution() {
        return documentContext.read("proposed_solution");
    }

    private String getRootCause() {
        return documentContext.read("root_cause");
    }

    private String getProblemDescription() {
        return documentContext.read("problem_description");
    }

    private String getFunctionalClusterId() {
        return documentContext.read("functional_cluster_id");
    }

    private String getProductId() {
        return documentContext.read("product_id");
    }

    private String getProjectId() {
        return documentContext.read("project_id");
    }

    private Integer getAnalysisPriority() {
        return documentContext.read("analysis_priority");
    }

    private Float getExcessAndObsolescenceSavings() {
        return documentContext.read("excess_and_obsolescence_savings");
    }

    private List<String> getDependentChangeRequestIds() {
        return documentContext.read("dependent_change_request_ids");
    }

    private List<String> getIssueTypes() {
        return documentContext.read("issue_types");
    }

    private Integer getImplementationPriority() {
        return documentContext.read("implementation_priority");
    }

    public String getChangeSpecialist1UserId() {
        return documentContext.read("$.change_specialist1.user_id");
    }

    public String getChangeSpecialist1FullName() {
        return documentContext.read("$.change_specialist1.full_name");
    }

    public String getChangeSpecialist1Email() {
        return documentContext.read("$.change_specialist1.email");
    }

    public String getChangeSpecialist1DepartmentName() {
        return documentContext.read("$.change_specialist1.department_name");
    }

    public String getChangeSpecialist1Abbreviation() {
        return documentContext.read("$.change_specialist1.abbreviation");
    }

    public String getChangeSpecialist2UserId() {
        return documentContext.read("$.change_specialist2.user_id");
    }

    public String getChangeSpecialist2FullName() {
        return documentContext.read("$.change_specialist2.full_name");
    }

    public String getChangeSpecialist2Email() {
        return documentContext.read("$.change_specialist2.email");
    }

    public String getChangeSpecialist2DepartmentName() {
        return documentContext.read("$.change_specialist2.department_name");
    }

    public String getChangeSpecialist2Abbreviation() {
        return documentContext.read("$.change_specialist2.abbreviation");
    }

    //scope
    public Scope getScope() {
        Scope scope = new Scope();
        scope.setId(getId());
        scope.setPackaging(getPackaging());
        scope.setParts(getParts());
        scope.setPackagingDetail(getPackagingDetail());
        scope.setTooling(getTooling());
        scope.setToolingDetail(getToolingDetail());
        scope.setPartDetail(getPartDetail());
        scope.setScopeDetails(getScopeDetails());
        return scope;
    }
    public Long getScopeId() {
        return Long.valueOf("" + documentContext.read("$.scope.id"));
    }

    public String getScopeDetails() {
        return documentContext.read("$.scope.scopeDetails");
    }

    public String getParts() {
        return documentContext.read("$.scope.parts");
    }

    public String getPackaging() {
        return documentContext.read("$.scope.packaging");
    }

    public Object getPartDetailNode() {
        return documentContext.read("$.scope.part_detail");
    }

    public PartDetail getPartDetail() {
        PartDetail partDetail = new PartDetail();
        if (Objects.nonNull(getPartDetailNode())) {
            partDetail.setDevBagPart(getDevBagPart());
            partDetail.setFcoUpgradeOptionCsr(getFcoUpgradeOptionCsr());
            partDetail.setMachineBomPart(getMachineBomPart());
            partDetail.setPreinstallPart(getPreinstallPart());
            partDetail.setServicePart(getServicePart());
            partDetail.setTestRigPart(getTestRigPart());
            return partDetail;
        }
        return null;
    }

    private String getTestRigPart() {
        return documentContext.read("$.scope.part_detail.test_rig_part");
    }

    private String getServicePart() {
        return documentContext.read("$.scope.part_detail.service_part");
    }

    private String getPreinstallPart() {
        return documentContext.read("$.scope.part_detail.preinstall_part");
    }

    private String getMachineBomPart() {
        return documentContext.read("$.scope.part_detail.machine_bom_part");
    }

    private String getFcoUpgradeOptionCsr() {
        return documentContext.read("$.scope.part_detail.fco_upgrade_option_csr");
    }

    private String getDevBagPart() {
        return documentContext.read("$.scope.part_detail.dev_bag_part");
    }

    public String getTooling() {
        return documentContext.read("$.scope.tooling");
    }

    public Object getToolingDetailNode() {
        return documentContext.read("$.scope.tooling_detail");
    }

    public ToolingDetail getToolingDetail() {
        ToolingDetail toolingDetail = new ToolingDetail();
        if (Objects.nonNull(getToolingDetailNode())) {
            toolingDetail.setManufacturingDeTooling(getManufacturingDeTooling());
            toolingDetail.setServiceTooling(getServiceTooling());
            toolingDetail.setSupplierTooling(getSupplierTooling());
            return toolingDetail;
        }
        return null;
    }

    private String getSupplierTooling() {
        return documentContext.read("$.scope.tooling_detail.supplier_tooling");
    }

    private String getServiceTooling() {
        return documentContext.read("$.scope.tooling_detail.service_tooling");
    }

    private String getManufacturingDeTooling() {
        return documentContext.read("$.scope.tooling_detail.manufacturing_de_tooling");
    }

    public Object getPackagingDetailNode() {
        return documentContext.read("$.scope.packaging_detail");
    }

    public PackagingDetail getPackagingDetail() {
        PackagingDetail packagingDetail = new PackagingDetail();
        if (Objects.nonNull(getPackagingDetailNode())) {
            packagingDetail.setReusablePackaging(getReusablePackaging());
            packagingDetail.setShippingPackaging(getShippingPackaging());
            packagingDetail.setStoragePackaging(getStoragePackaging());
            packagingDetail.setSupplierPackaging(getSupplierPackaging());
            return packagingDetail;
        }
        return null;
    }

    private String getSupplierPackaging() {
        return documentContext.read("$.scope.packaging_detail.reusable_packaging");
    }

    private String getStoragePackaging() {
        return documentContext.read("$.scope.packaging_detail.shipping_packaging");
    }

    private String getShippingPackaging() {
        return documentContext.read("$.scope.packaging_detail.storage_packaging");
    }

    private String getReusablePackaging() {
        return documentContext.read("$.scope.packaging_detail.supplier_packaging");
    }


    // solution definition

    public SolutionDefinition getSolutionDefinition() {
        SolutionDefinition solutionDefinition = new SolutionDefinition();
        solutionDefinition.setAlignedWithFo(getAlignedWithFO());
        solutionDefinition.setAlignedWithFoDetails(getAlignedWithFoDetails());
        solutionDefinition.setFunctionalHardwareDependencies(getFunctionalHardwareDependencies());
        solutionDefinition.setFunctionalHardwareDependenciesDetails(getFunctionalHardwareDependenciesDetails());
        solutionDefinition.setFunctionalSoftwareDependencies(getFunctionalSoftwareDependencies());
        solutionDefinition.setFunctionalSoftwareDependenciesDetails(getFunctionalSoftwareDependenciesDetails());
        solutionDefinition.setHardwareSoftwareDependenciesAligned(getHardwareSoftwareDependenciesAligned());
        solutionDefinition.setHardwareSoftwareDependenciesAlignedDetails(getHardwareSoftwareDependenciesAlignedDetails());
        solutionDefinition.setProductsAffected(getProductsAffected());
        solutionDefinition.setProductsModuleAffected(getProductsModuleAffected());
        solutionDefinition.setTechnicalRecommendation(getTechnicalRecommendation());
        solutionDefinition.setTestAndReleaseStrategy(getTestAndReleaseStrategy());
        solutionDefinition.setTestAndReleaseStrategyDetails(getTestAndReleaseStrategyDetails());
        return solutionDefinition;
    }
    public Long getSolutionDefinitionId() {
        return Long.valueOf("" + documentContext.read("$.solution_definition.id"));
    }

    private String getFunctionalSoftwareDependencies() {
        return documentContext.read("$.solution_definition.functional_software_dependencies");
    }

    private String getFunctionalSoftwareDependenciesDetails() {
        return documentContext.read("$.solution_definition.functional_software_dependencies_details");
    }

    private String getFunctionalHardwareDependencies() {
        return documentContext.read("$.solution_definition.functional_hardware_dependencies");
    }

    private String getFunctionalHardwareDependenciesDetails() {
        return documentContext.read("$.solution_definition.functional_hardware_dependencies_details");
    }

    private String getHardwareSoftwareDependenciesAligned() {
        return documentContext.read("$.solution_definition.hardware_software_dependencies_aligned");
    }

    private String getHardwareSoftwareDependenciesAlignedDetails() {
        return documentContext.read("$.solution_definition.hardware_software_dependencies_aligned_details");
    }

    private String getTestAndReleaseStrategy() {
        return documentContext.read("$.solution_definition.test_and_release_strategy");
    }

    private String getTestAndReleaseStrategyDetails() {
        return documentContext.read("$.solution_definition.test_and_release_strategy_details");
    }

    private List<String> getProductsAffected() {
        return documentContext.read("$.solution_definition.products_affected");
    }

    private String getProductsModuleAffected() {
        return documentContext.read("$.solution_definition.products_module_affected");
    }

    private String getAlignedWithFO() {
        return documentContext.read("$.solution_definition.aligned_with_fo");
    }

    private String getAlignedWithFoDetails() {
        return documentContext.read("$.solution_definition.aligned_with_fodetails");
    }

    private String getTechnicalRecommendation() {
        return documentContext.read("$.solution_definition.technical_recommendation");
    }
}
