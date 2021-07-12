package com.example.mirai.projectname.changerequestservice.migration.controller;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestAggregate;
import com.example.mirai.projectname.changerequestservice.completebusinesscase.model.CompleteBusinessCase;
import com.example.mirai.projectname.changerequestservice.customerimpact.model.CustomerImpact;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
import com.example.mirai.projectname.changerequestservice.migration.model.ChangeRequestAggregateWithComments;
import com.example.mirai.projectname.changerequestservice.migration.model.ChangeRequestMigrate;
import com.example.mirai.projectname.changerequestservice.migration.model.EntitiesPreviousRevision;
import com.example.mirai.projectname.changerequestservice.migration.model.FieldUpdate;
import com.example.mirai.projectname.changerequestservice.migration.service.ChangeRequestMigrateService;
import com.example.mirai.projectname.changerequestservice.preinstallimpact.model.PreinstallImpact;
import com.example.mirai.projectname.changerequestservice.scope.model.Scope;
import com.example.mirai.projectname.changerequestservice.solutiondefinition.model.SolutionDefinition;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/migrate/change-requests")
public class ChangeRequestMigrateController {
    private final ChangeRequestMigrateService changeRequestMigrateService;
    private final ObjectMapper objectMapper;

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ChangeRequest create(@RequestBody JsonNode jsonNode) throws JsonProcessingException {
        ChangeRequestMigrate changeRequestMigrate = objectMapper.treeToValue(jsonNode, ChangeRequestMigrate.class);
        ChangeRequest changeRequest =  changeRequestMigrateService.createChangeRequestMigrateAggregate(changeRequestMigrate);
        changeRequestMigrateService.updateChangeRequestAudit(changeRequest, changeRequestMigrate);
        return (ChangeRequest) changeRequestMigrateService.getEntityById(changeRequest.getId());
    }

