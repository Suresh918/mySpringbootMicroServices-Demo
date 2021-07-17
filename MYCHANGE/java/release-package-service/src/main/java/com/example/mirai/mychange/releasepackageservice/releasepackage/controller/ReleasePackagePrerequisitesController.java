package com.example.mirai.projectname.releasepackageservice.releasepackage.controller;

import java.util.Arrays;
import java.util.List;

import com.example.mirai.libraries.core.exception.CaseActionNotFoundException;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.PrerequisiteReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.Overview;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.ReleasePackageReorderPrerequisites;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.ReleasePackageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Data
@RequestMapping("release-packages")
public class ReleasePackagePrerequisitesController {

    private final ReleasePackageService releasePackageService;
    private final ObjectMapper objectMapper;

    @GetMapping(value = "/{id:\\d+}/prerequisites", params = "view=overview")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseEntityList<Overview> getReleasePackagePrerequisitesOverviewById(@PathVariable Long id) {
        return releasePackageService.getPrerequisitesOverview(id);
    }

    @GetMapping(value = "/{releasePackageNumber:[0-9]+-[0-9]+}/prerequisites", params = "view=overview")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseEntityList<Overview> getReleasePackagePrerequisitesOverviewByNumber(@PathVariable String releasePackageNumber) {
        return releasePackageService.getPrerequisitesOverview(releasePackageService.getReleasePackageIdByReleasePackageNumber(releasePackageNumber));
    }

    @GetMapping(value = "/{ecn:^ECN-[0-9]+}/prerequisites", params = "view=overview")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseEntityList<Overview> getReleasePackagePrerequisitesOverviewByEcn(@PathVariable String ecn) {
        return releasePackageService.getPrerequisitesOverview(releasePackageService.getReleasePackageIdByContext(ecn, "ECN"));
    }

    @GetMapping(value = "/{id:\\d+}/prerequisites", params = "view=release-package-numbers")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getPrerequisitesByReleasePackageNumbers(@PathVariable Long id) {
        return releasePackageService.getPrerequisiteReleasePackageNumbers(id);
    }

    @GetMapping(value = "/{releasePackageNumber:[0-9]+-[0-9]+}/prerequisites", params = "view=release-package-numbers")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getPrerequisitesByReleasePackageNumber(@PathVariable String releasePackageNumber) {
        return releasePackageService.getPrerequisiteReleasePackageNumbers(releasePackageService.getReleasePackageIdByReleasePackageNumber(releasePackageNumber));
    }

    @GetMapping(value = "/{ecn:^ECN-[0-9]+}/prerequisites", params = "view=release-package-numbers")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getPrerequisitesByEcn(@PathVariable String ecn) {
        return releasePackageService.getPrerequisiteReleasePackageNumbers(releasePackageService.getReleasePackageIdByContext(ecn, "ECN"));
    }

    @PutMapping(value = "/{id}/prerequisites", params = {"case-action"})
    @ResponseStatus(HttpStatus.OK)
    public ReleasePackageReorderPrerequisites reorderPrerequisites(@PathVariable Long id, @RequestBody JsonNode jsonNode,@RequestParam(name="case-action") String caseAction
            , @RequestParam(name="is-impact-check-required",defaultValue="false") boolean isImpactCheckRequired) {

        synchronized (this) {
            PrerequisiteReleasePackage prerequisiteReleasePackage = objectMapper.convertValue(jsonNode, PrerequisiteReleasePackage.class);
            switch(caseAction.toUpperCase()) {
                case "UPDATE_PREREQUISITE":
                    return releasePackageService.reorderPrerequisites(id, prerequisiteReleasePackage, isImpactCheckRequired);
                case "REMOVE_PREREQUISITE":
                    return releasePackageService.deletePrerequisites(id, prerequisiteReleasePackage, isImpactCheckRequired);
                default:
                    throw new CaseActionNotFoundException();
            }
        }
    }

    @PostMapping(value = "/{id}/prerequisites")
    @ResponseStatus(HttpStatus.OK)
    public BaseEntityList <Overview> addPrerequisites(@PathVariable Long id, @RequestBody JsonNode jsonNode) {
        PrerequisiteReleasePackage[] inputPrerequisites = objectMapper.convertValue(jsonNode, PrerequisiteReleasePackage[].class);
        return releasePackageService.addPrerequisites(id, Arrays.asList(inputPrerequisites));
    }

}
