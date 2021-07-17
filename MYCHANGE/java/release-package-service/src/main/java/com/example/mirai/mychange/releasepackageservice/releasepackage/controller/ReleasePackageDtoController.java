package com.example.mirai.projectname.releasepackageservice.releasepackage.controller;

import java.util.Optional;

import com.example.mirai.libraries.cerberus.diabom.model.DiaBom;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.teamcenter.ecn.model.DeltaReport;
import com.example.mirai.projectname.releasepackageservice.core.component.EntityResolver;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.CollaborationObjectCount;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.LinkedObject;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.Overview;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.ReleasePackageList;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.SapErDetails;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.SearchSummary;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.ReleasePackageService;
import lombok.Data;
import project.ProjectDto;
import wbs.WbsDto;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("{entityType:release-packages}")
@Data
public class ReleasePackageDtoController {

    private final ReleasePackageService releasePackageService;
    private final EntityResolver entityResolver;

    @GetMapping("/{id:[0-9]+}/project")
    @ResponseStatus(HttpStatus.OK)
    public WbsDto getProjectByReleasePackageId(@PathVariable Long id) {
        return releasePackageService.getWorkBreakdownStructure(id);
    }

    @GetMapping("/{releasePackageNumber:[0-9]+-[0-9]+}/project")
    @ResponseStatus(HttpStatus.OK)
    public WbsDto getProjectByReleasePackageNumber(@PathVariable String releasePackageNumber) {
        return releasePackageService.getWorkBreakdownStructure(releasePackageService.getReleasePackageIdByReleasePackageNumber(releasePackageNumber));
    }

    @GetMapping("/{ecn:^ECN-[0-9]+}/project")
    @ResponseStatus(HttpStatus.OK)
    public WbsDto getProjectByEcn(@PathVariable String ecn) {
        return releasePackageService.getWorkBreakdownStructure(releasePackageService.getReleasePackageIdByContext(ecn, "ECN"));
    }

    @GetMapping("/{id:[0-9]+}/product")
    @ResponseStatus(HttpStatus.OK)
    public ProjectDto getProductByReleasePackageId(@PathVariable Long id) {
        return releasePackageService.getProduct(id);
    }

    @GetMapping("/{releasePackageNumber:[0-9]+-[0-9]+}/product")
    @ResponseStatus(HttpStatus.OK)
    public ProjectDto getProductByReleasePackageNumber(@PathVariable String releasePackageNumber) {
        return releasePackageService.getProduct(releasePackageService.getReleasePackageIdByReleasePackageNumber(releasePackageNumber));
    }

    @GetMapping("/{ecn:^ECN-[0-9]+}/product")
    @ResponseStatus(HttpStatus.OK)
    public ProjectDto getProductByEcn(@PathVariable String ecn) {
        return releasePackageService.getProduct(releasePackageService.getReleasePackageIdByContext(ecn, "ECN"));
    }

    @GetMapping("/{id:[0-9]+}/project-lead")
    @ResponseStatus(HttpStatus.OK)
    public User getProjectLeadByReleasePackageId(@PathVariable Long id) {
        return releasePackageService.getProjectLead(id);
    }

    @GetMapping("/{releasePackageNumber:[0-9]+-[0-9]+}/project-lead")
    @ResponseStatus(HttpStatus.OK)
    public User getProjectLeadByReleasePackageNumber(@PathVariable String releasePackageNumber) {
        return releasePackageService.getProjectLead(releasePackageService.getReleasePackageIdByReleasePackageNumber(releasePackageNumber));
    }

    @GetMapping("/{ecn:^ECN-[0-9]+}/project-lead")
    @ResponseStatus(HttpStatus.OK)
    public User getProjectLeadByEcn(@PathVariable String ecn) {
        return releasePackageService.getProjectLead(releasePackageService.getReleasePackageIdByContext(ecn, "ECN"));
    }


    @GetMapping("/{id:\\d+}/delta1-report")
    @ResponseStatus(HttpStatus.OK)
    public DeltaReport getDeltaReportByReleasePackageId(@PathVariable Long id) {
        return releasePackageService.getDelta1Report(id);
    }

    @GetMapping("/{releasePackageNumber:[0-9]+-[0-9]+}/delta1-report")
    @ResponseStatus(HttpStatus.OK)
    public DeltaReport getDeltaReportByReleasePackageNumber(@PathVariable String releasePackageNumber) {
        return releasePackageService.getDelta1Report(releasePackageService.getReleasePackageIdByReleasePackageNumber(releasePackageNumber));
    }

    @GetMapping("/{ecn:^ECN-[0-9]+}/delta1-report")
    @ResponseStatus(HttpStatus.OK)
    public DeltaReport getDeltaReportByEcn(@PathVariable String ecn) {
        return releasePackageService.getDelta1Report(releasePackageService.getReleasePackageIdByContext(ecn, "ECN"));
    }


    @GetMapping("/{id}/dia-bom")
    @ResponseStatus(HttpStatus.OK)
    public DiaBom getDiaBomByReleasePackageId(@PathVariable Long id) {
        return releasePackageService.getDiaBom(id);
    }

