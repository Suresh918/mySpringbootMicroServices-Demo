package com.example.mirai.projectname.releasepackageservice.myteam.controller;

import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.myteam.controller.MyTeamSecurityController;
import com.example.mirai.projectname.releasepackageservice.myteam.model.ReleasePackageMyTeam;
import com.example.mirai.projectname.releasepackageservice.myteam.service.ReleasePackageMyTeamService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("{parentType:release-packages}/{entityType:my-team}")
public class ReleasePackageMyTeamSecurityController extends MyTeamSecurityController {

    public ReleasePackageMyTeamSecurityController(ReleasePackageMyTeamService releasePackageMyTeamService) {
        super(releasePackageMyTeamService);
    }

    @Override
    public Class<AggregateInterface> getCaseStatusAggregateClass() {
        return null;
    }

    @Override
    public Class<ReleasePackageMyTeam> getEntityClass() {
        return ReleasePackageMyTeam.class;
    }
}
