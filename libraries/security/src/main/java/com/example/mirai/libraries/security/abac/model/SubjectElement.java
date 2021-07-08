package com.example.mirai.libraries.security.abac.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class SubjectElement {
	String user;

	Set<String> roles;

	public SubjectElement(String user) {
		this.user = user;
		this.roles = new HashSet<>();
	}

	public SubjectElement(String user, Set<String> roles) {
		this.user = user;
		this.roles = roles;
	}

	public static Set<SubjectElement> merge(Set<SubjectElement> subjectSet1, Set<SubjectElement> subjectSet2) {
		Set<SubjectElement> merged = new HashSet<>();
		if (Objects.nonNull(subjectSet1) && Objects.isNull(subjectSet2))
			merged.addAll(subjectSet1);
		else if (Objects.isNull(subjectSet1) && Objects.nonNull(subjectSet2))
			merged.addAll(subjectSet2);
		else if (Objects.nonNull(subjectSet1) && Objects.nonNull(subjectSet2)) {
			merged.addAll(subjectSet1);
			merged.forEach(subjectElementFromSet1 -> {
				Optional<SubjectElement> foundSubjectElement = subjectSet2.stream().filter(subjectElementFromSet2 -> subjectElementFromSet2.user != null && subjectElementFromSet2.user.equals(subjectElementFromSet1.user)).findFirst();
				if (foundSubjectElement.isPresent())
					subjectElementFromSet1.addRoles(foundSubjectElement.get().getRoles());

			});
			subjectSet2.stream().filter(subjectElementFromSet2 -> !subjectElementFromSet2.isPresentIn(merged)).forEach(subjectElementFromSet2 -> merged.add(subjectElementFromSet2));
		}
		return merged;
	}


	public Set<String> getRoles() {
		return roles;
	}

	private void addRoles(Set<String> roles) {
		if (Objects.nonNull(roles))
			this.roles.addAll(roles);
	}

	public boolean isPresentIn(Set<SubjectElement> subjectElements) {
		if (Objects.nonNull(subjectElements)) {
			return subjectElements.stream().anyMatch(subjectElement -> Objects.nonNull(subjectElement) && Objects.nonNull(subjectElement.user) && subjectElement.user.equals(this.user));
		}
		return false;
	}
}
