package com.example.mirai.projectname.releasepackageservice.releasepackage.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.example.mirai.libraries.core.annotation.SecurePropertyRead;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.CaseStatus;
import com.example.mirai.libraries.entity.controller.EntityController;
import com.example.mirai.libraries.entity.model.LinkedItems;
import com.example.mirai.libraries.entity.model.StatusOverview;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.projectname.releasepackageservice.core.component.EntityResolver;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate.ReleasePackageAggregate;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.ReleasePackageList;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.ReleasePackageMandatoryParameters;
import com.example.mirai.projectname.releasepackageservice.releasepackage.scheduler.ReleasePackageSchedulerReconciliation;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.ReleasePackageAutomaticClosureService;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.ReleasePackageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("{entityType:release-packages}")
public class ReleasePackageEntityController extends EntityController {
    @Autowired
    private ReleasePackageAutomaticClosureService releasePackageAutomaticClosureService;

    @Autowired
    private ReleasePackageSchedulerReconciliation releasePackageSchedulerReconciliation;


    ReleasePackageEntityController(ReleasePackageService releasePackageService, EntityResolver entityResolver,
                                   ObjectMapper objectMapper) {
        super(objectMapper, releasePackageService, entityResolver);
    }

    @Override
    public Class<? extends BaseEntityInterface> getEntityClass() {
        return ReleasePackage.class;
    }

    public ReleasePackageService getService() {
        return (ReleasePackageService) entityServiceDefaultInterface;
    }

    @GetMapping({"/{id:[0-9]+}"})
    @ResponseStatus(HttpStatus.OK)
    public BaseEntityInterface get(@PathVariable Long id) {
        return this.entityServiceDefaultInterface.getEntityById(id);
    }

    @SecurePropertyRead
    @GetMapping({
            "/{releasePackageNumber:[0-9]+-[0-9]+}"
    })
    @ResponseStatus(HttpStatus.OK)
    public BaseEntityInterface getReleasePackageByReleasePackageNumber(@PathVariable String releasePackageNumber) {
        return getService().getEntityById(getService().getReleasePackageIdByReleasePackageNumber(releasePackageNumber));
    }

    @SecurePropertyRead
    @GetMapping({
            "/{ecn:^ECN-[0-9]+}"
    })
    @ResponseStatus(HttpStatus.OK)
    public BaseEntityInterface getReleasePackageByEcnNumber(@PathVariable String ecn) {
        return getService().getEntityById(getService().getReleasePackageIdByContext(ecn, "ECN"));
    }

    @GetMapping(
            value = {"/{ecn:^ECN-[0-9]+}"},
            params = {"view=linked-items"}
    )
    @ResponseStatus(HttpStatus.OK)
    public LinkedItems getLinkedItemsByEcn(@PathVariable String ecn) {
        Long releasePackageId = getService().getReleasePackageIdByContext(ecn, "ECN");
        return this.entityServiceDefaultInterface.getLinkedItems(releasePackageId);
    }

    @GetMapping(
            value = {"/{releasePackageNumber:[0-9]+-[0-9]+}"},
            params = {"view=linked-items"}
    )
    @ResponseStatus(HttpStatus.OK)
    public LinkedItems getLinkedItemsByReleasePackageNumber(@PathVariable String releasePackageNumber) {
        Long releasePackageId = getService().getReleasePackageIdByReleasePackageNumber(releasePackageNumber);
        return this.entityServiceDefaultInterface.getLinkedItems(releasePackageId);
    }

    @GetMapping(
            value = {"/{id:[0-9]+}"},
            params = {"view=linked-items"}
    )
    @ResponseStatus(HttpStatus.OK)
    @Override
    public LinkedItems getLinkedItems(@PathVariable Long id) {
        return this.entityServiceDefaultInterface.getLinkedItems(id);
    }

    @PatchMapping(
            value = {"/{id}"},
            params = {"case-action=DELETE-MATERIAL-AND-RELEASE"}
    )
    @ResponseStatus(HttpStatus.OK)
    public CaseStatus performCaseActionAndGetPermissions(@PathVariable Long id) {
        return getService().performReleaseAfterDeleteMaterialAndGetCaseStatus(id);

    }

    @GetMapping(
            value = {"/linked-items"},
            params = {"type", "id", "sort"}
    )
    @ResponseStatus(HttpStatus.OK)
    public List<LinkedItems.LinkItem> getLinkedReleasePackages(@RequestParam String type, @RequestParam String id, @RequestParam String sort) {
        return getService().getLinkedReleasePackages(id, type, sort);

    }

    @GetMapping(
            value = {"/{id:[0-9]+}"},
            params = {"view=aggregate"}
            )
    @ResponseStatus(HttpStatus.OK)
    @Override
    public AggregateInterface getAggregate(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        return ((ReleasePackageService) this.entityServiceDefaultInterface).getReleasePackageAggregate(id);
    }

    @GetMapping(
            value = {"/{releasePackageNumber:[0-9]+-[0-9]+}"},
            params = {"view=aggregate"}
    )
    @ResponseStatus(HttpStatus.OK)
    public ReleasePackageAggregate getReleasePackageAggregateByReleasePackageNumber(@PathVariable String releasePackageNumber) {
        Long releasePackageId = getService().getReleasePackageIdByReleasePackageNumber(releasePackageNumber);
        return (ReleasePackageAggregate) ((ReleasePackageService) this.entityServiceDefaultInterface).getReleasePackageAggregate(releasePackageId);
    }

