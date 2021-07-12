package com.example.mirai.projectname.changerequestservice.impactanalysis.controller;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.entity.controller.EntityController;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.projectname.changerequestservice.core.component.EntityResolver;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
import com.example.mirai.projectname.changerequestservice.impactanalysis.service.ImpactAnalysisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("change-requests/{entityType:impact-analysis}")
public class ImpactAnalysisEntityController extends EntityController {

    private ImpactAnalysisService impactAnalysisService;
    private ObjectMapper objectMapper;

    ImpactAnalysisEntityController(ImpactAnalysisService impactAnalysisService, EntityResolver entityResolver, ObjectMapper objectMapper) {
        super(objectMapper, impactAnalysisService, entityResolver);
        this.impactAnalysisService = impactAnalysisService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Class<? extends BaseEntityInterface> getEntityClass() {
        return ImpactAnalysis.class;
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
        return this.impactAnalysisService.merge(newIns, oldIns, oldInsChangedAttributeNames, newInsChangedAttributeNames);
    }
}
