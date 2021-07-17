package com.example.mirai.projectname.reviewservice.shared.exception;

import lombok.Getter;

@Getter
public enum ReviewErrorStatusCodes {
    DEFAULT("REVIEW-ERR-001", "<div>projectname did not work as expected. Create an <a href=\"https://exampleitsmprod.service-now.com/sp?id=sc_cat_item&sys_id=86abd789dbb1a384ff3fa5ca0b96195a&sysparm_category=e15706fc0a0a0aa7007fc21e1ab70c2f\" target=\"_blank\">IT Call Ticket</a> and mention @TxId@. <a href=\"https://example.sharepoint.com/teams/cm-processes/projectname/SitePages/Home.aspx\" target=\"_blank\">Need help?</a></div>"),
    REVIEW_TASK_EXIST("REVIEW-ERR-002", "<div>Reviewer not added. Add a reviewer not already present.</div>"),
    NO_LINKED_REVIEW_TASK("REVIEW-ERR-003", "<div>For the current Review, No Reviewer found with the current user as Assignee.</div>"),
    MANDATORY_VIOLATION_FOR_REVIEW_TASK("REVIEW-ERR-004", "<div>Review status not changed. Fill out all mandatory fields of Reviewers.</div>"),
    DEFECTS_NOT_PROCESSED_TO_COMPLETE_REVIEW("REVIEW-ERR-005", "<div>You have not processed all your open or accepted defects, Please process defects.</div>"),
    COMMENTS_EXIST_TO_DELETE_REVIEW_ENTRY("REVIEW-ERR-006", "<div>Deleting this defect is not possible as it has comments. Please delete all the comments and try again.</div>"),
    INCOMPLETE_REVIEW_EXIST_FOR_RELEASE_PACKAGE("REVIEW-ERR-007", "<div>Creating a new review is not possible as the Release Package already has a open review.</div>"),
    MDG_CR_NOT_EXISTS("REVIEW-ERR-008", "<div>Mdg Change Request is not available.</div>"),
    ZCN_REVIEW_FOUND("REVIEW-ERR-009", "<div>This Review is ZECN Review</div>"),
    DELETE_MATERIAL_FAILED("REVIEW-ERR-010", "<div>Delete Material from sap mdg not success </div>"),
    ADDITIONAL_SOLUTION_ITEM_IN_TEAMCENTER("REVIEW-ERR-011", "<div>The Teamcenter ECN has the following solution items that are not stated in SAP MDG:<br><br> <ITEMS> <br> <br> Please remove Solution Items from review in Teamcenter and again add the Solution Items in review in Teamcenter. We cannot change the Status to Released before this solved.</div>"),
    ADDITIONAL_MATERIAL_IN_SAP_MDG("REVIEW-ERR-012", "<div>projectname will automatically remove the following solution items: <br> <br> <ITEMS> <br><br> These Items are currently only stated in SAP MDG and not in Teamcenter </div>");

    String code;
    String message;

    ReviewErrorStatusCodes(String errorCode, String errorMessage) {
        code = errorCode;
        message = errorMessage;
    }
}
