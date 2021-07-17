package com.example.mirai.projectname.releasepackageservice.releasepackage.controller;

import com.example.mirai.libraries.audit.model.ChangeLog;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.ReleasePackageService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("release-packages")
public class ReleasePackageAuditController extends com.example.mirai.libraries.audit.controller.AuditController {

    private final ReleasePackageService releasePackageService;
    public ReleasePackageAuditController(ReleasePackageService releasePackageService) {
        super(releasePackageService);
        this.releasePackageService = releasePackageService;
    }

    @Override
    public Class<? extends AggregateInterface> getChangeLogAggregateClass() {
        return null;
    }

    @GetMapping(
            value = {"/{releasePackageNumber:^4[0-9]+-[0-9]+}/change-log"}
    )
    @ResponseStatus(HttpStatus.OK)
    public ChangeLog getChangeLogByReleasePackageNumber(@PathVariable String releasePackageNumber) {
        Long releasePackageId = releasePackageService.getReleasePackageIdByReleasePackageNumber(releasePackageNumber);
        return super.getChangeLog(releasePackageId);
    }

    @GetMapping(
            value = {"/{id:[0-9]+}/change-log"}
    )
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ChangeLog getChangeLog(@PathVariable Long id) {
        return super.getChangeLog(id);
    }
}
