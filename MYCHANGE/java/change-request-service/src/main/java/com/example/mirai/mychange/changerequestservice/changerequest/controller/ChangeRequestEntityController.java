package com.example.mirai.projectname.changerequestservice.changerequest.controller;

import com.example.mirai.libraries.core.annotation.SecurePropertyRead;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.entity.controller.EntityController;
import com.example.mirai.libraries.entity.model.Id;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
/*import com.example.mirai.libraries.scm.scia.model.Scia;
import com.example.mirai.libraries.scm.scia.model.SciaSummary;*/
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestAggregate;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestDetail;
import com.example.mirai.projectname.changerequestservice.changerequest.model.dto.ChangeRequestSummaryScia;
import com.example.mirai.projectname.changerequestservice.changerequest.model.dto.ImportData;
import com.example.mirai.projectname.changerequestservice.changerequest.scheduler.ChangeRequestSchedulerReconciliation;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestService;
import com.example.mirai.projectname.changerequestservice.core.component.EntityResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("{entityType:change-requests}")
public class ChangeRequestEntityController extends EntityController {

    @Resource
    ChangeRequestEntityController self;

    private ChangeRequestService changeRequestService;
    private ObjectMapper objectMapper;
    private ChangeRequestSchedulerReconciliation changeRequestSchedulerReconciliation;

    ChangeRequestEntityController(ChangeRequestService changeRequestService, EntityResolver entityResolver, ObjectMapper objectMapper, ChangeRequestSchedulerReconciliation changeRequestSchedulerReconciliation) {
        super(objectMapper, changeRequestService, entityResolver);
        this.changeRequestService = changeRequestService;
        this.objectMapper = objectMapper;
        this.changeRequestSchedulerReconciliation = changeRequestSchedulerReconciliation;
    }

    @Override
    public Class<? extends BaseEntityInterface> getEntityClass() {
        return ChangeRequest.class;
    }


    @PatchMapping(value = "/{id}", params = "case-action=unlink-air")
    @ResponseStatus(HttpStatus.OK)
    public ChangeRequest unlinkAir(@RequestBody JsonNode request, @PathVariable Long id) {
        ImportData.Source unlinkImportData = objectMapper.convertValue(request, ImportData.Source.class);
        return changeRequestService.unlinkAir(id, unlinkImportData);
    }

    @PatchMapping(value = "/{id}", params = "case-action=unlink-pbs")
    @ResponseStatus(HttpStatus.OK)
    public ChangeRequest unlinkPbs(@RequestBody JsonNode request, @PathVariable Long id) {
        ImportData.Source unlinkImportData = objectMapper.convertValue(request, ImportData.Source.class);
        return changeRequestService.unlinkPbs(id, unlinkImportData);
    }

    @PutMapping(value = "/{id}", params = "case-action=link-air")
    @ResponseStatus(HttpStatus.OK)
    public List<ImportData.Response> linkAir(@RequestBody JsonNode request, @PathVariable Long id) {
        ImportData importData = objectMapper.convertValue(request, ImportData.class);
        return changeRequestService.linkAirItems(id, importData);
    }

    @PutMapping(value = "/{id}", params = "case-action=link-pbs")
    @ResponseStatus(HttpStatus.OK)
    public List<ImportData.Response> linkPbs(@RequestBody JsonNode request, @PathVariable Long id) {
        ImportData importData = objectMapper.convertValue(request, ImportData.class);
        return changeRequestService.linkPbsItems(id, importData);
    }

    @PatchMapping(value = "/{id}", params = "case-action=unlink-CR")
    @ResponseStatus(HttpStatus.OK)
    public ChangeRequest unlinkChangeRequest(@RequestBody JsonNode request, @PathVariable Long id) {
        Id unlinkId = objectMapper.convertValue(request, Id.class);
        return changeRequestService.unlinkChangeRequest(id, unlinkId);
    }


    @GetMapping(value = "/{id}" , params = "view=change-request-detail")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ChangeRequestDetail getAggregateDetail(@PathVariable Long id) {
        ChangeRequestAggregate changeRequestAggregate = self.getAggregate(id);
        return changeRequestService.getChangeRequestDetail(changeRequestAggregate);
    }

