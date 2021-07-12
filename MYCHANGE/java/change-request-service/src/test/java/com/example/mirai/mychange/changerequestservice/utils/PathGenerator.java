package com.example.mirai.projectname.changerequestservice.utils;

import com.example.mirai.libraries.comment.model.CommentCaseActions;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestCaseActions;

import java.util.Objects;

public class PathGenerator {
    public static String getChangeRequestAggregateCreationPath() {
        return "/change-requests/aggregate";
    }

    public static String getChangeRequestCaseActionPath(Long id, ChangeRequestCaseActions changeRequestCaseAction) {
        return "/change-requests/" + id + "?case-action=" + changeRequestCaseAction.name();
    }
    public static String getChangeRequestUnLinkCaseActionPath(Long id, String unLinkCrCaseAction) {
        return  "/change-requests/"+id+"?case-action="+ unLinkCrCaseAction;
    }
    public static String getChangeRequestLinkPbsCaseActionPath(Long id) {
        return  "/change-requests/"+id+"?case-action=link-pbs";
    }
    public static String getChangeRequestUnLinkPbsCaseActionPath(Long id) {
        return  "/change-requests/"+id+"?case-action=unlink-pbs";
    }
    public static String getChangeRequestLinkAirCaseActionPath(Long id) {
        return  "/change-requests/"+id+"?case-action=link-air";
    }
    public static String getChangeRequestUnLinkAirCaseActionPath(Long id) {
        return  "/change-requests/"+id+"?case-action=unlink-air";
    }
    public static String getCreateSciaPath(Long id) {
        return  "/change-requests/"+id+"/scias";
    }
    public static String getSciaByChangeRequestIdPath(Long id) {
        return  "/change-requests/"+id+"/scias";
    }
    public static String getCopySciaPath(Long id,String sciaId) {
        return  "/change-requests/"+id+"/scias?scia-id="+sciaId;
    }
    public static String getEntityUpdatePath(String entity, Long id) {
        if(Objects.isNull(entity)) {
            return "/change-requests/" + id;
        }else{
            return "/change-requests/" + entity + "/" + id;
        }
    }
    public static String getCaseActionsPath(Long id) {
        return "/change-requests/" + id + "/case-actions";
    }

    public static String getChangeRequestCommentCaseActionPath(Long id, CommentCaseActions caseAction) {
        return "/comments/" + id + "/case-action/" + caseAction.name();
    }
    public static String getChildEntityUpdatePath(String entity, Long id) {
        return "/change-requests/" + entity + "/" + id;
    }
    public static String getChildEntityPathByParentEntity(String parentEntity, Long id, String childEntity) {
        return "/" + parentEntity + "/" + id + "/" + childEntity;
    }
    public static String getCommentDocumentDeletionPath(String entity, Long id) {
        return "/" + entity + "/documents" + "/" + id;
    }
    public static String getProjectPathById(Long id) {
        return "/change-requests/" + id + "/project";
    }

    public static String getProductPathById(Long id) {
        return "/change-requests/" + id + "/product";
    }
    public static String getProblemsByChangeRequestIdPathById(Long id) {
        return "/change-requests/" + id + "/problems";
    }
    public static String getFunctionalClusterDetailsPathById(Long id) {
        return "/change-requests/" + id + "/functional-cluster";
    }

    public static String searchFunctionalClusterPathById(String fcId) {
        return "/change-requests/functional-clusters?search="+fcId;
    }

    public static String getProjectLeadPathById(Long id) {
        return "/change-requests/" + id + "/project-lead";
    }

