package com.example.mirai.projectname.changerequestservice.solutiondefinition.controller;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.entity.controller.EntityController;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.projectname.changerequestservice.core.component.EntityResolver;
import com.example.mirai.projectname.changerequestservice.solutiondefinition.model.SolutionDefinition;
import com.example.mirai.projectname.changerequestservice.solutiondefinition.service.SolutionDefinitionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("change-requests/{entityType:solution-definition}")
public class SolutionDefinitionEntityController extends EntityController {

    @Resource
    SolutionDefinitionEntityController self;

    private SolutionDefinitionService solutionDefinitionService;
    private ObjectMapper objectMapper;

    SolutionDefinitionEntityController(SolutionDefinitionService solutionDefinitionService, EntityResolver entityResolver, ObjectMapper objectMapper) {
        super(objectMapper, solutionDefinitionService, entityResolver);
        this.solutionDefinitionService = solutionDefinitionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Class<? extends BaseEntityInterface> getEntityClass() {
        return SolutionDefinition.class;
    }


    @PatchMapping({"/{id}"})
    @ResponseStatus(HttpStatus.OK)
    @Override
    public BaseEntityInterface merge(@PathVariable Long id, @RequestBody JsonNode jsonNode) throws JsonProcessingException {
        BaseEntityInterface oldIns = this.objectMapper.treeToValue(jsonNode.get("oldIns"), this.getEntityClass());
        BaseEntityInterface newIns = this.objectMapper.treeToValue(jsonNode.get("newIns"), this.getEntityClass());
        oldIns.setId(id);
        newIns.setId(id);
        List<String> oldInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("oldIns"));
        List<String> newInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("newIns"));
        return this.solutionDefinitionService.merge(newIns, oldIns, oldInsChangedAttributeNames, newInsChangedAttributeNames);
    }
}
