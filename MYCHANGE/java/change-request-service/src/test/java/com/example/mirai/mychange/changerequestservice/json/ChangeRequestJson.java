package com.example.mirai.projectname.changerequestservice.json;


import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.RuleSet;
import com.example.mirai.libraries.core.model.User;

import java.util.Date;
import java.util.List;

public class ChangeRequestJson extends Content {
    public ChangeRequestJson(String content) {
        super(content);
    }

    public Long getId() {
        return Long.valueOf("" + documentContext.read("id"));
    }

    public String getTitle() {
        return documentContext.read("title");
    }

    public Integer getStatus() {
        return documentContext.read("$.status");
    }

    public Boolean getIsSecure() {
        return documentContext.read("is_secure");
    }

    public User getChangeSpecialist2() {
        User changeSpecialist2 = new User();
        changeSpecialist2.setUserId(getChangeSpecialist2UserId());
        changeSpecialist2.setFullName(getChangeSpecialist2FullName());
        changeSpecialist2.setEmail(getChangeSpecialist2Email());
        changeSpecialist2.setDepartmentName(getChangeSpecialist2DepartmentName());
        changeSpecialist2.setAbbreviation(getChangeSpecialist2Abbreviation());
        return changeSpecialist2;
    }

    public Date getCreatedOn() {
        return convertStringToDate(documentContext.read("created_on"));
    }

    public User getChangeSpecialist1() {
        User executor = new User();
        executor.setUserId(getChangeSpecialist1UserId());
        executor.setFullName(getChangeSpecialist1FullName());
        executor.setEmail(getChangeSpecialist1Email());
        executor.setDepartmentName(getChangeSpecialist1DepartmentName());
        executor.setAbbreviation(getChangeSpecialist1Abbreviation());
        return executor;
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
        changeSpecialist1.setUserId(getChangeSpecialist1UserId());
        changeSpecialist1.setFullName(getChangeSpecialist1FullName());
        changeSpecialist1.setEmail(getChangeSpecialist1Email());
        changeSpecialist1.setDepartmentName(getChangeSpecialist1DepartmentName());
        changeSpecialist1.setAbbreviation(getChangeSpecialist1Abbreviation());
        changeRequest.setChangeSpecialist1(changeSpecialist1);
        User changeSpecialist2 = new User();
        changeSpecialist2.setUserId(getChangeSpecialist2UserId());
        changeSpecialist2.setFullName(getChangeSpecialist2FullName());
        changeSpecialist2.setEmail(getChangeSpecialist2Email());
        changeSpecialist2.setDepartmentName(getChangeSpecialist2DepartmentName());
        changeSpecialist2.setAbbreviation(getChangeSpecialist2Abbreviation());
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

}