    @GetMapping(
            value = {"/{ecn:^ECN-[0-9]+}"},
            params = {"view=aggregate"}
    )
    @ResponseStatus(HttpStatus.OK)
    public ReleasePackageAggregate getReleasePackageAggregateByEcnNumber(@PathVariable String ecn) {
        Long releasePackageId = getService().getReleasePackageIdByContext(ecn, "ECN");
        return (ReleasePackageAggregate) ((ReleasePackageService) this.entityServiceDefaultInterface).getReleasePackageAggregate(releasePackageId);
    }

    @PatchMapping({"/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public BaseEntityInterface merge(@PathVariable Long id, @RequestBody JsonNode jsonNode) throws JsonProcessingException {
        BaseEntityInterface oldIns = this.objectMapper.treeToValue(jsonNode.get("oldIns"), this.getEntityClass());
        BaseEntityInterface newIns = this.objectMapper.treeToValue(jsonNode.get("newIns"), this.getEntityClass());
        oldIns.setId(id);
        newIns.setId(id);
        List<String> oldInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("oldIns"));
        List<String> newInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("newIns"));
        return getService().mergeEntity(newIns, oldIns, oldInsChangedAttributeNames, newInsChangedAttributeNames);
    }

    @PatchMapping(value = "/{id}", params = "is-system-account=true")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole(@mychangeReleasePackageServiceConfigurationProperties.getTibcoRoles())")
    public BaseEntityInterface mergeEntityBySystemUser(@PathVariable Long id, @RequestBody JsonNode jsonNode) throws JsonProcessingException {
        BaseEntityInterface oldIns = this.objectMapper.treeToValue(jsonNode.get("oldIns"), this.getEntityClass());
        BaseEntityInterface newIns = this.objectMapper.treeToValue(jsonNode.get("newIns"), this.getEntityClass());
        oldIns.setId(id);
        newIns.setId(id);
        List<String> oldInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("oldIns"));
        List<String> newInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("newIns"));
        return getService().mergeEntityBySystemUser(newIns, oldIns, oldInsChangedAttributeNames, newInsChangedAttributeNames);
    }

    @PutMapping({
            "/scheduler/reconciliation"
    })
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_administrator')")
    public void scheduleReleasePackageSchedulerReconciliation() {
        releasePackageSchedulerReconciliation.publishReleasePackagesForReconciliation();
    }

    @GetMapping(
            params = {"context-id", "context-type","view=mandatory-parameters","is-system-account=true"}
    )
    @PreAuthorize("hasAnyRole(@mychangeReleasePackageServiceConfigurationProperties.getTibcoRoles())")
    @ResponseStatus(HttpStatus.OK)
    public List<ReleasePackageMandatoryParameters> getMandatoryParametersByContextId(@RequestParam(name = "context-id") String contextId,
                                                                                     @RequestParam(name = "context-type", defaultValue = "CHANGENOTICE") String contextType) {
        return getService().getMandatoryParametersByContextId(contextId, contextType);
    }

    @PatchMapping(
            params = {"release-package-number", "is-system-account=true"}
    )
    @PreAuthorize("hasAnyRole(@mychangeReleasePackageServiceConfigurationProperties.getTibcoRoles())")
    @ResponseStatus(HttpStatus.OK)
    public ReleasePackage updateReleasePackageTypes(@RequestParam(name = "release-package-number") String releasePackageNumber,
                                                                             @RequestBody JsonNode jsonNode) throws JsonProcessingException, IllegalAccessException {
        Long releasePackageId = getService().getReleasePackageIdByReleasePackageNumber(releasePackageNumber);
        BaseEntityInterface oldIns = this.objectMapper.treeToValue(jsonNode.get("oldIns"), this.getEntityClass());
        BaseEntityInterface newIns = this.objectMapper.treeToValue(jsonNode.get("newIns"), this.getEntityClass());
        oldIns.setId(releasePackageId);
        newIns.setId(releasePackageId);
        List<String> oldInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("oldIns"));
        List<String> newInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("newIns"));
        return getService().mergeReleasePackageTypesBySystemUser((ReleasePackage) newIns, (ReleasePackage) oldIns, oldInsChangedAttributeNames, newInsChangedAttributeNames);
    }

    @GetMapping(params = "view=status-count")
    @ResponseStatus(HttpStatus.OK)
    public StatusOverview getStatusOverview(@RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria) {
        return entityServiceDefaultInterface.getStatusOverview(viewCriteria, entityResolverDefaultInterface.getEntityStatuses(getEntityClass()),ReleasePackageList.class);
    }

    @GetMapping(
            value = {"/{releasePackageNumber:[0-9]+-[0-9]+}"},
            params = {"view=aggregate", "is-system-account=true"}
    )
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole(@mychangeReleasePackageServiceConfigurationProperties.getTibcoRoles())")
    public ReleasePackageAggregate getReleasePackageAggregateBySystemUser(@PathVariable String releasePackageNumber) {
        Long releasePackageId = getService().getReleasePackageIdByReleasePackageNumber(releasePackageNumber);
        return ((ReleasePackageService) this.entityServiceDefaultInterface).getAggregate(releasePackageId);
    }

}