    @PatchMapping({"/{changeRequestId}"})
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ChangeRequest mergeChangeRequest(@PathVariable Long changeRequestId, @RequestBody JsonNode jsonNode) throws JsonProcessingException {
        BaseEntityInterface newIns = this.objectMapper.treeToValue(jsonNode.get("new_ins"), ChangeRequest.class);
        newIns.setId(changeRequestId);
        List<String> newInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("new_ins"));
        ChangeRequest changeRequest = (ChangeRequest) changeRequestMigrateService.mergeEntity(newIns, newInsChangedAttributeNames);
        FieldUpdate fieldUpdate = objectMapper.convertValue(jsonNode, FieldUpdate.class);
        Date modifiedOn = fieldUpdate.getModifiedOn();
        User modifiedBy = fieldUpdate.getModifiedBy();
        changeRequestMigrateService.updateAuditForMergeEntity(changeRequest, modifiedBy, modifiedOn, "aud_change_request");
        changeRequestMigrateService.updateAuditForCollection(newInsChangedAttributeNames, modifiedOn, changeRequest);
        return changeRequest;
        //changeRequestMigrateService.updateChangeRequestAudit(changeRequest, auditInformation);
    }

    @PatchMapping(value = "/solution-definition", params = "change-request-id")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public SolutionDefinition mergeSolutionDefinition(@RequestParam(name="change-request-id") Long changeRequestId, @RequestBody JsonNode jsonNode) throws JsonProcessingException {
        BaseEntityInterface newIns = this.objectMapper.treeToValue(jsonNode.get("new_ins"), SolutionDefinition.class);
        Long solutionDefinitionId = changeRequestMigrateService.getSolutionDefinitionIdByChangeRequestId(changeRequestId);
        newIns.setId(solutionDefinitionId);
        List<String> newInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("new_ins"));
        SolutionDefinition solutionDefinition = (SolutionDefinition) changeRequestMigrateService.mergeEntity(newIns, newInsChangedAttributeNames);
        FieldUpdate fieldUpdate = objectMapper.convertValue(jsonNode, FieldUpdate.class);
        Date modifiedOn = fieldUpdate.getModifiedOn();
        User modifiedBy = fieldUpdate.getModifiedBy();
        changeRequestMigrateService.updateAuditForMergeEntity(solutionDefinition, modifiedBy, modifiedOn, "aud_solution_definition");
        changeRequestMigrateService.updateAuditForCollection(newInsChangedAttributeNames, modifiedOn, solutionDefinition);
        return solutionDefinition;
    }

    @PatchMapping(value = "/scope", params = "change-request-id")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public Scope mergeScope(@RequestParam(name="change-request-id") Long changeRequestId, @RequestBody JsonNode jsonNode) throws JsonProcessingException {
        BaseEntityInterface newIns = this.objectMapper.treeToValue(jsonNode.get("new_ins"), Scope.class);
        Long scopeId = changeRequestMigrateService.getScopeIdByChangeRequestId(changeRequestId);
        newIns.setId(scopeId);
        List<String> newInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("new_ins"));
        Scope scope = (Scope) changeRequestMigrateService.mergeEntity(newIns, newInsChangedAttributeNames);
        FieldUpdate fieldUpdate = objectMapper.convertValue(jsonNode, FieldUpdate.class);
        Date modifiedOn = fieldUpdate.getModifiedOn();
        User modifiedBy = fieldUpdate.getModifiedBy();
        changeRequestMigrateService.updateAuditForMergeEntity(scope, modifiedBy, modifiedOn, "aud_scope");
        return scope;
    }

    @PatchMapping(value = "/impact-analysis", params = "change-request-id")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ImpactAnalysis mergeImpactAnalysis(@RequestParam(name="change-request-id") Long changeRequestId, @RequestBody JsonNode jsonNode) throws JsonProcessingException {
        BaseEntityInterface newIns = this.objectMapper.treeToValue(jsonNode.get("new_ins"), ImpactAnalysis.class);
        Long impactAnalysisId = changeRequestMigrateService.getImpactAnalysisIdByChangeRequestId(changeRequestId);
        newIns.setId(impactAnalysisId);
        List<String> newInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("new_ins"));
        ImpactAnalysis impactAnalysis = (ImpactAnalysis) changeRequestMigrateService.mergeEntity(newIns, newInsChangedAttributeNames);
        FieldUpdate fieldUpdate = objectMapper.convertValue(jsonNode, FieldUpdate.class);
        Date modifiedOn = fieldUpdate.getModifiedOn();
        User modifiedBy = fieldUpdate.getModifiedBy();
        changeRequestMigrateService.updateAuditForMergeEntity(impactAnalysis, modifiedBy, modifiedOn, "aud_impact_analysis");
        changeRequestMigrateService.updateAuditForCollection(newInsChangedAttributeNames, modifiedOn, impactAnalysis);
        return impactAnalysis;
    }

    @PatchMapping(value = "/customer-impact", params = "change-request-id")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerImpact mergeCustomerImpact(@RequestParam(name="change-request-id") Long changeRequestId, @RequestBody JsonNode jsonNode) throws JsonProcessingException {
        BaseEntityInterface newIns = this.objectMapper.treeToValue(jsonNode.get("new_ins"), CustomerImpact.class);
        Long customerImpactId = changeRequestMigrateService.getCustomerImpactIdByChangeRequestId(changeRequestId);
        newIns.setId(customerImpactId);
        List<String> newInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("new_ins"));
        CustomerImpact customerImpact = (CustomerImpact) changeRequestMigrateService.mergeEntity(newIns, newInsChangedAttributeNames);
        FieldUpdate fieldUpdate = objectMapper.convertValue(jsonNode, FieldUpdate.class);
        Date modifiedOn = fieldUpdate.getModifiedOn();
        User modifiedBy = fieldUpdate.getModifiedBy();
        changeRequestMigrateService.updateAuditForMergeEntity(customerImpact, modifiedBy, modifiedOn, "aud_customer_impact");
        return customerImpact;
    }

    @PatchMapping(value = "/preinstall-impact", params = "change-request-id")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public PreinstallImpact mergePreinstallImpact(@RequestParam(name="change-request-id") Long changeRequestId, @RequestBody JsonNode jsonNode) throws JsonProcessingException {
        BaseEntityInterface newIns = this.objectMapper.treeToValue(jsonNode.get("new_ins"), PreinstallImpact.class);
        Long preinstallImpactId = changeRequestMigrateService.getPreinstallImpactIdByChangeRequestId(changeRequestId);
        newIns.setId(preinstallImpactId);
        List<String> newInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("new_ins"));
        PreinstallImpact preinstallImpact = (PreinstallImpact) changeRequestMigrateService.mergeEntity(newIns, newInsChangedAttributeNames);
        FieldUpdate fieldUpdate = objectMapper.convertValue(jsonNode, FieldUpdate.class);
        Date modifiedOn = fieldUpdate.getModifiedOn();
        User modifiedBy = fieldUpdate.getModifiedBy();
        changeRequestMigrateService.updateAuditForMergeEntity(preinstallImpact, modifiedBy, modifiedOn, "aud_preinstall_impact");
        return preinstallImpact;
    }

    @PatchMapping(value = "/complete-business-case", params = "change-request-id")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public CompleteBusinessCase mergeCompleteBusinessCase(@RequestParam(name="change-request-id") Long changeRequestId, @RequestBody JsonNode jsonNode) throws JsonProcessingException {
        BaseEntityInterface newIns = this.objectMapper.treeToValue(jsonNode.get("new_ins"), CompleteBusinessCase.class);
        Long completeBusinessCaseId = changeRequestMigrateService.getCompleteBusinessCaseIdByChangeRequestId(changeRequestId);
        newIns.setId(completeBusinessCaseId);
        List<String> newInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("new_ins"));
        CompleteBusinessCase completeBusinessCase = (CompleteBusinessCase) changeRequestMigrateService.mergeEntity(newIns, newInsChangedAttributeNames);
        FieldUpdate fieldUpdate = objectMapper.convertValue(jsonNode, FieldUpdate.class);
        Date modifiedOn = fieldUpdate.getModifiedOn();
        User modifiedBy = fieldUpdate.getModifiedBy();
        changeRequestMigrateService.updateAuditForMergeEntity(completeBusinessCase, modifiedBy, modifiedOn, "aud_complete_business_case");
        return completeBusinessCase;
    }


    @PutMapping(value = "/{changeRequestId}", params = "view=aggregate")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ChangeRequestAggregateWithComments updateChangeRequestAggregateAndCreateComments(@PathVariable Long changeRequestId, @RequestBody JsonNode jsonNode) throws JsonProcessingException {
        ChangeRequestAggregateWithComments changeRequestAggregate = objectMapper.treeToValue(jsonNode, ChangeRequestAggregateWithComments.class);
        log.info("updating change request aggregate of " + changeRequestAggregate.getChangeRequestAggregate().getDescription().getId());
        EntitiesPreviousRevision entitiesPreviousRevision = changeRequestMigrateService.getEntitiesPreviousRevisions(changeRequestAggregate.getChangeRequestAggregate());
        ChangeRequestAggregateWithComments changeRequestAggregateWithComments = changeRequestMigrateService.updateChangeRequestAggregateAndCreateComments(changeRequestAggregate, changeRequestId);
        changeRequestMigrateService.updateAuditOfEntities(changeRequestAggregate, entitiesPreviousRevision);
        return changeRequestAggregateWithComments;
    }

    @DeleteMapping(value = "/{changeRequestId}")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public void deleteChangeRequest(@PathVariable Long changeRequestId) {
        ChangeRequestAggregate deletedChangeRequestAggregate = changeRequestMigrateService.deleteChangeRequest(changeRequestId);
        changeRequestMigrateService.deleteAuditEntries(deletedChangeRequestAggregate);
    }

}