    public static String getCollaborationObjectsCountPathById(Long id) {
        return "/change-requests/" + id + "?view=collaboration-objects";
    }
    public static String getPathForChangeRequestList(Long id) {
        String viewCriteria = "id:" + id;
        String queryParameters = (viewCriteria != null && viewCriteria.length() > 0) ? "&view-criteria=" + viewCriteria : "";
        return "/change-requests?view=change-request-list" + queryParameters;
    }
    public static String getPathForChangeRequestAsLinkedObject(Long id) {
        String viewCriteria = "id:" + id;
        String queryParameters = (viewCriteria != null && viewCriteria.length() > 0) ? "&view-criteria=" + viewCriteria : "";
        return "/change-requests?view=linked-object" + queryParameters;
    }
    public static String getPathForChangeRequestOverview(Long id) {
        String viewCriteria = "id:" + id;
        String queryParameters = (viewCriteria != null && viewCriteria.length() > 0) ? "&view-criteria=" + viewCriteria : "";
        return "/change-requests?view=overview" + queryParameters;
    }
    public static String getPathForChangeRequestSearchSummary(Long id) {
        String viewCriteria = "id:" + id;
        String queryParameters = (viewCriteria != null && viewCriteria.length() > 0) ? "&view-criteria=" + viewCriteria : "";
        return "/change-requests?view=search-summary" + queryParameters;
    }
    public static String getPathForInsecureFetchChangeRequestsSummary(Long id) {
        String viewCriteria = "id:" + id;
        String queryParameters = (viewCriteria != null && viewCriteria.length() > 0) ? "&view-criteria=" + viewCriteria : "";
        return "/change-requests?view=summary" + queryParameters;
    }
    public static String getPathChangeRequestStatusCount(Long id) {
        String viewCriteria = "id:" + id;
        String queryParameters = (viewCriteria != null && viewCriteria.length() > 0) ? "&view-criteria=" + viewCriteria : "";
        return "/change-requests?view=status-count" + queryParameters+"&status=1";
    }
    public static String getPathForChangeRequestsSummary(Long id) {
        String viewCriteria = "id:" + id;
        String queryParameters = (viewCriteria != null && viewCriteria.length() > 0) ? "&view-criteria=" + viewCriteria : "";
        return "/change-requests?view=summary" + queryParameters;
    }
    public static String getStatusCountByPriorityPath(Long id) {
        String viewCriteria = "id:" + id;
        String queryParameters = (viewCriteria != null && viewCriteria.length() > 0) ? "&view-criteria=" + viewCriteria : "";
        return "/change-requests?view=state-overview-by-priority" + queryParameters+"&status=1";
    }
    public static String getPathForChangeRequestsForGlobalSearch(Long id) {
        String viewCriteria = "id:" + id;
        String queryParameters = (viewCriteria != null && viewCriteria.length() > 0) ? "&view-criteria=" + viewCriteria : "";
        return "/change-requests?view=global-search" + queryParameters;
    }

    public static String getPathForChangeRequestTrackerboardSummary(Long id) {
        String viewCriteria = "id:" + id;
        String queryParameters = (viewCriteria != null && viewCriteria.length() > 0) ? "&view-criteria=" + viewCriteria : "";
        return "/change-requests?view=trackerboard-summary" + queryParameters;
    }

    public static String getStateOverviewPath(Long id) {
        String viewCriteria = "id:" + id;
        String queryParameters = (viewCriteria != null && viewCriteria.length() > 0) ? "&view-criteria=" + viewCriteria : "";
        return "/change-requests?view=state-overview" + queryParameters+"&status=1";
    }

    public static String getDiaBomPathById(String entity, Long id) {
        return "/" + entity + "/" + id + "/dia-bom";
    }
    public static String getPathForChangeRequestSummaryForScm(Long id) {
        return "/change-requests/" + id + "?view=scm";
    }
    public static String getPathForChangeRequestDetailsByAgendaItemId(String agendaItemId) {
        return "/change-requests?agenda-item-id=" +agendaItemId;
    }
    public static String getPathForProjectByAgendaItemId(String agendaItemId) {
        return "/change-requests/project?agenda-item-id=" +agendaItemId;
    }
    public static String getPathForProductByAgendaItemId(String agendaItemId) {
        return "/change-requests/product?agenda-item-id=" +agendaItemId;
    }
    public static String getPathForProblemsByAgendaItemId(String agendaItemId) {
        return "/change-requests/problems?agenda-item-id=" +agendaItemId;
    }
    public static String getPathForPmoDetailsByAgendaItemId(String agendaItemId) {
        return "/change-requests/pmo?agenda-item-id=" +agendaItemId;
    }
    public static String getPathForFindAirProblemsByPartialId(String airProblemId) {
        return "/change-requests/problems?search=" +airProblemId;
    }
    public static String getPathForgetProductBreakdownStructures(Long changeRequestId) {
        return "/change-requests/"+changeRequestId+"/product-breakdown-structures";
    }
    public static String getPathForfetchMultipleProductBreakDownStructures(String productBreakdownStructureId) {
        return "/change-requests/product-breakdown-structures?search=" +productBreakdownStructureId;
    }
    public static String getPathForProductBreakdownStructuresByAgendaItemId(String agendaItemId) {
        return "/change-requests/product-breakdown-structures?agenda-item-id=" +agendaItemId;
    }
    public static String getPathForgetScopeFieldEnablement(Long changeRequestId) {
        return "/change-requests/"+changeRequestId+"/scope-field-enablement";
    }
    public static String getSubjectsPathById(String entity, Long id) {
        return "/" + entity + "/" + id + "/subjects";
    }
    public static String getCIADetailsPathById(Long id) {
        return "/change-requests/" + id + "/customer-impact?view=details";
    }
    public static String getCaseActionsPathById(Long id) {
        return "/change-requests/" + id + "/case-actions";
    }

