package com.example.mirai.projectname.changerequestservice.myteam.controller;

import com.example.mirai.libraries.myteam.controller.MyTeamChildEntityController;
import com.example.mirai.libraries.myteam.model.dto.TeamDetails;
import com.example.mirai.projectname.changerequestservice.core.component.EntityResolver;
import com.example.mirai.projectname.changerequestservice.myteam.service.ChangeRequestMyTeamService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"{parentType:change-requests}/{parentId}/{entityType:my-team}"})
public class ChangeRequestMyTeamChildEntityController extends MyTeamChildEntityController {
    ChangeRequestMyTeamService changeRequestMyTeamService;
    public ChangeRequestMyTeamChildEntityController(ObjectMapper objectMapper, ChangeRequestMyTeamService changeRequestMyTeamService,
                                                     EntityResolver entityResolver) {
        super(objectMapper, changeRequestMyTeamService, entityResolver);
        this.changeRequestMyTeamService = changeRequestMyTeamService;
    }


    @SneakyThrows
    @GetMapping(params = "view=detail")
    @ResponseStatus(HttpStatus.OK)
    public TeamDetails getTeamDetail(@PathVariable Long parentId) {
        return changeRequestMyTeamService.getTeamDetails(parentId);
    }
}
