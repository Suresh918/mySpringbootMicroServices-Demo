package com.example.mirai.projectname.reviewservice.reviewentry.model.aggregate;

import com.example.mirai.libraries.audit.model.ChangeLog;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.annotation.LinkTo;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntry;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewEntryChangeLogAggregate implements AggregateInterface {
    @LinkTo({Review.class, ReviewTask.class})
    @EntityClass(ReviewEntry.class)
    private ChangeLog reviewEntryChangeLog;
}
