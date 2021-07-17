package com.example.mirai.services.userservice.fixtures;

import java.util.List;

import net.minidev.json.JSONArray;

import org.springframework.security.oauth2.jwt.Jwt;

public class JwtFactory {
	public static Jwt getJwtToken(String dataIdentifier, List<String> roles) {
		JSONArray groups = new JSONArray();
		roles.forEach(role -> {
			switch (role) {
				case "user":
					groups.appendElement("cug-authorized-user");
					break;
				case "change-specialist-1":
					groups.appendElement("cug-change-specialist-1");
					break;
				case "change-specialist-2":
					groups.appendElement("cug-change-specialist-2");
					break;
				case "change-specialist-3":
					groups.appendElement("cug-change-specialist-3");
					break;
				case "administrator":
					groups.appendElement("cug-administrator");
					break;
				case "supply-chain-project-coordinator":
					groups.appendElement("cug-sc-project-coordinator");
					break;
				default:
					groups.appendElement("cug-unspecialized");
			}
		});

		Jwt.Builder jwtBuilder = Jwt.withTokenValue("token")
				.header("typ", "JWT")
				.header("alg", "RS512")
				.claim("full_name", dataIdentifier + "_full_name")
				.claim("user_id", dataIdentifier)
				.claim("department_name", dataIdentifier + "_department_name")
				.claim("department_number", dataIdentifier + "_department_number")
				.claim("abbreviation", dataIdentifier + "_abbreviation")
				.claim("employee_number", dataIdentifier + "_employee_number")
				.claim("email", dataIdentifier + "_email@example.net")
				.claim("group_membership", groups);
		return jwtBuilder.build();
	}
}
