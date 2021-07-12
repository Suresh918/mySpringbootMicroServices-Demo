package com.example.mirai.projectname.changerequestservice.myteam.controller;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.myteam.controller.MyTeamEntityController;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestDetail;
import com.example.mirai.projectname.changerequestservice.core.component.EntityResolver;
import com.example.mirai.projectname.changerequestservice.myteam.model.ChangeRequestMyTeam;
import com.example.mirai.projectname.changerequestservice.myteam.model.aggregate.ChangeRequestMyTeamMemberAggregate;
import com.example.mirai.projectname.changerequestservice.myteam.service.ChangeRequestMyTeamService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"{parentType:change-requests}/{entityType:my-team}"})
public class ChangeRequestMyTeamEntityController extends MyTeamEntityController {

    private final ChangeRequestMyTeamService changeRequestMyTeamService;

    public ChangeRequestMyTeamEntityController(ObjectMapper objectMapper, ChangeRequestMyTeamService changeRequestMyTeamService,
                                               EntityResolver entityResolver) {
        super(objectMapper, changeRequestMyTeamService, entityResolver);
        this.changeRequestMyTeamService = changeRequestMyTeamService;
    }

    @Override
    public Class<? extends BaseEntityInterface> getEntityClass() {
        return ChangeRequestMyTeam.class;
    }

    @PatchMapping(value = "/my-team-members", params = "change-request-id")
    public ChangeRequestDetail.MyTeamDetail addImpactedItemMyTeamMember(@RequestParam(name="change-request-id") Long changeRequestId, @RequestBody JsonNode jsonNode) {
        ChangeRequestMyTeamMemberAggregate[] changeObjectMyTeamMembers = objectMapper.convertValue(jsonNode, ChangeRequestMyTeamMemberAggregate[].class);
        List<MyTeamMember> myTeamMembers = Arrays.asList(changeObjectMyTeamMembers).stream().map(memberAggregate -> memberAggregate.getMember()).collect(Collectors.toList());
        return changeRequestMyTeamService.updateMyTeamForCreatorsAndUsers(changeRequestId, myTeamMembers);
    }

}

