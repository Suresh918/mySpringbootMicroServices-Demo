package com.example.mirai.libraries.security.core.model;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EntityAccessRule {
	private String filter;

	private String fetchRule;

	private String fetchViewRule;

	private Set<String> caseActions;

	@Override
	public String toString() {
		return "[filter=" + filter + ", caseActions=" + caseActions.toString();
	}
}
