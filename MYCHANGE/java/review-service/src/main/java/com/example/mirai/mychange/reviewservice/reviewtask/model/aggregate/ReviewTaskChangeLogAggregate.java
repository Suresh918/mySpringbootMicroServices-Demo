package com.example.mirai.projectname.reviewservice.reviewtask.model.aggregate;

import com.example.mirai.libraries.audit.model.ChangeLog;
import com.example.mirai.libraries.core.annotation.Aggregate;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.annotation.LinkTo;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.reviewentry.model.aggregate.ReviewEntryChangeLogAggregate;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ReviewTaskChangeLogAggregate implements AggregateInterface {
    @Aggregate
    private Set<ReviewEntryChangeLogAggregate> reviewEntriesChangeLog;
    @LinkTo({Review.class})
    @EntityClass(ReviewTask.class)
    private ChangeLog reviewTaskChangeLog;
}
