package com.example.mirai.projectname.reviewservice.tests.reviewentry;

import com.example.mirai.projectname.reviewservice.json.ReviewEntryJson;
import com.example.mirai.projectname.reviewservice.review.model.ReviewStatus;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntry;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntryStatus;
import com.example.mirai.projectname.reviewservice.tests.ExceptionValidator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class Validator {

    public static void reviewEntriesAreSameWithoutComparingAssignee(ReviewEntry reviewEntry1, ReviewEntry reviewEntry2) {
        if (reviewEntry1.getCreator() != null || reviewEntry2.getCreator() != null) {
            assertThat("creators are not same", reviewEntry1.getCreator(), samePropertyValuesAs(reviewEntry2.getCreator()));
        }
        assertThat("Status are not same", reviewEntry1.getStatus(), equalTo(reviewEntry2.getStatus()));
        assertThat("Description are not same", reviewEntry1.getDescription(), equalTo(reviewEntry2.getDescription()));
        assertThat("Classification are not same", reviewEntry1.getClassification(), equalTo(reviewEntry2.getClassification()));
        assertThat("Remark are not same", reviewEntry1.getRemark(), equalTo(reviewEntry2.getRemark()));
    }

    public static void reviewEntriesAreSameWithoutComparingClassification(ReviewEntry reviewEntry1, ReviewEntry reviewEntry2) {
        if (reviewEntry1.getCreator() != null || reviewEntry2.getCreator() != null) {
            assertThat("creators are not same", reviewEntry1.getCreator(), samePropertyValuesAs(reviewEntry2.getCreator()));
        }
        assertThat("Status are not same", reviewEntry1.getStatus(), equalTo(reviewEntry2.getStatus()));
        assertThat("Description are not same", reviewEntry1.getDescription(), equalTo(reviewEntry2.getDescription()));
        assertThat("Remark are not same", reviewEntry1.getRemark(), equalTo(reviewEntry2.getRemark()));
        if (reviewEntry1.getAssignee() != null || reviewEntry2.getAssignee() != null) {
            assertThat("Assignees are not same", reviewEntry1.getAssignee(), samePropertyValuesAs(reviewEntry2.getAssignee()));
        }

    }

    public static void reviewEntriesAreSameWithoutComparingDescription(ReviewEntry reviewEntry1, ReviewEntry reviewEntry2) {
        if (reviewEntry1.getCreator() != null || reviewEntry2.getCreator() != null) {
            assertThat("creators are not same", reviewEntry1.getCreator(), samePropertyValuesAs(reviewEntry2.getCreator()));
        }
        assertThat("Status are not same", reviewEntry1.getStatus(), equalTo(reviewEntry2.getStatus()));
        assertThat("Classification are not same", reviewEntry1.getClassification(), equalTo(reviewEntry2.getClassification()));
        assertThat("Remark are not same", reviewEntry1.getRemark(), equalTo(reviewEntry2.getRemark()));
        if (reviewEntry1.getAssignee() != null || reviewEntry2.getAssignee() != null) {
            assertThat("Assignees are not same", reviewEntry1.getAssignee(), samePropertyValuesAs(reviewEntry2.getAssignee()));
        }

    }

    public static void createReviewEntryIsSuccessful(ReviewEntry requestEntry, ReviewEntry savedEntry, ReviewEntryJson responseEntry) {

        assertThat("ids are not available", responseEntry.getId(), equalTo(savedEntry.getId()));
        assertThat("statuses are not same in response and saved review entry", responseEntry.getStatus(), equalTo(savedEntry.getStatus()));
        assertThat("status is not 'OPENED' in response", responseEntry.getStatus(), equalTo(ReviewStatus.valueOf("OPENED").getStatusCode()));
        reviewEntriesAreSameWithoutComparingAuditAndStatus(savedEntry, requestEntry);

    }

    public static void reviewEntriesAreSameWithoutComparingAuditAndStatus(ReviewEntry reviewEntry1, ReviewEntry reviewEntry2) {
        assertThat("Classification are not same", reviewEntry1.getClassification(), equalTo(reviewEntry2.getClassification()));
        assertThat("Description are not same", reviewEntry1.getDescription(), equalTo(reviewEntry2.getDescription()));
        assertThat("Remark is not same", reviewEntry1.getRemark(), equalTo(reviewEntry2.getRemark()));
        if (reviewEntry1.getAssignee() != null || reviewEntry2.getAssignee() != null) {
            assertThat("assignee are not same", reviewEntry1.getAssignee(), samePropertyValuesAs(reviewEntry2.getAssignee()));
        }
    }

    public static void unauthorizedExceptionAndReviewEntryDidNotChange(ReviewEntry reviewEntryBeforeCaseAction, ReviewEntry reviewEntryAfterCaseAction,

                                                                       com.example.mirai.projectname.reviewservice.json.ExceptionResponse exceptionResponse,
                                                                       String path, ReviewEntryStatus originalStatus) {
        ExceptionValidator.exceptionResponseIsUnauthorizedException(exceptionResponse, path);
        reviewEntriesAreSame(reviewEntryBeforeCaseAction, reviewEntryAfterCaseAction);
        assertThat("unauthorized case action has changed status", reviewEntryAfterCaseAction.getStatus(), equalTo(originalStatus.getStatusCode()));
    }

    public static void reviewEntriesAreSame(ReviewEntry reviewEntry1, ReviewEntry reviewEntry2) {
        reviewEntriesAreSameWithoutComparingAuditAndStatus(reviewEntry1, reviewEntry2);
        //assertThat("created_on is null", reviewEntry1.getCreatedOn(), is(notNullValue()));
        assertThat("created_on dates are not same", reviewEntry1.getCreatedOn(), is(reviewEntry2.getCreatedOn()));
        if (reviewEntry1.getCreator() != null || reviewEntry2.getCreator() != null) {
            assertThat("creators are not same", reviewEntry1.getCreator(), samePropertyValuesAs(reviewEntry2.getCreator()));
        }
        assertThat("statuses are not same", reviewEntry1.getStatus(), equalTo(reviewEntry2.getStatus()));

    }
}
