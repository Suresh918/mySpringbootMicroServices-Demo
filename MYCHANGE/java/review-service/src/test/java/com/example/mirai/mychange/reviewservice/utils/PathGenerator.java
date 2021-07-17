package com.example.mirai.projectname.reviewservice.utils;


import com.example.mirai.projectname.reviewservice.review.model.ReviewCaseActions;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntryCaseActions;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTaskCaseActions;

public class PathGenerator {
    public static String getReviewCreationPath() {
        return "/reviews";
    }

    public static String getReviewCaseActionPath(Long id, ReviewCaseActions caseAction) {
        return "/reviews/" + id + "/case-action/" + caseAction.name();
    }

    public static String getReviewCasePermissionsPath(Long id) {
        return "/reviews/" + id + "/case-permissions";
    }

    public static String getReviewOverviewPath(String criteria, String viewCriteria) {
        String queryParameters = (criteria != null && criteria.length() > 0) ? "?criteria=" + criteria : "";
        queryParameters = (viewCriteria != null && viewCriteria.length() > 0) ?
                ((queryParameters.contains("criteria")) ? queryParameters + "&view-criteria=" + viewCriteria : queryParameters + "?view-criteria=" + viewCriteria) :
                queryParameters;
        queryParameters = (queryParameters != null && queryParameters.length() > 0) ? queryParameters + "&view=overview" : "?view=overview";
        return "/reviews" + queryParameters;
    }

    public static String getReviewSummaryPath(String criteria, String viewCriteria) {
        String queryParameters = (criteria != null && criteria.length() > 0) ? "?criteria=" + criteria : "";
        queryParameters = (viewCriteria != null && viewCriteria.length() > 0) ?
                ((queryParameters.contains("criteria")) ? queryParameters + "&view-criteria=" + viewCriteria : queryParameters + "?view-criteria=" + viewCriteria) :
                queryParameters;
        queryParameters = (queryParameters != null && queryParameters.length() > 0) ? queryParameters + "&view=summary" : "?view=summary";
        return "/reviews" + queryParameters;
    }

    public static String getReviewTaskSummaryPath(String criteria, String viewCriteria, Long reviewId) {
        String queryParameters = (criteria != null && criteria.length() > 0) ? "?criteria=" + criteria : "";
        queryParameters = (viewCriteria != null && viewCriteria.length() > 0) ?
                ((queryParameters.contains("criteria")) ? queryParameters + "&view-criteria=" + viewCriteria : queryParameters + "?view-criteria=" + viewCriteria) :
                queryParameters;
        queryParameters = (queryParameters != null && queryParameters.length() > 0) ? queryParameters + "&view=summary" : "?view=summary";
        return "/reviews/" + reviewId + "/review-tasks" + queryParameters;
    }

    public static String getReviewEntryOverviewPath(String criteria, String viewCriteria, Long reviewId) {
        String queryParameters = (criteria != null && criteria.length() > 0) ? "?criteria=" + criteria : "";
        queryParameters = (viewCriteria != null && viewCriteria.length() > 0) ?
                ((queryParameters.contains("criteria")) ? queryParameters + "&view-criteria=" + viewCriteria : queryParameters + "?view-criteria=" + viewCriteria) :
                queryParameters;
        queryParameters = (queryParameters != null && queryParameters.length() > 0) ? queryParameters + "&view=overview" : "?view=overview";
        return "/reviews/" + reviewId + "/review-entries" + queryParameters;
    }

    public static String getReviewTaskCasePermissionsPath(Long id) {
        return "/review-tasks/" + id + "/case-permissions";
    }

    public static String getReviewTaskCreationPath(Long id) {
        return "/reviews/" + id + "/review-tasks";
    }

    public static String getReviewTaskCaseActionPath(Long id, ReviewTaskCaseActions caseAction) {
        return "/review-tasks/" + id + "/case-action/" + caseAction.name();
    }

    public static String getReviewTaskDeletePath(Long id) {
        return "/review-tasks/" + id;
    }

    public static String getReviewEntryCreationPath(Long id) {
        return "/reviews/" + id + "/review-entries";
    }

    public static String getReviewEntryCaseActionPath(Long id, ReviewEntryCaseActions caseAction) {
        return "/review-entries/" + id + "/case-action/" + caseAction.name();
    }

    public static String getReviewEntryCasePermissionsPath(Long id) {
        return "/review-entries/" + id + "/case-permissions";
    }

    public static String getEntityUpdatePath(String entity, Long id) {
        return "/" + entity + "/" + id;
    }

    public static String getReviewAggregatePath(Long id) {
        return "/reviews/" + id + "?view=aggregate";
    }

    public static String getReviewCaseActionPathWithForceComplete(Long reviewId, ReviewCaseActions caseAction) {
        return "/reviews/" + reviewId + "/case-action/" + caseAction.name() + "?force-complete=true";
    }
}
