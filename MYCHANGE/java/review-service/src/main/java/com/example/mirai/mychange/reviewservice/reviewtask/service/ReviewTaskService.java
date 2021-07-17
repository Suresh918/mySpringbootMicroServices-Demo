package com.example.mirai.projectname.reviewservice.reviewtask.service;

import com.example.mirai.libraries.audit.service.AuditServiceDefaultInterface;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.annotation.SecureCaseAction;
import com.example.mirai.libraries.core.annotation.SecureLinkedEntityCaseAction;
import com.example.mirai.libraries.core.annotation.SecurePropertyMerge;
import com.example.mirai.libraries.core.exception.CaseActionNotFoundException;
import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.libraries.core.exception.MandatoryFieldViolationException;
import com.example.mirai.libraries.core.model.*;
import com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface;
import com.example.mirai.libraries.event.annotation.PublishResponse;
import com.example.mirai.libraries.security.abac.AbacAwareInterface;
import com.example.mirai.libraries.security.abac.component.AbacProcessor;
import com.example.mirai.libraries.security.core.component.CaseActionList;
import com.example.mirai.libraries.security.core.component.EntityACL;
import com.example.mirai.libraries.security.core.component.PropertyACL;
import com.example.mirai.libraries.security.core.service.SecurityServiceDefaultInterface;
import com.example.mirai.libraries.security.rbac.RbacAwareInterface;
import com.example.mirai.libraries.security.rbac.component.RbacProcessor;
import com.example.mirai.libraries.util.Constants;
import com.example.mirai.projectname.reviewservice.core.component.AuthenticatedContext;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.review.model.aggregate.ReviewAggregate;
import com.example.mirai.projectname.reviewservice.review.service.ReviewService;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTaskCaseActions;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTaskStatus;
import com.example.mirai.projectname.reviewservice.reviewtask.model.dto.ReviewTaskSummaries;
import com.example.mirai.projectname.reviewservice.reviewtask.model.dto.ReviewTaskSummary;
import com.example.mirai.projectname.reviewservice.reviewtask.repository.ReviewTaskRepository;
import com.example.mirai.projectname.reviewservice.shared.exception.MandatoryFieldViolationForReviewTasksException;
import com.example.mirai.projectname.reviewservice.shared.exception.ReviewTaskExistException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@EntityClass(ReviewTask.class)
public class ReviewTaskService implements EntityServiceDefaultInterface, SecurityServiceDefaultInterface, AuditServiceDefaultInterface {
    @Autowired
    ReviewTaskRepository reviewTaskRepository;
    @Autowired
    ReviewService reviewService;
    @Autowired
    AuthenticatedContext auditorExtractorImpl;
    @Resource
    private ReviewTaskService self;
    private ReviewTaskStateMachine stateMachine;
    private AbacProcessor abacProcessor;
    private RbacProcessor rbacProcessor;
    private EntityACL acl;
    private PropertyACL pacl;

