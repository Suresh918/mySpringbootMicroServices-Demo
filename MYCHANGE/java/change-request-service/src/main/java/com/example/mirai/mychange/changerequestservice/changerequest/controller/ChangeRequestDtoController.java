package com.example.mirai.projectname.changerequestservice.changerequest.controller;

import com.example.mirai.libraries.air.problem.model.Problem;
import com.example.mirai.libraries.cerberus.diabom.model.DiaBom;
import com.example.mirai.libraries.cerberus.functionalcluster.model.FunctionalCluster;
import com.example.mirai.libraries.cerberus.productbrakedownstructure.model.ProductBreakdownStructure;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.entity.model.StatusOverview;
import com.example.mirai.projectname.changerequestservice.changerequest.model.dto.*;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestService;
import com.example.mirai.projectname.changerequestservice.core.component.EntityResolver;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.dto.CustomerImpactDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.ProjectDto;
import wbs.WbsDto;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@Data
@RequestMapping("{entityType:change-requests}")
public class ChangeRequestDtoController {

    private final ChangeRequestService changeRequestService;
    private final EntityResolver entityResolver;
    private final ObjectMapper objectMapper;

    @GetMapping("/{id}/project")
    @ResponseStatus(HttpStatus.OK)
    public WbsDto getProjectByChangeRequestId(@PathVariable Long id) {
        return changeRequestService.getWorkBreakdownStructure(id);
    }

    @GetMapping("/{id}/product")
    @ResponseStatus(HttpStatus.OK)
    public ProjectDto getProductByChangeRequestId(@PathVariable Long id) {
        return changeRequestService.getProduct(id);
    }

    @GetMapping("/{id}/project-lead")
    @ResponseStatus(HttpStatus.OK)
    public User getProjectLeadByChangeRequestId(@PathVariable Long id) {
        return changeRequestService.getProjectLead(id);
    }

    @GetMapping("/{id}/dia-bom")
    @ResponseStatus(HttpStatus.OK)
    public DiaBom getDiaBomByChangeRequestId(@PathVariable Long id) {
        return changeRequestService.getDiaBom(id);
    }

    @GetMapping(value = "/{id}", params = "view=collaboration-objects")
    @ResponseStatus(HttpStatus.OK)
    public CollaborationObjectCount getCollaborationObjectsCountById(@PathVariable Long id) {
        return changeRequestService.getCollaborationObjectsCount(id);
    }

    @GetMapping(params = "view=change-request-list")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseEntityList<ChangeRequestList> getChangeRequestsList(@RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria,
                                                              @RequestParam(name = "slice-select", defaultValue = "") String sliceSelect,
                                                              @PageableDefault(20) Pageable pageable) {
        return changeRequestService.getChangeRequestsList(viewCriteria, pageable, Optional.ofNullable(sliceSelect));
    }

    @GetMapping(params = "view=linked-object")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseEntityList<LinkedObject> getChangeRequestAsLinkedObject(@RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria,
                                                             @RequestParam(name = "slice-select", defaultValue = "") String sliceSelect,
                                                             @PageableDefault(20) Pageable pageable) {
        return changeRequestService.getChangeRequestAsLinkedObject(viewCriteria, pageable, Optional.ofNullable(sliceSelect));
    }

    @GetMapping(params = "view=overview")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseEntityList<Overview> getChangeRequestOverview(@RequestParam(name = "criteria", defaultValue = "") String criteria,
                                                             @RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria,
                                                             @RequestParam(name = "slice-select", defaultValue = "") String sliceSelect,
                                                             @PageableDefault(20) Pageable pageable) {
        return changeRequestService.getChangeRequestsOverview(criteria, viewCriteria, pageable, Optional.ofNullable(sliceSelect));
    }

    @GetMapping(params = "view=summary")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseEntityList<Summary> getChangeRequestsSummary(@RequestParam(name = "criteria", defaultValue = "") String criteria,
                                                            @RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria,
                                                            @RequestParam(name = "slice-select", defaultValue = "") String sliceSelect,
                                                            @PageableDefault(20) Pageable pageable) {
        return changeRequestService.getChangeRequestsSummary(criteria, viewCriteria, pageable, Optional.ofNullable(sliceSelect));
    }

