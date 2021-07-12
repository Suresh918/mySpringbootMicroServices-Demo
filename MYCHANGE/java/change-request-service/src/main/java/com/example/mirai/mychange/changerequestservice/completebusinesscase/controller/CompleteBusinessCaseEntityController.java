package com.example.mirai.projectname.changerequestservice.completebusinesscase.controller;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.entity.controller.EntityController;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.projectname.changerequestservice.completebusinesscase.model.CompleteBusinessCase;
import com.example.mirai.projectname.changerequestservice.completebusinesscase.service.CompleteBusinessCaseService;
import com.example.mirai.projectname.changerequestservice.core.component.EntityResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("change-requests/{entityType:complete-business-case}")
public class CompleteBusinessCaseEntityController extends EntityController {

    @Resource
    CompleteBusinessCaseEntityController self;

    private CompleteBusinessCaseService completeBusinessCaseService;
    private ObjectMapper objectMapper;

    CompleteBusinessCaseEntityController(CompleteBusinessCaseService completeBusinessCaseService, EntityResolver entityResolver, ObjectMapper objectMapper) {
        super(objectMapper, completeBusinessCaseService, entityResolver);
        this.completeBusinessCaseService = completeBusinessCaseService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Class<? extends BaseEntityInterface> getEntityClass() {
        return CompleteBusinessCase.class;
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
        return this.completeBusinessCaseService.merge(newIns, oldIns, oldInsChangedAttributeNames, newInsChangedAttributeNames);
    }
}
