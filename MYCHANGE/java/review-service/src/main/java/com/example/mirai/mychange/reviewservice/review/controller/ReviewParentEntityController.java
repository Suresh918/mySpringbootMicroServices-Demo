package com.example.mirai.projectname.reviewservice.review.controller;

import com.example.mirai.libraries.core.annotation.SecurePropertyRead;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.entity.controller.ParentEntityController;
import com.example.mirai.projectname.reviewservice.core.component.EntityResolver;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.review.model.aggregate.ReviewAggregate;
import com.example.mirai.projectname.reviewservice.review.service.ReviewService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping("reviews")
public class ReviewParentEntityController extends ParentEntityController {

    @Resource
    ReviewParentEntityController self;

    private ReviewService reviewService;
    private ObjectMapper objectMapper;

    ReviewParentEntityController(ReviewService reviewService, EntityResolver entityResolver, ObjectMapper objectMapper) {
        super(objectMapper, reviewService, entityResolver);
        this.reviewService = reviewService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Class<? extends BaseEntityInterface> getEntityClass() {
        return Review.class;
    }

    @PostMapping("/aggregate")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewAggregate createAggregate(@RequestBody JsonNode jsonNode) throws JsonProcessingException {
        return self.createReviewAggregate(jsonNode);
    }

    @SecurePropertyRead
    public ReviewAggregate createReviewAggregate(JsonNode jsonNode) throws JsonProcessingException {
        ReviewAggregate reviewAggregate = objectMapper.treeToValue(jsonNode, ReviewAggregate.class);
        return reviewService.createReviewAggregate(reviewAggregate);
    }
}
