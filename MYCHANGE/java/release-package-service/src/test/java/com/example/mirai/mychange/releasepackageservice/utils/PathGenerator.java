package com.example.mirai.projectname.releasepackageservice.utils;

import com.example.mirai.libraries.comment.model.CommentCaseActions;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageCaseActions;

public class PathGenerator {

    public static String getReleasePackageCreationPath() {
        return "/release-packages";
    }

    public static String getReleasePackageCaseActionPath(Long id, ReleasePackageCaseActions caseAction) {
        return "/release-packages/" + id;
    }

    public static String getMyTeamCreationPath(Long id) {
        return "/release-packages/" + id + "/my-team";
    }

    public static String getMyTeamMemberCreationPath(Long id) {
        return "/my-team/" + id + "/my-team-members";
    }

    public static String getMyTeamMemberDeletionPath(Long id) {
        return "/my-team-members/" + id;
    }

    public static String getMyTeamCasePermissionPath(Long id) {
        return "/my-team/" + id + "/case-permissions";
    }

    public static String getReleasePackageCasePermissionPath(Long id) {
        return "/release-packages/" + id + "/case-permissions";
    }

    public static String getMyTeamAggregatePath(String releasePackageId) {
        return "/release-packages/" + releasePackageId + "/my-team?view=aggregate";
    }

    public static String getLinkedEntitiesPath(Long id) {
        return "/release-packages/" + id + "?view=linked-items";
    }

    public static String getDiaBomPath(String id) {
        return "/release-packages/" + id + "/dia-bom";
    }

    public static String getProjectDetailsPath(Long id) {
        return "/release-packages/" + id + "/project";
    }

    public static String getProductDetailsPath(Long id) {
        return "/release-packages/" + id + "/product";
    }

    public static String getProjectLeadDetailsPath(Long id) {
        return "/release-packages/" + id + "/project-lead";
    }
    public static String getEntityUpdatePath(String entity, Long id) {
        return "/" + entity + "/" + id;
    }

    public static String getChildEntityUpdatePath(String entity, Long id) {
        return "/release-packages/" + entity + "/" + id;
    }
    public static String getReleasePackageCommentCaseActionPath(Long id, CommentCaseActions caseAction) {
        return "/comments/" + id + "/case-action/" + caseAction.name();
    }

    public static String getEntityCreationPath(String entity, Long id) {
        return "/" + entity + "/" + id + "/" + entity;
    }

    public static String getCommentDocumentCreationPath(String entity, Long id) {
        return "/" + entity + "/" + id + "/documents";
    }
    public static String getCommentDocumentDeletionPath(String entity, Long id) {
        return "/" + entity + "/documents" + "/" + id;
    }
    public static String getReleasePackageDocumentCreationPath(String entity, Long id) {
        return "/" + entity + "/" + id + "/documents";
    }

    public static String getAddPrerequisitesPath(String entity, Long id) {
        return "/" + entity + "/" + id + "/prerequisites";
    }

    public static String getUpdatePrerequisitesPath(String entity,Long id,String caseAction,boolean isImpactCheckRequired) {
        String queryParameters = (caseAction !=null && caseAction.length() > 0) ? "?case-action=" + caseAction : "";
         return "/" + entity + "/" + id + "/prerequisites" + queryParameters +  "&is-impact-check-required=" +isImpactCheckRequired;
    }
    public static String getUnauthorisedExceptionPath(String entity,Long id){
        return "/" + entity + "/" + id + "/prerequisites";
    }

    public static String getPrerequisitesRelasePackageNumbersPath(String entity, Long id) {
        return "/" + entity + "/" + id + "/prerequisites?view=release-package-numbers";
    }

    public static String getPathByReleasePackageNumber(String entity, String releasePackageNumber) {
        return "/" + entity + "/" + releasePackageNumber + "/prerequisites?view=release-package-numbers";
    }

    public static String getPathForPrerequisiteRelasePackageNumbersByECN(String entity, String ecn) {
        return "/" + entity + "/" + ecn + "/prerequisites?view=release-package-numbers";
    }

    public static String getPathForPrerequisitesOverviewById(String entity,Long Id) {
        return "/" + entity + "/" + Id + "/prerequisites?view=overview";
    }

    public static String getPathForPrerequisitesOverviewByNumber(String entity,String releasePackageNumber) {
        return "/" + entity + "/" + releasePackageNumber + "/prerequisites?view=overview";
    }

    public static String getPathForPrerequisitesOverviewByEcn(String entity,String ecn) {
        return "/" + entity + "/" + ecn + "/prerequisites?view=overview";
    }

    public static String getPathForPrerequisitesSearchSummary(String entity,String releasePackageNumber){
        String criteria = "status@1,2,3,4 and releasePackageNumber@"+releasePackageNumber;
        String queryParameters = (criteria != null && criteria.length() > 0) ? "&criteria=" + criteria :"";
        return "/" + entity +  "?view=search-summary" + queryParameters;
    }

    public static String getPathForReleasePackageOverview(String entity,Long releasePackageId) {
        String criteria = "status@2 and id@"+releasePackageId;
        String queryParameters = (criteria != null && criteria.length() > 0) ? "&criteria=" + criteria :"";
        return "/" + entity + "?view=overview" + queryParameters;
    }

}
