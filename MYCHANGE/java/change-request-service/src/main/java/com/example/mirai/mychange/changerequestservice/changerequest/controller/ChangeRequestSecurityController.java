package com.example.mirai.projectname.changerequestservice.changerequest.controller;

import com.example.mirai.libraries.security.core.controller.SecurityController;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestCaseStatusAggregate;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("{entityType:change-requests}")
public class ChangeRequestSecurityController extends SecurityController {

    public ChangeRequestSecurityController(ChangeRequestService changeRequestService) {
        super(changeRequestService);
    }

    @Override
    public Class<ChangeRequestCaseStatusAggregate> getCaseStatusAggregateClass() {
        return ChangeRequestCaseStatusAggregate.class;
    }

    @Override
    public Class<ChangeRequest> getEntityClass() {
        return ChangeRequest.class;
    }
}
