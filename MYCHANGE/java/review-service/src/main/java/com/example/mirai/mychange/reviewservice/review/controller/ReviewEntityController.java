package com.example.mirai.projectname.reviewservice.review.controller;

import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.CaseStatus;
import com.example.mirai.libraries.entity.controller.EntityController;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.projectname.reviewservice.core.component.EntityResolver;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.review.model.aggregate.ReviewAggregate;
import com.example.mirai.projectname.reviewservice.review.model.aggregate.ReviewCaseStatusAggregate;
import com.example.mirai.projectname.reviewservice.review.scheduler.ReviewReconciliationScheduler;
import com.example.mirai.projectname.reviewservice.review.service.ReviewService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("{entityType:reviews}")
public class ReviewEntityController extends EntityController {

    @Autowired
    ReviewReconciliationScheduler reviewReconciliationScheduler;

    ReviewEntityController(ReviewService reviewService, EntityResolver entityResolver, ObjectMapper objectMapper) {
        super(objectMapper, reviewService, entityResolver);
    }

    ReviewService getService() {
        return ((ReviewService) (super.entityServiceDefaultInterface));
    }

    EntityResolver getEntityResolver() {
        return ((EntityResolver) (super.entityResolverDefaultInterface));
    }

    @Override
    public Class<? extends BaseEntityInterface> getEntityClass() {
        return Review.class;
    }

    @PatchMapping("/{id}/aggregate")
    @ResponseBody
    public ReviewAggregate updateAggregate(@PathVariable Long id, @RequestBody JsonNode jsonNode) throws JsonProcessingException {
        return getService().updateReviewAggregate(id, jsonNode);
    }

    @PatchMapping(value = "/do-nothing")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public AggregateInterface performCaseActionAndGetCaseStatusAggregate(@PathVariable String entityType, @PathVariable Long id, @RequestParam(name="case-action") String caseAction) {
        throw new RuntimeException();
    }


    @PatchMapping(value = "/{id}", params = {"view=case-status-aggregate", "case-action"})
    @ResponseStatus(HttpStatus.OK)
    public AggregateInterface performCaseActionAndGetCaseStatusAggregate(@PathVariable Long id, @RequestParam(name="case-action") String caseAction,
                                                               @RequestParam(name="force-complete", defaultValue = "false") boolean forceComplete) {
        return getService().performCaseActionOnEntity(id, caseAction, (Class) ReviewCaseStatusAggregate.class, forceComplete);
    }

    @PatchMapping(
            value = {"/{id}"},
            params = {"view=case-status-aggregate", "case-action=DELETE-MATERIAL-AND-COMPLETE"}
    )
    @ResponseStatus(HttpStatus.OK)
    public AggregateInterface performCaseActionAndGetPermissions(@PathVariable Long id, @RequestParam(name="case-action") String caseAction, @RequestParam(name="force-complete", defaultValue = "false") boolean forceComplete) {
        return getService().performCompleteAfterDeleteMaterialAndGetCaseStatus(id,  forceComplete);
    }


    @Override
    @DeleteMapping({"/{id}"})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_administrator')")
    public void delete(@PathVariable Long id) {
        List<Long> deleteReviewEntries = getService().deleteReview(id);
        getService().deleteAuditEntries(id, deleteReviewEntries);
    }

    @PutMapping({"/scheduler/reconciliation"})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_administrator')")
    public void scheduleReviewForReconciliation() {
        reviewReconciliationScheduler.publishReviewsForReconciliation();
    }

    @PatchMapping(value = "/{id}", params = "is-system-account=true")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_tibco')")
    public BaseEntityInterface mergeEntityBySystemUser(@PathVariable Long id, @RequestBody JsonNode jsonNode) throws JsonProcessingException {
        BaseEntityInterface oldIns = this.objectMapper.treeToValue(jsonNode.get("oldIns"), this.getEntityClass());
        BaseEntityInterface newIns = this.objectMapper.treeToValue(jsonNode.get("newIns"), this.getEntityClass());
        oldIns.setId(id);
        newIns.setId(id);
        List<String> oldInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("oldIns"));
        List<String> newInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("newIns"));
        return getService().mergeEntityBySystemUser(newIns, oldIns, oldInsChangedAttributeNames, newInsChangedAttributeNames);
    }

}
