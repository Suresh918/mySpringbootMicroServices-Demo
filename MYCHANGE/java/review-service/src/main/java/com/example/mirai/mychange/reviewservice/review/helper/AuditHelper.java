package com.example.mirai.projectname.reviewservice.review.helper;

import com.example.mirai.libraries.audit.model.ChangeLog;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.projectname.reviewservice.review.model.ReviewContext;
import com.example.mirai.projectname.reviewservice.review.model.aggregate.ReviewChangeLogAggregate;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntryContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AuditHelper {

    public static AggregateInterface handleAuditEntriesForContexts(ReviewChangeLogAggregate reviewChangeLogAggregate) {
        List<ChangeLog.Entry> reviewChangeLogEntries = (List<ChangeLog.Entry>) ((ArrayList) reviewChangeLogAggregate.getReviewChangeLog().getEntries()).clone();
        reviewChangeLogEntries.forEach(entry -> {
            if ((entry.getOldValue() != null && Collection.class.isAssignableFrom(entry.getOldValue().getClass())) ||
                    (entry.getValue() != null && Collection.class.isAssignableFrom(entry.getValue().getClass()))) {
                handleAuditEntriesForCollection(entry, reviewChangeLogAggregate.getReviewChangeLog());
            }
        });
        reviewChangeLogAggregate.getReviewTasksChangeLog().forEach(reviewTaskChangeLogAggregate -> {
            if (Objects.nonNull(reviewTaskChangeLogAggregate.getReviewEntriesChangeLog())) {
                reviewTaskChangeLogAggregate.getReviewEntriesChangeLog().forEach(item -> {
                    List<ChangeLog.Entry> reviewEntryChangeLogEntries = (List<ChangeLog.Entry>) ((ArrayList) item.getReviewEntryChangeLog().getEntries()).clone();
                    reviewEntryChangeLogEntries.forEach(reviewEntryItemEntry -> {
                        if (reviewEntryItemEntry.getProperty().toUpperCase().equals("CONTEXTS")) {
                            List<ReviewEntryContext> newReviewEntryContexts = (List<ReviewEntryContext>) reviewEntryItemEntry.getValue();
                            List<ReviewEntryContext> oldReviewEntryContexts = (List<ReviewEntryContext>) reviewEntryItemEntry.getOldValue();
                            String oldValue = oldReviewEntryContexts != null ? oldReviewEntryContexts.stream().map(context -> context.getName()).collect(Collectors.joining(", ")) : "";
                            String value = newReviewEntryContexts != null ? newReviewEntryContexts.stream().map(context -> context.getName()).collect(Collectors.joining(", ")) : "";
                            reviewEntryItemEntry.setOldValue(oldValue);
                            reviewEntryItemEntry.setValue(value);
                            reviewEntryItemEntry.setProperty("Solution Items");
                        }
                    });
                });
            }
        });
        return reviewChangeLogAggregate;
    }

    private static void handleAuditEntriesForCollection(ChangeLog.Entry entry, ChangeLog reviewChangeLog) {
        Collection value = (Collection) entry.getValue();
        Collection oldValue = (Collection) entry.getOldValue();
        if (Objects.nonNull(value)) {
            value.stream().forEach(item -> {
                if (item instanceof ReviewContext) {
                    List<ReviewContext> reviewOldContext = (oldValue != null) ? (List<ReviewContext>) oldValue.stream().filter((oldItem) -> Objects.equals(oldItem, item)).collect(Collectors.toList()) : null;
                    String entryValue = ((ReviewContext) item).getContextId() + " - " + ((ReviewContext) item).getName();
                    String entryOldValue = "";
                    if (reviewOldContext != null && reviewOldContext.size() > 0 && item.equals(reviewOldContext.get(0))) {
                        entryOldValue = reviewOldContext.get(0).getContextId() + " - " + reviewOldContext.get(0).getName();
                    }
                    String property = entry.getProperty() + "." + ((ReviewContext) item).getType().toLowerCase();
                    reviewChangeLog.addEntry(entry.getUpdater(), entry.getUpdatedOn(), entry.getRevision(), entry.getRevisionType(), property, entryValue, entryOldValue, entry.getId());
                }
            });
        }
        reviewChangeLog.getEntries().remove(entry);
    }
}