    @SecurePropertyRead
    public ChangeRequestAggregate getAggregate(Long id) {
        return changeRequestService.getChangeRequestAggregate(id);
    }

    @GetMapping(params ={"agenda-item-id"} )
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ChangeRequest getChangeRequestDetailsByAgendaItemId(@RequestParam(name = "agenda-item-id") String agendaItemId) {
        return changeRequestService.getChangeRequestDetailsByAgendaItemId(agendaItemId);
    }

    @PatchMapping({"/{id}"})
    @ResponseStatus(HttpStatus.OK)
    @Override
    public BaseEntityInterface merge(@PathVariable Long id, @RequestBody JsonNode jsonNode) throws JsonProcessingException {
        BaseEntityInterface oldIns = this.objectMapper.treeToValue(jsonNode.get("oldIns"), this.getEntityClass());
        BaseEntityInterface newIns = this.objectMapper.treeToValue(jsonNode.get("newIns"), this.getEntityClass());
        oldIns.setId(id);
        newIns.setId(id);
        List<String> oldInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("oldIns"));
        List<String> newInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("newIns"));
        return this.changeRequestService.mergeEntity(newIns, oldIns, oldInsChangedAttributeNames, newInsChangedAttributeNames);
    }

    @PatchMapping(value = "/{id}", params = "is-system-account=true")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole(@mychangeChangeRequestServiceConfigurationProperties.getTibcoRoles())")
    public BaseEntityInterface mergeEntityBySystemUser(@PathVariable Long id, @RequestBody JsonNode jsonNode) throws JsonProcessingException {
        BaseEntityInterface oldIns = this.objectMapper.treeToValue(jsonNode.get("oldIns"), this.getEntityClass());
        BaseEntityInterface newIns = this.objectMapper.treeToValue(jsonNode.get("newIns"), this.getEntityClass());
        oldIns.setId(id);
        newIns.setId(id);
        List<String> oldInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("oldIns"));
        List<String> newInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("newIns"));
        return this.changeRequestService.mergeEntityBySystemUser(newIns, oldIns, oldInsChangedAttributeNames, newInsChangedAttributeNames);
    }

    @GetMapping(value = "/{id}", params = {"view=scm"} )
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ChangeRequestSummaryScia getChangeRequestSummaryForScm(@PathVariable Long id) {
        return changeRequestService.getChangeRequestSummaryForScm(id);
    }

    /*@PatchMapping(value = "/{id}/scias")
    @ResponseStatus(HttpStatus.OK)
    public SciaSummary createScia(@RequestBody JsonNode request, @PathVariable Long id) {
        Scia scia = objectMapper.convertValue(request, Scia.class);
        return changeRequestService.createScia(id, scia);
    }

    @PatchMapping(value = "/{id}/scias" , params = "scia-id")
    @ResponseStatus(HttpStatus.OK)
    public SciaSummary copyScia(@PathVariable Long id, @RequestParam(name = "scia-id") Long sciaId) {
        return changeRequestService.cloneScia(id, sciaId);
    }

    @GetMapping(value = "/{id}/scias")
    @ResponseStatus(HttpStatus.OK)
    public List<SciaSummary> getScias(@PathVariable Long id) {
        return changeRequestService.getScias(id);
    }
*/
    @PutMapping({"/scheduler/reconciliation"})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole(@mychangeChangeRequestServiceConfigurationProperties.getAdminRoles())")
    public void scheduleChangeRequestReconciliation() {
        changeRequestSchedulerReconciliation.publishModifiedChangeRequests();
    }

    @PostMapping(params = {"is-test-automation-account=true", "status=approved"})
    @ResponseStatus(HttpStatus.OK)
    public ChangeRequest createChangeRequestInApprovedStatus(@RequestBody JsonNode jsonNode) {
        ChangeRequestAggregate changeRequestAggregate = objectMapper.convertValue(jsonNode, ChangeRequestAggregate.class);
        return changeRequestService.createChangeRequestInApprovedStatus(changeRequestAggregate);
    }
}
