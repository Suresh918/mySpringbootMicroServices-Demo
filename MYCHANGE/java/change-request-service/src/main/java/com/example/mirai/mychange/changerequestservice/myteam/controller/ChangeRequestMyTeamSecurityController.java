package com.example.mirai.projectname.changerequestservice.myteam.controller;

import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.myteam.controller.MyTeamSecurityController;
import com.example.mirai.projectname.changerequestservice.myteam.model.ChangeRequestMyTeam;
import com.example.mirai.projectname.changerequestservice.myteam.service.ChangeRequestMyTeamService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("{parentType:change-requests}/{entityType:my-team}")
public class ChangeRequestMyTeamSecurityController extends MyTeamSecurityController {

    public ChangeRequestMyTeamSecurityController(ChangeRequestMyTeamService changeRequestMyTeamService) {
        super(changeRequestMyTeamService);
    }

    @Override
    public Class<AggregateInterface> getCaseStatusAggregateClass() {
        return null;
    }

    @Override
    public Class<ChangeRequestMyTeam> getEntityClass() {
        return ChangeRequestMyTeam.class;
    }
}
