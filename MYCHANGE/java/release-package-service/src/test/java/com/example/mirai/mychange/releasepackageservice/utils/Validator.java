package com.example.mirai.projectname.releasepackageservice.utils;


import com.example.mirai.libraries.comment.model.Comment;
import com.example.mirai.libraries.document.model.Document;
import com.example.mirai.projectname.releasepackageservice.comment.model.ReleasePackageComment;
import com.example.mirai.projectname.releasepackageservice.document.model.ReleasePackageCommentDocument;
import com.example.mirai.projectname.releasepackageservice.json.ReleasePackageCommentDocumentJson;
import com.example.mirai.projectname.releasepackageservice.json.ReleasePackageCommentJson;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class Validator {

    public static void releasePackagesAreSameWithoutComparingTitle(ReleasePackage releasePackage1, ReleasePackage releasePackage2) {
        assertThat("planned effective dates are not same", releasePackage1.getPlannedEffectiveDate(), equalTo(releasePackage2.getPlannedEffectiveDate()));
        assertThat("planned release dates are not same", releasePackage1.getPlannedReleaseDate(), equalTo(releasePackage2.getPlannedReleaseDate()));
        assertThat("executors is null", releasePackage1.getExecutor(), is(notNullValue()));
        //assertThat("executors are not same", releasePackage1.getExecutor(), samePropertyValuesAs(releasePackage2.getExecutor()));
        assertThat("sap change control are not same", releasePackage1.getSapChangeControl(), equalTo(releasePackage2.getSapChangeControl()));
        assertThat("project id are not same", releasePackage1.getProjectId(), equalTo(releasePackage2.getProjectId()));
        assertThat("product id are not same", releasePackage1.getProductId(), equalTo(releasePackage2.getProductId()));
        assertThat("prerequisites detail are not same", releasePackage1.getPrerequisitesDetail(), equalTo(releasePackage2.getPrerequisitesDetail()));
        assertThat("prerequisites applicable are not same", releasePackage1.getPrerequisitesApplicable(), equalTo(releasePackage2.getPrerequisitesApplicable()));
    }

    public static void releasePackagesAreSameWithoutComparingStatus(ReleasePackage releasePackage1, ReleasePackage releasePackage2) {
        assertThat("planned effective dates are not same", releasePackage1.getPlannedEffectiveDate(), equalTo(releasePackage2.getPlannedEffectiveDate()));
        assertThat("planned release dates are not same", releasePackage1.getPlannedReleaseDate(), equalTo(releasePackage2.getPlannedReleaseDate()));
        assertThat("executors is null", releasePackage1.getExecutor(), is(notNullValue()));
        //assertThat("executors are not same", releasePackage1.getExecutor(), samePropertyValuesAs(releasePackage2.getExecutor()));
        assertThat("sap change control are not same", releasePackage1.getSapChangeControl(), equalTo(releasePackage2.getSapChangeControl()));
        assertThat("project id are not same", releasePackage1.getProjectId(), equalTo(releasePackage2.getProjectId()));
        assertThat("product id are not same", releasePackage1.getProductId(), equalTo(releasePackage2.getProductId()));
        assertThat("prerequisites detail are not same", releasePackage1.getPrerequisitesDetail(), equalTo(releasePackage2.getPrerequisitesDetail()));
        assertThat("prerequisites applicable are not same", releasePackage1.getPrerequisitesApplicable(), equalTo(releasePackage2.getPrerequisitesApplicable()));
    }


    public static void releasePackagesAreSameWithoutComparingExecutor(ReleasePackage releasePackage1, ReleasePackage releasePackage2) {
        assertThat("titles are not same", releasePackage1.getTitle(), equalTo(releasePackage2.getTitle()));
        assertThat("planned effective dates are not same", releasePackage1.getPlannedEffectiveDate(), equalTo(releasePackage2.getPlannedEffectiveDate()));
        assertThat("planned release dates are not same", releasePackage1.getPlannedReleaseDate(), equalTo(releasePackage2.getPlannedReleaseDate()));
        assertThat("change specialist3 is null", releasePackage1.getChangeSpecialist3(), is(notNullValue()));
        //assertThat("change specialist3 are not same", releasePackage1.getChangeSpecialist3(), samePropertyValuesAs(releasePackage2.getChangeSpecialist3()));
        assertThat("sap change control are not same", releasePackage1.getSapChangeControl(), equalTo(releasePackage2.getSapChangeControl()));
        assertThat("project id are not same", releasePackage1.getProjectId(), equalTo(releasePackage2.getProjectId()));
        assertThat("product id are not same", releasePackage1.getProductId(), equalTo(releasePackage2.getProductId()));
        assertThat("prerequisites detail are not same", releasePackage1.getPrerequisitesDetail(), equalTo(releasePackage2.getPrerequisitesDetail()));
        assertThat("prerequisites applicable are not same", releasePackage1.getPrerequisitesApplicable(), equalTo(releasePackage2.getPrerequisitesApplicable()));

    }

    public static void releasePackagesAreSameWithoutComparingTags(ReleasePackage releasePackage1, ReleasePackage releasePackage2) {
        assertThat("tags are same", releasePackage1.getTags(), not(releasePackage2.getTags()));
        releasePackagesAreSame(releasePackage1,releasePackage2);
    }

    public static void releasePackagesAreSameWithoutComparingPlannedReleaseDate(ReleasePackage releasePackage1, ReleasePackage releasePackage2) {
        assertThat("titles are not same", releasePackage1.getTitle(), equalTo(releasePackage2.getTitle()));
        assertThat("planned effective dates are not same", releasePackage1.getPlannedEffectiveDate(), equalTo(releasePackage2.getPlannedEffectiveDate()));
        assertThat("change specialist3 is null", releasePackage1.getChangeSpecialist3(), is(notNullValue()));
        //assertThat("change specialist3 are not same", releasePackage1.getChangeSpecialist3(), samePropertyValuesAs(releasePackage2.getChangeSpecialist3()));
        assertThat("sap change control are not same", releasePackage1.getSapChangeControl(), equalTo(releasePackage2.getSapChangeControl()));
        assertThat("project id are not same", releasePackage1.getProjectId(), equalTo(releasePackage2.getProjectId()));
        assertThat("product id are not same", releasePackage1.getProductId(), equalTo(releasePackage2.getProductId()));
        assertThat("prerequisites detail are not same", releasePackage1.getPrerequisitesDetail(), equalTo(releasePackage2.getPrerequisitesDetail()));
        assertThat("prerequisites applicable are not same", releasePackage1.getPrerequisitesApplicable(), equalTo(releasePackage2.getPrerequisitesApplicable()));
    }

    public static void releasePackagesAreSameWithoutComparingPlannedEffectiveDate(ReleasePackage releasePackage1, ReleasePackage releasePackage2) {
        assertThat("titles are not same", releasePackage1.getTitle(), equalTo(releasePackage2.getTitle()));
        assertThat("planned release dates are not same", releasePackage1.getPlannedReleaseDate(), equalTo(releasePackage2.getPlannedReleaseDate()));
        assertThat("change specialist3 is null", releasePackage1.getChangeSpecialist3(), is(notNullValue()));
        //assertThat("change specialist3 are not same", releasePackage1.getChangeSpecialist3(), samePropertyValuesAs(releasePackage2.getChangeSpecialist3()));
        assertThat("sap change control are not same", releasePackage1.getSapChangeControl(), equalTo(releasePackage2.getSapChangeControl()));
        assertThat("project id are not same", releasePackage1.getProjectId(), equalTo(releasePackage2.getProjectId()));
        assertThat("product id are not same", releasePackage1.getProductId(), equalTo(releasePackage2.getProductId()));
        assertThat("prerequisites detail are not same", releasePackage1.getPrerequisitesDetail(), equalTo(releasePackage2.getPrerequisitesDetail()));
        assertThat("prerequisites applicable are not same", releasePackage1.getPrerequisitesApplicable(), equalTo(releasePackage2.getPrerequisitesApplicable()));

    }

    public static void releasePackagesAreSameWithoutComparingPrerequisitesApplicable(ReleasePackage releasePackage1, ReleasePackage releasePackage2) {
        assertThat("titles are not same", releasePackage1.getTitle(), equalTo(releasePackage2.getTitle()));
        assertThat("planned release dates are not same", releasePackage1.getPlannedReleaseDate(), equalTo(releasePackage2.getPlannedReleaseDate()));
        assertThat("change specialist3 is null", releasePackage1.getChangeSpecialist3(), is(notNullValue()));
       // assertThat("change specialist3 are not same", releasePackage1.getChangeSpecialist3(), samePropertyValuesAs(releasePackage2.getChangeSpecialist3()));
        assertThat("sap change control are not same", releasePackage1.getSapChangeControl(), equalTo(releasePackage2.getSapChangeControl()));
        assertThat("project id are not same", releasePackage1.getProjectId(), equalTo(releasePackage2.getProjectId()));
        assertThat("product id are not same", releasePackage1.getProductId(), equalTo(releasePackage2.getProductId()));
        assertThat("prerequisites detail are not same", releasePackage1.getPrerequisitesDetail(), equalTo(releasePackage2.getPrerequisitesDetail()));
        assertThat("planned effective dates are not same", releasePackage1.getPlannedEffectiveDate(), equalTo(releasePackage2.getPlannedEffectiveDate()));

    }

    public static void releasePackagesAreSameWithoutComparingPrerequisitesDetail(ReleasePackage releasePackage1, ReleasePackage releasePackage2) {
        assertThat("titles are not same", releasePackage1.getTitle(), equalTo(releasePackage2.getTitle()));
        assertThat("planned release dates are not same", releasePackage1.getPlannedReleaseDate(), equalTo(releasePackage2.getPlannedReleaseDate()));
        assertThat("change specialist3 is null", releasePackage1.getChangeSpecialist3(), is(notNullValue()));
        //assertThat("change specialist3 are not same", releasePackage1.getChangeSpecialist3(), samePropertyValuesAs(releasePackage2.getChangeSpecialist3()));
        assertThat("sap change control are not same", releasePackage1.getSapChangeControl(), equalTo(releasePackage2.getSapChangeControl()));
        assertThat("project id are not same", releasePackage1.getProjectId(), equalTo(releasePackage2.getProjectId()));
        assertThat("product id are not same", releasePackage1.getProductId(), equalTo(releasePackage2.getProductId()));
        assertThat("prerequisites applicable are not same", releasePackage1.getPrerequisitesApplicable(), equalTo(releasePackage2.getPrerequisitesApplicable()));
        assertThat("planned effective dates are not same", releasePackage1.getPlannedEffectiveDate(), equalTo(releasePackage2.getPlannedEffectiveDate()));

    }

    public static void releasePackagesAreSameWithoutComparingProductId(ReleasePackage releasePackage1, ReleasePackage releasePackage2) {
        assertThat("titles are not same", releasePackage1.getTitle(), equalTo(releasePackage2.getTitle()));
        assertThat("planned release dates are not same", releasePackage1.getPlannedReleaseDate(), equalTo(releasePackage2.getPlannedReleaseDate()));
        assertThat("change specialist3 is null", releasePackage1.getChangeSpecialist3(), is(notNullValue()));
       // assertThat("change specialist3 are not same", releasePackage1.getChangeSpecialist3(), samePropertyValuesAs(releasePackage2.getChangeSpecialist3()));
        assertThat("sap change control are not same", releasePackage1.getSapChangeControl(), equalTo(releasePackage2.getSapChangeControl()));
        assertThat("project id are not same", releasePackage1.getProjectId(), equalTo(releasePackage2.getProjectId()));
        assertThat("prerequisites applicable are not same", releasePackage1.getPrerequisitesApplicable(), equalTo(releasePackage2.getPrerequisitesApplicable()));
        assertThat("prerequisites detail are not same", releasePackage1.getPrerequisitesDetail(), equalTo(releasePackage2.getPrerequisitesDetail()));
        assertThat("planned effective dates are not same", releasePackage1.getPlannedEffectiveDate(), equalTo(releasePackage2.getPlannedEffectiveDate()));

    }

    public static void releasePackagesAreSameWithoutComparingProjectId(ReleasePackage releasePackage1, ReleasePackage releasePackage2) {
        assertThat("titles are not same", releasePackage1.getTitle(), equalTo(releasePackage2.getTitle()));
        assertThat("planned release dates are not same", releasePackage1.getPlannedReleaseDate(), equalTo(releasePackage2.getPlannedReleaseDate()));
        assertThat("change specialist3 is null", releasePackage1.getChangeSpecialist3(), is(notNullValue()));
       // assertThat("change specialist3 are not same", releasePackage1.getChangeSpecialist3(), samePropertyValuesAs(releasePackage2.getChangeSpecialist3()));
        assertThat("sap change control are not same", releasePackage1.getSapChangeControl(), equalTo(releasePackage2.getSapChangeControl()));
        assertThat("product id are not same", releasePackage1.getProductId(), equalTo(releasePackage2.getProductId()));
        assertThat("prerequisites applicable are not same", releasePackage1.getPrerequisitesApplicable(), equalTo(releasePackage2.getPrerequisitesApplicable()));
        assertThat("prerequisites detail are not same", releasePackage1.getPrerequisitesDetail(), equalTo(releasePackage2.getPrerequisitesDetail()));
        assertThat("planned effective dates are not same", releasePackage1.getPlannedEffectiveDate(), equalTo(releasePackage2.getPlannedEffectiveDate()));

    }

    public static void releasePackagesAreSameWithoutComparingReleasePackagePrerequisites(ReleasePackage releasePackage1, ReleasePackage releasePackage2) {
        assertThat("titles are not same", releasePackage1.getTitle(), equalTo(releasePackage2.getTitle()));
        assertThat("planned release dates are not same", releasePackage1.getPlannedReleaseDate(), equalTo(releasePackage2.getPlannedReleaseDate()));
        assertThat("change specialist3 is null", releasePackage1.getChangeSpecialist3(), is(notNullValue()));
     //   assertThat("change specialist3 are not same", releasePackage1.getChangeSpecialist3(), samePropertyValuesAs(releasePackage2.getChangeSpecialist3()));
        assertThat("sap change control are not same", releasePackage1.getSapChangeControl(), equalTo(releasePackage2.getSapChangeControl()));
        assertThat("product id are not same", releasePackage1.getProductId(), equalTo(releasePackage2.getProductId()));
        assertThat("project id are not same", releasePackage1.getProjectId(), equalTo(releasePackage2.getProjectId()));
        assertThat("prerequisites applicable are not same", releasePackage1.getPrerequisitesApplicable(), equalTo(releasePackage2.getPrerequisitesApplicable()));
        assertThat("prerequisites detail are not same", releasePackage1.getPrerequisitesDetail(), equalTo(releasePackage2.getPrerequisitesDetail()));
        assertThat("planned effective dates are not same", releasePackage1.getPlannedEffectiveDate(), equalTo(releasePackage2.getPlannedEffectiveDate()));

    }

    public static void releasePackagesAreSameWithoutComparingChangeSpecialist3(ReleasePackage releasePackage1, ReleasePackage releasePackage2) {
        assertThat("titles are not same", releasePackage1.getTitle(), equalTo(releasePackage2.getTitle()));
        assertThat("executors is null", releasePackage1.getExecutor(), is(notNullValue()));
       // assertThat("executors are not same", releasePackage1.getExecutor(), samePropertyValuesAs(releasePackage2.getExecutor()));
        assertThat("planned effective dates are not same", releasePackage1.getPlannedEffectiveDate(), equalTo(releasePackage2.getPlannedEffectiveDate()));
        assertThat("planned release dates are not same", releasePackage1.getPlannedReleaseDate(), equalTo(releasePackage2.getPlannedReleaseDate()));
        assertThat("sap change control are not same", releasePackage1.getSapChangeControl(), equalTo(releasePackage2.getSapChangeControl()));
        assertThat("project id are not same", releasePackage1.getProjectId(), equalTo(releasePackage2.getProjectId()));
        assertThat("product id are not same", releasePackage1.getProductId(), equalTo(releasePackage2.getProductId()));
        assertThat("prerequisites detail are not same", releasePackage1.getPrerequisitesDetail(), equalTo(releasePackage2.getPrerequisitesDetail()));

    }


    public static void releasePackagesAreSameWithoutComparingAuditAndStatus(ReleasePackage releasePackage1, ReleasePackage releasePackage2) {
        assertThat("titles are not same", releasePackage1.getTitle(), equalTo(releasePackage2.getTitle()));
        assertThat("planned effective dates are not same", releasePackage1.getPlannedEffectiveDate(), equalTo(releasePackage2.getPlannedEffectiveDate()));
        assertThat("executors is null", releasePackage1.getExecutor(), is(notNullValue()));
        //assertThat("executors are not same", releasePackage1.getExecutor(), samePropertyValuesAs(releasePackage2.getExecutor()));
        assertThat("planned effective dates are not same", releasePackage1.getPlannedEffectiveDate(), equalTo(releasePackage2.getPlannedEffectiveDate()));
        assertThat("planned release dates are not same", releasePackage1.getPlannedReleaseDate(), equalTo(releasePackage2.getPlannedReleaseDate()));
        assertThat("sap change control are not same", releasePackage1.getSapChangeControl(), equalTo(releasePackage2.getSapChangeControl()));
        assertThat("project id are not same", releasePackage1.getProjectId(), equalTo(releasePackage2.getProjectId()));
        assertThat("product id are not same", releasePackage1.getProductId(), equalTo(releasePackage2.getProductId()));
        assertThat("prerequisites detail are not same", releasePackage1.getPrerequisitesDetail(), equalTo(releasePackage2.getPrerequisitesDetail()));

    }

    public static void releasePackagesAreSame(ReleasePackage releasePackage1, ReleasePackage releasePackage2) {
        releasePackagesAreSameWithoutComparingAuditAndStatus(releasePackage1, releasePackage2);

        assertThat("created_on is null", releasePackage1.getCreatedOn(), is(notNullValue()));
        assertThat("created_on dates are not same", releasePackage1.getCreatedOn(), is(releasePackage2.getCreatedOn()));

        assertThat("creator is null", releasePackage1.getCreator(), is(notNullValue()));
        //assertThat("creators are not same", releasePackage1.getCreator(), samePropertyValuesAs(releasePackage2.getCreator()));

        assertThat("statuses are not same", releasePackage1.getStatus(), equalTo(releasePackage2.getStatus()));

        assertThat("titles are not same", releasePackage1.getTitle(), equalTo(releasePackage2.getTitle()));
        assertThat("executor is null", releasePackage1.getExecutor(), is(notNullValue()));
     //   assertThat("executors are not same", releasePackage1.getExecutor(), samePropertyValuesAs(releasePackage2.getExecutor()));
        assertThat("planned effective dates are not same", releasePackage1.getPlannedEffectiveDate(), equalTo(releasePackage2.getPlannedEffectiveDate()));
        assertThat("planned release dates are not same", releasePackage1.getPlannedReleaseDate(), equalTo(releasePackage2.getPlannedReleaseDate()));

    }

    public static void unauthorizedExceptionAndReleasePackageDidNotChange(ReleasePackage releasePackageBeforeCaseAction, ReleasePackage releasePackageAfterCaseAction,
                                                                          com.example.mirai.projectname.releasepackageservice.json.ExceptionResponse exceptionResponse,
                                                                          String path, Integer originalStatus) {
        ExceptionValidator.exceptionResponseIsUnauthorizedException(exceptionResponse, path);
        releasePackagesAreSame(releasePackageBeforeCaseAction, releasePackageAfterCaseAction);
        assertThat("unauthorized case action has changed status", releasePackageAfterCaseAction.getStatus(), equalTo(originalStatus));
    }

    public static void releasePackageCommentsAreSameWithoutComparingAuditAndStatus(Comment comment1, Comment comment2) {
        assertThat("commentText are not same", comment1.getCommentText(), equalTo(comment2.getCommentText()));

    }

    public static void releasePackagesCommentAreSameWithoutComparingCommentText(Comment comment1, Comment comment2) {
        assertThat("status are not same", comment1.getStatus(), equalTo(comment2.getStatus()));
    }

    public static void unauthorizedExceptionAndReleasePackageCommentDidNotChange(Comment commentBeforeCaseAction, Comment commentAfterCaseAction,
                                                                                 com.example.mirai.projectname.releasepackageservice.json.ExceptionResponse exceptionResponse,
                                                                                 String path, Integer originalStatus) {
        ExceptionValidator.exceptionResponseIsUnauthorizedException(exceptionResponse, path);
        assertThat("commentText are not same", commentBeforeCaseAction.getCommentText(), equalTo(commentAfterCaseAction.getCommentText()));
        assertThat("status are not same", commentBeforeCaseAction.getStatus(), equalTo(commentAfterCaseAction.getStatus()));
    }

    public static void createCommentIsSuccessful(Comment comment, ReleasePackageComment releasePackageComment, ReleasePackageCommentJson releasePackageCommentJson) {

        assertThat("created_on is null in response", releasePackageComment.getCreatedOn(), is(notNullValue()));
        assertThat("status is not draft for a reply", releasePackageComment.getStatus(), equalTo(1));
    }

    public static void createCommentDocumentIsSuccessful(Document document, ReleasePackageCommentDocument releasePackageCommentDocument, ReleasePackageCommentDocumentJson releasePackageCommentJson) {

        assertThat("document tags are null for a comment document", releasePackageCommentDocument.getTags(), is(notNullValue()));
        assertThat("document name is null for a comment document", releasePackageCommentDocument.getName(), is(notNullValue()));
        assertThat("status is not draft for a comment document", releasePackageCommentDocument.getStatus(), equalTo(1));
    }

}
