package com.example.mirai.projectname.releasepackageservice.releasepackage.controller;

import com.example.mirai.libraries.core.annotation.SecurePropertyRead;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.entity.controller.ParentEntityController;
import com.example.mirai.projectname.releasepackageservice.core.component.EntityResolver;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate.ReleasePackageAggregate;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.ReleasePackageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("release-packages")
public class ReleasePackageParentEntityController extends ParentEntityController {

    @Autowired
    ReleasePackageService releasePackageService;

    ReleasePackageParentEntityController(ReleasePackageService releasePackageService, EntityResolver entityResolver,
                                         ObjectMapper objectMapper) {
        super(objectMapper, releasePackageService, entityResolver);
    }

    @Override
    public Class<? extends BaseEntityInterface> getEntityClass() {
        return ReleasePackage.class;
    }

    @PostMapping("/aggregate")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @SecurePropertyRead
    public ReleasePackageAggregate createAggregate(@RequestBody JsonNode jsonNode) throws JsonProcessingException {
        ReleasePackageAggregate releasePackageAggregate = this.objectMapper.treeToValue(jsonNode, ReleasePackageAggregate.class);
        return releasePackageService.createAggregate(releasePackageAggregate);
    }

}
