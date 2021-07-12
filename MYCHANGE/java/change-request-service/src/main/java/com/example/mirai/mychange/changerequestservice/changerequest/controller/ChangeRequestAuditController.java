package com.example.mirai.projectname.changerequestservice.changerequest.controller;

import com.example.mirai.libraries.audit.controller.AuditController;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestChangeLogAggregate;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("{entityType:change-requests}")
public class ChangeRequestAuditController extends AuditController {

    public ChangeRequestAuditController(ChangeRequestService changeRequestService) {
        super(changeRequestService);
    }

    @Override
    public Class<? extends AggregateInterface> getChangeLogAggregateClass() {
        return ChangeRequestChangeLogAggregate.class;
    }
}
