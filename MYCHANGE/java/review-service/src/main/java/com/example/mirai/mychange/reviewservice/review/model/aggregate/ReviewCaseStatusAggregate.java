package com.example.mirai.projectname.reviewservice.review.model.aggregate;

import com.example.mirai.libraries.core.annotation.Aggregate;
import com.example.mirai.libraries.core.annotation.AggregateRoot;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.CaseStatus;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.reviewtask.model.aggregate.ReviewTaskCaseStatusAggregate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@Setter
@Getter
public class ReviewCaseStatusAggregate implements AggregateInterface {
    @Aggregate
    private Set<ReviewTaskCaseStatusAggregate> reviewTasksCaseStatus;
    @AggregateRoot
    @EntityClass(Review.class)
    private CaseStatus reviewCaseStatus;
}
