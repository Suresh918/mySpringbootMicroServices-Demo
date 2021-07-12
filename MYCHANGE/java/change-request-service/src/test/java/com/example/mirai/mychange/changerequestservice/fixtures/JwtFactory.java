package com.example.mirai.projectname.changerequestservice.fixtures;

import net.minidev.json.JSONArray;
import org.springframework.security.oauth2.jwt.Jwt;

public class JwtFactory {

    public static Jwt getJwtToken(String dataIdentifier, String role) {
        String roleInsertion = (role != null ? "_" + role : "");
        JSONArray roles = new JSONArray();
        switch (role) {
            case "executor":
                roles.appendElement("cug-projectname-authorized-user");
                break;
            default:
                roles.appendElement(role);
                break;
        }

        Jwt.Builder jwtBuilder = Jwt.withTokenValue("token")
                .header("typ", "JWT")
                .header("alg", "RS512")
                .claim("full_name", dataIdentifier + roleInsertion + "_full_name")
                .claim("user_id", dataIdentifier + roleInsertion + "_user_id")
                .claim("department_name", dataIdentifier + roleInsertion + "_department_name")
                .claim("abbreviation", dataIdentifier + roleInsertion + "_abbreviation")
                .claim("employee_number", dataIdentifier + roleInsertion + "_employee_number")
                .claim("email", dataIdentifier + roleInsertion + "_email@example.net")
                .claim("group_membership", roles);
        return jwtBuilder.build();
    }
}
