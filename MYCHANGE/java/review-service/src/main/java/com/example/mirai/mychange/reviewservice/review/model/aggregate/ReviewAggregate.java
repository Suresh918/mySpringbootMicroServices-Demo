package com.example.mirai.projectname.reviewservice.review.model.aggregate;

import com.example.mirai.libraries.core.annotation.Aggregate;
import com.example.mirai.libraries.core.annotation.AggregateRoot;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.reviewtask.model.aggregate.ReviewTaskAggregate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.util.Set;

@Immutable
@Getter
@Setter
public class ReviewAggregate implements AggregateInterface {
    @Aggregate
    private Set<ReviewTaskAggregate> reviewTasks;
    @AggregateRoot
    private Review review;
}
