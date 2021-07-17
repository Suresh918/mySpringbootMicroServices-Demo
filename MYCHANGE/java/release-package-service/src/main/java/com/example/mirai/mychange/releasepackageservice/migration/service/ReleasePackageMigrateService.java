package com.example.mirai.projectname.releasepackageservice.migration.service;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.example.mirai.libraries.audit.service.AuditServiceDefaultInterface;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.CaseStatus;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface;
import com.example.mirai.libraries.entity.service.helper.service.ServiceHelper;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.libraries.security.abac.component.AbacProcessor;
import com.example.mirai.libraries.security.core.component.EntityACL;
import com.example.mirai.libraries.security.core.component.PropertyACL;
import com.example.mirai.libraries.security.rbac.component.RbacProcessor;
import com.example.mirai.libraries.util.DaoUtility;
import com.example.mirai.projectname.releasepackageservice.comment.model.ReleasePackageComment;
import com.example.mirai.projectname.releasepackageservice.comment.service.ReleasePackageCommentService;
import com.example.mirai.projectname.releasepackageservice.migration.model.ReleasePackageCommentMigrate;
import com.example.mirai.projectname.releasepackageservice.migration.model.ReleasePackageMigrate;
import com.example.mirai.projectname.releasepackageservice.migration.model.ReleasePackageWithComments;
import com.example.mirai.projectname.releasepackageservice.myteam.model.ReleasePackageMyTeam;
import com.example.mirai.projectname.releasepackageservice.myteam.model.aggregate.ReleasePackageMyTeamMemberAggregate;
import com.example.mirai.projectname.releasepackageservice.myteam.service.ReleasePackageMyTeamService;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate.ReleasePackageAggregate;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate.ReleasePackageMyTeamDetailsAggregate;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.ReleasePackageService;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.ReleasePackageStateMachine;
import com.google.common.base.Throwables;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@EntityClass(ReleasePackage.class)
@Slf4j
public class ReleasePackageMigrateService implements EntityServiceDefaultInterface,
        AuditServiceDefaultInterface {

    private ReleasePackageStateMachine stateMachine;
    private AbacProcessor abacProcessor;
    private RbacProcessor rbacProcessor;
    private EntityACL acl;
    private PropertyACL pacl;
    private ReleasePackageService releasePackageService;
    private ReleasePackageCommentService releasePackageCommentService;

    private final ReleasePackageMyTeamService releasePackageMyTeamService;

    public ReleasePackageMigrateService(ReleasePackageStateMachine stateMachine, AbacProcessor abacProcessor, RbacProcessor rbacProcessor,
                                        EntityACL acl, PropertyACL pacl, ReleasePackageService releasePackageService,
                                        ReleasePackageCommentService releasePackageCommentService, ReleasePackageMyTeamService releasePackageMyTeamService) {
        this.stateMachine = stateMachine;
        this.abacProcessor = abacProcessor;
        this.rbacProcessor = rbacProcessor;
        this.acl = acl;
        this.pacl = pacl;
        this.releasePackageService = releasePackageService;
        this.releasePackageCommentService = releasePackageCommentService;
        this.releasePackageMyTeamService = releasePackageMyTeamService;
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

    @Transactional
    public ReleasePackage createReleasePackage(ReleasePackageMigrate releasePackageMigrate) {
        if (Objects.isNull(releasePackageMigrate.getCreatedOn()) || Objects.isNull(releasePackageMigrate.getCreatedBy()))
            throw new InternalAssertionException("Invalid input");
        ReleasePackageAggregate releasePackageAggregateRequest = new ReleasePackageAggregate();
        ReleasePackage releasePackage = new ReleasePackage();
        releasePackage.setReleasePackageNumber(releasePackageMigrate.getReleasePackageNumber());
        releasePackage.setTitle(releasePackageMigrate.getTitle());
        releasePackageAggregateRequest.setReleasePackage(releasePackage);
        ReleasePackageMyTeamDetailsAggregate releasePackageMyTeamDetailsAggregate = new ReleasePackageMyTeamDetailsAggregate();
        releasePackageMyTeamDetailsAggregate.setMembers(new HashSet<>());
        releasePackageMyTeamDetailsAggregate.setMyTeam(new ReleasePackageMyTeam());
        releasePackageAggregateRequest.setMyTeamDetails(releasePackageMyTeamDetailsAggregate);
        releasePackageAggregateRequest.getReleasePackage().setStatus(1);
        ReleasePackageAggregate releasePackageAggregate = (ReleasePackageAggregate) EntityServiceDefaultInterface.super.createRootAggregate(releasePackageAggregateRequest);
        EntityManager entityManager = (EntityManager) ApplicationContextHolder.getBean(EntityManager.class);
        //update creator and created on
        Query query = entityManager.createNativeQuery("UPDATE release_package set creator_email=?1, creator_abbreviation=?2, creator_department_name=?3, creator_full_name=?4, creator_user_id=?5, created_on=?6 where id=?7");
        query.setParameter(1, releasePackageMigrate.getCreatedBy().getEmail());
        query.setParameter(2, releasePackageMigrate.getCreatedBy().getAbbreviation());
        query.setParameter(3, releasePackageMigrate.getCreatedBy().getDepartmentName());
        query.setParameter(4, releasePackageMigrate.getCreatedBy().getFullName());
        query.setParameter(5, releasePackageMigrate.getCreatedBy().getUserId());
        query.setParameter(6, releasePackageMigrate.getCreatedOn());
        query.setParameter(7, releasePackageAggregate.getReleasePackage().getId());
        query.executeUpdate();
        return (ReleasePackage) releasePackageService.getEntityById(releasePackageAggregate.getReleasePackage().getId());
    }

    @Transactional
    public void updateReleasePackageAudit(ReleasePackage releasePackage, ReleasePackageMigrate releasePackageMigrate) {
        //update creator and created by of release package
        EntityManager entityManager = (EntityManager) ApplicationContextHolder.getBean(EntityManager.class);

        //update creator and created on in aud table
        Query queryForAuditCreatorUpdate = entityManager.createNativeQuery("UPDATE aud_release_package set creator_email=?1, creator_abbreviation=?2, creator_department_name=?3, creator_full_name=?4, creator_user_id=?5, created_on=?6 where id=?7");
        queryForAuditCreatorUpdate.setParameter(1, releasePackageMigrate.getCreatedBy().getEmail());
        queryForAuditCreatorUpdate.setParameter(2, releasePackageMigrate.getCreatedBy().getAbbreviation());
        queryForAuditCreatorUpdate.setParameter(3, releasePackageMigrate.getCreatedBy().getDepartmentName());
        queryForAuditCreatorUpdate.setParameter(4, releasePackageMigrate.getCreatedBy().getFullName());
        queryForAuditCreatorUpdate.setParameter(5, releasePackageMigrate.getCreatedBy().getUserId());
        queryForAuditCreatorUpdate.setParameter(6, releasePackageMigrate.getCreatedOn());
        queryForAuditCreatorUpdate.setParameter(7, releasePackage.getId());
        queryForAuditCreatorUpdate.executeUpdate();

        //get aud of releasepackage
        Query audReleasePackageQuery = entityManager.createNativeQuery("SELECT * FROM aud_release_package WHERE id = ?1 AND revtype=?2");
        audReleasePackageQuery.setParameter(1, releasePackage.getId());
        audReleasePackageQuery.setParameter(2, 0);
        List<Object> releasePackageRevisions = audReleasePackageQuery.getResultList();

        //update aud updater
        if (releasePackageRevisions.size() == 1) {
            Object[] revision = ((Object[]) releasePackageRevisions.get(0));
            Integer revId = (Integer) revision[1];
            try {
                updateAudUpdaterByRevId(revId, releasePackageMigrate.getCreatedBy(), releasePackageMigrate.getCreatedOn());
            } catch (Exception e) {
                throw new InternalAssertionException("Error while updating aud_updater of release package " + releasePackage.getId());
            }
        } else {
            throw new InternalAssertionException("Error while updating aud_updater of release package" + releasePackage.getId());
        }
    }

    private void updateAudUpdaterByRevId(Integer revId, User updatedBy, Date updatedOn) {
        EntityManager entityManager = (EntityManager) ApplicationContextHolder.getBean(EntityManager.class);
        Query updateAudUpdater = entityManager.createNativeQuery("UPDATE aud_updater set email=?1, abbreviation=?2, department_name=?3, full_name=?4, user_id=?5, timestamp =?6 where id=?7");
        updateAudUpdater.setParameter(1, updatedBy.getEmail());
        updateAudUpdater.setParameter(2, updatedBy.getAbbreviation());
        updateAudUpdater.setParameter(3, updatedBy.getDepartmentName());
        updateAudUpdater.setParameter(4, updatedBy.getFullName());
        updateAudUpdater.setParameter(5, updatedBy.getUserId());
        updateAudUpdater.setParameter(6, updatedOn.getTime());
        updateAudUpdater.setParameter(7, revId);
        updateAudUpdater.executeUpdate();
    }

    /////MERGE/////

    @SneakyThrows
    @Transactional
    public BaseEntityInterface mergeEntity(BaseEntityInterface newInst, List<String> newInsChangedAttributeNames) {
        JpaRepository jpaRepository = ApplicationContextHolder.getJpaRepository(newInst.getClass());
        BaseEntityInterface readInst = (BaseEntityInterface)jpaRepository.getOne(newInst.getId());
        readInst = DaoUtility.initializeAndUnproxy(readInst);
        Map<String, Object> readInstMap = (Map) ObjectMapperUtil.getObjectMapper().convertValue(DaoUtility.initializeAndUnproxy(readInst), Map.class);
        Map<String, Object> newInstMap = (Map)ObjectMapperUtil.getObjectMapper().convertValue(newInst, Map.class);
        newInsChangedAttributeNames.stream().forEach((fieldName) -> {
            Object value = newInstMap.get(fieldName);
            readInstMap.put(fieldName, value);
        });
        BaseEntityInterface updatedInst = ObjectMapperUtil.getObjectMapper().convertValue(readInstMap, newInst.getClass());
        ServiceHelper.updateJSONIgnoreFields(readInst, updatedInst);
        BaseEntityInterface savedInst = (BaseEntityInterface)jpaRepository.save(updatedInst);
        return DaoUtility.initializeAndUnproxy(savedInst);
    }

    @Transactional
    public void updateAuditForMergeEntity(BaseEntityInterface entity, User modifiedBy, Date modifiedOn, String tableName) {
        EntityManager entityManager = (EntityManager) ApplicationContextHolder.getBean(EntityManager.class);
        //get aud of release package
        Query audReleasePackageQuery = entityManager.createNativeQuery("SELECT * FROM " + tableName + " WHERE id = ?1 AND (revtype=?2 OR revtype=?3) order by rev desc");
        audReleasePackageQuery.setParameter(1, entity.getId());
        audReleasePackageQuery.setParameter(2, 0);
        audReleasePackageQuery.setParameter(3, 1);
        List<Object> revisions = audReleasePackageQuery.getResultList();

        //update aud updater
        if (revisions.size() > 1) {
            Object[] revision = ((Object[]) revisions.get(0));
            Integer revId = (Integer) revision[1];
            try {
                updateAudUpdaterByRevId(revId, modifiedBy, modifiedOn);
            } catch (Exception e) {
                throw new InternalAssertionException("Error while updating aud_updater of release package AudUpdater" + entity.getId());
            }
            //update revend and revend of previous version
            Object[] previousRevision = ((Object[]) revisions.get(1));
            Integer previousRevId = (Integer) previousRevision[1];
            try {
                updateRevEndTimestamp(previousRevId, modifiedOn, tableName);
            } catch (Exception e) {
                throw new InternalAssertionException("Error while updating aud_updater of release package RevEndTimestamp" + entity.getId());
            }
        } else {
            throw new InternalAssertionException("Error while updating aud_updater of release package " + entity.getId());
        }
    }

    private void updateRevEndTimestamp(Integer revId, Date revEndTimestamp, String tableName) {
        EntityManager entityManager = (EntityManager) ApplicationContextHolder.getBean(EntityManager.class);
        Query updateAudUpdater = entityManager.createNativeQuery("UPDATE " + tableName + " set revend_tstmp =?1 where rev=?2");
        updateAudUpdater.setParameter(1, revEndTimestamp);
        updateAudUpdater.setParameter(2, revId);
        updateAudUpdater.executeUpdate();
    }

///// Rp  Update with comments

    @Transactional
    public ReleasePackageWithComments updateReleasePackageAndCreateComments(ReleasePackageWithComments request, Long releasePackageId) {
        ReleasePackageWithComments releasePackageWithComments = new ReleasePackageWithComments();
        ReleasePackage releasePackage = request.getReleasePackageAggregate().getReleasePackage();
        List<ReleasePackageCommentMigrate> commentList = request.getComments();

        //update release package
        releasePackageService.update(releasePackage);


        //process myteam
        ReleasePackageAggregate releasePackageAggregate = request.getReleasePackageAggregate();
        ReleasePackageMyTeamDetailsAggregate releasePackageMyTeamAggregate = releasePackageAggregate.getMyTeamDetails();
        Long changeRequestMyTeamId = releasePackageMyTeamService.getMyTeamIdByLinkedEntity(releasePackageId);
        for (ReleasePackageMyTeamMemberAggregate memberAggregate : releasePackageMyTeamAggregate.getMembers()) {
            releasePackageMyTeamService.addMyTeamMemberForMigration(changeRequestMyTeamId, memberAggregate.getMember());
        }
        //process comments
        List<ReleasePackageCommentMigrate> createdComments = new ArrayList<>();
        if (Objects.nonNull(commentList)) {
            commentList.forEach(comment -> {
                try {
                    String commentOldId = comment.getCommentOldId();
                    ReleasePackageComment releasePackageComment1 = new ReleasePackageComment();
                    releasePackageComment1.setCommentText(comment.getCommentText());
                    releasePackageComment1.setStatus(comment.getStatus());
                    ReleasePackageComment releasePackageComment = (ReleasePackageComment) releasePackageCommentService.createCommentMigrate(releasePackageComment1, releasePackageId, ReleasePackage.class, comment.getCreatedOn(), comment.getCreator());
                    createdComments.add(new ReleasePackageCommentMigrate(releasePackageComment, commentOldId));
                } catch (ParseException exception) {
					log.error(Throwables.getStackTraceAsString(exception));
                }
            });
        }
        ReleasePackageAggregate aggregate = releasePackageService.getAggregate(releasePackageId);
        releasePackageWithComments.setReleasePackageAggregate(aggregate);
        releasePackageWithComments.setComments(createdComments);
        releasePackageWithComments.setModifiedOn(request.getModifiedOn());
        releasePackageWithComments.setModifiedBy(request.getModifiedBy());
        return releasePackageWithComments;
    }

    public Integer getReleasePackagePreviousRevision(ReleasePackage releasePackage) {
        return getLatestRevision("aud_release_package", releasePackage);
    }

    public Integer getLatestRevision(String tableName, BaseEntityInterface entity) {
        EntityManager entityManager = (EntityManager) ApplicationContextHolder.getBean(EntityManager.class);
        //get aud of entity
        Query audChangeRequestQuery = entityManager.createNativeQuery("SELECT * FROM " + tableName + " WHERE id = ?1 AND (revtype=?2 OR revtype=?3) order by rev desc");
        audChangeRequestQuery.setParameter(1, entity.getId());
        audChangeRequestQuery.setParameter(2, 0);
        audChangeRequestQuery.setParameter(3, 1);
        List<Object> revisions = audChangeRequestQuery.getResultList();
        Object[] revision = ((Object[]) revisions.get(0));
        return (Integer) revision[1];
    }


    @Transactional
    public void updateAuditOfReleasePackage(ReleasePackageWithComments request, Integer releasePackagePreviousRevision) {
        ReleasePackage releasePackage = request.getReleasePackageAggregate().getReleasePackage();
        Integer releasePackageLatestRevision = getLatestRevision("aud_release_package", releasePackage);
        if (!Objects.equals(releasePackageLatestRevision, releasePackagePreviousRevision)) {
            if (Objects.nonNull(releasePackagePreviousRevision))
                updateRevEndTimestamp(releasePackagePreviousRevision, request.getModifiedOn(), "aud_release_package");
            updateAudUpdaterByRevId(releasePackageLatestRevision, request.getModifiedBy(), request.getModifiedOn());
        }
    }

    @Transactional
    public void updateAuditForCollection(List<String> newInsChangedAttributeNames, Date modifiedOn, ReleasePackage releasePackage) {
        if (newInsChangedAttributeNames.isEmpty()) {
            return;
        }
        String fieldName = newInsChangedAttributeNames.get(0);
        if (!isFieldOfTypeCollection(fieldName)) {
            return;
        }
        String tableName = "aud_" + fieldName;
        if (Objects.nonNull(fieldName) && fieldName.equalsIgnoreCase("contexts"))
            tableName = "aud_release_package_contexts";
        EntityManager entityManager = (EntityManager) ApplicationContextHolder.getBean(EntityManager.class);

        //get latest revision id
        Query audCollectionSelectQuery = entityManager.createNativeQuery("select * FROM " + tableName + " WHERE id = ?1 order by rev desc");
        audCollectionSelectQuery.setParameter(1, releasePackage.getId());
        List<Object> auditRevisions = audCollectionSelectQuery.getResultList();
        Integer latestRevisionId = null;
        if (!auditRevisions.isEmpty()) {
            Object[] latestRevision = ((Object[]) auditRevisions.get(0));
            latestRevisionId = (Integer) latestRevision[0];
        }
        if (Objects.isNull(latestRevisionId))
            return;
        //update revend of previous revision
        Query audCollectionQuery = entityManager.createNativeQuery("select * FROM " + tableName + " WHERE id = ?1 AND revend=?2 order by rev desc");
        audCollectionQuery.setParameter(1, releasePackage.getId());
        audCollectionQuery.setParameter(2, latestRevisionId);
        List<Object> revisions = audCollectionQuery.getResultList();
        if (!revisions.isEmpty()) {
            for (Object revision: revisions) {
                Object[] previousRevision = ((Object[]) revision);
                Integer previousRevId = (Integer) previousRevision[0];
                updateRevEndTimestamp(previousRevId, modifiedOn, tableName);
            }
        }
    }

    public Boolean isFieldOfTypeCollection(String fieldName) {
        List<String> collectionFields = new ArrayList<>(Arrays.asList(new String[]{"contexts", "change_control_boards", "prerequisite_release_packages", "tags", "types"}));
        return collectionFields.contains(fieldName);
    }
}
