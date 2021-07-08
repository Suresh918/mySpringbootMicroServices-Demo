package com.example.mirai.libraries.core.exception;

import lombok.Getter;

@Getter
public enum ErrorStatusCodes {
	DEFAULT("FRAMEWORK-ERR-000", "<div>This did not work as expected. Create an <a href=\"https://example.com/sp?id=sc_cat_item&sys_id=86abd789dbb1a384ff3fa5ca0b96195a&sysparm_category=e15706fc0a0a0aa7007fc21e1ab70c2f\" target=\"_blank\">IT Call Ticket</a> and mention @TxId@.<a href=\"https://example.com/Home.aspx\" target=\"_blank\">Need help?</a></div>\n"),
	CONFLICT("FRAMEWORK-ERR-001", "<div>Entity already updated by another user. You will loose your modifications, refresh page to see latest details.</div>"),
	ID_NOT_FOUND("FRAMEWORK-ERR-002", "<div>${entity} Not Found.</div>"),
	FORBIDDEN("FRAMEWORK-ERR-003", "<div>You are not authorized. <a href=\"https://example.com.aspx\" target=\"_blank\">Request access</a> to correct CUG.</div>"),
	NOT_EXTENDED("FRAMEWORK-ERR-004", "<div>This did not work as expected. Create an <a href=\"https://example.com" target=\"_blank\">IT Call Ticket</a> and mention @TxId@.<a href=\"https://example.com/Home.aspx\" target=\"_blank\">Need help?</a></div>"),
	BAD_REQUEST("FRAMEWORK-ERR-005", "<div>Bad Request.</div>"),
	INVALID_CASE_ACTION("FRAMEWORK-ERR-006", "<div>Invalid Case action</div>"),
	ENTITY_LINK_MISMATCH("FRAMEWORK-ERR-007", "<div>Incorrect entity Id or link Id</div>"),
	MANDATORY_MISSING("FRAMEWORK-ERR-008", "<div>${entity} status not changed. Fill out all mandatory fields.</div>"),
	METHOD_NOT_ALLOWED("FRAMEWORK-ERR-009", "<div>Request Method Not Allowed.</div>"),
	UNSUPPORTED_MEDIA_TYPE("FRAMEWORK-ERR-010", "<div>Unsupported Media Type. Content type not supported.'</div>"),
	NOT_ACCEPTABLE("FRAMEWORK-ERR-011", "<div>Not Acceptable. Could not find acceptable representation.</div>"),
	NO_HANDLER_FOUND("FRAMEWORK-ERR-012", "<div>No Handler found for the method.</div>"),
	SERVICE_UNAVAILABLE("FRAMEWORK-ERR-013", "<div>This did not work as expected. Create an <a href=\"https://example.com/" target=\"_blank\">IT Call Ticket</a> and mention @TxId@.<a href=\"https://example.com/Home.aspx\" target=\"_blank\">Need help?</a></div>");

	String code;

	String message;

	ErrorStatusCodes(String errorCode, String errorMessage) {
		code = errorCode;
		message = errorMessage;
	}
}
