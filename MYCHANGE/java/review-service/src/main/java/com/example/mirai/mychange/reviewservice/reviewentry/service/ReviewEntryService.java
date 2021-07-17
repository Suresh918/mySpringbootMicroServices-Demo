package com.example.mirai.projectname.reviewservice.reviewentry.service;

import com.example.mirai.libraries.audit.service.AuditServiceDefaultInterface;
import com.example.mirai.libraries.comment.model.CommentStatus;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.annotation.SecureCaseAction;
import com.example.mirai.libraries.core.annotation.SecureLinkedEntityCaseAction;
import com.example.mirai.libraries.core.annotation.SecurePropertyMerge;
import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.exception.CaseActionNotFoundException;
import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.libraries.core.model.*;
import com.example.mirai.libraries.entity.model.Id;
import com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.libraries.event.EventActorExtractorInterface;
import com.example.mirai.libraries.event.annotation.PublishResponse;
import com.example.mirai.libraries.security.abac.AbacAwareInterface;
import com.example.mirai.libraries.security.abac.component.AbacProcessor;
import com.example.mirai.libraries.security.core.component.CaseActionList;
import com.example.mirai.libraries.security.core.component.EntityACL;
import com.example.mirai.libraries.security.core.component.PropertyACL;
import com.example.mirai.libraries.security.core.service.SecurityServiceDefaultInterface;
import com.example.mirai.libraries.security.rbac.RbacAwareInterface;
import com.example.mirai.libraries.security.rbac.component.RbacProcessor;
import com.example.mirai.projectname.reviewservice.comment.service.ReviewEntryCommentService;
import com.example.mirai.projectname.reviewservice.core.component.AuthenticatedContext;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.review.model.aggregate.ReviewAggregate;
import com.example.mirai.projectname.reviewservice.review.service.ReviewService;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntry;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntryCaseActions;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntryStatus;
import com.example.mirai.projectname.reviewservice.reviewentry.model.dto.ReviewEntryOverview;
import com.example.mirai.projectname.reviewservice.reviewentry.repository.ReviewEntryRepository;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTaskStatus;
import com.example.mirai.projectname.reviewservice.reviewtask.service.ReviewTaskService;
import com.example.mirai.projectname.reviewservice.shared.exception.ReviewEntryDeleteNotPossibleException;
import com.example.mirai.projectname.reviewservice.shared.exception.ReviewTaskNotExistException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.jms.JMSException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@EntityClass(ReviewEntry.class)
public class ReviewEntryService implements EntityServiceDefaultInterface, SecurityServiceDefaultInterface, AuditServiceDefaultInterface {

    @Autowired
    ReviewEntryRepository reviewEntryRepository;
    @Autowired
    ReviewService reviewService;
    @Autowired
    ReviewEntryCommentService reviewEntryCommentService;
    @Resource
    private ReviewEntryService self;
    private AuthenticatedContext auditorExtractorImpl;
    private ReviewEntryStateMachine stateMachine;
    private ReviewTaskService reviewTaskService;
    private AbacProcessor abacProcessor;
    private RbacProcessor rbacProcessor;
    private EntityACL acl;
    private PropertyACL pacl;
    private ObjectMapper objectMapper;
    private final JmsTemplate jmsTemplate;