    @GetMapping(params = {"view=summary", "is-system-account=true"})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole(@mychangeChangeRequestServiceConfigurationProperties.getTibcoRoles())")
    public BaseEntityList<Summary> insecureFetchChangeRequestsSummary(@RequestParam(name = "criteria", defaultValue = "") String criteria,
                                                            @RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria,
                                                            @RequestParam(name = "slice-select", defaultValue = "") String sliceSelect,
                                                            @PageableDefault(20) Pageable pageable) {
        return changeRequestService.insecureFetchChangeRequestsSummary(criteria, viewCriteria, pageable, Optional.ofNullable(sliceSelect));
    }

    @GetMapping(params = "view=state-overview")
    @ResponseStatus(HttpStatus.OK)
    public StateOverview getStateOverview(@RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria, @PathVariable String entityType) {
        return changeRequestService.getStateOverview(viewCriteria, entityResolver.getEntityStatuses(entityType));
    }
    @GetMapping(params = "view=state-overview-by-priority")
    @ResponseStatus(HttpStatus.OK)
    public StateOverviewByField getStatusCountByPriority(@RequestParam(name = "criteria", defaultValue = "") String criteria,
                                                         @RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria) {
        return changeRequestService.getStatusCountByPriority(criteria, viewCriteria);
    }

    @GetMapping(params = {"view=status-count", "status"})
    @ResponseStatus(HttpStatus.OK)
    public StatusOverview.StatusCount getChangeRequestStatusCount(@RequestParam(name = "criteria", defaultValue = "") String criteria,
                                                                     @RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria,
                                                                     @RequestParam (defaultValue = "8") Integer status) {
        return changeRequestService.getChangeRequestStatusCount(criteria, viewCriteria, status);
    }

    @GetMapping(params = "view=search-summary")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseEntityList<SearchSummary> getChangeRequestSearchSummary(@RequestParam(name = "criteria", defaultValue = "") String criteria,
                                                                       @RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria,
                                                                       @RequestParam(name = "slice-select", defaultValue = "") String sliceSelect,
                                                                       @PageableDefault(20) Pageable pageable) {
        return changeRequestService.getChangeRequestSearchSummary(criteria, viewCriteria, pageable, Optional.ofNullable(sliceSelect));
    }

    @GetMapping(params = {"view=agenda-link-summary", "agenda-item-ids"})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseEntityList<AgendaLinkOverview> getChangeRequestAgendaLinkSummary(@RequestParam(name = "agenda-item-ids", defaultValue = "") String[] agendaItemIds,
                                                                                @RequestParam(name = "slice-select", defaultValue = "") String sliceSelect,
                                                                                @RequestParam(name = "include-ruleset", defaultValue = "false") boolean includeRuleSet,
                                                                                @PageableDefault(20) Pageable pageable) {
        return changeRequestService.getChangeRequestAgendaLinkSummary(agendaItemIds, pageable, Optional.ofNullable(sliceSelect), includeRuleSet);
    }


    //Get Product Breakdown Structure

    /*@GetMapping(value = "/product-breakdown-structures/{productBreakdownStructureId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductBreakdownStructure getProductBreakDownStructureForCrId(@PathVariable String productBreakdownStructureId) throws JsonProcessingException, URISyntaxException, URISyntaxException {
        return changeRequestService.getProductBreakdownStructureById(productBreakdownStructureId);
    }*/

    @GetMapping(value = "/product-breakdown-structures", params = {"search"})
    @ResponseStatus(HttpStatus.OK)
    public List<ProductBreakdownStructure> fetchMultipleProductBreakDownStructures(@RequestParam(name = "search", defaultValue = "") Long productBreakdownStructureId) throws URISyntaxException {
        return changeRequestService.findProductBreakdownStructuresByPartialId(productBreakdownStructureId.toString());
    }

    /*@GetMapping(value="/problems/{airProblemId}")
    @ResponseStatus(HttpStatus.OK)
    public Problem getAirProblemById(@PathVariable String airProblemId) {
        return changeRequestService.getAirProblemById(airProblemId);
    }*/
    @GetMapping(value = "/{changeRequestId}/problems")
    @ResponseStatus(HttpStatus.OK)
    public List<Problem> getProblems(@PathVariable Long changeRequestId) {
        return changeRequestService.getProblemsByChangeRequestId(changeRequestId);
    }

    @GetMapping(value = "/{changeRequestId}/product-breakdown-structures")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductBreakdownStructure> getProductBreakdownStructures(@PathVariable Long changeRequestId) {
        return changeRequestService.getProductBreakdownStructureByChangeRequestId(changeRequestId);
    }

