package com.example.mirai.projectname.reviewservice.reviewtask.controller;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.EntityLink;
import com.example.mirai.libraries.entity.controller.ChildEntityController;
import com.example.mirai.projectname.reviewservice.core.component.EntityResolver;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.reviewentry.service.ReviewEntryService;
import com.example.mirai.projectname.reviewservice.reviewtask.model.dto.ReviewTaskSummaries;
import com.example.mirai.projectname.reviewservice.reviewtask.service.ReviewTaskService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("{parentType:reviews}/{parentId}/{entityType:review-tasks}")
public class ReviewTaskChildEntityController extends ChildEntityController {

    public ReviewTaskChildEntityController(ObjectMapper objectMapper, ReviewTaskService reviewTaskService,
                                           EntityResolver entityResolver) {
        super(objectMapper, reviewTaskService, entityResolver);
    }

    ReviewTaskService getService() {
        return ((ReviewTaskService) (super.entityServiceDefaultInterface));
    }

    @GetMapping(params = {"view=summary"})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ReviewTaskSummaries filterReviewTaskSummary(@PathVariable String parentType, @PathVariable Long parentId,
                                                       @PageableDefault(page = 0, value = Integer.MAX_VALUE) Pageable pageable,
                                                       @RequestParam(name = "criteria", defaultValue = "") String criteria,
                                                       @RequestParam(name = "slice-select", defaultValue = "") String sliceSelect,
                                                       @RequestParam(name = "get-case-actions", defaultValue = "false") Boolean isGetCaseActions) throws JsonProcessingException {
        Class parentClass = Review.class;
        EntityLink entityLink = new EntityLink<BaseEntityInterface>(parentId, parentClass);
        return getService().getReviewTaskSummaries(entityLink, criteria, pageable, Optional.ofNullable(sliceSelect), isGetCaseActions);
    }
}
