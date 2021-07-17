package com.example.mirai.projectname.reviewservice.reviewtask.controller;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.entity.controller.EntityController;
import com.example.mirai.projectname.reviewservice.core.component.EntityResolver;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask;
import com.example.mirai.projectname.reviewservice.reviewtask.scheduler.ReviewTaskDueDateScheduler;
import com.example.mirai.projectname.reviewservice.reviewtask.service.ReviewTaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("reviews/{entityType:review-tasks}")
public class ReviewTaskEntityController extends EntityController {
    private ReviewTaskDueDateScheduler reviewTaskDueDateScheduler;

    public ReviewTaskEntityController(ObjectMapper objectMapper, ReviewTaskService reviewTaskService, EntityResolver entityResolver,
                                      ReviewTaskDueDateScheduler reviewTaskDueDateScheduler) {
        super(objectMapper, reviewTaskService, entityResolver);
        this.reviewTaskDueDateScheduler = reviewTaskDueDateScheduler;
    }

    @Override
    public Class<? extends BaseEntityInterface> getEntityClass() {
        return ReviewTask.class;
    }


    @PutMapping({
            "/scheduler/due-date-soon"
    })
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_administrator')")
    public void scheduleReviewTaskDueDateSoon() {
        reviewTaskDueDateScheduler.publishDueSoonReviewTasks();
    }

    @PutMapping({
            "/scheduler/due-date-expired"
    })
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_administrator')")
    public void scheduleReviewTaskDueDateExpired() {
        reviewTaskDueDateScheduler.publishDueSoonReviewTasks();
    }

}
