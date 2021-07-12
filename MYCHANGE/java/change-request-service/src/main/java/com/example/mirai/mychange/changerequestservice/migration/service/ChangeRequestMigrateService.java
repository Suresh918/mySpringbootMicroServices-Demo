package com.example.mirai.projectname.changerequestservice.migration.service;

import com.example.mirai.libraries.audit.service.AuditServiceDefaultInterface;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.CaseStatus;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.entity.model.Id;
import com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface;
import com.example.mirai.libraries.entity.service.helper.service.ServiceHelper;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.libraries.security.abac.AbacAwareInterface;
import com.example.mirai.libraries.security.abac.component.AbacProcessor;
import com.example.mirai.libraries.security.core.component.CaseActionList;
import com.example.mirai.libraries.security.core.component.EntityACL;
import com.example.mirai.libraries.security.core.component.PropertyACL;
import com.example.mirai.libraries.security.core.service.SecurityServiceDefaultInterface;
import com.example.mirai.libraries.security.rbac.RbacAwareInterface;
import com.example.mirai.libraries.security.rbac.component.RbacProcessor;
import com.example.mirai.libraries.util.DaoUtility;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestAggregate;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestInitializer;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestService;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestStateMachine;
import com.example.mirai.projectname.changerequestservice.comment.model.ChangeRequestComment;
import com.example.mirai.projectname.changerequestservice.comment.service.ChangeRequestCommentService;
import com.example.mirai.projectname.changerequestservice.completebusinesscase.model.CompleteBusinessCase;
import com.example.mirai.projectname.changerequestservice.completebusinesscase.service.CompleteBusinessCaseService;
import com.example.mirai.projectname.changerequestservice.customerimpact.model.CustomerImpact;
import com.example.mirai.projectname.changerequestservice.customerimpact.service.CustomerImpactService;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
import com.example.mirai.projectname.changerequestservice.impactanalysis.service.ImpactAnalysisService;
import com.example.mirai.projectname.changerequestservice.migration.model.ChangeRequestAggregateWithComments;
import com.example.mirai.projectname.changerequestservice.migration.model.ChangeRequestCommentMigrate;
import com.example.mirai.projectname.changerequestservice.migration.model.ChangeRequestMigrate;
import com.example.mirai.projectname.changerequestservice.migration.model.EntitiesPreviousRevision;
import com.example.mirai.projectname.changerequestservice.myteam.model.aggregate.ChangeRequestMyTeamDetailsAggregate;
import com.example.mirai.projectname.changerequestservice.myteam.model.aggregate.ChangeRequestMyTeamMemberAggregate;
import com.example.mirai.projectname.changerequestservice.myteam.service.ChangeRequestMyTeamService;
import com.example.mirai.projectname.changerequestservice.preinstallimpact.model.PreinstallImpact;
import com.example.mirai.projectname.changerequestservice.preinstallimpact.service.PreinstallImpactService;
import com.example.mirai.projectname.changerequestservice.scope.model.Scope;
import com.example.mirai.projectname.changerequestservice.scope.service.ScopeService;
import com.example.mirai.projectname.changerequestservice.solutiondefinition.model.SolutionDefinition;
import com.example.mirai.projectname.changerequestservice.solutiondefinition.service.SolutionDefinitionService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.ParseException;
import java.util.*;


@Service
@EntityClass(ChangeRequest.class)
@Slf4j
public class ChangeRequestMigrateService implements EntityServiceDefaultInterface, SecurityServiceDefaultInterface, AuditServiceDefaultInterface {

    @Resource
    ChangeRequestMigrateService self;
    private final ChangeRequestStateMachine stateMachine;
    private final AbacProcessor abacProcessor;
    private final RbacProcessor rbacProcessor;
    private final EntityACL acl;
    private final PropertyACL pacl;

