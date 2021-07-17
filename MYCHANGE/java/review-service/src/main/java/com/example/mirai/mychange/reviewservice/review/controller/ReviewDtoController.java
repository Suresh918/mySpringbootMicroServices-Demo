package com.example.mirai.projectname.reviewservice.review.controller;

import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.entity.model.StatusOverview;
import com.example.mirai.projectname.reviewservice.core.component.EntityResolver;
import com.example.mirai.projectname.reviewservice.review.model.dto.ReviewOverview;
import com.example.mirai.projectname.reviewservice.review.model.dto.ReviewSummary;
import com.example.mirai.projectname.reviewservice.review.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Data
public class ReviewDtoController {

    private final ReviewService reviewService;
    private final EntityResolver entityResolver;
    private final ObjectMapper objectMapper;

    @GetMapping(value = "/reviews", params = "view=overview")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseEntityList<ReviewOverview> filterReviewOverview(@RequestParam(name = "criteria", defaultValue = "") String criteria,
                                                               @RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria,
                                                               @RequestParam(name = "slice-select", defaultValue = "") String sliceSelect,
                                                               @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        return reviewService.getReviewOverviews(criteria, viewCriteria, pageable, Optional.ofNullable(sliceSelect));
    }

    @GetMapping(value = "/reviews", params = "view=summary")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseEntityList<ReviewSummary> filterReviewSummary(@RequestParam(name = "criteria", defaultValue = "") String criteria,
                                                             @RequestParam(name = "slice-select", defaultValue = "") String sliceSelect,
                                                             @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        return reviewService.getReviewSummaries(criteria, pageable, Optional.ofNullable(sliceSelect));
    }


}
