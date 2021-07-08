package com.example.mirai.libraries.notification.core;

import com.example.mirai.libraries.audit.AuditableUserExtractorInterface;
import com.example.mirai.libraries.core.model.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthenticatedContext implements AuditableUserExtractorInterface {
	public String getPrincipal() {
		return getUserId();
	}


	public Set<String> getRoles() {
		return getGroupMembership();
	}

	public Set getGroupMembership() {
		Jwt jwt = getJwt();
		if (jwt == null)
			return null;

		List<String> groupMemberships = jwt.getClaimAsStringList("group_membership");

		if (groupMemberships == null)
			return null;
		Set<String> roles = groupMemberships.stream().map(r -> r).collect(Collectors.toSet());
		return roles;
	}

	public String getUserId() {
		return (String) getClaim("user_id");
	}

	public String getFullName() {
		return (String) getClaim("full_name");
	}

	public String getEmail() {
		return (String) getClaim("email");
	}

	public String getDepartmentNumber() {
		return (String) getClaim("department_number");
	}

	public String getDepartmentName() {
		return (String) getClaim("department_name");
	}

	public String getAbbreviation() {
		return (String) getClaim("abbreviation");
	}

	public String getEmployeeNumber() {
		return (String) getClaim("employee_number");
	}

	private Object getClaim(String claim) {
		Jwt jwt = getJwt();
		if (jwt == null)
			return null;
		List<String> claims = jwt.getClaimAsStringList(claim);
		if (claims == null)
			return null;
		return claims.get(0);
	}


	private User getPrincipalUser() {
		if (SecurityContextHolder.getContext() == null)
			return null;
		if (SecurityContextHolder.getContext().getAuthentication() == null)
			return null;
		if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null)
			return null;

		if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User)
			return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return null;
	}

	private Jwt getJwt() {
		if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null ||
				SecurityContextHolder.getContext().getAuthentication().getCredentials() == null)
			return null;
		return (Jwt) SecurityContextHolder.getContext().getAuthentication().getCredentials();
	}

	@Override
	public User getAuditableUser() {
		User principalUser = getPrincipalUser();
		User user = new User();
		user.setUserId(principalUser.getUserId());
		user.setFullName(principalUser.getFullName());
		user.setAbbreviation(principalUser.getAbbreviation());
		user.setEmail(principalUser.getEmail());
		user.setDepartmentName(principalUser.getDepartmentName());
		return user;
	}
}
