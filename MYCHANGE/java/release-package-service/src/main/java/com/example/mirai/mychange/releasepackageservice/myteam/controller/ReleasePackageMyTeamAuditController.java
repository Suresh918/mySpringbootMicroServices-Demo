package com.example.mirai.projectname.releasepackageservice.myteam.controller;

import com.example.mirai.libraries.audit.controller.AuditController;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.projectname.releasepackageservice.myteam.model.aggregate.ReleasePackageMyTeamChangeLogAggregateWithRoot;
import com.example.mirai.projectname.releasepackageservice.myteam.service.ReleasePackageMyTeamService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"{parentType:release-packages}/{entityType:my-team}"})
public class ReleasePackageMyTeamAuditController extends AuditController {

    public ReleasePackageMyTeamAuditController(ReleasePackageMyTeamService releasePackageMyTeamService) {
        super(releasePackageMyTeamService);
    }

    @Override
    public Class<? extends AggregateInterface> getChangeLogAggregateClass() {
        return ReleasePackageMyTeamChangeLogAggregateWithRoot.class;
    }
}