    public ReviewTaskService(ReviewTaskStateMachine stateMachine, AbacProcessor abacProcessor, RbacProcessor rbacProcessor,
                             EntityACL acl, PropertyACL pacl) {
        this.stateMachine = stateMachine;
        this.abacProcessor = abacProcessor;
        this.rbacProcessor = rbacProcessor;
        this.acl = acl;
        this.pacl = pacl;
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
    @SecureLinkedEntityCaseAction(caseAction = "CREATEREVIEWTASK")
    @PublishResponse(eventType = "CREATE", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.reviewtask")
    @Transactional
    public BaseEntityInterface createLinkedEntityWithLinks(BaseEntityInterface entity, Set<EntityLink<BaseEntityInterface>> entityLinkSet) {

        stateMachine.checkForMandatoryFieldsAndSetStatus(entity);
        if (entityLinkSet.iterator().next() != null) {
            BaseEntityInterface review = reviewService.getEntityById(entityLinkSet.iterator().next().getId());
            List<ReviewTask> reviewTasksWithCurrentAssignee = reviewTaskRepository.findByReviewAndAssignee_UserId((Review) review, ((ReviewTask) entity).getAssignee().getUserId());
            if (reviewTasksWithCurrentAssignee.size() > 0) {
                throw new ReviewTaskExistException();
            }
        }
        ReviewTask reviewTask = (ReviewTask) EntityServiceDefaultInterface.super.createLinkedEntityWithLinks(entity, entityLinkSet);
        return reviewTask;
    }

    @Transactional
    public ReviewTask createReviewTaskMigrate(BaseEntityInterface entity, Long reviewId, Class parentEntityClass, String createdOn, User creator) throws ParseException {
        Set<EntityLink<BaseEntityInterface>> entityLinkSet = new HashSet<>();
        entityLinkSet.add(new EntityLink(reviewId, parentEntityClass));
        ReviewTask reviewTask = (ReviewTask) createLinkedEntityWithLinks(entity, entityLinkSet);
        final SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
        reviewTask.setCreatedOn(formatter.parse(createdOn));
        reviewTask.setCreator(creator);
        return reviewTask;
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
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.reviewtask")
    @Transactional
    public ReviewTask merge(BaseEntityInterface newInst, BaseEntityInterface oldInst, List<String> oldInsChangedAttrNames, List<String> newInsChangedAttrNames) {
        return (ReviewTask) EntityServiceDefaultInterface.super.merge(newInst, oldInst, oldInsChangedAttrNames, newInsChangedAttrNames);
    }

    @Override
    public CaseStatus performCaseActionAndGetCaseStatus(Long id, String action) {
        ReviewTask updatedEntity = (ReviewTask) performCaseAction(id, action);
        return getCaseStatus(updatedEntity);
    }

    @Override
    public AggregateInterface performCaseActionAndGetCaseStatusAggregate(Long aLong, String s, Class<AggregateInterface> aClass) {
        return null;
    }

    public BaseEntityInterface performCaseAction(Long id, String action) {
        ReviewTaskCaseActions caseAction = null;
        try {
            caseAction = ReviewTaskCaseActions.valueOf(action.toUpperCase());
        } catch (Exception e) {
            throw new CaseActionNotFoundException();
        }
        BaseEntityInterface entity = self.getEntityById(id);
        switch (caseAction) {
            case ACCEPT:
                return self.accept(entity);
            case REJECT:
                return self.reject(entity);
            case COMPLETE:
                return self.complete(entity);
            case LOCK:
                return self.lock(entity);
            case UNLOCK:
                return self.unlock(entity);
            case REOPEN:
                return self.reopen(entity);
            default:
                throw new InternalAssertionException("Invalid Case Action");
        }
    }

    public CaseStatus getCaseStatus(BaseEntityInterface updatedEntity) {
        CaseStatus caseStatus = new CaseStatus();
        caseStatus.setId(updatedEntity.getId());
        caseStatus.setStatus(updatedEntity.getStatus());
        caseStatus.setStatusLabel(ReviewTaskStatus.getLabelByCode(((ReviewTask) updatedEntity).getStatus()));
        Set<CaseAction> caseActions = getCaseActions(updatedEntity);
        CaseProperty caseProperties = getCaseProperties(updatedEntity);
        caseStatus.setCasePermissions(new CasePermissions(caseActions, caseProperties));
        return caseStatus;
    }

    @SecureCaseAction("ACCEPT")
    @PublishResponse(eventType = "ACCEPT", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.reviewtask")
    @Transactional
    public ReviewTask accept(BaseEntityInterface entity) {
        EntityUpdate entityUpdate = stateMachine.accept(entity);
        return (ReviewTask) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    @SecureCaseAction("REJECT")
    @PublishResponse(eventType = "REJECT", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.reviewtask")
    @Transactional
    public ReviewTask reject(BaseEntityInterface entity) {
        EntityUpdate entityUpdate = stateMachine.reject(entity);
        return (ReviewTask) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    @SecureCaseAction("COMPLETE")
    @PublishResponse(eventType = "COMPLETE", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.reviewtask")
    @Transactional
    public ReviewTask complete(BaseEntityInterface entity) {
        EntityUpdate entityUpdate = stateMachine.complete(entity);
        return (ReviewTask) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    @SecureCaseAction("LOCK")
    @Transactional
    public ReviewTask lock(BaseEntityInterface entity) {
        EntityUpdate entityUpdate = stateMachine.lock(entity);
        return (ReviewTask) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    @SecureCaseAction("UNLOCK")
    @PublishResponse(eventType = "UNLOCK", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.reviewtask")
    @Transactional
    public ReviewTask unlock(BaseEntityInterface entity) {
        EntityUpdate entityUpdate = stateMachine.unlock(entity);
        return (ReviewTask) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    @SecureCaseAction("REOPEN")
    @PublishResponse(eventType = "REOPEN", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.reviewtask")
    @Transactional
    public ReviewTask reopen(BaseEntityInterface entity) {
        EntityUpdate entityUpdate = stateMachine.reopen(entity);
        return (ReviewTask) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    @SecureCaseAction("DELETE")
    @PublishResponse(eventType = "DELETE", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReviewAggregate.class, destination = "com.example.mirai.projectname.reviewservice.reviewtask")
    @Transactional
    public void delete(Long id) {
        EntityServiceDefaultInterface.super.delete(id);
    }

    public ReviewTaskSummaries getReviewTaskSummaries(EntityLink<BaseEntityInterface> entityLink, String criteria, Pageable pageable,
                                                      Optional<String> sliceSelect, Boolean isGetCaseActions) {
        if (!Objects.isNull(criteria) && criteria.length() > 0)
            criteria = "(review.id:" + entityLink.getId() + ") and (" + criteria + ")";
        else
            criteria = "(review.id:" + entityLink.getId() + ")";
        Slice<BaseView> reviewTaskSummarySlice = EntityServiceDefaultInterface.super.getEntitiesFromViewFilterOnEntity(criteria, pageable, sliceSelect, ReviewTaskSummary.class);
        List<BaseView> reviewTaskSummaryList = reviewTaskSummarySlice.getContent();
        if (isGetCaseActions) {
            reviewTaskSummaryList.stream().forEach(baseView -> {
                Long reviewTaskId = ((ReviewTaskSummary) baseView).getId();
                Set<CaseAction> caseActions = getCaseActions(reviewTaskId);
                CaseProperty caseProperty = getCaseProperties(reviewTaskId);
                ((ReviewTaskSummary) baseView).setCasePermissions(new CasePermissions(caseActions, caseProperty));
            });
        }
        Long completedReviewTaskCount = reviewTaskSummaryList.stream().filter(reviewSummary -> ((ReviewTaskSummary) reviewSummary).getStatus().equals("COMPLETED")).count();
        reviewTaskSummaryList = reArrangeReviewTaskSummary(reviewTaskSummaryList);
        ReviewTaskSummaries reviewTaskSummaries = new ReviewTaskSummaries();
        reviewTaskSummaries.setCompletedReviewTaskCount(completedReviewTaskCount.intValue());
        reviewTaskSummaries.setReviewTaskCount(reviewTaskSummaryList.size());
        reviewTaskSummaries.setReviewTaskSummaries(reviewTaskSummaryList);
        return reviewTaskSummaries;
    }

    private List<BaseView> reArrangeReviewTaskSummary(List<BaseView> reviewerSummaryList) {
        User auditor = auditorExtractorImpl.getAuditableUser();
        List<BaseView> reviewersList = new ArrayList(Arrays.asList(reviewerSummaryList.toArray()));
        Optional<BaseView> loggedInUserAsReviewer = reviewersList.stream().filter(reviewer -> ((ReviewTaskSummary) reviewer).getUserId().equals(auditor.getUserId())).findFirst();
        if (!loggedInUserAsReviewer.isEmpty()) {
            reviewersList.remove(loggedInUserAsReviewer.get());
            reviewersList.add(0, loggedInUserAsReviewer.get());
        }
        return reviewersList;
    }

    public BaseEntityList<ReviewTaskSummary> getReviewTaskSummaries1(EntityLink<BaseEntityInterface> entityLink, String criteria, Pageable pageable,
                                                                     Optional<String> sliceSelect, Boolean isGetCaseActions) {
        if (!Objects.isNull(criteria) && criteria.length() > 0)
            criteria = "( review.id:" + entityLink.getId() + " ) and ( " + criteria + " )";
        else
            criteria = "( review.id:" + entityLink.getId() + " )";
        Slice<BaseView> reviewTaskSummaryList = EntityServiceDefaultInterface.super.getEntitiesFromViewFilterOnEntity(criteria, pageable, sliceSelect, ReviewTaskSummary.class);
        if (isGetCaseActions) {
            List<BaseView> reviewTaskSummaries = reviewTaskSummaryList.getContent();
            reviewTaskSummaries.stream().forEach(baseView -> {
                Long reviewTaskId = ((ReviewTaskSummary) baseView).getId();

            });
        }
        return new BaseEntityList(reviewTaskSummaryList);
    }

    public AggregateInterface getAggregate(long id, Class<AggregateInterface> aggregateInterfaceClass) {
        return EntityServiceDefaultInterface.super.getAggregate(id, aggregateInterfaceClass);
    }

    public List<ReviewTask> findByReviewAndAssignee_UserId(Review review, String assigneeUserId) {
        return reviewTaskRepository.findByReviewAndAssignee_UserId(review, assigneeUserId);
    }

    public List<ReviewTask> findReviewTasksByReviewIdAndAssigneeUserId(Long reviewId, String userId) {
        return reviewTaskRepository.findReviewTasksByReviewIdAndAssigneeUserId(reviewId, userId);
    }

    public List<ReviewTask> getIncompleteReviewTasksWithDueDateSoon(int days) {
        Long currentMilliseconds = System.currentTimeMillis();
        Timestamp startTimestamp = new Timestamp(currentMilliseconds);
        Timestamp endTimestamp = new Timestamp(currentMilliseconds + (days * 86400000));
        List<Integer> statusList = new ArrayList<>();
        statusList.add(1);
        statusList.add(2);
        List<ReviewTask> reviewTasks = reviewTaskRepository.findReviewTasksByStatusInAndDueDateBetween(statusList, startTimestamp, endTimestamp);
        return reviewTasks.stream().filter(reviewTask -> reviewTask.getReview().getStatus() != 4).collect(Collectors.toList());
    }

    public List<ReviewTask> getIncompleteReviewTasksWithDueDateExpired() {
        Timestamp startTimestamp = new Timestamp(System.currentTimeMillis());
        List<Integer> statusList = new ArrayList<>();
        statusList.add(1);
        statusList.add(2);
        List<ReviewTask> reviewTasks = reviewTaskRepository.findReviewTasksByStatusInAndDueDateLessThan(statusList, startTimestamp);
        return reviewTasks.stream().filter(reviewTask -> reviewTask.getReview().getStatus() != 4).collect(Collectors.toList());
    }

    public void lockReviewTasksByReviewId(Long reviewId) {
        //lock only those review tasks which are in open and accepted
        Integer[] statusesToInclude = new Integer[]{ReviewTaskStatus.OPENED.getStatusCode(), ReviewTaskStatus.ACCEPTED.getStatusCode()};
        List<ReviewTask> reviewTasks = reviewTaskRepository.findReviewTasksByReviewIdAndInStatuses(reviewId, statusesToInclude);
        reviewTasks.stream().forEach(reviewTask -> {
            try {
                self.performCaseAction(reviewTask.getId(), ReviewTaskCaseActions.LOCK.name());
            } catch (MandatoryFieldViolationException e) {
                throw new MandatoryFieldViolationForReviewTasksException();
            } catch (Exception exception) {
                throw exception;
            }
        });
    }

    @Override
    @Transactional
    public List<BaseEntityInterface> mergeMultiple(ArrayNode arrayNode) {
        return EntityServiceDefaultInterface.super.mergeMultiple(arrayNode);
    }

    public void unlockReviewTasksByReviewId(Long reviewId) {
        // unlock only those review tasks which are in status not finalized and finalized
        Integer[] statuses = new Integer[]{ReviewTaskStatus.NOTFINALIZED.getStatusCode(), ReviewTaskStatus.FINALIZED.getStatusCode()};
        List<ReviewTask> reviewTasks = reviewTaskRepository.findReviewTasksByReviewIdAndInStatuses(reviewId, statuses);
        reviewTasks.stream().forEach(reviewTask -> {
            try {
                self.performCaseAction(reviewTask.getId(), ReviewTaskCaseActions.UNLOCK.name());
            } catch (MandatoryFieldViolationException e) {
                throw new MandatoryFieldViolationForReviewTasksException();
            } catch (Exception exception) {
                throw exception;
            }
        });
    }
}
