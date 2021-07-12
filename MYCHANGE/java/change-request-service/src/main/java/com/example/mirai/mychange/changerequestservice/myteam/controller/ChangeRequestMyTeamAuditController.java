package com.example.mirai.projectname.changerequestservice.myteam.controller;

import com.example.mirai.libraries.audit.controller.AuditController;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.projectname.changerequestservice.myteam.model.aggregate.ChangeRequestMyTeamChangeLogAggregateWithRoot;
import com.example.mirai.projectname.changerequestservice.myteam.service.ChangeRequestMyTeamService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"{parentType:change-requests}/{entityType:my-team}"})
public class ChangeRequestMyTeamAuditController extends AuditController {

    public ChangeRequestMyTeamAuditController(ChangeRequestMyTeamService changeRequestMyTeamService) {
        super(changeRequestMyTeamService);
    }

    @Override
    public Class<? extends AggregateInterface> getChangeLogAggregateClass() {
        return ChangeRequestMyTeamChangeLogAggregateWithRoot.class;
    }
}
