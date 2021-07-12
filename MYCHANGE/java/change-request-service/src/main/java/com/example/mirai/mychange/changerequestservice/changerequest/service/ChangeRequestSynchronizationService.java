package com.example.mirai.projectname.changerequestservice.changerequest.service;

import com.example.mirai.libraries.audit.component.AuditableUserAware;
import com.example.mirai.libraries.audit.model.AuditableUpdater;
import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.core.model.SynchronizationContextInterface;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.entity.model.Id;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestContext;
import com.example.mirai.projectname.changerequestservice.completebusinesscase.model.CompleteBusinessCase;
import com.example.mirai.projectname.changerequestservice.completebusinesscase.service.CompleteBusinessCaseService;
import com.example.mirai.projectname.changerequestservice.shared.util.Constants;
import com.example.mirai.projectname.libraries.bpm.*;
import com.example.mirai.projectname.libraries.model.ReleasePackage;
import com.example.mirai.projectname.libraries.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Node;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ChangeRequestSynchronizationService {
    private final ChangeRequestService changeRequestService;
    private final ObjectMapper objectMapper;
    private final CompleteBusinessCaseService completeBusinessCaseService;

    @Transactional
    public void updateChangeNoticeStatus(BpmEvent bpmEvent) {
        String changeNoticeXml = bpmEvent.getManagedObjectDetails();
        ChangeNotice changeNotice = new ChangeNotice(changeNoticeXml, "CHANGEREQUEST");
        User user = new User();
        user.setUserId(bpmEvent.getUserId());
        user.setAbbreviation(bpmEvent.getUserAbbreviation());
        user.setDepartmentName(bpmEvent.getUserDepartmentName());
        user.setEmail(bpmEvent.getUserEmail());
        user.setFullName(bpmEvent.getUserFullName());
        AuditableUserAware.AuditableUserHolder.user().set(user);
        Date eventTimestamp = bpmEvent.getEventTimestamp();
        checkAndUpdateContextsOfChangeRequest(eventTimestamp, changeNotice);
    }

    @SneakyThrows
    @Transactional
    public void updateChangeNoticeStatus(BwEvent bwEvent) {
        Node changeNoticeNode = bwEvent.getFunctional();
        ChangeNotice changeNotice = new ChangeNotice(changeNoticeNode, "CHANGEREQUEST");
        User user = new User();
        user.setUserId(bwEvent.getUserId());
        user.setAbbreviation(bwEvent.getUserAbbreviation());
        user.setDepartmentName(bwEvent.getUserDepartmentName());
        user.setEmail(bwEvent.getUserEmail());
        user.setFullName(bwEvent.getUserFullName());
        Date eventTimestamp = bwEvent.getEventTimestamp();
        AuditableUserAware.AuditableUserHolder.user().set(user);
        checkAndUpdateContextsOfChangeRequest(eventTimestamp, changeNotice);
    }

    @Transactional
    public void updateActionData(BpmEvent bpmEvent) {
        String actionXml = bpmEvent.getManagedObjectDetails();
        Action action = new Action(actionXml, "CHANGEREQUEST");
        User user = new User();
        user.setUserId(bpmEvent.getUserId());
        user.setAbbreviation(bpmEvent.getUserAbbreviation());
        user.setDepartmentName(bpmEvent.getUserDepartmentName());
        user.setEmail(bpmEvent.getUserEmail());
        user.setFullName(bpmEvent.getUserFullName());
        AuditableUserAware.AuditableUserHolder.user().set(user);
        Date eventTimestamp = bpmEvent.getEventTimestamp();
        if (!action.getParentIds().isEmpty())
            checkAndUpdateContextsOfChangeRequest(eventTimestamp, action);
    }

    @SneakyThrows
    @Transactional
    public void updateActionData(BwEvent bwEvent) {
        Node actionNode = bwEvent.getFunctional();
        Action action = new Action(actionNode, "CHANGEREQUEST");
        User user = new User();
        user.setUserId(bwEvent.getUserId());
        user.setAbbreviation(bwEvent.getUserAbbreviation());
        user.setDepartmentName(bwEvent.getUserDepartmentName());
        user.setEmail(bwEvent.getUserEmail());
        user.setFullName(bwEvent.getUserFullName());
        Date eventTimestamp = bwEvent.getEventTimestamp();
        AuditableUserAware.AuditableUserHolder.user().set(user);
        if (!action.getParentIds().isEmpty())
            checkAndUpdateContextsOfChangeRequest(eventTimestamp, action);
    }

    @Transactional
    public void updateAgendaItemData(BpmEvent bpmEvent) {
        String agendaItemXml = bpmEvent.getManagedObjectDetails();
        AgendaItem agendaItem = new AgendaItem(agendaItemXml, "CHANGEREQUEST");
        User user = new User();
        user.setUserId(bpmEvent.getUserId());
        user.setAbbreviation(bpmEvent.getUserAbbreviation());
        user.setDepartmentName(bpmEvent.getUserDepartmentName());
        user.setEmail(bpmEvent.getUserEmail());
        user.setFullName(bpmEvent.getUserFullName());
        AuditableUserAware.AuditableUserHolder.user().set(user);
        Date eventTimestamp = bpmEvent.getEventTimestamp();
        if (!agendaItem.getParentIds().isEmpty())
            checkAndUpdateContextsOfChangeRequest(eventTimestamp, agendaItem);
    }

    @SneakyThrows
    @Transactional
    public void updateAgendaItemData(BwEvent bwEvent) {
        Node agendaItemNode = bwEvent.getFunctional();
        AgendaItem agendaItem = new AgendaItem(agendaItemNode, "CHANGEREQUEST");
        User user = new User();
        user.setUserId(bwEvent.getUserId());
        user.setAbbreviation(bwEvent.getUserAbbreviation());
        user.setDepartmentName(bwEvent.getUserDepartmentName());
        user.setEmail(bwEvent.getUserEmail());
        user.setFullName(bwEvent.getUserFullName());
        Date eventTimestamp = bwEvent.getEventTimestamp();
        AuditableUserAware.AuditableUserHolder.user().set(user);
        if (!agendaItem.getParentIds().isEmpty())
            checkAndUpdateContextsOfChangeRequest(eventTimestamp, agendaItem);
    }

    @Transactional
    public void updateReleasePackageStatus(ReleasePackage releasePackage) {
        User user = new User();
        log.info("release package updated ----- " + releasePackage.jsonData);
        user.setUserId(releasePackage.getActorUserId());
        user.setAbbreviation(releasePackage.getActorAbbreviation());
        user.setDepartmentName(releasePackage.getActorDepartmentName());
        user.setEmail(releasePackage.getActorEmail());
        user.setFullName(releasePackage.getActorFullName());
        AuditableUserAware.AuditableUserHolder.user().set(user);
        Date eventTimestamp = releasePackage.getEventTimestamp();
        checkAndUpdateContextsOfChangeRequest(eventTimestamp, releasePackage);
        SynchronizationContextInterface ecnContext = new Ecn(releasePackage, "release_package", "CHANGEREQUEST");
        checkAndUpdateContextsOfChangeRequest(eventTimestamp, ecnContext);
        SynchronizationContextInterface teamcenterContext = new Teamcenter(releasePackage, "release_package", "CHANGEREQUEST");
        if (Objects.nonNull(teamcenterContext.getContextId()))
            checkAndUpdateContextsOfChangeRequest(eventTimestamp, teamcenterContext);
    }

    @Transactional
    public void updateMyTeamWithImpactedItemMyTeam(com.example.mirai.projectname.libraries.model.ChangeObject changeObject) {
        //TODO: how to check last updated
        if (Objects.isNull(changeObject.getChangeObjectType()) ||  !changeObject.getChangeObjectType().toUpperCase().equals("CHANGEREQUEST"))
            return;
        List<HashMap> myTeamMembers = changeObject.getMyTeamMembers();
        List<MyTeamMember> changeObjectMyTeamMembers = myTeamMembers.stream().map(item -> objectMapper.convertValue(item, MyTeamMember.class)).collect(Collectors.toList());
        String changeRequestId = changeObject.getParentId();
        Date eventTimestamp = changeObject.getEventTimestamp();
        if (Objects.nonNull(changeRequestId) && !changeObjectMyTeamMembers.isEmpty()) {
            changeRequestService.updateMyTeamWithChangeObjectMyTeam(changeObjectMyTeamMembers, changeRequestId);
            checkAndUpdateContextsOfChangeRequest(eventTimestamp, changeObject);
        }
    }

    @Transactional
    public void updateContextWithChangeObject(ChangeObject changeObject) {
        if (Objects.isNull(changeObject.getChangeObjectType()) ||  !changeObject.getChangeObjectType().toUpperCase().equals("CHANGEREQUEST"))
            return;
        setAuditableUserForChangeObjectUpdate(changeObject);
        String changeRequestId = changeObject.getParentId();
        Date eventTimestamp = changeObject.getEventTimestamp();
        if (Objects.nonNull(changeRequestId)) {
            checkAndUpdateContextsOfChangeRequest(eventTimestamp, changeObject);
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

    @SneakyThrows
    public void checkAndUpdateContextsOfChangeRequest(Date eventTimestamp, SynchronizationContextInterface contextData) {
        String contextStatus = contextData.getStatus();
        String contextId = contextData.getContextId();
        String contextTitle = contextData.getTitle();
        String contextType = contextData.getType();
        String criteria = "contexts.contextId:" + contextId;
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE -1);
        List<String> parentIds =  contextData.getParentIds();
        List<Long> changeRequestIds = parentIds.stream().distinct().map(id -> {
            try {
                return Long.parseLong(id);
            } catch(NumberFormatException e) {
                return null;
            }
        }).collect(Collectors.toList());
        changeRequestIds = changeRequestIds.stream().filter(Objects::nonNull).collect(Collectors.toList());
        log.info("in synchronization criteria - " + criteria);
        Slice<Id> idsWithContext = changeRequestService.filterIds(criteria, pageable);
        if (idsWithContext.getNumberOfElements() > 0) {
            //changeRequestIds.addAll(idsWithContext.getContent().stream().map(item -> item.getValue()).collect(Collectors.toList()));
            List<Long> changeRequestIdsWithContext = new ArrayList();
            idsWithContext.getContent().stream().forEach(item -> {
                ChangeRequest changeRequest = (ChangeRequest) changeRequestService.getEntityById(item.getValue());
                if (Objects.nonNull(changeRequest.getContexts()) && changeRequest.getContexts().stream().filter(context -> context.getType().equals(contextType) && context.getContextId().equals(contextId)).findFirst().isPresent()) {
                    changeRequestIdsWithContext.add(changeRequest.getId());
                }
            });
            changeRequestIds.addAll(changeRequestIdsWithContext);
        }
        changeRequestIds = changeRequestIds.stream().distinct().collect(Collectors.toList());
        List<ChangeRequest> changeRequestList = new ArrayList<>();
        if (!changeRequestIds.isEmpty()) {
            removeChangeNoticeContextFromChangeRequest(contextData, idsWithContext, changeRequestIds);
            if (!changeRequestIds.isEmpty()) {
                criteria = "id@" + changeRequestIds.toString().replaceAll("\\s", "");
                BaseEntityList baseEntityList = changeRequestService.filter(criteria, pageable);
                changeRequestList = baseEntityList.getResults();
            }
        }
        changeRequestList.stream().forEach(changeRequest -> {
            if (isUpdateApplicable(changeRequest, contextData, eventTimestamp)) {
                List<ChangeRequestContext> changeRequestOldContextList = Objects.isNull(changeRequest.getContexts()) ? new ArrayList<>() : changeRequest.getContexts();
                ChangeRequest newIns = new ChangeRequest();
                List<ChangeRequestContext> changeRequestNewContextList = new ArrayList<>();
                Optional<ChangeRequestContext> currentContext = changeRequestOldContextList.stream().filter(context -> context.getType().equals(contextType) && context.getContextId().equals(contextId)).findFirst();
                changeRequestOldContextList.stream().forEach(context -> {
                    ChangeRequestContext changeRequestContext;
                    if (context.getType().equals(contextType) && context.getContextId().equals(contextId)) {
                        changeRequestContext = new ChangeRequestContext(contextType, contextId, contextTitle, contextStatus);
                    } else {
                        changeRequestContext = new ChangeRequestContext(context.getType(), context.getContextId(), context.getName(), context.getStatus());
                    }
                    changeRequestNewContextList.add(changeRequestContext);
                });
                if (currentContext.isEmpty()) {
                    changeRequestNewContextList.add(new ChangeRequestContext(contextType, contextId, contextTitle, contextStatus));
                }
                removeDeletedAgendaItems(changeRequestNewContextList);
                newIns.setContexts(changeRequestNewContextList);
                newIns.setId(changeRequest.getId());
                Map<String, Object> changedAttrs = new HashMap<>();
                changedAttrs.put("contexts", newIns.getContexts());
                changeRequestService.updateContexts(newIns, changedAttrs);
            }
        });
    }

    private void removeChangeNoticeContextFromChangeRequest(SynchronizationContextInterface contextData, Slice<Id> idsWithChangeNoticeContext, List<Long> changeRequestIds) {
        if (!contextData.getType().equals("CHANGENOTICE"))
            return;
        List<String> parentIds = contextData.getParentIds();
        if (Objects.isNull(parentIds)) {
            return;
        }
        List<Long> changeRequestIdsFromChangeNotice = parentIds.stream().distinct().map(id -> {
            try {
                return Long.parseLong(id);
            } catch(NumberFormatException e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
        List<Long> changeRequestsUnlinked = new ArrayList<>();
        log.info("idsWithChangeNoticeContext " + idsWithChangeNoticeContext.getSize());
        idsWithChangeNoticeContext.getContent().stream().forEach(item -> {
            if (!changeRequestIdsFromChangeNotice.contains(item.getValue())) {
                changeRequestsUnlinked.add(item.getValue());
                changeRequestIds.remove(item.getValue());
            }
        });
        String changeNoticeId = contextData.getContextId();
        if (changeRequestsUnlinked.isEmpty() || Objects.isNull(changeNoticeId))
            return;
        String criteria = "id@" + changeRequestsUnlinked.toString().replaceAll("\\s", "");
        BaseEntityList<ChangeRequest> changeRequestList = changeRequestService.filter(criteria, PageRequest.of(0, Integer.MAX_VALUE -1));
        changeRequestList.getResults().forEach(changeRequest -> {
            List<ChangeRequestContext> changeRequestContexts = changeRequest.getContexts();
            if (Objects.nonNull(changeRequestContexts)) {
                Optional<ChangeRequestContext> changeNoticeContext = changeRequestContexts.stream().filter(item -> item.getType().equals("CHANGENOTICE") && item.getContextId().equals(changeNoticeId)).findFirst();
                if (changeNoticeContext.isPresent()) {
                    changeRequestContexts.remove(changeNoticeContext.get());
                    Map<String, Object> changedAttrs = new HashMap<>();
                    changedAttrs.put("contexts", changeRequestContexts);
                    changeRequest.setContexts(changeRequestContexts);
                    changeRequestService.update(changeRequest, changedAttrs);
                }
            }
        });
    }

    private void removeDeletedAgendaItems(List<ChangeRequestContext> changeRequestNewContextList) {
        List<ChangeRequestContext> deletedAgendaItemContexts = changeRequestNewContextList.stream().filter(item -> Objects.equals(item.getType(), "AGENDAITEM") && Objects.equals(item.getStatus(), "DELETED")).collect(Collectors.toList());
        changeRequestNewContextList.removeAll(deletedAgendaItemContexts);
    }

    public boolean isUpdateApplicable(ChangeRequest changeRequest, SynchronizationContextInterface contextData, Date eventTimestamp) {
        List<ChangeRequestContext> changeRequestContexts = Objects.isNull(changeRequest.getContexts()) ? new ArrayList<>() : changeRequest.getContexts();
        Optional<ChangeRequestContext> changeRequestContext = changeRequestContexts.stream().filter(context -> context.getType().equals(contextData.getType()) && context.getContextId().equals(contextData.getContextId())).findFirst();
        if (changeRequestContext.isEmpty()) {
            return true;
        } else if (Objects.equals(changeRequestContext.get().getName(), contextData.getTitle()) && Objects.equals(changeRequestContext.get().getStatus(),contextData.getStatus())) {
            return false;
        }
        EntityManager entityManager = (EntityManager) ApplicationContextHolder.getBean(EntityManager.class);
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        AuditQuery auditQuery = auditReader.createQuery().forRevisionsOfEntityWithChanges(ChangeRequest.class, true);
        auditQuery.add(AuditEntity.id().eq(changeRequest.getId()));
        auditQuery.add(AuditEntity.property("contexts").hasChanged());
        auditQuery.addOrder(AuditEntity.revisionNumber().desc());
        List<Object> revisions = auditQuery.getResultList();
        Date lastUpdatedOn = null;
        List<Object> contextRevisions;
        for (Object revision : revisions) {
            Object[] properties = (Object[]) revision;
            AuditableUpdater auditableUpdater = (AuditableUpdater) properties[1];
            //TODO: check for alternatives
            Query query = entityManager.createNativeQuery("SELECT * FROM aud_change_request_contexts WHERE type = ?1 AND context_id=?2 AND rev=?3 AND revtype=?4");
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

    @Transactional
    public void updateSciaData(Scia scia) {
        User user = new User();
        user.setUserId(scia.getActorUserId());
        user.setAbbreviation(scia.getActorAbbreviation());
        user.setDepartmentName(scia.getActorDepartmentName());
        user.setEmail(scia.getActorEmail());
        user.setFullName(scia.getActorFullName());
        AuditableUserAware.AuditableUserHolder.user().set(user);
        Date eventTimestamp = scia.getEventTimestamp();
        checkAndUpdateContextsOfChangeRequest(eventTimestamp, scia);
        if(Objects.equals(scia.getStatus(), Constants.SCIA_STATUS_RELEASE)) {
            CompleteBusinessCase completeBusinessCase = completeBusinessCaseService.getCompleteBusinessCaseByChangeRequestId(Long.parseLong(scia.getParentId()));
            completeBusinessCase.setSupplyChainAdjustmentsNonrecurringCosts(scia.getSuppChainAdjustment());
            float partOrToolScrapFactoryWarehouseOrWip = Objects.nonNull(scia.getPartOrToolScrapFactoryWarehouseOrWip()) ? scia.getPartOrToolScrapFactoryWarehouseOrWip() : 0;
            float partOrToolScrapFieldWarehouse = Objects.nonNull(scia.getPartOrToolScrapFieldWarehouse()) ? scia.getPartOrToolScrapFieldWarehouse() : 0;
            completeBusinessCase.setInventoryScrapNonrecurringCosts(partOrToolScrapFactoryWarehouseOrWip + partOrToolScrapFieldWarehouse);
            completeBusinessCase.setFactoryChangeOrderNonrecurringCosts(scia.getFactoryChangeOrderCostWip());
            completeBusinessCase.setFieldChangeOrderNonrecurringCosts(scia.getFieldChangeOrderCost());
            completeBusinessCase.setInventoryReplaceNonrecurringCosts(scia.getInventory());
            completeBusinessCase.setFsToolingInvestments(scia.getFieldInvestment());
            completeBusinessCase.setSupplyChainManagementInvestments(scia.getSupplyChainManagementInvestment());
            completeBusinessCase.setRiskOnExcessAndObsolescence(scia.getInventoryAtRiskValue());
            completeBusinessCase.setRiskOnExcessAndObsolescenceReductionProposal(scia.getInventoryAtRiskReductionProposal());
            completeBusinessCase.setRiskOnExcessAndObsolescenceReductionProposalCosts(scia.getInventoryAtRiskReductionProposalCost());
            completeBusinessCaseService.update(completeBusinessCase);
        }

    }
}
