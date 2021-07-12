package com.example.mirai.projectname.changerequestservice.changerequest.controller;

import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestDetailWithComments;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestMigrateServiceOld;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class ChangeRequestMigrateControllerOld {

    private final ChangeRequestMigrateServiceOld changeRequestMigrateServiceOld;
    private final ObjectMapper objectMapper;

    @PostMapping({"" +
            "/migrate/change-requests/aggregate"
    })
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ChangeRequestDetailWithComments create(@RequestBody JsonNode jsonNode) throws JsonProcessingException {
        ChangeRequestDetailWithComments changeRequestAggregate = objectMapper.treeToValue(jsonNode, ChangeRequestDetailWithComments.class);
        return changeRequestMigrateServiceOld.createChangeRequestMigrateAggregate(changeRequestAggregate);
    }
}
