package com.example.mirai.services.userservice.exception;

public enum ApplicationErrorStatusCodes {
	PROFILE_CREATION_FAILED("USER SERVICE-ERR-001", "<div>User Profile creation failed. Create an <a href=\"https://example.service-now.com/sp?id=sc_cat_item&sys_id=86abd789dbb1a384ff3fa5ca0b96195a&sysparm_category=e15706fc0a0a0aa7007fc21e1ab70c2f\" target=\"_blank\">IT Call Ticket</a> and mention @TxId@. <a href=\"https://example.sharepoint.com/teams/cm-processes/projectname/SitePages/Home.aspx\" target=\"_blank\">Need help?</a></div>"),
	PROFILE_PUBLISH_FAILED("USER SERVICE-ERR-002", "<div>User Profile publish failed. Create an <a href=\"https://example.service-now.com/sp?id=sc_cat_item&sys_id=86abd789dbb1a384ff3fa5ca0b96195a&sysparm_category=e15706fc0a0a0aa7007fc21e1ab70c2f\" target=\"_blank\">IT Call Ticket</a> and mention @TxId@. <a href=\"https://example.sharepoint.com/teams/cm-processes/projectname/SitePages/Home.aspx\" target=\"_blank\">Need help?</a></div>"),
	PREFERRED_ROLES_UPDATE_PUBLISH_FAILED("USER SERVICE-ERR-003", "<div>Preferred Roles Update publish failed</div>");

	String code;

	String message;

	ApplicationErrorStatusCodes(String errorCode, String errorMessage) {
		this.code = errorCode;
		this.message = errorMessage;
	}

	public String getCode() {
		return this.code;
	}

	public String getMessage() {
		return this.message;
	}

}