    @GetMapping(value = "/problems", params = {"search"})
    @ResponseStatus(HttpStatus.OK)
    public List<Problem> findAirProblemsById(@RequestParam(name = "search") String airProblemId) {
        return changeRequestService.findAirProblemsByPartialId(airProblemId);
    }

    @GetMapping("/{id}/functional-cluster")
    @ResponseStatus(HttpStatus.OK)
    public FunctionalCluster getFunctionalClusterDetails(@PathVariable Long id) {
        return changeRequestService.getFunctionalClusterDetails(id);
    }

    @GetMapping(value = "/functional-clusters", params = {"search"})
    @ResponseStatus(HttpStatus.OK)
    public List<FunctionalCluster> searchFunctionalCluster(@RequestParam(name = "search") String fcId) {
        return changeRequestService.findFunctionalClusterByPartialId(fcId);
    }

    @GetMapping(value = "/{id}/customer-impact", params = "view=details")
    @ResponseStatus(HttpStatus.OK)
    public CustomerImpactDetail getCiaDetails(@PathVariable Long id) {
        return changeRequestService.getCiaDetail(id);
    }

    @GetMapping(value = "/problems", params = {"agenda-item-id"})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<Problem> getProblemsByAgendaItemId(@RequestParam(name = "agenda-item-id") String agendaItemId) {
        return changeRequestService.getProblemsByAgendaItemId(agendaItemId);
    }

    @GetMapping(value = "/project", params = {"agenda-item-id"})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public WbsDto getProjectByAgendaItemId(@RequestParam(name = "agenda-item-id") String agendaItemId) {
        return changeRequestService.getProjectByAgendaItemId(agendaItemId);
    }

    @GetMapping(value = "/product", params = {"agenda-item-id"})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ProjectDto getProductByAgendaItemId(@RequestParam(name = "agenda-item-id") String agendaItemId) {
        return changeRequestService.getProductByAgendaItemId(agendaItemId);
    }

    @GetMapping(value = "/products", params = {"agenda-item-ids"})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<ChangeRequestProjectDto> getProductByAgendaItemId(@RequestParam(name = "agenda-item-ids") String[] agendaItemIds) {
        return changeRequestService.getProductByAgendaItemIds(agendaItemIds);
    }

    @GetMapping(value= "/pmo" , params = {"agenda-item-id"})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public PmoDetails getPmoDetailsByAgendaItemId(@RequestParam(name = "agenda-item-id") String agendaItemId) {
        return changeRequestService.getPmoDetailsByAgendaItemId(agendaItemId);
    }

    @GetMapping(value = "/product-breakdown-structures", params = {"agenda-item-id"})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<ProductBreakdownStructure> getProductBreakdownStructuresByContextIdAndType(@RequestParam(name = "agenda-item-id") String agendaItemId) {
        return changeRequestService.getProductBreakdownStructuresByAgendaItemId(agendaItemId);
    }


    @GetMapping(params = "view=trackerboard-summary")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<ChangeRequestCategory> getChangeRequestTrackerboardSummary(@RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria,
                                                                           @RequestParam(name = "slice-select", defaultValue = "") String sliceSelect,
                                                                           @PageableDefault(20) Pageable pageable) {
        return changeRequestService.getChangeRequestTrackerBoardSummary(viewCriteria, pageable, Optional.ofNullable(sliceSelect));
    }

    @GetMapping(value = "/{id}/scope-field-enablement")
    @ResponseStatus(HttpStatus.OK)
    public Object getFieldEnablement(@PathVariable Long id) {
        return changeRequestService.getScopeFieldVisibilityFactor(id);
    }

    @GetMapping(params = "view=global-search")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseEntityList<GlobalSearch> getChangeRequestsForGlobalSearch(@RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria,
                                                             @RequestParam(name = "slice-select", defaultValue = "") String sliceSelect,
                                                             @PageableDefault(20) Pageable pageable) {
        return changeRequestService.getChangeRequestsForGlobalSearch(viewCriteria, pageable, Optional.ofNullable(sliceSelect));
    }

    @GetMapping(value = "/{id}", params = "view=is-first-draft")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public IsFirstDraft getChangeRequestIsFirstDraft(@PathVariable Long id) {
        return changeRequestService.getChangeRequestIsFirstDraft(id);
    }


}
