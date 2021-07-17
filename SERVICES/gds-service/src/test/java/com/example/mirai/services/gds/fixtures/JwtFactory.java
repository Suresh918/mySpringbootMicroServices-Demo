package com.example.mirai.services.gds.fixtures;

import org.springframework.security.oauth2.jwt.Jwt;

public class JwtFactory {
	public static Jwt getJwtToken(String dataIdentifier) {
		return Jwt.withTokenValue("token")
				.header("typ", "JWT")
				.header("alg", "RS512")
				.claim("full_name", dataIdentifier + "_full_name")
				.claim("user_id", dataIdentifier + "_user_id")
				.claim("department_name", dataIdentifier + "_department_name")
				.claim("abbreviation", dataIdentifier + "_abbreviation")
				.claim("employee_number", dataIdentifier + "_employee_number")
				.claim("email", dataIdentifier + "_email@example.net")
				.claim("group_membership", dataIdentifier + "_group")
				.build();
	}
}
