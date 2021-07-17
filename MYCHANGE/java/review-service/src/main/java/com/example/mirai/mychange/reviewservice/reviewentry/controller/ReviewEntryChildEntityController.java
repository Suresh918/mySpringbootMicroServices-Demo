package com.example.mirai.projectname.reviewservice.reviewentry.controller;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.core.model.EntityLink;
import com.example.mirai.libraries.entity.controller.ChildEntityController;
import com.example.mirai.projectname.reviewservice.core.component.EntityResolver;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntry;
import com.example.mirai.projectname.reviewservice.reviewentry.model.dto.ReviewEntryOverview;
import com.example.mirai.projectname.reviewservice.reviewentry.service.ReviewEntryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("{parentType:reviews}/{parentId}/{entityType:review-entries}")
public class ReviewEntryChildEntityController extends ChildEntityController {

    public ReviewEntryChildEntityController(ObjectMapper objectMapper, ReviewEntryService reviewEntryService,
                                            EntityResolver entityResolver) {
        super(objectMapper, reviewEntryService, entityResolver);
    }

    ReviewEntryService getService() {
        return ((ReviewEntryService) (super.entityServiceDefaultInterface));
    }


    @PostMapping(params = {"multiple=true"})
    @ResponseStatus(HttpStatus.CREATED)
    public List<ReviewEntry> createMultipleReviewEntries(@RequestBody JsonNode jsonNode,
                                                         @PathVariable Long parentId) throws JsonProcessingException {
        Class parentEntityClass = Review.class;
        ArrayList<Object> entityIns = objectMapper.treeToValue(jsonNode, ArrayList.class);
        Set<EntityLink<BaseEntityInterface>> entityLinkSet = new HashSet<>();
        entityLinkSet.add(new EntityLink(parentId, parentEntityClass));
        return getService().createMultipleReviewEntry(entityIns, entityLinkSet);
    }

    @GetMapping(params = {"view=overview"})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseEntityList<ReviewEntryOverview> getReviewEntryOverview(@PathVariable Long parentId,
                                                                      @RequestParam(name = "criteria", defaultValue = "") String criteria,
                                                                      @RequestParam(name = "slice-select", defaultValue = "") String sliceSelect,
                                                                      @RequestParam(name = "get-case-actions", defaultValue = "false") Boolean isGetCaseActions,
                                                                      @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        Class parentClass = Review.class;
        EntityLink entityLink = new EntityLink<BaseEntityInterface>(parentId, parentClass);
        return getService().getReviewEntryOverview(entityLink, criteria, pageable, Optional.ofNullable(sliceSelect), isGetCaseActions);
    }

}
