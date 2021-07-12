package com.example.mirai.projectname.changerequestservice.changerequest.controller;

import com.example.mirai.libraries.core.annotation.SecurePropertyRead;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.entity.controller.ParentEntityController;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestAggregate;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestDetail;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestService;
import com.example.mirai.projectname.changerequestservice.core.component.EntityResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("{entityType:change-requests}")
public class ChangeRequestParentEntityController extends ParentEntityController {
    @Resource
    ChangeRequestParentEntityController self;

    private ChangeRequestService changeRequestService;

    ChangeRequestParentEntityController(ChangeRequestService changeRequestService, EntityResolver entityResolver, ObjectMapper objectMapper) {
        super(objectMapper, changeRequestService, entityResolver);
        this.changeRequestService = changeRequestService;
    }

    @Override
    public Class<? extends BaseEntityInterface> getEntityClass() {
        return ChangeRequest.class;
    }

    @PostMapping({"" +
            "/aggregate"
    })
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ChangeRequestDetail createAggregate(@RequestBody JsonNode jsonNode) throws JsonProcessingException {
        ChangeRequestAggregate changeRequestAggregate = self.createChangeRequestAggregate(jsonNode);
        return changeRequestService.getChangeRequestDetailFromAggregate(changeRequestAggregate);
    }

    //added separate method to wrap for securepropertyread
    @SecurePropertyRead
    public ChangeRequestAggregate createChangeRequestAggregate(JsonNode jsonNode) throws JsonProcessingException {
        ChangeRequestAggregate changeRequestAggregate = objectMapper.treeToValue(jsonNode, ChangeRequestAggregate.class);
        return changeRequestService.createChangeRequestAggregate(changeRequestAggregate);
    }
}
