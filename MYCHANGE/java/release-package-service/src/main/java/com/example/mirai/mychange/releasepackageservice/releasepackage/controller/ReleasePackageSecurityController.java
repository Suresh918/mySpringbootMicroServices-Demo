package com.example.mirai.projectname.releasepackageservice.releasepackage.controller;

import java.util.List;

import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.CasePermissions;
import com.example.mirai.libraries.security.core.controller.SecurityController;
import com.example.mirai.projectname.libraries.impacteditem.impacteditem.model.SdlMonitor;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.ReleasePackageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("release-packages")
public class ReleasePackageSecurityController extends SecurityController {

    @Autowired
    ReleasePackageService releasePackageService;

    public ReleasePackageSecurityController(ReleasePackageService releasePackageService) {
        super(releasePackageService);
    }

    @Override
    public Class<? extends AggregateInterface> getCaseStatusAggregateClass() {
        return null;
    }

    @Override
    public Class<ReleasePackage> getEntityClass() {
        return ReleasePackage.class;
    }

    @GetMapping({"//{id:[0-9]+}/case-permissions"})
    public CasePermissions getCaseActionsAndCaseProperties(@PathVariable Long id) {
        return this.securityServiceDefaultInterface.getCasePermissions(id);
    }

    @GetMapping({"/{releasePackageNumber:[0-9]+-[0-9]+}/case-permissions"})
    public CasePermissions getCaseActionsAndCasePropertiesByReleasePackageNumber(@PathVariable String releasePackageNumber) {
        return this.securityServiceDefaultInterface.getCasePermissions(releasePackageService.getReleasePackageIdByReleasePackageNumber(releasePackageNumber));
    }

    @GetMapping({"/{ecn:^ECN-[0-9]+}/case-permissions"})
    public CasePermissions getCaseActionsAndCasePropertiesByEcn(@PathVariable String ecn) {
        return this.securityServiceDefaultInterface.getCasePermissions(releasePackageService.getReleasePackageIdByContext(ecn, "ECN"));
    }

    @PatchMapping(value = "/{id}/impacted-items", params = {"work-instruction-ids","case-action"})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<SdlMonitor> performCaseActionAndGetSdlMonitor(@PathVariable("id") Long id, @RequestParam("work-instruction-ids")
                                                  String workInstructionIds, @RequestParam("case-action") String caseAction){
        return releasePackageService.performCaseActionAndGetSdlMonitor(id, workInstructionIds, caseAction);
    }
}
