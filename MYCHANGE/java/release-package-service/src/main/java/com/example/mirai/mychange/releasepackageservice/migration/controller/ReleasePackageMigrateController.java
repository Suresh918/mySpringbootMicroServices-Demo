package com.example.mirai.projectname.releasepackageservice.migration.controller;

import java.util.Date;
import java.util.List;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.projectname.releasepackageservice.comment.service.ReleasePackageCommentService;
import com.example.mirai.projectname.releasepackageservice.core.component.EntityResolver;
import com.example.mirai.projectname.releasepackageservice.migration.model.FieldUpdate;
import com.example.mirai.projectname.releasepackageservice.migration.model.ReleasePackageMigrate;
import com.example.mirai.projectname.releasepackageservice.migration.model.ReleasePackageWithComments;
import com.example.mirai.projectname.releasepackageservice.migration.service.ReleasePackageMigrateService;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/migrate/release-packages")
public class ReleasePackageMigrateController {

    ReleasePackageMigrateService releasePackageMigrateService;
    ReleasePackageCommentService releasePackageCommentService;
    EntityResolver entityResolver;
    ObjectMapper objectMapper;

    public ReleasePackageMigrateController(ReleasePackageMigrateService releasePackageMigrateService,
                                           ReleasePackageCommentService releasePackageCommentService,
                                           EntityResolver entityResolver, ObjectMapper objectMapper) {
        this.releasePackageCommentService = releasePackageCommentService;
        this.releasePackageMigrateService = releasePackageMigrateService;
        this.entityResolver = entityResolver;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ReleasePackage create(@RequestBody JsonNode jsonNode) throws JsonProcessingException {
        ReleasePackageMigrate releasePackageMigrate = objectMapper.treeToValue(jsonNode, ReleasePackageMigrate.class);
        ReleasePackage releasePackage = releasePackageMigrateService.createReleasePackage(releasePackageMigrate);
        releasePackageMigrateService.updateReleasePackageAudit(releasePackage, releasePackageMigrate);
        return (ReleasePackage) releasePackageMigrateService.getEntityById(releasePackage.getId());
    }

    @PatchMapping({"/{releasePackageId}"})
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ReleasePackage mergeReleasePackage(@PathVariable Long releasePackageId, @RequestBody JsonNode jsonNode) throws JsonProcessingException {
        BaseEntityInterface newIns = this.objectMapper.treeToValue(jsonNode.get("new_ins"), ReleasePackage.class);
        newIns.setId(releasePackageId);
        List<String> newInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("new_ins"));
        ReleasePackage releasePackage = (ReleasePackage) releasePackageMigrateService.mergeEntity(newIns, newInsChangedAttributeNames);
        FieldUpdate fieldUpdate = objectMapper.convertValue(jsonNode, FieldUpdate.class);
        Date modifiedOn = fieldUpdate.getModifiedOn();
        User modifiedBy = fieldUpdate.getModifiedBy();
        releasePackageMigrateService.updateAuditForMergeEntity(releasePackage, modifiedBy, modifiedOn, "aud_release_package");
        releasePackageMigrateService.updateAuditForCollection(newInsChangedAttributeNames, modifiedOn, releasePackage);
        return releasePackage;
    }

    @PutMapping(value = "/{releasePackageId}", params = "view=aggregate")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ReleasePackageWithComments updateChangeRequestAggregateAndCreateComments(@PathVariable Long releasePackageId, @RequestBody JsonNode jsonNode) throws JsonProcessingException {
        ReleasePackageWithComments releasePackageWithComments = objectMapper.treeToValue(jsonNode, ReleasePackageWithComments.class);
        Integer releasePackagePreviousRevision = releasePackageMigrateService.getReleasePackagePreviousRevision(releasePackageWithComments.getReleasePackageAggregate().getReleasePackage());
        ReleasePackageWithComments updatedReleasePackageWithComments = releasePackageMigrateService.updateReleasePackageAndCreateComments(releasePackageWithComments, releasePackageId);
        releasePackageMigrateService.updateAuditOfReleasePackage(updatedReleasePackageWithComments, releasePackagePreviousRevision);
        return updatedReleasePackageWithComments;
    }
}
