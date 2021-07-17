package com.example.mirai.projectname.reviewservice.tests.reviewtask;

import com.example.mirai.projectname.reviewservice.json.ReviewTaskJson;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTaskStatus;
import com.example.mirai.projectname.reviewservice.tests.ExceptionValidator;
import org.exparity.hamcrest.date.DateMatchers;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class Validator {

    public static void reviewTasksAreSameWithoutComparingAuditAndStatus(ReviewTask reviewTask1, ReviewTask reviewTask2) {
        assertThat("DueDates are not same", reviewTask1.getDueDate(), equalTo(reviewTask2.getDueDate()));

        assertThat("Assignees are null", reviewTask1.getAssignee(), is(notNullValue()));
        assertThat("Assignees are not same", reviewTask1.getAssignee(), samePropertyValuesAs(reviewTask2.getAssignee()));
        assertThat("Statuses are not same", reviewTask1.getStatus(), equalTo(reviewTask2.getStatus()));
    }

    public static void reviewTasksAreSameWithoutComparingStatus(ReviewTask reviewTask1, ReviewTask reviewTask2) {
        assertThat("DueDates are not same", reviewTask1.getDueDate(), equalTo(reviewTask2.getDueDate()));

        assertThat("Assignees are null", reviewTask1.getAssignee(), is(notNullValue()));
        assertThat("Assignees are not same", reviewTask1.getAssignee(), samePropertyValuesAs(reviewTask2.getAssignee()));

        assertThat("created_on is null", reviewTask1.getCreatedOn(), is(notNullValue()));
        assertThat("created_on dates are not same", reviewTask1.getCreatedOn(), is(reviewTask2.getCreatedOn()));

        assertThat("creator is null", reviewTask1.getCreator(), is(notNullValue()));
        assertThat("creators are not same", reviewTask1.getCreator(), samePropertyValuesAs(reviewTask2.getCreator()));
    }

    public static void reviewTasksAreSameWithoutComparingAssignee(ReviewTask reviewTask1, ReviewTask reviewTask2) {
        assertThat("DueDates are not same", reviewTask1.getDueDate(), equalTo(reviewTask2.getDueDate()));
        assertThat("Statuses are not same", reviewTask1.getStatus(), equalTo(reviewTask2.getStatus()));
    }

    public static void reviewTasksAreSameWithoutComparingDueDate(ReviewTask reviewTask1, ReviewTask reviewTask2) {
        assertThat("Assignees are null", reviewTask1.getAssignee(), is(notNullValue()));
        assertThat("Assignees are not same", reviewTask1.getAssignee(), samePropertyValuesAs(reviewTask2.getAssignee()));
        assertThat("Statuses are not same", reviewTask1.getStatus(), equalTo(reviewTask2.getStatus()));
    }

    public static void reviewTasksAreSame(ReviewTask reviewTask1, ReviewTask reviewTask2) {
        //reviewTasksAreSameWithoutComparingAuditAndStatus(reviewTask1, reviewTask2);
        assertThat("created_on is null", reviewTask1.getCreatedOn(), is(notNullValue()));
        assertThat("created_on dates are not same", reviewTask1.getCreatedOn(), is(reviewTask2.getCreatedOn()));

        assertThat("creator is null", reviewTask1.getCreator(), is(notNullValue()));
        assertThat("creators are not same", reviewTask1.getCreator(), samePropertyValuesAs(reviewTask2.getCreator()));

        assertThat("statuses are not same", reviewTask1.getStatus(), equalTo(reviewTask2.getStatus()));

        assertThat("Due dates are not same", reviewTask1.getDueDate(), equalTo(reviewTask2.getDueDate()));
        assertThat("Assignees are not same", reviewTask1.getAssignee(), equalTo(reviewTask2.getAssignee()));
    }

    public static void reviewTaskAreSameWithoutComparingAuditAndStatus(ReviewTask reviewTask1, ReviewTask reviewTask2) {
        assertThat("DueDates are not same", reviewTask1.getDueDate(), equalTo(reviewTask2.getDueDate()));
        assertThat("Review is not same", reviewTask1.getReview(), samePropertyValuesAs(reviewTask2.getReview()));

        assertThat("Assignees are null", reviewTask1.getAssignee(), is(notNullValue()));
        assertThat("Assignees are not same", reviewTask1.getAssignee(), samePropertyValuesAs(reviewTask2.getAssignee()));

    }

    public static void reviewTaskAreSameWithoutComparingStatus(ReviewTask reviewTask1, ReviewTask reviewTask2) {
        assertThat("created_on is null", reviewTask1.getCreatedOn(), is(notNullValue()));
        assertThat("created_on dates are not same", reviewTask1.getCreatedOn(), is(reviewTask2.getCreatedOn()));

        assertThat("creator is null", reviewTask1.getCreator(), is(notNullValue()));
        assertThat("creators are not same", reviewTask1.getCreator(), samePropertyValuesAs(reviewTask2.getCreator()));
        assertThat("DueDates are not same", reviewTask1.getDueDate(), equalTo(reviewTask2.getDueDate()));
        assertThat("Review is not same", reviewTask1.getReview(), samePropertyValuesAs(reviewTask2.getReview()));

        assertThat("Assignees are null", reviewTask1.getAssignee(), is(notNullValue()));
        assertThat("Assignees are not same", reviewTask1.getAssignee(), samePropertyValuesAs(reviewTask2.getAssignee()));

    }

    public static void unauthorizedExceptionAndReviewTaskDidNotChange(ReviewTask reviewTaskBeforeCaseAction, ReviewTask reviewTaskAfterCaseAction,

                                                                      com.example.mirai.projectname.reviewservice.json.ExceptionResponse exceptionResponse,
                                                                      String path, Integer originalStatus) {
        ExceptionValidator.exceptionResponseIsUnauthorizedException(exceptionResponse, path);
        reviewTasksAreSame(reviewTaskBeforeCaseAction, reviewTaskAfterCaseAction);
        assertThat("unauthorized case action has changed status", reviewTaskAfterCaseAction.getStatus(), equalTo(originalStatus));
    }

    public static void createReviewTaskIsSuccessful(ReviewTask requestReviewTask, ReviewTask savedReviewTask, ReviewTaskJson responseReviewTask) {
        reviewTasksAreSameWithoutComparingAuditAndStatus(requestReviewTask, savedReviewTask);

        assertThat("ids are not same in response and saved review task", responseReviewTask.getId(), equalTo(savedReviewTask.getId()));

        assertThat("titles are not same in response and saved review task", responseReviewTask.getAssignee(), equalTo(savedReviewTask.getAssignee()));
        assertThat("Due dates are not same in response and saved review", responseReviewTask.getDueDate(), equalTo(savedReviewTask.getDueDate()));

        assertThat("status is not 'OPENED' in response", responseReviewTask.getStatus(), equalTo(ReviewTaskStatus.OPENED.getStatusCode()));

        assertThat("created_on is null in response", responseReviewTask.getCreatedOn(), is(notNullValue()));
        assertThat("created_on dates are not same in response and saved review", responseReviewTask.getCreatedOn(), is(savedReviewTask.getCreatedOn()));
        assertThat("created_on is not within 10 seconds of 'now' in response", responseReviewTask.getCreatedOn(), DateMatchers.within(10, ChronoUnit.SECONDS, new Date()));

    }

}
