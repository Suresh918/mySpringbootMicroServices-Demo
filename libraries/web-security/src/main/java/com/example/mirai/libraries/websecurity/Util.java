package com.example.mirai.libraries.websecurity;

import java.util.List;

import org.springframework.security.oauth2.jwt.Jwt;

public class Util {
	private Util() {
		throw new IllegalStateException("Utility class");
	}

	public static Object getClaim(Jwt jwt, String claim) {
		if (jwt == null)
			return null;
		List<String> claims = jwt.getClaimAsStringList(claim);
		if (claims == null)
			return null;
		return claims.get(0);
	}
}
