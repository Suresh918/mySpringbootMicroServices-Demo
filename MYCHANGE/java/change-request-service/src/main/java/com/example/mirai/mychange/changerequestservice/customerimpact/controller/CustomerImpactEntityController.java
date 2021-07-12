package com.example.mirai.projectname.changerequestservice.customerimpact.controller;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.entity.controller.EntityController;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.projectname.changerequestservice.core.component.EntityResolver;
import com.example.mirai.projectname.changerequestservice.customerimpact.model.CustomerImpact;
import com.example.mirai.projectname.changerequestservice.customerimpact.service.CustomerImpactService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("change-requests/{entityType:customer-impact}")
public class CustomerImpactEntityController extends EntityController {

    @Resource
    CustomerImpactEntityController self;

    private CustomerImpactService customerImpactService;
    private ObjectMapper objectMapper;

    CustomerImpactEntityController(CustomerImpactService customerImpactService, EntityResolver entityResolver, ObjectMapper objectMapper) {
        super(objectMapper, customerImpactService, entityResolver);
        this.customerImpactService = customerImpactService;
        this.objectMapper = objectMapper;
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
        return this.customerImpactService.merge(newIns, oldIns, oldInsChangedAttributeNames, newInsChangedAttributeNames);
    }

    @Override
    public Class<? extends BaseEntityInterface> getEntityClass() {
        return CustomerImpact.class;
    }

    @PatchMapping(value = "/{id}", params = "is-system-account=true")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_tibco')")
    public BaseEntityInterface mergeEntityBySystemUser(@PathVariable Long id, @RequestBody JsonNode jsonNode) throws JsonProcessingException {
        BaseEntityInterface oldIns = this.objectMapper.treeToValue(jsonNode.get("oldIns"), this.getEntityClass());
        BaseEntityInterface newIns = this.objectMapper.treeToValue(jsonNode.get("newIns"), this.getEntityClass());
        oldIns.setId(id);
        newIns.setId(id);
        List<String> oldInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("oldIns"));
        List<String> newInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("newIns"));
        return this.customerImpactService.mergeEntityBySystemUser(newIns, oldIns, oldInsChangedAttributeNames, newInsChangedAttributeNames);
    }
}
