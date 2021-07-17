package com.example.mirai.projectname.reviewservice.review.service;

import com.example.mirai.libraries.audit.component.AuditableUserAware;
import com.example.mirai.libraries.audit.model.AuditableUpdater;
import com.example.mirai.libraries.audit.service.AuditServiceDefaultInterface;
import com.example.mirai.libraries.core.annotation.*;
import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.exception.CaseActionNotFoundException;
import com.example.mirai.libraries.core.model.*;
import com.example.mirai.libraries.entity.model.StatusOverview;
import com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface;
import com.example.mirai.libraries.event.annotation.PublishResponse;
import com.example.mirai.libraries.sapmdg.changerequest.model.DeleteMaterialResponse;
import com.example.mirai.libraries.sapmdg.changerequest.service.SapMdgChangeRequestService;
import com.example.mirai.libraries.security.abac.AbacAwareInterface;
import com.example.mirai.libraries.security.abac.component.AbacProcessor;
import com.example.mirai.libraries.security.core.component.CaseActionList;
import com.example.mirai.libraries.security.core.component.EntityACL;
import com.example.mirai.libraries.security.core.component.PropertyACL;
import com.example.mirai.libraries.security.core.service.SecurityServiceDefaultInterface;
import com.example.mirai.libraries.security.rbac.RbacAwareInterface;
import com.example.mirai.libraries.security.rbac.component.RbacProcessor;
import com.example.mirai.libraries.util.Constants;
import com.example.mirai.projectname.libraries.model.Ecn;
import com.example.mirai.projectname.libraries.model.ReleasePackage;
import com.example.mirai.projectname.libraries.model.Teamcenter;
import com.example.mirai.projectname.reviewservice.review.helper.AuditHelper;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.review.model.ReviewCaseActions;
import com.example.mirai.projectname.reviewservice.review.model.ReviewContext;
import com.example.mirai.projectname.reviewservice.review.model.ReviewStatus;
import com.example.mirai.projectname.reviewservice.review.model.aggregate.ReviewAggregate;
import com.example.mirai.projectname.reviewservice.review.model.aggregate.ReviewCaseStatusAggregate;
import com.example.mirai.projectname.reviewservice.review.model.aggregate.ReviewChangeLogAggregate;
import com.example.mirai.projectname.reviewservice.review.model.dto.ReviewOverview;
import com.example.mirai.projectname.reviewservice.review.model.dto.ReviewSummary;
import com.example.mirai.projectname.reviewservice.review.model.dto.ecn.MaterialDelta;
import com.example.mirai.projectname.reviewservice.review.repository.ReviewRepository;
import com.example.mirai.projectname.reviewservice.reviewentry.service.ReviewEntryService;
import com.example.mirai.projectname.reviewservice.reviewtask.service.ReviewTaskService;
import com.example.mirai.projectname.reviewservice.shared.exception.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@EntityClass(Review.class)
public class ReviewService implements EntityServiceDefaultInterface, SecurityServiceDefaultInterface, AuditServiceDefaultInterface {
    @Resource
    private ReviewService self;

    private ReviewRepository reviewRepository;
    @Autowired
    private ReviewTaskService reviewTaskService;
    @Autowired
    private ReviewEntryService reviewEntryService;

    private EcnReviewService ecnReviewService;
    private SapMdgChangeRequestService sapMdgChangeRequestService;

    private ReviewStateMachine stateMachine;
    private AbacProcessor abacProcessor;
    private RbacProcessor rbacProcessor;
    private EntityACL acl;
    private PropertyACL pacl;
    private CaseActionList caseActionList;


    public ReviewService(ReviewStateMachine stateMachine, AbacProcessor abacProcessor, RbacProcessor rbacProcessor,
                         EntityACL acl, PropertyACL pacl, CaseActionList caseActionList, ReviewRepository reviewRepository, EcnReviewService ecnReviewService, SapMdgChangeRequestService sapMdgChangeRequestService) {
        this.stateMachine = stateMachine;
        this.abacProcessor = abacProcessor;
        this.rbacProcessor = rbacProcessor;
        this.acl = acl;
        this.pacl = pacl;
        this.caseActionList = caseActionList;
        this.reviewRepository = reviewRepository;
        this.ecnReviewService = ecnReviewService;
        this.sapMdgChangeRequestService = sapMdgChangeRequestService;
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
        return caseActionList;
    }

