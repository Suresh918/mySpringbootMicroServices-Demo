package com.example.mirai.services.gds.shared;

import com.example.mirai.libraries.core.model.User;
import com.example.mirai.services.gds.model.GdsUser;

public class Util {
	private Util() {
		throw new IllegalStateException("Utility class");
	}

	public static User convertGdsUserToUser(GdsUser gdsUser) {
		return new User(
				gdsUser.getUserId(),
				gdsUser.getFullName(),
				gdsUser.getEmail(),
				gdsUser.getDepartmentName(),
				gdsUser.getAbbreviation()
		);
	}
}