    @GetMapping("/{releasePackageNumber:[0-9]+-[0-9]+}/dia-bom")
    @ResponseStatus(HttpStatus.OK)
    public DiaBom getDiaBomByReleasePackageNumber(@PathVariable String releasePackageNumber) {
        return releasePackageService.getDiaBom(releasePackageService.getReleasePackageIdByReleasePackageNumber(releasePackageNumber));
    }

    @GetMapping("/{ecn:^ECN-[0-9]+}/dia-bom")
    @ResponseStatus(HttpStatus.OK)
    public DiaBom getDiaBomByEcn(@PathVariable String ecn) {
        return releasePackageService.getDiaBom(releasePackageService.getReleasePackageIdByContext(ecn, "ECN"));
    }

    @GetMapping(value = "/{id:[0-9]+}", params = "view=collaboration-objects")
    @ResponseStatus(HttpStatus.OK)
    public CollaborationObjectCount getCollaborationObjectsCountById(@PathVariable Long id) {
        return releasePackageService.getCollaborationObjectsCount(id);
    }

    @GetMapping(value = "/{releasePackageNumber:[0-9]+-[0-9]+}", params = "view=collaboration-objects")
    @ResponseStatus(HttpStatus.OK)
    public CollaborationObjectCount getCollaborationObjectsCountByReleasePackageNumber(@PathVariable String releasePackageNumber) {
        return releasePackageService.getCollaborationObjectsCount(releasePackageService.getReleasePackageIdByReleasePackageNumber(releasePackageNumber));
    }

    @GetMapping(value = "/{ecn:^ECN-[0-9]+}", params = "view=collaboration-objects")
    @ResponseStatus(HttpStatus.OK)
    public CollaborationObjectCount getCollaborationObjectsCountByEcn(@PathVariable String ecn) {
        return releasePackageService.getCollaborationObjectsCount(releasePackageService.getReleasePackageIdByContext(ecn, "ECN"));
    }

    @GetMapping(params = "view=release-package-list")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseEntityList<ReleasePackageList> getReleasePackageList(@RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria,
                                                              @RequestParam(name = "slice-select", defaultValue = "") String sliceSelect,
                                                              @PageableDefault(20) Pageable pageable) {
        return releasePackageService.getReleasePackageList(viewCriteria, pageable, Optional.ofNullable(sliceSelect));
    }

    @GetMapping(params = "view=overview")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseEntityList<Overview> getReleasePackageOverview(@RequestParam(name = "criteria", defaultValue = "") String criteria,
                                                              @RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria,
                                                              @RequestParam(name = "slice-select", defaultValue = "") String sliceSelect,
                                                              @PageableDefault(20) Pageable pageable) {
        return releasePackageService.getReleasePackageOverview(criteria, viewCriteria, pageable, Optional.ofNullable(sliceSelect));
    }

    @GetMapping(params = "view=search-summary")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseEntityList<SearchSummary> getReleasePackageSearchSummary(@RequestParam(name = "criteria", defaultValue = "") String criteria,
                                                                        @RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria,
                                                                        @RequestParam(name = "slice-select", defaultValue = "") String sliceSelect,
                                                                        @RequestParam(name = "view", defaultValue = "") String view,
                                                                        @PageableDefault(20) Pageable pageable) {
        return releasePackageService.getReleasePackageSearchSummary(criteria, viewCriteria, view, pageable, Optional.ofNullable(sliceSelect));
    }

    @GetMapping("/{id:[0-9]+}/sap-engineering-record")
    @ResponseStatus(HttpStatus.OK)
    public SapErDetails getSapErDetails(@PathVariable Long id) {
        return releasePackageService.getSapErDetails(id);
    }

    @GetMapping("/{releasePackageNumber:[0-9]+-[0-9]+}/sap-engineering-record")
    @ResponseStatus(HttpStatus.OK)
    public SapErDetails getSapErDetailsByReleasePackageNumber(@PathVariable String releasePackageNumber) {
        return releasePackageService.getSapErDetails(releasePackageService.getReleasePackageIdByReleasePackageNumber(releasePackageNumber));
    }

    @GetMapping("/{ecn:^ECN-[0-9]+}/sap-engineering-record")
    @ResponseStatus(HttpStatus.OK)
    public SapErDetails getSapErDetailsByEcnNumber(@PathVariable String ecn) {
        return releasePackageService.getSapErDetails(releasePackageService.getReleasePackageIdByContext(ecn, "ECN"));
    }

    @GetMapping(params = "view=global-search")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseEntityList<Overview> getReleasePackagesForGlobalSearch(@RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria,
                                                                     @RequestParam(name = "slice-select", defaultValue = "") String sliceSelect,
                                                                     @PageableDefault(20) Pageable pageable) {
        return releasePackageService.getReleasePackagesForGlobalSearch(viewCriteria, pageable, Optional.ofNullable(sliceSelect));
    }

    @GetMapping(params = "view=linked-object")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseEntityList<LinkedObject> getChangeRequestAsLinkedObject(@RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria,
                                                                       @RequestParam(name = "slice-select", defaultValue = "") String sliceSelect,
                                                                       @PageableDefault(20) Pageable pageable) {
        return releasePackageService.getReleasePackageAsLinkedObject(viewCriteria, pageable, Optional.ofNullable(sliceSelect));
    }
}
