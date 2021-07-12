package com.example.mirai.projectname.changerequestservice.scope.controller;

import com.example.mirai.libraries.core.annotation.SecurePropertyRead;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.entity.controller.EntityController;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestAggregate;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestDetail;
import com.example.mirai.projectname.changerequestservice.core.component.EntityResolver;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.dto.CustomerImpactDetail;
import com.example.mirai.projectname.changerequestservice.scope.model.Scope;
import com.example.mirai.projectname.changerequestservice.scope.service.ScopeService;
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
@RequestMapping("change-requests/{entityType:scope}")
public class ScopeEntityController extends EntityController {

    @Resource
    ScopeEntityController self;

    private final ScopeService scopeService;
    private final ObjectMapper objectMapper;

    ScopeEntityController(ScopeService scopeService, EntityResolver entityResolver, ObjectMapper objectMapper) {
        super(objectMapper, scopeService, entityResolver);
        this.scopeService = scopeService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Class<? extends BaseEntityInterface> getEntityClass() {
        return Scope.class;
    }

    @SneakyThrows
    @PutMapping(value = "/{id}", params = "view=change-request-detail")
    @ResponseStatus(HttpStatus.OK)
    public ChangeRequestDetail updateScope(@PathVariable Long id, @RequestBody JsonNode jsonNode)  {
        Map<String, Object> newInsChangedAttrs = ObjectMapperUtil.getChangedAttributes(jsonNode);
        BaseEntityInterface entityIns = objectMapper.treeToValue(jsonNode, Scope.class);
        entityIns.setId(id);
        Scope updatedScope = scopeService.updateScope((Scope) entityIns, newInsChangedAttrs);
        CustomerImpactDetail customerImpactDetail = scopeService.evaluateCustomerImpact(updatedScope);
        ChangeRequestAggregate changeRequestAggregate = self.getChangeRequestAggregate(updatedScope);
        return new ChangeRequestDetail(changeRequestAggregate, customerImpactDetail);
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
        return this.scopeService.merge(newIns, oldIns, oldInsChangedAttributeNames, newInsChangedAttributeNames);
    }
    @SecurePropertyRead
    public ChangeRequestAggregate getChangeRequestAggregate(Scope scope) {
        return scopeService.getChangeRequestAggregate(scope);
    }
}
