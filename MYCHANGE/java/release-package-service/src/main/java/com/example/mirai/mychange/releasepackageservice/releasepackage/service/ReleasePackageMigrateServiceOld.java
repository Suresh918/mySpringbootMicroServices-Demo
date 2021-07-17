package com.example.mirai.projectname.releasepackageservice.releasepackage.service;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.mirai.libraries.audit.service.AuditServiceDefaultInterface;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.CaseStatus;
import com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface;
import com.example.mirai.libraries.security.abac.component.AbacProcessor;
import com.example.mirai.libraries.security.core.component.EntityACL;
import com.example.mirai.libraries.security.core.component.PropertyACL;
import com.example.mirai.libraries.security.rbac.component.RbacProcessor;
import com.example.mirai.projectname.releasepackageservice.comment.model.ReleasePackageComment;
import com.example.mirai.projectname.releasepackageservice.comment.model.ReleasePackageCommentMigrate;
import com.example.mirai.projectname.releasepackageservice.comment.service.ReleasePackageCommentService;
import com.example.mirai.projectname.releasepackageservice.myteam.model.ReleasePackageMyTeam;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate.ReleasePackageAggregate;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate.ReleasePackageDetail;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate.ReleasePackageDetailWithComments;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate.ReleasePackageMyTeamDetailsAggregate;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@EntityClass(ReleasePackage.class)
@Slf4j
public class ReleasePackageMigrateServiceOld implements EntityServiceDefaultInterface,
        AuditServiceDefaultInterface {

    private ReleasePackageStateMachine stateMachine;
    private AbacProcessor abacProcessor;
    private RbacProcessor rbacProcessor;
    private EntityACL acl;
    private PropertyACL pacl;
    private ReleasePackageService releasePackageService;
    private ReleasePackageCommentService releasePackageCommentService;

    public ReleasePackageMigrateServiceOld(ReleasePackageStateMachine stateMachine, AbacProcessor abacProcessor, RbacProcessor rbacProcessor,
                                           EntityACL acl, PropertyACL pacl, ReleasePackageService releasePackageService,
                                           ReleasePackageCommentService releasePackageCommentService) {
        this.stateMachine = stateMachine;
        this.abacProcessor = abacProcessor;
        this.rbacProcessor = rbacProcessor;
        this.acl = acl;
        this.pacl = pacl;
        this.releasePackageService = releasePackageService;
        this.releasePackageCommentService = releasePackageCommentService;
    }

    @Transactional
    public ReleasePackageDetailWithComments createReleasePackageMigrateAggregate(ReleasePackageDetailWithComments request) {
        ReleasePackageDetailWithComments releasePackageDetailWithComments = new ReleasePackageDetailWithComments();
        ReleasePackageDetail releasePackageDetail = request.getReleasePackageDetail();
        List<ReleasePackageCommentMigrate> commentList = request.getComments();
        //process release package aggregate
        ReleasePackageAggregate releasePackageAggregate = new ReleasePackageAggregate();
        releasePackageAggregate.setReleasePackage(getReleasePackage(releasePackageDetail));
        //process myteam aggregate
        ReleasePackageMyTeamDetailsAggregate releasePackageMyTeamAggregate = new ReleasePackageMyTeamDetailsAggregate();
        ReleasePackageMyTeam myTeam = new ReleasePackageMyTeam();
        myTeam.setReleasePackage(releasePackageAggregate.getReleasePackage());
        releasePackageMyTeamAggregate.setMyTeam(myTeam);
        if (Objects.nonNull(request.getReleasePackageDetail().getMyTeam())) {
            releasePackageMyTeamAggregate.setMembers(request.getReleasePackageDetail().getMyTeam().getMembers());
        }
        releasePackageAggregate.setMyTeamDetails(releasePackageMyTeamAggregate);
        //create change request aggregate
        releasePackageAggregate = (ReleasePackageAggregate) EntityServiceDefaultInterface.super.createRootAggregate(releasePackageAggregate);
        //update change request creator info
        releasePackageAggregate.getReleasePackage().setCreatedOn(request.getReleasePackageDetail().getCreatedOn());
        releasePackageAggregate.getReleasePackage().setCreator(request.getReleasePackageDetail().getCreator());
        //create change request detail
        releasePackageDetailWithComments.setReleasePackageDetail(new ReleasePackageDetail(releasePackageAggregate));
        //process comments
        final Long releasePackageId = releasePackageAggregate.getReleasePackage().getId();
        List<ReleasePackageCommentMigrate> createdComments = new ArrayList<>();
        commentList.forEach(comment -> {
            try {
                String commentOldId = comment.getCommentOldId();
                ReleasePackageComment releasePackageComment = new ReleasePackageComment();
                releasePackageComment.setCommentText(comment.getCommentText());
                releasePackageComment.setStatus(comment.getStatus());
                ReleasePackageComment createdComment = (ReleasePackageComment) releasePackageCommentService.createCommentMigrate(releasePackageComment, releasePackageId, ReleasePackage.class, comment.getCreatedOn(), comment.getCreator());
                createdComments.add(new ReleasePackageCommentMigrate(createdComment, commentOldId));
            } catch (ParseException exception) {
            	log.error("Parsing exception occurred", exception);
            }
        });
        releasePackageDetailWithComments.setComments(createdComments);
        return releasePackageDetailWithComments;
    }

    private ReleasePackage getReleasePackage(ReleasePackageDetail releasePackageDetail) {
        ReleasePackage releasePackage = new ReleasePackage();
        releasePackage.setId(releasePackageDetail.getId());
        releasePackage.setTitle(releasePackageDetail.getTitle());
        releasePackage.setTags(releasePackageDetail.getTags());
        releasePackage.setChangeControlBoards(releasePackageDetail.getChangeControlBoards());
        releasePackage.setChangeSpecialist3(releasePackageDetail.getChangeSpecialist3());
        releasePackage.setContexts(releasePackageDetail.getContexts());
        //releasePackage.setCreatedOn(releasePackageDetail.getCreatedOn());
        releasePackage.setCreator(releasePackageDetail.getCreator());
        releasePackage.setExecutor(releasePackageDetail.getExecutor());
        releasePackage.setIsSecure(releasePackageDetail.getIsSecure());
        releasePackage.setPlannedEffectiveDate(releasePackageDetail.getPlannedEffectiveDate());
        releasePackage.setPlannedReleaseDate(releasePackageDetail.getPlannedReleaseDate());
        releasePackage.setPrerequisitesApplicable(releasePackageDetail.getPrerequisitesApplicable());
        releasePackage.setReleasePackageNumber(releasePackageDetail.getReleasePackageNumber());
        releasePackage.setPrerequisitesDetail(releasePackageDetail.getPrerequisitesDetail());
        releasePackage.setProductId(releasePackageDetail.getProductId());
        releasePackage.setProjectId(releasePackageDetail.getProjectId());
        releasePackage.setStatus(releasePackageDetail.getStatus());
        releasePackage.setTypes(releasePackageDetail.getTypes());
        releasePackage.setSapChangeControl(releasePackageDetail.getSapChangeControl());
        releasePackage.setPrerequisiteReleasePackages(releasePackageDetail.getReleasePackagePrerequisites());
        return releasePackage;
    }

    @Override
    public BaseEntityInterface performCaseAction(Long aLong, String s) {
        return null;
    }

    @Override
    public CaseStatus performCaseActionAndGetCaseStatus(Long aLong, String s) {
        return null;
    }

    @Override
    public AggregateInterface performCaseActionAndGetCaseStatusAggregate(Long aLong, String s, Class<AggregateInterface> aClass) {
        return null;
    }

}