    public static String getAllCaseActionsPath() {
        return "/change-requests/case-actions";
    }

    public static String getCasePropertiesPathById(Long id) {
        return "/change-requests/" + id + "/case-properties";
    }

    public static String getChangeRequestCasePermissionsPath(Long id) {
        return "/change-requests/" + id + "/case-permissions";
    }
    public static String getSubjectsPathByMyTeamId(Long id) {
        return "/change-requests/my-team/" + id + "/subjects";
    }

    public static String getCaseStatusAggregatePathById(Long id) {
        return "/change-requests/" + id + "/case-status?view=aggregate";
    }
    public static String getCaseActionsByContextPath(String congtextId) {
        return "/change-requests/case-actions?context-type=TEAMCENTER&context-id="+congtextId;
    }
    public static String getCasePermissionsByContextPath(String congtextId) {
        return "/change-requests/case-permissions?context-type=TEAMCENTER&context-id="+congtextId;
    }
    public static String getCasePermissionsByChangeRequestPath(Long id) {
        return "/change-requests/" + id + "/case-permissions";
    }
    public static String getMyTeamMemberCreationPath(Long id) {
        return "/change-requests/my-team/" + id + "/my-team-members";
    }
    public static String getMyTeamAggregatePath(String id) {
        return "/change-requests/my-team/" + id + "?view=aggregate";
    }
    public static String getMyTeamMemberDeletionPath(Long id) {
        return "/change-requests/my-team/my-team-members/" + id;
    }
    public static String getMyTeamMemberUpdatePath(Long id) {
        return "/change-requests/my-team/my-team-members/" + id;
    }

    public static String getPathForMyTeamDetails(Long id) {
        return "/change-requests/" + id + "/my-team?view=detail";
    }
    public static String getPathForAddImpactedItemMyTeamMember(Long id) {
        return "/change-requests/my-team/my-team-members?change-request-id=" +id;
    }
    public static String getMyTeamCasePermissionPath(Long id) {
        return "/change-requests/my-team/" + id + "/case-permissions";
    }
    public static String getPathForBulkUpdateMyTeamMembersIsAllSelectedTrue(String caseaction) {
        return "/change-requests/my-team/my-team-members?is-all-selected=true&case-action="+caseaction;
    }
    public static String getPathForBulkUpdateMyTeamMembersIsAllSelectedFalse(String caseaction) {
        return "/change-requests/my-team/my-team-members?is-all-selected=false&case-action="+caseaction;
    }
    public static String getBackgroundJobsPath() {
        return "/change-requests/jobs?view=categorized";
    }
    public static String getUpdateScopePath(Long scopeId) {
        return "/change-requests/scope/" + scopeId + "?view=change-request-detail";
    }
    public static String getChangeRequestIsFirstDraft(Long id) {
        return "/change-requests/" + id + "?view=is-first-draft";
    }
    public static String getUpdatePreInstallImpactPath(Long preInstallImpactId) {
        return "/change-requests/preinstall-impact/" + preInstallImpactId + "?view=change-request-detail";
    }
}
