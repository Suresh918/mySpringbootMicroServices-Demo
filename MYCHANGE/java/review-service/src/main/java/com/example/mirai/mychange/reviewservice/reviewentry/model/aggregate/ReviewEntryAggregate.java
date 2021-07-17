package com.example.mirai.projectname.reviewservice.reviewentry.model.aggregate;

import com.example.mirai.libraries.core.annotation.LinkTo;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntry;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewEntryAggregate implements AggregateInterface {
    @LinkTo({Review.class, ReviewTask.class})
    private ReviewEntry reviewEntry;
}
