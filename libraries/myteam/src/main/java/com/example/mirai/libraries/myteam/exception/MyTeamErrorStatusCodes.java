package com.example.mirai.libraries.myteam.exception;

import lombok.Getter;

@Getter
public enum MyTeamErrorStatusCodes {
	MY_TEAM_MEMBER_EXISTS("MYTEAM-ERR-001", "<div>Member not added to myTeam. Add a Member not already present.</div>"),
	MY_TEAM_MEMBER_ROLE_EMPTY("MYTEAM-ERR-002", "<div>Member not added to myTeam. Add at least one role for the member.</div>");

	private final String code;

	private final String message;

	MyTeamErrorStatusCodes(String errorCode, String errorMessage) {
		code = errorCode;
		message = errorMessage;
	}

}
