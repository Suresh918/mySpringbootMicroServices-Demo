package com.example.mirai.projectname.releasepackageservice.releasepackage.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.xml.parsers.ParserConfigurationException;

import com.example.mirai.libraries.audit.component.AuditableUserAware;
import com.example.mirai.libraries.audit.model.AuditableUpdater;
import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.core.model.SynchronizationContextInterface;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.event.annotation.PublishResponse;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import com.example.mirai.projectname.libraries.bpm.Action;
import com.example.mirai.projectname.libraries.bpm.BPMEvent;
import com.example.mirai.projectname.libraries.bpm.BWEvent;
import com.example.mirai.projectname.libraries.bpm.ChangeNotice;
import com.example.mirai.projectname.libraries.model.ChangeObject;
import com.example.mirai.projectname.libraries.model.ChangeRequest;
import com.example.mirai.projectname.libraries.model.ReviewAggregate;
import com.example.mirai.projectname.releasepackageservice.myteam.service.ReleasePackageMyTeamService;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageContext;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate.ReleasePackageAggregate;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.w3c.dom.Node;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ReleasePackageSynchronizationService {

    @Resource
    ReleasePackageSynchronizationService self;
    private ReleasePackageService releasePackageService;
    private ReleasePackageMyTeamService releasePackageMyTeamService;
    private final ObjectMapper objectMapper;

    public ReleasePackageSynchronizationService(ReleasePackageService releasePackageService, ReleasePackageMyTeamService releasePackageMyTeamService, ObjectMapper objectMapper) {
        this.releasePackageService = releasePackageService;
        this.releasePackageMyTeamService = releasePackageMyTeamService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void updateReleasePackageLinkedToReview(ReviewAggregate reviewAggregate) {
        User user = new User();
        user.setUserId(reviewAggregate.getActorUserId());
        user.setAbbreviation(reviewAggregate.getActorAbbreviation());
        user.setDepartmentName(reviewAggregate.getActorDepartmentName());
        user.setEmail(reviewAggregate.getActorEmail());
        user.setFullName(reviewAggregate.getActorFullName());
        AuditableUserAware.AuditableUserHolder.user().set(user);
        Date eventTimestamp = reviewAggregate.getEventTimestamp();
        upsertContextsOfReleasePackage(eventTimestamp, reviewAggregate);
    }

    @Transactional
    public void updateReleasePackageLinkedToChangeRequest(ChangeRequest changeRequest) {
        User user = new User();
        user.setUserId(changeRequest.getActorUserId());
        user.setAbbreviation(changeRequest.getActorAbbreviation());
        user.setDepartmentName(changeRequest.getActorDepartmentName());
        user.setEmail(changeRequest.getActorEmail());
        user.setFullName(changeRequest.getActorFullName());
        AuditableUserAware.AuditableUserHolder.user().set(user);
        Date eventTimestamp = changeRequest.getEventTimestamp();
        upsertContextsOfReleasePackage(eventTimestamp, changeRequest);
    }

    @Transactional
    public void updateReleasePackageLinkedToAction(BPMEvent bpmEvent) {
        String actionsXml = bpmEvent.getManagedObjectDetails();
        Action action = new Action(actionsXml, "RELEASEPACKAGE");
        User user = new User();
        user.setUserId(bpmEvent.getUserId());
        user.setAbbreviation(bpmEvent.getUserAbbreviation());
        user.setDepartmentName(bpmEvent.getUserDepartmentName());
        user.setEmail(bpmEvent.getUserEmail());
        user.setFullName(bpmEvent.getUserFullName());
        AuditableUserAware.AuditableUserHolder.user().set(user);
        Date eventTimestamp = bpmEvent.getEventTimestamp();
        if (!action.getParentIds().isEmpty())
            upsertContextsOfReleasePackage(eventTimestamp, action);
    }

    @Transactional
    @SneakyThrows
    public void updateReleasePackageLinkedToAction(BWEvent bwEvent) {
        Node actionNode = bwEvent.getFunctional();
        Action action = new Action(actionNode, "RELEASEPACKAGE");
        User user = new User();
        user.setUserId(bwEvent.getUserId());
        user.setAbbreviation(bwEvent.getUserAbbreviation());
        user.setDepartmentName(bwEvent.getUserDepartmentName());
        user.setEmail(bwEvent.getUserEmail());
        user.setFullName(bwEvent.getUserFullName());
        Date eventTimestamp = bwEvent.getEventTimestamp();
        AuditableUserAware.AuditableUserHolder.user().set(user);
        if (!action.getParentIds().isEmpty())
            upsertContextsOfReleasePackage(eventTimestamp, action);
    }


    @Transactional
    public void updateReleasePackageLinkedToChangeNotice(BPMEvent bpmEvent) {
        String changeNoticeXml = bpmEvent.getManagedObjectDetails();
        ChangeNotice changeNotice = new ChangeNotice(changeNoticeXml, "RELEASEPACKAGE");
        User user = new User();
        user.setUserId(bpmEvent.getUserId());
        user.setAbbreviation(bpmEvent.getUserAbbreviation());
        user.setDepartmentName(bpmEvent.getUserDepartmentName());
        user.setEmail(bpmEvent.getUserEmail());
        user.setFullName(bpmEvent.getUserFullName());
        AuditableUserAware.AuditableUserHolder.user().set(user);
        Date eventTimestamp = bpmEvent.getEventTimestamp();
        if (!changeNotice.getParentIds().isEmpty())
            upsertContextsOfReleasePackage(eventTimestamp, changeNotice);
    }


    @Transactional
    public void updateReleasePackageLinkedToChangeNotice(BWEvent bwEvent) throws ParserConfigurationException {
        Node changeNoticeNode = bwEvent.getFunctional();
        ChangeNotice changeNotice = new ChangeNotice(changeNoticeNode, "RELEASEPACKAGE");
        User user = new User();
        user.setUserId(bwEvent.getUserId());
        user.setAbbreviation(bwEvent.getUserAbbreviation());
        user.setDepartmentName(bwEvent.getUserDepartmentName());
        user.setEmail(bwEvent.getUserEmail());
        user.setFullName(bwEvent.getUserFullName());
        Date eventTimestamp = bwEvent.getEventTimestamp();
        AuditableUserAware.AuditableUserHolder.user().set(user);
        if (!changeNotice.getParentIds().isEmpty())
            upsertContextsOfReleasePackage(eventTimestamp, changeNotice);
    }

    private void upsertContextsOfReleasePackage(Date eventTimestamp, SynchronizationContextInterface contextData) {
        String contextStatus = contextData.getStatus();
        String contextTitle = contextData.getTitle();
        String contextType = contextData.getType();
        String contextId = contextData.getContextId();
        String criteria = "contexts.contextId:" + contextId;
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE - 1);
        BaseEntityList baseEntityList = releasePackageService.filter(criteria, pageable);
        List<ReleasePackage> releasePackageList = baseEntityList.getResults();
        List<ReleasePackage> releasePackagesWithContext = new ArrayList<>();
        releasePackageList.forEach(releasePackage -> {
            if (Objects.nonNull(releasePackage.getContexts()) && releasePackage.getContexts().stream().filter(context -> Objects.nonNull(context) && Objects.equals(context.getContextId(), contextId) && Objects.equals(context.getType(), contextType)).findFirst().isPresent()) {
                releasePackagesWithContext.add(releasePackage);
            }
        });
        Long releasePackageId = null;
        if (Objects.nonNull(contextData.getParentId()))
            releasePackageId = releasePackageService.getReleasePackageIdByReleasePackageNumber(contextData.getParentId());
        if (releasePackagesWithContext.isEmpty() && Objects.nonNull(releasePackageId)) {
            releasePackagesWithContext.add((ReleasePackage) releasePackageService.getEntityById(releasePackageId));
        }
        getDistinctReleasePackages(releasePackagesWithContext).stream().forEach(releasePackage -> {
            if (isUpdateApplicable(releasePackage, contextData, eventTimestamp)) {
                //TODO: check if this creates a new entity
                ReleasePackage newIns = new ReleasePackage();
                List<ReleasePackageContext> releasePackageContextList = releasePackage.getContexts();
                if (Objects.isNull(releasePackageContextList)) {
                    releasePackageContextList = new ArrayList<>();
                }
                Boolean isReviewStatusChanged = false;
                Optional<ReleasePackageContext> currentContext = releasePackage.getContexts().stream().filter(context -> Objects.nonNull(context) && context.getType().equals(contextType) && context.getContextId().equals(contextId)).findFirst();
                if (releasePackage.getContexts().isEmpty() || currentContext.isEmpty()) {
                    ReleasePackageContext releasePackageContext = new ReleasePackageContext(contextType, contextId, contextTitle, contextStatus);
                    releasePackageContextList.add(releasePackageContext);
                    isReviewStatusChanged = Objects.equals(contextType, "REVIEW");
                } else if (currentContext.isPresent()) {
                    isReviewStatusChanged = isReviewStatusChanged || (contextType.equals("REVIEW") && !contextStatus.equals(currentContext.get().getStatus()));
                    currentContext.get().setStatus(contextStatus);
                    currentContext.get().setName(contextTitle);
                }
                newIns.setContexts(releasePackageContextList);
                newIns.setId(releasePackage.getId());
                Map<String, Object> changedAttrs = new HashMap<>();
                changedAttrs.put("contexts", newIns.getContexts());
                ReleasePackage updatedReleasePackage = releasePackageService.updateContexts(newIns, changedAttrs);
                if (isReviewStatusChanged && Objects.nonNull(updatedReleasePackage)) {
                    self.sendNotificationOnReviewStatusUpdate(updatedReleasePackage);
                }
                if (contextType.equals("CHANGENOTICE") && releasePackageMyTeamService.isChangeSpecialist2Updated(releasePackage, ((ChangeNotice) contextData).getChangeSpecialist2User())) {
                    checkLastUpdatedAndUpdateMyTeam(releasePackage, eventTimestamp, (ChangeNotice) contextData);
                }
            }
        });
    }

    private List<ReleasePackage> getDistinctReleasePackages(List<ReleasePackage> releasePackageList) {
        List<ReleasePackage> distinctReleasePackages = new ArrayList<>();
        releasePackageList.forEach(releasePackage -> {
            boolean isAdded = false;
            for (ReleasePackage distinctReleasePackage : distinctReleasePackages) {
                isAdded = isAdded || Objects.equals(releasePackage.getId(), distinctReleasePackage.getId());
            }
            if (!isAdded) {
                distinctReleasePackages.add(releasePackage);
            }
        });
        return distinctReleasePackages;
    }

    public boolean isUpdateApplicable(ReleasePackage releasePackage, SynchronizationContextInterface contextData, Date eventTimestamp) {
        List<ReleasePackageContext> releasePackageContexts = Objects.isNull(releasePackage.getContexts()) ? new ArrayList<>() : releasePackage.getContexts();
        Optional<ReleasePackageContext> releasePackageContext = releasePackageContexts.stream().filter(context -> Objects.nonNull(context) && context.getType().equals(contextData.getType()) && context.getContextId().equals(contextData.getContextId())).findFirst();
        if (releasePackageContext.isEmpty()) {
            return true;
        } else if (Objects.equals(releasePackageContext.get().getName(), contextData.getTitle()) && Objects.equals(releasePackageContext.get().getStatus(), contextData.getStatus())) {
            return false;
        }
        EntityManager entityManager = (EntityManager) ApplicationContextHolder.getBean(EntityManager.class);
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        AuditQuery auditQuery = auditReader.createQuery().forRevisionsOfEntityWithChanges(ReleasePackage.class, true);
        auditQuery.add(AuditEntity.id().eq(releasePackage.getId()));
        auditQuery.add(AuditEntity.property("contexts").hasChanged());
        auditQuery.addOrder(AuditEntity.revisionNumber().desc());
        List<Object> revisions = auditQuery.getResultList();
        Date lastUpdatedOn = null;
        List<Object> contextRevisions;
        for (Object revision : revisions) {
            Object[] properties = (Object[]) revision;
            AuditableUpdater auditableUpdater = (AuditableUpdater) properties[1];
            Query query = entityManager.createNativeQuery("SELECT * FROM aud_release_package_contexts WHERE type = ?1 AND context_id=?2 AND rev=?3 AND revtype=?4");
            query.setParameter(1, contextData.getType());
            query.setParameter(2, contextData.getContextId());
            query.setParameter(3, auditableUpdater.getId());
            query.setParameter(4, 0);
            contextRevisions = query.getResultList();
            //updatedOn = auditReader.getRevisionDate(auditableUpdater.getContextId());
            lastUpdatedOn = new Date(auditableUpdater.getTimestamp());
            // if the context is found, break the loop, as the revisions are sorted on desc
            if (contextRevisions.size() > 0) {
                break;
            }
        }
        return Objects.nonNull(eventTimestamp) && Objects.nonNull(lastUpdatedOn) && eventTimestamp.compareTo(lastUpdatedOn) > 0;
    }

    @PublishResponse(eventType = "REVIEWUPDATE", destination = "com.example.mirai.projectname.releasepackageservice.reviewupdate",
            eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReleasePackageAggregate.class)
    @Transactional
    public ReleasePackage sendNotificationOnReviewStatusUpdate(BaseEntityInterface releasePackage) {
        return (ReleasePackage) releasePackage;
    }

    private void checkLastUpdatedAndUpdateMyTeam(ReleasePackage releasePackage, Date eventTimestamp, ChangeNotice changeNotice) {
        EntityManager entityManager = (EntityManager) ApplicationContextHolder.getBean(EntityManager.class);
        //TODO: try to optimize the below query
        Query query = entityManager.createNativeQuery("select id from aud_member_role where revtype in (0,1) and roles = 'changeSpecialist2' and id in (" +
                "select id from aud_my_team_member where myteam_id in (" +
                "select id from aud_my_team where id = ?1)) order by rev desc");
        Long myTeamId = releasePackageMyTeamService.getMyTeamIdByLinkedEntity(releasePackage.getId());
        query.setParameter(1, myTeamId);
        List<Object> myTeamMemberId = query.getResultList();

        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        BigInteger id = (BigInteger) myTeamMemberId.get(0);
        AuditQuery auditQuery = auditReader.createQuery().forRevisionsOfEntityWithChanges(MyTeamMember.class, true);
        auditQuery.add(AuditEntity.id().eq(id.longValue()));
        auditQuery.add(AuditEntity.property("roles").hasChanged());
        auditQuery.addOrder(AuditEntity.revisionNumber().desc());
        List<Object> revisions = auditQuery.getResultList();
        for (Object obj : revisions) {
            Object[] properties = (Object[]) obj;
            AuditableUpdater auditableUpdater = (AuditableUpdater) properties[1];
            Date lastUpdatedOn = new Date(auditableUpdater.getTimestamp());
            if (eventTimestamp.compareTo(lastUpdatedOn) > 0) {
                releasePackageMyTeamService.addChangeSpecialist2ToMyTeam(changeNotice.getChangeSpecialist2User(), releasePackage.getId());
                break;
            }
        }
    }

    @Transactional
    public void updateContextWithChangeObject(ChangeObject changeObject) {
        if (Objects.isNull(changeObject.getChangeObjectType()) ||  !changeObject.getChangeObjectType().toUpperCase().equals("RELEASEPACKAGE"))
            return;
        setAuditableUserForChangeObjectUpdate(changeObject);
        String releasePackageNumber = changeObject.getParentId();
        Date eventTimestamp = changeObject.getEventTimestamp();
        if (Objects.nonNull(releasePackageNumber)) {
            checkForContextsOfReleasePackage(eventTimestamp, changeObject);
        }
    }

    private void setAuditableUserForChangeObjectUpdate(ChangeObject changeObject) {
        User user = new User();
        user.setUserId(changeObject.getActorUserId());
        user.setAbbreviation(changeObject.getActorAbbreviation());
        user.setDepartmentName(changeObject.getActorDepartmentName());
        user.setEmail(changeObject.getActorEmail());
        user.setFullName(changeObject.getActorFullName());
        AuditableUserAware.AuditableUserHolder.user().set(user);
    }

    @Transactional
    public void updateReleasePackageLinkedToChangeObject(ChangeObject changeObject) {
        if (Objects.isNull(changeObject.getChangeObjectType()) ||  !changeObject.getChangeObjectType().toUpperCase().equals("RELEASEPACKAGE"))
            return;
        setAuditableUserForChangeObjectUpdate(changeObject);
        Date eventTimestamp = changeObject.getEventTimestamp();
        upsertContextsOfReleasePackage(eventTimestamp, changeObject);
        updateMyTeamWithImpactedItemMyTeam(changeObject);
    }

    private void updateMyTeamWithImpactedItemMyTeam(ChangeObject changeObject) {
        List<HashMap> myTeamMembers = changeObject.getMyTeamMembers();
        List<MyTeamMember> changeObjectMyTeamMembers = myTeamMembers.stream().map(item -> objectMapper.convertValue(item, MyTeamMember.class)).collect(Collectors.toList());
        String releasePackageNumber = changeObject.getParentId();
        Date eventTimestamp = changeObject.getEventTimestamp();
        if (Objects.nonNull(releasePackageNumber) && !changeObjectMyTeamMembers.isEmpty()) {
            releasePackageService.updateMyTeamWithChangeObjectMyTeam(changeObjectMyTeamMembers, releasePackageNumber);
        }
    }

    public void checkForContextsOfReleasePackage(Date eventTimestamp, SynchronizationContextInterface contextData) {
        String contextStatus = contextData.getStatus();
        String contextId = contextData.getContextId();
        String contextTitle = contextData.getTitle();
        String contextType = contextData.getType();
        String criteria = "contexts.type:" + contextType + " and contexts.contextId:" + contextId + "'";
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE -1);
        BaseEntityList baseEntityList = releasePackageService.filter(criteria, pageable);
        List<ReleasePackage> releasePackageList = baseEntityList.getResults();
        Long releasePackageId = releasePackageService.getReleasePackageIdByReleasePackageNumber(contextData.getParentId());
        if (releasePackageList.isEmpty() && Objects.nonNull(releasePackageId)) {
            releasePackageList.add((ReleasePackage) releasePackageService.getEntityById(releasePackageId));
        }

        releasePackageList.stream().forEach(releasePackage -> {
            if (isUpdateApplicable(releasePackage, contextData, eventTimestamp)) {
                //ChangeRequest oldIns = new ChangeRequest();
                List<ReleasePackageContext> releasePackageOldContextList = Objects.isNull(releasePackage.getContexts()) ? new ArrayList<>() : releasePackage.getContexts();
                //oldIns.setContexts(changeRequestOldContextList);
                //oldIns.setId(changeRequest.getId());
                ReleasePackage newIns = new ReleasePackage();
                List<ReleasePackageContext> releasePackageNewContextList = new ArrayList<>();
                Optional<ReleasePackageContext> currentContext = releasePackageOldContextList.stream().filter(context -> Objects.nonNull(context) && context.getType().equals(contextType) && context.getContextId().equals(contextId)).findFirst();
                releasePackageOldContextList.stream().filter(Objects::nonNull).forEach(context -> {
                    ReleasePackageContext releasePackageContext;
                    if (context.getType().equals(contextType) && context.getContextId().equals(contextId)) {
                        releasePackageContext = new ReleasePackageContext(contextType, contextId, contextTitle, contextStatus);
                    } else {
                        releasePackageContext = new ReleasePackageContext(context.getType(), context.getContextId(), context.getName(), context.getStatus());
                    }
                    releasePackageNewContextList.add(releasePackageContext);
                });
                if (currentContext.isEmpty()) {
                    releasePackageNewContextList.add(new ReleasePackageContext(contextType, contextId, contextTitle, contextStatus));
                }
                newIns.setContexts(releasePackageNewContextList);
                newIns.setId(releasePackage.getId());
                Map<String, Object> changedAttrs = new HashMap<>();
                changedAttrs.put("contexts", newIns.getContexts());
                releasePackageService.updateContexts(newIns, changedAttrs);
            }
        });
    }

}
