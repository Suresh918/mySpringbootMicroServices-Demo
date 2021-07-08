package com.example.mirai.libraries.security.abac.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SubjectElementFactory {
	public static Set<SubjectElement> getSubjectsFromUserElement(UserRolesElement userRolesElement) {
		Set<SubjectElement> subjects = new HashSet<>();

		addUserRolesElementToSubject(subjects, userRolesElement.getUserRoles());

		return subjects;
	}

	private static void addUserRolesElementToSubject(Set<SubjectElement> subjects, HashMap<String, HashSet<String>> userRoles) {
		userRoles.forEach((k, v) -> {
			SubjectElement subjectElement = getAuthorizationSubject(subjects, k);
			subjectElement.roles.addAll(v);
			subjects.add(subjectElement);
		});
	}

	private static SubjectElement getAuthorizationSubject(Set<SubjectElement> subjects, String principal) {
		return subjects.stream().filter(subject -> subject.user != null && subject.user.equals(principal)).findFirst().orElse(new SubjectElement(principal));
	}
}
