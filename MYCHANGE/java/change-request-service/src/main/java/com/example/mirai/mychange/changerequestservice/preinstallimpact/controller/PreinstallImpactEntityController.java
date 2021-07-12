package com.example.mirai.projectname.changerequestservice.preinstallimpact.controller;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.entity.controller.EntityController;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestAggregate;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestDetail;
import com.example.mirai.projectname.changerequestservice.core.component.EntityResolver;
import com.example.mirai.projectname.changerequestservice.preinstallimpact.model.PreinstallImpact;
import com.example.mirai.projectname.changerequestservice.preinstallimpact.service.PreinstallImpactService;
import com.example.mirai.projectname.changerequestservice.solutiondefinition.controller.SolutionDefinitionEntityController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("change-requests/{entityType:preinstall-impact}")
public class PreinstallImpactEntityController extends EntityController {

    @Resource
    SolutionDefinitionEntityController self;

    private PreinstallImpactService preinstallImpactService;
    private ObjectMapper objectMapper;

    PreinstallImpactEntityController(PreinstallImpactService preinstallImpactService, EntityResolver entityResolver, ObjectMapper objectMapper) {
        super(objectMapper, preinstallImpactService, entityResolver);
        this.preinstallImpactService = preinstallImpactService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Class<? extends BaseEntityInterface> getEntityClass() {
        return PreinstallImpact.class;
    }

    @SneakyThrows
    @PutMapping(value= "/{id}", params = "view=change-request-detail")
    @ResponseStatus(HttpStatus.OK)
    public ChangeRequestDetail updatePreinstallImpact(@PathVariable Long id, @RequestBody JsonNode jsonNode) {
        Map<String, Object> newInsChangedAttrs = ObjectMapperUtil.getChangedAttributes(jsonNode);
        BaseEntityInterface entityIns = objectMapper.treeToValue(jsonNode, PreinstallImpact.class);
        entityIns.setId(id);
        ChangeRequestAggregate changeRequestAggregate = preinstallImpactService.updatePreinstallImpact(entityIns, newInsChangedAttrs);
        return preinstallImpactService.getChangeRequestDetail(changeRequestAggregate);
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
        return this.preinstallImpactService.merge(newIns, oldIns, oldInsChangedAttributeNames, newInsChangedAttributeNames);
    }
}
