package com.example.mirai.projectname.changerequestservice.shared.exception;

import lombok.Getter;

@Getter
public enum ChangeRequestErrorStatusCodes {
    DEFAULT("CHANGEREQUEST-ERR-001", "<div>projectname did not work as expected. Create an <a href=\"https://example.com" target=\"_blank\">IT Call Ticket</a> and mention @TxId@. <a href=\"https://example.com/" target=\"_blank\">Need help?</a></div>"),
    MANDATORY_VIOLATION_FOR_CHANGEREQUEST("CHANGEREQUEST-ERR-004", "<div>Change Request status not changed. Fill out all mandatory fields of Change Request.</div>"),
    UNLINK_PBS_FAILED("CHANGEREQUEST-ERR-002", "<div></div>"), // passes the same message that comes from cerberus library
    UNLINK_AIR_FAILED("CHANGEREQUEST-ERR-003", "<div></div>"),
    INCORRECT_DOCUMENT_TAG_SIZE("CHANGEREQUEST-ERR-004", "It is possible to update the document, that has only one tag."),
    CHANGE_OWNER_UPDATE_NOT_ALLOWED("CHANGEREQUEST-ERR-005", "Change owner can be updated only by selecting change owner in Problem Item or Solution Item."),
    SELF_DEPENDENT_NOT_ALLOWED("CHANGEREQUEST-ERR-006", "It is not allowed to add self as Dependent CR."),
    HANA_ENTITY_NOT_FOUND("CHANGEREQUEST-ERR-07","<di>Entity Not Found</div>");

    String code;
    String message;

    ChangeRequestErrorStatusCodes(String errorCode, String errorMessage) {
        code = errorCode;
        message = errorMessage;
    }
}
