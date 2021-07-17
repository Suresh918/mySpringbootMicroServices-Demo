package com.example.mirai.projectname.releasepackageservice.releasepackage.controller;


import com.example.mirai.projectname.releasepackageservice.comment.service.ReleasePackageCommentService;
import com.example.mirai.projectname.releasepackageservice.core.component.EntityResolver;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate.ReleasePackageDetailWithComments;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.ReleasePackageMigrateServiceOld;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReleasePackageMigrateControllerOld {

    ReleasePackageMigrateServiceOld releasePackageMigrateServiceOld;
    ReleasePackageCommentService releasePackageCommentService;
    EntityResolver entityResolver;
    ObjectMapper objectMapper;

    public ReleasePackageMigrateControllerOld(ReleasePackageMigrateServiceOld releasePackageMigrateServiceOld,
                                              ReleasePackageCommentService releasePackageCommentService,
                                              EntityResolver entityResolver, ObjectMapper objectMapper) {
        this.releasePackageCommentService = releasePackageCommentService;
        this.releasePackageMigrateServiceOld = releasePackageMigrateServiceOld;
        this.entityResolver = entityResolver;
        this.objectMapper = objectMapper;
    }

    @PostMapping({"" +
            "/migrate/release-packages/aggregate"
    })
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ReleasePackageDetailWithComments create(@RequestBody JsonNode jsonNode) throws JsonProcessingException {
        ReleasePackageDetailWithComments releasePackageAggregate = objectMapper.treeToValue(jsonNode, ReleasePackageDetailWithComments.class);
        return releasePackageMigrateServiceOld.createReleasePackageMigrateAggregate(releasePackageAggregate);
    }
}
