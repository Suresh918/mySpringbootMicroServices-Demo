package com.example.mirai.projectname.releasepackageservice.shared.exception;

import lombok.Getter;

@Getter
public enum ReleasePackageErrorStatusCodes {
    DEFAULT("RELEASEPACKAGE-ERR-001", "<div>This did not work as expected. Create an <a href=\"https://example.service-now.com/" target=\"_blank\">IT Call Ticket</a> and mention @TxId@. <a href=\"https://example.sharepoint.com/teams/cm-processes/projectname/SitePages/Home.aspx\" target=\"_blank\">Need help?</a></div>"),
    MANDATORY_VIOLATION_FOR_RELEASE_PACKAGE("RELEASEPACKAGET-ERR-002", "<div>Release Package status not changed. Fill out all mandatory fields of Release Package.</div>"),
    ADDITIONAL_SOLUTION_ITEM_IN_TEAMCENTER("RELEASEPACKAGE-ERR-003", "<div>The Teamcenter ECN has the following solution items that are not stated in SAP MDG:<br><br> <ITEMS> <br> <br> Please remove Solution Items from review in Teamcenter and again add the Solution Items in review in Teamcenter. We cannot change the Status to Released before this solved.</div>"),
    ADDITIONAL_MATERIAL_IN_SAP_MDG("RELEASEPACKAGE-ERR-004", "<div>projectname will automatically remove the following solution items: <br> <br> <ITEMS> <br><br> These Items are currently only stated in SAP MDG and not in Teamcenter </div>"),
    DELETE_MATERIAL_FAILED("RELEASEPACKAGE-ERR-005", "<div>Delete Material from sap mdg not success </div>"),
    SDL_START_CASE_ACTION_FAILED("RELEASEPACKAGE-ERR-006","<div>START SDL case action failed</div>"),
    UNABLE_TO_OBSOLETE_RP("RELEASEPACKAGE-ERR-007","<div>Unable to Obsolete Release Package or MDG CR</div>"),
    COMMUNICATION_ERROR("RELEASEPACKAGE-ERR-008","<div>SAP-MDG and projectname communication error</div>"),
    INVALID_RELEASE_PACKAGE_NUMBER("SAPMDG-CHANGEREQUEST-006","<div>Invalid Release Package Number, please provide valid Release Package Number</div>"),
    UNABLE_TO_RELEASE_FOR_ACTIVATION_RP("SAPMDG-CHANGEREQUEST-012","<div>Unable to Release For Activation for Release Package or MDG CR</div>"),
    IMPACTED_ITEM_ERROR("IMPACTEDITEM-001","<div>Unable to Perform operation on SDL</div>"),
    CHANGE_OBJECT_PUBLICATION_PENDING("IMPACTEDITEM-002","<div>Publication has been started in SDL. Refresh the page to see the latest status of Work Instructions and Try releasing Release Package again once all the Work Instructions are published in SDL.</div>"),
    SDL_RELEASE_CASE_ACTION_FAILED("RELEASEPACKAGE-ERR-009","<div>SDL case action RELEASE failed</div>"),
    SDL_PUBLISH_CASE_ACTION_FAILED("RELEASEPACKAGE-ERR-010","<div>SDL case action PUBLISH failed</div>"),
    SDL_NOT_AUTHENTICATED("IMPACTEDITEM-003","<div>SDL Not Authenticated Exception</div>"),
    SDL_NOT_WORKING("IMPACTEDITEM-004","<div>SDL Not Working Exception</div>"),
    SDL_UNREACHABLE("IMPACTEDITEM-005","<div>SDL Unreachable Exception</div>"),
    SDL_TIMEOUT("IMPACTEDITEM-006","<div>SDL Timeout</div>"),
    HANA_ENTITY_NOT_FOUND("RELEASEPACKAGE-ERR-011","<div>Entity Not Found</div>"),
    HW_NOT_ALLOWED_FOR_CREATOR_RP("RELEASEPACKAGE-ERR-012","<div>It is not possible to add Hardware type for a Creator Release Package</div>"),
    CANNOT_REMOVE_OP_PR_FROM_RP_LINKED_TO_TC("RELEASEPACKAGE-ERR-013","<div>It is not possible to remove Hardware or Operation/Process from Release Package as ECN has been created in Teamcenter</div>"),
    CHANGE_NOTICE_STATUS_INVALID_FOR_READY("RELEASEPACKAGE-ERR-014","<div>Release Package status not changed. Ensure Change Notice is in Planned Status.</div>");

    String code;
    String message;

    ReleasePackageErrorStatusCodes(String errorCode, String errorMessage) {
        code = errorCode;
        message = errorMessage;
    }
}
