package com.example.mirai.projectname.reviewservice.tests.review;


import com.example.mirai.projectname.reviewservice.json.ReviewJson;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.review.model.ReviewContext;
import com.example.mirai.projectname.reviewservice.review.model.ReviewStatus;
import com.example.mirai.projectname.reviewservice.tests.ExceptionValidator;
import org.exparity.hamcrest.date.DateMatchers;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class Validator {

    public static void reviewsAreSameWithoutComparingTitle(Review review1, Review review2) {
        assertThat("completion dates are not same", review1.getCompletionDate(), equalTo(review2.getCompletionDate()));

        assertThat("executors is null", review1.getExecutor(), is(notNullValue()));
        assertThat("executors are not same", review1.getExecutor(), samePropertyValuesAs(review2.getExecutor()));

        long releasePackageContextCount = review1.getContexts().stream().filter(context -> context.getType().equals("RELEASEPACKAGE")).count();
        assertThat("number of releasepackage contexts is not equal to 1", releasePackageContextCount, is(equalTo(1L)));
        ReviewContext releasePackageReviewContextFromReview1 = review1.getContexts().stream().filter(context -> context.getType().equals("RELEASEPACKAGE")).findFirst().get();
        ReviewContext releasePackageReviewContextFromReview2 = review2.getContexts().stream().filter(context -> context.getType().equals("RELEASEPACKAGE")).findFirst().get();
        assertThat("releasepackage contexts are not same", releasePackageReviewContextFromReview1, samePropertyValuesAs(releasePackageReviewContextFromReview2));

        long ecnContextCount = review1.getContexts().stream().filter(context -> context.getType().equals("ECN")).count();
        assertThat("number of ecn contexts is not equal to 1", ecnContextCount, is(equalTo(1L)));
        ReviewContext ecnReviewContextFromReview1 = review1.getContexts().stream().filter(context -> context.getType().equals("ECN")).findFirst().get();
        ReviewContext ecnReviewContextFromReview2 = review2.getContexts().stream().filter(context -> context.getType().equals("ECN")).findFirst().get();
        assertThat("ecn contexts are not same", ecnReviewContextFromReview1, samePropertyValuesAs(ecnReviewContextFromReview2));

        long teamcenterContextCount = review1.getContexts().stream().filter(context -> context.getType().equals("TEAMCENTER")).count();
        assertThat("number of teamcenter contexts is not equal to 1", teamcenterContextCount, is(equalTo(1L)));
        ReviewContext teamcenterReviewContextFromReview1 = review1.getContexts().stream().filter(context -> context.getType().equals("TEAMCENTER")).findFirst().get();
        ReviewContext teamcenterReviewContextFromReview2 = review2.getContexts().stream().filter(context -> context.getType().equals("TEAMCENTER")).findFirst().get();
        assertThat("teamcenter contexts are not same", teamcenterReviewContextFromReview1, samePropertyValuesAs(teamcenterReviewContextFromReview2));
    }

    public static void reviewsAreSameWithoutComparingExecutor(Review review1, Review review2) {
        assertThat("titles are not same", review1.getTitle(), equalTo(review2.getTitle()));
        assertThat("completion dates are not same", review1.getCompletionDate(), equalTo(review2.getCompletionDate()));

        long releasePackageContextCount = review1.getContexts().stream().filter(context -> context.getType().equals("RELEASEPACKAGE")).count();
        assertThat("number of releasepackage contexts is not equal to 1", releasePackageContextCount, is(equalTo(1L)));
        ReviewContext releasePackageReviewContextFromReview1 = review1.getContexts().stream().filter(context -> context.getType().equals("RELEASEPACKAGE")).findFirst().get();
        ReviewContext releasePackageReviewContextFromReview2 = review2.getContexts().stream().filter(context -> context.getType().equals("RELEASEPACKAGE")).findFirst().get();
        assertThat("releasepackage contexts are not same", releasePackageReviewContextFromReview1, samePropertyValuesAs(releasePackageReviewContextFromReview2));

        long ecnContextCount = review1.getContexts().stream().filter(context -> context.getType().equals("ECN")).count();
        assertThat("number of ecn contexts is not equal to 1", ecnContextCount, is(equalTo(1L)));
        ReviewContext ecnReviewContextFromReview1 = review1.getContexts().stream().filter(context -> context.getType().equals("ECN")).findFirst().get();
        ReviewContext ecnReviewContextFromReview2 = review2.getContexts().stream().filter(context -> context.getType().equals("ECN")).findFirst().get();
        assertThat("ecn contexts are not same", ecnReviewContextFromReview1, samePropertyValuesAs(ecnReviewContextFromReview2));

        long teamcenterContextCount = review1.getContexts().stream().filter(context -> context.getType().equals("TEAMCENTER")).count();
        assertThat("number of teamcenter contexts is not equal to 1", teamcenterContextCount, is(equalTo(1L)));
        ReviewContext teamcenterReviewContextFromReview1 = review1.getContexts().stream().filter(context -> context.getType().equals("TEAMCENTER")).findFirst().get();
        ReviewContext teamcenterReviewContextFromReview2 = review2.getContexts().stream().filter(context -> context.getType().equals("TEAMCENTER")).findFirst().get();
        assertThat("teamcenter contexts are not same", teamcenterReviewContextFromReview1, samePropertyValuesAs(teamcenterReviewContextFromReview2));
    }

    public static void reviewsAreSameWithoutComparingCompletionDate(Review review1, Review review2) {
        assertThat("titles are not same", review1.getTitle(), equalTo(review2.getTitle()));

        assertThat("executors is null", review1.getExecutor(), is(notNullValue()));
        assertThat("executors are not same", review1.getExecutor(), samePropertyValuesAs(review2.getExecutor()));

        long releasePackageContextCount = review1.getContexts().stream().filter(context -> context.getType().equals("RELEASEPACKAGE")).count();
        assertThat("number of releasepackage contexts is not equal to 1", releasePackageContextCount, is(equalTo(1L)));
        ReviewContext releasePackageReviewContextFromReview1 = review1.getContexts().stream().filter(context -> context.getType().equals("RELEASEPACKAGE")).findFirst().get();
        ReviewContext releasePackageReviewContextFromReview2 = review2.getContexts().stream().filter(context -> context.getType().equals("RELEASEPACKAGE")).findFirst().get();
        assertThat("releasepackage contexts are not same", releasePackageReviewContextFromReview1, samePropertyValuesAs(releasePackageReviewContextFromReview2));

        long ecnContextCount = review1.getContexts().stream().filter(context -> context.getType().equals("ECN")).count();
        assertThat("number of ecn contexts is not equal to 1", ecnContextCount, is(equalTo(1L)));
        ReviewContext ecnReviewContextFromReview1 = review1.getContexts().stream().filter(context -> context.getType().equals("ECN")).findFirst().get();
        ReviewContext ecnReviewContextFromReview2 = review2.getContexts().stream().filter(context -> context.getType().equals("ECN")).findFirst().get();
        assertThat("ecn contexts are not same", ecnReviewContextFromReview1, samePropertyValuesAs(ecnReviewContextFromReview2));

        long teamcenterContextCount = review1.getContexts().stream().filter(context -> context.getType().equals("TEAMCENTER")).count();
        assertThat("number of teamcenter contexts is not equal to 1", teamcenterContextCount, is(equalTo(1L)));
        ReviewContext teamcenterReviewContextFromReview1 = review1.getContexts().stream().filter(context -> context.getType().equals("TEAMCENTER")).findFirst().get();
        ReviewContext teamcenterReviewContextFromReview2 = review2.getContexts().stream().filter(context -> context.getType().equals("TEAMCENTER")).findFirst().get();
        assertThat("teamcenter contexts are not same", teamcenterReviewContextFromReview1, samePropertyValuesAs(teamcenterReviewContextFromReview2));
    }

    public static void reviewsAreSameWithoutComparingAuditAndStatus(Review review1, Review review2) {
        assertThat("titles are not same", review1.getTitle(), equalTo(review2.getTitle()));
        assertThat("completion dates are not same", review1.getCompletionDate(), equalTo(review2.getCompletionDate()));

        assertThat("executors is null", review1.getExecutor(), is(notNullValue()));
        assertThat("executors are not same", review1.getExecutor(), samePropertyValuesAs(review2.getExecutor()));

        long releasePackageContextCount = review1.getContexts().stream().filter(context -> context.getType().equals("RELEASEPACKAGE")).count();
        assertThat("number of releasepackage contexts is not equal to 1", releasePackageContextCount, is(equalTo(1L)));
        ReviewContext releasePackageReviewContextFromReview1 = review1.getContexts().stream().filter(context -> context.getType().equals("RELEASEPACKAGE")).findFirst().get();
        ReviewContext releasePackageReviewContextFromReview2 = review2.getContexts().stream().filter(context -> context.getType().equals("RELEASEPACKAGE")).findFirst().get();
        assertThat("releasepackage contexts are not same", releasePackageReviewContextFromReview1, samePropertyValuesAs(releasePackageReviewContextFromReview2));

        long ecnContextCount = review1.getContexts().stream().filter(context -> context.getType().equals("ECN")).count();
        assertThat("number of ecn contexts is not equal to 1", ecnContextCount, is(equalTo(1L)));
        ReviewContext ecnReviewContextFromReview1 = review1.getContexts().stream().filter(context -> context.getType().equals("ECN")).findFirst().get();
        ReviewContext ecnReviewContextFromReview2 = review2.getContexts().stream().filter(context -> context.getType().equals("ECN")).findFirst().get();
        assertThat("ecn contexts are not same", ecnReviewContextFromReview1, samePropertyValuesAs(ecnReviewContextFromReview2));

        long teamcenterContextCount = review1.getContexts().stream().filter(context -> context.getType().equals("TEAMCENTER")).count();
        assertThat("number of teamcenter contexts is not equal to 1", teamcenterContextCount, is(equalTo(1L)));
        ReviewContext teamcenterReviewContextFromReview1 = review1.getContexts().stream().filter(context -> context.getType().equals("TEAMCENTER")).findFirst().get();
        ReviewContext teamcenterReviewContextFromReview2 = review2.getContexts().stream().filter(context -> context.getType().equals("TEAMCENTER")).findFirst().get();
        assertThat("teamcenter contexts are not same", teamcenterReviewContextFromReview1, samePropertyValuesAs(teamcenterReviewContextFromReview2));
    }

    public static void reviewsAreSame(Review review1, Review review2) {
        reviewsAreSameWithoutComparingAuditAndStatus(review1, review2);

        assertThat("created_on is null", review1.getCreatedOn(), is(notNullValue()));
        assertThat("created_on dates are not same", review1.getCreatedOn(), is(review2.getCreatedOn()));

        assertThat("creator is null", review1.getCreator(), is(notNullValue()));
        assertThat("creators are not same", review1.getCreator(), samePropertyValuesAs(review2.getCreator()));

        assertThat("statuses are not same", review1.getStatus(), equalTo(review2.getStatus()));
        //MYC 13097 start
        assertThat("titles are not same", review1.getTitle(), equalTo(review2.getTitle()));
        assertThat("executor is null", review1.getExecutor(), is(notNullValue()));
        assertThat("executors are not same", review1.getExecutor(), samePropertyValuesAs(review2.getExecutor()));
        assertThat(" Completion dates are not same", review1.getCompletionDate(), equalTo(review2.getCompletionDate()));
        //MYC 13097 end
    }

    public static void unauthorizedExceptionAndReviewDidNotChange(Review reviewBeforeCaseAction, Review reviewAfterCaseAction,
                                                                  com.example.mirai.projectname.reviewservice.json.ExceptionResponse exceptionResponse,
                                                                  String path, Integer originalStatus) {
        ExceptionValidator.exceptionResponseIsUnauthorizedException(exceptionResponse, path);
        reviewsAreSame(reviewBeforeCaseAction, reviewAfterCaseAction);
        assertThat("unauthorized case action has changed status", reviewAfterCaseAction.getStatus(), equalTo(originalStatus));
    }

    public static void createReviewIsSuccessful(Review requestReview, Review savedReview, ReviewJson responseReview) {
        reviewsAreSameWithoutComparingAuditAndStatus(requestReview, savedReview);

        assertThat("ids are not same in response and saved review", responseReview.getId(), equalTo(savedReview.getId()));

        assertThat("titles are not same in response and saved review", responseReview.getTitle(), equalTo(savedReview.getTitle()));

        assertThat("statuses are not same in response and saved review", responseReview.getStatus(), equalTo(savedReview.getStatus()));
        assertThat("status is not 'OPENED' in response", responseReview.getStatus(), equalTo(ReviewStatus.valueOf("OPENED").getStatusCode()));

        assertThat("completion dates are not same in response and saved review", responseReview.getCompletionDate(), equalTo(savedReview.getCompletionDate()));

        assertThat("created_on is null in response", responseReview.getCreatedOn(), is(notNullValue()));
        assertThat("created_on dates are not same in response and saved review", responseReview.getCreatedOn(), is(savedReview.getCreatedOn()));
        assertThat("created_on is not within 10 seconds of 'now' in response", responseReview.getCreatedOn(), DateMatchers.within(10, ChronoUnit.SECONDS, new Date()));

/*
        assertThat("creator is null in response", responseReview.getCreator(), is(notNullValue()));
        assertThat("creator is not same in response and saved review", responseReview.getCreator(), samePropertyValuesAs(savedReview.getCreator()));
*/

        assertThat("executor is not same in response and saved review", responseReview.getExecutor(), samePropertyValuesAs(savedReview.getExecutor()));

        ReviewContext releasePackageReviewContextFromSaved = savedReview.getContexts().stream().filter(context -> context.getType().equals("RELEASEPACKAGE")).findFirst().get();
        assertThat("releasepackage context is not same in response and saved review", responseReview.getReleasePackageReviewContext(), samePropertyValuesAs(releasePackageReviewContextFromSaved));


        ReviewContext ecnReviewContextFromSaved = savedReview.getContexts().stream().filter(context -> context.getType().equals("ECN")).findFirst().get();
        assertThat("ecn context is not same in response and saved review", responseReview.getECNReviewContext(), samePropertyValuesAs(ecnReviewContextFromSaved));


        ReviewContext teamcenterReviewContextFromSaved = savedReview.getContexts().stream().filter(context -> context.getType().equals("TEAMCENTER")).findFirst().get();
        assertThat("teamcenter context is not same in response and saved review", responseReview.getTeamcenterReviewContext(), samePropertyValuesAs(teamcenterReviewContextFromSaved));
    }

}
