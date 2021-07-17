package com.example.mirai.projectname.reviewservice.reviewtask.model.aggregate;

import com.example.mirai.libraries.core.annotation.Aggregate;
import com.example.mirai.libraries.core.annotation.LinkTo;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.reviewentry.model.aggregate.ReviewEntryAggregate;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ReviewTaskAggregate implements AggregateInterface {
    @Aggregate
    private Set<ReviewEntryAggregate> reviewEntries;
    @LinkTo({Review.class})
    private ReviewTask reviewTask;
}
