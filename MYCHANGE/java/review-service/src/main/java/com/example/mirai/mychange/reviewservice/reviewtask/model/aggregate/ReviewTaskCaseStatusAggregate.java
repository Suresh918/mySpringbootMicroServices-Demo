package com.example.mirai.projectname.reviewservice.reviewtask.model.aggregate;

import com.example.mirai.libraries.core.annotation.Aggregate;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.annotation.LinkTo;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.CaseStatus;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.reviewentry.model.aggregate.ReviewEntryCaseStatusAggregate;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ReviewTaskCaseStatusAggregate implements AggregateInterface {
    @Aggregate
    private Set<ReviewEntryCaseStatusAggregate> reviewEntriesCaseStatus;
    @LinkTo({Review.class})
    @EntityClass(ReviewTask.class)
    private CaseStatus reviewTaskCaseStatus;
}
