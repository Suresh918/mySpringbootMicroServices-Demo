package com.example.mirai.projectname.releasepackageservice.fixtures;


import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import com.example.mirai.projectname.releasepackageservice.comment.model.ReleasePackageComment;
import com.example.mirai.projectname.releasepackageservice.document.model.ReleasePackageCommentDocument;
import com.example.mirai.projectname.releasepackageservice.document.model.ReleasePackageDocument;
import com.example.mirai.projectname.releasepackageservice.myteam.model.ReleasePackageMyTeam;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageCaseActions;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class EntityPojoFactory {

    public static ReleasePackage createReleasePackage(String dataIdentifier, String properties) {

        ReleasePackage releasePackage = new ReleasePackage();

        switch (properties) {
            case "ALL_PROPERTIES":
                addReleasePackageContext(releasePackage, dataIdentifier);
                addReleasePackageTitle(releasePackage, dataIdentifier);
                addReleasePackageId(releasePackage, dataIdentifier);
                addPlmCoordinator(releasePackage, dataIdentifier);
                addReleasePackageSapChangeControl(releasePackage, false);
                addReleasePackageType(releasePackage, "CHANGENOTICE");
                addReleasePackagePlannedEffectiveDate(releasePackage, dataIdentifier);
                addReleasePackagePlannedReleasedDate(releasePackage, dataIdentifier);
                addReleasePackageExecutor(releasePackage, dataIdentifier);
                addReleasePackageChangeSpecialist3(releasePackage, dataIdentifier);
                addReleasePackageCreator(releasePackage, dataIdentifier);
                addReleasePackageCreatedOn(releasePackage, dataIdentifier);
                addReleasePackagePlmCoordinator(releasePackage,dataIdentifier);
                return releasePackage;
            case "ALL_PROPERTIES_EXCEPT_TITLE":
                addReleasePackageContext(releasePackage, dataIdentifier);
                addReleasePackageId(releasePackage, dataIdentifier);
                addReleasePackageSapChangeControl(releasePackage, false);
                addReleasePackageType(releasePackage, "CHANGENOTICE");
                addReleasePackagePlannedEffectiveDate(releasePackage, dataIdentifier);
                addReleasePackagePlannedReleasedDate(releasePackage, dataIdentifier);
                addReleasePackageExecutor(releasePackage, dataIdentifier);
                addReleasePackageChangeSpecialist3(releasePackage, dataIdentifier);
                addReleasePackageCreator(releasePackage, dataIdentifier);
                addReleasePackageCreatedOn(releasePackage, dataIdentifier);
                addReleasePackagePlmCoordinator(releasePackage,dataIdentifier);
                return releasePackage;
            case "ALL_PROPERTIES_EXCEPT_PLANNED_RELEASE_DATE":
                addReleasePackageContext(releasePackage, dataIdentifier);
                addReleasePackageTitle(releasePackage, dataIdentifier);
                addReleasePackageId(releasePackage, dataIdentifier);
                addReleasePackageSapChangeControl(releasePackage, false);
                addReleasePackageType(releasePackage, "CHANGENOTICE");
                addReleasePackagePlannedEffectiveDate(releasePackage, dataIdentifier);
                addReleasePackageExecutor(releasePackage, dataIdentifier);
                addReleasePackageChangeSpecialist3(releasePackage, dataIdentifier);
                addReleasePackageCreator(releasePackage, dataIdentifier);
                addReleasePackageCreatedOn(releasePackage, dataIdentifier);
                addReleasePackagePlmCoordinator(releasePackage,dataIdentifier);
                return releasePackage;
            case "ALL_PROPERTIES_EXCEPT_PLANNED_EFFECTIVE_DATE":
                addReleasePackageContext(releasePackage, dataIdentifier);
                addReleasePackageTitle(releasePackage, dataIdentifier);
                addReleasePackageId(releasePackage, dataIdentifier);
                addReleasePackageSapChangeControl(releasePackage, false);
                addReleasePackageType(releasePackage, "CHANGENOTICE");
                addReleasePackagePlannedReleasedDate(releasePackage, dataIdentifier);
                addReleasePackageExecutor(releasePackage, dataIdentifier);
                addReleasePackageChangeSpecialist3(releasePackage, dataIdentifier);
                addReleasePackageCreator(releasePackage, dataIdentifier);
                addReleasePackageCreatedOn(releasePackage, dataIdentifier);
                addReleasePackagePlmCoordinator(releasePackage,dataIdentifier);
                return releasePackage;
            case "ALL_PROPERTIES_EXCEPT_SAP_CHANGE_CONTROL":
                addReleasePackageContext(releasePackage, dataIdentifier);
                addReleasePackageTitle(releasePackage, dataIdentifier);
                addReleasePackageId(releasePackage, dataIdentifier);
                addReleasePackageType(releasePackage, "CHANGENOTICE");
                addReleasePackagePlannedEffectiveDate(releasePackage, dataIdentifier);
                addReleasePackagePlannedReleasedDate(releasePackage, dataIdentifier);
                addReleasePackageExecutor(releasePackage, dataIdentifier);
                addReleasePackageChangeSpecialist3(releasePackage, dataIdentifier);
                addReleasePackageCreator(releasePackage, dataIdentifier);
                addReleasePackageCreatedOn(releasePackage, dataIdentifier);
                addReleasePackagePlmCoordinator(releasePackage,dataIdentifier);
                return releasePackage;
            case "ALL_PROPERTIES_LINKED_ENTITIES":
                addReleasePackageContextForLinkedEntities(releasePackage, dataIdentifier);
                addReleasePackageTitle(releasePackage, dataIdentifier);
                addReleasePackageId(releasePackage, dataIdentifier);
                addReleasePackageType(releasePackage, "CHANGENOTICE");
                addReleasePackagePlannedEffectiveDate(releasePackage, dataIdentifier);
                addReleasePackagePlannedReleasedDate(releasePackage, dataIdentifier);
                addReleasePackageExecutor(releasePackage, dataIdentifier);
                addReleasePackageChangeSpecialist3(releasePackage, dataIdentifier);
                addReleasePackageCreator(releasePackage, dataIdentifier);
                addReleasePackageCreatedOn(releasePackage, dataIdentifier);
                addReleasePackagePlmCoordinator(releasePackage,dataIdentifier);
                return releasePackage;
            case "ALL_PROPERTIES_WITHOUT_REVIEW_CONTEXT":
                addReleasePackageWithoutReviewContext(releasePackage, dataIdentifier);
                addReleasePackageSapChangeControl(releasePackage, false);
                addReleasePackageTitle(releasePackage, dataIdentifier);
                addReleasePackageId(releasePackage, dataIdentifier);
                addReleasePackageType(releasePackage, "CHANGENOTICE");
                addReleasePackagePlannedEffectiveDate(releasePackage, dataIdentifier);
                addReleasePackagePlannedReleasedDate(releasePackage, dataIdentifier);
                addReleasePackageExecutor(releasePackage, dataIdentifier);
                addReleasePackageChangeSpecialist3(releasePackage, dataIdentifier);
                addReleasePackageCreator(releasePackage, dataIdentifier);
                addReleasePackageCreatedOn(releasePackage, dataIdentifier);
                addReleasePackagePlmCoordinator(releasePackage,dataIdentifier);
                return releasePackage;
            case "ALL_PROPERTIES_EXCEPT_TYPES":
                addReleasePackageContext(releasePackage, dataIdentifier);
                addReleasePackageTitle(releasePackage, dataIdentifier);
                addReleasePackageId(releasePackage, dataIdentifier);
                addReleasePackageSapChangeControl(releasePackage, false);
                addReleasePackagePlmCoordinator(releasePackage,dataIdentifier);
                addReleasePackagePlannedEffectiveDate(releasePackage, dataIdentifier);
                addReleasePackagePlannedReleasedDate(releasePackage, dataIdentifier);
                addReleasePackageExecutor(releasePackage, dataIdentifier);
                addReleasePackageChangeSpecialist3(releasePackage, dataIdentifier);
                addReleasePackageCreator(releasePackage, dataIdentifier);
                addReleasePackageCreatedOn(releasePackage, dataIdentifier);
                return releasePackage;
            case "ALL_PROPERTIES_EXCEPT_PLMCOORDINATOR":
                addReleasePackageContext(releasePackage, dataIdentifier);
                addReleasePackageTitle(releasePackage, dataIdentifier);
                addReleasePackageId(releasePackage, dataIdentifier);
                addReleasePackageSapChangeControl(releasePackage, false);
                addReleasePackagePlannedEffectiveDate(releasePackage, dataIdentifier);
                addReleasePackagePlannedReleasedDate(releasePackage, dataIdentifier);
                addReleasePackageExecutor(releasePackage, dataIdentifier);
                addReleasePackageChangeSpecialist3(releasePackage, dataIdentifier);
                addReleasePackageCreator(releasePackage, dataIdentifier);
                addReleasePackageCreatedOn(releasePackage, dataIdentifier);
                addReleasePackageType(releasePackage, "CHANGENOTICE");
                return releasePackage;
            default:
                return releasePackage;
        }


    }

    private static ReleasePackage addReleasePackageCreatedOn(ReleasePackage releasePackage, String dataIdentifier) {
        releasePackage.setCreatedOn(getDate(dataIdentifier));
        return releasePackage;
    }

    private static Date getDate(String dataIdentifier) {
        try {
            Long timestamp = Long.parseLong(dataIdentifier);
            return new Date(timestamp);
        } catch (NumberFormatException nfe) {
            return new Date();
        }
    }

    private static ReleasePackage addReleasePackageCreator(ReleasePackage releasePackage, String dataIdentifier) {
        User creator = new User();
        creator.setAbbreviation(dataIdentifier + "_release_package-creator_abbreviation");
        creator.setDepartmentName(dataIdentifier + "_release_package-creator_department_name");
        creator.setEmail(dataIdentifier + "_release_package-creator_email");
        creator.setFullName(dataIdentifier + "_release_package-creator_full_name");
        creator.setUserId(dataIdentifier + "_release_package-creator_user_id");
        releasePackage.setCreator(creator);

        return releasePackage;
    }

    private static ReleasePackage addReleasePackageChangeSpecialist3(ReleasePackage releasePackage, String dataIdentifier) {
        User changeSpecialist3 = new User();
        changeSpecialist3.setAbbreviation(dataIdentifier + "_change_specialist_3_abbreviation");
        changeSpecialist3.setDepartmentName(dataIdentifier + "_change_specialist_3_department_name");
        changeSpecialist3.setEmail(dataIdentifier + "_change_specialist_3_email");
        changeSpecialist3.setFullName(dataIdentifier + "_change_specialist_3_full_name");
        changeSpecialist3.setUserId(dataIdentifier + "_change_specialist_3_user_id");
        releasePackage.setChangeSpecialist3(changeSpecialist3);

        return releasePackage;
    }

    private static ReleasePackage addReleasePackageExecutor(ReleasePackage releasePackage, String dataIdentifier) {
        User executor = new User();
        executor.setAbbreviation(dataIdentifier + "_executor_abbreviation");
        executor.setDepartmentName(dataIdentifier + "_executor_department_name");
        executor.setEmail(dataIdentifier + "_executor_email");
        executor.setFullName(dataIdentifier + "_executor_full_name");
        executor.setUserId(dataIdentifier + "_executor_user_id");
        releasePackage.setExecutor(executor);

        return releasePackage;

    }

    private static ReleasePackage addReleasePackageTitle(ReleasePackage releasePackage, String dataIdentifier) {
        releasePackage.setTitle(dataIdentifier + "_title");
        return releasePackage;
    }

    private static ReleasePackage addReleasePackageId(ReleasePackage releasePackage, String dataIdentifier) {
        releasePackage.setReleasePackageNumber(dataIdentifier + "_release_package_id");
        return releasePackage;
    }

    private static ReleasePackage addPlmCoordinator(ReleasePackage releasePackage, String dataIdentifier) {
        User user = new User();
        user.setAbbreviation(dataIdentifier + "_plm_coordinator");
        user.setDepartmentName(dataIdentifier + "_plm_coordinator");
        user.setEmail(dataIdentifier + "_plm_coordinator");
        user.setFullName(dataIdentifier + "_plm_coordinator");
        user.setUserId(dataIdentifier + "_plm_coordinator");
        releasePackage.setPlmCoordinator(user);
        return releasePackage;
    }

    private static ReleasePackage addReleasePackageSapChangeControl(ReleasePackage releasePackage, boolean sapChangeControl) {
        releasePackage.setSapChangeControl(false);
        return releasePackage;
    }

    private static ReleasePackage addReleasePackageType(ReleasePackage releasePackage, String type) {
//        ArrayList types = new ArrayList<String>();
//        types.add(type);
//        releasePackage.setTypes(types);
        return releasePackage;
    }

    private static ReleasePackage addReleasePackagePlannedEffectiveDate(ReleasePackage releasePackage, String dataIdentifier) {
        releasePackage.setPlannedEffectiveDate(getDate(dataIdentifier));
        return releasePackage;
    }

    private static ReleasePackage addReleasePackagePlannedReleasedDate(ReleasePackage releasePackage, String dataIdentifier) {
        releasePackage.setPlannedReleaseDate(getDate(dataIdentifier));
        return releasePackage;
    }

    private static ReleasePackage addReleasePackageContext(ReleasePackage releasePackage, String dataIdentifier) {
        List<ReleasePackageContext> releasePackageContexts = new ArrayList<>();
        ReleasePackageContext changeNoticeContext = new ReleasePackageContext();
        changeNoticeContext.setType("CHANGENOTICE");
        changeNoticeContext.setName(dataIdentifier + "_releasepackage_name");
        changeNoticeContext.setContextId(dataIdentifier + "_releasepackage_context_id");
        changeNoticeContext.setStatus("PLANNED");
        releasePackageContexts.add(changeNoticeContext);

        ReleasePackageContext reviewContext = new ReleasePackageContext();
        reviewContext.setType("REVIEW");
        reviewContext.setName(dataIdentifier + "_releasepackage_name_review");
        reviewContext.setContextId(dataIdentifier + "_releasepackage_context_id_review");
        reviewContext.setStatus(dataIdentifier + "_releasepackage_status_review");
        releasePackageContexts.add(reviewContext);

        ReleasePackageContext ecnContext = new ReleasePackageContext();
        ecnContext.setType("ECN");
        ecnContext.setName(dataIdentifier + "_releasepackage_name_ecn");
        ecnContext.setContextId(dataIdentifier + "_releasepackage_context_id_ecn");
        ecnContext.setStatus(dataIdentifier + "_releasepackage_status_ecn");
        releasePackageContexts.add(ecnContext);

        ReleasePackageContext teamcenterContext = new ReleasePackageContext();
        teamcenterContext.setType("TEAMCENTER");
        teamcenterContext.setContextId(dataIdentifier + "_releasepackage_context_id_teamcenter");
        releasePackageContexts.add(teamcenterContext);
        releasePackage.setContexts(releasePackageContexts);
        return releasePackage;
    }

    private static ReleasePackage addReleasePackageWithoutReviewContext(ReleasePackage releasePackage, String dataIdentifier) {
        List<ReleasePackageContext> releasePackageContexts = new ArrayList<>();
        ReleasePackageContext changeNoticeContext = new ReleasePackageContext();
        changeNoticeContext.setType("CHANGENOTICE");
        changeNoticeContext.setName(dataIdentifier + "_releasepackage_name");
        changeNoticeContext.setContextId(dataIdentifier + "_releasepackage_context_id");
        changeNoticeContext.setStatus(dataIdentifier + "_releasepackage_status");
        releasePackageContexts.add(changeNoticeContext);

        ReleasePackageContext ecnContext = new ReleasePackageContext();
        ecnContext.setType("ECN");
        ecnContext.setName(dataIdentifier + "_releasepackage_name_ecn");
        ecnContext.setContextId(dataIdentifier + "_releasepackage_context_id_ecn");
        ecnContext.setStatus(dataIdentifier + "_releasepackage_status_ecn");
        releasePackageContexts.add(ecnContext);

        ReleasePackageContext teamcenterContext = new ReleasePackageContext();
        teamcenterContext.setType("TEAMCENTER");
        teamcenterContext.setContextId(dataIdentifier + "_releasepackage_context_id_teamcenter");
        releasePackageContexts.add(teamcenterContext);

        releasePackage.setContexts(releasePackageContexts);
        return releasePackage;
    }

    private static ReleasePackage addReleasePackageContextForLinkedEntities(ReleasePackage releasePackage, String dataIdentifier) {
        List<ReleasePackageContext> releasePackageContexts = new ArrayList<>();

        ReleasePackageContext changeRequestContext = new ReleasePackageContext();
        changeRequestContext.setType("CHANGEREQUEST");
        changeRequestContext.setName(dataIdentifier + "_releasepackage_name_change_request");
        changeRequestContext.setContextId(dataIdentifier + "_releasepackage_context_id_change_request");
        changeRequestContext.setStatus(dataIdentifier + "_releasepackage_status_change_request");
        releasePackageContexts.add(changeRequestContext);

        ReleasePackageContext changeNoticeContext = new ReleasePackageContext();
        changeNoticeContext.setType("CHANGENOTICE");
        changeNoticeContext.setName(dataIdentifier + "_releasepackage_name_change_notice");
        changeNoticeContext.setContextId(dataIdentifier + "_releasepackage_context_id_change_notice");
        changeNoticeContext.setStatus(dataIdentifier + "_releasepackage_status_change_notice");
        releasePackageContexts.add(changeNoticeContext);

        ReleasePackageContext airContext = new ReleasePackageContext();
        airContext.setType("AIR");
        airContext.setName(dataIdentifier + "_releasepackage_name_air");
        airContext.setContextId(dataIdentifier + "_releasepackage_context_id_air");
        airContext.setStatus(dataIdentifier + "_releasepackage_status_air");
        releasePackageContexts.add(airContext);
        //ADD PBS

        ReleasePackageContext ecnContext = new ReleasePackageContext();
        ecnContext.setType("ECN");
        ecnContext.setName(dataIdentifier + "_releasepackage_name_ecn");
        ecnContext.setContextId(dataIdentifier + "_releasepackage_context_id_ecn");
        ecnContext.setStatus(dataIdentifier + "_releasepackage_status_ecn");
        releasePackageContexts.add(ecnContext);

        ReleasePackageContext teamcenterContext = new ReleasePackageContext();
        teamcenterContext.setType("TEAMCENTER");
        teamcenterContext.setContextId(dataIdentifier + "_releasepackage_context_id_teamcenter");
        releasePackageContexts.add(teamcenterContext);

        releasePackage.setContexts(releasePackageContexts);
        return releasePackage;
    }

    public static ReleasePackageMyTeam createMyTeam(Long releasePackageId) {
        ReleasePackage releasePackage = new ReleasePackage();
        releasePackage.setId(releasePackageId);
        ReleasePackageMyTeam releasePackageMyTeam = new ReleasePackageMyTeam();
        releasePackageMyTeam.setReleasePackage(releasePackage);
        return releasePackageMyTeam;
    }

    public static MyTeamMember createMyTeamMemberRequest(String dataIdentifier, ReleasePackageMyTeam savedReleasePackageMyTeam) throws JsonProcessingException {
        MyTeamMember myTeamMember = new MyTeamMember();
        myTeamMember.setMyteam(savedReleasePackageMyTeam);
        User user = new User();
        user.setAbbreviation(dataIdentifier + "_my_team_member-abbreviation");
        user.setDepartmentName(dataIdentifier + "_my_team_member_department_name");
        user.setEmail(dataIdentifier + "_my_team_member_email");
        user.setFullName(dataIdentifier + "_my_team_member_full_name");
        user.setUserId(dataIdentifier + "_my_team_member_user_id");
        myTeamMember.setUser(user);
        List<String> roles = new ArrayList<>();
        roles.add("submitterRequestor");
        roles.add("changeSpecialist2");
        myTeamMember.setRoles(roles);
        return myTeamMember;
    }

    public static User createMyTeamMember(String dataIdentifier) {
        User user = new User();
        user.setAbbreviation(dataIdentifier + "_my_team_member-abbreviation");
        user.setDepartmentName(dataIdentifier + "_my_team_member_department_name");
        user.setEmail(dataIdentifier + "_my_team_member_email");
        user.setFullName(dataIdentifier + "_my_team_member_full_name");
        user.setUserId(dataIdentifier + "_my_team_member_user_id");
        return user;
    }

    public static String getUpdateRequest(String oldIns, String newIns) throws JsonProcessingException {
        UpdateTeamMemberRequest request = new UpdateTeamMemberRequest();
        request.oldIns.roles.add(oldIns);

        request.newIns.roles.add(newIns);
        return new ObjectMapper().writeValueAsString(request);
    }

    private static ReleasePackage createReleasePackage(String dataIdentifier) {
        ReleasePackage releasePackage = new ReleasePackage();

        List<ReleasePackageContext> reviewContexts = new ArrayList<>();

        ReleasePackageContext releasePackageContext = new ReleasePackageContext();
        releasePackageContext.setType("RELEASEPACKAGE");
        releasePackageContext.setName(dataIdentifier + "_releasepackage_name");
        releasePackageContext.setContextId(dataIdentifier + "_releasepackage_context_id");
        releasePackageContext.setStatus(dataIdentifier + "_releasepackage_status");
        reviewContexts.add(releasePackageContext);

        releasePackage.setContexts(reviewContexts);

        return releasePackage;
    }

    //RPComment
    public static ReleasePackageComment createReleasePackageComment(String dataIdentifier, String properties) {

        ReleasePackageComment releasePackageComment = new ReleasePackageComment();
        switch (properties) {
            case "ALL_PROPERTIES":
                addReleasePackageCommentText(releasePackageComment, dataIdentifier);
                addReleasePackageCommentCreator(releasePackageComment, dataIdentifier);
                addReleasePackageCommentCreatedOn(releasePackageComment, dataIdentifier);
                return releasePackageComment;
            default:
                return releasePackageComment;
        }
    }

    private static ReleasePackageComment addReleasePackageCommentCreator(ReleasePackageComment releasePackageComment, String dataIdentifier) {
        User creator = new User();
        creator.setAbbreviation(dataIdentifier + "_Comment.creator_abbreviation");
        creator.setDepartmentName(dataIdentifier + "_Comment.creator_department_name");
        creator.setEmail(dataIdentifier + "_Comment.creator_email");
        creator.setFullName(dataIdentifier + "_Comment.creator_full_name");
        creator.setUserId(dataIdentifier + "_Comment.creator_user_id");
        releasePackageComment.setCreator(creator);
        return releasePackageComment;
    }

    private static ReleasePackageComment addReleasePackageCommentText(ReleasePackageComment releasePackageComment, String dataIdentifier) {
        releasePackageComment.setCommentText(dataIdentifier + "_text");
        return releasePackageComment;
    }

    private static ReleasePackageComment addReleasePackageCommentCreatedOn(ReleasePackageComment releasePackageComment, String dataIdentifier) {
        releasePackageComment.setCreatedOn(getDate(dataIdentifier));
        return releasePackageComment;
    }

    public static ReleasePackageCommentDocument createReleasePackageCommentDocument(String dataIdentifier, String properties) {

        ReleasePackageCommentDocument releasePackageCommentDocument = new ReleasePackageCommentDocument();
        switch (properties) {
            case "ALL_PROPERTIES":
                addReleasePackageCommentDocumentTags(releasePackageCommentDocument, dataIdentifier);
                addReleasePackageCommentDocumentCreator(releasePackageCommentDocument, dataIdentifier);
                addReleasePackageCommentDocumentCreatedOn(releasePackageCommentDocument, dataIdentifier);
                addReleasePackageCommentDocumentDescription(releasePackageCommentDocument, dataIdentifier);
                return releasePackageCommentDocument;
            default:
                return releasePackageCommentDocument;
        }
    }

    private static ReleasePackageCommentDocument addReleasePackageCommentDocumentTags(ReleasePackageCommentDocument releasePackageCommentDocument, String dataIdentifier) {
        List<String> documentTags = new ArrayList<>();
        documentTags.add("Notes");
        releasePackageCommentDocument.setTags(Collections.singletonList(dataIdentifier + documentTags));
        return releasePackageCommentDocument;
    }

    private static ReleasePackageCommentDocument addReleasePackageCommentDocumentDescription(ReleasePackageCommentDocument releasePackageCommentDocument, String dataIdentifier) {
        releasePackageCommentDocument.setDescription(dataIdentifier + "_description");
        return releasePackageCommentDocument;
    }

    //RP Comment Document

    private static ReleasePackageCommentDocument addReleasePackageCommentDocumentCreator(ReleasePackageCommentDocument releasePackageCommentDocument, String dataIdentifier) {
        User creator = new User();
        creator.setAbbreviation(dataIdentifier + "_ReleasePackageComment.creator_abbreviation");
        creator.setDepartmentName(dataIdentifier + "_ReleasePackageComment.creator_department_name");
        creator.setEmail(dataIdentifier + "_ReleasePackageComment.creator_email");
        creator.setFullName(dataIdentifier + "_ReleasePackageComment.creator_full_name");
        creator.setUserId(dataIdentifier + "_ReleasePackageComment.creator_user_id");
        releasePackageCommentDocument.setCreator(creator);
        return releasePackageCommentDocument;
    }

    private static ReleasePackageCommentDocument addReleasePackageCommentDocumentCreatedOn(ReleasePackageCommentDocument releasePackageCommentDocument, String dataIdentifier) {
        releasePackageCommentDocument.setCreatedOn(getDate(dataIdentifier));
        return releasePackageCommentDocument;
    }

    //RP Document
    public static ReleasePackageDocument createReleasePackageDocument(String dataIdentifier, String properties) {

        ReleasePackageDocument releasePackageDocument = new ReleasePackageDocument();
        switch (properties) {
            case "ALL_PROPERTIES":
                addReleasePackageDocumentTags(releasePackageDocument, dataIdentifier);
                addReleasePackageDocumentCreator(releasePackageDocument, dataIdentifier);
                addReleasePackageDocumentCreatedOn(releasePackageDocument, dataIdentifier);
                addReleasePackageDocumentDescription(releasePackageDocument, dataIdentifier);
                return releasePackageDocument;
            default:
                return releasePackageDocument;
        }
    }

    private static ReleasePackageDocument addReleasePackageDocumentTags(ReleasePackageDocument releasePackageDocument, String dataIdentifier) {
        List<String> documentTags = new ArrayList<>();
        documentTags.add("Notes");
        releasePackageDocument.setTags(Collections.singletonList(dataIdentifier + documentTags));
        return releasePackageDocument;
    }

    private static ReleasePackageDocument addReleasePackageDocumentDescription(ReleasePackageDocument releasePackageDocument, String dataIdentifier) {
        releasePackageDocument.setDescription(dataIdentifier + "_description");
        return releasePackageDocument;
    }

    private static ReleasePackageDocument addReleasePackageDocumentCreator(ReleasePackageDocument releasePackageDocument, String dataIdentifier) {
        User creator = new User();
        creator.setAbbreviation(dataIdentifier + "_cug-projectname-change-specialist-2_abbreviation");
        creator.setDepartmentName(dataIdentifier + "_cug-projectname-change-specialist-2_department_name");
        creator.setEmail(dataIdentifier + "_cug-projectname-change-specialist-2_email");
        creator.setFullName(dataIdentifier + "_cug-projectname-change-specialist-2_full_name");
        creator.setUserId(dataIdentifier + "_cug-projectname-change-specialist-2_user_id");
        releasePackageDocument.setCreator(creator);
        return releasePackageDocument;
    }

    private static ReleasePackageDocument addReleasePackageDocumentCreatedOn(ReleasePackageDocument releasePackageDocument, String dataIdentifier) {
        releasePackageDocument.setCreatedOn(getDate(dataIdentifier));
        return releasePackageDocument;
    }

    public static List<String> addReleasePackageTags(String dataIdentifier){
        List<String> tags = new ArrayList<>();
        tags.add(dataIdentifier+"-1");
        tags.add(dataIdentifier+"-2");
        return tags;
    }
    @Data
    private static class TeamMemberDetail {
        User user;
        String[] roles;
    }

    @Data
    private static class TeamMemberRole {
        List<String> roles = new ArrayList<>();
    }

    @Data
    private static class UpdateTeamMemberRequest {
        TeamMemberRole oldIns = new TeamMemberRole();
        TeamMemberRole newIns = new TeamMemberRole();
    }

    public static List<String> addReleasePackageTypes(){
        List<String> types = new ArrayList<>();
        types.add("WI");
        return types;
    }

    private static ReleasePackage addReleasePackagePlmCoordinator(ReleasePackage releasePackage, String dataIdentifier) {
        User plmCoordinator = new User();
        plmCoordinator.setAbbreviation(dataIdentifier + "_release_package-plm_coordinator_abbreviation");
        plmCoordinator.setDepartmentName(dataIdentifier + "_release_package-plm_coordinator_department_name");
        plmCoordinator.setEmail(dataIdentifier + "_release_package-plm_coordinator_email");
        plmCoordinator.setFullName(dataIdentifier + "_release_package-plm_coordinator_full_name");
        plmCoordinator.setUserId(dataIdentifier + "_release_package-plm_coordinator_user_id");
        releasePackage.setPlmCoordinator(plmCoordinator);

        return releasePackage;
    }

   /* static ReleasePackage addReleasePackageContextChangeObject(ReleasePackage releasePackage, String dataIdentifier, ReleasePackageCaseActions releasePackageCaseActions){

        List<ReleasePackageContext> releasePackageContexts = new ArrayList<>();
          ReleasePackageContext changeObjectContext = new ReleasePackageContext();
        changeObjectContext.setType("CHANGEOBJECT");
        if(releasePackageCaseActions.equals(ReleasePackageCaseActions.CREATE)) {
            changeObjectContext.setStatus("3");
        }
        else if(releasePackageCaseActions.equals(ReleasePackageCaseActions.READY)) {
            changeObjectContext.setStatus("4");
        }
        changeObjectContext.setContextId(dataIdentifier + "_releasepackage_context_id_changeobject");
        releasePackageContexts.add(changeObjectContext);

        releasePackage.setContexts(releasePackageContexts);
        return releasePackage;

    }*/

    public static ReleasePackage createReleasePackageForCaseAction(String dataIdentifier, String properties,ReleasePackageCaseActions releasePackageCaseActions) {

        ReleasePackage releasePackage = new ReleasePackage();

        switch (properties) {
            case "ALL_PROPERTIES":
                addReleasePackageContextForCaseAction(releasePackage, dataIdentifier,releasePackageCaseActions);
                addReleasePackageTitle(releasePackage, dataIdentifier);
                addReleasePackageId(releasePackage, dataIdentifier);
                addPlmCoordinator(releasePackage, dataIdentifier);
                addReleasePackageSapChangeControl(releasePackage, false);
                addReleasePackageType(releasePackage, "CHANGENOTICE");
                addReleasePackagePlannedEffectiveDate(releasePackage, dataIdentifier);
                addReleasePackagePlannedReleasedDate(releasePackage, dataIdentifier);
                addReleasePackageExecutor(releasePackage, dataIdentifier);
                addReleasePackageChangeSpecialist3(releasePackage, dataIdentifier);
                addReleasePackageCreator(releasePackage, dataIdentifier);
                addReleasePackageCreatedOn(releasePackage, dataIdentifier);
                addReleasePackagePlmCoordinator(releasePackage,dataIdentifier);
                return releasePackage;
            case "ALL_PROPERTIES_EXCEPT_TITLE":
                addReleasePackageContextForCaseAction(releasePackage, dataIdentifier,releasePackageCaseActions);
                addReleasePackageId(releasePackage, dataIdentifier);
                addReleasePackageSapChangeControl(releasePackage, false);
                addReleasePackageType(releasePackage, "CHANGENOTICE");
                addReleasePackagePlannedEffectiveDate(releasePackage, dataIdentifier);
                addReleasePackagePlannedReleasedDate(releasePackage, dataIdentifier);
                addReleasePackageExecutor(releasePackage, dataIdentifier);
                addReleasePackageChangeSpecialist3(releasePackage, dataIdentifier);
                addReleasePackageCreator(releasePackage, dataIdentifier);
                addReleasePackageCreatedOn(releasePackage, dataIdentifier);
                addReleasePackagePlmCoordinator(releasePackage,dataIdentifier);
                return releasePackage;
            case "ALL_PROPERTIES_EXCEPT_PLANNED_RELEASE_DATE":
                addReleasePackageContextForCaseAction(releasePackage, dataIdentifier,releasePackageCaseActions);
                addReleasePackageTitle(releasePackage, dataIdentifier);
                addReleasePackageId(releasePackage, dataIdentifier);
                addReleasePackageSapChangeControl(releasePackage, false);
                addReleasePackageType(releasePackage, "CHANGENOTICE");
                addReleasePackagePlannedEffectiveDate(releasePackage, dataIdentifier);
                addReleasePackageExecutor(releasePackage, dataIdentifier);
                addReleasePackageChangeSpecialist3(releasePackage, dataIdentifier);
                addReleasePackageCreator(releasePackage, dataIdentifier);
                addReleasePackageCreatedOn(releasePackage, dataIdentifier);
                addReleasePackagePlmCoordinator(releasePackage,dataIdentifier);
                return releasePackage;
            case "ALL_PROPERTIES_EXCEPT_PLANNED_EFFECTIVE_DATE":
                addReleasePackageContextForCaseAction(releasePackage, dataIdentifier,releasePackageCaseActions);
                addReleasePackageTitle(releasePackage, dataIdentifier);
                addReleasePackageId(releasePackage, dataIdentifier);
                addReleasePackageSapChangeControl(releasePackage, false);
                addReleasePackageType(releasePackage, "CHANGENOTICE");
                addReleasePackagePlannedReleasedDate(releasePackage, dataIdentifier);
                addReleasePackageExecutor(releasePackage, dataIdentifier);
                addReleasePackageChangeSpecialist3(releasePackage, dataIdentifier);
                addReleasePackageCreator(releasePackage, dataIdentifier);
                addReleasePackageCreatedOn(releasePackage, dataIdentifier);
                addReleasePackagePlmCoordinator(releasePackage,dataIdentifier);
                return releasePackage;
            case "ALL_PROPERTIES_EXCEPT_SAP_CHANGE_CONTROL":
                addReleasePackageContextForCaseAction(releasePackage, dataIdentifier,releasePackageCaseActions);
                addReleasePackageTitle(releasePackage, dataIdentifier);
                addReleasePackageId(releasePackage, dataIdentifier);
                addReleasePackageType(releasePackage, "CHANGENOTICE");
                addReleasePackagePlannedEffectiveDate(releasePackage, dataIdentifier);
                addReleasePackagePlannedReleasedDate(releasePackage, dataIdentifier);
                addReleasePackageExecutor(releasePackage, dataIdentifier);
                addReleasePackageChangeSpecialist3(releasePackage, dataIdentifier);
                addReleasePackageCreator(releasePackage, dataIdentifier);
                addReleasePackageCreatedOn(releasePackage, dataIdentifier);
                addReleasePackagePlmCoordinator(releasePackage,dataIdentifier);
                return releasePackage;
            case "ALL_PROPERTIES_LINKED_ENTITIES":
                addReleasePackageContextForLinkedEntities(releasePackage, dataIdentifier);
                addReleasePackageTitle(releasePackage, dataIdentifier);
                addReleasePackageId(releasePackage, dataIdentifier);
                addReleasePackageType(releasePackage, "CHANGENOTICE");
                addReleasePackagePlannedEffectiveDate(releasePackage, dataIdentifier);
                addReleasePackagePlannedReleasedDate(releasePackage, dataIdentifier);
                addReleasePackageExecutor(releasePackage, dataIdentifier);
                addReleasePackageChangeSpecialist3(releasePackage, dataIdentifier);
                addReleasePackageCreator(releasePackage, dataIdentifier);
                addReleasePackageCreatedOn(releasePackage, dataIdentifier);
                addReleasePackagePlmCoordinator(releasePackage,dataIdentifier);
                return releasePackage;
            case "ALL_PROPERTIES_WITHOUT_REVIEW_CONTEXT":
                addReleasePackageWithoutReviewContext(releasePackage, dataIdentifier);
                addReleasePackageSapChangeControl(releasePackage, false);
                addReleasePackageTitle(releasePackage, dataIdentifier);
                addReleasePackageId(releasePackage, dataIdentifier);
                addReleasePackageType(releasePackage, "CHANGENOTICE");
                addReleasePackagePlannedEffectiveDate(releasePackage, dataIdentifier);
                addReleasePackagePlannedReleasedDate(releasePackage, dataIdentifier);
                addReleasePackageExecutor(releasePackage, dataIdentifier);
                addReleasePackageChangeSpecialist3(releasePackage, dataIdentifier);
                addReleasePackageCreator(releasePackage, dataIdentifier);
                addReleasePackageCreatedOn(releasePackage, dataIdentifier);
                addReleasePackagePlmCoordinator(releasePackage,dataIdentifier);
                return releasePackage;
            case "ALL_PROPERTIES_EXCEPT_TYPES":
                addReleasePackageContextForCaseAction(releasePackage, dataIdentifier,releasePackageCaseActions);
                addReleasePackageTitle(releasePackage, dataIdentifier);
                addReleasePackageId(releasePackage, dataIdentifier);
                addReleasePackageSapChangeControl(releasePackage, false);
                addReleasePackagePlmCoordinator(releasePackage,dataIdentifier);
                addReleasePackagePlannedEffectiveDate(releasePackage, dataIdentifier);
                addReleasePackagePlannedReleasedDate(releasePackage, dataIdentifier);
                addReleasePackageExecutor(releasePackage, dataIdentifier);
                addReleasePackageChangeSpecialist3(releasePackage, dataIdentifier);
                addReleasePackageCreator(releasePackage, dataIdentifier);
                addReleasePackageCreatedOn(releasePackage, dataIdentifier);
                return releasePackage;
            case "ALL_PROPERTIES_EXCEPT_PLMCOORDINATOR":
                addReleasePackageContextForCaseAction(releasePackage, dataIdentifier,releasePackageCaseActions);
                addReleasePackageTitle(releasePackage, dataIdentifier);
                addReleasePackageId(releasePackage, dataIdentifier);
                addReleasePackageSapChangeControl(releasePackage, false);
                addReleasePackagePlannedEffectiveDate(releasePackage, dataIdentifier);
                addReleasePackagePlannedReleasedDate(releasePackage, dataIdentifier);
                addReleasePackageExecutor(releasePackage, dataIdentifier);
                addReleasePackageChangeSpecialist3(releasePackage, dataIdentifier);
                addReleasePackageCreator(releasePackage, dataIdentifier);
                addReleasePackageCreatedOn(releasePackage, dataIdentifier);
                addReleasePackageType(releasePackage, "CHANGENOTICE");
                return releasePackage;
            default:
                return releasePackage;
        }


    }

    private static ReleasePackage addReleasePackageContextForCaseAction(ReleasePackage releasePackage, String dataIdentifier,ReleasePackageCaseActions releasePackageCaseActions) {
        List<ReleasePackageContext> releasePackageContexts = new ArrayList<>();
        ReleasePackageContext changeNoticeContext = new ReleasePackageContext();
        changeNoticeContext.setType("CHANGENOTICE");
        changeNoticeContext.setName(dataIdentifier + "_releasepackage_name");
        changeNoticeContext.setContextId(dataIdentifier + "_releasepackage_context_id");
        changeNoticeContext.setStatus("PLANNED");
        releasePackageContexts.add(changeNoticeContext);

        ReleasePackageContext reviewContext = new ReleasePackageContext();
        reviewContext.setType("REVIEW");
        reviewContext.setName(dataIdentifier + "_releasepackage_name_review");
        reviewContext.setContextId(dataIdentifier + "_releasepackage_context_id_review");
        reviewContext.setStatus(dataIdentifier + "_releasepackage_status_review");
        releasePackageContexts.add(reviewContext);

        ReleasePackageContext ecnContext = new ReleasePackageContext();
        ecnContext.setType("ECN");
        ecnContext.setName(dataIdentifier + "_releasepackage_name_ecn");
        ecnContext.setContextId(dataIdentifier + "_releasepackage_context_id_ecn");
        ecnContext.setStatus(dataIdentifier + "_releasepackage_status_ecn");
        releasePackageContexts.add(ecnContext);

        ReleasePackageContext teamcenterContext = new ReleasePackageContext();
        teamcenterContext.setType("TEAMCENTER");
        teamcenterContext.setContextId(dataIdentifier + "_releasepackage_context_id_teamcenter");
        releasePackageContexts.add(teamcenterContext);

        ReleasePackageContext changeObjectContext = new ReleasePackageContext();
        changeObjectContext.setType("CHANGEOBJECT");
        if(releasePackageCaseActions.equals(ReleasePackageCaseActions.CREATE) || releasePackageCaseActions.equals(ReleasePackageCaseActions.OBSOLETE)) {
            changeObjectContext.setStatus("3");
        }
        else if(releasePackageCaseActions.equals(ReleasePackageCaseActions.READY)) {
            changeObjectContext.setStatus("4");
        }
        else if(releasePackageCaseActions.equals(ReleasePackageCaseActions.RELEASE) || releasePackageCaseActions.equals(ReleasePackageCaseActions.RECREATE)) {
            changeObjectContext.setStatus("4");
        }
        else if(releasePackageCaseActions.equals(ReleasePackageCaseActions.CLOSE) || releasePackageCaseActions.equals(ReleasePackageCaseActions.REREADY)) {
            changeObjectContext.setStatus("6");
        }
        changeObjectContext.setContextId(dataIdentifier + "_releasepackage_context_id_changeobject");
        releasePackageContexts.add(changeObjectContext);

        releasePackage.setContexts(releasePackageContexts);
        return releasePackage;
    }
}