    private final ChangeRequestInitializer changeRequestInitializer;
    private final ChangeRequestService changeRequestService;
    private final ChangeRequestCommentService changeRequestCommentService;
    private final SolutionDefinitionService solutionDefinitionService;
    private final ImpactAnalysisService impactAnalysisService;
    private final ScopeService scopeService;
    private final CustomerImpactService customerImpactService;
    private final PreinstallImpactService preinstallImpactService;
    private final CompleteBusinessCaseService completeBusinessCaseService;
    private final ChangeRequestMyTeamService changeRequestMyTeamService;

    public ChangeRequestMigrateService(ChangeRequestStateMachine stateMachine, AbacProcessor abacProcessor, RbacProcessor rbacProcessor, EntityACL acl, PropertyACL pacl, ChangeRequestInitializer changeRequestInitializer, ChangeRequestService changeRequestService, ChangeRequestCommentService changeRequestCommentService, SolutionDefinitionService solutionDefinitionService, ImpactAnalysisService impactAnalysisService, ScopeService scopeService, CustomerImpactService customerImpactService, PreinstallImpactService preinstallImpactService, CompleteBusinessCaseService completeBusinessCaseService, ChangeRequestMyTeamService changeRequestMyTeamService) {
        this.stateMachine = stateMachine;
        this.abacProcessor = abacProcessor;
        this.rbacProcessor = rbacProcessor;
        this.acl = acl;
        this.pacl = pacl;
        this.changeRequestInitializer = changeRequestInitializer;
        this.changeRequestService = changeRequestService;
        this.changeRequestCommentService = changeRequestCommentService;
        this.solutionDefinitionService = solutionDefinitionService;
        this.impactAnalysisService = impactAnalysisService;
        this.scopeService = scopeService;
        this.customerImpactService = customerImpactService;
        this.preinstallImpactService = preinstallImpactService;
        this.completeBusinessCaseService = completeBusinessCaseService;
        this.changeRequestMyTeamService = changeRequestMyTeamService;
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

    @Override
    public CaseStatus getCaseStatus(BaseEntityInterface baseEntityInterface) {
        return null;
    }

    @Override
    public AbacAwareInterface getABACAware() {
        return null;
    }

    @Override
    public RbacAwareInterface getRBACAware() {
        return null;
    }

    @Override
    public EntityACL getEntityACL() {
        return acl;
    }

    @Override
    public PropertyACL getPropertyACL() {
        return pacl;
    }

    @Override
    public CaseActionList getCaseActionList() {
        return null;
    }



    @Transactional
    public ChangeRequest createChangeRequestMigrateAggregate(ChangeRequestMigrate changeRequestMigrate) {
        if (Objects.isNull(changeRequestMigrate.getCreatedOn()) || Objects.isNull(changeRequestMigrate.getCreatedBy()))
            throw new InternalAssertionException("Invalid input");
        ChangeRequestAggregate changeRequestAggregateRequest = new ChangeRequestAggregate();
        changeRequestInitializer.initiateLinkedEntities(changeRequestAggregateRequest);
        changeRequestAggregateRequest.getDescription().setStatus(1);
        changeRequestAggregateRequest.getDescription().setTitle(changeRequestMigrate.getTitle());
        ChangeRequestAggregate changeRequestAggregate = (ChangeRequestAggregate) EntityServiceDefaultInterface.super.createRootAggregate(changeRequestAggregateRequest);
        EntityManager entityManager = (EntityManager) ApplicationContextHolder.getBean(EntityManager.class);
        //update creator and created on
        Query query = entityManager.createNativeQuery("UPDATE change_request set creator_email=?1, creator_abbreviation=?2, creator_department_name=?3, creator_full_name=?4, creator_user_id=?5, created_on=?6 where id=?7");
        query.setParameter(1, changeRequestMigrate.getCreatedBy().getEmail());
        query.setParameter(2, changeRequestMigrate.getCreatedBy().getAbbreviation());
        query.setParameter(3, changeRequestMigrate.getCreatedBy().getDepartmentName());
        query.setParameter(4, changeRequestMigrate.getCreatedBy().getFullName());
        query.setParameter(5, changeRequestMigrate.getCreatedBy().getUserId());
        query.setParameter(6, changeRequestMigrate.getCreatedOn());
        query.setParameter(7, changeRequestAggregate.getDescription().getId());
        query.executeUpdate();
        ChangeRequest changeRequest = changeRequestAggregate.getDescription();
        //self.cacheChangeRequest(changeRequest);
        return (ChangeRequest) changeRequestService.getEntityById(changeRequest.getId());
    }

    @Transactional
    public void updateChangeRequestAudit(ChangeRequest changeRequest, ChangeRequestMigrate changeRequestMigrate) {
        //update creator and created by of change request
        EntityManager entityManager = (EntityManager) ApplicationContextHolder.getBean(EntityManager.class);

        //update creator and created on in aud table
        Query queryForAuditCreatorUpdate = entityManager.createNativeQuery("UPDATE aud_change_request set creator_email=?1, creator_abbreviation=?2, creator_department_name=?3, creator_full_name=?4, creator_user_id=?5, created_on=?6 where id=?7");
        queryForAuditCreatorUpdate.setParameter(1, changeRequestMigrate.getCreatedBy().getEmail());
        queryForAuditCreatorUpdate.setParameter(2, changeRequestMigrate.getCreatedBy().getAbbreviation());
        queryForAuditCreatorUpdate.setParameter(3, changeRequestMigrate.getCreatedBy().getDepartmentName());
        queryForAuditCreatorUpdate.setParameter(4, changeRequestMigrate.getCreatedBy().getFullName());
        queryForAuditCreatorUpdate.setParameter(5, changeRequestMigrate.getCreatedBy().getUserId());
        queryForAuditCreatorUpdate.setParameter(6, changeRequestMigrate.getCreatedOn());
        queryForAuditCreatorUpdate.setParameter(7, changeRequest.getId());
        queryForAuditCreatorUpdate.executeUpdate();

        //get aud of change request
        Query audChangeRequestQuery = entityManager.createNativeQuery("SELECT * FROM aud_change_request WHERE id = ?1 AND revtype=?2");
        audChangeRequestQuery.setParameter(1, changeRequest.getId());
        audChangeRequestQuery.setParameter(2, 0);
        List<Object> changeRequestRevisions = audChangeRequestQuery.getResultList();

        //update aud updater
        if (changeRequestRevisions.size() == 1) {
            Object[] revision = ((Object[]) changeRequestRevisions.get(0));
            Integer revId = (Integer) revision[1];
            try {
                updateAudUpdaterAndTimestampByRevId(revId, changeRequestMigrate.getCreatedBy(), changeRequestMigrate.getCreatedOn());
            } catch (Exception e) {
                throw new InternalAssertionException("Error while updating aud_updater of change request " + changeRequest.getId());
            }
        } else {
            throw new InternalAssertionException("Error while updating aud_updater of change request " + changeRequest.getId());
        }

    }

    private void updateAudUpdaterAndTimestampByRevId(Integer revId, User updatedBy, Date updatedOn) {
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

//////// MERGE //////////


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
        //get aud of change request
        Query audChangeRequestQuery = entityManager.createNativeQuery("SELECT * FROM " + tableName + " WHERE id = ?1 AND (revtype=?2 OR revtype=?3) order by rev desc");
        audChangeRequestQuery.setParameter(1, entity.getId());
        audChangeRequestQuery.setParameter(2, 0);
        audChangeRequestQuery.setParameter(3, 1);
        List<Object> revisions = audChangeRequestQuery.getResultList();

        //update aud updater
        if (revisions.size() > 1) {
            Object[] revision = ((Object[]) revisions.get(0));
            Integer revId = (Integer) revision[1];
            try {
                updateAudUpdaterAndTimestampByRevId(revId, modifiedBy, modifiedOn);
            } catch (Exception e) {
                throw new InternalAssertionException("Error while updating aud_updater of change request " + entity.getId());
            }
            //update revend and revend of previous version
            Object[] previousRevision = ((Object[]) revisions.get(1));
            Integer previousRevId = (Integer) previousRevision[1];
            try {
                updateRevEndTimestamp(previousRevId, modifiedOn, tableName);
            } catch (Exception e) {
                throw new InternalAssertionException("Error while updating aud_updater of change request " + entity.getId());
            }
        } else {
            throw new InternalAssertionException("Error while updating aud_updater of change request " + entity.getId());
        }
    }

    private void updateRevEndTimestamp(Integer revId, Date revEndTimestamp, String tableName) {
        EntityManager entityManager = (EntityManager) ApplicationContextHolder.getBean(EntityManager.class);
        Query updateAudUpdater = entityManager.createNativeQuery("UPDATE " + tableName + " set revend_tstmp =?1 where rev=?2");
        updateAudUpdater.setParameter(1, revEndTimestamp);
        updateAudUpdater.setParameter(2, revId);
        updateAudUpdater.executeUpdate();
    }

    public Long getSolutionDefinitionIdByChangeRequestId(Long changeRequestId) {
        Slice<Id> idSlice = solutionDefinitionService.filterIds("changeRequest.id: " + changeRequestId, PageRequest.of(0, 1));
        if (Objects.isNull(idSlice) || idSlice.getNumberOfElements() != 1)
            throw  new InternalAssertionException("No Solution Definition / multiple solution definitions found with changeRequest Id " + changeRequestId);
        return idSlice.getContent().get(0).getValue();
    }

    public Long getImpactAnalysisIdByChangeRequestId(Long changeRequestId) {
        Slice<Id> idSlice = impactAnalysisService.filterIds("changeRequest.id: " + changeRequestId, PageRequest.of(0, 1));
        if (Objects.isNull(idSlice) || idSlice.getNumberOfElements() != 1)
            throw  new InternalAssertionException("No Impact Analysis / multiple Impact Analysis found with changeRequest Id " + changeRequestId);
        return idSlice.getContent().get(0).getValue();
    }

    public Long getScopeIdByChangeRequestId(Long changeRequestId) {
        Slice<Id> idSlice = scopeService.filterIds("changeRequest.id: " + changeRequestId, PageRequest.of(0, 1));
        if (Objects.isNull(idSlice) || idSlice.getNumberOfElements() != 1)
            throw  new InternalAssertionException("No Scope / multiple scope found with changeRequest Id " + changeRequestId);
        return idSlice.getContent().get(0).getValue();
    }

    public Long getCustomerImpactIdByChangeRequestId(Long changeRequestId) {
        Long impactAnalysisId = getImpactAnalysisIdByChangeRequestId(changeRequestId);
        Slice<Id> idSlice = customerImpactService.filterIds("impactAnalysis.id: " + impactAnalysisId, PageRequest.of(0, 1));
        if (Objects.isNull(idSlice) || idSlice.getNumberOfElements() != 1)
            throw  new InternalAssertionException("No Customer Impact / multiple Customer Impacts found with changeRequest Id " + changeRequestId);
        return idSlice.getContent().get(0).getValue();
    }

    public Long getPreinstallImpactIdByChangeRequestId(Long changeRequestId) {
        Long impactAnalysisId = getImpactAnalysisIdByChangeRequestId(changeRequestId);
        Slice<Id> idSlice = preinstallImpactService.filterIds("impactAnalysis.id: " + impactAnalysisId, PageRequest.of(0, 1));
        if (Objects.isNull(idSlice) || idSlice.getNumberOfElements() != 1)
            throw  new InternalAssertionException("No Preinstall Impact/ multiple Preinstall Impact found with changeRequest Id " + changeRequestId);
        return idSlice.getContent().get(0).getValue();
    }

    public Long getCompleteBusinessCaseIdByChangeRequestId(Long changeRequestId) {
        Long impactAnalysisId = getImpactAnalysisIdByChangeRequestId(changeRequestId);
        Slice<Id> idSlice = completeBusinessCaseService.filterIds("impactAnalysis.id: " + impactAnalysisId, PageRequest.of(0, 1));
        if (Objects.isNull(idSlice) || idSlice.getNumberOfElements() != 1)
            throw  new InternalAssertionException("No Complete Business Case / multiple Complete Business Case found with changeRequest Id " + changeRequestId);
        return idSlice.getContent().get(0).getValue();
    }

    ///// Aggregate Update

    @Transactional
    public ChangeRequestAggregateWithComments updateChangeRequestAggregateAndCreateComments(ChangeRequestAggregateWithComments request, Long changeRequestId) {
        ChangeRequestAggregateWithComments changeRequestAggregateWithComments = new ChangeRequestAggregateWithComments();
        ChangeRequestAggregate changeRequestAggregate = request.getChangeRequestAggregate();
        List<ChangeRequestCommentMigrate> commentList = request.getComments();
        //update change request aggregate

        changeRequestService.update(changeRequestAggregate.getDescription());
        Scope scope = changeRequestAggregate.getScope();
        scope.setChangeRequest(changeRequestAggregate.getDescription());
        scopeService.update(scope);
        SolutionDefinition solutionDefinition = changeRequestAggregate.getSolutionDefinition();
        solutionDefinition.setChangeRequest(changeRequestAggregate.getDescription());
        solutionDefinitionService.update(changeRequestAggregate.getSolutionDefinition());
        ImpactAnalysis impactAnalysis = changeRequestAggregate.getImpactAnalysis().getGeneral();
        impactAnalysis.setChangeRequest(changeRequestAggregate.getDescription());
        impactAnalysisService.update(impactAnalysis);
        CustomerImpact customerImpact = changeRequestAggregate.getImpactAnalysis().getDetails().getCustomerImpact();
        customerImpact.setImpactAnalysis(impactAnalysis);
        customerImpactService.update(customerImpact);
        PreinstallImpact preinstallImpact = changeRequestAggregate.getImpactAnalysis().getDetails().getPreinstallImpact();
        preinstallImpact.setImpactAnalysis(impactAnalysis);
        preinstallImpactService.update(preinstallImpact);
        CompleteBusinessCase completeBusinessCase = changeRequestAggregate.getImpactAnalysis().getDetails().getCompleteBusinessCase();
        completeBusinessCase.setImpactAnalysis(impactAnalysis);
        completeBusinessCaseService.update(completeBusinessCase);

        //process myteam

        ChangeRequestMyTeamDetailsAggregate changeRequestMyTeamAggregate = changeRequestAggregate.getMyTeamDetails();
        Long changeRequestMyTeamId = changeRequestMyTeamService.getMyTeamIdByLinkedEntity(changeRequestId);
        for (ChangeRequestMyTeamMemberAggregate memberAggregate : changeRequestMyTeamAggregate.getMembers()) {
            changeRequestMyTeamService.addMyTeamMemberForMigration(changeRequestMyTeamId, memberAggregate.getMember());
        }

        //process comments
        List<ChangeRequestCommentMigrate> createdComments = new ArrayList<>();
        if (Objects.nonNull(commentList)) {
            commentList.forEach(comment -> {
                try {
                    String commentOldId = comment.getCommentOldId();
                    ChangeRequestComment changeRequestComment1 = new ChangeRequestComment();
                    changeRequestComment1.setCommentText(comment.getCommentText());
                    changeRequestComment1.setStatus(comment.getStatus());
                    ChangeRequestComment changeRequestComment = (ChangeRequestComment) changeRequestCommentService.createCommentMigrate(changeRequestComment1, changeRequestId, ChangeRequest.class, comment.getCreatedOn(), comment.getCreator());
                    createdComments.add(new ChangeRequestCommentMigrate(changeRequestComment, commentOldId));
                } catch (ParseException exception) {
                	log.error("Exception occurred", exception);
                }
            });
        }
        changeRequestAggregateWithComments.setChangeRequestAggregate(changeRequestService.getAggregate(changeRequestId));
        changeRequestAggregateWithComments.setComments(createdComments);
        return changeRequestAggregateWithComments;
    }

    public EntitiesPreviousRevision getEntitiesPreviousRevisions(ChangeRequestAggregate changeRequestAggregate) {
        EntitiesPreviousRevision entitiesPreviousRevision = new EntitiesPreviousRevision();
        entitiesPreviousRevision.setChangeRequestRevision(getLatestRevision("aud_change_request", changeRequestAggregate.getDescription()));
        Long scopeId = getScopeIdByChangeRequestId(changeRequestAggregate.getDescription().getId());
        changeRequestAggregate.getScope().setId(scopeId);
        entitiesPreviousRevision.setScopeRevision(getLatestRevision("aud_scope", changeRequestAggregate.getScope()));
        Long solutionDefinitionId = getSolutionDefinitionIdByChangeRequestId(changeRequestAggregate.getDescription().getId());
        changeRequestAggregate.getSolutionDefinition().setId(solutionDefinitionId);
        entitiesPreviousRevision.setSolutionDefinitionRevision(getLatestRevision("aud_solution_definition", changeRequestAggregate.getSolutionDefinition()));
        Long impactAnalysisId = getImpactAnalysisIdByChangeRequestId(changeRequestAggregate.getDescription().getId());
        changeRequestAggregate.getImpactAnalysis().getGeneral().setId(impactAnalysisId);
        entitiesPreviousRevision.setImpactAnalysisRevision(getLatestRevision("aud_impact_analysis", changeRequestAggregate.getImpactAnalysis().getGeneral()));
        Long customerImpactId = getCustomerImpactIdByChangeRequestId(changeRequestAggregate.getDescription().getId());
        changeRequestAggregate.getImpactAnalysis().getDetails().getCustomerImpact().setId(customerImpactId);
        entitiesPreviousRevision.setCustomerImpactRevision(getLatestRevision("aud_customer_impact", changeRequestAggregate.getImpactAnalysis().getDetails().getCustomerImpact()));
        Long preinstallImpactId = getPreinstallImpactIdByChangeRequestId(changeRequestAggregate.getDescription().getId());
        changeRequestAggregate.getImpactAnalysis().getDetails().getPreinstallImpact().setId(preinstallImpactId);
        entitiesPreviousRevision.setPreinstallImpactRevision(getLatestRevision("aud_preinstall_impact", changeRequestAggregate.getImpactAnalysis().getDetails().getPreinstallImpact()));
        Long completeBusinessCaseId = getCompleteBusinessCaseIdByChangeRequestId(changeRequestAggregate.getDescription().getId());
        changeRequestAggregate.getImpactAnalysis().getDetails().getCompleteBusinessCase().setId(completeBusinessCaseId);
        entitiesPreviousRevision.setCompleteBusinessCaseRevision(getLatestRevision("aud_complete_business_case", changeRequestAggregate.getImpactAnalysis().getDetails().getCompleteBusinessCase()));
        return entitiesPreviousRevision;
    }

    public Integer getLatestRevision(String tableName, BaseEntityInterface entity) {
        EntityManager entityManager = (EntityManager) ApplicationContextHolder.getBean(EntityManager.class);
        //get aud of entity
        Query audChangeRequestQuery = entityManager.createNativeQuery("SELECT * FROM " + tableName + " WHERE id = ?1 AND (revtype=?2 OR revtype=?3) order by rev desc");
        audChangeRequestQuery.setParameter(1, entity.getId());
        audChangeRequestQuery.setParameter(2, 0);
        audChangeRequestQuery.setParameter(3, 1);
        List<Object> revisions = audChangeRequestQuery.getResultList();
        if (!revisions.isEmpty()) {
            Object[] revision = ((Object[]) revisions.get(0));
            return (Integer) revision[1];
        }
        return null;
    }

    public Integer getFirstRevisionOfChangeRequest(BaseEntityInterface entity) {
        EntityManager entityManager = (EntityManager) ApplicationContextHolder.getBean(EntityManager.class);
        //get aud of entity
        Query audChangeRequestQuery = entityManager.createNativeQuery("SELECT * FROM aud_change_request WHERE id = ?1 AND (revtype=?2) order by rev desc");
        audChangeRequestQuery.setParameter(1, entity.getId());
        audChangeRequestQuery.setParameter(2, 0);
        List<Object> revisions = audChangeRequestQuery.getResultList();
        if (!revisions.isEmpty()) {
            Object[] revision = ((Object[]) revisions.get(0));
            return (Integer) revision[1];
        }
        return null;
    }

    @Transactional
    public void updateAuditOfEntities(ChangeRequestAggregateWithComments request, EntitiesPreviousRevision entitiesPreviousRevision) {
        ChangeRequestAggregate changeRequestAggregate = request.getChangeRequestAggregate();
        Integer changeRequestLatestRevision = getLatestRevision("aud_change_request", changeRequestAggregate.getDescription());
        Integer changeRequestFirstRevision = getFirstRevisionOfChangeRequest(changeRequestAggregate.getDescription());
        if (!Objects.equals(changeRequestLatestRevision, entitiesPreviousRevision.getChangeRequestRevision())) {
            if (Objects.nonNull(entitiesPreviousRevision.getChangeRequestRevision()))
                updateRevEndTimestamp(entitiesPreviousRevision.getChangeRequestRevision(), request.getModifiedOn(), "aud_change_request");
            if(!Objects.equals(changeRequestLatestRevision, changeRequestFirstRevision))
                updateAudUpdaterAndTimestampByRevId(changeRequestLatestRevision, request.getModifiedBy(), request.getModifiedOn());
        }

        //scope

        Integer scopeLatestRevision = getLatestRevision("aud_scope", changeRequestAggregate.getScope());
        if (!Objects.equals(scopeLatestRevision, entitiesPreviousRevision.getScopeRevision())) {
            if (Objects.nonNull(entitiesPreviousRevision.getScopeRevision()))
                updateRevEndTimestamp(entitiesPreviousRevision.getScopeRevision(), request.getModifiedOn(), "aud_scope");
            if(!Objects.equals(changeRequestLatestRevision, changeRequestFirstRevision))
                updateAudUpdaterAndTimestampByRevId(scopeLatestRevision, request.getModifiedBy(), request.getModifiedOn());
        }

        //solution definition

        Integer solutionDefinitionLatestRevision = getLatestRevision("aud_solution_definition", changeRequestAggregate.getSolutionDefinition());
        if (!Objects.equals(solutionDefinitionLatestRevision, entitiesPreviousRevision.getSolutionDefinitionRevision())) {
            if (Objects.nonNull(entitiesPreviousRevision.getSolutionDefinitionRevision()))
                updateRevEndTimestamp(entitiesPreviousRevision.getSolutionDefinitionRevision(), request.getModifiedOn(), "aud_solution_definition");
            if(!Objects.equals(changeRequestLatestRevision, changeRequestFirstRevision))
                updateAudUpdaterAndTimestampByRevId(solutionDefinitionLatestRevision, request.getModifiedBy(), request.getModifiedOn());
        }

        //impact analysis

        Integer impactAnalysisLatestRevision = getLatestRevision("aud_impact_analysis", changeRequestAggregate.getImpactAnalysis().getGeneral());
        if (!Objects.equals(impactAnalysisLatestRevision, entitiesPreviousRevision.getImpactAnalysisRevision())) {
            if (Objects.nonNull(entitiesPreviousRevision.getImpactAnalysisRevision()))
                updateRevEndTimestamp(entitiesPreviousRevision.getImpactAnalysisRevision(), request.getModifiedOn(), "aud_impact_analysis");
            if(!Objects.equals(changeRequestLatestRevision, changeRequestFirstRevision))
                updateAudUpdaterAndTimestampByRevId(impactAnalysisLatestRevision, request.getModifiedBy(), request.getModifiedOn());
        }

        //customer impact

        Integer customerImpactLatestRevision = getLatestRevision("aud_customer_impact", changeRequestAggregate.getImpactAnalysis().getDetails().getCustomerImpact());
        if (!Objects.equals(customerImpactLatestRevision, entitiesPreviousRevision.getCustomerImpactRevision())) {
            if (Objects.nonNull(entitiesPreviousRevision.getCustomerImpactRevision()))
                updateRevEndTimestamp(entitiesPreviousRevision.getCustomerImpactRevision(), request.getModifiedOn(), "aud_customer_impact");
            if(!Objects.equals(changeRequestLatestRevision, changeRequestFirstRevision))
                updateAudUpdaterAndTimestampByRevId(customerImpactLatestRevision, request.getModifiedBy(), request.getModifiedOn());
        }

        //preinstall_impact
        Integer preinstallImpactLatestRevision = getLatestRevision("aud_preinstall_impact", changeRequestAggregate.getImpactAnalysis().getDetails().getPreinstallImpact());
        if (!Objects.equals(preinstallImpactLatestRevision, entitiesPreviousRevision.getPreinstallImpactRevision())) {
            if (Objects.nonNull(entitiesPreviousRevision.getPreinstallImpactRevision()))
                updateRevEndTimestamp(entitiesPreviousRevision.getPreinstallImpactRevision(), request.getModifiedOn(), "aud_preinstall_impact");
            if(!Objects.equals(changeRequestLatestRevision, changeRequestFirstRevision))
                updateAudUpdaterAndTimestampByRevId(preinstallImpactLatestRevision, request.getModifiedBy(), request.getModifiedOn());
        }

        //cbc
        Integer completeBusinessCaseLatestRevision = getLatestRevision("aud_complete_business_case", changeRequestAggregate.getImpactAnalysis().getDetails().getCompleteBusinessCase());
        if (Objects.equals(completeBusinessCaseLatestRevision, entitiesPreviousRevision.getCompleteBusinessCaseRevision())) {
            if (Objects.nonNull(entitiesPreviousRevision.getCompleteBusinessCaseRevision()))
                updateRevEndTimestamp(entitiesPreviousRevision.getCompleteBusinessCaseRevision(), request.getModifiedOn(), "aud_complete_business_case");
            if(!Objects.equals(changeRequestLatestRevision, changeRequestFirstRevision))
                updateAudUpdaterAndTimestampByRevId(completeBusinessCaseLatestRevision, request.getModifiedBy(), request.getModifiedOn());
        }
    }

    @Transactional
    public void updateAuditForCollection(List<String> newInsChangedAttributeNames, Date modifiedOn, BaseEntityInterface changeRequest) {
        if (newInsChangedAttributeNames.isEmpty()) {
            return;
        }
        String fieldName = newInsChangedAttributeNames.get(0);
        if (!isFieldOfTypeCollection(fieldName)) {
            return;
        }
        String tableName = "aud_" + fieldName;
        if (Objects.nonNull(fieldName) && fieldName.equalsIgnoreCase("contexts"))
            tableName = "aud_change_request_contexts";
        EntityManager entityManager = (EntityManager) ApplicationContextHolder.getBean(EntityManager.class);

        //get latest revision id
        Query audCollectionSelectQuery = entityManager.createNativeQuery("select * FROM " + tableName + " WHERE id = ?1 order by rev desc");
        audCollectionSelectQuery.setParameter(1, changeRequest.getId());
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
        audCollectionQuery.setParameter(1, changeRequest.getId());
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
        List<String> collectionFields = new ArrayList<>(Arrays.asList(new String[]{"contexts", "change_control_boards", "change_boards", "issue_types", "reasons_for_change", "dependent_change_request_ids","products_affected","liability_risks","implementation_ranges", "cbp_strategies", "fco_types"}));
        return collectionFields.contains(fieldName);
    }

    public ChangeRequestAggregate deleteChangeRequest(Long changeRequestId) {
        return null;
    }

    public void deleteAuditEntries(ChangeRequestAggregate deletedChangeRequestAggregate) {
        return;
    }
}