    @Override
    public AbacAwareInterface getABACAware() {
        return abacProcessor;
    }

    @Override
    public RbacAwareInterface getRBACAware() {
        return rbacProcessor;
    }

    @Override
    @SecureCaseAction("CREATE")
    @PublishResponse(eventType = "CREATE", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.review")
    @Transactional
    public Review create(BaseEntityInterface entity) {
        entity.setStatus(ReviewStatus.valueOf("OPENED").getStatusCode());
        stateMachine.checkForMandatoryFieldsAndSetStatusForCreate(entity);
        return (Review) EntityServiceDefaultInterface.super.create(entity);
    }

    @Override
    public BaseEntityInterface performCaseAction(Long aLong, String s) {
        return null;
    }


    @Transactional
    public Review createReviewMigrate(BaseEntityInterface entity, String createdOn, User creator) throws ParseException {
        Review review = (Review) EntityServiceDefaultInterface.super.create(entity);
        final SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
        review.setCreatedOn(formatter.parse(createdOn));
        review.setCreator(creator);
        return review;
    }

    @SecureCaseAction("CREATE-AGGREGATE")
    @PublishResponse(eventType = "CREATE-AGGREGATE", destination = "com.example.mirai.projectname.reviewservice.review", eventEntity = "com.example.mirai.projectname.reviewservice.review.model.Review")
    @Transactional
    public ReviewAggregate createReviewAggregate(ReviewAggregate aggregate) {
        //check for incomplete review
        if (this.incompleteReviewExistForReleasePackage(aggregate)) {
            throw new IncompleteReviewExistForReleasePackage();
        } else {
            return (ReviewAggregate) EntityServiceDefaultInterface.super.createRootAggregate(aggregate);
        }
    }

    private boolean incompleteReviewExistForReleasePackage(ReviewAggregate reviewAggregate) {
        List<ReviewContext> reviewContexts = reviewAggregate.getReview().getContexts();
        Optional<ReviewContext> releasePackageContext = reviewContexts.stream().filter(reviewContext -> reviewContext.getType().equals("RELEASEPACKAGE")).findFirst();
        String criteria = "contexts.type:'RELEASEPACKAGE' and contexts.contextId:'" + releasePackageContext.get().getContextId() + "'";
        Pageable pageable = PageRequest.of(0, 3);
        BaseEntityList baseEntityList = filter(criteria, pageable);
        List<Review> reviewList = baseEntityList.getResults();
        //if no reviews linked to release package, return true
        if (reviewList.isEmpty()) {
            return false;
        }
        //if reviews exists, check for status
        List<Review> incompleteReviews = reviewList.stream().filter(review -> (!review.getStatus().equals(ReviewStatus.COMPLETED.getStatusCode()))).collect(Collectors.toList());
        return !incompleteReviews.isEmpty();
    }

    @Override
    @SecureCaseAction("READ")
    public BaseEntityInterface get(Long id) {
        return EntityServiceDefaultInterface.super.get(id);
    }

    @SneakyThrows
    public AggregateInterface performCaseActionOnEntity(Long id, String action, Class<AggregateInterface> aggregateClass, boolean forceComplete) {
        Review updatedEntity = (Review) performCaseAction(id, action, forceComplete);
        return getCaseStatusAggregate(updatedEntity.getId(), aggregateClass);
    }

    @SecureFetchAction
    @Override
    public StatusOverview getStatusOverview(@SecureFetchCriteria String criteria, String viewCriteria, StatusInterface[] statuses, Optional<Class> viewClass) {
        return EntityServiceDefaultInterface.super.getStatusOverview(criteria, viewCriteria, statuses, Optional.of(ReviewOverview.class));
    }

    public AggregateInterface performCompleteAfterDeleteMaterialAndGetCaseStatus(Long id, boolean forceComplete) {
        Review entity = (Review) self.getEntityById(id);
        Review updatedEntity = self.completeAfterDeleteMaterials(entity, forceComplete);
        return getCaseStatusAggregate(updatedEntity.getId(), (Class) ReviewCaseStatusAggregate.class);
    }

    public BaseEntityInterface performCaseAction(Long id, String action, boolean forceComplete) {
        ReviewCaseActions caseAction = null;
        BaseEntityInterface entity = self.getEntityById(id);
        try {
            caseAction = ReviewCaseActions.valueOf(action.toUpperCase());
        } catch (Exception e) {
            throw new CaseActionNotFoundException();
        }
        switch (caseAction) {
            case LOCK:
                return self.lock(entity);
            case STARTVALIDATION:
                return self.startValidation(entity);
            case COMPLETE:
                return self.complete(entity, forceComplete);
            case REOPEN:
                return self.reopen(entity);
            default:
                throw new CaseActionNotFoundException();
        }
    }

    @Override
    public CaseStatus performCaseActionAndGetCaseStatus(Long aLong, String s) {
        return null;
    }

    @Override
    public AggregateInterface performCaseActionAndGetCaseStatusAggregate(Long aLong, String s, Class<AggregateInterface> aClass) {
        return null;
    }

    public CaseStatus getCaseStatus(BaseEntityInterface review) {
        CaseStatus caseStatus = new CaseStatus();
        caseStatus.setId(review.getId());
        caseStatus.setStatus(review.getStatus());
        caseStatus.setStatusLabel(ReviewStatus.getLabelByCode(((Review) review).getStatus()));
        caseStatus.setCasePermissions(new CasePermissions(getCaseActions(review), getCaseProperties(review)));
        return caseStatus;
    }

    @Override
    @SecureCaseAction("UPDATE")
    @SecurePropertyMerge
    @PublishResponse(eventType = "MERGE", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.review")
    @Transactional
    public Review merge(BaseEntityInterface newInst, BaseEntityInterface oldInst, List<String> oldInsChangedAttributeNames, List<String> newInsChangedAttributeNames) {
        return (Review) EntityServiceDefaultInterface.super.merge(newInst, oldInst, oldInsChangedAttributeNames, newInsChangedAttributeNames);
    }

    @SecureCaseAction("LOCK")
    @PublishResponse(eventType = "LOCK", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.review")
    @Transactional
    public Review lock(BaseEntityInterface entity) {
        EntityUpdate entityUpdate = stateMachine.lock(entity);
        reviewTaskService.lockReviewTasksByReviewId(entity.getId());
        return (Review) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    @SecureCaseAction("STARTVALIDATION")
    @PublishResponse(eventType = "STARTVALIDATION", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.review")
    @Transactional
    public Review startValidation(BaseEntityInterface entity) {
        //lock review tasks only when comes from locked status but not from completed
        if (entity.getStatus().equals(ReviewStatus.LOCKED.getStatusCode())) {
            reviewTaskService.lockReviewTasksByReviewId(entity.getId());
        }
        EntityUpdate entityUpdate = stateMachine.startValidation(entity);
        return (Review) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    @SecureCaseAction("REOPEN")
    @PublishResponse(eventType = "REOPEN", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.review")
    @Transactional
    public Review reopen(BaseEntityInterface entity) {
        EntityUpdate entityUpdate = stateMachine.reopen(entity);
        reviewTaskService.unlockReviewTasksByReviewId(entity.getId());
        return (Review) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    @SecureCaseAction("COMPLETE")
    @PublishResponse(eventType = "COMPLETE", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.review")
    @Transactional
    public Review complete(BaseEntityInterface entity, boolean forceComplete) {
        if (!reviewEntryService.areAllReviewEntriesInFinalStatus(entity.getId()) && !forceComplete) {
            throw new ReviewCompletionException();
        }
        try {
            checkForMaterialDelta(entity);
        }catch (ZecnReviewException ex){
            log.info("No need to check Zecn for MaterialDelta ");
        }
        if (entity.getStatus().equals(ReviewStatus.VALIDATIONSTARTED.getStatusCode())) {
            reviewTaskService.lockReviewTasksByReviewId(entity.getId());
        }
        EntityUpdate entityUpdate = stateMachine.complete(entity);
        return (Review) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());

    }


    @SecureCaseAction("COMPLETE")
    @PublishResponse(eventType = "COMPLETE", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.review")
    @Transactional
    public Review completeAfterDeleteMaterials(BaseEntityInterface entity, boolean forceComplete) {
        if (!reviewEntryService.areAllReviewEntriesInFinalStatus(entity.getId()) && !forceComplete) {
            throw new ReviewCompletionException();
        }
        try {
            checkForMaterialDelta(entity);
        } catch (SapMdgAdditionalMaterialException e) {
            Optional<ReviewContext> optionalReviewContext = ((Review) entity).getContexts().stream().filter(context -> context.getType().equals("RELEASEPACKAGE")).findFirst();
            if (optionalReviewContext.isPresent()) {
                deleteMaterialsFromSapMdg(e.getItems(), optionalReviewContext.get().getContextId());
            }
        }
        if (entity.getStatus().equals(ReviewStatus.VALIDATIONSTARTED.getStatusCode())) {
            reviewTaskService.lockReviewTasksByReviewId(entity.getId());
        }
        EntityUpdate entityUpdate = stateMachine.complete(entity);
        return (Review) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    private void deleteMaterialsFromSapMdg(List<String> items, String rleleasePackageNumber) {
        DeleteMaterialResponse deleteMaterialResponse = sapMdgChangeRequestService.deleteMaterialByMaterialNumber(rleleasePackageNumber, items);
        if (deleteMaterialResponse.getStatus().equals("ERROR")) {
            throw new DeleteMaterialFailedException();
        }
    }

    private void checkForMaterialDelta(BaseEntityInterface entity) {
        List<MaterialDelta> materialDeltaList = ecnReviewService.getMaterialDeltaList(entity.getId());
        List<String> additionalItemsInSapMdg = new ArrayList<>();
        List<String> additionalItemsInTeamcenter = new ArrayList<>();
        materialDeltaList.stream().forEach(materialDelta -> {
            if (!Objects.isNull(materialDelta.getTeamcenterSolutionItem()) && Objects.isNull((materialDelta.getSapMdgSolutionItem()))) {
                additionalItemsInTeamcenter.add(materialDelta.getId());
            }
        });
        if (!additionalItemsInTeamcenter.isEmpty()) {
            throw new TeamcenterAdditionalSolutionItemException(additionalItemsInTeamcenter);
        }
        materialDeltaList.stream().forEach(materialDelta -> {
            if (Objects.isNull(materialDelta.getTeamcenterSolutionItem()) && !Objects.isNull((materialDelta.getSapMdgSolutionItem()))) {
                additionalItemsInSapMdg.add(materialDelta.getId());
            }
        });

        if (!additionalItemsInSapMdg.isEmpty()) {
            throw new SapMdgAdditionalMaterialException(additionalItemsInSapMdg);
        }
    }

    @SecureFetchAction
    public BaseEntityList<ReviewOverview> getReviewOverviews(@SecureFetchCriteria String criteria, String viewCriteria, Pageable pageable, Optional<String> sliceSelect) {
        Slice<BaseView> reviewOverviewList = EntityServiceDefaultInterface.super.getEntitiesFromView(criteria, viewCriteria, pageable, sliceSelect, ReviewOverview.class);
        return new BaseEntityList(reviewOverviewList);
    }

    @SecureFetchAction
    public BaseEntityList<ReviewSummary> getReviewSummaries(@SecureFetchCriteria String criteria, Pageable pageable, Optional<String> sliceSelect) {
        Slice<BaseView> reviewSummaryList = EntityServiceDefaultInterface.super.getEntitiesFromViewFilterOnEntity(criteria, pageable, sliceSelect, ReviewSummary.class);
        return new BaseEntityList(reviewSummaryList);
    }

    public AggregateInterface getAggregate(long id, Class<AggregateInterface> aggregateInterfaceClass) {
        return EntityServiceDefaultInterface.super.getAggregate(id, aggregateInterfaceClass);
    }

    @Transactional
    @SecureCaseAction("UPDATE")
    @SecurePropertyMerge
    @PublishResponse(eventType = "MERGE", destination = "com.example.mirai.projectname.reviewservice.model.reviewtask.ReviewTask",
            responseClass = ReviewAggregate.class, eventBuilder = AggregateEventBuilder.class, eventEntity = "com.example.mirai.projectname.reviewservice.review.model.Review")
    public ReviewAggregate updateReviewAggregate(Long id, JsonNode jsonNode) throws JsonProcessingException {
        return (ReviewAggregate) EntityServiceDefaultInterface.super.updateAggregate(id, jsonNode, ReviewAggregate.class);
    }

    @Override
    public AggregateInterface getChangeLogAggregate(long id, Class<AggregateInterface> aggregateClass, boolean includeDeleted) {
        ReviewChangeLogAggregate reviewChangeLogAggregate = (ReviewChangeLogAggregate) AuditServiceDefaultInterface.super.getChangeLogAggregate(id, aggregateClass, includeDeleted);
        return AuditHelper.handleAuditEntriesForContexts(reviewChangeLogAggregate);
    }

    @Transactional
    public void updateReleasePackageStatus(ReleasePackage releasePackage) {
        User user = new User();
        user.setUserId(releasePackage.getActorUserId());
        user.setAbbreviation(releasePackage.getActorAbbreviation());
        user.setDepartmentName(releasePackage.getActorDepartmentName());
        user.setEmail(releasePackage.getActorEmail());
        user.setFullName(releasePackage.getActorFullName());
        AuditableUserAware.AuditableUserHolder.user().set(user);
        Date eventTimestamp = releasePackage.getEventTimestamp();
        if (Objects.nonNull(releasePackage.getContextId())) {
            checkForContextsOfReview(eventTimestamp, releasePackage);
            SynchronizationContextInterface ecnContext = new Ecn(releasePackage,"ecn","ECN");
            checkForContextsOfReview(eventTimestamp, ecnContext);
            SynchronizationContextInterface teamcenterContext = new Teamcenter(releasePackage,"teamcenter","TEAMCENTER");
            checkForContextsOfReview(eventTimestamp, teamcenterContext);
        }
    }

    public void checkForContextsOfReview(Date eventTimestamp, SynchronizationContextInterface contextData) {
        Object contextStatus = contextData.getStatus();
        Object contextId = contextData.getContextId();//contextData.getId();
        String contextTitle = contextData.getTitle();
        String contextType = contextData.getType();
        String criteria = "contexts.type:" + contextType + " and contexts.contextId:'" + contextId + "'";
        Pageable pageable = PageRequest.of(0, 3);
        BaseEntityList baseEntityList = filter(criteria, pageable);
        List<Review> reviewList = baseEntityList.getResults();

        reviewList.stream().forEach(review -> {
            if (areValuesOrLastUpdatedOnVary(review, contextData, eventTimestamp)) {
                Review oldIns = new Review();
                oldIns.setContexts(review.getContexts());
                oldIns.setId(review.getId());
                Review newIns = new Review();
                List<ReviewContext> reviewContextList = new ArrayList<>();
                review.getContexts().stream().forEach(context -> {
                    ReviewContext reviewContext = new ReviewContext();
                    reviewContext.setContextId(context.getContextId());
                    reviewContext.setType(context.getType());
                    if (reviewContext.getContextId().equals(contextId) && reviewContext.getType().toUpperCase().equals(contextType)) {
                        if (Objects.nonNull(contextStatus))
                            reviewContext.setStatus(contextStatus.toString());
                        reviewContext.setName(contextTitle);
                    } else {
                        reviewContext.setStatus(context.getStatus());
                        reviewContext.setName(context.getName());
                    }
                    reviewContextList.add(reviewContext);
                });
                newIns.setContexts(reviewContextList);
                newIns.setId(review.getId());
                try {
                    EntityServiceDefaultInterface.super.merge(newIns, oldIns, Arrays.asList("contexts"), Arrays.asList("contexts"));
                } catch(Exception e) {
                    //TODO: added to handle aud updater not exists issue, can be removed once migration is completed properly
                    log.info(" unable to sync RP in review Jpa Exception for review " + review.getId() );
                    return;
                }
            }

        });
    }

    public boolean areValuesOrLastUpdatedOnVary(Review review, SynchronizationContextInterface contextData, Date eventTimestamp) {
        Optional<ReviewContext> reviewContext = review.getContexts().stream().filter(context -> context.getType().equals(contextData.getType()) && context.getContextId().equals(contextData.getContextId())).findFirst();
        if (reviewContext.isEmpty()) {
            return true;
        } else if (Objects.equals(reviewContext.get().getName(), contextData.getTitle()) && Objects.equals(reviewContext.get().getStatus(), contextData.getStatus())) {
            return false;
        }
        EntityManager entityManager = (EntityManager) ApplicationContextHolder.getBean(EntityManager.class);
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        AuditQuery auditQuery = auditReader.createQuery().forRevisionsOfEntityWithChanges(Review.class, true);
        auditQuery.add(AuditEntity.id().eq(review.getId()));
        auditQuery.add(AuditEntity.property("contexts").hasChanged());
        auditQuery.addOrder(AuditEntity.revisionNumber().desc());
        List<Object> revisions = auditQuery.getResultList();
        Date lastUpdatedOn = null;
        List<Object> contextRevisions = new ArrayList<>();
        for (Object revision : revisions) {
            Object[] properties = (Object[]) revision;
            AuditableUpdater auditableUpdater = (AuditableUpdater) properties[1];
            if (Objects.nonNull(auditableUpdater)) {
                Query query = entityManager.createNativeQuery("SELECT * FROM aud_review_contexts WHERE type = ?1 AND context_id=?2 AND rev=?3 AND revtype=?4");
                query.setParameter(1, contextData.getType());
                query.setParameter(2, contextData.getContextId());
                query.setParameter(3, auditableUpdater.getId());
                query.setParameter(4, 0);
                contextRevisions = query.getResultList();
                //updatedOn = auditReader.getRevisionDate(auditableUpdater.getId());
                lastUpdatedOn = new Date(auditableUpdater.getTimestamp());
                // if the context is found, break the loop, as the revisions are sorted on desc
                if (contextRevisions.size() > 0) {
                    break;
                }
            }
        }
        return Objects.isNull(lastUpdatedOn) ? true : eventTimestamp.compareTo(lastUpdatedOn) > 0;
    }

    @Transactional
    public List<BaseEntityInterface> getUpdatedReviewsInDuration(int days) {
        Long currentMilliseconds = System.currentTimeMillis();
        Timestamp startTimestamp = new Timestamp(currentMilliseconds);
        Timestamp endTimestamp = new Timestamp(currentMilliseconds + (days * 86400000));
        return AuditServiceDefaultInterface.super.getEntitiesUpdatedInDuration(startTimestamp, endTimestamp);
    }

    public List<Review> findReviewsByContextTypeAndId(String type, String contextId) {
        return reviewRepository.findAllReviewsByContextIdAndContextType(contextId, type);
    }


    @Transactional
    public List<Long> deleteReview(Long id) {
        List<Long> reviewEntryIds = reviewEntryService.getReviewEntriesForReview(id);
        reviewEntryIds.forEach(reviewEntryId -> reviewEntryService.deleteWithoutApplicationEventPublish(reviewEntryId));
        EntityServiceDefaultInterface.super.deleteWithoutApplicationEventPublish(id);
        return reviewEntryIds;
    }

    @Transactional
    public void deleteAuditEntries(Long id, List<Long> deletedReviewEntries) {
        //delete review audit entries
        //review contexts
        EntityManager entityManager = (EntityManager) ApplicationContextHolder.getBean(EntityManager.class);
        Query query = entityManager.createNativeQuery("DELETE FROM aud_review_contexts WHERE id = ?1");
        query.setParameter(1, id);
        query.executeUpdate();
        //review
        Query reviewQuery = entityManager.createNativeQuery("DELETE FROM aud_review WHERE id = ?1");
        reviewQuery.setParameter(1, id);
        reviewQuery.executeUpdate();

        //review task
        Query reviewTaskQuery = entityManager.createNativeQuery("DELETE FROM aud_review_task WHERE review_id = ?1");
        reviewTaskQuery.setParameter(1, id);
        reviewTaskQuery.executeUpdate();

        //review entry contexts
        deletedReviewEntries.forEach(reviewEntryId -> {
            Query reviewEntryContextsQuery = entityManager.createNativeQuery("DELETE FROM aud_review_entry_contexts WHERE id = ?1");
            reviewEntryContextsQuery.setParameter(1, reviewEntryId);
            reviewEntryContextsQuery.executeUpdate();
        });
        //review entry
        Query reviewEntryQuery = entityManager.createNativeQuery("DELETE FROM aud_review_entry WHERE review_id = ?1");
        reviewEntryQuery.setParameter(1, id);
        reviewEntryQuery.executeUpdate();
    }

    @SneakyThrows
    @Transactional
    public Review mergeEntityBySystemUser(BaseEntityInterface newInst, BaseEntityInterface oldInst, List<String> oldInsChangedAttributeNames, List<String> newInsChangedAttributeNames) {
        return (Review) EntityServiceDefaultInterface.super.merge(newInst, oldInst, oldInsChangedAttributeNames, newInsChangedAttributeNames);
    }
}
