package com.example.mirai.libraries.websecurity;

import java.util.Collection;

import com.example.mirai.libraries.core.model.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class PrincipalAwareJwtAuthenticationToken extends JwtAuthenticationToken {
	private final User user;

	public PrincipalAwareJwtAuthenticationToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities,
			boolean isSystemAccount) {
		super(jwt, authorities);

		if (isSystemAccount) {
			this.user = new User();
			user.setUserId((String) Util.getClaim(jwt, "sub"));
			authorities.stream().findFirst().ifPresentOrElse(
					authority -> user.setFullName(authority.getAuthority().substring(5)),
					() -> user.setFullName("undefined-system-account")
			);
		}
		else {
			this.user = new User();
			user.setUserId((String) Util.getClaim(jwt, "user_id"));
			user.setAbbreviation((String) Util.getClaim(jwt, "abbreviation"));
			user.setDepartmentName((String) Util.getClaim(jwt, "department_name"));
			user.setFullName((String) Util.getClaim(jwt, "full_name"));
			user.setEmail((String) Util.getClaim(jwt, "email"));
		}
	}

	@Override
	public User getPrincipal() {
		return user;
	}
}