    public ReviewEntryService(AuthenticatedContext auditorExtractorImpl, ReviewEntryStateMachine stateMachine,
                              ReviewTaskService reviewTaskService, AbacProcessor abacProcessor, RbacProcessor rbacProcessor,
                              EntityACL acl, PropertyACL pacl, ObjectMapper objectMapper, JmsTemplate jmsTemplate) {
        this.auditorExtractorImpl = auditorExtractorImpl;
        this.stateMachine = stateMachine;
        this.reviewTaskService = reviewTaskService;
        this.abacProcessor = abacProcessor;
        this.rbacProcessor = rbacProcessor;
        this.acl = acl;
        this.pacl = pacl;
        this.objectMapper = objectMapper;
        this.jmsTemplate = jmsTemplate;
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
        return stateMachine.getCaseActionList();
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
    @SecureCaseAction("READ")
    public BaseEntityInterface get(Long id) {
        return EntityServiceDefaultInterface.super.get(id);
    }

    @Override
    @SecureCaseAction("UPDATE")
    @SecurePropertyMerge
    @PublishResponse(eventType = "MERGE", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.reviewentry")
    @Transactional
    public ReviewEntry merge(BaseEntityInterface newInst, BaseEntityInterface oldInst,
                             List<String> oldInsChangedAttributeNames, List<String> newInsChangedAttributeNames) {
        return (ReviewEntry) EntityServiceDefaultInterface.super.merge(newInst, oldInst, oldInsChangedAttributeNames, newInsChangedAttributeNames);
    }


    @SecureCaseAction("UPDATE")
    /*@PublishResponse(eventType = "UPDATE", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.reviewentry")*/
    @Transactional
    public ReviewEntry updateReviewEntry(BaseEntityInterface entity, Map<String, Object> newChangedAttrs) {
        //check while renaming this method to update => "update" is called from case actions
        ReviewEntry existingReviewEntry = (ReviewEntry) self.getEntityById(entity.getId());
        ReviewEntry updatedReviewEntry = (ReviewEntry) self.update(entity, newChangedAttrs);
        EventActorExtractorInterface eventActorExtractor = ApplicationContextHolder.getApplicationContext().getBean(EventActorExtractorInterface.class);
        User actor = eventActorExtractor.getEventActor();
        ReviewAggregate reviewAggregate = (ReviewAggregate) reviewService.getAggregate(existingReviewEntry.getReview().getId(), (Class) ReviewAggregate.class);
        Event event = new Event("UPDATE", "SUCCESS", getEntityClass().getName(),
                "com.example.mirai.projectname.reviewservice.reviewentry", actor, reviewAggregate, getChangedAttributes(existingReviewEntry, updatedReviewEntry, newChangedAttrs),
                System.currentTimeMillis());
        createAndSendMessage(event);
        return updatedReviewEntry;
    }

    public void createAndSendMessage(Event event) {
        String messageAsString = null;
        try {
            messageAsString = ObjectMapperUtil.getObjectMapper().writeValueAsString(event);
        } catch (JsonProcessingException jspe) {
            throw new RuntimeException("Unable to send message for myteam bulk update");
        }
        if (Objects.isNull(jmsTemplate))
            return;
        //inject jms template in consumer class, for this to work
        jmsTemplate.convertAndSend("com.example.mirai.projectname.reviewservice.reviewentry",
                messageAsString, new MessagePostProcessor() {
                    @Override
                    public javax.jms.Message
                    postProcessMessage(javax.jms.Message message) throws JMSException {
                        message.setStringProperty("type", "UPDATE");
                        message.setStringProperty("status", "SUCCESS");
                        message.setStringProperty("entity", getEntityClass().getName());
                        message.setStringProperty("payload", event.getPayload());
                        message.setLongProperty("timestamp", System.currentTimeMillis());
                        message.setBooleanProperty("JMS_TIBCO_PRESERVE_UNDELIVERED", true);
                        return message;
                    }
                });
    }

    private Map<String, Map<String, Object>> getChangedAttributes(ReviewEntry oldInst, ReviewEntry newInst, Map<String, Object> newChangedAttrs) {
        Map changedAttributes = new HashMap<>();
        List<String> newInsChangedAttributeNames = objectMapper.convertValue(newChangedAttrs.keySet(), List.class);
        Map<String, Object> oldInstMap = objectMapper.convertValue(oldInst, Map.class);
        Map<String, Object> newInstMap = objectMapper.convertValue(newInst, Map.class);

        for (String fieldName : newInsChangedAttributeNames) {
            if (newInstMap.containsKey(fieldName)) {
                Object oldValue = oldInstMap.get(fieldName);
                Object newValue = newInstMap.get(fieldName);
                if (!Objects.deepEquals(oldValue,newValue)) {
                    Map valuesMap = new HashMap<>();
                    valuesMap.put("oldValue", oldValue);
                    valuesMap.put("newValue", newValue);
                    changedAttributes.put(fieldName, valuesMap);
                }
            }
        }
        return changedAttributes;
    }

    @Override
    @SecureLinkedEntityCaseAction(caseAction = "CREATEREVIEWENTRY", links = {ReviewTask.class, Review.class})
    @Transactional
    public BaseEntityInterface createLinkedEntityWithLinks(BaseEntityInterface entity, Set<EntityLink<BaseEntityInterface>> entityLinkSet) {
        Long reviewId = entityLinkSet.iterator().next().getId();
        List<ReviewTask> reviewTasks = getReviewTasksByReviewId(reviewId);
        return self.createReviewEntry((ReviewEntry) entity, reviewId, reviewTasks, entityLinkSet);
    }

    /*@SecureLinkedEntityCaseAction(caseAction = "CREATEREVIEWENTRY", links = {ReviewTask.class, Review.class})
    @Transactional
    public ReviewEntry createReviewEntry(BaseEntityInterface entity, Set<EntityLink<BaseEntityInterface>> entityLinkSet) {
        Long reviewId = entityLinkSet.iterator().next().getId();
        List<ReviewTask> reviewTasks = getReviewTasksByReviewId(reviewId);
        return self.createReviewEntry((ReviewEntry) entity, reviewId, reviewTasks, entityLinkSet);
    }*/

    @PublishResponse(eventType = "CREATE", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.reviewentry")
    public ReviewEntry createReviewEntry(ReviewEntry entity, Long reviewId, List<ReviewTask> reviewTasks, Set<EntityLink<BaseEntityInterface>> entityLinkSet) {
        stateMachine.checkForMandatoryFieldsAndSetStatusForCreate(entity);
        synchronized (this) {
            entity.setSequenceNumber(getSequenceNumber(reviewId));
        }
        if (reviewTasks.size() > 0) {
            ReviewTask reviewTask = reviewTasks.get(0);
            entityLinkSet.removeIf(item -> item.getId() == reviewTask.getId() && item.getEClass().equals(ReviewTask.class));
            entityLinkSet.add(new EntityLink(reviewTask.getId(), ReviewTask.class));
            if (reviewTask.getStatus() == ReviewTaskStatus.valueOf("OPENED").getStatusCode()) {
                reviewTaskService.performCaseAction(reviewTask.getId(), "ACCEPT");
            }
        }
        return (ReviewEntry) EntityServiceDefaultInterface.super.createLinkedEntityWithLinks(entity, entityLinkSet);
    }

    public List<ReviewTask> getReviewTasksByReviewId(Long reviewId) {
        User auditor = auditorExtractorImpl.getAuditableUser();
        List<ReviewTask> reviewTasks = reviewTaskService.findByReviewAndAssignee_UserId((Review) reviewService.getEntityById(reviewId), auditor.getUserId());
        if (reviewTasks.size() == 0) {
            throw new ReviewTaskNotExistException();
        }
        return reviewTasks;
    }

    @SecureLinkedEntityCaseAction(caseAction = "CREATEREVIEWENTRY", links = {ReviewTask.class, Review.class})
    @Transactional
    public List<ReviewEntry> createMultipleReviewEntry(ArrayList<Object> entities, Set<EntityLink<BaseEntityInterface>> entityLinkSet) {
        List<ReviewEntry> reviewEntries = new ArrayList<>();
        Long reviewId = entityLinkSet.iterator().next().getId();
        List<ReviewTask> reviewTasks = getReviewTasksByReviewId(reviewId);
        entities.forEach(entity -> {
            ReviewEntry reviewEntry = objectMapper.convertValue(entity, ReviewEntry.class);
            reviewEntries.add(self.createReviewEntry(reviewEntry, reviewId, reviewTasks, entityLinkSet));
        });
        return reviewEntries;
    }

    private int getSequenceNumber(Long reviewId) {
        int sequenceNumber = 1;
        List<Integer> sequenceNumbers = reviewEntryRepository.findByReviewAndOrderBySequenceNumberDesc(reviewId);
        if (sequenceNumbers.size() > 0 && sequenceNumbers.get(0) != null) {
            sequenceNumber = sequenceNumbers.get(0) + 1;
        }
        return sequenceNumber;
    }

    public CaseStatus performCaseActionAndGetCaseStatus(Long id, String action) {
        ReviewEntry updatedEntity = (ReviewEntry) this.performCaseAction(id, action);
        return getCaseStatus(updatedEntity);
    }

    @Override
    public AggregateInterface performCaseActionAndGetCaseStatusAggregate(Long aLong, String s, Class<AggregateInterface> aClass) {
        return null;
    }

    public BaseEntityInterface performCaseAction(Long id, String action) {
        BaseEntityInterface entity = self.getEntityById(id);
        ReviewEntryCaseActions caseAction = null;
        try {
            caseAction = ReviewEntryCaseActions.valueOf(action.toUpperCase());
        } catch (Exception e) {
            new CaseActionNotFoundException();
        }
        switch (caseAction) {
            case ACCEPT:
                return self.accept(entity);
            case REJECT:
                return self.reject(entity);
            case MARKDUPLICATE:
                return self.markDuplicate(entity);
            case REOPEN:
                return self.reopen(entity);
            case COMPLETE:
                return self.complete(entity);
            default:
                throw new InternalAssertionException("Invalid Case Action");
        }
    }

    public CaseStatus getCaseStatus(BaseEntityInterface updatedEntity) {
        CaseStatus caseStatus = new CaseStatus();
        caseStatus.setId(updatedEntity.getId());
        caseStatus.setStatus(updatedEntity.getStatus());
        caseStatus.setStatusLabel(ReviewEntryStatus.getLabelByCode(((ReviewEntry) updatedEntity).getStatus()));
        Set<CaseAction> caseActions = getCaseActions(updatedEntity);
        CaseProperty caseProperties = getCaseProperties(updatedEntity);
        caseStatus.setCasePermissions(new CasePermissions(caseActions, caseProperties));
        return caseStatus;
    }

    @SecureCaseAction("ACCEPT")
    @PublishResponse(eventType = "ACCEPT", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.reviewentry")
    @Transactional
    public ReviewEntry accept(BaseEntityInterface entity) {
        EntityUpdate entityUpdate = stateMachine.accept(entity);
        return (ReviewEntry) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    @SecureCaseAction("REJECT")
    @PublishResponse(eventType = "REJECT", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.reviewentry")
    @Transactional
    public ReviewEntry reject(BaseEntityInterface entity) {
        EntityUpdate entityUpdate = stateMachine.reject(entity);
        return (ReviewEntry) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    @SecureCaseAction("COMPLETE")
    @PublishResponse(eventType = "COMPLETE", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.reviewentry")
    public ReviewEntry complete(BaseEntityInterface entity) {
        EntityUpdate entityUpdate = stateMachine.complete(entity);
        return (ReviewEntry) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    @SecureCaseAction("MARKDUPLICATE")
    @PublishResponse(eventType = "MARKDUPLICATE", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.reviewentry")
    @Transactional
    public ReviewEntry markDuplicate(BaseEntityInterface entity) {
        EntityUpdate entityUpdate = stateMachine.markDuplicate(entity);
        return (ReviewEntry) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    @SecureCaseAction("REOPEN")
    @PublishResponse(eventType = "REOPEN", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.reviewentry")
    @Transactional
    public ReviewEntry reopen(BaseEntityInterface entity) {
        EntityUpdate entityUpdate = stateMachine.reopen(entity);
        return (ReviewEntry) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    @SecureCaseAction("DELETE")
    @PublishResponse(eventType = "DELETE", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.reviewentry")
    @Transactional
    public void delete(Long id) {
        Slice<Id> idSlice = reviewEntryCommentService.filterIds("status:" + CommentStatus.PUBLISHED.getStatusCode() + " and reviewEntry.id:" + id, PageRequest.of(0, 1));
        if (Objects.nonNull(idSlice) && Objects.nonNull(idSlice.getContent()) && idSlice.getContent().size() > 0) {
            throw new ReviewEntryDeleteNotPossibleException();
        }
        EntityServiceDefaultInterface.super.delete(id);
    }

    public AggregateInterface getAggregate(long id, Class<AggregateInterface> aggregateInterfaceClass) {
        return EntityServiceDefaultInterface.super.getAggregate(id, aggregateInterfaceClass);
    }

    public List<ReviewEntry> findReviewEntriesByReviewTask(ReviewTask reviewTask, Pageable pageable) {
        return reviewEntryRepository.findReviewEntriesByReviewTask(reviewTask, pageable);
    }

    public BaseEntityList<ReviewEntryOverview> getReviewEntryOverview(EntityLink<BaseEntityInterface> entityLink, String criteria, Pageable pageable,
                                                                      Optional<String> sliceSelect, Boolean isGetCaseActions) {
        if (!Objects.isNull(criteria) && criteria.length() > 0)
            criteria = "(review.id:" + entityLink.getId() + ") and (" + criteria + ")";
        else
            criteria = "(review.id:" + entityLink.getId() + ")";
        Slice<BaseView> reviewEntryOverviewSlice = EntityServiceDefaultInterface.super.getEntitiesFromViewFilterOnEntity(criteria, pageable, sliceSelect, ReviewEntryOverview.class);
        List<BaseView> reviewEntryOverviewList = reviewEntryOverviewSlice.getContent();
        User auditor = auditorExtractorImpl.getAuditableUser();
        if (isGetCaseActions) {
            for (BaseView baseView : reviewEntryOverviewList) {
                Long reviewEntryId = ((ReviewEntryOverview) baseView).getId();
                Set<CaseAction> caseActions = getCaseActions(reviewEntryId);
                CaseProperty caseProperties = getCaseProperties(reviewEntryId);
                Integer commentCount = reviewEntryCommentService.getCommentsCountByReviewEntryIdAndAuditor(reviewEntryId, auditor.getUserId());
                ((ReviewEntryOverview) baseView).setCommentCount(commentCount);
                ((ReviewEntryOverview) baseView).setCasePermissions(new CasePermissions(caseActions, caseProperties));
            }
        }
        return new BaseEntityList(reviewEntryOverviewSlice);
    }

    public boolean areAllReviewEntriesInFinalStatus(Long reviewId) {
        List<Integer> reviewEntriesStatus = reviewEntryRepository.findReviewEntriesStatusByReviewId(reviewId);
        List<Integer> nonFinalStatuses = reviewEntriesStatus.stream().filter(status ->
                (status == ReviewEntryStatus.OPENED.getStatusCode() || status == ReviewEntryStatus.ACCEPTED.getStatusCode())
        ).collect(Collectors.toList());
        return nonFinalStatuses.size() == 0;
    }

    public List<Long> getReviewEntriesForReview(Long reviewId) {
        String criteria = "(review.id:" + reviewId + ")";
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE - 1);
        List<Long> ids = new ArrayList();
        Slice<Id> idSlice = this.filterIds(criteria, pageable);
        ids = idSlice.getContent().stream().map(id -> id.getValue()).collect(Collectors.toList());
        return ids;
    }
}
