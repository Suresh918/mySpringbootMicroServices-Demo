package com.example.mirai.projectname.reviewservice.core.component;

import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.StatusInterface;
import com.example.mirai.libraries.entity.service.EntityResolverDefaultInterface;
import com.example.mirai.projectname.reviewservice.comment.model.ReviewEntryComment;
import com.example.mirai.projectname.reviewservice.document.model.ReviewEntryCommentDocument;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.review.model.ReviewStatus;
import com.example.mirai.projectname.reviewservice.review.model.aggregate.ReviewAggregate;
import com.example.mirai.projectname.reviewservice.review.model.aggregate.ReviewCaseStatusAggregate;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntry;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntryStatus;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTaskStatus;
import com.example.mirai.projectname.reviewservice.shared.utils.Constants;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class EntityResolver implements EntityResolverDefaultInterface {
    @Override
    public Class getEntityClass(String link) {
        if (Objects.nonNull(link)) {
            switch (link.toUpperCase()) {
                case Constants.REVIEWS:
                    return Review.class;
                case Constants.REVIEW_TASKS:
                    return ReviewTask.class;
                case Constants.REVIEW_ENTRIES:
                    return ReviewEntry.class;
                case "COMMENTS":
                    return ReviewEntryComment.class;
                case "DOCUMENTS":
                    return ReviewEntryCommentDocument.class;
                default:
                    return null;
            }
        }
        return null;
    }

    @Override
    public Class<? extends BaseEntityInterface> getEntityClass(String parentType, String entityType) {
        if (Objects.isNull(parentType) || parentType.length() == 0)
            return null;
        if (Objects.isNull(entityType) || entityType.length() == 0)
            return null;
        if (parentType.toUpperCase().equals("COMMENTS") && entityType.toUpperCase().equals("DOCUMENTS"))
            return ReviewEntryCommentDocument.class;
        return null;
    }

    @Override
    public Class<? extends AggregateInterface> getAggregateClass(String s, String s1) {
        return null;
    }


    public Class getCaseStatusAggregateClass(String link) {
        if (Objects.nonNull(link)) {
            switch (link.toUpperCase()) {
                case Constants.REVIEWS:
                case Constants.REVIEW_TASKS:
                case Constants.REVIEW_ENTRIES:
                    return ReviewCaseStatusAggregate.class;
                default:
                    return null;
            }
        }
        return null;
    }

    public Class getAggregateClass(String link) {
        if (Objects.nonNull(link)) {
            switch (link.toUpperCase()) {
                case Constants.REVIEWS:
                case Constants.REVIEW_TASKS:
                case Constants.REVIEW_ENTRIES:
                    return ReviewAggregate.class;
                default:
                    return null;
            }
        }
        return null;
    }

    @Override
    public StatusInterface[] getEntityStatuses(String link) {
        if (Objects.nonNull(link)) {
            switch (link.toUpperCase()) {
                case Constants.REVIEWS:
                    return ReviewStatus.values();
                case Constants.REVIEW_TASKS:
                    return ReviewTaskStatus.values();
                case Constants.REVIEW_ENTRIES:
                    return ReviewEntryStatus.values();
                default:
                    return null;
            }
        }
        return null;
    }

    @Override
    public StatusInterface[] getEntityStatuses(Class entityClass) {
        if (Objects.nonNull(entityClass)) {
            if (Review.class.equals(entityClass)) {
                return ReviewStatus.values();
            } else if (ReviewTask.class.equals(entityClass)) {
                return ReviewTaskStatus.values();
            } else if (ReviewEntry.class.equals(entityClass)) {
                return ReviewEntryStatus.values();
            }
            return null;
        }
        return null;
    }

}
