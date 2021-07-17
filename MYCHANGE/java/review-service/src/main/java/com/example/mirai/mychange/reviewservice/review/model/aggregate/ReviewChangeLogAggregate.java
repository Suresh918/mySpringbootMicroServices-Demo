package com.example.mirai.projectname.reviewservice.review.model.aggregate;

import com.example.mirai.libraries.audit.model.ChangeLog;
import com.example.mirai.libraries.core.annotation.Aggregate;
import com.example.mirai.libraries.core.annotation.AggregateRoot;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.reviewtask.model.aggregate.ReviewTaskChangeLogAggregate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@Setter
@Getter
public class ReviewChangeLogAggregate implements AggregateInterface {
    @Aggregate
    private Set<ReviewTaskChangeLogAggregate> reviewTasksChangeLog;
    @AggregateRoot
    @EntityClass(Review.class)
    private ChangeLog reviewChangeLog;
}
